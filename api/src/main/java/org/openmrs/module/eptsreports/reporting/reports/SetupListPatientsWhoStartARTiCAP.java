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
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.txnew.ListOfPatientWhoStartArtICAPDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.txnew.SummaryPatientWhoStartArtDataSet;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupListPatientsWhoStartARTiCAP extends EptsDataExportManager {

  @Autowired private ListOfPatientWhoStartArtICAPDataSet txNew;
  @Autowired SummaryPatientWhoStartArtDataSet summaryPatientWhoStartArtDataSet;
  @Autowired private DatimCodeDataSet datimCodeDataSet;

  @Override
  public String getExcelDesignUuid() {
    return "24e62264-b893-4503-9f4d-bf99cfc93d0b";
  }

  @Override
  public String getUuid() {
    return "5bf0c84a-4d53-4234-881b-1d5d51a1df62";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES NA COORTE DE TARV - ICAP";
  }

  @Override
  public String getDescription() {
    return "LISTA DE PACIENTES NA COORTE DE TARV - ICAP";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition rd = new ReportDefinition();
    rd.setUuid(this.getUuid());
    rd.setName(this.getName());
    rd.setDescription(this.getDescription());
    rd.addParameters(this.getParameters());

    rd.addDataSetDefinition(
        "NR", Mapped.mapStraightThrough(this.txNew.constructDataset(this.getParameters())));

    rd.addDataSetDefinition(
        "RT",
        Mapped.mapStraightThrough(this.summaryPatientWhoStartArtDataSet.getTotaStartARTDataset()));

    rd.addDataSetDefinition(
        "D",
        Mapped.mapStraightThrough(this.datimCodeDataSet.constructDataset(this.getParameters())));

    return rd;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "LISTA_INICIO_DE_TARV_ICAP.xls",
              "LISTA_DE_PACIENTES_NA_COORTE_DE_TARV",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:7,dataset:NR");
      props.put("sortWeight", "5000");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("startDate", "Cohort Start Date", Date.class),
        new Parameter("endDate", "  Cohort End Date", Date.class),
        new Parameter("evaluationDate", "Evaluation Date", Date.class),
        new Parameter("location", "Unidade Sanitária", Location.class));
  }
}
