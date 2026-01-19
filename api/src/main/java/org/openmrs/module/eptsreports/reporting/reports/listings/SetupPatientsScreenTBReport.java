package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PatientsScreenedToTBDataSet;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Abdul Sacur */
@Component
public class SetupPatientsScreenTBReport extends EptsDataExportManager {

  @Autowired private PatientsScreenedToTBDataSet tbScreen;

  @Override
  public String getUuid() {
    return "b22757f4-8354-11ec-a8a3-0242ac120002";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES COM RASTREIO POSITIVO PARA TB - V1.0.0";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of patients screened for TB";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.tbScreen.getParameters());
    reportDefinition.addDataSetDefinition(
        "TB1",
        Mapped.mapStraightThrough(
            this.tbScreen.constructPatientsScreenedToTB(this.tbScreen.getParameters())));

    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "b2275ae2-8354-11ec-a8a3-0242ac120002";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "ListaRastreioTBPositivoICAP.xls",
              "Lista de Pacientes com Rastreio Positivo para TB",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:7,dataset:TB1");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
