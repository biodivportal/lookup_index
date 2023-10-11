package org.gfbio.LookupIndex.query;


import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.gfbio.LookupIndex.env.StandardVariables;
import org.gfbio.LookupIndex.model.database.Virtuoso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SPARQLQueries {

    private static StandardVariables vars = new StandardVariables();

    private static Virtuoso virtDB = new Virtuoso();

    /**
     * Queries for all synonyms of a given URI in a given terminology
     *
     * @param terminology_id
     * @param term_uri
     * @return
     */
    public static String getSPARQLSynonym(String terminology_id, String term_uri) {

        int hash = terminology_id.hashCode();

        StringBuilder query = new StringBuilder();

        List<String> synonymProps = getSynonymProperties(hash);

        if (synonymProps != null && synonymProps.size() > 0) {
            // stream the found properties into one string
            StringBuilder props = new StringBuilder();
            synonymProps.forEach(props::append);

            query.append("SELECT ?synonym FROM <" + vars.getUriPrefix() + terminology_id + "> "
                    + "WHERE {<" + term_uri + "> ?p ?synonym. FILTER(?p = " + props.toString()
                    + " || ?p = skos:altLabel)}");
        }
        return query.toString();

    }

    /**
     * Queries for all broader terms of a given URI in a given terminology
     *
     * @param terminology_id
     * @param term_uri
     * @return
     */
    public static String getSPARQLBroader(String terminology_id, String term_uri) {

        int hash = terminology_id.hashCode();

        String graph = vars.getUriPrefix() + terminology_id;

        HashMap<String, String> metadata = getGraphMetadata(hash);
        String lang = metadata.get("lang");
        String label = metadata.get("label");

        StringBuilder query = new StringBuilder();

        ResultSet res = null;

        if (graph != null && lang != null) {

            String hierarchyQueryBroader = "SELECT ?broaderProperty FROM <" + graph
                    + "> WHERE {?broaderProperty rdfs:subPropertyOf skos:broader}";
            res = virtDB.executeQuery(hierarchyQueryBroader);
            RDFNode broaderProperty = null;
            if (res.hasNext()) {
                QuerySolution result = res.nextSolution();
                broaderProperty = result.get("broaderProperty");
            }
            virtDB.closeQuery();

            String hierarchyQueryNarrower = "SELECT ?narrowerProperty FROM <" + graph
                    + "> WHERE {?narrowerProperty rdfs:subPropertyOf skos:narrower}";
            res = virtDB.executeQuery(hierarchyQueryNarrower);
            RDFNode narrowerProperty = null;
            if (res.hasNext()) {
                QuerySolution result = res.nextSolution();
                narrowerProperty = result.get("narrowerProperty");
            }
            virtDB.closeQuery();

            String hierarchyQuerySubClassOf = "SELECT ?subClassOfProperty FROM <" + graph
                    + "> WHERE {?subClassOfProperty rdfs:subPropertyOf rdfs:subClassOf}";
            res = virtDB.executeQuery(hierarchyQuerySubClassOf);
            RDFNode subClassOfProperty = null;
            if (res.hasNext()) {
                QuerySolution result = res.nextSolution();
                subClassOfProperty = result.get("subClassOfProperty");
            }
            virtDB.closeQuery();

            query.append("SELECT DISTINCT ?broaderuri ?broaderlabel  FROM <" + graph + "> ");
            switch (lang.toString()) {
                case (StandardVariables.SKOSURI):
                    query
                            .append("WHERE {{ SELECT DISTINCT ?broaderuri ?broaderlabel FROM <" + graph + ">"
                                    + "WHERE { ?graphuri a  <http://www.w3.org/2004/02/skos/core#ConceptScheme>.<"
                                    + term_uri + "> (<" + broaderProperty + "> | skos:broader)* ?broaderuri."
                                    + "?broaderuri ?p ?broaderlabel. FILTER(?p = skos:prefLabel || ?p = <" + label
                                    + ">)}} ")
                            .append("UNION { SELECT DISTINCT ?broaderuri ?broaderlabel FROM <" + graph
                                    + "> WHERE{ ?graphuri a  <http://www.w3.org/2004/02/skos/core#ConceptScheme>. ?broaderUri (<"
                                    + narrowerProperty + "> | skos:narrower)* <" + term_uri
                                    + ">. ?broaderuri ?p ?broaderlabel. FILTER(?p = skos:prefLabel || ?p = <" + label
                                    + ">)}}}");
                    break;
                case (StandardVariables.OWLURI):
                    query.append("WHERE {{ SELECT DISTINCT ?broaderuri ?broaderlabel FROM <" + graph + "> "
                                    + "WHERE { ?graphuri a  <http://www.w3.org/2002/07/owl#Ontology>. <" + term_uri
                                    + "> (<" + subClassOfProperty
                                    + "> | rdfs:subClassOf)+ ?broaderuri. ?broaderuri ?p ?broaderlabel. FILTER(?p = rdfs:label || ?p = "
                                    + label + ")}}")
                            .append(" UNION { SELECT DISTINCT ?broaderuri ?broaderlabel FROM <" + graph
                                    + "> WHERE {<" + term_uri
                                    + "> rdf:type ?type . ?type rdfs:subClassOf+ ?broaderuri. ?broaderuri ?p ?broaderlabel. FILTER(?p = rdfs:label || ?p = "
                                    + label + ")}}}");
                    break;
            }
        }

        return query.toString();
    }

    /**
     * @param terminologyId Acronym of a terminology, e.g, ENVO
     * @return
     */
    public static String getSPARQLAllTerms(String terminologyId) {
        return getSPARQLAllTerms(terminologyId, null, null);
    }

    /**
     * @param terminology_id Acronym of a terminology, e.g, ENVO
     * @param limit          How many rows select at once
     * @param offset         Skip n rows
     * @return
     */
    public static String getSPARQLAllTerms(String terminology_id, String limit, String offset) {

        int hash = terminology_id.hashCode();

        StringBuilder query = new StringBuilder();

        String label = getGraphMetadata(hash).get("label");

        String graph = vars.getUriPrefix() + terminology_id;

        query.append("SELECT DISTINCT ?uri ?label{");
        if (label.equals("")) {
            label = "rdfs:label";
        }

        query.append("{SELECT ?uri ?label FROM <" + graph + "> ");
        query.append("WHERE { ?uri a owl:Class . ?uri " + label + " ?label . }}");
        query.append(" UNION ");
        query.append("{SELECT ?uri ?label FROM <" + graph + "> "
                + "WHERE { ?uri a owl:DatatypeProperty . ?uri " + label + " ?label . }}");
        query.append(" UNION ");
        query.append("{SELECT ?uri ?label FROM <" + graph + "> "
                + "WHERE { ?uri a owl:ObjectProperty . ?uri " + label + " ?label . }}");
        query.append(" UNION ");
        query.append("{SELECT ?ind as ?uri ?label FROM <" + graph + "> "
                + "WHERE { ?uri a owl:Class . ?ind rdf:type ?uri . ?ind " + label + " ?label . }}}");
        if (limit != null)
            query.append(" LIMIT " + limit);
        if (offset != null)
            query.append(" OFFSET " + offset);

        return query.toString();
    }

    /**
     * @param acronym Acronym of a terminology, e.g, ENVO
     * @param limit          How many rows select at once
     * @param offset         Skip n rows
     * @param countQuery     Whether to just execute the query as COUNT()
     * @return
     */
    public static String getSPARQLModifiedTerms(String acronym, String limit, String offset,
                                                boolean countQuery) {
        StringBuilder query = new StringBuilder();

        if (countQuery == true) {
            query.append("SELECT COUNT(*) as ?cnt ");
        } else {
            query.append("SELECT ?o ?change ");
        }

        query.append(" FROM <" + vars.getUriPrefix() + acronym + "MOD>");

        query.append(
                " WHERE { ?s ?p ?o. ?o <http://terminologies.gfbio.org/terms/ontology#change> ?change . }");

        if (limit != null)
            query.append("LIMIT " + limit);

        if (offset != null)
            query.append(" OFFSET " + offset);

        return query.toString();
    }

    /**
     * @param terminology_id
     * @return
     */
    public static String getSPARQLClassesNumber(String terminology_id) {
        int hash = terminology_id.hashCode();
        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT ?number FROM <" + vars.getUriPrefix() + vars.getMetadataGraph() + "> WHERE {");
        sb.append("<" + vars.getUriPrefix() + hash + "> omv:" + vars.getClassesNumber() + " ?number}");
        return sb.toString();
    }

    /**
     * @param acronym
     * @param URI
     * @return
     */
    public static String getSPARQLLabelForURI(String acronym, String URI) {

        StringBuilder query = new StringBuilder();

        String terminology_id = vars.getUriPrefix() + acronym;

        query.append("SELECT ?label ").append("FROM <" + terminology_id + "> ")
                .append("WHERE { <" + URI + "> rdfs:label ?label . }");

        return query.toString();

    }

    /**
     * Queries the metadata graph for synonym properties of the respective graph
     *
     * @param hash String.hashCode() of terminology acronym, e.g., ITIS
     * @return List of synonyms used (as URI), separated by ' || ?p=' if more than one property
     */
    private static List<String> getSynonymProperties(int hash) {
        List<String> synonyms = new ArrayList<String>();

        String metadata_query =
                "SELECT ?synonym FROM <" + vars.getUriPrefix() + vars.getMetadataGraph() + "> WHERE {<"
                        + vars.getUriPrefix() + hash + "> " + vars.getUriShortPrefix() + ":synonym ?synonym}";

        ResultSet res = virtDB.executeQuery(metadata_query);

        while (res.hasNext()) {
            QuerySolution result = res.nextSolution();
            RDFNode synonym = result.get("synonym");
            synonyms.add("<" + synonym.asResource().getURI() + ">");
            if (res.hasNext())
                synonyms.add(" || ?p=");
        }

        virtDB.closeQuery();

        return synonyms;
    }

    /**
     * Queries the metadata graph for properties, e.g., label or synonym
     *
     * @param hash String.hashCode() of terminology acronym, e.g., ITIS
     * @return Map of property name (key) and its value (value)
     */
    private static HashMap<String, String> getGraphMetadata(int hash) {

        HashMap<String, String> metadata = new HashMap<String, String>();

        String query = new StringBuilder()
                .append("SELECT ?lang ?graph ?label FROM <" + vars.getUriPrefix() + vars.getMetadataGraph()
                        + ">")
                .append(" WHERE {<" + vars.getUriPrefix() + hash + "> omv:hasOntologyLanguage ?lang .")
                .append(
                        " <" + vars.getUriPrefix() + hash + "> " + vars.getUriShortPrefix() + ":graph ?graph .")
                .append(" <" + vars.getUriPrefix() + hash + "> " + vars.getUriShortPrefix()
                        + ":label ?label .}")
                .toString();

        ResultSet res = virtDB.executeQuery(query);

        while (res.hasNext()) {
            QuerySolution result = res.nextSolution();
            RDFNode label = result.get("label");
            RDFNode lang = result.get("lang");

            metadata.put("label", "<" + label.asResource().getURI() + ">");
            metadata.put("lang", lang.asResource().getURI());
        }

        virtDB.closeQuery();

        return metadata;
    }
}
