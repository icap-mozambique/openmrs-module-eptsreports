package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.TxCombinadoDimensions;
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
public class SetupTxCombinadoListing extends EptsDataExportManager {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private TxCombinadoDimensions dataset;

  @Override
  public String getExcelDesignUuid() {
    return "5e3079ae-7673-4298-9eb6-32b3d819c6ac";
  }

  @Override
  public String getUuid() {
    return "a05f8935-e866-4b48-9fb4-3380c9d90d10";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getName() {
    return "TX_COMBINADO LISTAGEM";
  }

  @Override
  public String getDescription() {
    return "TX COMBINADO Listing ";
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
        "N", Mapped.mapStraightThrough(this.dataset.getTxCombinationListNumerator()));

    rd.addDataSetDefinition(
        "D", Mapped.mapStraightThrough(this.dataset.getTxCombinationListDenominator()));

    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "COMBINADO_LIST.xls",
              "TX COMBINADO List",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:8,dataset:N|sheet:2,row:8,dataset:D");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
