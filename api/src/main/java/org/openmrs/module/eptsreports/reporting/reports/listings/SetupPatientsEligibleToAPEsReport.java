/** */
package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PatientsEligibleToAPEsDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Abdul Sacur */
@Component
public class SetupPatientsEligibleToAPEsReport extends EptsDataExportManager {

  @Autowired private PatientsEligibleToAPEsDataset patientEligibleToAPEs;

  @Override
  public String getUuid() {
    return "e38d7ab8-73a4-11ec-90d6-0242ac120003";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES ELEGÍVEIS PARA APEs - v1.0.0";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of patients eligible to APEs";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();

    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.patientEligibleToAPEs.getParameters());
    reportDefinition.addDataSetDefinition(
        "APES",
        Mapped.mapStraightThrough(
            this.patientEligibleToAPEs.constructPatientsEligibleToAPEs(
                this.patientEligibleToAPEs.getParameters())));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "LIST_OF_PATIENTS_ELIGIBLE_TO_APEs_REPORT.xls",
              "LISTA DE PACIENTES ELEGÍVEIS PARA APEs",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:7,dataset:APES");
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
    return "e38d7cfc-73a4-11ec-90d6-0242ac120003";
  }
}
