/** */
package org.openmrs.module.eptsreports.reporting.library.datasets.listings;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Service;

/** @author Abdul Sacur */
@Service
public class PrepAvaliacaoDataset extends BaseDataSet {

  private static final String PREP = "PREP/prep_avaliacao_v3.sql";

  public DataSetDefinition loadData(final List<Parameter> parameters) {

    final SqlDataSetDefinition dataset = new SqlDataSetDefinition();

    dataset.setName("PREP DATASET");
    dataset.addParameters(parameters);
    dataset.setSqlQuery(EptsQuerysUtils.loadQuery(PrepAvaliacaoDataset.PREP));

    return dataset;
  }
}
