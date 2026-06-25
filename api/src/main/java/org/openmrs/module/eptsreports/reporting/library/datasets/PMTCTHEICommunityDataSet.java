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

import org.openmrs.module.eptsreports.reporting.library.cohorts.PMTCTHEICommunityCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.PMTCTAgeDimensions;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PMTCTHEICommunityDataSet extends BaseDataSet {

  @Autowired private PMTCTHEICommunityCohortQueries pmtctheiCohortQueries;

  @Autowired private PMTCTAgeDimensions pmtctAgeDimensions;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired
  public DataSetDefinition constructPMTCTHEIDataset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("PMTCTEID Data Set");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortIndicator heiNumerator =
        this.eptsGeneralIndicator.getIndicator(
            "heiNumerator",
            EptsReportUtils.map(
                this.pmtctheiCohortQueries.getNumberOfInfantsWhoHadVirologicHIVTestResults(),
                mappings));

    final CohortIndicator positiveTestResults =
        this.eptsGeneralIndicator.getIndicator(
            "positiveTestResults",
            EptsReportUtils.map(
                this.pmtctheiCohortQueries
                    .getNumberOfInfantsWhoHadVirologicHIVTestWithPositiveTestResults(),
                mappings));

    final CohortIndicator negativeTestResults =
        this.eptsGeneralIndicator.getIndicator(
            "negativeTestResults",
            EptsReportUtils.map(
                this.pmtctheiCohortQueries
                    .getNumberOfInfantsWhoHadVirologicHIVTestWithNegativeTestResults(),
                mappings));

    final CohortIndicator positiveResultsAndART =
        this.eptsGeneralIndicator.getIndicator(
            "positiveResultsAndART",
            EptsReportUtils.map(
                this.pmtctheiCohortQueries
                    .getNumberOfInfantsWhoHadVirologicHIVTestWithPositiveTestResultsWhoInitatedARTTreatment(),
                mappings));

    dataSetDefinition.addDimension(
        "pmtctage-hei",
        EptsReportUtils.map(this.pmtctAgeDimensions.getPMTCTHEIAgeDimensions(), mappings));

    dataSetDefinition.addColumn(
        "PMTCT-HEI",
        "PMTCT_EID: Number of infants who had a virologic HIV test (sample collected) by 12 months of age during the reporting period",
        EptsReportUtils.map(heiNumerator, mappings),
        "");

    dataSetDefinition.addColumn(
        "HEI-P-LESS2MONTHS",
        "Infant age at virologic sample collection and result returned - Positive (< 2 months)",
        EptsReportUtils.map(positiveTestResults, mappings),
        "pmtctage-hei=less2months");

    dataSetDefinition.addColumn(
        "HEI-P-GREATER2MONTHS",
        "Infant age at virologic sample collection and result returned - Positive (2-12 months)",
        EptsReportUtils.map(positiveTestResults, mappings),
        "pmtctage-hei=betweent2and12months");

    dataSetDefinition.addColumn(
        "HEI-N-LESS2MONTHS",
        "Infant age at virologic sample collection and result returned - Negative (< 2 months)",
        EptsReportUtils.map(negativeTestResults, mappings),
        "pmtctage-hei=less2months");

    dataSetDefinition.addColumn(
        "HEI-N-GREATER2MONTHS",
        "Infant age at virologic sample collection and result returned - Negative (2-12 months)",
        EptsReportUtils.map(negativeTestResults, mappings),
        "pmtctage-hei=betweent2and12months");

    dataSetDefinition.addColumn(
        "HEI-ART-LESS2MONTHS",
        "Result returned, Positive, confirmed initiated ART by age at virologic sample collection (< 2 months)",
        EptsReportUtils.map(positiveResultsAndART, mappings),
        "pmtctage-hei=less2months");

    dataSetDefinition.addColumn(
        "HEI-ART-GREATER2MONTHS",
        "Result returned, Positive, confirmed initiated ART by age at virologic sample collection (2-12 months)",
        EptsReportUtils.map(positiveResultsAndART, mappings),
        "pmtctage-hei=betweent2and12months");

    return dataSetDefinition;
  }
}
