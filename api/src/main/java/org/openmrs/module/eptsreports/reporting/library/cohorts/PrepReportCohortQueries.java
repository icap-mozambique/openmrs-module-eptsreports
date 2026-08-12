/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrepReportCohortQueries {

  private static final String FIND_PATIENTS_ON_PREP_INJECTABLE =
      "PREP/FIND_PATIENTS_ON_PREP_INJECTABLE.sql";

  private static final String FIND_PREP_KP = "PREP/PREP_CDC_KP.sql";

  private static final String FIND_PREGNANT_BF = "PREP/PREP_CDC_PREGNANT_BF.sql";

  @Autowired private GenericCohortQueries genericCohorts;

  @DocumentedDefinition(value = "findPatientsOnPrepInjectable")
  public CohortDefinition findPatientsOnPrepInjectable() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("findPatientsOnPrepInjectable");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "PREP",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Find patients on PrEP injectable",
                EptsQuerysUtils.loadQuery(
                    PrepReportCohortQueries.FIND_PATIENTS_ON_PREP_INJECTABLE)),
            mappings));

    definition.setCompositionString("PREP");

    return definition;
  }

  public CohortDefinitionDimension getPrepKp(final PrepKp kp) {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("PrEP KP " + kp.getName());
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        kp.getName(),
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                kp.getName(), this.getQuery(PrepReportCohortQueries.FIND_PREP_KP, kp)),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension getPregnantAndBF(final PrepKp kp) {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("PrEP Pregnant and BF " + kp.getName());
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        kp.getName(),
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                kp.getName(), this.getQuery(PrepReportCohortQueries.FIND_PREGNANT_BF, kp)),
            mappings));

    return dimension;
  }

  private String getQuery(final String query_file, final PrepKp kp) {
    String query = EptsQuerysUtils.loadQuery(query_file);

    query = query.replace(":id", kp.getId() + "");

    return query;
  }
}
