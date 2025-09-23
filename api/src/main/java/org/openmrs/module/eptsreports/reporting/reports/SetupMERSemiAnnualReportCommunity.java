package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.CxCaSCRNDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.CxCaTXDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.TbPrevDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxTBCommunityDataset;
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
public class SetupMERSemiAnnualReportCommunity extends EptsDataExportManager {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private TxTBCommunityDataset TxTBCommunityDataset;

  @Autowired private TbPrevDataset tbPrevDataset;

  @Autowired private CxCaSCRNDataSet cxCaSCRNDataSet;

  @Autowired private CxCaTXDataSet CxCaTXDataSet;

  @Autowired private DatimCodeDataSet datimCodeDataSet;

  @Override
  public String getExcelDesignUuid() {
    return "218381d8-7c4b-4fe0-9e14-c6d13a336e64";
  }

  @Override
  public String getUuid() {
    return "3ff54e61-9bf9-4458-a35f-f2deb5254af6";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "PEPFAR MER 2.7 Semi-Annual Community";
  }

  @Override
  public String getDescription() {
    return "PEPFAR MER 2.7 Semi-Annual Report Community";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition rd = new ReportDefinition();
    rd.setUuid(this.getUuid());
    rd.setName(this.getName());
    rd.setDescription(this.getDescription());
    rd.setParameters(this.TxTBCommunityDataset.getParameters());
    rd.addDataSetDefinition(
        "T", Mapped.mapStraightThrough(this.TxTBCommunityDataset.constructTxTBCommunityDataset()));
    rd.addDataSetDefinition(
        "TBPREV", Mapped.mapStraightThrough(this.tbPrevDataset.constructDatset(Boolean.TRUE)));
    rd.addDataSetDefinition(
        "CX", Mapped.mapStraightThrough(this.cxCaSCRNDataSet.constructDatset(Boolean.TRUE)));
    rd.addDataSetDefinition(
        "CXT", Mapped.mapStraightThrough(this.CxCaTXDataSet.constructDatset(Boolean.TRUE)));

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
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "PEPFAR_MER_2.7_Semiannual.xls",
              "PEPFAR MER 2.7 Semi-Annual Report",
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
