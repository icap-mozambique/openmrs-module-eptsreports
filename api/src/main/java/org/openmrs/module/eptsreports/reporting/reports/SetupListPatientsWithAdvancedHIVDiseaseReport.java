package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.ListOfPatientsWithDAHDataSet;
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
public class SetupListPatientsWithAdvancedHIVDiseaseReport extends EptsDataExportManager {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private ListOfPatientsWithDAHDataSet listOfPatientsWithDAHDataSet;

  @Autowired private DatimCodeDataSet datimCodeDataset;
  @Autowired private SismaCodeDataSet sismaCodeDataset;

  @Override
  public String getExcelDesignUuid() {
    return "37529b40-5585-4496-9192-6717f037d294";
  }

  @Override
  public String getUuid() {
    return "35b22b9f-9f32-4447-9dcf-b13eae7fc681";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "Lista de Utentes em Doença Avançada por HIV (DAH)";
  }

  @Override
  public String getDescription() {
    return "Este relatório gera o número agregado e a lista de utentes com Doença Avançada por HIV e utentes com critérios de Doença Avançada por HIV durante o período de avaliação.";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.setParameters(this.getParameters());
    rd.addDataSetDefinition(
        "DAH",
        Mapped.mapStraightThrough(listOfPatientsWithDAHDataSet.constructDataset(getParameters())));

    rd.addDataSetDefinition(
        "DAHTOTAL",
        Mapped.mapStraightThrough(
            this.listOfPatientsWithDAHDataSet.getTotalOfPatietsWithDAHDataset()));

    rd.addDataSetDefinition(
        "MDSDAHTOTAL",
        Mapped.mapStraightThrough(
            this.listOfPatientsWithDAHDataSet.getTotalOfPatietsiNmdsOfDAHDataset()));

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
              "Lista_Utentes_DAH.xls",
              "Lista de Utentes em Doença Avançada por HIV",
              getExcelDesignUuid(),
              null);

      Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:10,dataset:DAH");
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
