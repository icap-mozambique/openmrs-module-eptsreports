package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.CxCaTXQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.CxType;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CxCaTXCohortQueries {

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;
  @Autowired private CxCaSCRNCohortQueries CxCaSCRNCohortQueries;
  @Autowired private GenericCohortQueries genericCohortQueries;
  @Autowired private TxNewCohortQueries txNewCohortQueries;

  @DocumentedDefinition(value = "findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod")
  public CohortDefinition findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriod");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(CxType.ALL));

    return definition;
  }

  @DocumentedDefinition(
      value = "getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodTotalDenominator")
  public CohortDefinition getPatientsWhoReceivedTreatmentTypeDuringReportingPeriodTotalDenominator(
      final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodTotalDenominator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "TXCURR",
        EptsReportUtils.map(
            this.txCurrCohortQueries.findPatientsWhoAreActiveOnART(),
            "endDate=${endDate},location=${location}"));
    definition.addSearch(
        "CX-TX-DENOMINATOR",
        EptsReportUtils.map(
            this.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(), mappings));

    definition.setCompositionString("(TXCURR AND CX-TX-DENOMINATOR)");

    if (isCommunity) {
      final CohortDefinition communityDispensation =
          this.txNewCohortQueries.communityDispensation();

      definition.addSearch(
          "COMMUNITY-DISPENSATION",
          EptsReportUtils.map(
              communityDispensation,
              EptsReportUtils.removeMissingParameterMappingsFromCohortDefintion(
                  communityDispensation, mappings)));

      definition.setCompositionString("(TXCURR AND CX-TX-DENOMINATOR AND COMMUNITY-DISPENSATION)");
    }

    return definition;
  }

  @DocumentedDefinition(
      value = "getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreean")
  public CohortDefinition getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodFirstScreean(
      final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreean");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-TX-DENOMINATOR",
        EptsReportUtils.map(
            this.getPatientsWhoReceivedTreatmentTypeDuringReportingPeriodTotalDenominator(
                isCommunity),
            mappings));

    definition.addSearch(
        "PREVIOUS",
        EptsReportUtils.map(
            this.CxCaSCRNCohortQueries
                .findPatientsWithScreeningTestForCervicalCancerPreviousReportingPeriod(),
            mappings));

    definition.setCompositionString("(CX-TX-DENOMINATOR NOT PREVIOUS)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreeanCryotherapyDesagregations")
  public CohortDefinition
      getPatientsWhoReceivedTreatmentTypeDuringReportingPeriodFirstScreeanCryotherapyDesagregations(
          final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreean");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-TX-FIRST-SCREEN",
        EptsReportUtils.map(
            this.getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodFirstScreean(isCommunity),
            mappings));

    definition.addSearch(
        "CRYOTHERAPY",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "CRYOTHERAPY",
                CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
                    CxType.CRYOTHERAPY)),
            mappings));

    definition.setCompositionString("CX-TX-FIRST-SCREEN AND CRYOTHERAPY");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreeanThermocoagulationDesagregations")
  public CohortDefinition
      getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodFirstScreeanThermocoagulationDesagregations(
          final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreean");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-TX-FIRST-SCREEN",
        EptsReportUtils.map(
            this.getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodFirstScreean(isCommunity),
            mappings));

    definition.addSearch(
        "THERMOCOAGULATION",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "THERMOCOAGULATION",
                CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
                    CxType.THERMOCOAGULATION)),
            mappings));

    definition.setCompositionString("CX-TX-FIRST-SCREEN AND THERMOCOAGULATION");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreeanLeepDesagregations")
  public CohortDefinition
      getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodFirstScreeanLeepDesagregations(
          final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreean");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-TX-FIRST-SCREEN",
        EptsReportUtils.map(
            this.getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodFirstScreean(isCommunity),
            mappings));

    definition.addSearch(
        "LEEP",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "LEEP",
                CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
                    CxType.LEEP)),
            mappings));

    definition.setCompositionString("CX-TX-FIRST-SCREEN AND LEEP");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegative")
  public CohortDefinition
      getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegative(
          final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreean");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-TX-DENOMINATOR",
        EptsReportUtils.map(
            this.getPatientsWhoReceivedTreatmentTypeDuringReportingPeriodTotalDenominator(
                isCommunity),
            mappings));

    definition.addSearch(
        "NEGATIVE",
        EptsReportUtils.map(
            this.CxCaSCRNCohortQueries
                .findPatientWithScreeningTypeVisitAsRescreenedAfterPreviousNegative(),
            mappings));

    definition.setCompositionString("(CX-TX-DENOMINATOR AND NEGATIVE)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegativeCryotherapyDesagregations")
  public CohortDefinition
      getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegativeCryotherapyDesagregations(
          final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreean");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-RN",
        EptsReportUtils.map(
            this
                .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegative(
                    isCommunity),
            mappings));

    definition.addSearch(
        "CRYOTHERAPY",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "CRYOTHERAPY",
                CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
                    CxType.CRYOTHERAPY)),
            mappings));

    definition.setCompositionString("(CX-RN AND CRYOTHERAPY)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegativeThermocoagulationDesagregations")
  public CohortDefinition
      getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegativeThermocoagulationDesagregations(
          final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreean");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-RN",
        EptsReportUtils.map(
            this
                .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegative(
                    isCommunity),
            mappings));

    definition.addSearch(
        "THERMOCOAGULATION",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "THERMOCOAGULATION",
                CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
                    CxType.THERMOCOAGULATION)),
            mappings));

    definition.setCompositionString("(CX-RN AND THERMOCOAGULATION)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegativeLeepDesagregations")
  public CohortDefinition
      getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegativeLeepDesagregations(
          final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreean");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-RN",
        EptsReportUtils.map(
            this
                .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegative(
                    isCommunity),
            mappings));

    definition.addSearch(
        "LEEP",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "LEEP",
                CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
                    CxType.LEEP)),
            mappings));

    definition.setCompositionString("(CX-RN AND LEEP)");

    return definition;
  }

  @DocumentedDefinition(
      value = "getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUp")
  public CohortDefinition
      getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUp(
          final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreean");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-TX-DENOMINATOR",
        EptsReportUtils.map(
            this.getPatientsWhoReceivedTreatmentTypeDuringReportingPeriodTotalDenominator(
                isCommunity),
            mappings));

    definition.addSearch(
        "POST-TRETMENT",
        EptsReportUtils.map(
            this.CxCaSCRNCohortQueries.findpatientwithScreeningTypeVisitAsPostTreatmentFollowUp(),
            mappings));

    definition.setCompositionString("(CX-TX-DENOMINATOR AND POST-TRETMENT)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUpCryotherapyDesagregations")
  public CohortDefinition
      getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUpCryotherapyDesagregations(
          final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreean");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-PT",
        EptsReportUtils.map(
            this.getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUp(
                isCommunity),
            mappings));

    definition.addSearch(
        "CRYOTHERAPY",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "CRYOTHERAPY",
                CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
                    CxType.CRYOTHERAPY)),
            mappings));

    definition.setCompositionString("(CX-PT AND CRYOTHERAPY)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUpThermocoagulationDesagregations")
  public CohortDefinition
      getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUpThermocoagulationDesagregations(
          final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreean");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-PT",
        EptsReportUtils.map(
            this.getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUp(
                isCommunity),
            mappings));

    definition.addSearch(
        "THERMOCOAGULATION",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "THERMOCOAGULATION",
                CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
                    CxType.THERMOCOAGULATION)),
            mappings));

    definition.setCompositionString("(CX-PT AND THERMOCOAGULATION)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUpLeepDesagregations")
  public CohortDefinition
      getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUpLeepDesagregations(
          final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUpLeepDesagregations");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-PT",
        EptsReportUtils.map(
            this.getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUp(
                isCommunity),
            mappings));

    definition.addSearch(
        "LEEP",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "LEEP",
                CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
                    CxType.LEEP)),
            mappings));

    definition.setCompositionString("(CX-PT AND LEEP)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositive")
  public CohortDefinition
      getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositive(
          final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getPatientsWhoerceivedTreatmentTypeDuringReportingPeriodFirstScreean");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-TX-DENOMINATOR",
        EptsReportUtils.map(
            this.getPatientsWhoReceivedTreatmentTypeDuringReportingPeriodTotalDenominator(
                isCommunity),
            mappings));

    definition.addSearch(
        "CX-RP",
        EptsReportUtils.map(
            this.CxCaSCRNCohortQueries
                .findPatientWithScreeningTypeVisitAsRescreenedAfterPreviousPositive(),
            mappings));

    definition.addSearch(
        "FIRST-SCREEN",
        EptsReportUtils.map(
            this.getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodFirstScreean(isCommunity),
            mappings));

    definition.addSearch(
        "CX-RN",
        EptsReportUtils.map(
            this
                .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousNegative(
                    isCommunity),
            mappings));

    definition.addSearch(
        "CX-PT",
        EptsReportUtils.map(
            this.getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodPostTreatmentFollowUp(
                isCommunity),
            mappings));

    definition.setCompositionString(
        "(CX-TX-DENOMINATOR AND CX-RP) NOT (FIRST-SCREEN OR CX-RN OR CX-PT)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositiveCryotherapypDesagregations")
  public CohortDefinition
      getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositiveCryotherapypDesagregations(
          final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositiveCryotherapypDesagregations");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-POSITIVE",
        EptsReportUtils.map(
            this
                .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositive(
                    isCommunity),
            mappings));

    definition.addSearch(
        "CRYOTHERAPY",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "CRYOTHERAPY",
                CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
                    CxType.CRYOTHERAPY)),
            mappings));

    definition.setCompositionString("(CX-POSITIVE AND CRYOTHERAPY)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositiveThermocoagulationDesagregations")
  public CohortDefinition
      getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositiveThermocoagulationDesagregations(
          final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositiveThermocoagulationDesagregations");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-POSITIVE",
        EptsReportUtils.map(
            this
                .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositive(
                    isCommunity),
            mappings));

    definition.addSearch(
        "THERMOCOAGULATION",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "THERMOCOAGULATION",
                CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
                    CxType.THERMOCOAGULATION)),
            mappings));

    definition.setCompositionString("(CX-POSITIVE AND THERMOCOAGULATION)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositiveLeepDesagregations")
  public CohortDefinition
      getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositiveLeepDesagregations(
          final Boolean isCommunity) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositiveLeepDesagregations");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CX-POSITIVE",
        EptsReportUtils.map(
            this
                .getPatientsWhoeReceivedTreatmentTypeDuringReportingPeriodRescreenedAfterPreviousPositive(
                    isCommunity),
            mappings));

    definition.addSearch(
        "LEEP",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "LEEP",
                CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
                    CxType.LEEP)),
            mappings));

    definition.setCompositionString("(CX-POSITIVE AND LEEP)");

    return definition;
  }
}
