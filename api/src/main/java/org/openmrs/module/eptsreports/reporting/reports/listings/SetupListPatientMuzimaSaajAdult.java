package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PatientsMuzimaVisitasBuscasSaajAdultoSmiDataset;
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
public class SetupListPatientMuzimaSaajAdult extends EptsDataExportManager {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired
  private PatientsMuzimaVisitasBuscasSaajAdultoSmiDataset
      listPatientVisitaBuscaSaajAdultoMuzimaDataset;

  @Override
  public String getExcelDesignUuid() {
    return "61d6ebe6-bb09-489e-a25f-968400cf5b09";
  }

  @Override
  public String getUuid() {
    return "fbe61848-2d09-470d-9700-0bce808e7de7";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "Lista De Pacientes para Visita e Busca Prevenção e Reintegração Semanal para SAAJ Adultos e SMI MUZIMA";
  }

  @Override
  public String getDescription() {
    return "Este relatório gera a lista de pacientes para visita e busca preventiva e  de reintegração semanal para SAAJ Adultos e SMI para o MUZIMA";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.setParameters(getParameters());
    rd.addDataSetDefinition(
        "LISTA1EPARES",
        Mapped.mapStraightThrough(
            this.listPatientVisitaBuscaSaajAdultoMuzimaDataset.loadDataAdultoPrevencao(
                getParameters())));

    rd.addDataSetDefinition(
        "LISTA2EPARES",
        Mapped.mapStraightThrough(
            this.listPatientVisitaBuscaSaajAdultoMuzimaDataset.loadDataAdultoReintegracao(
                getParameters())));

    rd.addDataSetDefinition(
        "LISTA1SAAJ",
        Mapped.mapStraightThrough(
            this.listPatientVisitaBuscaSaajAdultoMuzimaDataset.loadDataSaajPrevencao(
                getParameters())));

    rd.addDataSetDefinition(
        "LISTA2SAAJ",
        Mapped.mapStraightThrough(
            this.listPatientVisitaBuscaSaajAdultoMuzimaDataset.loadDataSaajReintegracao(
                getParameters())));

    rd.addDataSetDefinition(
        "LISTA1GRAVIDAS",
        Mapped.mapStraightThrough(
            this.listPatientVisitaBuscaSaajAdultoMuzimaDataset.loadDataSmiPrevencao(
                getParameters())));

    rd.addDataSetDefinition(
        "LISTA2GRAVIDAS",
        Mapped.mapStraightThrough(
            this.listPatientVisitaBuscaSaajAdultoMuzimaDataset.loadDataSmiReintegracao(
                this.getParameters())));

    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
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
              "LISTA_PACIENTES_VISITA_BUSCA_SEMANAL_ADULTO_SAAJ_SMI_MUZIMA.xls",
              "Lista De Pacientes para Visita e Busca Semanal para SAAJ Adultos e SMI MUZIMA",
              getExcelDesignUuid(),
              null);

      Properties props = new Properties();
      props.put(
          "repeatingSections",
          "sheet:1,row:4,dataset:LISTA1EPARES | sheet:2,row:4,dataset:LISTA2EPARES | sheet:3,row:4,dataset:LISTA1SAAJ | sheet:4,row:4,dataset:LISTA2SAAJ | sheet:5,row:4,dataset:LISTA1GRAVIDAS | sheet:6,row:4,dataset:LISTA2GRAVIDAS");
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
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }
}
