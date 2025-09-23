/** */
package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PatientsOnARTwithViralLoadsDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class SetupPatientsOnARTwithViralLoadList extends EptsDataExportManager {

  @Autowired private PatientsOnARTwithViralLoadsDataset patientsOnARTwithViralLoadsDataset;

  @Override
  public String getUuid() {
    return "72c400f9-9ffd-4c8d-a2d0-80ae4d290548";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES ACTIVOS COM EXAMES DE CARGA VIRAL PRIMEIRA E ULTIMA CV - AQD MISAU";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of patients who are on ART and have Viral Loads";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();

    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.patientsOnARTwithViralLoadsDataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "AQDM",
        Mapped.mapStraightThrough(
            this.patientsOnARTwithViralLoadsDataset.constructPatientsOnARTwithViralLoadsDataset(
                this.patientsOnARTwithViralLoadsDataset.getParameters())));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "LIST_OF_PATIENTS_WITH_VL_AQD_MISAU.xls",
              "LISTA DE PACIENTES ACTIVOS COM EXAMES DE CARGA VIRAL PRIMEIRA E ULTIMA CV - AQD MISAU",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:7,dataset:AQDM");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "826b07f4-5692-4ed6-ac0e-368e361eee07";
  }
}
