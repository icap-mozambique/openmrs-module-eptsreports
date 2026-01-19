/** */
package org.openmrs.module.eptsreports.reporting.library.datasets.listings;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.listings.PatientsEligibleToTPTQueries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class PatientsEligibleToTPTDataset extends BaseDataSet {

  public DataSetDefinition constructPatientsEligibleToTPTDataset(final List<Parameter> parameters) {

    final SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();
    dataSetDefinition.setName("Patients Elibible to TPT");
    dataSetDefinition.addParameters(parameters);
    dataSetDefinition.setSqlQuery(PatientsEligibleToTPTQueries.QUERY.findPatientsEligibleToTPT);

    return dataSetDefinition;
  }
}
