/** */
package org.openmrs.module.eptsreports.reporting.library.datasets.listings;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Service;

/** @author Abdul Sacur */
@Service
public class PatientsMuzimaVisitasBuscasSaajAdultoSmiDataset extends BaseDataSet {

  private static final String SAAJ_PREVENCAO = "MUZIMAICAP/SAAJ_PREVENTIVO_LIST.sql";
  private static final String SAAJ_REINTEGRACAO = "MUZIMAICAP/SAAJ_REINTEGRACAO_LIST.sql";

  private static final String ADULTO_PREVENCAO = "MUZIMAICAP/ADULTO_PREVENTIVO_LIST.sql";
  private static final String ADULTO_REINTEGRACAO = "MUZIMAICAP/ADULTO_REINTEGRACAO_LIST.sql";

  private static final String SMI_PREVENCAO = "MUZIMAICAP/SMI_PREVENTIVO_LIST.sql";
  private static final String SMI_REINTEGRACAO = "MUZIMAICAP/SMI_REINTEGRACAO_LIST.sql";

  public DataSetDefinition loadDataSaajPrevencao(final List<Parameter> parameters) {

    final SqlDataSetDefinition dataset = new SqlDataSetDefinition();

    dataset.setName("MUZIMAICAP SAAJ PREVENCAO DATASET");
    dataset.addParameters(parameters);
    dataset.setSqlQuery(
        EptsQuerysUtils.loadQuery(PatientsMuzimaVisitasBuscasSaajAdultoSmiDataset.SAAJ_PREVENCAO));

    return dataset;
  }

  public DataSetDefinition loadDataSaajReintegracao(final List<Parameter> parameters) {

    final SqlDataSetDefinition dataset = new SqlDataSetDefinition();

    dataset.setName("MUZIMAICAP SAAJ REINTEGRACAO DATASET");
    dataset.addParameters(parameters);
    dataset.setSqlQuery(
        EptsQuerysUtils.loadQuery(
            PatientsMuzimaVisitasBuscasSaajAdultoSmiDataset.SAAJ_REINTEGRACAO));

    return dataset;
  }

  public DataSetDefinition loadDataAdultoPrevencao(final List<Parameter> parameters) {

    final SqlDataSetDefinition dataset = new SqlDataSetDefinition();

    dataset.setName("MUZIMAICAP ADULTO PREVENCAO DATASET");
    dataset.addParameters(parameters);
    dataset.setSqlQuery(
        EptsQuerysUtils.loadQuery(
            PatientsMuzimaVisitasBuscasSaajAdultoSmiDataset.ADULTO_PREVENCAO));

    return dataset;
  }

  public DataSetDefinition loadDataAdultoReintegracao(final List<Parameter> parameters) {

    final SqlDataSetDefinition dataset = new SqlDataSetDefinition();

    dataset.setName("MUZIMAICAP ADULTO REINTEGRACAO DATASET");
    dataset.addParameters(parameters);
    dataset.setSqlQuery(
        EptsQuerysUtils.loadQuery(
            PatientsMuzimaVisitasBuscasSaajAdultoSmiDataset.ADULTO_REINTEGRACAO));

    return dataset;
  }

  public DataSetDefinition loadDataSmiPrevencao(final List<Parameter> parameters) {

    final SqlDataSetDefinition dataset = new SqlDataSetDefinition();

    dataset.setName("MUZIMAICAP SMI PREVENCAO DATASET");
    dataset.addParameters(parameters);
    dataset.setSqlQuery(
        EptsQuerysUtils.loadQuery(PatientsMuzimaVisitasBuscasSaajAdultoSmiDataset.SMI_PREVENCAO));

    return dataset;
  }

  public DataSetDefinition loadDataSmiReintegracao(final List<Parameter> parameters) {

    final SqlDataSetDefinition dataset = new SqlDataSetDefinition();

    dataset.setName("MUZIMAICAP SMI REINTEGRACAO DATASET");
    dataset.addParameters(parameters);
    dataset.setSqlQuery(
        EptsQuerysUtils.loadQuery(
            PatientsMuzimaVisitasBuscasSaajAdultoSmiDataset.SMI_REINTEGRACAO));

    return dataset;
  }
}
