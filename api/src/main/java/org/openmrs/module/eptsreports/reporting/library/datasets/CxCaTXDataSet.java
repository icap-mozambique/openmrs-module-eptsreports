package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.CxCaTXCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CxCaTXDataSet extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;
  @Autowired private CxCaTXCohortQueries cxCaTXCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructDatset(final Boolean isCommunity) {
    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            this.eptsCommonDimension.ageCX(this.ageDimensionCohort), "effectiveDate=${endDate}"));

    dataSetDefinition.addDimension(
        "gender", EptsReportUtils.map(this.eptsCommonDimension.gender(), ""));

    dataSetDefinition.addColumn(
        "CXTXTOTAL",
        "Number of cervical cancer screen-positive women "
            + "who are HIV-positive and on ART eligible for cryotherapy, "
            + "thermocoagulation or LEEP who received cryotherapy, thermocoagulation or LEEP ",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "CXCA_TX (Numerator): Number of cervical cancer screen-positive women "
                    + "who are HIV-positive and on ART eligible for cryotherapy, "
                    + "thermocoagulation or LEEP who received cryotherapy, thermocoagulation or LEEP ",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoReceivedTreatmentTypeDuringReportingPeriodTotalDenominator(
                            isCommunity),
                    mappings)),
            mappings),
        "gender=F|age=15+");

    /*
     * First Screen
     */
    dataSetDefinition.addColumn(
        "CXTXFRT",
        "1st time screened  (total)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "1st time screened  total",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodFirstScreean(
                            isCommunity),
                    mappings)),
            mappings),
        "gender=F|age=15+");

    this.addRow(
        dataSetDefinition,
        "CXTXFC",
        "1st time screened (Cryotherapy)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "1st time screened (Cryotherapy)",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoReceivedTreatmentTypeDuringReportingPeriodFirstScreeanCryotherapyDesagregations(
                            isCommunity),
                    mappings)),
            mappings),
        this.getCXColumns());

    this.addRow(
        dataSetDefinition,
        "CXTXFT",
        "1st time screened (Thermocoagulation)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "1st time screened (Thermocoagulation)",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodFirstScreeanThermocoagulationDesagregations(
                            isCommunity),
                    mappings)),
            mappings),
        this.getCXColumns());

    this.addRow(
        dataSetDefinition,
        "CXTXFL",
        "1st time screened (Leep)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "1st time screened (Leep)",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodFirstScreeanLeepDesagregations(
                            isCommunity),
                    mappings)),
            mappings),
        this.getCXColumns());

    /*
     * Rescreened after previous negative
     */
    dataSetDefinition.addColumn(
        "CXTXRNTOTAL",
        "Rescreened after previous negative (total) ",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Rescreened after previous negative (total)",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegative(
                            isCommunity),
                    mappings)),
            mappings),
        "gender=F|age=15+");

    this.addRow(
        dataSetDefinition,
        "CXTXRNC",
        "Rescreened after previous negative (Cryotherapy)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Rescreened after previous negative (Cryotherapy)",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegativeCryotherapyDesagregations(
                            isCommunity),
                    mappings)),
            mappings),
        this.getCXColumns());

    this.addRow(
        dataSetDefinition,
        "CXTXRNT",
        "Rescreened after previous negative (Thermocoagulation)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Rescreened after previous negative (Thermocoagulation)",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegativeThermocoagulationDesagregations(
                            isCommunity),
                    mappings)),
            mappings),
        this.getCXColumns());

    this.addRow(
        dataSetDefinition,
        "CXTXRNL",
        "Rescreened after previous negative (Leep)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Rescreened after previous negative (Leep)",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegativeLeepDesagregations(
                            isCommunity),
                    mappings)),
            mappings),
        this.getCXColumns());

    /*
     * Post Tretment
     */

    dataSetDefinition.addColumn(
        "CXTXPTTOTAL",
        "Post-treatment follow-up (total) ",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Post-treatment follow-up (total)",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUp(
                            isCommunity),
                    mappings)),
            mappings),
        "gender=F|age=15+");

    this.addRow(
        dataSetDefinition,
        "CXTXPTC",
        "Post-treatment follow-up (Cryotherapy)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Post-treatment follow-up (Cryotherapy)",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUpCryotherapyDesagregations(
                            isCommunity),
                    mappings)),
            mappings),
        this.getCXColumns());

    this.addRow(
        dataSetDefinition,
        "CXTXPTT",
        "Post-treatment follow-up (Thermocoagulation)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Post-treatment follow-up (Thermocoagulation)",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUpThermocoagulationDesagregations(
                            isCommunity),
                    mappings)),
            mappings),
        this.getCXColumns());

    this.addRow(
        dataSetDefinition,
        "CXTXPTL",
        "Post-treatment follow-up (Leep)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Post-treatment follow-up (Leep)",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUpLeepDesagregations(
                            isCommunity),
                    mappings)),
            mappings),
        this.getCXColumns());

    /*
     * Rescreened after previous positive
     */
    dataSetDefinition.addColumn(
        "CXTXRPTOTAL",
        "Rescreened after previous positive (total) ",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Rescreened after previous positive  total",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositive(
                            isCommunity),
                    mappings)),
            mappings),
        "gender=F|age=15+");

    this.addRow(
        dataSetDefinition,
        "CXTXRPC",
        "Rescreened after previous positive (Cryotherapy)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Rescreened after previous positive (Cryotherapy)",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositiveCryotherapypDesagregations(
                            isCommunity),
                    mappings)),
            mappings),
        this.getCXColumns());

    this.addRow(
        dataSetDefinition,
        "CXTXRPT",
        "Rescreened after previous positive (Thermocoagulation)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Rescreened after previous positive (Thermocoagulation)",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositiveThermocoagulationDesagregations(
                            isCommunity),
                    mappings)),
            mappings),
        this.getCXColumns());

    this.addRow(
        dataSetDefinition,
        "CXTXRPL",
        "Rescreened after previous positive (Leep)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "Rescreened after previous positive (Leep)",
                EptsReportUtils.map(
                    this.cxCaTXCohortQueries
                        .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositiveLeepDesagregations(
                            isCommunity),
                    mappings)),
            mappings),
        this.getCXColumns());

    return dataSetDefinition;
  }

  @Override
  public List<Parameter> getParameters() {
    final List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.add(ReportingConstants.START_DATE_PARAMETER);
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }

  private List<ColumnParameters> getCXColumns() {

    final ColumnParameters a1 = new ColumnParameters("15-19", "15-19", "gender=F|age=15-19", "01");
    final ColumnParameters a2 =
        new ColumnParameters("20-24", "20-24 years female", "gender=F|age=20-24", "02");
    final ColumnParameters a3 =
        new ColumnParameters("25-29", "25-29 years female", "gender=F|age=25-29", "03");
    final ColumnParameters a4 =
        new ColumnParameters("30-34", "30-34 female", "gender=F|age=30-34", "04");
    final ColumnParameters a5 =
        new ColumnParameters("35-39", "35-39 female", "gender=F|age=35-39", "05");
    final ColumnParameters a6 =
        new ColumnParameters("40-44", "40-44 female", "gender=F|age=40-44", "06");
    final ColumnParameters a7 =
        new ColumnParameters("45-49", "45-49 female", "gender=F|age=45-49", "07");
    final ColumnParameters a8 =
        new ColumnParameters("50-54", "50-54 female", "gender=F|age=50-54", "08");
    final ColumnParameters a9 =
        new ColumnParameters("55-59", "55-59 female", "gender=F|age=55-59", "09");
    final ColumnParameters a10 =
        new ColumnParameters("60-64", "60-64 female", "gender=F|age=60-64", "10");
    final ColumnParameters a11 =
        new ColumnParameters("65+", "65+ female", "gender=F|age=65+", "11");
    final ColumnParameters unknownF =
        new ColumnParameters("unknownF", "Unknown age", "gender=F|age=UK", "12");

    final ColumnParameters a13 =
        new ColumnParameters("subTotal", "subTotal", "gender=F|age=15+", "13");

    return Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, unknownF, a9, a10, a11, a13);
  }
}
