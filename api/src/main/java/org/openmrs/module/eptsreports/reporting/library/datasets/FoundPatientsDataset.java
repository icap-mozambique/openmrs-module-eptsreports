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
public class FoundPatientsDataset extends BaseDataSet {

  @Autowired private SearchPatientsCohortQueries searchPatientsCohortQueries;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  public DataSetDefinition constructFoundPatientsDataset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("FOUND PATIENT Data Set");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition foundPatientsCohortDefinition =
        this.searchPatientsCohortQueries.findFoundPatients();

    final CohortIndicator patientFoundIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsSerachedAndFound",
            EptsReportUtils.map(foundPatientsCohortDefinition, mappings));

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
        SearchReason.FORGOT_DATE,
        SearchReason.WAS_SICK,
        SearchReason.LACK_OF_TRANSPORTATION,
        SearchReason.BAD_SERVICE,
        SearchReason.PROVIDER_FEAR,
        SearchReason.PROVIDER_ABSENCE,
        SearchReason.SECONDARY_EFFECTS,
        SearchReason.TRADITIONAL_TREATMENT,
        SearchReason.TRANSFERED_OUT,
        SearchReason.AUTO_TRANSFER,
        SearchReason.ART_ABANDONMENT,
        SearchReason.OTHER_MISSING,
        SearchReason.PATIENT_IS_FINE,
        SearchReason.PATIENT_HAD_ISSUES,
        SearchReason.FAMILY_CONCERN,
        SearchReason.MEDICATION_IS_DOING_BAD,
        SearchReason.LACK_OF_FAMILY_SUPPORT,
        SearchReason.ISSUES_TO_HAVE_MEDICATIONS,
        SearchReason.DIAGNOSIS_NOT_REVEALED,
        SearchReason.OTHER_REPORT);

    dataSetDefinition.addColumn(
        "PSF", "PATIENTS SEARCH: FOUND", EptsReportUtils.map(patientFoundIndicator, mappings), "");

    dataSetDefinition.addColumn(
        "PSF_CH",
        "PATIENTS SEARCH: FOUND",
        EptsReportUtils.map(patientFoundIndicator, mappings),
        "children=children");

    dataSetDefinition.addColumn(
        "PSF_AD",
        "PATIENTS SEARCH: FOUND",
        EptsReportUtils.map(patientFoundIndicator, mappings),
        "adult=adult");

    this.addColumns(
        dataSetDefinition,
        patientFoundIndicator,
        "CH",
        mappings,
        "children=children",
        SearchReason.FORGOT_DATE,
        SearchReason.WAS_SICK,
        SearchReason.LACK_OF_TRANSPORTATION,
        SearchReason.BAD_SERVICE,
        SearchReason.PROVIDER_FEAR,
        SearchReason.PROVIDER_ABSENCE,
        SearchReason.SECONDARY_EFFECTS,
        SearchReason.TRADITIONAL_TREATMENT,
        SearchReason.TRANSFERED_OUT,
        SearchReason.AUTO_TRANSFER,
        SearchReason.ART_ABANDONMENT,
        SearchReason.OTHER_MISSING,
        SearchReason.PATIENT_IS_FINE,
        SearchReason.PATIENT_HAD_ISSUES,
        SearchReason.FAMILY_CONCERN,
        SearchReason.MEDICATION_IS_DOING_BAD,
        SearchReason.LACK_OF_FAMILY_SUPPORT,
        SearchReason.ISSUES_TO_HAVE_MEDICATIONS,
        SearchReason.DIAGNOSIS_NOT_REVEALED,
        SearchReason.OTHER_REPORT);

    this.addColumns(
        dataSetDefinition,
        patientFoundIndicator,
        "AD",
        mappings,
        "adult=adult",
        SearchReason.FORGOT_DATE,
        SearchReason.WAS_SICK,
        SearchReason.LACK_OF_TRANSPORTATION,
        SearchReason.BAD_SERVICE,
        SearchReason.PROVIDER_FEAR,
        SearchReason.PROVIDER_ABSENCE,
        SearchReason.SECONDARY_EFFECTS,
        SearchReason.TRADITIONAL_TREATMENT,
        SearchReason.TRANSFERED_OUT,
        SearchReason.AUTO_TRANSFER,
        SearchReason.ART_ABANDONMENT,
        SearchReason.OTHER_MISSING,
        SearchReason.PATIENT_IS_FINE,
        SearchReason.PATIENT_HAD_ISSUES,
        SearchReason.FAMILY_CONCERN,
        SearchReason.MEDICATION_IS_DOING_BAD,
        SearchReason.LACK_OF_FAMILY_SUPPORT,
        SearchReason.ISSUES_TO_HAVE_MEDICATIONS,
        SearchReason.DIAGNOSIS_NOT_REVEALED,
        SearchReason.OTHER_REPORT);

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
              this.searchPatientsCohortQueries.findPatientsByAbsenseReason(
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
          "PSF_" + label + "_" + reason.getLabel() + "_" + reason.ordinal(),
          "PATIENTS SEARCH: FOUND " + label + " " + reason.ordinal(),
          EptsReportUtils.map(cohortIndicator, mappings),
          dimension + "|" + reason.getLabel() + "=" + reason.getLabel());
    }
  }
}
