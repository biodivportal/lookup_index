package org.gfbio.LookupIndex.env;


import org.gfbio.LookupIndex.AppProperties;

public class StandardVariables {

    private static AppProperties properties = AppProperties.getInstance();

    private final String metadaSchema;

    private final String uriPrefix;

    private final String metaDataGraph;

    private final String classesNumber;

    private final String uriShortPrefix;

    public static final String SKOSURI = "http://terminologies.gfbio.org/terms/ontology#SKOS";
    public static final String OWLURI = "http://omv.ontoware.org/2005/05/ontology#OWL";

    public StandardVariables() {
        this.metadaSchema = properties.getProperty("metada.schema");
        this.uriPrefix = properties.getProperty("uri.prefix");
        this.metaDataGraph = properties.getProperty("metadata.graph");
        this.classesNumber = "numberOfClasses"; // TODO
        this.uriShortPrefix = properties.getProperty("uri.shortprefix");
    }

    // Getters
    public String getMetadaschema() {
        return metadaSchema;
    }

    public String getUriPrefix() {
        return uriPrefix;
    }

    public String getMetadataGraph() {
        return metaDataGraph;
    }

    public String getClassesNumber() {
        return classesNumber;
    }

    public String getUriShortPrefix() {
        return uriShortPrefix;
    }

}
