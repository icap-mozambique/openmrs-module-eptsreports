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
import org.openmrs.module.eptsreports.reporting.library.datasets.PrepReportDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsPeriodIndicatorDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupPREPReport extends EptsPeriodIndicatorDataExportManager {

  @Autowired private PrepReportDataset prepReportDataset;

  @Autowired protected GenericCohortQueries genericCohortQueries;

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getUuid() {
    return "3e9ba8b4-d089-4005-8fef-6d01e41a2aef";
  }

  @Override
  public String getExcelDesignUuid() {
    return "9062cd94-b2ac-4a93-a318-2dfe3a58ecd3";
  }

  @Override
  public String getName() {
    return "PREP CDC REPORT";
  }

  @Override
  public String getDescription() {
    return "PREP CDC REPORT";
  }

  @Override
  public PeriodIndicatorReportDefinition constructReportDefinition() {

    final PeriodIndicatorReportDefinition reportDefinition =
        SetupResumoMensalReport.getDefaultPeriodIndicatorReportDefinition();

    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.prepReportDataset.getParameters());

    reportDefinition.addDataSetDefinition(
        "PREP", Mapped.mapStraightThrough(this.prepReportDataset.getPrepReportDataSetDefinition()));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "PREP_CDC_REPORT.xls",
              "PREP CDC REPORT",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
