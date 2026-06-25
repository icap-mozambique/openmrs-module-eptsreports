/** */
package org.openmrs.module.eptsreports.reporting.library.datasets.listings;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.PatientsEligibleToAPEs;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** @author Abdul Sacur */
@Component
public class PatientsEligibleToAPEsDataset extends BaseDataSet {

  public DataSetDefinition constructPatientsEligibleToAPEs(final List<Parameter> parameters) {
    final SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();

    dataSetDefinition.setName("PATIENTS ELIGIBLE TO APEs");
    dataSetDefinition.addParameters(parameters);
    dataSetDefinition.setSqlQuery(PatientsEligibleToAPEs.findPatientEligibleToAPEsQuery);

    return dataSetDefinition;
  }

  @Override
  public List<Parameter> getParameters() {
    final List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }
}
