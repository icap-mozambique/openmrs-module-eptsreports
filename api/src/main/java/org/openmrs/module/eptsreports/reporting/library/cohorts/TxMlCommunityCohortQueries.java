package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.TxNewQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** All queries needed for TxMl report needed for EPTS project */
@Component
public class TxMlCommunityCohortQueries {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private TxMlCohortQueries txMlCohortQueries;

  @DocumentedDefinition(value = "patientsWhoMissedNextApointment")
  public CohortDefinition getPatientsWhoMissedNextApointment() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.setName("patientsWhoMissedNextApointment");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.addSearch(
        "PATIENTS-WHO-MISSED-NEXT-APOINTMENT",
        EptsReportUtils.map(this.txMlCohortQueries.getPatientsWhoMissedNextApointment(), mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    definition.setCompositionString(
        "PATIENTS-WHO-MISSED-NEXT-APOINTMENT AND COMMUNITY-DISPENSATION");

    return definition;
  }

  public CohortDefinition getPatientsWhoAreIITLessThan3Months() {
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("Get patients who are LTFU less than 3 months");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    definition.addSearch(
        "PATIENTS-WHO-ARE-IIT-LESS-THAN-3-MONTHS",
        EptsReportUtils.map(
            this.txMlCohortQueries.getPatientsWhoAreIITLessThan3Months(), mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    definition.setCompositionString(
        "PATIENTS-WHO-ARE-IIT-LESS-THAN-3-MONTHS AND COMMUNITY-DISPENSATION");

    return definition;
  }

  public CohortDefinition getPatientsWhoAreIITBetween3And5Months() {
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("Get patients who are LTFU Greater than 3 months And Less Than 6 Months");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    definition.addSearch(
        "PATIENTS-WHO-ARE-IIT-BETWEEN-3-AND-5-MONTHS",
        EptsReportUtils.map(
            this.txMlCohortQueries.getPatientsWhoAreIITBetween3And5Months(), mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    definition.setCompositionString(
        "PATIENTS-WHO-ARE-IIT-BETWEEN-3-AND-5-MONTHS AND COMMUNITY-DISPENSATION");

    return definition;
  }

  public CohortDefinition getPatientsWhoAreIITGreaterOrEqual6Months() {
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("Get patients who are LTFU less than 6 months");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    definition.addSearch(
        "PATIENTS-WHO-ARE-IIT-GREATER-OR-EQUAL-6-MONTHS",
        EptsReportUtils.map(
            this.txMlCohortQueries.getPatientsWhoAreIITGreaterOrEqual6Months(), mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    definition.setCompositionString(
        "PATIENTS-WHO-ARE-IIT-GREATER-OR-EQUAL-6-MONTHS AND COMMUNITY-DISPENSATION");

    return definition;
  }
}
