package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.dimension.CohortDimensionResult;
import org.openmrs.module.reporting.indicator.dimension.service.DimensionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public abstract class DefinitionsTest extends BaseModuleContextSensitiveTest {

  protected void addParameters(final CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }

  protected void addParameters(final DataSetDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }

  protected void setParameters(
      final Date startDate,
      final Date endDate,
      final Location location,
      final EvaluationContext context) {
    context.addParameterValue("startDate", startDate);
    context.addParameterValue("endDate", endDate);
    context.addParameterValue("location", location);
  }

  protected Date getStartDate() {
    return DateUtil.getDateTime(1930, 1, 1);
  }

  protected Date getEndDate() {
    return DateUtil.getDateTime(2019, 4, 26);
  }

  protected Location getLocation() {
    return Context.getLocationService().getLocation(1);
  }

  protected DataSet evaluateDatasetDefinition(final DataSetDefinition cd, final Cohort baseCohort)
      throws EvaluationException {
    final EvaluationContext context = new EvaluationContext();
    context.addParameterValue("startDate", this.getStartDate());
    context.addParameterValue("endDate", this.getEndDate());
    context.addParameterValue("location", this.getLocation());
    context.setBaseCohort(baseCohort);
    this.addParameters(cd);
    return Context.getService(DataSetDefinitionService.class).evaluate(cd, context);
  }

  protected DataSet evaluateDatasetDefinition(final DataSetDefinition cd)
      throws EvaluationException {
    final EvaluationContext context = new EvaluationContext();
    context.addParameterValue("startDate", this.getStartDate());
    context.addParameterValue("endDate", this.getEndDate());
    context.addParameterValue("location", this.getLocation());
    this.addParameters(cd);
    return Context.getService(DataSetDefinitionService.class).evaluate(cd, context);
  }

  protected DataSet evaluateDatasetDefinition(
      final DataSetDefinition cd, final Map<Parameter, Object> parameters)
      throws EvaluationException {
    final EvaluationContext context = this.getEvaluationContext(cd, parameters);

    return Context.getService(DataSetDefinitionService.class).evaluate(cd, context);
  }

  protected EvaluatedCohort evaluateCodedObsCohortDefinition(final CohortDefinition cd)
      throws EvaluationException {
    this.addParameters(cd);
    final EvaluationContext context = new EvaluationContext();
    context.addParameterValue("onOrAfter", this.getStartDate());
    context.addParameterValue("onOrBefore", this.getEndDate());
    context.addParameterValue("locationList", this.getLocation());
    return Context.getService(CohortDefinitionService.class).evaluate(cd, context);
  }

  protected EvaluatedCohort evaluateCalculationCohortDefinition(final CohortDefinition cd)
      throws EvaluationException {
    this.addParameters(cd);
    final EvaluationContext context = new EvaluationContext();
    context.addParameterValue("onOrAfter", DateUtil.getDateTime(2019, 2, 6));
    context.addParameterValue("onOrBefore", this.getEndDate());
    context.addParameterValue("location", this.getLocation());
    return Context.getService(CohortDefinitionService.class).evaluate(cd, context);
  }

  protected EvaluatedCohort evaluateCohortDefinition(final CohortDefinition cd)
      throws EvaluationException {
    this.addParameters(cd);
    final EvaluationContext context = new EvaluationContext();

    this.setParameters(this.getStartDate(), this.getEndDate(), this.getLocation(), context);
    return Context.getService(CohortDefinitionService.class).evaluate(cd, context);
  }

  protected EvaluatedCohort evaluateCohortDefinition(
      final CohortDefinition cd, final Date startDate, final Date endDate, final Location location)
      throws EvaluationException {
    this.addParameters(cd);
    final EvaluationContext context = new EvaluationContext();

    this.setParameters(startDate, endDate, location, context);
    return Context.getService(CohortDefinitionService.class).evaluate(cd, context);
  }

  protected EvaluatedCohort evaluateCohortDefinition(
      final CohortDefinition cd, final Map<Parameter, Object> parameters)
      throws EvaluationException {
    final EvaluationContext context = this.getEvaluationContext(cd, parameters);
    return Context.getService(CohortDefinitionService.class).evaluate(cd, context);
  }

  protected DataSet evaluateCohortDefinition(
      final DataSetDefinition dd, final Map<Parameter, Object> parameters)
      throws EvaluationException {
    final EvaluationContext context = this.getEvaluationContext(dd, parameters);
    return Context.getService(DataSetDefinitionService.class).evaluate(dd, context);
  }

  private EvaluationContext getEvaluationContext(
      final Definition cd, final Map<Parameter, Object> parameters) {
    final EvaluationContext context = new EvaluationContext();
    if (parameters != null) {
      final Iterator it = parameters.entrySet().iterator();
      while (it.hasNext()) {
        final Map.Entry p = (Map.Entry) it.next();
        final Parameter parameter = (Parameter) p.getKey();
        if (!cd.getParameters().contains(parameter)) {
          cd.addParameter(parameter);
        }
        context.addParameterValue(parameter.getName(), p.getValue());
      }
    }
    return context;
  }

  protected CohortDimensionResult evaluateCohortDefinitionDimension(
      final CohortDefinitionDimension cohortDefinitionDimension,
      final Map<Parameter, Object> parameters)
      throws EvaluationException {
    final EvaluationContext context =
        this.getEvaluationContext(cohortDefinitionDimension, parameters);
    return (CohortDimensionResult)
        Context.getService(DimensionService.class).evaluate(cohortDefinitionDimension, context);
  }
}
