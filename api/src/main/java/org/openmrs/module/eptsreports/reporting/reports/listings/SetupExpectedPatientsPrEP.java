/** */
package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PrEPExpectedPatientsListDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Abdul Sacur */
@Component
public class SetupExpectedPatientsPrEP extends EptsDataExportManager {

  @Autowired private PrEPExpectedPatientsListDataset expectedPrEP;

  @Override
  public String getUuid() {
    return "da2be5a4-94ab-11ec-b909-0242ac120002";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES ESPERADOS DE PREP NUM DETERMINADO PERÍODO - v1.1.0";
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
    reportDefinition.setParameters(this.expectedPrEP.getParameters());
    reportDefinition.addDataSetDefinition(
        "PREP",
        Mapped.mapStraightThrough(
            this.expectedPrEP.constructPatientExpectedPrEP(this.expectedPrEP.getParameters())));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "ListaEsperadoPrEP.xls",
              "Lista de Pacientes Esperados para PrEP",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:7,dataset:PREP");
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
    return "da2be7fc-94ab-11ec-b909-0242ac120002";
  }
}
