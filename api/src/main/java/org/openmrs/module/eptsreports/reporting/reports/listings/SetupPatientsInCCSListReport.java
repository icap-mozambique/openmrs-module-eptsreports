package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PatientsInCCSDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class SetupPatientsInCCSListReport extends EptsDataExportManager {

  @Autowired private PatientsInCCSDataset patientsInCCSDataset;

  @Override
  public String getUuid() {
    return "d0037f3b-8b56-45fb-ab4d-5935211bb264";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES RASTREADOS PARA CCU - v1.11.0 ";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of patients in CCS";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.patientsInCCSDataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "CCU",
        Mapped.mapStraightThrough(
            this.patientsInCCSDataset.constructPatientsInCcsDataset(
                this.patientsInCCSDataset.getParameters())));

    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "6553c409-75b0-4444-a753-17cfc41d3f6b";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "LIST_OF_PATIENTS_IN_CCS_REPORT.xls",
              "Lista de Pacientes Rastreados para CCU",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:11,dataset:CCU");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
