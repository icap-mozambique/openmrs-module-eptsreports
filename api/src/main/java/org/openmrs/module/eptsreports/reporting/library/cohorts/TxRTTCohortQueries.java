/** */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxRTTPatientsWhoAreTransferedOutCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxRTTPatientsWhoExperiencedIITCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxRttBetween3To5MonthsListCalculator;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxRttGreaterThan6MonthsListCalculator;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxRttLessThan3MonthsListCalculator;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxRttListCalculator;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseIcapCalculationDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.TxNewQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxRttQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class TxRTTCohortQueries {

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @Autowired private TRFINCohortQueries tRFINCohortQueries;

  @Autowired private GenericCohortQueries genericCohorts;

  private static final String FIND_PATIENTS_WHO_ARE_IIT_PREVIOUS_PERIOD =
      "TX_RTT/PATIENTS_WHO_ARE_IIT_PREVIOUS_PERIOD.sql";

  private static final String FIND_PATIENTS_WITH_CD4 =
      "TX_RTT/PATIENTS_IIT_PREVIOUS_PERIOD_WITH_CD4.sql";

  private static final String FIND_PATIENTS_NOT_ELIGIBLE_TO_CD4 =
      "TX_RTT/PATIENTS_IIT_PREVIOUS_PERIOD_NOT_ELIGIBLE_TO_CD4.sql";

  private static final String FIND_AGE_PATIENTS_ON_STAGE_3_4 =
      "TX_NEW/PATIENTS_WHO_ARE_IN_STAGE_3_OR_4.sql";

  final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

  @DocumentedDefinition(value = "TxRttPatientsOnRTT")
  public CohortDefinition getPatientsOnRTT() {

    final CompositionCohortDefinition composition = new CompositionCohortDefinition();

    composition.setName("Tx RTT - Patients on RTT");
    composition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    composition.addParameter(new Parameter("endDate", "End Date", Date.class));
    composition.addParameter(new Parameter("location", "location", Location.class));

    composition.addSearch(
        "IIT-PREVIOUS-PERIOD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Patients who experienced interruption in treatment by end of previous reporting period",
                EptsQuerysUtils.loadQuery(
                    TxRTTCohortQueries.FIND_PATIENTS_WHO_ARE_IIT_PREVIOUS_PERIOD)),
            this.mappings));

    composition.addSearch(
        "RTT-TRANFERRED-OUT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Patients who experienced interruption in treatment by end of previous reporting period",
                TxRttQueries.QUERY
                    .findPatientsWhoWhereTransferredOutByEndOfPreviousReportingPeriod),
            this.mappings));

    composition.addSearch(
        "TX-CURR",
        EptsReportUtils.map(
            this.txCurrCohortQueries.findPatientsWhoAreActiveOnART(),
            "endDate=${endDate},location=${location}"));

    composition.addSearch(
        "TRF-IN",
        EptsReportUtils.map(
            this.tRFINCohortQueries.getPatiensWhoAreTransferredIn(), this.mappings));

    composition.setCompositionString(
        "((IIT-PREVIOUS-PERIOD NOT RTT-TRANFERRED-OUT) AND TX-CURR) NOT TRF-IN");

    return composition;
  }

  public CohortDefinition findPatientsWithCD4LessThan200() {
    final CompositionCohortDefinition composition = new CompositionCohortDefinition();

    composition.setName("CD4 LESS THAN 200");
    composition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    composition.addParameter(new Parameter("endDate", "End Date", Date.class));
    composition.addParameter(new Parameter("location", "location", Location.class));

    final String query =
        String.format(
            EptsQuerysUtils.loadQuery(TxRTTCohortQueries.FIND_PATIENTS_WITH_CD4),
            " coorte_final.data_cd4 is not null and  (coorte_final.data_cd4_greater is null or coorte_final.data_cd4_greater >= coorte_final.data_cd4 ) ");

    composition.addSearch(
        "CD4-LESS-200",
        EptsReportUtils.map(
            this.genericCohorts.generalSql("findPatientsWithCD4LessThan200", query),
            this.mappings));

    composition.addSearch("RTT", EptsReportUtils.map(this.getPatientsOnRTT(), this.mappings));

    composition.addSearch(
        "CD4-NOT-ELIGIBLE",
        EptsReportUtils.map(this.findPatientsNotEligibleToCD4(), this.mappings));

    composition.setCompositionString("(CD4-LESS-200 AND RTT) NOT CD4-NOT-ELIGIBLE");

    return composition;
  }

  public CohortDefinition findPatientsWIthCD4GreaterOrEqual200() {
    final CompositionCohortDefinition composition = new CompositionCohortDefinition();

    composition.setName("CD4 GREATER OR EQUAL 200");
    composition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    composition.addParameter(new Parameter("endDate", "End Date", Date.class));
    composition.addParameter(new Parameter("location", "location", Location.class));

    final String query =
        String.format(
            EptsQuerysUtils.loadQuery(TxRTTCohortQueries.FIND_PATIENTS_WITH_CD4),
            " coorte_final.data_cd4_greater is not null and ( coorte_final.data_cd4 is null or coorte_final.data_cd4_greater < coorte_final.data_cd4) ");

    composition.addSearch(
        "CD4-GREATER-OR-EQUAL-200",
        EptsReportUtils.map(
            this.genericCohorts.generalSql("findPatientsWIthCD4GreaterOrEqual200", query),
            this.mappings));

    composition.addSearch(
        "CD4-NOT-ELIGIBLE",
        EptsReportUtils.map(this.findPatientsNotEligibleToCD4(), this.mappings));
    composition.addSearch("RTT", EptsReportUtils.map(this.getPatientsOnRTT(), this.mappings));

    composition.setCompositionString("(CD4-GREATER-OR-EQUAL-200 AND RTT) NOT CD4-NOT-ELIGIBLE");

    return composition;
  }

  public CohortDefinition findPatientsWithUnknownCD4() {
    final CompositionCohortDefinition composition = new CompositionCohortDefinition();

    composition.setName("Unkwown CD4");
    composition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    composition.addParameter(new Parameter("endDate", "End Date", Date.class));
    composition.addParameter(new Parameter("location", "location", Location.class));

    composition.addSearch("RTT", EptsReportUtils.map(this.getPatientsOnRTT(), this.mappings));

    composition.addSearch(
        "CD4-LESS-200", EptsReportUtils.map(this.findPatientsWithCD4LessThan200(), this.mappings));

    composition.addSearch(
        "CD4-GREATER-OR-EQUAL-200",
        EptsReportUtils.map(this.findPatientsWIthCD4GreaterOrEqual200(), this.mappings));

    composition.addSearch(
        "CD4-NOT-ELIGIBLE",
        EptsReportUtils.map(this.findPatientsNotEligibleToCD4(), this.mappings));

    composition.setCompositionString(
        "RTT NOT (CD4-LESS-200 OR CD4-GREATER-OR-EQUAL-200 OR CD4-NOT-ELIGIBLE)");

    return composition;
  }

  public CohortDefinition findPatientsNotEligibleToCD4() {
    final CompositionCohortDefinition composition = new CompositionCohortDefinition();

    composition.setName("CD4 GREATER OR EQUAL 200");
    composition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    composition.addParameter(new Parameter("endDate", "End Date", Date.class));
    composition.addParameter(new Parameter("location", "location", Location.class));

    composition.addSearch("RTT", EptsReportUtils.map(this.getPatientsOnRTT(), this.mappings));

    composition.addSearch(
        "CD4-NOT-ELIGIBLE",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsNotEligibleToCD4",
                EptsQuerysUtils.loadQuery(TxRTTCohortQueries.FIND_PATIENTS_NOT_ELIGIBLE_TO_CD4)),
            this.mappings));

    composition.setCompositionString("RTT AND CD4-NOT-ELIGIBLE");

    return composition;
  }

  @DocumentedDefinition(value = "TxRttPatientsOnRTT")
  public CohortDefinition getCommunityPatientsOnRTT() {

    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();

    compositionDefinition.setName("Tx RTT Community - Patients on RTT");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compositionDefinition.addSearch("RTT", EptsReportUtils.map(this.getPatientsOnRTT(), mappings));

    compositionDefinition.addSearch(
        "WITH-COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedARTWithComunnityDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    compositionDefinition.setCompositionString("RTT AND WITH-COMMUNITY-DISPENSATION");

    return compositionDefinition;
  }

  public CohortDefinition findPatientsWithCD4LessThan200Community() {
    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();

    compositionDefinition.setName("CD4 GREATER OR EQUAL 200");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compositionDefinition.addSearch(
        "CD4-LESS-200", EptsReportUtils.map(this.findPatientsWithCD4LessThan200(), mappings));

    compositionDefinition.addSearch(
        "WITH-COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedARTWithComunnityDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    compositionDefinition.setCompositionString("CD4-LESS-200 AND WITH-COMMUNITY-DISPENSATION");

    return compositionDefinition;
  }

  public CohortDefinition findPatientsNotEligibleToCD4Community() {
    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();

    compositionDefinition.setName("CD4 GREATER OR EQUAL 200");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compositionDefinition.addSearch(
        "CD4-NOT-ELIGIBLE", EptsReportUtils.map(this.findPatientsNotEligibleToCD4(), mappings));

    compositionDefinition.addSearch(
        "WITH-COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedARTWithComunnityDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    compositionDefinition.setCompositionString("CD4-NOT-ELIGIBLE AND WITH-COMMUNITY-DISPENSATION");

    return compositionDefinition;
  }

  public CohortDefinition findPatientsWithUnknownCD4Community() {
    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();

    compositionDefinition.setName("CD4 UNKNOWN");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compositionDefinition.addSearch(
        "CD4-UNKNOWN", EptsReportUtils.map(this.findPatientsWithUnknownCD4(), mappings));

    compositionDefinition.addSearch(
        "WITH-COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedARTWithComunnityDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    compositionDefinition.setCompositionString("CD4-UNKNOWN AND WITH-COMMUNITY-DISPENSATION");

    return compositionDefinition;
  }

  public CohortDefinition findPatientsWIthCD4GreaterOrEqual200Community() {
    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();

    compositionDefinition.setName("CD4 GREATER OR EQUAL 200");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compositionDefinition.addSearch(
        "CD4-GREATER-OR-EQUAL-200",
        EptsReportUtils.map(this.findPatientsWIthCD4GreaterOrEqual200(), mappings));

    compositionDefinition.addSearch(
        "WITH-COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedARTWithComunnityDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    compositionDefinition.setCompositionString(
        "CD4-GREATER-OR-EQUAL-200 AND WITH-COMMUNITY-DISPENSATION");

    return compositionDefinition;
  }

  @DocumentedDefinition(value = "TxRttPatientsWhoExperiencedIITCalculation")
  public CohortDefinition getPatientsWhoExperiencedIITCalculation() {
    final BaseFghCalculationCohortDefinition definition =
        new BaseFghCalculationCohortDefinition(
            "txRTTPatientsWhoExperiencedIITCalculation",
            Context.getRegisteredComponents(TxRTTPatientsWhoExperiencedIITCalculation.class)
                .get(0));
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("realEndDate", "Real End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    return definition;
  }

  @DocumentedDefinition(value = "TxRttPatientsWhoWhereTransferredOutCalculation")
  public CohortDefinition getPatientsWhoWhereTransferredOutCalculation() {
    final BaseFghCalculationCohortDefinition definition =
        new BaseFghCalculationCohortDefinition(
            "txRTTPatientsWhoWhereTransferredOutCalculation",
            Context.getRegisteredComponents(TxRTTPatientsWhoAreTransferedOutCalculation.class)
                .get(0));
    definition.addParameter(new Parameter("endDate", "end Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }

  @DocumentedDefinition(value = "DurationInterruptionOfTreatmentLessThan3Months")
  public CohortDefinition getDurationInterruptionOfTreatmentLessThan3Months() {
    return this.getDurationofIITInterval(
        "Patients who experienced treatment interruption of  <3 months before returning to treatment",
        "  where iit_art_interval < 90 ");
  }

  @DocumentedDefinition(value = "DurationInterruptionOfTreatmentBetween3And5Months")
  public CohortDefinition getDurationInterruptionOfTreatmentBetween3And5Months() {
    return this.getDurationofIITInterval(
        "Patients who experienced treatment interruption of 3-5 months before returning to treatmentt",
        "  where iit_art_interval >= 90 and iit_art_interval < 180 ");
  }

  @DocumentedDefinition(value = "DurationInterruptionOfTreatmentGreaterOrEqual6Months")
  public CohortDefinition getDurationInterruptionOfTreatmentGreaterOrEqual6Months() {
    return this.getDurationofIITInterval(
        "Patients who experienced treatment interruption of 6 or more months before returning to treatment",
        "  where iit_art_interval >= 180 ");
  }

  @DocumentedDefinition(value = "TxRttPLHIVLess12MonthCalculation")
  public CohortDefinition getPLHIVLess12MonthCalculation() {
    return this.getDurationofIITInterval(
        "Patients who experienced treatment interruption of  <12 months before returning to treatment",
        "  where iit_art_interval < 365 ");
  }

  @DocumentedDefinition(value = "TxRttPLHIVGreater12MonthCalculation")
  public CohortDefinition getPLHIVGreather12MonthCalculation() {
    return this.getDurationofIITInterval(
        "Patients who experienced treatment interruption of  12 or more months before returning to treatment",
        "  where iit_art_interval >= 365 ");
  }

  // PFACT
  @DocumentedDefinition(value = "TxRttPLHIVGreater12MonthCalculation")
  public CohortDefinition getPLHIVGreather12MonthCalculationAndCD4Under200() {
    return this.getDurationofIITIntervalANDCD4Under200(
        "Patients who experienced treatment interruption of  12 or more months before returning to treatment",
        "  where iit_art_interval >= 365 ");
  }

  @DocumentedDefinition(value = "TxRttPLHIVGreater12MonthCalculation")
  public CohortDefinition getPLHIVGreather12MonthCalculationAndStage3_4() {
    return this.getDurationofIITIntervalANDStage3Or4(
        "Patients who experienced treatment interruption of  12 or more months before returning to treatment",
        "  where iit_art_interval >= 365 ");
  }

  @DocumentedDefinition(value = "TxRttPLHIVUnknownDesaggregation")
  public CohortDefinition getPLHIVUnknownDesaggregation() {

    return this.getDurationofIITInterval(
        "Patients who experienced - Unknown Duration",
        " where data_iit is null and  data_restart is not null ");
  }

  // CommunityITT

  @DocumentedDefinition(value = "DurationInterruptionOfTreatmentLessThan3Months")
  public CohortDefinition getDurationInterruptionOfTreatmentLessThan3MonthsCommunity() {
    return this.getDurationofIITIntervalCommunity(
        "Patients who experienced treatment interruption of  <3 months before returning to treatment",
        "  where iit_art_interval < 90 ");
  }

  @DocumentedDefinition(value = "DurationInterruptionOfTreatmentBetween3And5Months")
  public CohortDefinition getDurationInterruptionOfTreatmentBetween3And5MonthsCommunity() {
    return this.getDurationofIITIntervalCommunity(
        "Patients who experienced treatment interruption of 3-5 months before returning to treatmentt",
        "  where iit_art_interval >= 90 and iit_art_interval < 180 ");
  }

  @DocumentedDefinition(value = "DurationInterruptionOfTreatmentGreaterOrEqual6Months")
  public CohortDefinition getDurationInterruptionOfTreatmentGreaterOrEqual6MonthsCommunity() {
    return this.getDurationofIITIntervalCommunity(
        "Patients who experienced treatment interruption of 6 or more months before returning to treatment",
        "  where iit_art_interval >= 180 ");
  }

  @DocumentedDefinition(value = "TxRttPLHIVLess12MonthCalculation")
  public CohortDefinition getPLHIVLess12MonthCalculationCommunity() {
    return this.getDurationofIITIntervalCommunity(
        "Patients who experienced treatment interruption of  <12 months before returning to treatment",
        "  where iit_art_interval < 365 ");
  }

  @DocumentedDefinition(value = "TxRttPLHIVGreater12MonthCalculation")
  public CohortDefinition getPLHIVGreather12MonthCalculationCommunity() {
    return this.getDurationofIITIntervalCommunity(
        "Patients who experienced treatment interruption of  12 or more months before returning to treatment",
        "  where iit_art_interval >= 365 ");
  }

  @DocumentedDefinition(value = "TxRttPLHIVUnknownDesaggregation")
  public CohortDefinition getPLHIVUnknownDesaggregationCommunity() {

    return this.getDurationofIITIntervalCommunity(
        "Patients who experienced - Unknown Duration",
        " where data_iit is null and  data_restart is not null ");
  }

  @DocumentedDefinition(value = "TxRttPLHIVTotal")
  public CohortDefinition getPLHIVTotal() {

    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();

    compositionDefinition.setName("Tx RTT- Total PLHIV");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionDefinition.addSearch(
        "RTT-GREATER12MONTHS",
        EptsReportUtils.map(this.getPLHIVGreather12MonthCalculation(), this.mappings));

    compositionDefinition.addSearch(
        "RTT-LESS12MONTHS",
        EptsReportUtils.map(this.getPLHIVLess12MonthCalculation(), this.mappings));

    compositionDefinition.addSearch(
        "RTT-PLHIVUNKNOWN",
        EptsReportUtils.map(this.getPLHIVUnknownDesaggregation(), this.mappings));

    compositionDefinition.setCompositionString(
        "RTT-LESS12MONTHS OR RTT-GREATER12MONTHS OR RTT-PLHIVUNKNOWN");

    return compositionDefinition;
  }

  @DocumentedDefinition(value = "TxRttPLHIVTotal")
  public CohortDefinition getPLHIVTotalCommunity() {

    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();

    compositionDefinition.setName("Tx RTT- Total PLHIV Community");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionDefinition.addSearch(
        "TOTAL", EptsReportUtils.map(this.getPLHIVTotal(), this.mappings));

    compositionDefinition.addSearch(
        "WITH-COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedARTWithComunnityDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    compositionDefinition.setCompositionString("TOTAL AND WITH-COMMUNITY-DISPENSATION");

    return compositionDefinition;
  }

  private CohortDefinition getDurationofIITInterval(
      final String intervalLabel, final String interval) {
    final CompositionCohortDefinition composition = new CompositionCohortDefinition();

    composition.setName("IIT -" + intervalLabel);
    composition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    composition.addParameter(new Parameter("endDate", "End Date", Date.class));
    composition.addParameter(new Parameter("location", "location", Location.class));

    composition.addSearch("RTT", EptsReportUtils.map(this.getPatientsOnRTT(), this.mappings));

    final String query =
        EptsQuerysUtils.loadQuery(TxRTTCohortQueries.FIND_PATIENTS_WHO_ARE_IIT_PREVIOUS_PERIOD)
            + interval;

    composition.addSearch(
        "IIT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Patients who experienced treatment interruption of  <3 months before returning to treatment",
                query),
            this.mappings));

    composition.setCompositionString("RTT AND IIT");

    return composition;
  }

  // PFACTS
  private CohortDefinition getDurationofIITIntervalANDCD4Under200(
      final String intervalLabel, final String interval) {
    final CompositionCohortDefinition composition = new CompositionCohortDefinition();

    composition.setName("IIT -" + intervalLabel);
    composition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    composition.addParameter(new Parameter("endDate", "End Date", Date.class));
    composition.addParameter(new Parameter("location", "location", Location.class));

    composition.addSearch(
        "RTT", EptsReportUtils.map(this.findPatientsWithCD4LessThan200(), this.mappings));

    final String query =
        EptsQuerysUtils.loadQuery(TxRTTCohortQueries.FIND_PATIENTS_WHO_ARE_IIT_PREVIOUS_PERIOD)
            + interval;

    composition.addSearch(
        "IIT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Patients who experienced treatment interruption of  <3 months before returning to treatment",
                query),
            this.mappings));

    composition.setCompositionString("RTT AND IIT");

    return composition;
  }

  private CohortDefinition getDurationofIITIntervalANDStage3Or4(
      final String intervalLabel, final String interval) {
    final CompositionCohortDefinition composition = new CompositionCohortDefinition();

    composition.setName("IIT -" + intervalLabel);
    composition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    composition.addParameter(new Parameter("endDate", "End Date", Date.class));
    composition.addParameter(new Parameter("location", "location", Location.class));

    composition.addSearch(
        "IIT",
        EptsReportUtils.map(this.getDurationofIITInterval(intervalLabel, interval), this.mappings));

    composition.addSearch(
        "STAGE",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsStage3OR4",
                EptsQuerysUtils.loadQuery(TxRTTCohortQueries.FIND_AGE_PATIENTS_ON_STAGE_3_4)),
            mappings));

    composition.setCompositionString("STAGE AND IIT");

    return composition;
  }

  private CohortDefinition getDurationofIITIntervalCommunity(
      final String intervalLabel, final String interval) {
    final CompositionCohortDefinition composition = new CompositionCohortDefinition();

    composition.setName("IIT -" + intervalLabel);
    composition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    composition.addParameter(new Parameter("endDate", "End Date", Date.class));
    composition.addParameter(new Parameter("location", "location", Location.class));

    composition.addSearch(
        "IIT",
        EptsReportUtils.map(this.getDurationofIITInterval(intervalLabel, interval), this.mappings));

    composition.addSearch(
        "WITH-COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedARTWithComunnityDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    composition.setCompositionString("IIT AND WITH-COMMUNITY-DISPENSATION ");

    return composition;
  }

  @DocumentedDefinition(value = "TxRttList")
  public DataSetDefinition getTxRttList() {

    final DataSetDefinition definition =
        new BaseIcapCalculationDataSetDefinition(
            "TxRttList", Context.getRegisteredComponents(TxRttListCalculator.class).get(0));

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("realEndDate", "Real End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }

  @DocumentedDefinition(value = "TxRttLessThan3MonthsList")
  public DataSetDefinition getTxRttLessThan3MonthsList() {

    final DataSetDefinition definition =
        new BaseIcapCalculationDataSetDefinition(
            "TxRttLessThan3MonthsList",
            Context.getRegisteredComponents(TxRttLessThan3MonthsListCalculator.class).get(0));

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("realEndDate", "Real End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }

  @DocumentedDefinition(value = "TxRttBetween3To5MonthsList")
  public DataSetDefinition getTxRttBetween3To5MonthsList() {

    final DataSetDefinition definition =
        new BaseIcapCalculationDataSetDefinition(
            "TxRttBetween3To5MonthsList",
            Context.getRegisteredComponents(TxRttBetween3To5MonthsListCalculator.class).get(0));

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("realEndDate", "Real End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }

  @DocumentedDefinition(value = "TxRttGreaterTo6MonthsList")
  public DataSetDefinition getTxRttGreaterThan6MonthsList() {

    final DataSetDefinition definition =
        new BaseIcapCalculationDataSetDefinition(
            "TxRttGreaterTo6MonthsList",
            Context.getRegisteredComponents(TxRttGreaterThan6MonthsListCalculator.class).get(0));

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("realEndDate", "Real End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }
}
