/** */
package org.openmrs.module.eptsreports.reporting.library.datasets.listings;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Service;

/** @author Stélio Moiane */
@Service
public class TxMlWithConsultationOrPickupDataset extends BaseDataSet {

  private static final String TX_ML_WITH_CONSULTATION_OR_PICKUP_DRUG = "TX_ML/TX_ML_LIST.sql";

  public DataSetDefinition loadData(final List<Parameter> parameters) {

    final SqlDataSetDefinition dataset = new SqlDataSetDefinition();

    dataset.setName("TX_ML_WITH_CONULTATION_OR_PICKUP_DRUG");
    dataset.addParameters(parameters);
    dataset.setSqlQuery(
        EptsQuerysUtils.loadQuery(
            TxMlWithConsultationOrPickupDataset.TX_ML_WITH_CONSULTATION_OR_PICKUP_DRUG));

    return dataset;
  }
}
