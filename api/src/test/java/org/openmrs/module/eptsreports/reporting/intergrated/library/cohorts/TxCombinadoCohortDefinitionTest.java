package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsFGHLiveTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxCombinadoDenominatorDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxCombinadoNumeratorDataset;
import org.openmrs.module.eptsreports.reporting.library.queries.TxCombinadoQueries;
import org.openmrs.module.eptsreports.reporting.utils.TxCombinadoType;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

/** @author Stélio Moiane */
public class TxCombinadoCohortDefinitionTest extends DefinitionsFGHLiveTest {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private TxCombinadoNumeratorDataset numeratorDataset;

  @Autowired private TxCombinadoDenominatorDataset denominatorDataset;

  private Map<Parameter, Object> parameters;

  @Before
  public void setup() {
    final Location location = Context.getLocationService().getLocation(290);
    final Date startDate = DateUtil.getDateTime(2020, 9, 21);
    final Date endDate = DateUtil.getDateTime(2021, 9, 20);
    final Integer month = 12;

    this.parameters = new HashMap<>();

    this.parameters.put(new Parameter("startDate", "Start Date", Date.class), startDate);

    this.parameters.put(new Parameter("endDate", "End Date", Date.class), endDate);

    this.parameters.put(new Parameter("location", "Location", Location.class), location);

    this.parameters.put(
        new Parameter("months", "Número de Meses (12, 24, 36)", Integer.class), month);
  }

  @Test
  public void shouldFindPatientsNewlyEnrolledInART() throws EvaluationException {

    final TxCombinadoNumeratorDataset txCombinadoNumeratorDataset =
        new TxCombinadoNumeratorDataset();

    txCombinadoNumeratorDataset.setGenericCohortQueries(this.genericCohortQueries);

    final CohortDefinition txNewCompositionCohort =
        txCombinadoNumeratorDataset.numeratorCohortDefinition();

    final EvaluatedCohort evaluateCohortDefinition =
        this.evaluateCohortDefinition(txNewCompositionCohort, this.parameters);

    Assert.assertFalse(evaluateCohortDefinition.getMemberIds().isEmpty());
    Assert.assertEquals(2906, evaluateCohortDefinition.getMemberIds().size());
  }

  @Test
  public void shouldFindPatientsWhoArePregnant() throws EvaluationException {

    final CohortDefinition cohortDefinition =
        this.genericCohortQueries.generalSql(
            "Pregnant",
            TxCombinadoQueries.QUERY.findPatientsWhoPregnant(TxCombinadoType.DENOMINATOR));

    final EvaluatedCohort evaluateCohortDefinition =
        this.evaluateCohortDefinition(cohortDefinition, this.parameters);

    Assert.assertFalse(evaluateCohortDefinition.getMemberIds().isEmpty());
    Assert.assertEquals(114, evaluateCohortDefinition.getMemberIds().size());
  }

  @Test
  public void shouldFindPatientsWhoAreBreastFeeding() throws EvaluationException {

    final CohortDefinition cohortDefinition =
        this.genericCohortQueries.generalSql(
            "breastfeeding",
            TxCombinadoQueries.QUERY.findPatientsWhoAreBreastFeeding(TxCombinadoType.NUMERATOR));

    final EvaluatedCohort evaluateCohortDefinition =
        this.evaluateCohortDefinition(cohortDefinition, this.parameters);

    Assert.assertFalse(evaluateCohortDefinition.getMemberIds().isEmpty());
    Assert.assertEquals(194, evaluateCohortDefinition.getMemberIds().size());
  }

  @Test
  public void shouldEvaluateNumeratorDataSet() throws EvaluationException {

    final DataSetDefinition numeratorDataset =
        this.numeratorDataset.constructTxCombinadoNumeratorDataset();

    final DataSet evaluateDatasetDefinition =
        this.evaluateDatasetDefinition(numeratorDataset, this.parameters);

    Assert.assertNotNull(evaluateDatasetDefinition.getMetaData().getColumns());
  }

  @Test
  public void shouldEvaluateDenominatorDataSet() throws EvaluationException {

    final DataSetDefinition numeratorDataset =
        this.denominatorDataset.constructTxCombinadoDenominatorDataset();

    final DataSet evaluateDatasetDefinition =
        this.evaluateDatasetDefinition(numeratorDataset, this.parameters);

    Assert.assertNotNull(evaluateDatasetDefinition.getMetaData().getColumns());
  }

  @Override
  protected String username() {
    return "admin";
  }

  @Override
  protected String password() {
    return "Ic@pSIS2021";
  }
}
