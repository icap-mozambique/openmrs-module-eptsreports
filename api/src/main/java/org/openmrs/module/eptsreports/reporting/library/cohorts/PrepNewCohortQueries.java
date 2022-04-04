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
import org.openmrs.module.eptsreports.reporting.library.queries.PrepNewQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.PrepNewStartingSectorQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxNewQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.PrepNewKeyPopType;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** DataSet for PREP NEW */
@Component
public class PrepNewCohortQueries {

  @Autowired private GenericCohortQueries genericCohorts;

  /**
   * Build TxNew composition cohort definition
   *
   * @param cohortName Cohort name
   * @return CompositionQuery
   */
  public CohortDefinition getClientsNewlyEnrolledInPrep() {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName("PREP NEW");
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    txNewCompositionCohort.addSearch(
        "START-PREP",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findClientsNewlyEnrolledInPrep",
                PrepNewQueries.QUERY.findClientsNewlyEnrolledInPrep),
            mappings));

    txNewCompositionCohort.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findClientsWhoWhereTransferredIn",
                PrepNewQueries.QUERY.findClientsWhoWhereTransferredIn),
            mappings));

    txNewCompositionCohort.setCompositionString("START-PREP NOT TRANSFERED-IN");

    return txNewCompositionCohort;
  }

  public CohortDefinition getCommnunityClientsNewlyEnrolledInPrep() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("PREP NEW");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "ENROLLED-IN-PREP", EptsReportUtils.map(this.getClientsNewlyEnrolledInPrep(), mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    definition.setCompositionString("ENROLLED-IN-PREP AND COMMUNITY-DISPENSATION");

    return definition;
  }

  public CohortDefinition getSectorClientsNewlyEnrolledInPrep(
      Integer sector, Integer conceitoKeyPop, PrepNewKeyPopType keyPop) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("PREP NEW");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "ENROLLED-IN-PREP", EptsReportUtils.map(this.getClientsNewlyEnrolledInPrep(), mappings));

    definition.addSearch(
        "SECTOR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPrEPNewBySector",
                PrepNewStartingSectorQueries.QUERY.findClientsNewlyEnrolledInPrepAtCPN(
                    sector, conceitoKeyPop, keyPop)),
            mappings));

    definition.setCompositionString("ENROLLED-IN-PREP AND SECTOR");

    return definition;
  }
}
