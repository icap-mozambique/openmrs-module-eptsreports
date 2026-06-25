package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.ExpectedPatientsListDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class SetupExpectedPatientsListReport extends EptsDataExportManager {

  @Autowired private ExpectedPatientsListDataset expectedPatientsListDataset;

  @Override
  public String getUuid() {
    return "40a5b03f-7e35-4fc2-afc2-8fc510352d5a";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES ESPERADOS - v1.13.0 ";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of expected patients for pickUp drugs or followup consultations";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.expectedPatientsListDataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "EPL",
        Mapped.mapStraightThrough(
            this.expectedPatientsListDataset.constructExpectedPatientsListDataset(
                this.expectedPatientsListDataset.getParameters())));

    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "de920488-cd19-487d-b5d7-430044a2ade5";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "EXPECTED_PATIENTS_REPORT.xls",
              "Lista de Pacientes Esperados",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:11,dataset:EPL");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
