package org.gfbio.LookupIndex.utils;

/**
 * Description of a term in a terminology changelog, i.e., the intersection of two of a terminology.
 * Terms were either added, removed or modified between versions.
 *
 */
public enum ChangeStatus {
  ADDED, REMOVED, MODIFIED;
}
