/** */
package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PatientsWhoAreSecondLineDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class SetupPatientsWhoAreOnSecondLineList extends EptsDataExportManager {

  @Autowired private PatientsWhoAreSecondLineDataset patientsWhoAreSecondLineDataset;

  @Override
  public String getUuid() {
    return "a7987c6e-ea6f-4447-bafc-5dfc376690f7";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES QUE ESTÃO NA SEGUNDA LINHA - v1.9.0";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of patients who are on ART second line";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();

    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.patientsWhoAreSecondLineDataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "PSL",
        Mapped.mapStraightThrough(
            this.patientsWhoAreSecondLineDataset.constructPatientsWhoAreSecondLineDataset(
                this.patientsWhoAreSecondLineDataset.getParameters())));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "PATIENTS_WHO_ARE_ON_SECOND_LINE_LIST_REPORT.xls",
              "LISTA DE PACIENTES QUE ESTÃO NA SEGUNDA LINHA",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:3,dataset:PSL");
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
    return "a7488250-f6be-4903-be9b-a65037c2a581";
  }
}
