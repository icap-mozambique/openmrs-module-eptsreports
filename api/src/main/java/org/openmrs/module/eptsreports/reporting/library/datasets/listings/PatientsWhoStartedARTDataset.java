/** */
package org.openmrs.module.eptsreports.reporting.library.datasets.listings;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.listings.PatientsWhoStartedARTQueries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class PatientsWhoStartedARTDataset extends BaseDataSet {

  public DataSetDefinition constructPatientsWhoStartedARTDataset(final List<Parameter> parameters) {
    final SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();

    dataSetDefinition.setName("PATIENTS WHO STARTED ART");
    dataSetDefinition.addParameters(parameters);
    dataSetDefinition.setSqlQuery(PatientsWhoStartedARTQueries.QUERY.findPatientsWhoStartedART);

    return dataSetDefinition;
  }
}
