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
    return "Relatório de PrEP New por Sector e População Chave";
  }

  @Override
  public String getDescription() {
    return "Relatório de PrEP New por Sector e População Chave Report";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.setParameters(prepNew.getParameters());
    rd.addDataSetDefinition(
        "PREP", Mapped.mapStraightThrough(prepNew.constructDatset("CPN", 1978)));
    rd.addDataSetDefinition(
        "PREPCPF", Mapped.mapStraightThrough(prepNew.constructDatset("CPF", 5483)));
    rd.addDataSetDefinition(
        "PREPCD", Mapped.mapStraightThrough(prepNew.constructDatset("DC", 165206)));
    rd.addDataSetDefinition(
        "PREPSAAJ", Mapped.mapStraightThrough(prepNew.constructDatset("SAAJ", 1987)));
    rd.addDataSetDefinition(
        "PREPTA", Mapped.mapStraightThrough(prepNew.constructDatset("TA", 23873)));
    rd.addDataSetDefinition("PREPOUTRO", Mapped.mapStraightThrough(prepNew.OtherDatset(23873)));

    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
            "endDate=${endDate},location=${location}"));
    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "PREP_NEW_Sector.xls",
              "Relatório de PrEP New por Sector e População Chave Report",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
