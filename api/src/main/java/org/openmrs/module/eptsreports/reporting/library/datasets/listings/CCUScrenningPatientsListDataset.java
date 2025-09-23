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
public class CCUScrenningPatientsListDataset extends BaseDataSet {

  private static final String CCU = "CCU_PATIENTS/RASTREIO_CCU.sql";

  public DataSetDefinition constructCCUEligiblePatientsScreeningListDataset(
      List<Parameter> parameters) {

    SqlDataSetDefinition dataSetDefinition = new SqlDataSetDefinition();
    dataSetDefinition.setName("patients eligible for CACUM screening");
    dataSetDefinition.addParameters(parameters);
    dataSetDefinition.setSqlQuery(EptsQuerysUtils.loadQuery(CCUScrenningPatientsListDataset.CCU));

    return dataSetDefinition;
  }
}
