package org.gfbio.LookupIndex.model.json;

import java.util.List;

/**
 * Wraps a document and prepares it to be JSON-fied from the Elasticsearch API
 * 
 *
 */
public class IndexDocument {

  private String label, sourceTerminology, uri, description, url, author, id;

  private List<String> synonyms;

  private List<String> broaders;

  /**
   * no args default constructor for jackson deserialization
   */
  public IndexDocument() {}

  /**
   * 
   * @param label
   * @param sourceTerminology
   * @param uri
   * @param description
   * @param url
   * @param author
   * @param id
   * @param synonyms
   * @param broaders
   */
  public IndexDocument(String label, String sourceTerminology, String uri, String description,
      String url, String author, String id, List<String> synonyms, List<String> broaders) {
    this.label = label;
    this.sourceTerminology = sourceTerminology;
    this.uri = uri;
    this.description = description;
    this.synonyms = synonyms;
    this.broaders = broaders;
    this.id = id == null ? String.valueOf(this.uri.hashCode()) : id;
    this.url = url;
    this.author = author;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getSourceTerminology() {
    return sourceTerminology;
  }

  public void setSourceTerminology(String sourceTerminology) {
    this.sourceTerminology = sourceTerminology;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<String> getSynonyms() {
    return synonyms;
  }

  public void setSynonyms(List<String> synonyms) {
    this.synonyms = synonyms;
  }

  public List<String> getBroaders() {
    return broaders;
  }

  public void setBroaders(List<String> broaders) {
    this.broaders = broaders;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("IndexDocument [label=");
    builder.append(label);
    builder.append(", sourceTerminology=");
    builder.append(sourceTerminology);
    builder.append(", uri=");
    builder.append(uri);
    builder.append(", description=");
    builder.append(description);
    builder.append(", url=");
    builder.append(url);
    builder.append(", author=");
    builder.append(author);
    builder.append(", indexID=");
    builder.append(id);
    builder.append(", synonyms=");
    builder.append(synonyms);
    builder.append(", broaders=");
    builder.append(broaders);
    builder.append("]");
    return builder.toString();
  }

}
