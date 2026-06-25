package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxNewQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TXTBCohortQueries {

  @Autowired private TbMetadata tbMetadata;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  private final String generalParameterMapping =
      "startDate=${startDate},endDate=${endDate},location=${location}";

  public Mapped<CohortDefinition> map(final CohortDefinition cd, final String parameterMappings) {
    return EptsReportUtils.map(
        cd,
        EptsReportUtils.removeMissingParameterMappingsFromCohortDefintion(cd, parameterMappings));
  }

  private void addGeneralParameters(final CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }

  public CohortDefinition getPulmonaryTBWithinReportingDate() {
    final CohortDefinition definition =
        this.genericCohortQueries.generalSql(
            "pulmonaryTBeWithinReportingDate",
            TXTBQueries.dateObsByObsDateTimeClausule(
                this.hivMetadata.getOtherDiagnosis().getConceptId(),
                this.tbMetadata.getPulmonaryTB().getConceptId(),
                this.hivMetadata.getMasterCardEncounterType().getEncounterTypeId()));
    this.addGeneralParameters(definition);
    return definition;
  }

  public CohortDefinition getTuberculosisTreatmentPlanWithinReportingDate() {
    final CohortDefinition definition =
        this.genericCohortQueries.generalSql(
            "tuberculosisTreatmentPlanWithinReportingDate",
            TXTBQueries.dateObsByObsDateTimeClausule(
                this.tbMetadata.getTBTreatmentPlanConcept().getConceptId(),
                this.hivMetadata.getStartDrugsConcept().getConceptId(),
                this.hivMetadata.getAdultoSeguimentoEncounterType().getId()));
    this.addGeneralParameters(definition);
    return definition;
  }

  /** PROGRAMA: PACIENTES INSCRITOS NO PROGRAMA DE TUBERCULOSE - NUM PERIODO */
  public CohortDefinition getInTBProgram() {
    final CohortDefinition definition =
        this.genericCohortQueries.generalSql(
            "TBPROGRAMA", TXTBQueries.inTBProgramWithinReportingPeriodAtLocation());
    this.addGeneralParameters(definition);
    return definition;
  }

  public CohortDefinition artList() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("TxTB - artList");
    cd.addSearch(
        "started-by-end-reporting-period",
        EptsReportUtils.map(
            this.genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("started-by-end-reporting-period");
    this.addGeneralParameters(cd);
    return cd;
  }

  @DocumentedDefinition(value = "txTbNumerator")
  public CohortDefinition txTbNumerator() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("TxTB - txTbNumerator");
    final CohortDefinition A = this.findPatientsWhoAreTBTreatment();

    cd.addSearch(
        "DENOMINATOR", EptsReportUtils.map(this.getDenominator(), this.generalParameterMapping));

    cd.addSearch("TBTRETMENT", EptsReportUtils.map(A, this.generalParameterMapping));
    cd.setCompositionString("DENOMINATOR AND TBTRETMENT");

    this.addGeneralParameters(cd);
    return cd;
  }

  @DocumentedDefinition(value = "txTbNumeratorForCommunity")
  public CohortDefinition txTbNumeratorForCommunity() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB - txTbNumerator - For Community");

    definition.addSearch(
        "NUMERATOR", EptsReportUtils.map(this.txTbNumerator(), this.generalParameterMapping));
    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        this.map(this.txNewCohortQueries.communityDispensation(), this.generalParameterMapping));

    definition.setCompositionString("NUMERATOR AND COMMUNITY-DISPENSATION");

    this.addGeneralParameters(definition);

    return definition;
  }

  @DocumentedDefinition(value = "newOnARTPositiveScreening")
  public CohortDefinition newOnARTPositiveScreening() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB - newOnARTPositiveScreening");
    definition.addSearch(
        "denominator", EptsReportUtils.map(this.getDenominator(), this.generalParameterMapping));
    definition.addSearch(
        "new-on-art", EptsReportUtils.map(this.getNewOnArt(), this.generalParameterMapping));
    definition.addSearch(
        "positive-screening",
        EptsReportUtils.map(
            this.findPatientWhoAreTBPositiveScreening(), this.generalParameterMapping));

    this.addGeneralParameters(definition);

    definition.setCompositionString("denominator AND new-on-art AND positive-screening");
    return definition;
  }

  @DocumentedDefinition(value = "newOnARTPositiveScreeningForCommunity")
  public CohortDefinition newOnARTPositiveScreeningForCommuity() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB - newOnARTPositiveScreening");
    definition.addSearch(
        "denominator",
        EptsReportUtils.map(this.getDenominatorForCommunity(), this.generalParameterMapping));
    definition.addSearch(
        "new-on-art", EptsReportUtils.map(this.getNewOnArt(), this.generalParameterMapping));
    definition.addSearch(
        "positive-screening",
        EptsReportUtils.map(
            this.findPatientWhoAreTBPositiveScreening(), this.generalParameterMapping));

    this.addGeneralParameters(definition);

    definition.setCompositionString("denominator AND new-on-art AND positive-screening");
    return definition;
  }

  @DocumentedDefinition(value = "newOnARTNegativeScreening")
  public CohortDefinition newOnARTNegativeScreening() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB - newOnARTPositiveScreening");
    definition.addSearch(
        "denominator", EptsReportUtils.map(this.getDenominator(), this.generalParameterMapping));
    definition.addSearch(
        "new-on-art", EptsReportUtils.map(this.getNewOnArt(), this.generalParameterMapping));
    definition.addSearch(
        "positive-screening",
        EptsReportUtils.map(
            this.findPatientWhoAreTBPositiveScreening(), this.generalParameterMapping));
    this.addGeneralParameters(definition);
    definition.setCompositionString("(denominator AND new-on-art) NOT positive-screening");
    return definition;
  }

  @DocumentedDefinition(value = "newOnARTNegativeScreeningForCommunity")
  public CohortDefinition newOnARTNegativeScreeningForCommunity() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB - newOnARTPositiveScreening");
    definition.addSearch(
        "denominator",
        EptsReportUtils.map(this.getDenominatorForCommunity(), this.generalParameterMapping));
    definition.addSearch(
        "new-on-art", EptsReportUtils.map(this.getNewOnArt(), this.generalParameterMapping));
    definition.addSearch(
        "positive-screening",
        EptsReportUtils.map(
            this.findPatientWhoAreTBPositiveScreening(), this.generalParameterMapping));
    this.addGeneralParameters(definition);
    definition.setCompositionString("(denominator AND new-on-art) NOT positive-screening");
    return definition;
  }

  @DocumentedDefinition(value = "previouslyOnARTPositiveScreening")
  public CohortDefinition previouslyOnARTPositiveScreening() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB - previouslyOnARTPositiveScreening");
    definition.addSearch(
        "denominator", EptsReportUtils.map(this.getDenominator(), this.generalParameterMapping));
    definition.addSearch(
        "new-on-art", EptsReportUtils.map(this.getNewOnArt(), this.generalParameterMapping));
    definition.addSearch(
        "positive-screening",
        EptsReportUtils.map(
            this.findPatientWhoAreTBPositiveScreening(), this.generalParameterMapping));
    this.addGeneralParameters(definition);
    definition.setCompositionString("(denominator AND positive-screening) NOT new-on-art");
    return definition;
  }

  @DocumentedDefinition(value = "previouslyOnARTPositiveScreeningForcommunity")
  public CohortDefinition previouslyOnARTPositiveScreeningForCommunity() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB - previouslyOnARTPositiveScreening - For Community");
    definition.addSearch(
        "denominator",
        EptsReportUtils.map(this.getDenominatorForCommunity(), this.generalParameterMapping));
    definition.addSearch(
        "new-on-art", EptsReportUtils.map(this.getNewOnArt(), this.generalParameterMapping));
    definition.addSearch(
        "positive-screening",
        EptsReportUtils.map(
            this.findPatientWhoAreTBPositiveScreening(), this.generalParameterMapping));
    this.addGeneralParameters(definition);
    definition.setCompositionString("(denominator AND positive-screening) NOT new-on-art");
    return definition;
  }

  @DocumentedDefinition(value = "previouslyOnARTNegativeScreening")
  public CohortDefinition previouslyOnARTNegativeScreening() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB - previouslyOnARTNegativeScreening");
    definition.addSearch(
        "denominator", EptsReportUtils.map(this.getDenominator(), this.generalParameterMapping));
    definition.addSearch(
        "new-on-art", EptsReportUtils.map(this.getNewOnArt(), this.generalParameterMapping));
    definition.addSearch(
        "positive-screening",
        EptsReportUtils.map(
            this.findPatientWhoAreTBPositiveScreening(), this.generalParameterMapping));
    this.addGeneralParameters(definition);
    definition.setCompositionString("denominator NOT (new-on-art OR positive-screening)");
    return definition;
  }

  @DocumentedDefinition(value = "previouslyOnARTNegativeScreeningForCommunity")
  public CohortDefinition previouslyOnARTNegativeScreeningForCommunity() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB - previouslyOnARTNegativeScreening - For Community");
    definition.addSearch(
        "denominator",
        EptsReportUtils.map(this.getDenominatorForCommunity(), this.generalParameterMapping));
    definition.addSearch(
        "new-on-art", EptsReportUtils.map(this.getNewOnArt(), this.generalParameterMapping));
    definition.addSearch(
        "positive-screening",
        EptsReportUtils.map(
            this.findPatientWhoAreTBPositiveScreening(), this.generalParameterMapping));
    this.addGeneralParameters(definition);
    definition.setCompositionString("denominator NOT (new-on-art OR positive-screening)");
    return definition;
  }

  @DocumentedDefinition(value = "patientsNewOnARTNumerator")
  public CohortDefinition patientsNewOnARTNumerator() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("TxTB - patientsNewOnARTNumerator");
    final CohortDefinition NUM = this.txTbNumerator();
    cd.addSearch("NUM", this.map(NUM, this.generalParameterMapping));

    cd.addSearch(
        "started-during-reporting-period",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoAreNewlyEnrolledOnART",
                TxNewQueries.QUERY.findPatientsWhoAreNewlyEnrolledOnART),
            this.generalParameterMapping));

    cd.setCompositionString("NUM AND started-during-reporting-period");
    this.addGeneralParameters(cd);

    return cd;
  }

  @DocumentedDefinition(value = "patientsNewOnARTNumeratorForCommunity")
  public CohortDefinition patientsNewOnARTNumeratorForCommunity() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB - patientsNewOnARTNumerator - For Community");

    definition.addSearch(
        "NUMERATOR", this.map(this.txTbNumeratorForCommunity(), this.generalParameterMapping));

    definition.addSearch(
        "started-during-reporting-period",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoAreNewlyEnrolledOnART",
                TxNewQueries.QUERY.findPatientsWhoAreNewlyEnrolledOnART),
            this.generalParameterMapping));

    definition.setCompositionString("NUMERATOR AND started-during-reporting-period");

    this.addGeneralParameters(definition);

    return definition;
  }

  @DocumentedDefinition(value = "patientsPreviouslyOnARTNumerator")
  public CohortDefinition patientsPreviouslyOnARTNumerator() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("TxTB - patientsPreviouslyOnARTNumerator");
    final CohortDefinition NUM = this.txTbNumerator();
    cd.addSearch("NUM", this.map(NUM, this.generalParameterMapping));
    cd.addSearch(
        "started-before-start-reporting-period",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoAreNewlyEnrolledOnART",
                TxNewQueries.QUERY.findPatientsWhoAreNewlyEnrolledOnARTByPreviousReportingPeriod),
            "endDate=${startDate-1d},location=${location}"));

    cd.addSearch(
        "started-during-reporting-period",
        EptsReportUtils.map(this.patientsNewOnARTNumerator(), this.generalParameterMapping));

    cd.setCompositionString(
        "(NUM AND started-before-start-reporting-period) NOT started-during-reporting-period");
    this.addGeneralParameters(cd);
    return cd;
  }

  @DocumentedDefinition(value = "patientsPreviouslyOnARTNumeratorForCommunity")
  public CohortDefinition patientsPreviouslyOnARTNumeratorForCommunity() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB - patientsPreviouslyOnARTNumerator - For Community");

    definition.addSearch(
        "NUMERATOR", this.map(this.txTbNumeratorForCommunity(), this.generalParameterMapping));

    definition.addSearch(
        "started-before-start-reporting-period",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoAreNewlyEnrolledOnART",
                TxNewQueries.QUERY.findPatientsWhoAreNewlyEnrolledOnARTByPreviousReportingPeriod),
            "endDate=${startDate-1d},location=${location}"));

    definition.addSearch(
        "started-during-reporting-period",
        EptsReportUtils.map(this.patientsNewOnARTNumerator(), this.generalParameterMapping));

    definition.setCompositionString(
        "(NUMERATOR AND started-before-start-reporting-period) NOT started-during-reporting-period");

    this.addGeneralParameters(definition);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreNewEnrolledOnARTUntilTheEndReportinPeriod")
  public CohortDefinition findPatientsWhoAreNewEnrolledOnARTUntilTheEndReportinPeriod() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.setQuery(TXTBQueries.findPatientWhoAreNewEnrolledOnARTUntilEndDate());

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreScreenedForTB")
  public CohortDefinition findPatientsWhoAreScreenedForTB() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.setQuery(TXTBQueries.findPatientWhoAreScreenedForTB());

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreTBTreatment")
  public CohortDefinition findPatientsWhoAreTBTreatment() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWhoAreTBTreatment");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.setQuery(TXTBQueries.findPatientWhoAreInTBTreatment());

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreTrasferedOut")
  public CohortDefinition findPatientsWhoAreTrasferedOut() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.setQuery(TXTBQueries.findPatientWhoAreTransferedOut());

    return definition;
  }

  @DocumentedDefinition(value = "findPatientWhoAreTBPositiveScreening")
  public CohortDefinition findPatientWhoAreTBPositiveScreening() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientWhoAreTBPositiveScreening");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.setQuery(TXTBQueries.findPatientWhoAreTBPositiveScreening());

    return definition;
  }

  @DocumentedDefinition(value = "findPatientWhoAreSpecimenSent")
  private CohortDefinition findPatientWhoAreSpecimenSent() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.setQuery(TXTBQueries.findPatientWhoAreSpecimenSent());

    return definition;
  }

  @DocumentedDefinition(value = "findPatientWhoArePosiveResult")
  private CohortDefinition findPatientWhoArePosiveResult() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.setQuery(TXTBQueries.findPatientWhoArePosiveResult());

    return definition;
  }

  private CohortDefinition findPatientWhoAreCXR() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.setQuery(TXTBQueries.findPatientWhoAreCXR());

    return definition;
  }

  @DocumentedDefinition(value = "Denominator")
  public CohortDefinition getDenominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalDenominator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    final String mappingsToBeExclude =
        "startDate=${startDate-6m},endDate=${startDate-1d},location=${location}";

    definition.addSearch(
        "TXNEW",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewEnrolledOnARTUntilTheEndReportinPeriod(), mappings));

    definition.addSearch(
        "SCREENED", EptsReportUtils.map(this.findPatientsWhoAreScreenedForTB(), mappings));
    definition.addSearch(
        "TREATMENT-1", EptsReportUtils.map(this.findPatientsWhoAreTBTreatment(), mappings));
    definition.addSearch(
        "TREATMENT-2",
        EptsReportUtils.map(this.findPatientsWhoAreTBTreatment(), mappingsToBeExclude));
    definition.addSearch(
        "TROUT", EptsReportUtils.map(this.findPatientsWhoAreTrasferedOut(), mappings));

    definition.setCompositionString(
        "(TXNEW AND SCREENED) NOT((TROUT NOT(TREATMENT-1)) OR TREATMENT-2)");

    return definition;
  }

  @DocumentedDefinition(value = "DenominatorForCommunity")
  public CohortDefinition getDenominatorForCommunity() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalDenominatorForCommunity");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    final String mappingsToBeExclude =
        "startDate=${startDate-6m},endDate=${startDate-1d},location=${location}";

    definition.addSearch(
        "TXNEW",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewEnrolledOnARTUntilTheEndReportinPeriod(), mappings));

    definition.addSearch(
        "SCREENED", EptsReportUtils.map(this.findPatientsWhoAreScreenedForTB(), mappings));
    definition.addSearch(
        "TREATMENT-1", EptsReportUtils.map(this.findPatientsWhoAreTBTreatment(), mappings));
    definition.addSearch(
        "TREATMENT-2",
        EptsReportUtils.map(this.findPatientsWhoAreTBTreatment(), mappingsToBeExclude));
    definition.addSearch(
        "TROUT", EptsReportUtils.map(this.findPatientsWhoAreTrasferedOut(), mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        this.map(this.txNewCohortQueries.communityDispensation(), this.generalParameterMapping));

    definition.setCompositionString(
        "(TXNEW AND SCREENED AND COMMUNITY-DISPENSATION) NOT((TROUT NOT(TREATMENT-1)) OR TREATMENT-2)");

    return definition;
  }

  @DocumentedDefinition(value = "get New on Art")
  public CohortDefinition getNewOnArt() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "TXNEW",
        EptsReportUtils.map(
            this.txNewCohortQueries.findPatientsWhoAreNewEnrolmentOnARTForTxTB(), mappings));

    definition.setCompositionString("TXNEW");

    return definition;
  }

  @DocumentedDefinition(value = "get Specimen Sent")
  public CohortDefinition getSpecimenSentCohortDefinition(final String mappings) {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.addSearch("DENOMINATOR", EptsReportUtils.map(this.getDenominator(), mappings));

    definition.addSearch(
        "SPS", EptsReportUtils.map(this.findPatientWhoAreSpecimenSent(), mappings));

    definition.setCompositionString("DENOMINATOR AND SPS");

    return definition;
  }

  @DocumentedDefinition(value = "get Specimen Sent For Community")
  public CohortDefinition getSpecimenSentCohortDefinitionForCommunity(final String mappings) {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.addSearch(
        "DENOMINATOR", EptsReportUtils.map(this.getDenominatorForCommunity(), mappings));

    definition.addSearch(
        "SPS", EptsReportUtils.map(this.findPatientWhoAreSpecimenSent(), mappings));

    definition.setCompositionString("DENOMINATOR AND SPS");

    return definition;
  }

  @DocumentedDefinition(value = "findPatientWhoAreCXR")
  public CohortDefinition getPatientWhoAreCXR() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    definition.addSearch("DENOMINATOR", EptsReportUtils.map(this.getDenominator(), mappings));

    definition.addSearch("CXR", EptsReportUtils.map(this.findPatientWhoAreCXR(), mappings));

    definition.setCompositionString("DENOMINATOR AND CXR");

    return definition;
  }

  @DocumentedDefinition(value = "findPatientWhoAreCXRForCommunity")
  public CohortDefinition getPatientWhoAreCXRForCommunity() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    definition.addSearch(
        "DENOMINATOR", EptsReportUtils.map(this.getDenominatorForCommunity(), mappings));

    definition.addSearch("CXR", EptsReportUtils.map(this.findPatientWhoAreCXR(), mappings));

    definition.setCompositionString("DENOMINATOR AND CXR");

    return definition;
  }

  @DocumentedDefinition(value = "getDiagnosticTestSmearMicroscopyOnly")
  public CohortDefinition getDiagnosticTestSmearMicroscopyOnly() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch("DENOMINATOR", EptsReportUtils.map(this.getDenominator(), mappings));

    definition.addSearch(
        "SW",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "Finding patients with wWRD Tests Diagnosis",
                TXTBQueries.findPatientWhoAreDiagnosticTestSmearMicroscopyOnly()),
            mappings));

    definition.addSearch(
        "mWRD", EptsReportUtils.map(this.getDiagnosticTestCohortDefinitionMWRS(), mappings));

    definition.setCompositionString("(DENOMINATOR AND SW) NOT mWRD");

    return definition;
  }

  @DocumentedDefinition(value = "getDiagnosticTestSmearMicroscopyOnlyForCommunity")
  public CohortDefinition getDiagnosticTestSmearMicroscopyOnlyForCommunity() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "DENOMINATOR", EptsReportUtils.map(this.getDenominatorForCommunity(), mappings));

    definition.addSearch(
        "SW",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "Finding patients with wWRD Tests Diagnosis",
                TXTBQueries.findPatientWhoAreDiagnosticTestSmearMicroscopyOnly()),
            mappings));

    definition.addSearch(
        "mWRD", EptsReportUtils.map(this.getDiagnosticTestCohortDefinitionMWRS(), mappings));

    definition.setCompositionString("(DENOMINATOR AND SW) NOT mWRD");

    return definition;
  }

  @DocumentedDefinition(value = "getSymptomScreenAlone")
  public CohortDefinition getSymptomScreenAlone() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch("DENOMINATOR", EptsReportUtils.map(this.getDenominator(), mappings));

    definition.addSearch("DTS", EptsReportUtils.map(this.findPatientWhoAreCXR(), mappings));

    definition.setCompositionString("DENOMINATOR NOT DTS");

    return definition;
  }

  @DocumentedDefinition(value = "getSymptomScreenAloneForCommunity")
  public CohortDefinition getSymptomScreenAloneForCommunity() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "DENOMINATOR", EptsReportUtils.map(this.getDenominatorForCommunity(), mappings));

    definition.addSearch("DTS", EptsReportUtils.map(this.findPatientWhoAreCXR(), mappings));

    definition.setCompositionString("DENOMINATOR NOT DTS");

    return definition;
  }

  @DocumentedDefinition(value = "getDiagnosticTestCohortDefinitionMWRS")
  public CohortDefinition getDiagnosticTestCohortDefinitionMWRS() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch("DENOMINATOR", EptsReportUtils.map(this.getDenominator(), mappings));

    definition.addSearch(
        "mWRD",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "Finding patients with wWRD Tests Diagnosis",
                TXTBQueries.findPatientWhoAreDiagnosticTestMWRD()),
            mappings));

    definition.setCompositionString("DENOMINATOR AND mWRD");

    return definition;
  }

  @DocumentedDefinition(value = "getDiagnosticTestCohortDefinitionMWRSForCommunity")
  public CohortDefinition getDiagnosticTestCohortDefinitionMWRSForCommunity() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "DENOMINATOR", EptsReportUtils.map(this.getDenominatorForCommunity(), mappings));

    definition.addSearch(
        "mWRD",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "Finding patients with wWRD Tests Diagnosis",
                TXTBQueries.findPatientWhoAreDiagnosticTestMWRD()),
            mappings));

    definition.setCompositionString("DENOMINATOR AND mWRD");

    return definition;
  }

  @DocumentedDefinition(value = "getDiagnosticTestCohortDefinitionOther")
  public CohortDefinition getDiagnosticTestCohortDefinitionOther() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch("DENOMINATOR", EptsReportUtils.map(this.getDenominator(), mappings));

    definition.addSearch(
        "OTHER",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "Finding patients with other Tests Diagnosis",
                TXTBQueries.findPatientWhoAreDiagnosticTestOther()),
            mappings));

    definition.addSearch(
        "mWRD", EptsReportUtils.map(this.getDiagnosticTestCohortDefinitionMWRS(), mappings));

    definition.addSearch(
        "SM", EptsReportUtils.map(this.getDiagnosticTestSmearMicroscopyOnly(), mappings));

    definition.setCompositionString("(DENOMINATOR AND OTHER) NOT (mWRD OR SM)");

    return definition;
  }

  @DocumentedDefinition(value = "getDiagnosticTestCohortDefinitionOtherForCommunity")
  public CohortDefinition getDiagnosticTestCohortDefinitionOtherForCommunity() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "DENOMINATOR", EptsReportUtils.map(this.getDenominatorForCommunity(), mappings));

    definition.addSearch(
        "OTHER",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "Finding patients with other Tests Diagnosis",
                TXTBQueries.findPatientWhoAreDiagnosticTestOther()),
            mappings));

    definition.addSearch(
        "mWRD", EptsReportUtils.map(this.getDiagnosticTestCohortDefinitionMWRS(), mappings));

    definition.addSearch(
        "SM", EptsReportUtils.map(this.getDiagnosticTestSmearMicroscopyOnly(), mappings));

    definition.setCompositionString("(DENOMINATOR AND OTHER) NOT (mWRD OR SM)");

    return definition;
  }

  @DocumentedDefinition(value = "getDenominatorAndPositiveScreening")
  public CohortDefinition getDenominatorAndPositiveScreening(final String mapping) {

    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    this.addGeneralParameters(cd);
    cd.setName("TxTB - Positive Screening");

    cd.addSearch("DENOMINATOR", EptsReportUtils.map(this.getDenominator(), mapping));

    cd.addSearch(
        "POSITIVE-SCREENING",
        EptsReportUtils.map(this.findPatientWhoAreTBPositiveScreening(), mapping));

    cd.setCompositionString("DENOMINATOR AND POSITIVE-SCREENING");

    return cd;
  }

  @DocumentedDefinition(value = "getDenominatorAndPositiveResults")
  public CohortDefinition getDenominatorAndPositiveResults(final String mapping) {

    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    this.addGeneralParameters(cd);
    cd.setName("TxTB - Positive Results");

    cd.addSearch("DENOMINATOR", EptsReportUtils.map(this.getDenominator(), mapping));

    cd.addSearch(
        "POSITIVE-RESULTS", EptsReportUtils.map(this.findPatientWhoArePosiveResult(), mapping));

    cd.setCompositionString("DENOMINATOR AND POSITIVE-RESULTS");

    return cd;
  }

  @DocumentedDefinition(value = "getDenominatorAndPositiveResultsForCommunity")
  public CohortDefinition getDenominatorAndPositiveResultsForCommunity(final String mapping) {

    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    this.addGeneralParameters(cd);
    cd.setName("TxTB - Positive Results - For Community");

    cd.addSearch("DENOMINATOR", EptsReportUtils.map(this.getDenominatorForCommunity(), mapping));

    cd.addSearch(
        "POSITIVE-RESULTS", EptsReportUtils.map(this.findPatientWhoArePosiveResult(), mapping));

    cd.setCompositionString("DENOMINATOR AND POSITIVE-RESULTS");

    return cd;
  }
}
