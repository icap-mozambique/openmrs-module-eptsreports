package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxRetDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupTxRetReportICAP extends EptsDataExportManager {

  @Autowired private TxRetDataset txRetDataset;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Override
  public String getExcelDesignUuid() {
    return "0fe326d9-4c3e-48b7-8d48-3c68bb2e41bd";
  }

  @Override
  public String getUuid() {
    return "437bc4ec-1eb9-4772-9183-d177630a934e";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "TX_RET Report ICAP";
  }

  @Override
  public String getDescription() {
    return "TX RET 2.1 Report ICAP";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());

    rd.addParameter(new Parameter("startDate", "Data Inicial", Date.class));
    rd.addParameter(new Parameter("endDate", "Data Final", Date.class));
    rd.addParameter(new Parameter("location", "Location", Location.class));
    rd.addParameter(new Parameter("months", "Número de Meses (12, 24, 36)", Integer.class));
    // add a base cohort to the report
    rd.setBaseCohortDefinition(
        genericCohortQueries.getBaseCohort(),
        ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}"));
    rd.addDataSetDefinition("R", Mapped.mapStraightThrough(txRetDataset.constructTxRetDataset()));

    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          createXlsReportDesign(
              reportDefinition,
              "TX_RET_21_ReportICAP.xls",
              "TX_RET 2.1 Report ICAP",
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
