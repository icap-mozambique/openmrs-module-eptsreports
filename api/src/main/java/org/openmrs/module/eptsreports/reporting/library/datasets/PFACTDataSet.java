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

import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.ABOVE_TWENTY;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.TEN_TO_NINETEEN;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.UNDER_TEN;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.DSDCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxRTTCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.DSDCommonDimensions;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PFACTDataSet extends BaseDataSet {

  private EptsCommonDimension eptsCommonDimension;

  private EptsGeneralIndicator eptsGeneralIndicator;

  private TxNewCohortQueries txNewCohortQueries;

  private TxRTTCohortQueries txRTTCohortQueries;

  private TxCurrCohortQueries txCurrCohortQueries;

  @Autowired
  public PFACTDataSet(
      EptsCommonDimension eptsCommonDimension,
      EptsGeneralIndicator eptsGeneralIndicator,
      DSDCohortQueries dsdCohortQueries,
      DSDCommonDimensions dsdCommonDimensions,
      TxNewCohortQueries txNewCohortQueries,
      TxRTTCohortQueries txRTTCohortQueries,
      TxCurrCohortQueries txCurrCohortQueries) {
    this.eptsCommonDimension = eptsCommonDimension;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.txNewCohortQueries = txNewCohortQueries;
    this.txRTTCohortQueries = txRTTCohortQueries;
    this.txCurrCohortQueries = txCurrCohortQueries;
  }

  public DataSetDefinition constructDSDDataset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();

    dsd.setName("PFACT Dataset");
    dsd.addParameters(getParameters());

    this.addAgeDimensions(dsd, UNDER_TEN, TEN_TO_NINETEEN, ABOVE_TWENTY);

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition patientsWithCD4LessThan200RTT =
        this.txRTTCohortQueries.getPLHIVGreather12MonthCalculationAndCD4Under200();

    final CohortIndicator patientsWithCD4LessThan200IndicatorRTT =
        this.eptsGeneralIndicator.getIndicator(
            "patientsWithCD4LessThan200",
            EptsReportUtils.map(patientsWithCD4LessThan200RTT, mappings));

    final CohortDefinition patientsWithRTTStage3_4 =
        this.txRTTCohortQueries.getPLHIVGreather12MonthCalculationAndStage3_4();

    final CohortIndicator patientsWithRTTStage3_4IndicatorRTT =
        this.eptsGeneralIndicator.getIndicator(
            "patientsRTTStage3_4", EptsReportUtils.map(patientsWithRTTStage3_4, mappings));

    final CohortDefinition patientWithLessCD4Definition =
        this.txNewCohortQueries.findPatientsWithCD4LessThan200();

    final CohortIndicator patientWithLessCD4Indicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientWithLessCD4Indicator",
            EptsReportUtils.map(patientWithLessCD4Definition, mappings));

    final CohortDefinition patientsWithCD4LessThan200TXCURRTBLAM =
        this.txCurrCohortQueries.findPatientsWithLessThan200CD4WhoAreActiveOnARTTBLAM();

    final CohortIndicator patientsWithCD4LessThan200IndicatorTXCURR =
        this.eptsGeneralIndicator.getIndicator(
            "patientsWithCD4LessThan200TbLam",
            EptsReportUtils.map(patientsWithCD4LessThan200TXCURRTBLAM, mappings));

    final CohortDefinition patientsWithCD4LessThan200TXCURRGenExpert =
        this.txCurrCohortQueries.findPatientsWithLessThan200CD4OnARTANDGenExpert();

    final CohortIndicator patientsWithCD4LessThan200IndicatorTXCURRGenExpert =
        this.eptsGeneralIndicator.getIndicator(
            "patientsWithCD4LessThan200GenExpert",
            EptsReportUtils.map(patientsWithCD4LessThan200TXCURRGenExpert, mappings));

    final CohortDefinition patientsWithCD4LessThan200TXCURRCrag =
        this.txCurrCohortQueries.findPatientsWithLessThan200CD4OnARTANDCrag();

    final CohortIndicator patientsWithCD4LessThan200IndicatorTXCURRCrag =
        this.eptsGeneralIndicator.getIndicator(
            "patientsWithCD4LessThan200GenExpert",
            EptsReportUtils.map(patientsWithCD4LessThan200TXCURRCrag, mappings));

    final CohortDefinition patientsOnARTWithDAHDiagnosticTB =
        this.txCurrCohortQueries.findPatientsinDAHAndDiagnosticTB();

    final CohortIndicator patientsOnARTWithDAHDiagnosticTBIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsOnARTWithDAHDiagnosticTB",
            EptsReportUtils.map(patientsOnARTWithDAHDiagnosticTB, mappings));

    final CohortDefinition patientsOnARTWithDAHDiagnosticTBTreament =
        this.txCurrCohortQueries.findPatientsinDAHAndDiagnosticTBTreatment();

    final CohortIndicator patientsOnARTWithDAHDiagnosticTBTreatmentIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsOnARTWithDAHDiagnosticTB",
            EptsReportUtils.map(patientsOnARTWithDAHDiagnosticTBTreament, mappings));

    final CohortDefinition patientsOnARTWithDAHCragPositive =
        this.txCurrCohortQueries.findPatientsinDAHAndCragPositive();

    final CohortIndicator patientsOnARTWithDAHCragPositiveIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsOnARTWithDAHDiagnosticTB",
            EptsReportUtils.map(patientsOnARTWithDAHCragPositive, mappings));

    final CohortDefinition patientsOnARTWithDAHMCCDiagnostic =
        this.txCurrCohortQueries.findPatientsinDAHAndMCCDiagnostic();

    final CohortIndicator patientsOnARTWithDAHMCCDiagnosticndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsOnARTWithDAHDiagnosticTB",
            EptsReportUtils.map(patientsOnARTWithDAHMCCDiagnostic, mappings));

    final CohortDefinition patientsOnARTWithDAHMCCTreatment =
        this.txCurrCohortQueries.findPatientsinDAHAndMCCTreatment();

    final CohortIndicator patientsOnARTWithDAHMCCTreatmentIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsOnARTWithDAHDiagnosticTB",
            EptsReportUtils.map(patientsOnARTWithDAHMCCTreatment, mappings));

    this.addColumns(
        "D1",
        "D1: TX_NEW CD4 Less than 200.",
        dsd,
        patientWithLessCD4Indicator,
        mappings,
        UNDER_TEN,
        TEN_TO_NINETEEN,
        ABOVE_TWENTY);

    final CohortDefinition patientInStage3Or4Definition =
        this.txNewCohortQueries.getTxNewStage3OR4CompositionCohort();

    final CohortIndicator patientInStage3or4Indicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientWithLessCD4Indicator",
            EptsReportUtils.map(patientInStage3Or4Definition, mappings));

    this.addColumns(
        "D2",
        "D2: TX_NEW Stage 3 OR Stage 4.",
        dsd,
        patientInStage3or4Indicator,
        mappings,
        UNDER_TEN,
        TEN_TO_NINETEEN,
        ABOVE_TWENTY);

    this.addColumns(
        "D3",
        "D3: TX_RTT CD4 Less than 200.",
        dsd,
        patientsWithCD4LessThan200IndicatorRTT,
        mappings,
        UNDER_TEN,
        TEN_TO_NINETEEN,
        ABOVE_TWENTY);

    this.addColumns(
        "D4",
        "D4: TX_RTT Stage 3 OR Stage 4.",
        dsd,
        patientsWithRTTStage3_4IndicatorRTT,
        mappings,
        UNDER_TEN,
        TEN_TO_NINETEEN,
        ABOVE_TWENTY);

    this.addColumns(
        "D5",
        "D5: TX_CURR CD4 Less than 200 AND TB LAM",
        dsd,
        patientsWithCD4LessThan200IndicatorTXCURR,
        mappings,
        UNDER_TEN,
        TEN_TO_NINETEEN,
        ABOVE_TWENTY);

    this.addColumns(
        "D6",
        "D6: TX_CURR CD4 Less than 200 AND TB GENEXPERT",
        dsd,
        patientsWithCD4LessThan200IndicatorTXCURRGenExpert,
        mappings,
        UNDER_TEN,
        TEN_TO_NINETEEN,
        ABOVE_TWENTY);

    this.addColumns(
        "D7",
        "D7: TX_CURR CD4 Less than 200 AND TB CRAG",
        dsd,
        patientsWithCD4LessThan200IndicatorTXCURRCrag,
        mappings,
        UNDER_TEN,
        TEN_TO_NINETEEN,
        ABOVE_TWENTY);

    this.addColumns(
        "D8",
        "D8: TX_CURR DAH Diagnostic TB",
        dsd,
        patientsOnARTWithDAHDiagnosticTBIndicator,
        mappings,
        UNDER_TEN,
        TEN_TO_NINETEEN,
        ABOVE_TWENTY);

    this.addColumns(
        "D9",
        "D9: TX_CURR  DAH Diagnostic TB and Treament",
        dsd,
        patientsOnARTWithDAHDiagnosticTBTreatmentIndicator,
        mappings,
        UNDER_TEN,
        TEN_TO_NINETEEN,
        ABOVE_TWENTY);

    this.addColumns(
        "D10",
        "D10: TX_CURR  DAH Diagnostic CRAG Positive",
        dsd,
        patientsOnARTWithDAHCragPositiveIndicator,
        mappings,
        UNDER_TEN,
        TEN_TO_NINETEEN,
        ABOVE_TWENTY);

    this.addColumns(
        "D11",
        "D11: TX_CURR  DAH Diagnostic MCC",
        dsd,
        patientsOnARTWithDAHMCCDiagnosticndicator,
        mappings,
        UNDER_TEN,
        TEN_TO_NINETEEN,
        ABOVE_TWENTY);

    this.addColumns(
        "D12",
        "D12: TX_CURR  DAH Diagnostic MCC And Treatment",
        dsd,
        patientsOnARTWithDAHMCCTreatmentIndicator,
        mappings,
        UNDER_TEN,
        TEN_TO_NINETEEN,
        ABOVE_TWENTY);
    return dsd;
  }

  private void addAgeDimensions(
      final CohortIndicatorDataSetDefinition definition, final AgeRange... ranges) {

    for (final AgeRange range : ranges) {
      definition.addDimension(
          range.getName(),
          EptsReportUtils.map(
              this.eptsCommonDimension.findPatientsByRangePFACT(range.getName(), range),
              "endDate=${endDate}"));
    }
  }

  private void addColumns(
      final String name,
      String label,
      final CohortIndicatorDataSetDefinition definition,
      final CohortIndicator cohortIndicator,
      String mappings,
      final AgeRange... ranges) {

    for (final AgeRange range : ranges) {

      String columnName = getColumnNameByRange(name, range);

      definition.addColumn(
          columnName,
          name + " - (" + range.getName() + ")",
          EptsReportUtils.map(cohortIndicator, mappings),
          range.getName() + "=" + range.getName());
    }
  }

  private String getColumnNameByRange(String columnNamePrefix, AgeRange range) {
    StringBuilder sb = new StringBuilder(columnNamePrefix);
    sb.append("-");
    sb.append(range.getName());
    return sb.toString();
  }

  @Override
  public List<Parameter> getParameters() {
    List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.add(ReportingConstants.START_DATE_PARAMETER);
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }
}
