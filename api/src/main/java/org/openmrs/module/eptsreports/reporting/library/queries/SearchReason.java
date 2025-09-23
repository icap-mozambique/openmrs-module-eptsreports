/** */
package org.openmrs.module.eptsreports.reporting.library.queries;

/** @author Stélio Moiane */
public enum SearchReason {
  FORGOT_DATE(2005, "forgot-date"),

  WAS_SICK(2006, "was-sick"),

  LACK_OF_TRANSPORTATION(2007, "lack-of-transportation"),

  BAD_SERVICE(2010, "bad-service"),

  PROVIDER_FEAR(23915, "provider-fear"),

  PROVIDER_ABSENCE(23946, "provider-absence"),

  SECONDARY_EFFECTS(2015, "secondary-effects"),

  TRADITIONAL_TREATMENT(2013, "traditional-treatment"),

  TRANSFERED_OUT(1706, "transfered-out"),

  AUTO_TRANSFER(23863, "auto-tranfer"),

  ART_ABANDONMENT(2012, "art-abandonment"),

  OTHER_MISSING(2017, "other-missing"),

  PATIENT_IS_FINE(1383, "patient-is-fine"),

  PATIENT_HAD_ISSUES(2157, "patient-had-issues"),

  FAMILY_CONCERN(2156, "family-concern"),

  MEDICATION_IS_DOING_BAD(2015, "medication-is-doing-bad"),

  LACK_OF_FAMILY_SUPPORT(2153, "lack-of-family-support"),

  ISSUES_TO_HAVE_MEDICATIONS(2154, "issues-to-have-medications"),

  DIAGNOSIS_NOT_REVEALED(2155, "diagnosis-not-revealed"),

  OTHER_REPORT(1748, "other-report"),

  WRONG_ADDRESS(2024, "wrong-address"),

  CHANGED_ADDRESS(2026, "changed-adress"),

  TRAVELED(2011, "traveled"),

  DEAD(1366, "dead"),

  OTHER_NOT_FOUND(2032, "other-not-found");

  private final Integer reason;

  private final String label;

  SearchReason(final Integer reason, final String label) {
    this.reason = reason;
    this.label = label;
  }

  public int getReason() {
    return this.reason;
  }

  public String getLabel() {
    return this.label;
  }
}
