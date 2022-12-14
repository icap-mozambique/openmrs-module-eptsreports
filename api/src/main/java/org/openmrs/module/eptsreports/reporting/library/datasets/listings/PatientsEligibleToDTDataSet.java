/** */
package org.openmrs.module.eptsreports.reporting.library.datasets.listings;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.listings.PatientsEligibleToDTQueries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** @author Abdul Sacur */
@Component
public class PatientsEligibleToDTDataSet extends BaseDataSet {

  public DataSetDefinition constructPatientsEligibleToDTListDataset(List<Parameter> parameters) {

    SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();
    dataSetDefinition.setName("Patients Eligible to Trimestral pickUp drugs");
    dataSetDefinition.addParameters(parameters);
    dataSetDefinition.setSqlQuery(
        PatientsEligibleToDTQueries.QUERY.findPatientsEligibleToDTPickUP());

    return dataSetDefinition;
  }
}
