/** */
package org.openmrs.module.eptsreports.reporting.calculation.rtt;

import java.util.List;
import java.util.Set;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxCombinadoNumeratorDataset;
import org.openmrs.module.eptsreports.reporting.library.dimensions.TxCombinadoDimensions;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class TxCombinadoNumeratorListCalculator extends BaseFghCalculation {

  private static final String TX_COMBINATION_LIST = "TX_ML/TX_COMBINADO_LISTING_N.sql";

  @Autowired private TxCombinadoNumeratorDataset dataset;

  @Autowired private TxCombinadoDimensions txCombinadoDimensions;

  @Override
  public DataSet evaluate(final EvaluationContext context) {

    Set<Integer> memberIds = null;
    final SimpleDataSet dataSet =
        new SimpleDataSet(this.txCombinadoDimensions.getTxCombinationListNumerator(), context);

    try {
      memberIds =
          Context.getService(CohortDefinitionService.class)
              .evaluate(this.dataset.numeratorCohortDefinition(), context)
              .getMemberIds();
    } catch (final EvaluationException e) {
      throw new APIException(e);
    }

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(
            EptsQuerysUtils.loadQuery(TxCombinadoNumeratorListCalculator.TX_COMBINATION_LIST),
            context.getParameterValues());

    queryBuilder.addParameter("patientIds", memberIds);

    final List<DataSetColumn> columns =
        Context.getService(EvaluationService.class).getColumns(queryBuilder);

    final List<Object[]> evaluateToList =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToList(queryBuilder, context);

    for (final Object[] row : evaluateToList) {

      final DataSetRow dataSetRow = new DataSetRow();

      for (int i = 0; i < columns.size(); i++) {
        dataSetRow.addColumnValue(columns.get(i), row[i]);
      }
      dataSet.addRow(dataSetRow);
    }

    return dataSet;
  }
}
