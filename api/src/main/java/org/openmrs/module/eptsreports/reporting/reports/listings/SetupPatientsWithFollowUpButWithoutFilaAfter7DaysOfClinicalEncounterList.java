/** */
package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PatientsWithFollowUpButWithoutFila7DaysAfterClinicalEncounterDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class SetupPatientsWithFollowUpButWithoutFilaAfter7DaysOfClinicalEncounterList
    extends EptsDataExportManager {

  @Autowired
  private PatientsWithFollowUpButWithoutFila7DaysAfterClinicalEncounterDataset
      patientsWithFollowUpButWithoutFila7DaysAfterClinicalEncounterDataset;

  @Override
  public String getUuid() {
    return "43becb60-e8b7-40a2-973e-695fda887d41";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES COM SEGUIMENTO SEM FILA 7 DIAS DEPOIS DA CONSULTA CLINICA - v1.9.0";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of patients with follow up but without FILA 7 days after clinical encounter";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();

    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(
        this.patientsWithFollowUpButWithoutFila7DaysAfterClinicalEncounterDataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "PWWF",
        Mapped.mapStraightThrough(
            this.patientsWithFollowUpButWithoutFila7DaysAfterClinicalEncounterDataset
                .constructPatientsWithFollowUpButWithoutFila7DaysAfterClinicalEncounterDataset(
                    this.patientsWithFollowUpButWithoutFila7DaysAfterClinicalEncounterDataset
                        .getParameters())));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "PATIENTS_WITH_FOLLOW_UP_BUT_WITHOUT_FILA_7_DAYS_AFTER_CLINICAL_ENCOUNTER.xls",
              "LISTA DE PACIENTES COM SEGUIMENTO SEM FILA 7 DIAS DEPOIS DA CONSULTA CLINICA",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:5,dataset:PWWF");
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
    return "33f74d83-8347-470e-8f1d-b3517c522dbd";
  }
}
