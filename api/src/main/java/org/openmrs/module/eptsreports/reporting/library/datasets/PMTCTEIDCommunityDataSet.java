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

import org.openmrs.module.eptsreports.reporting.library.cohorts.PMTCTEIDCommunityCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.PMTCTAgeDimensions;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PMTCTEIDCommunityDataSet extends BaseDataSet {

  @Autowired private PMTCTEIDCommunityCohortQueries pmtcteidCohortQueries;

  @Autowired private PMTCTAgeDimensions pmtctAgeDimensions;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired
  public DataSetDefinition constructPMTCTEIDDataset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("PMTCTEID Data Set");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortIndicator eidNumerator =
        this.eptsGeneralIndicator.getIndicator(
            "eidNumerator",
            EptsReportUtils.map(
                this.pmtcteidCohortQueries.getNumberOfInfantsWhoHadVirologicHIVTest(), mappings));

    final CohortIndicator eidFirsTest =
        this.eptsGeneralIndicator.getIndicator(
            "eidFirsTest",
            EptsReportUtils.map(
                this.pmtcteidCohortQueries.getNumberOfInfantsWhoHadVirologicHIVTestWithFistTest(),
                mappings));

    final CohortIndicator eidSecondTest =
        this.eptsGeneralIndicator.getIndicator(
            "eidSecondTest",
            EptsReportUtils.map(
                this.pmtcteidCohortQueries.getNumberOfInfantsWhoHadVirologicHIVTestWithSecondTest(),
                mappings));

    dataSetDefinition.addDimension(
        "pmtctage",
        EptsReportUtils.map(this.pmtctAgeDimensions.getPMTCTEIDAgeDimensions(), mappings));

    dataSetDefinition.addColumn(
        "PMTCT-EID",
        "PMTCT_EID: Number of infants who had a virologic HIV test (sample collected) by 12 months of age during the reporting period",
        EptsReportUtils.map(eidNumerator, mappings),
        "");

    dataSetDefinition.addColumn(
        "EID-F-LESS2MONTHS",
        "Infant Test by Age at Sample Collection - First Test (< 2 months)",
        EptsReportUtils.map(eidFirsTest, mappings),
        "pmtctage=less2months");

    dataSetDefinition.addColumn(
        "EID-F-GREATER2MONTHS",
        "Infant Test by Age at Sample Collection - First Test ( 2-12 months)",
        EptsReportUtils.map(eidFirsTest, mappings),
        "pmtctage=betweent2and12months");

    dataSetDefinition.addColumn(
        "EID-S-LESS2MONTHS",
        "Infant Test by Age at Sample Collection - Second Test or more (< 2 months)",
        EptsReportUtils.map(eidSecondTest, mappings),
        "pmtctage=less2months");

    dataSetDefinition.addColumn(
        "EID-S-GREATER2MONTHS",
        "Infant Test by Age at Sample Collection - Second Test or more ( 2-12 months)",
        EptsReportUtils.map(eidSecondTest, mappings),
        "pmtctage=betweent2and12months");

    return dataSetDefinition;
  }
}
