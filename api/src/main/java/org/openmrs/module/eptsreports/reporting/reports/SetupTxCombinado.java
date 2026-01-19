package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxCombinadoDenominatorDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxCombinadoNumeratorDataset;
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
public class SetupTxCombinado extends EptsDataExportManager {

  @Autowired private TxCombinadoNumeratorDataset txCombinadoNumeratorDataset;

  @Autowired private TxCombinadoDenominatorDataset txCombinadoDenominatorDataset;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Override
  public String getExcelDesignUuid() {
    return "48b063ea-718e-45be-94ae-d2f6a9316364";
  }

  @Override
  public String getUuid() {
    return "83759d9a-c6ba-4776-a1d1-707aa1e08eee";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "TX_COMBINADO Report";
  }

  @Override
  public String getDescription() {
    return "TX COMBINADO 1.0 Report";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition rd = new ReportDefinition();
    rd.setUuid(this.getUuid());
    rd.setName(this.getName());
    rd.setDescription(this.getDescription());

    rd.addParameter(new Parameter("startDate", "Data Inicial", Date.class));
    rd.addParameter(new Parameter("endDate", "Data Final", Date.class));
    rd.addParameter(new Parameter("location", "Location", Location.class));
    rd.addParameter(new Parameter("months", "Número de Meses (12, 24, 36)", Integer.class));

    rd.setBaseCohortDefinition(
        this.genericCohortQueries.getBaseCohort(),
        ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}"));

    rd.addDataSetDefinition(
        "N",
        Mapped.mapStraightThrough(
            this.txCombinadoNumeratorDataset.constructTxCombinadoNumeratorDataset()));

    rd.addDataSetDefinition(
        "D",
        Mapped.mapStraightThrough(
            this.txCombinadoDenominatorDataset.constructTxCombinadoDenominatorDataset()));

    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "TX_COMBINADO.xls",
              "TX COMBINADO 1.0 Report",
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
