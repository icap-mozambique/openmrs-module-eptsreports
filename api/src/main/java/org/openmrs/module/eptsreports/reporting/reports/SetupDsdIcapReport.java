/** */
package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.IDPIcapDsdDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxCurrIcapDsdDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxNewIcapDsdDataset;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class SetupDsdIcapReport extends EptsDataExportManager {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private TxCurrIcapDsdDataset txCurrIcapDsdDataset;

  @Autowired private TxNewIcapDsdDataset txNewIcapDsdDataset;

  @Autowired private IDPIcapDsdDataset idp;

  @Override
  public String getUuid() {
    return "72a77701-e8d2-4226-9ecb-48436f2ccd7e";
  }

  @Override
  public String getName() {
    return "Relatório de MDS(ICAP)";
  }

  @Override
  public String getDescription() {
    return "DSD report using MER TX CURR and NEW";
  }

  @Override
  public String getExcelDesignUuid() {
    return "3ea49245-b6a3-49de-bab7-7037c04ba1e4";
  }

  @Override
  public ReportDefinition constructReportDefinition() {

    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.txCurrIcapDsdDataset.getParameters());

    reportDefinition.addDataSetDefinition(
        "C", Mapped.mapStraightThrough(this.txCurrIcapDsdDataset.constructTxCurrDataset()));

    reportDefinition.addDataSetDefinition(
        "N", Mapped.mapStraightThrough(this.txNewIcapDsdDataset.constructTxNewDataset()));

    reportDefinition.addDataSetDefinition(
        "I", Mapped.mapStraightThrough(this.idp.constructTxNewDataset()));

    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
            "endDate=${endDate},location=${location}"));

    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "MDS.xls",
              "Relatório de MDS(ICAP)",
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
