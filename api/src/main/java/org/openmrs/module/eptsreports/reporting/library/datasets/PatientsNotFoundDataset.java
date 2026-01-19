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

import org.openmrs.module.eptsreports.reporting.library.cohorts.SearchPatientsCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.library.queries.SearchReason;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientsNotFoundDataset extends BaseDataSet {

  @Autowired private SearchPatientsCohortQueries searchPatientsCohortQueries;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  public DataSetDefinition constructPatientsNotFoundDataset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("Patient Not Found Data Set");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition patientsNotFoundCohortDefinition =
        this.searchPatientsCohortQueries.findPatientsNotfoundInSearch();

    final CohortIndicator patientNotFoundIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientNotFoundIndicator",
            EptsReportUtils.map(patientsNotFoundCohortDefinition, mappings));

    dataSetDefinition.addDimension(
        "children",
        EptsReportUtils.map(
            this.searchPatientsCohortQueries.findPatientsByRange("children", AgeRange.CHILDREN),
            "endDate=${endDate}"));

    dataSetDefinition.addDimension(
        "adult",
        EptsReportUtils.map(
            this.searchPatientsCohortQueries.findPatientsByRange("adult", AgeRange.ADULT),
            "endDate=${endDate}"));

    this.addDimensions(
        dataSetDefinition,
        mappings,
        SearchReason.WRONG_ADDRESS,
        SearchReason.CHANGED_ADDRESS,
        SearchReason.TRAVELED,
        SearchReason.DEAD,
        SearchReason.OTHER_NOT_FOUND);

    dataSetDefinition.addColumn(
        "PSNF",
        "PATIENTS SEARCH: NOT FOUND",
        EptsReportUtils.map(patientNotFoundIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "PSNF_CH",
        "PATIENTS SEARCH: NOT FOUND",
        EptsReportUtils.map(patientNotFoundIndicator, mappings),
        "children=children");

    dataSetDefinition.addColumn(
        "PSNF_AD",
        "PATIENTS SEARCH: NOT FOUND",
        EptsReportUtils.map(patientNotFoundIndicator, mappings),
        "adult=adult");

    this.addColumns(
        dataSetDefinition,
        patientNotFoundIndicator,
        "CH",
        mappings,
        "children=children",
        SearchReason.WRONG_ADDRESS,
        SearchReason.CHANGED_ADDRESS,
        SearchReason.TRAVELED,
        SearchReason.DEAD,
        SearchReason.OTHER_NOT_FOUND);

    this.addColumns(
        dataSetDefinition,
        patientNotFoundIndicator,
        "AD",
        mappings,
        "adult=adult",
        SearchReason.WRONG_ADDRESS,
        SearchReason.CHANGED_ADDRESS,
        SearchReason.TRAVELED,
        SearchReason.DEAD,
        SearchReason.OTHER_NOT_FOUND);

    return dataSetDefinition;
  }

  private void addDimensions(
      final CohortIndicatorDataSetDefinition dataSetDefinition,
      final String mappings,
      final SearchReason... reasons) {
    for (final SearchReason reason : reasons) {
      dataSetDefinition.addDimension(
          reason.getLabel(),
          EptsReportUtils.map(
              this.searchPatientsCohortQueries.findPatientsNotFoundInSearchByReason(
                  reason.getLabel(), reason),
              mappings));
    }
  }

  private void addColumns(
      final CohortIndicatorDataSetDefinition dataSetDefinition,
      final CohortIndicator cohortIndicator,
      final String label,
      final String mappings,
      final String dimension,
      final SearchReason... reasons) {

    for (final SearchReason reason : reasons) {
      dataSetDefinition.addColumn(
          "PSNF_" + label + "_" + reason.getLabel() + "_" + reason.ordinal(),
          "PATIENTS SEARCH: NOT FOUND " + label + " " + reason.ordinal(),
          EptsReportUtils.map(cohortIndicator, mappings),
          dimension + "|" + reason.getLabel() + "=" + reason.getLabel());
    }
  }
}
