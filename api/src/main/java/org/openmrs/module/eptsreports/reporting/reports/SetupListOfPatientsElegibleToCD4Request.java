package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.ListOfPatientElegibleForCD4RequestDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.SismaCodeDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupListOfPatientsElegibleToCD4Request extends EptsDataExportManager {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired
  private ListOfPatientElegibleForCD4RequestDataSet ListOfPatientElegibleForCD4RequestDataSet;

  @Autowired private DatimCodeDataSet datimCodeDataset;
  @Autowired private SismaCodeDataSet sismaCodeDataset;

  @Override
  public String getExcelDesignUuid() {
    return "d3e6423a-24a9-11ef-b120-8f05e4fc91b7";
  }

  @Override
  public String getUuid() {
    return "dc652124-24a9-11ef-863d-73e3cd1b6ab9";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "Lista de Utentes Elegíveis ao Pedido de CD4";
  }

  @Override
  public String getDescription() {
    return "Lista de Utentes Elegíveis ao Pedido de CD4";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.setParameters(this.getParameters());

    rd.addDataSetDefinition(
        "CD",
        Mapped.mapStraightThrough(
            ListOfPatientElegibleForCD4RequestDataSet.constructDataset(getParameters())));

    rd.addDataSetDefinition(
        "CDT",
        Mapped.mapStraightThrough(
            ListOfPatientElegibleForCD4RequestDataSet
                .getTotalPatientElegibleForCD4RequestDataSet()));

    rd.addDataSetDefinition(
        "D",
        Mapped.mapStraightThrough(this.datimCodeDataset.constructDataset(this.getParameters())));
    rd.addDataSetDefinition(
        "SC",
        Mapped.mapStraightThrough(this.sismaCodeDataset.constructDataset(this.getParameters())));

    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQueryWithoutPrep()),
            "endDate=${endDate},location=${location}"));
    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "Template_Lista_de_Utentes_Elegiveis_Pedido_CD4.xls",
              "Lista de Utentes Elegíveis ao Pedido de CD4",
              getExcelDesignUuid(),
              null);

      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:10,dataset:CD");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }

  @Override
  public List<Parameter> getParameters() {
    List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.add(ReportingConstants.START_DATE_PARAMETER);
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }
}
