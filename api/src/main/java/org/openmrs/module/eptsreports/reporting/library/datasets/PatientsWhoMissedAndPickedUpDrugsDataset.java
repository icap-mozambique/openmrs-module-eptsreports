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

package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.library.queries.PatientsWhoMissedAndPickedUpDrugsQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientsWhoMissedAndPickedUpDrugsDataset extends BaseDataSet {

  @Autowired private GenericCohortQueries genericCohorts;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  public DataSetDefinition constructPatientsWhoMissedAndPickedUpDrugs() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("patientsWhoMissedAndPickedUpDrugsDataset");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortIndicator patientsScheduledToPickUpARTIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsScheduledToPickUpARTIndicator",
            EptsReportUtils.map(
                this.genericCohorts.generalSql(
                    "patientsScheduledToPickUpART",
                    PatientsWhoMissedAndPickedUpDrugsQueries.QUERY.patientsScheduledToPickUpART),
                mappings));

    dataSetDefinition.addColumn(
        "scheduled",
        "Patients Scheduled To PickUp ART Indicator",
        EptsReportUtils.map(patientsScheduledToPickUpARTIndicator, mappings),
        "");

    final CohortIndicator patientsWhoMissedARTPickUpIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsWhoMissedARTPickUpIndicator",
            EptsReportUtils.map(
                this.genericCohorts.generalSql(
                    "patientsWhoMissedARTPickUp",
                    PatientsWhoMissedAndPickedUpDrugsQueries.QUERY.patientsWhoMissedARTPickUp),
                mappings));

    dataSetDefinition.addColumn(
        "missed",
        "Patients Who Missed ART pickUp Indicator",
        EptsReportUtils.map(patientsWhoMissedARTPickUpIndicator, mappings),
        "");

    return dataSetDefinition;
  }
}
