/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.DSDQueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.SearchPatientsQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.SearchReason;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchPatientsCohortQueries {

  @Autowired private GenericCohortQueries genericCohorts;

  public CohortDefinition findFoundPatients() {

    final SqlCohortDefinition cohortDefinition = new SqlCohortDefinition();

    cohortDefinition.setName("FOUND PATIENTS");
    cohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    cohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    cohortDefinition.setQuery(SearchPatientsQueries.QUERY.findFoundPatients);

    return cohortDefinition;
  }

  public CohortDefinition findPatientsNotfoundInSearch() {

    final SqlCohortDefinition cohortDefinition = new SqlCohortDefinition();

    cohortDefinition.setName("PATIENTS NOT FOUND");
    cohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    cohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    cohortDefinition.setQuery(SearchPatientsQueries.QUERY.findPatientNotFoundInSearch);

    return cohortDefinition;
  }

  public CohortDefinitionDimension findPatientsByRange(final String name, final AgeRange range) {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName(name);
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    String query = DSDQueriesInterface.QUERY.findPatientsAgeRange;
    query = String.format(query, range.getMin(), range.getMax());

    if (AgeRange.ADULT.equals(range)) {

      query =
          query.replace(
              "BETWEEN " + range.getMin() + " AND " + range.getMax(), " >= " + range.getMax());
    }

    dimension.addCohortDefinition(
        name,
        EptsReportUtils.map(
            this.genericCohorts.generalSql("findPatientsByRange", query), "endDate=${endDate}"));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsByAbsenseReason(
      final String name, final SearchReason reason) {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    String query = SearchPatientsQueries.QUERY.findFoundPatientsByAbsenseReason;
    query = query.replace(":reason", String.valueOf(reason.getReason()));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        name,
        EptsReportUtils.map(this.genericCohorts.generalSql("PatientsByReason", query), mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsNotFoundInSearchByReason(
      final String name, final SearchReason reason) {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    String query = SearchPatientsQueries.QUERY.findPatientNotFoundInSearchByReason;
    query = query.replace(":reason", String.valueOf(reason.getReason()));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        name,
        EptsReportUtils.map(this.genericCohorts.generalSql("PatientsByReason", query), mappings));

    return dimension;
  }
}
