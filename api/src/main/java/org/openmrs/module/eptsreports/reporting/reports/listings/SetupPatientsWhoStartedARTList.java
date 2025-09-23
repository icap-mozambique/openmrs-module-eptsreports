/** */
package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PatientsWhoStartedARTDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class SetupPatientsWhoStartedARTList extends EptsDataExportManager {

  @Autowired private PatientsWhoStartedARTDataset patientsWhoStartedARTDataset;

  @Override
  public String getUuid() {
    return "ddce6fc7-8536-4d0d-8511-eb6be33976ca";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES QUE INICIARAM TARV - v1.9.0";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of patients who started ART";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();

    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.patientsWhoStartedARTDataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "ART",
        Mapped.mapStraightThrough(
            this.patientsWhoStartedARTDataset.constructPatientsWhoStartedARTDataset(
                this.patientsWhoStartedARTDataset.getParameters())));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "PATIENTS_WHO_STARTED_ART_LIST_REPORT.xls",
              "LISTA DE PACIENTES QUE INICIARAM TARV",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:7,dataset:ART");
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
    return "55b1dcbf-e093-4fa9-b738-4ad68573a65b";
  }
}
