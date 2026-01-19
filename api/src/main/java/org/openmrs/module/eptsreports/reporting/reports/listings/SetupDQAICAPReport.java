/** */
package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.TXCURRDQAICAPDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** @author Abdul Sacur */
@Service
public class SetupDQAICAPReport extends EptsDataExportManager {

  @Autowired private TXCURRDQAICAPDataset dataset;

  @Override
  public String getUuid() {
    return "de22463a-5c1d-4469-90b1-b0038a6fcc5a";
  }

  @Override
  public String getName() {
    return "DQA LISTA";
  }

  @Override
  public String getDescription() {
    return "DQA LISTA";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.dataset.getParametersNewART());
    reportDefinition.addDataSetDefinition(
        "LFU",
        Mapped.mapStraightThrough(this.dataset.loadData(this.dataset.getParametersNewART())));
    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.0";
  }

  @Override
  public String getExcelDesignUuid() {
    return "f28290d2-0ae1-4df5-a01f-cbaca2cb6b46";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;

    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "LIST_DQA_ICAP.xls",
              "Lista Pacientes TXCURR DQA - ICAP",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:3,dataset:LFU");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);

    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
