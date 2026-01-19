/** */
package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PrepAvaliacaoDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** @author Abdul Sacur */
@Service
public class SetupPrepAvaliacao extends EptsDataExportManager {

  @Autowired private PrepAvaliacaoDataset dataset;
  @Autowired private DatimCodeDataSet datimCodeDataset;

  @Override
  public String getUuid() {
    return "647d2f66-5adf-4e60-b489-e3fcb9676ee3";
  }

  @Override
  public String getName() {
    return "PREP LISTA AVALIACAO";
  }

  @Override
  public String getDescription() {
    return "PREP LISTA AVALIACAO";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.dataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "D",
        Mapped.mapStraightThrough(this.datimCodeDataset.constructDataset(this.getParameters())));
    reportDefinition.addDataSetDefinition(
        "PREP", Mapped.mapStraightThrough(this.dataset.loadData(this.dataset.getParameters())));
    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.1";
  }

  @Override
  public String getExcelDesignUuid() {
    return "7474bb74-7c7d-4faa-8148-34161a4bb2c7";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;

    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "PREP_LIST_AVALIACAO.xls",
              "Lista Pacientes na Coorte PREP",
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
}
