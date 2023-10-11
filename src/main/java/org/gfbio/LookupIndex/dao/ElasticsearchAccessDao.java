package org.gfbio.LookupIndex.dao;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.*;

import org.gfbio.LookupIndex.model.database.Elasticsearch;
import org.gfbio.LookupIndex.model.json.IndexDocument;
import org.gfbio.LookupIndex.utils.ChangeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Implementation of the DAO interface
 *
 */
public class ElasticsearchAccessDao implements ElasticsearchDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchAccessDao.class);

  private Elasticsearch ESHandle = new Elasticsearch();

  @Override
  public IndexResponse createDocument(IndexDocument idxDoc, String index, String id) {

    IndexResponse response = null;
    try {

      IndexRequest<IndexDocument> indexReq =
          new IndexRequest.Builder<IndexDocument>().document(idxDoc).index(index).id(id).build();

      // response = ESHandle.getEsClient().index(doc -> doc.index(index).id(id).document(idxDoc));
      response = ESHandle.getEsClient().index(indexReq);

    } catch (ElasticsearchException | IOException e) {
      e.printStackTrace();
    }

    return response;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public UpdateResponse updateDocument(IndexDocument idxDoc, String index, String id) {

    UpdateResponse response = null;
    try {

      UpdateRequest updateRequest =
          new UpdateRequest.Builder().id(id).doc(idxDoc).index(index).build();

      response = ESHandle.getEsClient().update(updateRequest, IndexDocument.class);

    } catch (ElasticsearchException | IOException e) {
      e.printStackTrace();
    }

    return response;
  }

  @Override
  public DeleteResponse deleteDocument(String index, String id) {

    DeleteResponse response = null;

    try {

      DeleteRequest request = new DeleteRequest.Builder().id(id).index(index).build();

      response = ESHandle.getEsClient().delete(request);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return response;
  }

  @Override
  public BulkResponse bulkOperation(List<IndexDocument> documents, String index,
      ChangeStatus mode) {

    BulkResponse response = null;

    if (documents.isEmpty())
      return response;

    LOGGER.info("bulkOperation() for status=" + mode + " on index=" + index);
    LOGGER.info("Number of documents=" + documents.size());

    try {

      BulkRequest.Builder br = new BulkRequest.Builder();

      for (IndexDocument idxDoc : documents) {

        String id = String.valueOf(idxDoc.getUri().hashCode());

        switch (mode) {
          case ADDED:
            // create new document(s) on index
            br.operations(
                operation -> operation.index(doc -> doc.index(index).id(id).document(idxDoc)));
            break;
          case MODIFIED:
            // update existing document(s) on index
            // FIXME FB 22/04/22 this is not working; somehow the document needs to be in the body,
            // but a body
            // value cannot be set?!
            // br.operations(
            // operation -> operation.update(doc -> doc.index(index).id(id).document(idxDoc)));

            // this will circumvent this problem for and use the normal API call
            updateDocument(idxDoc, index, id);
            br = null;

            break;

          case REMOVED:
            // delete existing document(s) from index
            br.operations(operation -> operation.delete(doc -> doc.index(index).id(id)));
            break;
        }
      }
      if (br != null) {
        // now execute the operation for all processed documents at once
        response = ESHandle.getEsClient().bulk(br.build());
        LOGGER.info("bulkOperation took " + response.took() + "ms total time");
      }

    } catch (ElasticsearchException | IOException e) {
      e.printStackTrace();
    }

    return response;
  }

  @Override
  public GetResponse<IndexDocument> getDocument(String index, String id) {

    GetResponse<IndexDocument> response = null;

    try {

      response = ESHandle.getEsClient().get(g -> g.index(index).id(id), IndexDocument.class);

    } catch (ElasticsearchException | IOException e) {
      e.printStackTrace();
    }

    return response;
  }


}
