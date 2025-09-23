/** */
package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxRTTCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** @author Stélio Moiane */
@Service
public class SetupTxRttListingReport extends EptsDataExportManager {

  @Autowired private TxRTTCohortQueries dataset;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Override
  public String getUuid() {
    return "1ae33f4f-1310-47e8-b67d-608bbc9e69f4";
  }

  @Override
  public String getName() {
    return "TX_RTT LISTAGEM";
  }

  @Override
  public String getDescription() {
    return "TX_RTT Listing";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());

    reportDefinition.addParameter(ReportingConstants.START_DATE_PARAMETER);
    reportDefinition.addParameter(ReportingConstants.END_DATE_PARAMETER);
    reportDefinition.addParameter(ReportingConstants.LOCATION_PARAMETER);

    reportDefinition.addDataSetDefinition(
        "RTT", Mapped.mapStraightThrough(this.dataset.getTxRttList()));

    reportDefinition.addDataSetDefinition(
        "RTT_3", Mapped.mapStraightThrough(this.dataset.getTxRttLessThan3MonthsList()));

    reportDefinition.addDataSetDefinition(
        "RTT_3_5", Mapped.mapStraightThrough(this.dataset.getTxRttBetween3To5MonthsList()));

    reportDefinition.addDataSetDefinition(
        "RTT_6", Mapped.mapStraightThrough(this.dataset.getTxRttGreaterThan6MonthsList()));

    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
            "endDate=${endDate},location=${location}"));

    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.0";
  }

  @Override
  public String getExcelDesignUuid() {
    return "4f205e40-6a4f-4fb5-8cd2-fb7ad69f01f5";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;

    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition, "RTT_LIST.xls", "TX_RTT Listagem", this.getExcelDesignUuid(), null);
      final Properties props = new Properties();
      props.put(
          "repeatingSections",
          "sheet:1,row:8,dataset:RTT|sheet:2,row:8,dataset:RTT_3|sheet:3,row:8,dataset:RTT_3_5|sheet:4,row:8,dataset:RTT_6");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);

    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
