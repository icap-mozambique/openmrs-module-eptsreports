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
import org.openmrs.module.eptsreports.reporting.library.datasets.TxCurrRegimesDataset;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.RegeminType;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupRegimesTXCURR extends EptsDataExportManager {

  @Autowired private TxCurrRegimesDataset txCurrDataset;

  @Autowired protected GenericCohortQueries genericCohortQueries;

  private ReportDefinition reportDefinition = new ReportDefinition();

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getUuid() {
    return "18bb2e05-aeea-45f5-b149-4f22b830dfc3";
  }

  @Override
  public String getExcelDesignUuid() {
    return "703ae8c3-b5c8-4047-9337-1dce2c09927a";
  }

  @Override
  public String getName() {
    return "TX_CURR 2.6.1 Regimes";
  }

  @Override
  public String getDescription() {
    return "TX_CURR 2.6.1 Regimes Report";
  }

  @Override
  public ReportDefinition constructReportDefinition() {

    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.addParameter(new Parameter("startDate", "Data Inicial", Date.class));
    reportDefinition.addParameter(new Parameter("endDate", "Data Final", Date.class));
    reportDefinition.addParameter(new Parameter("location", "Location", Location.class));

    reportDefinition.addDataSetDefinition(
        "A", Mapped.mapStraightThrough(this.txCurrDataset.constructTxCurrDataset()));

    txCurrRegeminsType("C", RegeminType.TDF_3TC_DTG);
    txCurrRegeminsType("N", RegeminType.ABC_3TC_LPV_r);
    txCurrRegeminsType("O", RegeminType.OTHERS);
    txCurrRegeminsType("B", RegeminType.ABC_3TC_DTG);
    txCurrRegeminsType("Z", RegeminType.AZT_3TC_LPV_r);

    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
            "endDate=${endDate},location=${location}"));

    return reportDefinition;
  }

  private void txCurrRegeminsType(String prefix, RegeminType type) {
    this.txCurrDataset.setPrefix(prefix);
    reportDefinition.addDataSetDefinition(
        prefix, Mapped.mapStraightThrough(this.txCurrDataset.constructTxCurrDataset(true, type)));
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "TX_CURR_2.6.1_Regimes_Desagregados.xls",
              "TX_CURR 2.6.1 Regimes Report",
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
