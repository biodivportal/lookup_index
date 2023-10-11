package org.gfbio.LookupIndex.dao;

import co.elastic.clients.elasticsearch.core.*;
import org.gfbio.LookupIndex.model.json.IndexDocument;
import org.gfbio.LookupIndex.utils.ChangeStatus;


import java.util.List;

/**
 * Interface that provides access to the underlying Elasticsearch API
 */
public interface ElasticsearchDao {

  /**
   * Retrieves a document via GET from the given Elasticsearch index
   * 
   * @param index Name of the Elasticsearch index
   * @param id Id of the document to retrieve
   * @return Response object which wraps the serialized JSON IndexDocument from Elasticsearch
   */
  public GetResponse<IndexDocument> getDocument(String index, String id);

  /**
   * Creates a document via PUT to the given Elasticsearch index
   * 
   * @param idxDoc POJO to be saved to Elasticsearch. No further JSON serialization is needed since
   *        this is done by the API automatically.
   * @param index Name of the Elasticsearch index
   * @param id Id of the document to create
   * @return Response object which wraps the JSON response from Elasticsearch
   */
  public IndexResponse createDocument(IndexDocument idxDoc, String index, String id);

  /**
   * Updates a document via POST on the given Elasticsearch index. Elasticsearch automatically
   * detects which fields to update. Furthermore, not yet existing fields will be inserted as well.
   * 
   * @param idxDoc POJO to be saved to Elasticsearch. No further JSON serialization is needed since
   *        this is done by the API automatically.
   * @param index Name of the Elasticsearch index
   * @param id Id of the document to be updated
   * @return Response object which wraps the JSON response from Elasticsearch
   */
  @SuppressWarnings("rawtypes")
  public UpdateResponse updateDocument(IndexDocument idxDoc, String index, String id);

  /**
   * Deletes a document via DELETE from the given Elasticsearch index.
   * 
   * @param index Name of the Elasticsearch index
   * @param id Id of the document to be deleted
   * @return Response object which wraps the JSON response from Elasticsearch
   */
  public DeleteResponse deleteDocument(String index, String id);

  /**
   * Bulk operations allow to create, update or delete multiple documents at once.
   * 
   * @param documents
   * @param index Name of the Elasticsearch index
   * @param mode Either STATUS.ADDED (for creating), STATUS.REMOVED (for deleting) or
   *        STATUS.MODIFIED (for updating)
   * @return Response object which wraps the JSON response from Elasticsearch (not for
   *         STATUS.MODIFIED)
   */
  public BulkResponse bulkOperation(List<IndexDocument> documents, String index, ChangeStatus mode);
}
