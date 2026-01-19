package org.openmrs.module.eptsreports.reporting.library.datasets.listings;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** @author Abdul Sacur */
@Component
public class PrEPExpectedPatientsListDataset extends BaseDataSet {

  private static final String EXPECTED_PATIENT_LIST = "PREP/PACIENTES_ESPERADOS.sql";

  public DataSetDefinition constructPatientExpectedPrEP(final List<Parameter> parameters) {

    final SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();
    dataSetDefinition.setName("Patients Expected to PrEP");
    dataSetDefinition.addParameters(parameters);
    dataSetDefinition.setSqlQuery(EptsQuerysUtils.loadQuery(EXPECTED_PATIENT_LIST));

    return dataSetDefinition;
  }
}
