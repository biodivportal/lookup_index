package org.gfbio.LookupIndex.service;

import co.elastic.clients.elasticsearch.core.*;
import org.gfbio.LookupIndex.dao.ElasticsearchAccessDao;
import org.gfbio.LookupIndex.model.json.IndexDocument;
import org.gfbio.LookupIndex.utils.ChangeStatus;

import java.util.List;

public class ElasticsearchService {

  private ElasticsearchAccessDao esDao = new ElasticsearchAccessDao();

  /**
   * 
   * @param document
   * @param index
   * @param id
   * @return
   */
  public IndexResponse createDocument(IndexDocument document, String index, String id) {
    return esDao.createDocument(document, index, id);
  }

  /**
   * 
   * @param index
   * @param id
   * @return
   */
  public GetResponse<IndexDocument> getDocument(String index, String id) {
    return esDao.getDocument(index, id);
  }

  /**
   * 
   * @param document
   * @param index
   * @param id
   * @return
   */
  public UpdateResponse updateDocument(IndexDocument document, String index, String id) {
    return esDao.updateDocument(document, index, id);
  }

  /**
   * 
   * @param index
   * @param id
   * @return
   */
  public DeleteResponse deleteDocument(String index, String id) {
    return esDao.deleteDocument(index, id);
  }

  /**
   * 
   * @param documents
   * @param index
   * @param mode
   * @return
   */
  public BulkResponse bulkOperation(List<IndexDocument> documents, String index,
      ChangeStatus mode) {
    return esDao.bulkOperation(documents, index, mode);
  }

}
