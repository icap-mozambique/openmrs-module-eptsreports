package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TXRetQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TXRetCohortQueries {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private CommonMetadata commonMetadata;

  @Autowired private GenderCohortQueries genderCohorts;

  @Autowired TxNewCohortQueries txNewCohortQueries;

  private final String mappings =
      "startDate=${startDate},endDate=${endDate},location=${location},months=${months}";

  private void addParameters(final CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Data Inicial", Date.class));
    cd.addParameter(new Parameter("endDate", "Data Final", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("months", "Número de Meses (12, 24, 36)", Integer.class));
  }

  private CohortDefinition cohortDefinition(final CohortDefinition cohortDefinition) {
    this.addParameters(cohortDefinition);
    return cohortDefinition;
  }

  public CohortDefinition obitoTwelveMonths() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql("obito", TXRetQueries.obitoTwelveMonths()));
  }

  public CohortDefinition suspensoTwelveMonths() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql("suspenso", TXRetQueries.suspensoTwelveMonths()));
  }

  private CohortDefinition initiotArvTwelveMonths() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql("initiotArv", TXRetQueries.initiotArvTwelveMonths()));
  }

  public CohortDefinition abandonoTwelveMonths() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("abandono");
    cd.addSearch(
        "NOTIFICADO",
        EptsReportUtils.map(
            this.cohortDefinition(
                this.genericCohortQueries.generalSql(
                    "notificado", TXRetQueries.notificadoTwelveMonths())),
            this.mappings));
    cd.addSearch(
        "NAONOTIFICADO",
        EptsReportUtils.map(
            this.cohortDefinition(
                this.genericCohortQueries.generalSql(
                    "naonotificado", TXRetQueries.naonotificadoTwelveMonths())),
            this.mappings));
    cd.setCompositionString("NOTIFICADO OR NAONOTIFICADO");
    this.addParameters(cd);
    return cd;
  }

  /** numerator */
  public CohortDefinition inCourtForTwelveMonths() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("inCourt12Months");
    cd.addSearch(
        "OBITO",
        EptsReportUtils.map(this.cohortDefinition(this.obitoTwelveMonths()), this.mappings));
    cd.addSearch(
        "SUSPENSO",
        EptsReportUtils.map(this.cohortDefinition(this.suspensoTwelveMonths()), this.mappings));
    cd.addSearch(
        "INICIOTARV",
        EptsReportUtils.map(this.cohortDefinition(this.initiotArvTwelveMonths()), this.mappings));
    cd.addSearch(
        "ABANDONO",
        EptsReportUtils.map(this.cohortDefinition(this.abandonoTwelveMonths()), this.mappings));

    cd.setCompositionString("INICIOTARV NOT (OBITO OR SUSPENSO OR ABANDONO)");
    this.addParameters(cd);
    return cd;
  }

  /** denominator */
  public CohortDefinition courtNotTransferredTwelveMonths() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "INICIO DE TRATAMENTO ARV - NUM PERIODO: EXCLUI TRANSFERIDOS PARA (SQL)",
            TXRetQueries.courtNotTransferredTwelveMonths()));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition under1YearIncreasedHARTAtARTStartDate() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "under1YearIncreasedHARTAtARTStartDate",
            TXRetQueries.under1YearIncreasedHARTAtARTStartDate()));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition oneTo19WhoStartedTargetAtARTInitiation() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "oneTo19WhoStartedTargetAtARTInitiation",
            TXRetQueries.oneTo19WhoStartedTargetAtARTInitiation()));
  }

  // ICAP
  /** map endDate, location rightly when using this */
  public CohortDefinition oneTo4WhoStartedTargetAtARTInitiation() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "oneTo4WhoStartedTargetAtARTInitiation",
            TXRetQueries.oneTo4WhoStartedTargetAtARTInitiation()));
  }

  // ICAP
  /** map endDate, location rightly when using this */
  public CohortDefinition fiveTo19WhoStartedTargetAtARTInitiation() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "fiveTo19WhoStartedTargetAtARTInitiation",
            TXRetQueries.fiveTo19WhoStartedTargetAtARTInitiation()));
  }

  /** map startDate, endDate, location rightly when using this */
  public CohortDefinition pregnancyEnrolledInART() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "GRAVIDAS INSCRITAS NO SERVIÇO TARV", TXRetQueries.pregnancyEnrolledInART()));
  }

  /**
   * breast feeding
   *
   * @return
   */
  public CohortDefinition possibleRegisteredClinicalProcedureAndFollowupForm() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setDescription(
        "LACTANTES OU PUERPUERAS (POS-PARTO) REGISTADAS: PROCESSO CLINICO E FICHA DE SEGUIMENTO");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.addSearch(
        "DATAPARTO",
        EptsReportUtils.map(
            this.txNewCohortQueries.getPatientsWithUpdatedDepartureInART(),
            "value1=${onOrAfter},value2=${onOrBefore},locationList=${location}"));
    cd.addSearch(
        "INICIOLACTANTE",
        EptsReportUtils.map(
            this.genericCohortQueries.hasCodedObs(
                this.hivMetadata.getCriteriaForArtStart(),
                BaseObsCohortDefinition.TimeModifier.FIRST,
                SetComparator.IN,
                Arrays.asList(this.hivMetadata.getAdultoSeguimentoEncounterType()),
                Arrays.asList(this.commonMetadata.getBreastfeeding())),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}"));
    cd.addSearch(
        "GRAVIDAS",
        EptsReportUtils.map(
            this.txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${onOrAfter},endDate=${onOrBefore},location=${location}"));
    cd.addSearch(
        "LACTANTEPROGRAMA",
        EptsReportUtils.map(
            this.infantsWhoGaveAwardsTwoYearsBehindReferenceDate(),
            "startDate=${onOrAfter},location=${location}"));
    cd.addSearch("FEMININO", EptsReportUtils.map(this.genderCohorts.femaleCohort(), ""));
    cd.addSearch(
        "LACTANTE",
        EptsReportUtils.map(
            this.genericCohortQueries.hasCodedObs(
                this.commonMetadata.getBreastfeeding(),
                BaseObsCohortDefinition.TimeModifier.LAST,
                SetComparator.IN,
                Arrays.asList(this.hivMetadata.getAdultoSeguimentoEncounterType()),
                Arrays.asList(this.commonMetadata.getYesConcept())),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}"));

    final String compositionString =
        "((DATAPARTO OR INICIOLACTANTE OR LACTANTEPROGRAMA  OR LACTANTE) NOT GRAVIDAS) AND FEMININO";
    cd.setCompositionString(compositionString);
    return cd;
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition menOnArt10To14() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "menOnArt10To14", TXRetQueries.genderOnArtXToY("M", 10, 14)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition womenOnArt10To14() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "womenOnArt10To14", TXRetQueries.genderOnArtXToY("F", 10, 14)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition menOnArt15To19() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "menOnArt15To19", TXRetQueries.genderOnArtXToY("M", 15, 19)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition womenOnArt15To19() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "womenOnArt15To19", TXRetQueries.genderOnArtXToY("F", 15, 19)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition menOnArt20To24() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "menOnArt20To24", TXRetQueries.genderOnArtXToY("M", 20, 24)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition womenOnArt20To24() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "womenOnArt20To24", TXRetQueries.genderOnArtXToY("F", 20, 24)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition menOnArt25To29() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "menOnArt25To29", TXRetQueries.genderOnArtXToY("M", 25, 29)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition womenOnArt25To29() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "womenOnArt25To29", TXRetQueries.genderOnArtXToY("F", 25, 29)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition menOnArt30To34() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "menOnArt30To34", TXRetQueries.genderOnArtXToY("M", 30, 34)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition womenOnArt30To34() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "womenOnArt30To34", TXRetQueries.genderOnArtXToY("F", 30, 34)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition menOnArt35To39() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "menOnArt35To39", TXRetQueries.genderOnArtXToY("M", 35, 39)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition womenOnArt35To39() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "womenOnArt35To39", TXRetQueries.genderOnArtXToY("F", 35, 39)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition menOnArt40To49() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "menOnArt40To49", TXRetQueries.genderOnArtXToY("M", 40, 49)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition womenOnArt40To49() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "womenOnArt40To49", TXRetQueries.genderOnArtXToY("F", 40, 49)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition menOnArtAbove50() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "menOnArtAbove50", TXRetQueries.genderOnArtAbove50("M")));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition womenOnArtAbove50() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "womenOnArtAbove50", TXRetQueries.genderOnArtAbove50("F")));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition infantsWhoGaveAwardsTwoYearsBehindReferenceDate() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "infantsWhoGaveAwardsTwoYearsBehindReferenceDate",
            TXRetQueries.infantsWhoGaveAwardsTwoYearsBehindReferenceDate()));
  }

  // ICAP

  /** map endDate, location rightly when using this */
  public CohortDefinition menOnArtUnder1() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "menOnArtUnder1", TXRetQueries.genderOnArtUnder1("M")));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition womenOnArtUnder1() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "womenOnArtUnder1", TXRetQueries.genderOnArtUnder1("F")));
  }

  public CohortDefinition menOnArt1To4() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "menOnArt1To4", TXRetQueries.genderOnArtXToY("M", 1, 4)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition womenOnArt1To4() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "womenOnArt1To4", TXRetQueries.genderOnArtXToY("F", 1, 4)));
  }

  public CohortDefinition menOnArt5To9() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "menOnArt5To9", TXRetQueries.genderOnArtXToY("M", 5, 9)));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition womenOnArt5To9() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "womenOnArt5To9", TXRetQueries.genderOnArtXToY("F", 5, 9)));
  }

  public CohortDefinition menOnArt40To44() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "menOnArt40To44", TXRetQueries.genderOnArtXToY("M", 40, 44)));
  }

  public CohortDefinition womenOnArt40To44() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "womenOnArt40To44", TXRetQueries.genderOnArtXToY("F", 40, 44)));
  }

  public CohortDefinition menOnArt45To49() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "menOnArt45To49", TXRetQueries.genderOnArtXToY("M", 45, 49)));
  }

  public CohortDefinition womenOnArt45To49() {
    return this.cohortDefinition(
        this.genericCohortQueries.generalSql(
            "womenOnArt45To49", TXRetQueries.genderOnArtXToY("F", 45, 49)));
  }
}
