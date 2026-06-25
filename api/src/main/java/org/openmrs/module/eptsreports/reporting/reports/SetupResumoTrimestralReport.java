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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.ResumoTrimestralUtil.QUARTERLIES;
import org.openmrs.module.eptsreports.reporting.library.datasets.LocationDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.resumo.ResumoTrimestralDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupResumoTrimestralReport extends EptsDataExportManager {

  public static final String YEAR_PARAMETER = "year";

  public static final String QUARTER_PARAMETER = "quarter";

  private final ResumoTrimestralDataSetDefinition resumoTrimestralDataSetDefinition;

  @Autowired
  public SetupResumoTrimestralReport(
      final ResumoTrimestralDataSetDefinition resumoTrimestralDataSetDefinition) {
    this.resumoTrimestralDataSetDefinition = resumoTrimestralDataSetDefinition;
  }

  @Override
  public String getExcelDesignUuid() {
    return "5c04a7b8-371d-445c-b075-5358dc07ef7f";
  }

  @Override
  public String getUuid() {
    return "0c64c016-6b09-4ac4-bb6d-bdacd77988ea";
  }

  @Override
  public String getName() {
    return "Resumo Trimestral das Coortes de Tratamento Antirretroviral";
  }

  @Override
  public String getDescription() {
    return "Resumo Trimestral das Coortes de Tratamento Antirretroviral";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition rd = new ReportDefinition();
    rd.setUuid(this.getUuid());
    rd.setName(this.getName());
    rd.setDescription(this.getDescription());
    rd.addParameters(SetupResumoTrimestralReport.getDataParameters());
    rd.addDataSetDefinition("HF", Mapped.mapStraightThrough(new LocationDataSetDefinition()));
    rd.addDataSetDefinition(
        "T",
        Mapped.mapStraightThrough(
            this.resumoTrimestralDataSetDefinition.constructResumoTrimestralDataset()));

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
              "Resumo_Trimestral.xls",
              "Resumo Trimestral",
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

  public static List<Parameter> getDataParameters() {
    final List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.addAll(SetupResumoTrimestralReport.getCustomParameteres());
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }

  public static List<Parameter> getCustomParameteres() {
    return Arrays.asList(
        SetupResumoTrimestralReport.getYearConfigurableParameter(),
        SetupResumoTrimestralReport.getQuarterConfigurableParameter());
  }

  private static Parameter getYearConfigurableParameter() {
    final Parameter parameter = new Parameter();
    parameter.setName(SetupResumoTrimestralReport.YEAR_PARAMETER);
    parameter.setLabel("Ano");
    parameter.setType(String.class);
    parameter.setCollectionType(List.class);
    parameter.setRequired(Boolean.TRUE);

    final Properties props = new Properties();
    final Calendar currentDate = Calendar.getInstance();
    final int currentYear = currentDate.get(Calendar.YEAR);

    String codedOptions = "";
    for (int i = 0; i < 5; i++) {
      final int year = currentYear - i;
      if (i == 0) {
        codedOptions += year;

      } else {
        codedOptions += "," + year;
      }
    }

    props.put("codedOptions", codedOptions);
    parameter.setWidgetConfiguration(props);
    parameter.setDefaultValue(Arrays.asList(currentYear));
    return parameter;
  }

  private static Parameter getQuarterConfigurableParameter() {
    final Parameter parameter = new Parameter();
    parameter.setName(SetupResumoTrimestralReport.QUARTER_PARAMETER);
    parameter.setLabel("Trimestre");
    parameter.setType(String.class);
    parameter.setCollectionType(List.class);
    parameter.setRequired(Boolean.TRUE);

    final Properties props = new Properties();
    props.put(
        "codedOptions",
        QUARTERLIES.QUARTER_ONE.getDescription()
            + ","
            + QUARTERLIES.QUARTER_TWO.getDescription()
            + ","
            + QUARTERLIES.QUARTER_THREE.getDescription()
            + ","
            + QUARTERLIES.QUARTER_FOUR.getDescription());
    parameter.setWidgetConfiguration(props);
    return parameter;
  }
}
