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
package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.CxCaDataset;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.CxCaQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupCXCAReport extends EptsDataExportManager {

  @Autowired private CxCaDataset cxCaDataset;

  @Autowired protected GenericCohortQueries genericCohortQueries;

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getUuid() {
    return "1b425145-52f8-487a-bf6a-24cea5bcc4e2";
  }

  @Override
  public String getExcelDesignUuid() {
    return "472fbae0-7303-43a3-900b-e68dcb351bf8";
  }

  @Override
  public String getName() {
    return "CXCA REPORT - v1.11.0";
  }

  @Override
  public String getDescription() {
    return "CXCA Report";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();

    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.cxCaDataset.getParameters());

    reportDefinition.addDataSetDefinition(
        "N",
        Mapped.mapStraightThrough(
            this.cxCaDataset.constructCxCaDataset(
                String.format(
                    CxCaQueries.findPatientsWithCervicalCencerScreen, CxCaQueries.negative),
                "N")));

    reportDefinition.addDataSetDefinition(
        "P",
        Mapped.mapStraightThrough(
            this.cxCaDataset.constructCxCaDataset(
                String.format(
                    CxCaQueries.findPatientsWithCervicalCencerScreen, CxCaQueries.positive),
                "P")));

    reportDefinition.addDataSetDefinition(
        "S",
        Mapped.mapStraightThrough(
            this.cxCaDataset.constructCxCaDataset(
                String.format(
                    CxCaQueries.findPatientsWithCervicalCencerScreen, CxCaQueries.suspectedCancer),
                "S")));

    reportDefinition.addDataSetDefinition(
        "C",
        Mapped.mapStraightThrough(
            this.cxCaDataset.constructCxCaDataset(
                CxCaQueries.findPatientsWithCryothherapyCervicalCencerScreen, "C")));

    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
            "endDate=${endDate},location=${location}"));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition, "CXCA_REPORT.xls", "CXCA REPORT", this.getExcelDesignUuid(), null);
      final Properties props = new Properties();
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
