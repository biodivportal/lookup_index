/**
 * 
 */
package org.gfbio.LookupIndex.service;



import org.gfbio.LookupIndex.dao.VirtuosoAccessDao;
import org.gfbio.LookupIndex.model.term.Term;

import java.util.List;

public class VirtuosoService {

  private VirtuosoAccessDao virtDao = new VirtuosoAccessDao();

  /**
   * 
   * @param acronym
   * @return
   */
  public int countAllTerms(String acronym) {
    return virtDao.countAllTerms(acronym);
  }

  /**
   * 
   * @param acronym
   * @return
   */
  public int countModifiedTerms(String acronym) {
    return virtDao.countModifiedTerms(acronym);
  }

  /**
   * 
   * @param acronym
   * @param uri
   * @return
   */
  public List<Term> readSynonyms(String acronym, String uri) {
    return virtDao.getTermSynonymsByUri(acronym, uri);
  }

  /**
   * 
   * @param acronym
   * @return
   */
  public List<Term> getAllTerms(String acronym) {
    return virtDao.getAllTerms(acronym);
  }

  /**
   * 
   * @param acronym
   * @param limit
   * @param offset
   * @return
   */
  public List<Term> getAllTerms(String acronym, String limit, String offset) {
    return virtDao.getAllTerms(acronym, limit, offset);
  }

  /**
   * 
   * @param acronym
   * @param limit
   * @param offset
   * @return
   */
  public List<Term> getModifiedTerms(String acronym, String limit, String offset) {
    return virtDao.getModifiedTerms(acronym, limit, offset);
  }

  /**
   * 
   * @param acronym
   * @param uri
   * @return
   */
  public List<Term> readBroader(String acronym, String uri) {
    return virtDao.getAllBroader(acronym, uri);
  }
}
