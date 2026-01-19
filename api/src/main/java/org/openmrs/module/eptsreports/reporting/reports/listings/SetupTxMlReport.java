/** */
package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.TxMlWithConsultationOrPickupDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** @author Abdul Sacur */
@Service
public class SetupTxMlReport extends EptsDataExportManager {

  @Autowired private TxMlWithConsultationOrPickupDataset dataset;

  @Override
  public String getUuid() {
    return "4eb86ce9-aed8-4c56-aefa-4aad12e7a61d";
  }

  @Override
  public String getName() {
    return "TXML LISTA";
  }

  @Override
  public String getDescription() {
    return "TXML LISTA";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.dataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "LFU", Mapped.mapStraightThrough(this.dataset.loadData(this.dataset.getParameters())));
    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.0";
  }

  @Override
  public String getExcelDesignUuid() {
    return "80a0f63d-264a-45e4-a49a-775aeee45a68";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;

    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "TX_ML_LIST.xls",
              "Lista Pacientes TX_ML",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:8,dataset:LFU");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);

    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
