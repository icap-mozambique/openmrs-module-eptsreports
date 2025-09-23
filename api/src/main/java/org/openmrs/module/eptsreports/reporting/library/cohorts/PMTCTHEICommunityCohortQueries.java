/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.TxNewQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PMTCTHEICommunityCohortQueries {

  @Autowired private GenericCohortQueries genericCohorts;

  @Autowired private PMTCTHEICohortQueries pmtctHeiCohort;

  public CohortDefinition getNumberOfInfantsWhoHadVirologicHIVTestResults() {
    final CompositionCohortDefinition composition = new CompositionCohortDefinition();

    composition.setName("PMTCT-HEI");
    composition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    composition.addParameter(new Parameter("endDate", "End Date", Date.class));
    composition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    composition.addSearch(
        "NUMERATOR",
        EptsReportUtils.map(
            this.pmtctHeiCohort.getNumberOfInfantsWhoHadVirologicHIVTestResults(), mappings));

    composition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    composition.setCompositionString("NUMERATOR AND COMMUNITY-DISPENSATION");

    return composition;
  }

  public CohortDefinition getNumberOfInfantsWhoHadVirologicHIVTestWithPositiveTestResults() {
    final CompositionCohortDefinition composition = new CompositionCohortDefinition();

    composition.setName("PMTCT-HEI - Positive Test Results");
    composition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    composition.addParameter(new Parameter("endDate", "End Date", Date.class));
    composition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    composition.addSearch(
        "NUMERATOR",
        EptsReportUtils.map(
            this.pmtctHeiCohort.getNumberOfInfantsWhoHadVirologicHIVTestWithPositiveTestResults(),
            mappings));

    composition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    composition.setCompositionString("NUMERATOR AND COMMUNITY-DISPENSATION");

    return composition;
  }

  public CohortDefinition getNumberOfInfantsWhoHadVirologicHIVTestWithNegativeTestResults() {
    final CompositionCohortDefinition composition = new CompositionCohortDefinition();

    composition.setName("PMTCT-HEI - Negative Test Results");
    composition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    composition.addParameter(new Parameter("endDate", "End Date", Date.class));
    composition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    composition.addSearch(
        "NUMERATOR",
        EptsReportUtils.map(
            this.pmtctHeiCohort.getNumberOfInfantsWhoHadVirologicHIVTestWithNegativeTestResults(),
            mappings));

    composition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    composition.setCompositionString("NUMERATOR AND COMMUNITY-DISPENSATION");

    return composition;
  }

  public CohortDefinition
      getNumberOfInfantsWhoHadVirologicHIVTestWithPositiveTestResultsWhoInitatedARTTreatment() {
    final CompositionCohortDefinition composition = new CompositionCohortDefinition();

    composition.setName("PMTCT-HEI - Positive Test Results and Art Initiated");
    composition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    composition.addParameter(new Parameter("endDate", "End Date", Date.class));
    composition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    composition.addSearch(
        "POSITIVE-RESULTS",
        EptsReportUtils.map(
            this.pmtctHeiCohort
                .getNumberOfInfantsWhoHadVirologicHIVTestWithPositiveTestResultsWhoInitatedARTTreatment(),
            mappings));

    composition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    composition.setCompositionString("POSITIVE-RESULTS AND COMMUNITY-DISPENSATION");

    return composition;
  }
}
