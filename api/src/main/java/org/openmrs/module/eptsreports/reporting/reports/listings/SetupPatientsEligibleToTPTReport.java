package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PatientsEligibleToTPTDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class SetupPatientsEligibleToTPTReport extends EptsDataExportManager {

  @Autowired private PatientsEligibleToTPTDataset patientsEligibleToTPTDataset;

  @Override
  public String getUuid() {
    return "24cbd272-cd45-4ba5-8e65-7bc8e23c1ecb";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES ELEGÍVEIS PARA TPT - v1.12.0 ";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of patients eligible to tpt";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.patientsEligibleToTPTDataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "LPETPT",
        Mapped.mapStraightThrough(
            this.patientsEligibleToTPTDataset.constructPatientsEligibleToTPTDataset(
                this.patientsEligibleToTPTDataset.getParameters())));

    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "7d07916c-9ced-4ae5-9795-31c84fcac015";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "LIST_OF_PATIENTS_ELIGIBLE_TO_TPT_REPORT.xls",
              "Lista de Pacientes Elegíveis para TPT",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:11,dataset:LPETPT");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
