/** */
package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseIcapCalculationDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/** @author Stélio Moiane */
@Handler(supports = BaseIcapCalculationDataSetDefinition.class)
public class BaseIcapDataSetCalculatorEvaluator implements DataSetEvaluator {

  @Override
  public DataSet evaluate(
      final DataSetDefinition dataSetDefinition, final EvaluationContext evalContext)
      throws EvaluationException {

    final BaseIcapCalculationDataSetDefinition calculationCohortDefinition =
        (BaseIcapCalculationDataSetDefinition) dataSetDefinition;

    final BaseFghCalculation calculation = calculationCohortDefinition.getCalculation();

    final DataSet dataSet = calculation.evaluate(evalContext);

    return dataSet;
  }
}
