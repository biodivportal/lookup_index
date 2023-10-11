package org.gfbio.LookupIndex.dao;


import org.gfbio.LookupIndex.model.term.Term;

import java.util.List;

/**
 * Interface that provides access to Virtuoso storage (via SPARQL query)
 */
public interface VirtuosoDao {

  /**
   * Executes a COUNT(*) on the provided terminology's graph
   * 
   * @param acronym Terminology acronym
   * @return Count of all terms in the graph
   */
  int countAllTerms(String acronym);

  /**
   * Executes a COUNT(*) on the provided terminology's MOD (e.g.
   * http://terminologies.gfbio.org/terms/ENVOMOD) graph, i.e., the graph which holds ADDED,
   * REMOVED, and MODIFIED terms between two versions of a graph
   * 
   * @param acronym Terminology acronym
   * @return Count of all entries in the MOD graph
   */
  int countModifiedTerms(String acronym);

  /**
   * Retrieves synonyms for a given term (uri) from a terminology
   * 
   * @param acronym Terminology acronym
   * @param uri Term uri (must be in that terminology)
   * @return List of synonyms (if any), wrapped in Term objects
   */
  List<Term> getTermSynonymsByUri(String acronym, String uri);

  /**
   * Retrieves broader terms for a given term (uri) from a terminology
   * 
   * @param acronym Terminology acronym
   * @param uri Term uri (must be in that terminology)
   * @return List of broader terms (if any), wrapped in Term objects
   */
  List<Term> getAllBroader(String acronym, String uri);

  /**
   * Retrieves all terms from a terminology
   * 
   * @param acronym Terminology acronym
   * @return List of terms (if any), wrapped in Term objects
   */
  List<Term> getAllTerms(String acronym);


  /**
   * Retrieves all terms from a terminology. Works with a LIMIT and an OFFSET.
   * 
   * @param acronym Terminology acronym
   * @param limit Query LIMIT
   * @param offset Query OFFSET
   * @return List of terms (if any), wrapped in Term objects
   */
  List<Term> getAllTerms(String acronym, String limit, String offset);

  /**
   * Retrieves all modified (i.e. ADDED, MODIFIED, REMOVED) terms for a given terminology
   * 
   * @param acronym Terminology acronym
   * @param limit Query LIMIT
   * @param offset Query OFFSET
   * @return List of all modified (if any) terms, wrapped in Term objects
   */
  List<Term> getModifiedTerms(String acronym, String limit, String offset);

}
