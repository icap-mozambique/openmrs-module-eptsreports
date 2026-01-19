package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.SearchedPatientsListDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class SetupSearchedPatientsListReport extends EptsDataExportManager {

  @Autowired private SearchedPatientsListDataset searchedPatientsListDataset;

  @Override
  public String getUuid() {
    return "50a0507b-da10-4cba-8293-9088b9109c4d";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES BUSCADOS";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of seached patients to follow up reitegrations";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.searchedPatientsListDataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "PSL",
        Mapped.mapStraightThrough(
            this.searchedPatientsListDataset.constructSearchedPatientsListDataset(
                this.searchedPatientsListDataset.getParameters())));

    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "65a594d6-d6b5-48dc-b528-59900fc5e4f4";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "SEARCHED_PATIENTS_REPORT.xls",
              "Lista de Pacientes Buscados",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:7,dataset:PSL");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
