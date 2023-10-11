package org.gfbio.LookupIndex.model.database;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import org.gfbio.LookupIndex.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Providing all the means for setting up an Elasticsearch API client
 */
public final class Elasticsearch {

    private static final Logger LOGGER = LoggerFactory.getLogger(Elasticsearch.class);

    private static AppProperties appProperties = AppProperties.getInstance();

    private RestClient restClient; // low-level client

    private ElasticsearchTransport esTransport; // de-/marshalling of JSON objects

    private ElasticsearchClient esClient; // API client

    public Elasticsearch() {
        initConn();
    }

    public ElasticsearchClient getEsClient() {
        return esClient;
    }

    /**
     * initiates connection to the ES
     */
    public void initConn() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(appProperties.getProperty("elasticsearch.user"),
                        appProperties.getProperty("elasticsearch.password")));

        // Create the low-level client
        restClient = RestClient
                .builder(new HttpHost(appProperties.getProperty("elasticsearch.host"), 9200)).build();

        // Create the transport with a Jackson mapper
        esTransport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // And create the API client
        esClient = new ElasticsearchClient(esTransport);

        LOGGER.info("Elasticsearch client on host " + appProperties.getProperty("elasticsearch.host")
                + " created!");
    }

}
