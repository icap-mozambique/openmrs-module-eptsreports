package org.openmrs.module.eptsreports.reporting.reports.listings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.SismaCodeDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.listings.CCUScrenningPatientsListDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Abdul Sacur */
@Component
public class SetupCCUScreeningPatientsListReport extends EptsDataExportManager {

  @Autowired private CCUScrenningPatientsListDataset CCU;

  @Autowired private SismaCodeDataSet sismaCodeDataSet;

  @Autowired private DatimCodeDataSet datimCodeDataSet;

  @Override
  public String getUuid() {
    return "ad652180-0edf-4abc-8276-b949381d03b0";
  }

  @Override
  public String getName() {
    return "LISTA DE PACIENTES ELEGIVEIS PARA O RASTREIO DE CACU - v1.0.0 ";
  }

  @Override
  public String getDescription() {
    return "Este relatorio gera pacientes elegiveis para o rastreio de CACU";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();
    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.CCU.getParameters());
    reportDefinition.addDataSetDefinition(
        "CCU",
        Mapped.mapStraightThrough(
            this.CCU.constructCCUEligiblePatientsScreeningListDataset(this.CCU.getParameters())));

    reportDefinition.addDataSetDefinition(
        "D",
        Mapped.mapStraightThrough(this.datimCodeDataSet.constructDataset(this.getParameters())));

    reportDefinition.addDataSetDefinition(
        "SC",
        Mapped.mapStraightThrough(this.sismaCodeDataSet.constructDataset(this.getParameters())));

    return reportDefinition;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getExcelDesignUuid() {
    return "a2c08d88-a6be-44c5-9a3c-991fe0b52a85";
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "LIST_CCU_SCREENING.xls",
              "Lista de Pacientes Elegiveis para rastreio de CACU",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("repeatingSections", "sheet:1,row:8,dataset:CCU");
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
