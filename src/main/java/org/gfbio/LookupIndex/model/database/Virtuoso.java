package org.gfbio.LookupIndex.model.database;

import org.apache.jena.query.ResultSet;
import org.gfbio.LookupIndex.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;


/**
 *
 */
public final class Virtuoso {

    private static final Logger LOGGER = LoggerFactory.getLogger(Virtuoso.class);
    private AppProperties properties = AppProperties.getInstance();
    private VirtGraph virtGraph;
    private VirtuosoQueryExecution vqe;

    public Virtuoso() {
        String host = properties.getProperty("virtuoso.host");
        String user = properties.getProperty("virtuoso.username");
        String pass = properties.getProperty("virtuoso.password");
        LOGGER.info(host + " " + user + " " + pass);
        virtGraph = new VirtGraph(host, user, pass);
    }

    /**
     * @param query
     * @return
     */
    public ResultSet executeQuery(String query) {
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, virtGraph);
        ResultSet res = vqe.execSelect();

        return res;
    }

    /**
     * Releases resources
     */
    public void closeQuery() {
        vqe.close();
    }

    public VirtGraph getVirtGraph() {
        return virtGraph;
    }
}
