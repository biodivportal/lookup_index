package org.gfbio.LookupIndex.dao;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import org.gfbio.LookupIndex.model.database.Virtuoso;
import org.gfbio.LookupIndex.model.term.Term;
import org.gfbio.LookupIndex.query.SPARQLQueries;
import org.gfbio.LookupIndex.utils.FormatterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;


import java.util.ArrayList;
import java.util.List;


public class VirtuosoAccessDao implements VirtuosoDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtuosoAccessDao.class);

    private final Gson gson = new Gson();

    private static Virtuoso virtDB = new Virtuoso();

    @Override
    public List<Term> getTermSynonymsByUri(String acronym, String uri) {

        List<Term> resultList = new ArrayList<>();

        String query = SPARQLQueries.getSPARQLSynonym(acronym, uri);

        ResultSet res = virtDB.executeQuery(query);

        while (res.hasNext()) {

            QuerySolution currentResult = res.next();
            String synonym_ = FormatterUtils.formatField(currentResult, "synonym");
            Term t = new Term();
            t.setLabel(synonym_);
            resultList.add(t);

        }

        virtDB.closeQuery();

        return resultList;
    }

    @Override
    public List<Term> getAllBroader(String acronym, String uri) {

        List<Term> broaderTerms = new ArrayList<Term>();

        String query = SPARQLQueries.getSPARQLBroader(acronym, uri);

        ResultSet res = virtDB.executeQuery(query);

        while (res.hasNext()) {

            QuerySolution currentResult = res.next();
            String broaderuri_ = FormatterUtils.formatField(currentResult, "broaderuri");
            String broaderlabel_ = FormatterUtils.formatField(currentResult, "broaderlabel");
            Term t = new Term();
            t.setLabel(broaderlabel_);
            t.setUri(broaderuri_);
            broaderTerms.add(t);

        }

        virtDB.closeQuery();

        return broaderTerms;
    }

    @Override
    public List<Term> getAllTerms(String acronym, String limit, String offset) {

        List<Term> resultList = new ArrayList<Term>();

        JsonObject builder = new JsonObject();

        String query = SPARQLQueries.getSPARQLAllTerms(acronym, limit, offset);

        ResultSet rs = virtDB.executeQuery(query);

        while (rs.hasNext()) {

            QuerySolution current_result = rs.next();
            builder = FormatterUtils.formatTermInfo(current_result);
            builder.addProperty("status", "added");
            Term resultMap = gson.fromJson(builder, Term.class);
            resultList.add(resultMap);

        }

        virtDB.closeQuery();

        return resultList;
    }

    @Override
    public List<Term> getAllTerms(String acronym) {

        List<Term> resultList = new ArrayList<Term>();

        JsonObject builder = new JsonObject();

        if (isSmallerThanLimit(acronym)) {

            String query = SPARQLQueries.getSPARQLAllTerms(acronym);

            ResultSet rs = virtDB.executeQuery(query);

            while (rs.hasNext()) {

                QuerySolution current_result = rs.next();
                builder = FormatterUtils.formatTermInfo(current_result);
                Term resultMap = gson.fromJson(builder, Term.class);
                resultList.add(resultMap);

            }

            virtDB.closeQuery();
        } else
            LOGGER.info("Terminology contains too many terms!");

        return resultList;
    }

    @Override
    public int countAllTerms(String acronym) {

        String sparql = SPARQLQueries.getSPARQLClassesNumber(acronym);

        ResultSet res = virtDB.executeQuery(sparql);

        int classesCount = Integer.parseInt(res.next().get("number").toString());

        virtDB.closeQuery();

        return classesCount;
    }

    @Override
    public int countModifiedTerms(String acronym) {

        String sparql = SPARQLQueries.getSPARQLModifiedTerms(acronym, null, null, true);

        ResultSet res = virtDB.executeQuery(sparql);

        int classesCount = res.next().get("cnt").asLiteral().getInt();

        virtDB.closeQuery();

        return classesCount;
    }

    @Override
    public List<Term> getModifiedTerms(String acronym, String limit, String offset) {

        List<Term> resultList = new ArrayList<Term>();

        JsonObject builder = new JsonObject();

        String sparql = SPARQLQueries.getSPARQLModifiedTerms(acronym, limit, offset, false);

        LOGGER.info("getModifiedTerms: \n" + sparql);

        ResultSet rs = virtDB.executeQuery(sparql);

        while (rs.hasNext()) {
            String URI = "";

            QuerySolution qs = rs.nextSolution();

            // extract URI and status of term
            URI = qs.get("o").asResource().toString();

            // either 'modified', 'added' or 'removed'
            String status = qs.get("change").asLiteral().getString();

            String subQuery = SPARQLQueries.getSPARQLLabelForURI(acronym, URI);

            VirtuosoQueryExecution vqe =
                    VirtuosoQueryExecutionFactory.create(subQuery, virtDB.getVirtGraph());

            ResultSet sub = virtDB.executeQuery(subQuery);

            if (sub.hasNext()) {
                QuerySolution sol = sub.nextSolution();

                if (sol.contains("label") == true)
                    builder.addProperty("label", sol.get("label").toString());

                builder.addProperty("uri", URI);
                builder.addProperty("status", status);

                Term resultMap = gson.fromJson(builder, Term.class);
                resultList.add(resultMap);

                vqe.close();
            }

        }

        virtDB.closeQuery();

        return resultList;
    }

    /**
     * Check the number of terms a terminology has
     *
     * @param acronym - acronym of the ontology that si queried
     * @return true if the number of terms is smaller than specified limit (limitAlltermsService)
     */
    private boolean isSmallerThanLimit(String acronym) {
        String sparql = SPARQLQueries.getSPARQLClassesNumber(acronym);

        ResultSet res = virtDB.executeQuery(sparql);

        int classesCount = Integer.parseInt(res.next().get("number").toString());

        virtDB.closeQuery();

        if (classesCount > 10000) {
            return false;
        }
        return true;
    }

}
