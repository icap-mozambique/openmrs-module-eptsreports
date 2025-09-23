package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.PTVAnalysisListDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class SetupPTVAnalysisListReport extends EptsDataExportManager {

  @Autowired private PTVAnalysisListDataset ptvAnalysisListDataset;

  @Override
  public String getUuid() {
    return "2797505d-df6e-48de-aace-79f58d868b98";
  }

  @Override
  public String getName() {
    return "Análise PTV - v1.14.0 ";
  }

  @Override
  public String getDescription() {
    return "This report provides the list of patients for PTV analysis";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.ptvAnalysisListDataset.getParameters());
    reportDefinition.addDataSetDefinition(
        "PTV",
        Mapped.mapStraightThrough(
            this.ptvAnalysisListDataset.constructPTVAnalysisListDataset(
                this.ptvAnalysisListDataset.getParameters())));

    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "a33ca15f-20a5-487a-9699-7f7ba3117e0a";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition, "PTV_LIST.xls", "Análise PTV", this.getExcelDesignUuid(), null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:5,dataset:PTV");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
