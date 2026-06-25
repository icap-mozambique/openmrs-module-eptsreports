/** */
package org.openmrs.module.eptsreports.reporting.library.datasets.listings;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.listings.ExpectedChildrenQueries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** @author Abdul Sacur */
@Component
public class ExpectedChildrenListDataset extends BaseDataSet {

  public DataSetDefinition constructExpectedPatientsListDataset(List<Parameter> parameters) {

    SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();
    dataSetDefinition.setName("Expected Children List");
    dataSetDefinition.addParameters(parameters);
    dataSetDefinition.setSqlQuery(ExpectedChildrenQueries.QUERY.findExpectedPatientsList());

    return dataSetDefinition;
  }
}
