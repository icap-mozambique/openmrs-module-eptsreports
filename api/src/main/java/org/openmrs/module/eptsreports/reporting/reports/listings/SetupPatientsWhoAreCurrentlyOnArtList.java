/** */
package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PatientsWhoAreCurrentlyOnARTDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class SetupPatientsWhoAreCurrentlyOnArtList extends EptsDataExportManager {

  @Autowired private PatientsWhoAreCurrentlyOnARTDataset patientsWhoAreCurrentlyOnARTDataset;

  @Override
  public String getUuid() {
    return "352bf377-b29e-404a-8fe3-684b5fa941fe";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES ACTUALMENTE EM TARV - 1.9.0";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of patients currently on ART";
  }

  @Override
  public ReportDefinition constructReportDefinition() {

    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());

    reportDefinition.setParameters(this.patientsWhoAreCurrentlyOnARTDataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "PWCA",
        Mapped.mapStraightThrough(
            this.patientsWhoAreCurrentlyOnARTDataset.constructPatientsWhoAreCurrentlyOnARTDataset(
                this.patientsWhoAreCurrentlyOnARTDataset.getParameters())));
    return reportDefinition;
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
              "PATIENTS_WHO_ARE_CURRENTLY_ON_ART.xls",
              "LISTA DE PACIENTES ACTUALMENTE EM TARV",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:6,dataset:PWCA");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }

  @Override
  public String getExcelDesignUuid() {
    return "24679856-12d0-4bd5-8fc7-c9e2861c75b5";
  }
}
