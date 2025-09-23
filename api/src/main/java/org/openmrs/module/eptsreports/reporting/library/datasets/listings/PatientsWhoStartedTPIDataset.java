/** */
package org.openmrs.module.eptsreports.reporting.library.datasets.listings;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.listings.PatientsWhoStartedTPIQueries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class PatientsWhoStartedTPIDataset extends BaseDataSet {

  public DataSetDefinition constructPatientsWhoStartedTPIDataset(List<Parameter> parameters) {
    SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();

    dataSetDefinition.setName("PATIENTS WHO STARTED TPI");
    dataSetDefinition.addParameters(parameters);
    dataSetDefinition.setSqlQuery(PatientsWhoStartedTPIQueries.QUERY.findPatientsWhoStartedTPI);

    return dataSetDefinition;
  }
}
