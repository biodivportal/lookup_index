package org.gfbio.LookupIndex;

import co.elastic.clients.elasticsearch.core.BulkResponse;
import me.tongfei.progressbar.ProgressBar;

import org.gfbio.LookupIndex.model.json.IndexDocument;
import org.gfbio.LookupIndex.model.term.Term;
import org.gfbio.LookupIndex.service.ElasticsearchService;
import org.gfbio.LookupIndex.service.VirtuosoService;
import org.gfbio.LookupIndex.utils.ChangeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Facade {

    private static final Logger LOGGER = LoggerFactory.getLogger(Facade.class);

    private AppProperties appProperties = AppProperties.getInstance();

    private VirtuosoService virtService = new VirtuosoService();

    private ElasticsearchService esService = new ElasticsearchService();

    private String ontology, mode;

    private int size = 0;

    private List<IndexDocument> addedDocuments, removedDocuments, modifiedDocuments;

    public Facade() {
    }

    public Facade(String ontology) {

        LOGGER.info("starting Facade, running Lookup Index Generation \n");

        this.ontology = ontology;
    }

    /**
     * application operation mode
     *
     * @param mode
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    public void run() {

        Instant start = Instant.now();

        switch (mode.toLowerCase()) {
            case "create":
                createLookupIndex();
                break;

            case "update":
                updateLookupIndex();
                break;

            default:
                LOGGER.info("no valid 'mode' argument supplied");
                break;
        }

        Instant end = Instant.now();
        LOGGER.info("\n");
        LOGGER.info("indexing took " + Duration.between(start, end).toMinutes() + " min");

        // done and exit
        System.exit(0);

    }


    /**
     * Starts creating the index. If the supplied terminology has a large number of terms the index
     * will be written batch-wise.
     */
    private void createLookupIndex() {

        // get count of all terms to assess, whether batch processing is needed
        int count = virtService.countAllTerms(this.ontology);

        LOGGER.info("number of terms to index: " + count);

        if (count > Integer.valueOf(appProperties.getProperty("query.limit"))) {

            // prepare indexing in batches to avoid OutOfMemory
            int numBatches = (count / Integer.valueOf(appProperties.getProperty("query.limit"))) + 1;
            int limit = Integer.valueOf(appProperties.getProperty("query.limit")), offset = 0;

            LOGGER.info("reading in " + numBatches + " batches");

            for (int i = 1; i <= numBatches; i++) {

                LOGGER.info("current batch=" + i);
                LOGGER.info("limit=" + limit + "; offset=" + offset);

                writeDocumentsToIndex(
                        prepareIndexDocuments(this.ontology, String.valueOf(limit), String.valueOf(offset)));

                offset = i * 1000;

            }

        } else {
            // terminology is small enough, we can write all terms at once
            writeDocumentsToIndex(prepareIndexDocuments(this.ontology));
        }

    }

    /**
     * Starts updating an existing index. If the supplied terminology has a large number of terms the
     * index will be updated batch-wise.
     */
    private void updateLookupIndex() {

        // get count of all modified terms to assess whether batch processing is needed
        int count = virtService.countModifiedTerms(this.ontology);

        LOGGER.info("number of terms to be transfered to index: " + count);

        if (count > Integer.valueOf(appProperties.getProperty("query.limit"))) {

            int numBatches = (count / Integer.valueOf(appProperties.getProperty("query.limit"))) + 1;
            int limit = Integer.valueOf(appProperties.getProperty("query.limit")), offset = 0;

            LOGGER.info("reading in " + numBatches + " batches");

            for (int i = 1; i <= numBatches; i++) {

                LOGGER.info("current batch=" + i);
                LOGGER.info("limit=" + limit + "; offset=" + offset);

                writeDocumentsToIndex(
                        prepareIndexDocuments(this.ontology, String.valueOf(limit), String.valueOf(offset)));

                offset = i * 1000;

            }

        } else {
            writeDocumentsToIndex(prepareIndexDocuments(this.ontology));
        }

    }

    /**
     * Prepares terms read from Virtuoso for further processing via Elasticsearch API
     *
     * @param acronym terminology acronym
     * @return
     */
    private Map<ChangeStatus, List<IndexDocument>> prepareIndexDocuments(String acronym) {
        return prepareIndexDocuments(acronym, null, null);
    }

    /**
     * Prepares terms read from Virtuoso for further processing via Elasticsearch API
     *
     * @param acronym terminology acronym
     * @param limit   Query LIMIT
     * @param offset  Query OFFSET
     * @return
     */
    private Map<ChangeStatus, List<IndexDocument>> prepareIndexDocuments(String acronym, String limit,
                                                                         String offset) {

        LOGGER.info("prepareIndexDocuments");

        // lists of documents per status, e.g., "removed" or "added"
        addedDocuments = new ArrayList<IndexDocument>();
        removedDocuments = new ArrayList<IndexDocument>();
        modifiedDocuments = new ArrayList<IndexDocument>();

        // lists will be stored in a map, with the according status as key
        Map<ChangeStatus, List<IndexDocument>> documents =
                new HashMap<ChangeStatus, List<IndexDocument>>();

        // temporary list
        List<Term> terms = new ArrayList<Term>();

        // mode we got from the commandline
        switch (mode.toLowerCase()) {
            case "create":
                terms = virtService.getAllTerms(acronym, limit, offset);
                break;

            case "update":
                terms = virtService.getModifiedTerms(acronym, limit, offset);
                break;
        }

        ProgressBar pb = new ProgressBar("Preparing", terms.size());
        pb.start(); // the progress bar starts timing

        // for all terms found extract synonyms and broader terms
        for (Term t : terms) {

            // read synonyms and broader terms
            List<Term> synonyms = virtService.readSynonyms(this.ontology, t.getUri());
            List<Term> broader = virtService.readBroader(this.ontology, t.getUri());

            // an IndexDocument represents a row/object in the index
            IndexDocument idxDoc = new IndexDocument(t.getLabel(), this.ontology, t.getUri(),
                    t.getDescription(), t.getUrl(), t.getAuthor(), String.valueOf(t.getUri().hashCode()),
                    synonyms.stream().map(Term::getLabel).collect(Collectors.toList()),
                    broader.stream().map(Term::getLabel).collect(Collectors.toList()));

            switch (t.getStatus().toLowerCase()) {
                case "added":
                    addedDocuments.add(idxDoc);
                    break;

                case "modified":
                    modifiedDocuments.add(idxDoc);
                    break;

                case "removed":
                    removedDocuments.add(idxDoc);
                    break;
            }

            pb.step();
        }

        pb.stop();

        size = addedDocuments.size() + removedDocuments.size() + modifiedDocuments.size();
        LOGGER.info("\n");
        LOGGER.info(size + " documents ready for processing to Elasticsearch");

        documents.put(ChangeStatus.ADDED, addedDocuments);
        documents.put(ChangeStatus.REMOVED, removedDocuments);
        documents.put(ChangeStatus.MODIFIED, modifiedDocuments);

        return documents;
    }

    /**
     * Executes
     *
     * @param documents
     */
    private void writeDocumentsToIndex(Map<ChangeStatus, List<IndexDocument>> documents) {

        LOGGER.info("writeDocumentsToIndex");

        ProgressBar pb = new ProgressBar("Indexing", size);
        pb.start(); // the progress bar starts timing

        long step = 0;

        for (ChangeStatus status : documents.keySet()) {

            // TODO FB 04/26/2022 use this for something?!
            BulkResponse response;

            switch (mode.toLowerCase()) {
                case "create":
                    step = documents.get(ChangeStatus.ADDED).size();
                    response = esService.bulkOperation(documents.get(ChangeStatus.ADDED),
                            appProperties.getProperty("elasticsearch.index"), ChangeStatus.ADDED);
                    break;

                case "update":
                    step = documents.get(status).size();
                    response = esService.bulkOperation(documents.get(status),
                            appProperties.getProperty("elasticsearch.index"), status);
                default:
                    break;
            }

            pb.stepBy(step);
            step = 0;

        }

        pb.stop();
    }

}
