/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
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

import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.Gender;
import org.openmrs.module.eptsreports.reporting.utils.RegeminType;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TxCurrRegimesDataset extends BaseDataSet {

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  private String prefix;

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getPrefix() {
    return prefix;
  }

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  // TX CURR Data Set
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

    dataSetDefinition.addColumn(
        "CURRAll", "TX_CURR: Currently on ART", EptsReportUtils.map(txCurrIndicator, mappings), "");

    return dataSetDefinition;
  }

  // TX REGEMINS Data Set
  public CohortIndicatorDataSetDefinition constructTxCurrDataset(
      final boolean currentSpec, RegeminType type) {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("TX_CURR Regemins Data Set");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition txCurrCompositionCohort =
        this.txCurrCohortQueries.findRegeminsOnPatientsWhoAreActiveOnART(type);

    final CohortIndicator txCurrIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "findPatientsWhoAreActiveOnART",
            EptsReportUtils.map(txCurrCompositionCohort, mappings));

    final CohortDefinition txCurrCompositionCohortOthers =
        this.txCurrCohortQueries.findRegeminsOnPatientsWhoAreActiveOnARTOthers(type);

    final CohortIndicator txCurrIndicatorOthers =
        this.eptsGeneralIndicator.getIndicator(
            "findPatientsWhoAreActiveOnART",
            EptsReportUtils.map(txCurrCompositionCohortOthers, mappings));

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

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
        ABOVE_FIFTY);

    if (type.equals(RegeminType.TDF_3TC_DTG)) {
      dataSetDefinition.addColumn(
          "C1All",
          "TX_CURR: TDF_3TC_DTG Regime",
          EptsReportUtils.map(txCurrIndicator, mappings),
          "");
    }

    if (type.equals(RegeminType.ABC_3TC_LPV_r)) {
      dataSetDefinition.addColumn(
          "N1All",
          "TX_CURR: ABC_3TC_LPV_r Regime",
          EptsReportUtils.map(txCurrIndicator, mappings),
          "");
    }

    if (type.equals(RegeminType.ABC_3TC_DTG)) {
      dataSetDefinition.addColumn(
          "B1All",
          "TX_CURR: ABC_3TC_DTG Regime",
          EptsReportUtils.map(txCurrIndicator, mappings),
          "");
    }

    if (type.equals(RegeminType.AZT_3TC_LPV_r)) {
      dataSetDefinition.addColumn(
          "Z1All",
          "TX_CURR: AZT_3TC_LPV_r Regime",
          EptsReportUtils.map(txCurrIndicator, mappings),
          "");
    }

    if (type.equals(RegeminType.OTHERS)) {
      dataSetDefinition.addColumn(
          "O1All",
          "TX_CURR: Outros Regimes",
          EptsReportUtils.map(txCurrIndicatorOthers, mappings),
          "");

      this.addColums(
          dataSetDefinition,
          mappings,
          txCurrIndicatorOthers,
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
    }

    if (!type.equals(RegeminType.OTHERS)) {
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
          ABOVE_FIFTY);
    }

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
              this.eptsCommonDimension.findPatientsByGenderAndRange(
                  this.getColumnName(range, Gender.MALE), range, Gender.MALE),
              mappings));

      cohortIndicatorDataSetDefinition.addDimension(
          this.getColumnName(range, Gender.FEMALE),
          EptsReportUtils.map(
              this.eptsCommonDimension.findPatientsByGenderAndRange(
                  this.getColumnName(range, Gender.FEMALE), range, Gender.FEMALE),
              mappings));
    }
  }

  private String getColumnName(final AgeRange range, final Gender gender) {

    return range.getDesagregationColumnName(prefix, gender);
  }
}
