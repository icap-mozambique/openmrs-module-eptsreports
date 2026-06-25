/** */
package org.openmrs.module.eptsreports.reporting.library.datasets.listings;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class ExpectedPatientsListDataset extends BaseDataSet {

  private static final String EXPECTED_PATIENT_LIST = "EXPECTED_PATIENTS/EXPECTED_PATIENT_LIST.sql";

  public DataSetDefinition constructExpectedPatientsListDataset(List<Parameter> parameters) {

    SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();
    dataSetDefinition.setName("Expected Patients List");
    dataSetDefinition.addParameters(parameters);
    dataSetDefinition.setSqlQuery(
        EptsQuerysUtils.loadQuery(ExpectedPatientsListDataset.EXPECTED_PATIENT_LIST));

    return dataSetDefinition;
  }
}
