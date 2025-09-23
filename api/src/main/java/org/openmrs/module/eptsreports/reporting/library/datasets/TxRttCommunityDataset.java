/** */
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
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxRTTCohortQueries;
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

/** @author Stélio Moiane */
@Component
public class TxRttCommunityDataset extends BaseDataSet {

  @Autowired private TxRTTCohortQueries txRTTCohortQueries;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private KeyPopulationDimension keyPopulationDimension;

  public DataSetDefinition constructTxRttDataset() {

    final CohortIndicatorDataSetDefinition definition = new CohortIndicatorDataSetDefinition();
    definition.setName("TX RTT Dataset");
    definition.addParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition patientsOnRttDefinition =
        this.txRTTCohortQueries.getCommunityPatientsOnRTT();
    final CohortDefinition patientsWithCD4LessThan200 =
        this.txRTTCohortQueries.findPatientsWithCD4LessThan200Community();
    final CohortDefinition patientsWIthCD4GreaterOrEqual200 =
        this.txRTTCohortQueries.findPatientsWIthCD4GreaterOrEqual200Community();
    final CohortDefinition patientsWithUnknownCD4 =
        this.txRTTCohortQueries.findPatientsWithUnknownCD4Community();
    final CohortDefinition patientsNotEligibleToCD4 =
        this.txRTTCohortQueries.findPatientsNotEligibleToCD4Community();

    final CohortIndicator patientOnRttIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsOnTxRtt", EptsReportUtils.map(patientsOnRttDefinition, mappings));

    final CohortIndicator patientsWithCD4LessThan200Indicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsWithCD4LessThan200",
            EptsReportUtils.map(patientsWithCD4LessThan200, mappings));

