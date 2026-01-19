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
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.ABOVE_SIXTY_FIVE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FIFTEEN_TO_NINETEEN;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FIFTY_FIVE_TO_FIFTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FIFTY_TO_FIFTY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FIVE_TO_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FORTY_FIVE_TO_FORTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FORTY_TO_FORTY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.ONE_TO_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.SIXTY_TO_SIXTY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.TEN_TO_FOURTEEN;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.THIRTY_FIVE_TO_THIRTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.THIRTY_TO_THRITY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.TWENTY_FIVE_TO_TWENTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.TWENTY_TO_TWENTY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.UNDER_ONE;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.KeyPopulationDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.Gender;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxNewDataset extends BaseDataSet {

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private KeyPopulationDimension keyPopulationDimension;

  public DataSetDefinition constructTxNewDataset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("TX_NEW Data Set");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition patientEnrolledInART =
        this.txNewCohortQueries.getTxNewCompositionCohort("patientEnrolledInART");

    final CohortIndicator patientEnrolledInHIVStartedARTIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(patientEnrolledInART, mappings));

    final CohortDefinition patientWithLessCD4Definition =
        this.txNewCohortQueries.findPatientsWithCD4LessThan200();

    final CohortIndicator patientWithLessCD4Indicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientWithLessCD4Indicator",
            EptsReportUtils.map(patientWithLessCD4Definition, mappings));

    final CohortDefinition patientWithGreaterCD4Definition =
        this.txNewCohortQueries.findPatientsWIthCD4GreaterOrEqual200();

    final CohortIndicator patientWithGreaterCD4DIndicatior =
        this.eptsGeneralIndicator.getIndicator(
            "patientWithGreaterCD4DIndicatior",
            EptsReportUtils.map(patientWithGreaterCD4Definition, mappings));

    final CohortDefinition patientsWithUnknownCD4Definition =
        this.txNewCohortQueries.findPatientsWithUnknownCD4();

    final CohortIndicator patientsWithUnknownCD4Indicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsWithUnknownCD4Definition",
            EptsReportUtils.map(patientsWithUnknownCD4Definition, mappings));

    dataSetDefinition.addDimension(
        "breastfeeding",
        EptsReportUtils.map(this.eptsCommonDimension.findPatientsWhoAreBreastfeeding(), mappings));

    this.addDimensions(
        dataSetDefinition,
        mappings,
        Arrays.asList("LCD4", "GCD4", "UCD4"),
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
        ABOVE_FIFTY,
        FIFTY_TO_FIFTY_FOUR,
        FIFTY_FIVE_TO_FIFTY_NINE,
        SIXTY_TO_SIXTY_FOUR,
        ABOVE_SIXTY_FIVE);

    dataSetDefinition.addDimension(
        "gender", EptsReportUtils.map(this.eptsCommonDimension.gender(), ""));

    this.addAGenderDimensionForUnkwonAgeDimension(dataSetDefinition);

    dataSetDefinition.addDimension(
        "homosexual",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreHomosexual(), mappings));

    dataSetDefinition.addDimension(
        "drug-user",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoUseDrugs(), mappings));

    dataSetDefinition.addDimension(
        "prisioner",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreInPrison(), mappings));

    dataSetDefinition.addDimension(
        "sex-worker",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreSexWorker(), mappings));

    dataSetDefinition.addColumn(
        "1All",
        "TX_NEW: New on ART",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "LCD4",
        "TX_NEW: CD4 Less than 200",
        EptsReportUtils.map(patientWithLessCD4Indicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "GCD4",
        "TX_NEW: CD4 greater or equal 200",
        EptsReportUtils.map(patientWithGreaterCD4DIndicatior, mappings),
        "");

    dataSetDefinition.addColumn(
        "UCD4",
        "TX_NEW: Unknown CD4",
        EptsReportUtils.map(patientsWithUnknownCD4Indicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "ANC",
        "TX_NEW: Breastfeeding Started ART",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "breastfeeding=breastfeeding");

    this.addColums(
        dataSetDefinition,
        mappings,
        "LCD4",
        patientWithLessCD4Indicator,
        FIVE_TO_NINE,
        TEN_TO_FOURTEEN,
        FIFTEEN_TO_NINETEEN,
        TWENTY_TO_TWENTY_FOUR,
        TWENTY_FIVE_TO_TWENTY_NINE,
        THIRTY_TO_THRITY_FOUR,
        THIRTY_FIVE_TO_THIRTY_NINE,
        FORTY_TO_FORTY_FOUR,
        FORTY_FIVE_TO_FORTY_NINE,
        ABOVE_FIFTY,
        FIFTY_TO_FIFTY_FOUR,
        FIFTY_FIVE_TO_FIFTY_NINE,
        SIXTY_TO_SIXTY_FOUR,
        ABOVE_SIXTY_FIVE);

    this.addColums(
        dataSetDefinition,
        mappings,
        "GCD4",
        patientWithGreaterCD4DIndicatior,
        FIVE_TO_NINE,
        TEN_TO_FOURTEEN,
        FIFTEEN_TO_NINETEEN,
        TWENTY_TO_TWENTY_FOUR,
        TWENTY_FIVE_TO_TWENTY_NINE,
        THIRTY_TO_THRITY_FOUR,
        THIRTY_FIVE_TO_THIRTY_NINE,
        FORTY_TO_FORTY_FOUR,
        FORTY_FIVE_TO_FORTY_NINE,
        ABOVE_FIFTY,
        FIFTY_TO_FIFTY_FOUR,
        FIFTY_FIVE_TO_FIFTY_NINE,
        SIXTY_TO_SIXTY_FOUR,
        ABOVE_SIXTY_FIVE);

    this.addColums(
        dataSetDefinition,
        mappings,
        "UCD4",
        patientsWithUnknownCD4Indicator,
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
        ABOVE_FIFTY,
        FIFTY_TO_FIFTY_FOUR,
        FIFTY_FIVE_TO_FIFTY_NINE,
        SIXTY_TO_SIXTY_FOUR,
        ABOVE_SIXTY_FIVE);

    dataSetDefinition.addColumn(
        "LCD4-males-unknownM",
        "CD4 < 200 for Unknown Age - Male",
        EptsReportUtils.map(patientWithLessCD4Indicator, mappings),
        this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN)
            + "="
            + this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN));

    dataSetDefinition.addColumn(
        "LCD4-females-unknownF",
        "CD4 < 200 for Unknown Age - Female",
        EptsReportUtils.map(patientWithLessCD4Indicator, mappings),
        this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN)
            + "="
            + this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN));

    dataSetDefinition.addColumn(
        "GCD4-males-unknownM",
        "CD4 >= 200 for Unknown Age - Male",
        EptsReportUtils.map(patientWithGreaterCD4DIndicatior, mappings),
        this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN)
            + "="
            + this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN));

    dataSetDefinition.addColumn(
        "GCD4-females-unknownF",
        "CD4 >=200 for Unknown Age - Male",
        EptsReportUtils.map(patientWithGreaterCD4DIndicatior, mappings),
        this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN)
            + "="
            + this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN));

    dataSetDefinition.addColumn(
        "UCD4-males-unknownM",
        "Unknown CD4 value for Unknown Age - Male",
        EptsReportUtils.map(patientsWithUnknownCD4Indicator, mappings),
        this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN)
            + "="
            + this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN));

    dataSetDefinition.addColumn(
        "UCD4-females-unknownF",
        "Unknown CD4 value for Unknown Age - Female",
        EptsReportUtils.map(patientsWithUnknownCD4Indicator, mappings),
        this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN)
            + "="
            + this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN));

    dataSetDefinition.addColumn(
        "N-MSM",
        "Homosexual",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "gender=M|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "N-PWID",
        "Drugs User",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "drug-user=drug-user");

    dataSetDefinition.addColumn(
        "N-PRI",
        "Prisioners",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "prisioner=prisioner");

    dataSetDefinition.addColumn(
        "N-FSW",
        "Sex Worker",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "gender=F|sex-worker=sex-worker");

    return dataSetDefinition;
  }

  public DataSetDefinition constructTxNewDatasetForIMER() {
    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("TX_NEW Data Set");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition patientEnrolledInART =
        this.txNewCohortQueries.getTxNewCompositionCohort("patientEnrolledInART");

    final CohortIndicator patientEnrolledInHIVStartedARTIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(patientEnrolledInART, mappings));

    dataSetDefinition.addDimension(
        "gender", EptsReportUtils.map(this.eptsCommonDimension.gender(), ""));
    this.addDimensions(
        dataSetDefinition,
        mappings,
        Arrays.asList("N"),
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
        ABOVE_FIFTY,
        FIFTY_TO_FIFTY_FOUR,
        FIFTY_FIVE_TO_FIFTY_NINE,
        SIXTY_TO_SIXTY_FOUR,
        ABOVE_SIXTY_FIVE);

    dataSetDefinition.addDimension(
        this.getColumnName(AgeRange.UNKNOWN, Gender.MALE),
        EptsReportUtils.map(
            this.eptsCommonDimension.findPatientsWithUnknownAgeByGender(
                this.getColumnName(AgeRange.UNKNOWN, Gender.MALE), Gender.MALE),
            ""));

    dataSetDefinition.addDimension(
        this.getColumnName(AgeRange.UNKNOWN, Gender.FEMALE),
        EptsReportUtils.map(
            this.eptsCommonDimension.findPatientsWithUnknownAgeByGender(
                this.getColumnName(AgeRange.UNKNOWN, Gender.FEMALE), Gender.FEMALE),
            ""));

    dataSetDefinition.addDimension(
        "breastfeeding",
        EptsReportUtils.map(this.eptsCommonDimension.findPatientsWhoAreBreastfeeding(), mappings));

    dataSetDefinition.addDimension(
        "homosexual",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreHomosexual(), mappings));

    dataSetDefinition.addDimension(
        "drug-user",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoUseDrugs(), mappings));

    dataSetDefinition.addDimension(
        "prisioner",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreInPrison(), mappings));

    dataSetDefinition.addDimension(
        "sex-worker",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreSexWorker(), mappings));

    dataSetDefinition.addColumn(
        "1All",
        "TX_NEW: New on ART",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "ANC",
        "TX_NEW: Breastfeeding Started ART",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "breastfeeding=breastfeeding");

    this.addColums(
        dataSetDefinition,
        mappings,
        "N",
        patientEnrolledInHIVStartedARTIndicator,
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
        ABOVE_FIFTY,
        FIFTY_TO_FIFTY_FOUR,
        FIFTY_FIVE_TO_FIFTY_NINE,
        SIXTY_TO_SIXTY_FOUR,
        ABOVE_SIXTY_FIVE);
    this.addColums(
        dataSetDefinition, "", "N", patientEnrolledInHIVStartedARTIndicator, AgeRange.UNKNOWN);

    dataSetDefinition.addColumn(
        "N-MSM",
        "Homosexual",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "gender=M|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "N-PWID",
        "Drugs User",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "drug-user=drug-user");

    dataSetDefinition.addColumn(
        "N-PRI",
        "Prisioners",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "prisioner=prisioner");

    dataSetDefinition.addColumn(
        "N-FSW",
        "Sex Worker",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "gender=F|sex-worker=sex-worker");

    return dataSetDefinition;
  }

  private void addAGenderDimensionForUnkwonAgeDimension(
      final CohortIndicatorDataSetDefinition dataSetDefinition) {
    dataSetDefinition.addDimension(
        this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN),
        EptsReportUtils.map(
            this.eptsCommonDimension.findPatientsWithUnknownAgeByGender(
                this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN), Gender.MALE),
            ""));

    dataSetDefinition.addDimension(
        this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN),
        EptsReportUtils.map(
            this.eptsCommonDimension.findPatientsWithUnknownAgeByGender(
                this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN), Gender.FEMALE),
            ""));
  }

  private void addColums(
      final CohortIndicatorDataSetDefinition dataSetDefinition,
      final String mappings,
      final String columnPrefix,
      final CohortIndicator cohortIndicator,
      final AgeRange... rannges) {

    for (final AgeRange range : rannges) {

      final String maleName = this.getColumnName(columnPrefix, range, Gender.MALE);
      final String femaleName = this.getColumnName(columnPrefix, range, Gender.FEMALE);

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

  private String getDimensionNameForUnkwonAge(final Gender gender, final AgeRange ageRange) {
    String name = "DM-males-" + ageRange.getName() + "" + gender.getName();

    if (gender.equals(Gender.FEMALE)) {
      name = "DM-females-" + ageRange.getName() + "" + gender.getName();
    }
    return name;
  }

  private void addDimensions(
      final CohortIndicatorDataSetDefinition cohortIndicatorDataSetDefinition,
      final String mappings,
      final List<String> columnPrefixs,
      final AgeRange... ranges) {

    for (final AgeRange range : ranges) {
      for (final String columnPrefix : columnPrefixs) {

        cohortIndicatorDataSetDefinition.addDimension(
            this.getColumnName(columnPrefix, range, Gender.MALE),
            EptsReportUtils.map(
                this.eptsCommonDimension.findPatientsWhoAreNewlyEnrolledOnArtByAgeAndGender(
                    this.getColumnName(columnPrefix, range, Gender.MALE),
                    range,
                    Gender.MALE.getName()),
                mappings));

        cohortIndicatorDataSetDefinition.addDimension(
            this.getColumnName(columnPrefix, range, Gender.FEMALE),
            EptsReportUtils.map(
                this.eptsCommonDimension.findPatientsWhoAreNewlyEnrolledOnArtByAgeAndGender(
                    this.getColumnName(columnPrefix, range, Gender.FEMALE),
                    range,
                    Gender.FEMALE.getName()),
                mappings));
      }
    }
  }

  private String getColumnName(
      final String columnPrefix, final AgeRange range, final Gender gender) {
    return range.getDesagregationColumnName(columnPrefix, gender);
  }

  private String getColumnName(final AgeRange range, final Gender gender) {
    return range.getDesagregationColumnName("N", gender);
  }
}
