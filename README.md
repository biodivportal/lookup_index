# Lookup Index
## About
This tool is intended for creating a so called *lookup index* on a running Elasticsearch (ES) instance, using data extracted from knowledge graphs (i.e. terminologies) which are stored in a Virtuoso triplestore. The application functions as adapter between ES and Virtuoso. 
Terms are extracted from Virtuoso (via SPARQL) and inserted into ES, which also handles the JSON (de-)serialization.

## Index Format
Following fields are used for the index:
* label (String)
* uri (String)
* author (String)
* sourceTerminology (String)
* description (String)
* url (String)
* id (String; hash generated from uri)
* synonyms (List<String>)
* broaders terms (List<String>)

To add more supported fiels (thus adding more indexed fields)
1. extend `org.gfbio.model.term.Term.java` class: it maps properties from Virtuoso to a Java object
2. extend `org.gfbio.model.json.IndexDocument.java` class: it represents the actual indexed document and will be converted to a JSON representation by the ES API. Fields extending the lookup index must be declared here.

## Prerequisites
- JDK >= 11
- Maven

This version was tested (and implemented) against **Virtuoso 7.2** and **Elasticsearch 7.16.3**.

## Build
1. Checkout the repository
2. Import project repository, e.g, via Eclipse
3. JUnit tests are supplied, but need a running ES instance; they can be skipped, when building the project
4. Make sure to edit parameters in the `lookup_index_generator.properties`, e.g., user(s) and password(s)

## Run
The applications comes as a standalone .jar, which can be executed on the commandline. 
```
usage: <this>.jar [-a <arg>] [-c <arg>] [-m <arg>]
 -a <arg>   terminology acronym, e.g., ENVO
 -c <arg>   path to configuration file (default:
            /var/opt/ts/lookup_index_generator.properties)
 -m <arg>   generator mode ('create', 'update')
 ```
 Currently, two modes are supported: 
 * create: use this mode to create a new index based on terms found in the supplied terminology. Terms will then be automatically extracted from the corresponding graph and inserted into the index
 * upate: use this mode to update an existing index, e.g., if there is a new terminology version available inside the triplestore. Terms will be extracted from the MOD graph of the terminology and - depending on their status - will either add, remove or modify items on the index.

 ## View
 - The Firefox addon [Elasticvue](https://github.com/cars10/elasticvue) can be used for an easy inspection of the ES instance