    final CohortIndicator patientsWIthCD4GreaterOrEqual200Indicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsWIthCD4GreaterOrEqual200",
            EptsReportUtils.map(patientsWIthCD4GreaterOrEqual200, mappings));

    final CohortIndicator patientsWithUnknownCD4Indicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsWithUnknownCD4", EptsReportUtils.map(patientsWithUnknownCD4, mappings));

    final CohortIndicator patientsNotEligibleToCD4Indicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsNotEligibleToCD4", EptsReportUtils.map(patientsNotEligibleToCD4, mappings));

    this.addDimensions(
        definition,
        "endDate=${endDate}",
        Arrays.asList("R-LCD4", "R-GCD4", "R-UCD4", "R-NE-CD4"),
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

    definition.addDimension("gender", EptsReportUtils.map(this.eptsCommonDimension.gender(), ""));

    this.addAGenderDimensionForUnkwonAgeDimension(definition);

    definition.addDimension(
        "homosexual",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreHomosexual(), mappings));

    definition.addDimension(
        "drug-user",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoUseDrugs(), mappings));

    definition.addDimension(
        "prisioner",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreInPrison(), mappings));

    definition.addDimension(
        "sex-worker",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreSexWorker(), mappings));

    definition.addColumn(
        "RTTALL", "TX_RTT ALL patients", EptsReportUtils.map(patientOnRttIndicator, mappings), "");

    definition.addColumn(
        "R-LCD4",
        "CD4 < 200 - Total",
        EptsReportUtils.map(patientsWithCD4LessThan200Indicator, mappings),
        "");
    definition.addColumn(
        "R-GCD4",
        " CD4 ≥ 200 - Total",
        EptsReportUtils.map(patientsWIthCD4GreaterOrEqual200Indicator, mappings),
        "");
    definition.addColumn(
        "R-UCD4",
        "Unknown CD4 - Total ",
        EptsReportUtils.map(patientsWithUnknownCD4Indicator, mappings),
        "");
    definition.addColumn(
        "R-NE-CD4",
        "Not Eligible for CD4 - Total ",
        EptsReportUtils.map(patientsNotEligibleToCD4Indicator, mappings),
        "");

    this.addColums(
        definition,
        mappings,
        "R-LCD4",
        patientsWithCD4LessThan200Indicator,
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
        definition,
        mappings,
        "R-GCD4",
        patientsWIthCD4GreaterOrEqual200Indicator,
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
        definition,
        mappings,
        "R-UCD4",
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

    this.addColums(
        definition,
        mappings,
        "R-NE-CD4",
        patientsNotEligibleToCD4Indicator,
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

    definition.addColumn(
        "R-LCD4-males-unknownM",
        "CD4 < 200 for Unknown Age - Male",
        EptsReportUtils.map(patientsWithCD4LessThan200Indicator, mappings),
        this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN)
            + "="
            + this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN));

    definition.addColumn(
        "R-LCD4-females-unknownF",
        "CD4 < 200 for Unknown Age - Female",
        EptsReportUtils.map(patientsWithCD4LessThan200Indicator, mappings),
        this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN)
            + "="
            + this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN));

    definition.addColumn(
        "R-GCD4-males-unknownM",
        "CD4 >= 200 for Unknown Age - Male",
        EptsReportUtils.map(patientsWIthCD4GreaterOrEqual200Indicator, mappings),
        this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN)
            + "="
            + this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN));

    definition.addColumn(
        "R-GCD4-females-unknownF",
        "CD4 >=200 for Unknown Age - Male",
        EptsReportUtils.map(patientsWIthCD4GreaterOrEqual200Indicator, mappings),
        this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN)
            + "="
            + this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN));

    definition.addColumn(
        "R-UCD4-males-unknownM",
        "Unknown CD4 value for Unknown Age - Male",
        EptsReportUtils.map(patientsWithUnknownCD4Indicator, mappings),
        this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN)
            + "="
            + this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN));

    definition.addColumn(
        "R-UCD4-females-unknownF",
        "Unknown CD4 value for Unknown Age - Female",
        EptsReportUtils.map(patientsWithUnknownCD4Indicator, mappings),
        this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN)
            + "="
            + this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN));

    definition.addColumn(
        "R-NE-CD4-males-unknownM",
        "Not Eligible for CD4 - Male",
        EptsReportUtils.map(patientsNotEligibleToCD4Indicator, mappings),
        this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN)
            + "="
            + this.getDimensionNameForUnkwonAge(Gender.MALE, AgeRange.UNKNOWN));

    definition.addColumn(
        "R-NE-CD4-females-unknownF",
        "Not Eligible for CD4 - Female",
        EptsReportUtils.map(patientsNotEligibleToCD4Indicator, mappings),
        this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN)
            + "="
            + this.getDimensionNameForUnkwonAge(Gender.FEMALE, AgeRange.UNKNOWN));

    definition.addColumn(
        "R-MSM",
        "Men who have sex with men (MSM)",
        EptsReportUtils.map(patientOnRttIndicator, mappings),
        "gender=M|homosexual=homosexual");

    definition.addColumn(
        "R-PWID",
        "People who inject drugs (PWID)",
        EptsReportUtils.map(patientOnRttIndicator, mappings),
        "drug-user=drug-user");

    definition.addColumn(
        "R-PRI",
        "People in prison and other closed settings",
        EptsReportUtils.map(patientOnRttIndicator, mappings),
        "prisioner=prisioner");

    definition.addColumn(
        "R-FSW",
        "Female sex workers (FSW)",
        EptsReportUtils.map(patientOnRttIndicator, mappings),
        "gender=F|sex-worker=sex-worker");

    definition.addColumn(
        "R-DurationIIT-LESS-3MONTHS",
        "Duration of IIT Before returning Treatment <3 months",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Experienced treatment interruption of  < 3 months before returning to treatment",
                EptsReportUtils.map(
                    this.txRTTCohortQueries
                        .getDurationInterruptionOfTreatmentLessThan3MonthsCommunity(),
                    mappings)),
            mappings),
        "");

    definition.addColumn(
        "R-DurationIIT-BETWEEN-3-5MONTHS",
        "Duration of IIT Before returning Treatment Between 3-5 months",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Experienced treatment interruption of   3-5 months before returning to treatment",
                EptsReportUtils.map(
                    this.txRTTCohortQueries
                        .getDurationInterruptionOfTreatmentBetween3And5MonthsCommunity(),
                    mappings)),
            mappings),
        "");

    definition.addColumn(
        "R-DurationIIT-GREATER-OR-EQUAL-6MONTHS",
        "Duration of IIT Before returning Treatment Greater Or Equal 6 months",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Experienced treatment interruption of  6 or more months before returning to treatment",
                EptsReportUtils.map(
                    this.txRTTCohortQueries
                        .getDurationInterruptionOfTreatmentGreaterOrEqual6MonthsCommunity(),
                    mappings)),
            mappings),
        "");

    definition.addColumn(
        "PLHIVLESS12MONTH",
        "PLHIV <12 months",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Experienced treatment interruption of  <12 months before returning to treatment",
                EptsReportUtils.map(
                    this.txRTTCohortQueries.getPLHIVLess12MonthCalculationCommunity(), mappings)),
            mappings),
        "");

    definition.addColumn(
        "PLHIVGREATER12MONTH",
        "PLHIV >=12 months",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Experienced treatment interruption of  12 or more months before returning to treatment",
                EptsReportUtils.map(
                    this.txRTTCohortQueries.getPLHIVGreather12MonthCalculationCommunity(),
                    mappings)),
            mappings),
        "");

    definition.addColumn(
        "PLHIVUNKOWN",
        "Unknown Duration",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patients PLHIV With unknown date of IIT ",
                EptsReportUtils.map(
                    this.txRTTCohortQueries.getPLHIVUnknownDesaggregationCommunity(), mappings)),
            mappings),
        "");

    definition.addColumn(
        "PLHIVTOTAL",
        "PLHIV Total",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Patients PLHIV - All",
                EptsReportUtils.map(this.txRTTCohortQueries.getPLHIVTotalCommunity(), mappings)),
            mappings),
        "");

    return definition;
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
                this.eptsCommonDimension.findPatientsByGenderAndRange(
                    this.getColumnName(columnPrefix, range, Gender.MALE), range, Gender.MALE),
                mappings));

        cohortIndicatorDataSetDefinition.addDimension(
            this.getColumnName(columnPrefix, range, Gender.FEMALE),
            EptsReportUtils.map(
                this.eptsCommonDimension.findPatientsByGenderAndRange(
                    this.getColumnName(columnPrefix, range, Gender.FEMALE), range, Gender.FEMALE),
                mappings));
      }
    }
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

  private String getColumnName(
      final String columnPrefix, final AgeRange range, final Gender gender) {
    return range.getDesagregationColumnName(columnPrefix, gender);
  }
}
