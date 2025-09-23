package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.Eri2MonthsCommunityBMDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.Eri2MonthsCommunityCMDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.Eri4MonthsCommunityBMDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.Eri4MonthsCommunityCMDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxCurrCommunityBMDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxCurrCommunityCMDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxNewCommunityBMDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxNewCommunityCMDataset;
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
public class SetupImErCommunityCMBMReport extends EptsDataExportManager {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private Eri2MonthsCommunityCMDataset eri2MonthsDataset;

  @Autowired private Eri2MonthsCommunityBMDataset eri2MonthsBMDataset;

  @Autowired private Eri4MonthsCommunityCMDataset eri4MonthsDataset;

  @Autowired private Eri4MonthsCommunityBMDataset eri4MonthsBMDataset;

  @Autowired private TxNewCommunityCMDataset txNewDataset;

  @Autowired private TxNewCommunityBMDataset txNewBMDataset;

  @Autowired private TxCurrCommunityCMDataset txCurrDataset;

  @Autowired private TxCurrCommunityBMDataset txCurrBMDataset;

  @Autowired private DatimCodeDataSet datimCodeDataSet;

  @Override
  public String getExcelDesignUuid() {
    return "6ccef034-f82f-4646-972d-93a32ed8c6bb";
  }

  @Override
  public String getUuid() {
    return "b61165bd-9068-49f7-80df-61ed584fdc87";
  }

  @Override
  public String getName() {
    return "IM-ER-Report Comunity-CM-BM";
  }

  @Override
  public String getDescription() {
    return "PEPFAR Early Retention Comunity-CM-BM Indicators";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition rd = new ReportDefinition();

    rd.setUuid(this.getUuid());
    rd.setName(this.getName());
    rd.setDescription(this.getDescription());
    rd.setParameters(this.txNewDataset.getParameters());

    rd.addDataSetDefinition(
        "N", Mapped.mapStraightThrough(this.txNewDataset.constructTxNewDataset()));

    rd.addDataSetDefinition(
        "NN", Mapped.mapStraightThrough(this.txNewBMDataset.constructTxNewDataset()));

    rd.addDataSetDefinition(
        "C", Mapped.mapStraightThrough(this.txCurrDataset.constructTxCurrDataset(true)));

    rd.addDataSetDefinition(
        "CC", Mapped.mapStraightThrough(this.txCurrBMDataset.constructTxCurrDataset(true)));

    rd.addDataSetDefinition(
        "ERI2", Mapped.mapStraightThrough(this.eri2MonthsDataset.constructEri2MonthsDatset()));

    rd.addDataSetDefinition(
        "ERI2BM", Mapped.mapStraightThrough(this.eri2MonthsBMDataset.constructEri2MonthsDatset()));

    rd.addDataSetDefinition(
        "ERI4", Mapped.mapStraightThrough(this.eri4MonthsDataset.constructEri4MonthsDataset()));

    rd.addDataSetDefinition(
        "ERI4BM", Mapped.mapStraightThrough(this.eri4MonthsBMDataset.constructEri4MonthsDataset()));

    rd.addDataSetDefinition(
        "D",
        Mapped.mapStraightThrough(this.datimCodeDataSet.constructDataset(this.getParameters())));

    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
            "endDate=${endDate},location=${location}"));

    return rd;
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
              "IM_ER_COMMUNITY_Report.xls",
              "ERI-COMMUNITY-Report",
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
