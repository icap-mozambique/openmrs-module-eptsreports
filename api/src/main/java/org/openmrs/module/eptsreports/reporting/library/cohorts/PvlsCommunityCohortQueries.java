package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.TxNewQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxPvlsQueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.TxPvlsQueriesInterface.QUERY.WomanState;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PvlsCommunityCohortQueries {

  @Autowired private GenericCohortQueries genericCohorts;

  @DocumentedDefinition(
      value = "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months")
  public CohortDefinition
      findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    final String mappings = "endDate=${endDate},location=${location}";

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.addSearch(
        "VIRAL-LOAD-REGISTERED",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxPvlsQueriesInterface.QUERY
                    .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months),
            mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    definition.setCompositionString("VIRAL-LOAD-REGISTERED AND COMMUNITY-DISPENSATION");

    return definition;
  }

  @DocumentedDefinition(
      value = "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget")
  public CohortDefinition
      findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    final String mappings = "endDate=${endDate},location=${location}";

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.addSearch(
        "VIRAL-LOAD-REGISTERED-TARGET",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxPvlsQueriesInterface.QUERY
                    .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget),
            mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    definition.setCompositionString("VIRAL-LOAD-REGISTERED-TARGET AND COMMUNITY-DISPENSATION");

    return definition;
  }

  @DocumentedDefinition(
      value = "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine")
  public CohortDefinition
      findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "IART3MOTHSVL12MONTHSVL",
        EptsReportUtils.map(
            this.findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            mappings));

    definition.addSearch(
        "TARGET",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(),
            mappings));

    definition.setCompositionString("IART3MOTHSVL12MONTHSVL NOT TARGET");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months")
  public CohortDefinition
      findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    final String mappings = "endDate=${endDate},location=${location}";

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.addSearch(
        "VIRAL-LOAD-LESS-THAN1000",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months",
                TxPvlsQueriesInterface.QUERY
                    .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months),
            mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    definition.setCompositionString("VIRAL-LOAD-LESS-THAN1000 AND COMMUNITY-DISPENSATION");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget")
  public CohortDefinition
      findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    final String mappings = "endDate=${endDate},location=${location}";

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.addSearch(
        "VIRAL-LOAD-LESS-THAN1000-TARGET",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget",
                TxPvlsQueriesInterface.QUERY
                    .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget),
            mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    definition.setCompositionString("VIRAL-LOAD-LESS-THAN1000-TARGET AND COMMUNITY-DISPENSATION");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine")
  public CohortDefinition
      findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "IART3MOTHSVL12MONTHSVL1000",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months(),
            mappings));

    definition.addSearch(
        "TARGET1000",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget(),
            mappings));

    definition.setCompositionString("IART3MOTHSVL12MONTHSVL1000 NOT TARGET1000");

    return definition;
  }

  @DocumentedDefinition(
      value = "PregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months")
  public CohortDefinition
      findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "PregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        TxPvlsQueriesInterface.QUERY
            .findWomanStateWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(
                WomanState.PREGNANT));

    return definition;
  }

  @DocumentedDefinition(
      value =
          "BreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months")
  public CohortDefinition
      findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "BreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        TxPvlsQueriesInterface.QUERY
            .findWomanStateWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(
                WomanState.BREASTFEEDING));

    return definition;
  }

  @DocumentedDefinition(
      value =
          "PregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine")
  public CohortDefinition
      findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "PREGNANT3MONTHSVL12",
        EptsReportUtils.map(
            this
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            mappings));

    definition.addSearch(
        "ROTINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(),
            mappings));

    definition.setCompositionString("PREGNANT3MONTHSVL12 AND ROTINE");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantBreatsFeedingWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Rotine")
  public CohortDefinition
      findPregnantBreatsFeedingWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Rotine() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "IART3MONTHSVL12MONTHSROTINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine(),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this
                .findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            mappings));

    definition.setCompositionString("IART3MONTHSVL12MONTHSROTINE AND BREASTFEEDING");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantBreatsFeedingWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Target")
  public CohortDefinition
      findPregnantBreatsFeedingWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Target() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "IART3MONTHSVL12MONTHSTARGET",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget(),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this
                .findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    definition.setCompositionString(
        "IART3MONTHSVL12MONTHSTARGET AND BREASTFEEDING AND COMMUNITY-DISPENSATION");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsDenominator")
  public CohortDefinition
      findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsDenominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsDenominator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "PREGNANT3MONTHSVL12",
        EptsReportUtils.map(
            this
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            mappings));

    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(
            this.findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    definition.setCompositionString(
        "PREGNANT3MONTHSVL12 AND DENOMINATOR AND COMMUNITY-DISPENSATION");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsNumerator")
  public CohortDefinition
      findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsNumerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "PREGNANT3MONTHSVL12",
        EptsReportUtils.map(
            this
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            mappings));

    definition.addSearch(
        "NUMERATOR",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months(),
            mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    definition.setCompositionString("PREGNANT3MONTHSVL12 AND NUMERATOR AND COMMUNITY-DISPENSATION");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsDenominator")
  public CohortDefinition
      findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsDenominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsDenominator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "BREASTEFEEDING3MONTHSVL12",
        EptsReportUtils.map(
            this
                .findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            mappings));

    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(
            this.findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    definition.setCompositionString(
        "BREASTEFEEDING3MONTHSVL12 AND DENOMINATOR AND COMMUNITY-DISPENSATION");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsNumerator")
  public CohortDefinition
      findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsNumerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "BREASTEFEEDING3MONTHSVL12",
        EptsReportUtils.map(
            this
                .findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            mappings));

    definition.addSearch(
        "NUMERATOR",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months(),
            mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    definition.setCompositionString(
        "BREASTEFEEDING3MONTHSVL12 AND NUMERATOR AND COMMUNITY-DISPENSATION");

    return definition;
  }
}
