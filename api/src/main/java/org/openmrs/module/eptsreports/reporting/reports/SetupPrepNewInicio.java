package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.PrepNewSectorDataSet;
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
public class SetupPrepNewInicio extends EptsDataExportManager {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private PrepNewSectorDataSet prepNew;

  @Override
  public String getExcelDesignUuid() {
    return "7e18a803-f512-42a3-a6c6-d6e080e32ea8";
  }

  @Override
  public String getUuid() {
    return "15c690c5-c775-4925-9441-638cb20dac47";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "Relatório de PrEP New por Sector e Sub População";
  }

  @Override
  public String getDescription() {
    return "Relatório de PrEP New por Sector e Sub População Report";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition rd = new ReportDefinition();
    rd.setUuid(this.getUuid());
    rd.setName(this.getName());
    rd.setDescription(this.getDescription());
    rd.setParameters(this.prepNew.getParameters());
    rd.addDataSetDefinition(
        "PREP", Mapped.mapStraightThrough(this.prepNew.constructDatset("CPN", 1978)));
    rd.addDataSetDefinition(
        "PREPCPF", Mapped.mapStraightThrough(this.prepNew.constructDatset("CPF", 5483)));
    rd.addDataSetDefinition(
        "PREPCD", Mapped.mapStraightThrough(this.prepNew.constructDatset("DC", 165206)));
    rd.addDataSetDefinition(
        "PREPSAAJ", Mapped.mapStraightThrough(this.prepNew.constructDatset("SAAJ", 1987)));
    rd.addDataSetDefinition(
        "PREPTA", Mapped.mapStraightThrough(this.prepNew.constructDatset("TA", 23873)));
    rd.addDataSetDefinition(
        "PREPOUTRO", Mapped.mapStraightThrough(this.prepNew.OtherDatset(23873)));

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
              "PREP_NEW_Sector.xls",
              "Relatório de PrEP New por Sector e População Chave Report",
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
