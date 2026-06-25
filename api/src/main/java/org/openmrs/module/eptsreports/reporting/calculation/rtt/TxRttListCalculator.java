/** */
package org.openmrs.module.eptsreports.reporting.calculation.rtt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxRTTCohortQueries;
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
public class TxRttListCalculator extends BaseFghCalculation {

  private static final String TX_RTT_LIST = "TX_ML/TX_RTT_LIST.sql";

  @Autowired private TxRTTCohortQueries txRTTCohortQueries;

  @Override
  public DataSet evaluate(final EvaluationContext context) {

    Set<Integer> memberIds = null;
    final SimpleDataSet dataSet =
        new SimpleDataSet(this.txRTTCohortQueries.getTxRttList(), context);

    try {
      memberIds =
          Context.getService(CohortDefinitionService.class)
              .evaluate(this.txRTTCohortQueries.getPatientsOnRTT(), context)
              .getMemberIds();
    } catch (final EvaluationException e) {
      throw new APIException(e);
    }

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(
            EptsQuerysUtils.loadQuery(TxRttListCalculator.TX_RTT_LIST),
            context.getParameterValues());

    if (memberIds == null || memberIds.isEmpty()) {

      memberIds = new HashSet<Integer>();
      memberIds.add(0);
    }

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
