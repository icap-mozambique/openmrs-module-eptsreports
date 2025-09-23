package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PatientsEligibleToViralLoadDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class SetupPatientsEligibleToViralLoadListReport extends EptsDataExportManager {

  @Autowired private PatientsEligibleToViralLoadDataset patientsEligibleToViralLoadDataset;

  @Override
  public String getUuid() {
    return "bf3909cc-d7af-450a-a725-7f949a6fd128";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES ELEGÍVEIS PARA CARGA VIRAL - v1.14.0 ";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of expected patients eligible to viral load";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.patientsEligibleToViralLoadDataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "LPECV",
        Mapped.mapStraightThrough(
            this.patientsEligibleToViralLoadDataset.constructPatientsEligibleToViralLoadDataset(
                this.patientsEligibleToViralLoadDataset.getParameters())));

    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "5bb71bb0-8d58-4e68-9e9f-76ba9bae19ca";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "LIST_OF_PATIENTS_ELIGIBLE_TO_VIRAL_LOAD_REPORT.xls",
              "Lista de Pacientes Elegíveis para Carga Viral",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:11,dataset:LPECV");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
