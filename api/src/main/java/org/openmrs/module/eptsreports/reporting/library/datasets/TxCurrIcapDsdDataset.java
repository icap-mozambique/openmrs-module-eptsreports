package org.openmrs.module.eptsreports.reporting.library.datasets;

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
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.IcapDsdDimensions;
import org.openmrs.module.eptsreports.reporting.library.dimensions.KeyPopulationDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.library.queries.DSDQueriesInterface.QUERY.DSDModeTypeLevel1;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.Gender;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class TxCurrIcapDsdDataset extends BaseDataSet {

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired private IcapDsdDimensions icapDsdDimensions;

  @Autowired private KeyPopulationDimension keyPopulationDimension;

  public CohortIndicatorDataSetDefinition constructTxCurrDataset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("TX_CURR Data Set");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "endDate=${endDate},location=${location}";

    final CohortDefinition txCurrCompositionCohort =
        this.txCurrCohortQueries.findPatientsWhoAreActiveOnART();

    final CohortIndicator txCurrIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "findPatientsWhoAreActiveOnART",
            EptsReportUtils.map(txCurrCompositionCohort, mappings));

    dataSetDefinition.addDimension(
        "gender", EptsReportUtils.map(this.eptsCommonDimension.gender(), ""));

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

    dataSetDefinition.addDimension(
        "dca",
        EptsReportUtils.map(
            this.icapDsdDimensions.findMDS("dca", DSDModeTypeLevel1.DCA_APE), mappings));

    dataSetDefinition.addDimension(
        "dcp",
        EptsReportUtils.map(
            this.icapDsdDimensions.findMDS("dcp", DSDModeTypeLevel1.DCP), mappings));

    dataSetDefinition.addDimension(
        "bm",
        EptsReportUtils.map(this.icapDsdDimensions.findMDS("bm", DSDModeTypeLevel1.BM), mappings));

    dataSetDefinition.addDimension(
        "cm",
        EptsReportUtils.map(this.icapDsdDimensions.findMDS("cm", DSDModeTypeLevel1.CM), mappings));

    /* Removido ao Pedido do Nacassaco
        dataSetDefinition.addDimension(
            "gaac",
            EptsReportUtils.map(
                this.icapDsdDimensions.findMDS("gaac", DSDModelTypeLevel2.GAAC), mappings));
    */
    this.addDimensions(
        dataSetDefinition,
        "endDate=${endDate}",
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
        FIFTY_TO_FIFTY_FOUR,
        FIFTY_FIVE_TO_FIFTY_NINE,
        SIXTY_TO_SIXTY_FOUR,
        ABOVE_SIXTY_FIVE);

    dataSetDefinition.addDimension(
        this.getName(Gender.MALE, AgeRange.UNKNOWN),
        EptsReportUtils.map(
            this.eptsCommonDimension.findPatientsWithUnknownAgeByGender(
                this.getName(Gender.MALE, AgeRange.UNKNOWN), Gender.MALE),
            ""));

    dataSetDefinition.addDimension(
        this.getName(Gender.FEMALE, AgeRange.UNKNOWN),
        EptsReportUtils.map(
            this.eptsCommonDimension.findPatientsWithUnknownAgeByGender(
                this.getName(Gender.FEMALE, AgeRange.UNKNOWN), Gender.FEMALE),
            ""));

    dataSetDefinition.addColumn(
        "C1All", "TX_CURR: Currently on ART", EptsReportUtils.map(txCurrIndicator, mappings), "");

    final List<String> models = Arrays.asList("dca", "dcp", "bm", "cm");

    for (final String mds : models) {

      dataSetDefinition.addColumn(
          "C1All-" + mds,
          "TX_CURR: Currently on ART - " + mds,
          EptsReportUtils.map(txCurrIndicator, mappings),
          mds + "=" + mds);

      dataSetDefinition.addColumn(
          "C-malesUnknownM-" + mds,
          "unknownM-" + mds,
          EptsReportUtils.map(txCurrIndicator, mappings),
          this.getName(Gender.MALE, AgeRange.UNKNOWN)
              + "="
              + this.getName(Gender.MALE, AgeRange.UNKNOWN)
              + "|"
              + mds
              + "="
              + mds);

      dataSetDefinition.addColumn(
          "C-femalesUnknownF-" + mds,
          "unknownF-" + mds,
          EptsReportUtils.map(txCurrIndicator, mappings),
          this.getName(Gender.FEMALE, AgeRange.UNKNOWN)
              + "="
              + this.getName(Gender.FEMALE, AgeRange.UNKNOWN)
              + "|"
              + mds
              + "="
              + mds);
    }

    this.addColums(
        dataSetDefinition,
        mappings,
        txCurrIndicator,
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
        FIFTY_TO_FIFTY_FOUR,
        FIFTY_FIVE_TO_FIFTY_NINE,
        SIXTY_TO_SIXTY_FOUR,
        ABOVE_SIXTY_FIVE);

    dataSetDefinition.addColumn(
        "C-malesUnknownM",
        "unknownM",
        EptsReportUtils.map(txCurrIndicator, mappings),
        this.getName(Gender.MALE, AgeRange.UNKNOWN)
            + "="
            + this.getName(Gender.MALE, AgeRange.UNKNOWN));

    dataSetDefinition.addColumn(
        "C-femalesUnknownF",
        "unknownF",
        EptsReportUtils.map(txCurrIndicator, mappings),
        this.getName(Gender.FEMALE, AgeRange.UNKNOWN)
            + "="
            + this.getName(Gender.FEMALE, AgeRange.UNKNOWN));

    dataSetDefinition.addColumn(
        "C-MSM",
        "Homosexual",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "gender=M|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "C-PWID",
        "Drugs User",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "drug-user=drug-user");

    dataSetDefinition.addColumn(
        "C-PRI",
        "Prisioners",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "prisioner=prisioner");

    dataSetDefinition.addColumn(
        "C-FSW",
        "Sex Worker",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "gender=F|sex-worker=sex-worker");

    this.addColums(
        dataSetDefinition,
        mappings,
        txCurrIndicator,
        models,
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
        FIFTY_TO_FIFTY_FOUR,
        FIFTY_FIVE_TO_FIFTY_NINE,
        SIXTY_TO_SIXTY_FOUR,
        ABOVE_SIXTY_FIVE);

    return dataSetDefinition;
  }

  private void addDimensions(
      final CohortIndicatorDataSetDefinition cohortIndicatorDataSetDefinition,
      final String mappings,
      final AgeRange... ranges) {

    for (final AgeRange range : ranges) {

      cohortIndicatorDataSetDefinition.addDimension(
          this.getName(Gender.MALE, range),
          EptsReportUtils.map(
              this.eptsCommonDimension.findPatientsByGenderAndRange(
                  this.getName(Gender.MALE, range), range, Gender.MALE),
              mappings));

      cohortIndicatorDataSetDefinition.addDimension(
          this.getName(Gender.FEMALE, range),
          EptsReportUtils.map(
              this.eptsCommonDimension.findPatientsByGenderAndRange(
                  this.getName(Gender.FEMALE, range), range, Gender.FEMALE),
              mappings));
    }
  }

  private void addColums(
      final CohortIndicatorDataSetDefinition dataSetDefinition,
      final String mappings,
      final CohortIndicator cohortIndicator,
      final AgeRange... ranges) {

    for (final AgeRange range : ranges) {

      final String maleName = this.getName(Gender.MALE, range);
      final String femaleName = this.getName(Gender.FEMALE, range);

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

  private void addColums(
      final CohortIndicatorDataSetDefinition dataSetDefinition,
      final String mappings,
      final CohortIndicator cohortIndicator,
      final List<String> mdsModels,
      final AgeRange... ranges) {

    for (final AgeRange range : ranges) {

      for (final String mds : mdsModels) {

        final String maleName = this.getName(Gender.MALE, range);
        final String femaleName = this.getName(Gender.FEMALE, range);

        dataSetDefinition.addColumn(
            maleName + "-" + mds,
            (maleName + mds).replace("-", " "),
            EptsReportUtils.map(cohortIndicator, mappings),
            maleName + "=" + maleName + "|" + mds + "=" + mds);

        dataSetDefinition.addColumn(
            femaleName + "-" + mds,
            (femaleName + mds).replace("-", " "),
            EptsReportUtils.map(cohortIndicator, mappings),
            femaleName + "=" + femaleName + "|" + mds + "=" + mds);
      }
    }
  }

  private String getName(final Gender gender, final AgeRange ageRange) {
    String name = "C-males-" + ageRange.getName() + "" + gender.getName();

    if (gender.equals(Gender.FEMALE)) {
      name = "C-females-" + ageRange.getName() + "" + gender.getName();
    }

    return name;
  }
}
