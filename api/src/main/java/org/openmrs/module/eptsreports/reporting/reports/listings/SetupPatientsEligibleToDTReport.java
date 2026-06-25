package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PatientsEligibleToDTDataSet;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Abdul Sacur */
@Component
public class SetupPatientsEligibleToDTReport extends EptsDataExportManager {

  @Autowired private PatientsEligibleToDTDataSet eligibleToDT;

  @Override
  public String getUuid() {
    return "7a2d65bc-ce97-42ba-bf42-4b2a0d1e7d3e";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES ELEGÍVEL A DISPENSA TRIMESTRAL - v1.0.0 ";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of patients eligible to trimestral pickUp drugs";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.eligibleToDT.getParameters());
    reportDefinition.addDataSetDefinition(
        "EPL",
        Mapped.mapStraightThrough(
            this.eligibleToDT.constructPatientsEligibleToDTListDataset(
                this.eligibleToDT.getParameters())));

    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "058785cc-de16-442f-b567-e1baef6509ed";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "List_Patients_Eligibles_DT.xls",
              "Lista de Pacientes Elegível a Dispensa Trimestral",
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
