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
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxCurrAPEs;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupAPEsTXCURR extends EptsDataExportManager {

  @Autowired private TxCurrAPEs txCurrDataset;

  @Autowired protected GenericCohortQueries genericCohortQueries;

  private ReportDefinition reportDefinition = new ReportDefinition();

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getUuid() {
    return "e46536c2-7833-11ec-90d6-0242ac120003";
  }

  @Override
  public String getExcelDesignUuid() {
    return "e4653924-7833-11ec-90d6-0242ac120003";
  }

  @Override
  public String getName() {
    return "RELATORIO DE DESEMPENHO APEs";
  }

  @Override
  public String getDescription() {
    return "RELATORIO DE DESEMPENHO APEs";
  }

  @Override
  public ReportDefinition constructReportDefinition() {

    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.addParameter(new Parameter("startDate", "Data Inicial", Date.class));
    reportDefinition.addParameter(new Parameter("endDate", "Data Final", Date.class));
    reportDefinition.addParameter(new Parameter("location", "Location", Location.class));

    // ALL
    txCurrAPEs("C", "C");

    // Carga-Viral
    txCurrAPEs("V", "V");

    // abaixo de mil copias
    txCurrAPEs("T", "T");

    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    return reportDefinition;
  }

  private void txCurrAPEs(String prefix, String desagregacao) {
    this.txCurrDataset.setPrefix(prefix);
    reportDefinition.addDataSetDefinition(
        prefix,
        Mapped.mapStraightThrough(this.txCurrDataset.constructTxCurrDataset(true, desagregacao)));
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "APEs_REPORT.xls",
              "APEs TX_CURR Report",
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
