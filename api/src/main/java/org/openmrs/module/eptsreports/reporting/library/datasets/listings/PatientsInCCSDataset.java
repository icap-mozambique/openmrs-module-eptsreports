/** */
package org.openmrs.module.eptsreports.reporting.library.datasets.listings;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.CxCaQueries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class PatientsInCCSDataset extends BaseDataSet {

  public DataSetDefinition constructPatientsInCcsDataset(final List<Parameter> parameters) {

    final SqlDataSetDefinition sqlDataSetDefinition = new SqlDataSetDefinition();

    sqlDataSetDefinition.setName("PATIENTS WHO ARE IN CCS");
    sqlDataSetDefinition.setParameters(parameters);
    sqlDataSetDefinition.setSqlQuery(CxCaQueries.findPatientsWithCervicalCencerScreenList);

    return sqlDataSetDefinition;
  }
}
