package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsFGHLiveTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory12P2CohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

/** @author Stélio Moiane */
public class TxNewCohortDefinitionTest extends DefinitionsFGHLiveTest {

  //  @Autowired private MQCohortQueries mqCohortQueries;
  @Autowired private MQCategory12P2CohortQueries mQCategory13Section1CohortQueries;

  @Test
  public void shouldFindPatientsNewlyEnrolledInART() throws EvaluationException {

    final Location location = Context.getLocationService().getLocation(271);
    final Date startDate = DateUtil.getDateTime(2020, 1, 1);
    final Date endDate = DateUtil.getDateTime(2020, 3, 31);
    final Date revisionDate = DateUtil.getDateTime(2020, 12, 31);

    final CohortDefinition txNewCompositionCohort =
        this.mQCategory13Section1CohortQueries
            .findPatientsInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line64ColumnDDenominator();

    final Map<Parameter, Object> parameters = new HashMap<>();

    parameters.put(new Parameter("startInclusionDate", "Start Date", Date.class), startDate);

    parameters.put(new Parameter("endInclusionDate", "End Date", Date.class), endDate);

    parameters.put(new Parameter("endRevisionDate", "End Date", Date.class), revisionDate);

    parameters.put(new Parameter("location", "Location", Location.class), location);

    final EvaluatedCohort evaluateCohortDefinition =
        this.evaluateCohortDefinition(txNewCompositionCohort, parameters);

    Assert.assertFalse(evaluateCohortDefinition.getMemberIds().isEmpty());
    //    System.out.println(evaluateCohortDefinition.size());
    //        System.out.println(evaluateCohortDefinition.getMemberIds());

    for (final int t : evaluateCohortDefinition.getMemberIds()) {
      System.out.println(t);
    }
  }

  @Override
  protected String username() {
    return "admin";
  }

  @Override
  protected String password() {
    return "eSaude123";
  }
}
