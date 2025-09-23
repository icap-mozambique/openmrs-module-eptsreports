/** */
package org.openmrs.module.eptsreports.reporting.library.datasets.listings;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.listings.PatientsWithFollowUpButWithoutFila7DaysAfterClinicalEnconterQueries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class PatientsWithFollowUpButWithoutFila7DaysAfterClinicalEncounterDataset
    extends BaseDataSet {

  public DataSetDefinition
      constructPatientsWithFollowUpButWithoutFila7DaysAfterClinicalEncounterDataset(
          final List<Parameter> parameters) {
    final SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();

    dataSetDefinition.setName(
        "PATIENTS WITH FOLLOW UP BUT WITHOUT FILA 7 DAYS AFTER CLINICAL ENCOUNTER");
    dataSetDefinition.addParameters(parameters);
    dataSetDefinition.setSqlQuery(
        PatientsWithFollowUpButWithoutFila7DaysAfterClinicalEnconterQueries.QUERY
            .findPatientsWithFollowUpButWithoutFila7DaysAfterClinicalEnconter);

    return dataSetDefinition;
  }
}
