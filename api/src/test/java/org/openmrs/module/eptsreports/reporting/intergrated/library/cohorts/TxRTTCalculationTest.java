package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsFGHLiveTest;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

public class TxRTTCalculationTest extends DefinitionsFGHLiveTest {

  @Test
  public void shouldFindPatientsNewlyEnrolledInART() throws EvaluationException {

    final Location location = Context.getLocationService().getLocation(311);

    System.out.println(location.getName());
    final Date startDate = DateUtil.getDateTime(2023, 03, 21);
    final Date endDate = DateUtil.getDateTime(2023, 06, 20);

    System.out.println(startDate);
    System.out.println(endDate);

    final Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), startDate);
    parameters.put(new Parameter("endDate", "End Date", Date.class), endDate);
    parameters.put(new Parameter("location", "Location", Location.class), location);
    parameters.put(new Parameter("months", "Months", Integer.class), 12);

    //		final DataSetDefinition definition =
    // this.txRTTCohortQueries.getTxCombinationListNumerator();

    //		final DataSet dataSet = this.evaluateDatasetDefinition(definition, parameters);

    //		System.out.println(dataSet.getMetaData().getColumns().size());
    //		Assert.assertFalse(dataSet.getMetaData().getColumns().isEmpty());
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
