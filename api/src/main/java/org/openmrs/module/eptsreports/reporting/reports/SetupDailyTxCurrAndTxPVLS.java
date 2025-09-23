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
import org.openmrs.module.eptsreports.reporting.library.datasets.TxCurrDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxPvlsDataset;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Deprecated
@Component
public class SetupDailyTxCurrAndTxPVLS extends EptsDataExportManager {

  @Autowired private TxCurrDataset txCurrDataset;

  @Autowired private TxPvlsDataset txPvlsDataset;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getUuid() {
    return "381077e6-ceb8-4288-863e-818499515a30";
  }

  @Override
  public String getExcelDesignUuid() {
    return "8a75c3be-995a-4448-a259-4d57d991b614";
  }

  @Override
  public String getName() {
    return "TX_CURR E TX_PVLS DIARIO - v1.11.0";
  }

  @Override
  public String getDescription() {
    return "Patients on TX_CURR and TX_PVLS";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.txCurrDataset.getParameters());

    reportDefinition.addDataSetDefinition(
        "C", Mapped.mapStraightThrough(this.txCurrDataset.constructTxCurrDataset(true)));

    reportDefinition.addDataSetDefinition(
        "P", Mapped.mapStraightThrough(this.txPvlsDataset.constructTxPvlsDatset()));

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
              reportDefinition,
              "TX_CURR_AND_TX_PVLS.xls",
              "TX_CURR and TX_PVLS Daily Report",
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
