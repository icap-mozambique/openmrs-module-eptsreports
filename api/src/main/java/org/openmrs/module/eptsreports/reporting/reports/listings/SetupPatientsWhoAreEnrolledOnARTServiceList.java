/** */
package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PatientsWhoAreEnrolledOnARTServiceDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class SetupPatientsWhoAreEnrolledOnARTServiceList extends EptsDataExportManager {

  @Autowired
  private PatientsWhoAreEnrolledOnARTServiceDataset patientsWhoAreEnrolledIOnARTServiceDataset;

  @Override
  public String getUuid() {
    return "4730a8c8-a14e-4110-a4f6-bc2f16d761b5";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES INSCRITOS NO SERVIÇO TARV - v1.9.0";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of patients who are enrolled on ART Service";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();

    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.patientsWhoAreEnrolledIOnARTServiceDataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "EAS",
        Mapped.mapStraightThrough(
            this.patientsWhoAreEnrolledIOnARTServiceDataset
                .constructPatientsWhoAreEnrolledOnARTServiceDataset(
                    this.patientsWhoAreEnrolledIOnARTServiceDataset.getParameters())));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "PATIENTS_WHO_ARE_ENROLLED_ON_ART_SERVICE_LIST_REPORT.xls",
              "LISTA DE PACIENTES INSCRITOS NO SERVIÇO TARV",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:4,dataset:EAS");
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
    return "6445d17e-b04f-4db0-addb-9325f2135c33";
  }
}
