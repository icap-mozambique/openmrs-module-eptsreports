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

import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.ABOVE_FIFTY;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FIFTEEN_TO_NINETEEN;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FIVE_TO_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FORTY_FIVE_TO_FORTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FORTY_TO_FORTY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.ONE_TO_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.TEN_TO_FOURTEEN;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.THIRTY_FIVE_TO_THIRTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.THIRTY_TO_THRITY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.TWENTY_FIVE_TO_TWENTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.TWENTY_TO_TWENTY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.UNDER_ONE;

import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.CommunityType;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.Gender;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TxNewCommunityDispensationTypeDataset extends BaseDataSet {

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  private String prefix;

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getPrefix() {
    return prefix;
  }

  @Autowired
  @Qualifier("txNewAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructTxNewCommunityAllDataset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("TXNEW All Data Set");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition patientEnrolledInART =
        this.txNewCohortQueries.getTxNewCommunityCompositionTypeCohort(
            "communityPatientEnrolledInART", CommunityType.ALL);

    final CohortIndicator patientEnrolledInHIVStartedARTIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(patientEnrolledInART, mappings));

    /*
        final CohortDefinition patientEnrolledInARTAPE =
            this.txNewCohortQueries.getTxNewCommunityCompositionTypeCohort(
                "communityPatientEnrolledInART", CommunityType.COMMUNITY_DISPENSE_APE);

        final CohortIndicator patientEnrolledInHIVStartedARTIndicatorAPE =
            this.eptsGeneralIndicator.getIndicator(
                "patientNewlyEnrolledInHIVIndicator",
                EptsReportUtils.map(patientEnrolledInARTAPE, mappings));

        final CohortDefinition patientEnrolledInARTProvider =
            this.txNewCohortQueries.getTxNewCommunityCompositionTypeCohort(
                "communityPatientEnrolledInART", CommunityType.COMMUNITY_DISPENSE_PROVIDER);

        final CohortIndicator patientEnrolledInHIVStartedARTIndicatorProvider =
            this.eptsGeneralIndicator.getIndicator(
                "patientNewlyEnrolledInHIVIndicator",
                EptsReportUtils.map(patientEnrolledInARTProvider, mappings));

        final CohortDefinition patientEnrolledInARTDB =
            this.txNewCohortQueries.getTxNewCommunityCompositionTypeCohort(
                "communityPatientEnrolledInART", CommunityType.DAILY_MOBILE_BRIGADES);

        final CohortIndicator patientEnrolledInHIVStartedARTIndicatorDB =
            this.eptsGeneralIndicator.getIndicator(
                "patientNewlyEnrolledInHIVIndicator",
                EptsReportUtils.map(patientEnrolledInARTDB, mappings));

        final CohortDefinition patientEnrolledInARTDM =
            this.txNewCohortQueries.getTxNewCommunityCompositionTypeCohort(
                "communityPatientEnrolledInART", CommunityType.DAILY_MOBILE_CLINICS);

        final CohortIndicator patientEnrolledInHIVStartedARTIndicatorDM =
            this.eptsGeneralIndicator.getIndicator(
                "patientNewlyEnrolledInHIVIndicator",
                EptsReportUtils.map(patientEnrolledInARTDM, mappings));

        final CohortDefinition patientEnrolledInARTNB =
            this.txNewCohortQueries.getTxNewCommunityCompositionTypeCohort(
                "communityPatientEnrolledInART", CommunityType.NIGHT_MOBILE_BRIGADES);

        final CohortIndicator patientEnrolledInHIVStartedARTIndicatorNB =
            this.eptsGeneralIndicator.getIndicator(
                "patientNewlyEnrolledInHIVIndicator",
                EptsReportUtils.map(patientEnrolledInARTNB, mappings));

        final CohortDefinition patientEnrolledInARTNT =
            this.txNewCohortQueries.getTxNewCommunityCompositionTypeCohort(
                "communityPatientEnrolledInART", CommunityType.NIGHT_MOBILE_CLINICS);

        final CohortIndicator patientEnrolledInHIVStartedARTIndicatorNT =
            this.eptsGeneralIndicator.getIndicator(
                "patientNewlyEnrolledInHIVIndicator",
                EptsReportUtils.map(patientEnrolledInARTNT, mappings));
    */
    final CohortDefinition patientEnrolledInARTBM =
        this.txNewCohortQueries.getTxNewCommunityCompositionTypeCohort(
            "communityPatientEnrolledInART", CommunityType.MOBILE_BRIGADES);

    final CohortIndicator patientEnrolledInHIVStartedARTIndicatorBM =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(patientEnrolledInARTBM, mappings));

    final CohortDefinition patientEnrolledInARTCM =
        this.txNewCohortQueries.getTxNewCommunityCompositionTypeCohort(
            "communityPatientEnrolledInART", CommunityType.MOBILE_CLINICS);

    final CohortIndicator patientEnrolledInHIVStartedARTIndicatorCM =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(patientEnrolledInARTCM, mappings));

    dataSetDefinition.addColumn(
        "1All",
        "TX_NEW: New on ART Community",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "");

    /*
        dataSetDefinition.addColumn(
            "1AllPE",
            "TX_NEW: New on ART Community",
            EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicatorAPE, mappings),
            "");

        dataSetDefinition.addColumn(
            "1AllProvider",
            "TX_NEW: New on ART Community",
            EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicatorProvider, mappings),
            "");

        dataSetDefinition.addColumn(
            "1AllDB",
            "TX_NEW: New on ART Community",
            EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicatorDB, mappings),
            "");

        dataSetDefinition.addColumn(
            "1AllDM",
            "TX_NEW: New on ART Community",
            EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicatorDM, mappings),
            "");

        dataSetDefinition.addColumn(
            "1AllNB",
            "TX_NEW: New on ART Community",
            EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicatorNB, mappings),
            "");

        dataSetDefinition.addColumn(
            "1AllPEX",
            "TX_NEW: New on ART Community",
            EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicatorNT, mappings),
            "");
    */
    dataSetDefinition.addColumn(
        "1AllBM",
        "TX_NEW: Community Mobile Brigades",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicatorBM, mappings),
        "");

    dataSetDefinition.addColumn(
        "1AllCM",
        "TX_NEW: Community Mobile Clinics",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicatorCM, mappings),
        "");

    return dataSetDefinition;
  }

  public DataSetDefinition constructTxNewCommunityTypeDataset(CommunityType type) {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("TXNEW Community Data Set " + type.toString());
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition patientEnrolledInARTCommunityType =
        this.txNewCohortQueries.getTxNewCommunityCompositionTypeCohort(
            "communityPatientEnrolledInARTNormal", type);

    final CohortIndicator patientEnrolledInHIVStartedARTIndicatorCommunityType =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicatorNormal",
            EptsReportUtils.map(patientEnrolledInARTCommunityType, mappings));

    this.addDimensions(
        dataSetDefinition,
        mappings,
        UNDER_ONE,
        ONE_TO_FOUR,
        FIVE_TO_NINE,
        TEN_TO_FOURTEEN,
        FIFTEEN_TO_NINETEEN,
        TWENTY_TO_TWENTY_FOUR,
        TWENTY_FIVE_TO_TWENTY_NINE,
        THIRTY_TO_THRITY_FOUR,
        THIRTY_FIVE_TO_THIRTY_NINE,
        FORTY_TO_FORTY_FOUR,
        FORTY_FIVE_TO_FORTY_NINE,
        ABOVE_FIFTY);

    this.addColums(
        dataSetDefinition,
        mappings,
        patientEnrolledInHIVStartedARTIndicatorCommunityType,
        UNDER_ONE,
        ONE_TO_FOUR,
        FIVE_TO_NINE,
        TEN_TO_FOURTEEN,
        FIFTEEN_TO_NINETEEN,
        TWENTY_TO_TWENTY_FOUR,
        TWENTY_FIVE_TO_TWENTY_NINE,
        THIRTY_TO_THRITY_FOUR,
        THIRTY_FIVE_TO_THIRTY_NINE,
        FORTY_TO_FORTY_FOUR,
        FORTY_FIVE_TO_FORTY_NINE,
        ABOVE_FIFTY);

    return dataSetDefinition;
  }

  private void addColums(
      final CohortIndicatorDataSetDefinition dataSetDefinition,
      final String mappings,
      final CohortIndicator cohortIndicator,
      final AgeRange... rannges) {

    for (final AgeRange range : rannges) {

      final String maleName = this.getColumnName(range, Gender.MALE);
      final String femaleName = this.getColumnName(range, Gender.FEMALE);

      dataSetDefinition.addColumn(
          maleName,
          maleName.replace("-", " "),
          EptsReportUtils.map(cohortIndicator, mappings),
          maleName + "=" + maleName);

      dataSetDefinition.addColumn(
          femaleName,
          femaleName.replace("-", " "),
          EptsReportUtils.map(cohortIndicator, mappings),
          femaleName + "=" + femaleName);
    }
  }

  private void addDimensions(
      final CohortIndicatorDataSetDefinition cohortIndicatorDataSetDefinition,
      final String mappings,
      final AgeRange... ranges) {

    for (final AgeRange range : ranges) {

      cohortIndicatorDataSetDefinition.addDimension(
          this.getColumnName(range, Gender.MALE),
          EptsReportUtils.map(
              this.eptsCommonDimension.findPatientsWhoAreNewlyEnrolledOnArtByAgeAndGender(
                  this.getColumnName(range, Gender.MALE), range, Gender.MALE.getName()),
              mappings));

      cohortIndicatorDataSetDefinition.addDimension(
          this.getColumnName(range, Gender.FEMALE),
          EptsReportUtils.map(
              this.eptsCommonDimension.findPatientsWhoAreNewlyEnrolledOnArtByAgeAndGender(
                  this.getColumnName(range, Gender.FEMALE), range, Gender.FEMALE.getName()),
              mappings));
    }
  }

  private String getColumnName(final AgeRange range, final Gender gender) {

    return range.getDesagregationColumnName(prefix, gender);
  }
}
