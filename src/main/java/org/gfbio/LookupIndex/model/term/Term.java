package org.gfbio.LookupIndex.model.term;

/**
 * Wraps a result from the Virtuoso triplestore
 *
 */
public class Term {

  private String uri;
  private String label;
  private String author;
  private String description;
  private String url;
  // this variable will NOT be saved to the index
  private String status;

  public Term() {}

  public Term(String uri, String label) {
    this.uri = uri;
    this.label = label;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Term [uri=");
    builder.append(uri);
    builder.append(", label=");
    builder.append(label);
    builder.append(", author=");
    builder.append(author);
    builder.append(", description=");
    builder.append(description);
    builder.append(", url=");
    builder.append(url);
    builder.append(", status=");
    builder.append(status);
    builder.append("]");
    return builder.toString();
  }

}
