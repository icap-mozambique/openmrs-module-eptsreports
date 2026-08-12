/** */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

/** @author Stélio Moiane */
public enum PrepKp {
  MILITARY(1902, "military"),

  MINER(1908, "miner"),

  DRIVER(1903, "driver"),

  COUPLES_RESULT_ARE_DIFFERENT(1995, "couples-result"),

  PREGNANT(1982, "pregnant"),

  BREASTFEEDING(6332, "breastfeeding"),

  HOMOSEXUAL(1377, "homosexual"),

  DRUG_USER(20454, "drug-user"),

  PRISIONER(20426, "prisioner"),

  SEX_WORKER(1901, "sex-worker"),

  ADOLESCENTS_AND_YOUTH_AT_RISK(165287, "adolescents-at-risk");

  private int id;

  private String name;

  PrepKp(final int id, final String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }
}
