/** */
package org.openmrs.module.eptsreports.reporting.library.datasets.listings;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.listings.PatientsOnARTwithViralLoadsQueries;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class PatientsOnARTwithViralLoadsDataset extends BaseDataSet {

  public DataSetDefinition constructPatientsOnARTwithViralLoadsDataset(
      final List<Parameter> parameters) {
    final SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();

    dataSetDefinition.setName("PATIENTS ON ART WITH VIRAL LOADS");
    dataSetDefinition.addParameters(parameters);
    dataSetDefinition.setSqlQuery(
        PatientsOnARTwithViralLoadsQueries.findPatientsOnARTwithViralLoads);

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
