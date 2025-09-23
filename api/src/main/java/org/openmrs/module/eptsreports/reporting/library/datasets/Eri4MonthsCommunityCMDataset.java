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

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.Eri4MonthsCommunityCMCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.Eri4MonthsDimensions;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.library.queries.ErimType;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Eri4MonthsCommunityCMDataset extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private Eri4MonthsDimensions eri4MonthsDimensions;

  @Autowired private Eri4MonthsCommunityCMCohortQueries eri4MonthsCohortQueries;

  public DataSetDefinition constructEri4MonthsDataset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    final String cohortPeriodMappings =
        "cohortStartDate=${endDate-5m+1d},cohortEndDate=${endDate-4m},location=${location}";

    final String reportingPeriodMappings =
        "startDate=${startDate},endDate=${endDate},location=${location}";

    dataSetDefinition.setName("ERI-4months Data Set - CM");

    dataSetDefinition.addParameters(this.getParameters());

    dataSetDefinition.addDimension(
        "state",
        EptsReportUtils.map(this.eri4MonthsDimensions.getDimension(), cohortPeriodMappings));

    this.addColumns(
        dataSetDefinition,
        "01",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "All patients",
                EptsReportUtils.map(
                    this.eri4MonthsCohortQueries.getEri4MonthsTotalCompositionCohort(
                        "All Patients", ErimType.TOTAL),
                    reportingPeriodMappings)),
            reportingPeriodMappings),
        this.get4MonthsRetentionColumns());

    this.addColumns(
        dataSetDefinition,
        "02",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "All patients in treatment",
                EptsReportUtils.map(
                    this.eri4MonthsCohortQueries.getEri4MonthsTotalCompositionCohort(
                        "All Patients in treatment", ErimType.IN_TREATMENT),
                    reportingPeriodMappings)),
            reportingPeriodMappings),
        this.get4MonthsRetentionColumns());

    this.addColumns(
        dataSetDefinition,
        "03",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "all patients dead",
                EptsReportUtils.map(
                    this.eri4MonthsCohortQueries.getEri4MonthsTotalCompositionCohort(
                        "All Patients dead", ErimType.DEAD),
                    reportingPeriodMappings)),
            reportingPeriodMappings),
        this.get4MonthsRetentionColumns());

    this.addColumns(
        dataSetDefinition,
        "04",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "all patients lftu",
                EptsReportUtils.map(
                    this.eri4MonthsCohortQueries.getEri4MonthsTotalCompositionCohort(
                        "All Patients lftu", ErimType.LFTU),
                    reportingPeriodMappings)),
            reportingPeriodMappings),
        this.get4MonthsRetentionColumns());

    this.addColumns(
        dataSetDefinition,
        "05",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "all patients in tranferred out",
                EptsReportUtils.map(
                    this.eri4MonthsCohortQueries.getEri4MonthsTotalCompositionCohort(
                        "all patients in tranferred out", ErimType.TRANFERED_OUT),
                    reportingPeriodMappings)),
            reportingPeriodMappings),
        this.get4MonthsRetentionColumns());

    this.addColumns(
        dataSetDefinition,
        "06",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "all patients who stopped treatment",
                EptsReportUtils.map(
                    this.eri4MonthsCohortQueries.getEri4MonthsTotalCompositionCohort(
                        "all patients who stopped treatment", ErimType.SPTOPPED_TREATMENT),
                    reportingPeriodMappings)),
            reportingPeriodMappings),
        this.get4MonthsRetentionColumns());

    this.addColumns(
        dataSetDefinition,
        "07",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "all patients defaulters",
                EptsReportUtils.map(
                    this.eri4MonthsCohortQueries.getEri4MonthsTotalCompositionCohort(
                        "all patients defaulters", ErimType.DEFAULTER),
                    reportingPeriodMappings)),
            reportingPeriodMappings),
        this.get4MonthsRetentionColumns());

    return dataSetDefinition;
  }

  private List<ColumnParameters> get4MonthsRetentionColumns() {

    final ColumnParameters allPatients =
        new ColumnParameters("initiated ART", "Initiated ART", "", "I");
    final ColumnParameters pregnantWoman =
        new ColumnParameters("pregnant woman", "Pregnant Woman", "state=PREGNANT", "I");
    final ColumnParameters brestfedding =
        new ColumnParameters("breastfeeding", "Breastfeeding", "state=BREASTFEEDING", "I");
    final ColumnParameters children =
        new ColumnParameters("children", "Children", "state=CHILDREN", "I");
    final ColumnParameters adult = new ColumnParameters("adult", "Adult", "state=ADULT", "I");

    return Arrays.asList(allPatients, pregnantWoman, brestfedding, children, adult);
  }

  private void addColumns(
      final CohortIndicatorDataSetDefinition definition,
      final String columNumber,
      final Mapped<CohortIndicator> indicator,
      final List<ColumnParameters> columns) {

    int position = 1;

    for (final ColumnParameters column : columns) {

      final String name = column.getColumn() + "" + position + "-" + columNumber;
      final String label = column.getLabel() + "(" + name + ")";

      definition.addColumn(name, label, indicator, column.getDimensions());

      position++;
    }
  }
}
