package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.PrepNewLigacaoCPNeATSDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupPrepCasalSeroDescordante extends EptsDataExportManager {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private PrepNewLigacaoCPNeATSDataSet prepNew;

  @Override
  public String getExcelDesignUuid() {
    return "dbbac542-c3a2-4219-bc94-f5655873d7e0";
  }

  @Override
  public String getUuid() {
    return "30dab545-7140-4a98-8d1c-6625fedc951d";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "Relatório de casais serodiscordantes PrEP com ligação em CPN e ATS";
  }

  @Override
  public String getDescription() {
    return "Relatório de casais serodiscordantes PrEP com ligação em CPN e ATS";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition rd = new ReportDefinition();
    rd.setUuid(this.getUuid());
    rd.setName(this.getName());
    rd.setDescription(this.getDescription());
    rd.setParameters(this.prepNew.getParameters());
    rd.addDataSetDefinition(
        "PREP", Mapped.mapStraightThrough(this.prepNew.constructDatset("ATSC", 6245)));
    rd.addDataSetDefinition(
        "PREPCPN", Mapped.mapStraightThrough(this.prepNew.constructDatset("CPN", 1978)));
    rd.addDataSetDefinition(
        "PREPUATS", Mapped.mapStraightThrough(this.prepNew.constructDatset("UATS", 1597)));

    // Se aceita ou não entrar na PREP
    rd.addDataSetDefinition(
        "ATSC",
        Mapped.mapStraightThrough(this.prepNew.constructDatasetEnrollemntPrep("ATSC", 6245)));
    rd.addDataSetDefinition(
        "CPN", Mapped.mapStraightThrough(this.prepNew.constructDatasetEnrollemntPrep("CPN", 1978)));
    rd.addDataSetDefinition(
        "UATS",
        Mapped.mapStraightThrough(this.prepNew.constructDatasetEnrollemntPrep("UATS", 1597)));

    // SubTotais e Total
    rd.addDataSetDefinition(
        "PREPSub", Mapped.mapStraightThrough(this.prepNew.constructDatasetSubTotal()));

    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
            "endDate=${endDate},location=${location}"));
    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "PrEP_Ligacao_CPN_ATS.xls",
              "Relatório de casais serodiscordantes PrEP com ligação em CPN e ATS",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
