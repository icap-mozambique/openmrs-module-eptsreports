package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.ExpectedChildrenListDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Abdul Sacur */
@Component
public class SetupExpectedChildrenListReport extends EptsDataExportManager {

  @Autowired private ExpectedChildrenListDataset expectedPatientsListDataset;

  @Override
  public String getUuid() {
    return "ffeff52b-331a-4a0d-9346-4570e6b2d2ab";
  }

  @Override
  public String getName() {
    return "LISTA DE CRIANÇAS ESPERADAS PARA CONSULTA - v1.0.0 ";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of expected childrens patients for pickUp drugs or followup consultations";
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
    return "a19c44cc-18be-4e28-a93e-e95528797585";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "Expected_Children_Report.xls",
              "Lista de Crianças Esperadas para Consulta",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:7,dataset:EPL");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
