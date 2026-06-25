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
import org.openmrs.module.eptsreports.reporting.library.queries.TxCurrQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxCurrQueries.QUERY.DispensationIntervalType;
import org.openmrs.module.eptsreports.reporting.library.queries.TxNewQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.RegeminType;
import org.openmrs.module.eptsreports.reporting.utils.TxCurrColumnsQuantity;
import org.openmrs.module.eptsreports.reporting.utils.TxCurrQuery;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Defines all of the TxCurrCohortQueries we want to expose for EPTS */
@Component
public class TxCurrCohortQueries {

  private static final String FIND_PATIENTS_WITH_CD4_LESS_THAN_200 = "PFACT/CD4_UNDER_200.sql";

  private static final String FIND_PATIENTS_TB_LAM = "PFACT/TB_LAM.sql";

  private static final String FIND_PATIENTS_TB_GENEXPERT = "PFACT/GENEXPERT.sql";

  private static final String FIND_PATIENTS_TB_CRAG = "PFACT/CRAG.sql";

  private static final String FIND_PATIENTS_DAH = "PFACT/DAH.sql";

  private static final String FIND_PATIENTS_TB_DIAGNOSTIC = "PFACT/DIAGNOSTICO_TB.sql";

  private static final String FIND_PATIENTS_TB_TREATMENT = "PFACT/TB_TREATMENT.sql";

  private static final String FIND_PATIENTS_CRAG_POSITIVE = "PFACT/CRAG_POSITIVE.sql";

  private static final String FIND_PATIENTS_MCC_DIAGNOSTIC = "PFACT/DIAGNOSTICO_MENINGITE.sql";

  private static final String FIND_PATIENTS_MCC_TREATMENT = "PFACT/TRATAMENTO_MENINGITE.sql";

  private static final String FIND_PATIENTS_ACTIVE_WITHOUT_RECEPCAO =
      "TX_CURR/TX_CURR_WITHOUT_RECEPCAO_LEVANTOU.sql";

  @Autowired private GenericCohortQueries genericCohorts;

  @DocumentedDefinition(value = "patientsWhoAreActiveOnART")
  public CohortDefinition findPatientsWhoAreActiveOnART() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("patientsWhoAreActiveOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "TXCURR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Finding patients who are currently enrolled on ART",
                TxCurrQuery.findPatientsInTxCurr(TxCurrColumnsQuantity.PATIENT_ID)),
            mappings));

    definition.setCompositionString("TXCURR");

    return definition;
  }

  @DocumentedDefinition(value = "patientsWhoAreActiveOnARTWithoutRecepcaoLEvantou")
  public CohortDefinition findPatientsWhoAreActiveOnARTWithoutRecepcaoLevantou() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("patientsWhoAreActiveOnART");

    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "endDate=${endDate},location=${location}";

    definition.addSearch(
        "TXCURR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Finding patients who are currently enrolled on ART Withou Recepcao Levantou",
                EptsQuerysUtils.loadQuery(
                    TxCurrCohortQueries.FIND_PATIENTS_ACTIVE_WITHOUT_RECEPCAO)),
            mappings));

    definition.setCompositionString("TXCURR");

    return definition;
  }

  @DocumentedDefinition(value = "patientsWhoAreActiveOnART")
  public CohortDefinition findCommunityCMPatientsWhoAreActiveOnART() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CommunityCMPatientsWhoAreActiveOnART");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART", EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION-CM",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityCMDispensation),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART AND COMMUNITY-DISPENSATION-CM");

    return definition;
  }

  // PFACT
  @DocumentedDefinition(value = "patientsWhoAreActiveOnARTWithLessThan200")
  public CohortDefinition findPatientsWithLessThan200CD4WhoAreActiveOnARTTBLAM() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("patientsWhoAreActiveOnARTWithLessThan200");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART", EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    definition.addSearch(
        "CD4-LESS-200",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCd4LessThan200",
                EptsQuerysUtils.loadQuery(
                    TxCurrCohortQueries.FIND_PATIENTS_WITH_CD4_LESS_THAN_200)),
            mappings));

    definition.addSearch(
        "TB-LAM",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientTBLAM",
                EptsQuerysUtils.loadQuery(TxCurrCohortQueries.FIND_PATIENTS_TB_LAM)),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART AND CD4-LESS-200 AND TB-LAM");

    return definition;
  }

  @DocumentedDefinition(value = "patientsWhoAreActiveOnARTWithLessThan200")
  public CohortDefinition findPatientsWithLessThan200CD4OnARTANDGenExpert() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("patientsWhoAreActiveOnARTWithLessThan200");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART", EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    definition.addSearch(
        "CD4-LESS-200",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCd4LessThan200",
                EptsQuerysUtils.loadQuery(
                    TxCurrCohortQueries.FIND_PATIENTS_WITH_CD4_LESS_THAN_200)),
            mappings));

    definition.addSearch(
        "TB-GEN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientTBLAM",
                EptsQuerysUtils.loadQuery(TxCurrCohortQueries.FIND_PATIENTS_TB_GENEXPERT)),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART AND CD4-LESS-200 AND TB-GEN");

    return definition;
  }

  @DocumentedDefinition(value = "patientsWhoAreActiveOnARTWithLessThan200AndCrag")
  public CohortDefinition findPatientsWithLessThan200CD4OnARTANDCrag() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("patientsWhoAreActiveOnARTWithLessThan200Crag");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART", EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    definition.addSearch(
        "CD4-LESS-200",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCd4LessThan200",
                EptsQuerysUtils.loadQuery(
                    TxCurrCohortQueries.FIND_PATIENTS_WITH_CD4_LESS_THAN_200)),
            mappings));

    definition.addSearch(
        "TB-CRAG",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientTBLAM",
                EptsQuerysUtils.loadQuery(TxCurrCohortQueries.FIND_PATIENTS_TB_CRAG)),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART AND CD4-LESS-200 AND TB-CRAG");

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsinDAH")
  public CohortDefinition findPatientsinDAH() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("findPatientsinDAHAndDiagnosticTB");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART", EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    definition.addSearch(
        "DAH",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientDAH", EptsQuerysUtils.loadQuery(TxCurrCohortQueries.FIND_PATIENTS_DAH)),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART AND DAH");

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsinDAHAndDiagnosticTB")
  public CohortDefinition findPatientsinDAHAndDiagnosticTB() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("findPatientsinDAHAndDiagnosticTB");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART-DAH", EptsReportUtils.map(this.findPatientsinDAH(), mappings));

    definition.addSearch(
        "TB",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientTBLAM",
                EptsQuerysUtils.loadQuery(TxCurrCohortQueries.FIND_PATIENTS_TB_DIAGNOSTIC)),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART-DAH AND TB");

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsinDAHAndDiagnosticTB")
  public CohortDefinition findPatientsinDAHAndDiagnosticTBTreatment() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("findPatientsinDAHAndDiagnosticTB");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART-TB",
        EptsReportUtils.map(this.findPatientsinDAHAndDiagnosticTB(), mappings));

    definition.addSearch(
        "TB-TREAT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientTBLAM",
                EptsQuerysUtils.loadQuery(TxCurrCohortQueries.FIND_PATIENTS_TB_TREATMENT)),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART-TB AND TB-TREAT");

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsinDAHAndCragPositive")
  public CohortDefinition findPatientsinDAHAndCragPositive() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("findPatientsinDAHAndCragPositive");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART-DAH", EptsReportUtils.map(this.findPatientsinDAH(), mappings));

    definition.addSearch(
        "CRAG-POSITIVE",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientTBLAM",
                EptsQuerysUtils.loadQuery(TxCurrCohortQueries.FIND_PATIENTS_CRAG_POSITIVE)),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART-DAH AND CRAG-POSITIVE");

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsinDAHAndMCCDiagnostic")
  public CohortDefinition findPatientsinDAHAndMCCDiagnostic() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("findPatientsinDAHAndCragPositive");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART-DAH", EptsReportUtils.map(this.findPatientsinDAH(), mappings));

    definition.addSearch(
        "MCC",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientTBLAM",
                EptsQuerysUtils.loadQuery(TxCurrCohortQueries.FIND_PATIENTS_MCC_DIAGNOSTIC)),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART-DAH AND MCC");

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsinDAHAndMCCTreatment")
  public CohortDefinition findPatientsinDAHAndMCCTreatment() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("findPatientsinDAHAndCragPositive");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART-DAH",
        EptsReportUtils.map(this.findPatientsinDAHAndMCCDiagnostic(), mappings));

    definition.addSearch(
        "TREATMENT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientTBLAM",
                EptsQuerysUtils.loadQuery(TxCurrCohortQueries.FIND_PATIENTS_MCC_TREATMENT)),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART-DAH AND TREATMENT");

    return definition;
  }

  // Para o IDP
  @DocumentedDefinition(value = "IDPpatientsWhoAreActiveOnART")
  public CohortDefinition findIDPWhoAreActiveOnART() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("IDPpatientsWhoAreActiveOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART", EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    mappings = "endDate=${endDate}";

    definition.addSearch(
        "IDP",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "IDPpatientsWhoAreActiveOnART", TxCurrQueries.QUERY.findIDPPatients),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART AND IDP");

    return definition;
  }

  @DocumentedDefinition(value = "patientsWhoAreActiveOnART")
  public CohortDefinition findCommunityBMPatientsWhoAreActiveOnART() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CommunityCMPatientsWhoAreActiveOnART");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART", EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION-BM",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityBMDispensation),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART AND COMMUNITY-DISPENSATION-BM");

    return definition;
  }

  @DocumentedDefinition(value = "patientsWhoAreActiveOnART")
  public CohortDefinition findCommunityPatientsWhoAreActiveOnART() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("CommunityPatientsWhoAreActiveOnART");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART", EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    definition.addSearch(
        "COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findCommunityPatientsDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensation),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART AND COMMUNITY-DISPENSATION");

    return definition;
  }

  // Regimes para pacientes Activos no TXCURR
  @DocumentedDefinition(value = "RegeminsOnPatientsWhoAreActiveOnART")
  public CohortDefinition findRegeminsOnPatientsWhoAreActiveOnART(final RegeminType regimens) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("DTGRegimeOnPatientsWhoAreActiveOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART", EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    mappings = mappings.concat(",startDate=${startDate}");

    definition.addSearch(
        "REGIMEN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findRegeminsOnPatientsWhoAreActiveOnART",
                TxCurrQueries.QUERY.findOnARTRegimens(regimens)),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART AND REGIMEN");

    return definition;
  }

  // Outros Regimes mais os que não tiveram Fila
  @DocumentedDefinition(value = "RegeminsOnPatientsWhoAreActiveOnART")
  public CohortDefinition findRegeminsOnPatientsWhoAreActiveOnARTOthers(
      final RegeminType regimens) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("DTGRegimeOnPatientsWhoAreActiveOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART", EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    mappings = mappings.concat(",startDate=${startDate}");

    definition.addSearch(
        "DTG-LPV-REGIMEN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findRegeminsOnPatientsWhoAreActiveOnART",
                TxCurrQueries.QUERY.findOnARTRegimens(regimens)),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART NOT DTG-LPV-REGIMEN");

    return definition;
  }

  // Dispensa pelos APES para pacientes Activos no TXCURR
  @DocumentedDefinition(value = "APEsOnPatientsWhoAreActiveOnART")
  public CohortDefinition findAPEsOnPatientsWhoAreActiveOnART() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("APEsOnPatientsWhoAreActiveOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART", EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    mappings = "startDate=${startDate}," + mappings;

    definition.addSearch(
        "APES",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findAPEsOnPatientsWhoAreActiveOnART", TxCurrQueries.QUERY.findOnARTAPEs),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART AND APES");

    return definition;
  }

  /*
   * Dispensa pelos APES Com evento de Carga Viral nos ultimos 12 meses para pacientes Activos no TXCURR
   */
  @DocumentedDefinition(value = "APEsOnPatientsWhoAreActiveOnARTWithViralLoadEvent")
  public CohortDefinition findPatientWithViralLoadEventWithinPast12Months() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("APEsOnPatientsWhoAreActiveOnARTWithViralLoadEvent");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART", EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    definition.addSearch(
        "VIRAL-LOAD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "APEsOnPatientsWhoAreActiveOnARTWithViralLoadEvent",
                TxCurrQueries.QUERY.findPatientWithViralLoadEventWithinPast12Months),
            mappings));

    mappings = "startDate=${startDate}," + mappings;

    definition.addSearch(
        "APES",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findAPEsOnPatientsWhoAreActiveOnART", TxCurrQueries.QUERY.findOnARTAPEs),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART AND VIRAL-LOAD AND APES");

    return definition;
  }

  /*
   * Dispensa pelos APES Com evento de Carga Viral abaixo de 1000 copias ultimos 12 meses para pacientes Activos no TXCURR
   */
  @DocumentedDefinition(value = "APEsOnPatientWithViralLoadLessThan1000WithinPast12Months")
  public CohortDefinition findPatientWithViralLoadLessThan1000WithinPast12Months() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("APEsOnPatientsWhoAreActiveOnARTWithViralLoadEvent");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "endDate=${endDate},location=${location}";

    definition.addSearch(
        "CURRENTLY-ON-ART", EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    definition.addSearch(
        "VIRAL-LOAD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "APEsOnPatientsWhoAreActiveOnARTWithViralLoadEvent",
                TxCurrQueries.QUERY.findPatientWithViralLoadEventWithinPast12Months),
            mappings));

    definition.addSearch(
        "THOUSAND-COPIES",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "APEsOnPatientWithViralLoadLessThan1000WithinPast12Months",
                TxCurrQueries.QUERY.findPatientWithViralLoadLessThan1000WithinPast12Months),
            mappings));

    mappings = "startDate=${startDate}," + mappings;

    definition.addSearch(
        "APES",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findAPEsOnPatientsWhoAreActiveOnART", TxCurrQueries.QUERY.findOnARTAPEs),
            mappings));

    definition.setCompositionString("CURRENTLY-ON-ART AND VIRAL-LOAD AND THOUSAND-COPIES AND APES");

    return definition;
  }

  public CohortDefinition getPatientsOnArtOnArvDispenseForLessThan3Months() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients On Art On ARV Dispensation less than 3 months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    cd.addSearch(
        "arvDispenseForLessThan3Months",
        EptsReportUtils.map(this.findPatientsOnArtOnArvDispenseForLessThan3Months(), mappings));

    cd.setCompositionString("arvDispenseForLessThan3Months");

    return cd;
  }

  public CohortDefinition getPatientsOnArtOnArvDispenseBetween3And5Months() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients On Art On ARV Dispensation Between 3 and 5 Months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    cd.addSearch(
        "patientsWhoAreActiveOnART",
        EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    cd.addSearch(
        "arvDispenseBetween3And5Months",
        EptsReportUtils.map(this.findPatientsOnArtOnArvDispenseBetween3And5Months(), mappings));

    cd.setCompositionString("patientsWhoAreActiveOnART AND arvDispenseBetween3And5Months");

    return cd;
  }

  public CohortDefinition getPatientsOnArtOnArvDispenseFor6OrMoreMonths() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients On Art On ARV Dispensation For 6 Or More Months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    cd.addSearch(
        "patientsWhoAreActiveOnART",
        EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    cd.addSearch(
        "arvDispenseFor6OrMoreMonths",
        EptsReportUtils.map(this.findPatientsOnArtOnArvDispenseFor6OrMoreMonths(), mappings));

    cd.setCompositionString("patientsWhoAreActiveOnART AND arvDispenseFor6OrMoreMonths");
    return cd;
  }

  @DocumentedDefinition(value = "findPatientsOnArtOnArvDispenseFor6OrMoreMonths")
  public CohortDefinition findPatientsOnArtOnArvDispenseFor6OrMoreMonths() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsOnArtOnArvDispenseFor6OrMoreMonths");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        TxCurrQueries.QUERY.findPatientsWhoAreInDispenseType(DispensationIntervalType.SEMI_ANNUAL));

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsOnArtOnArvDispenseBetween3And5Months")
  public CohortDefinition findPatientsOnArtOnArvDispenseBetween3And5Months() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsOnArtOnArvDispenseBetween3And5Months");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        TxCurrQueries.QUERY.findPatientsWhoAreInDispenseType(DispensationIntervalType.QUARTERLY));

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsOnArtOnArvDispenseForLessThan3Months")
  public CohortDefinition findPatientsOnArtOnArvDispenseForLessThan3Months() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients On Art On ARV Dispensation less than 3 Months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    cd.addSearch("TX-CURR", EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mappings));

    cd.addSearch(
        "ART-DISPENSATION-BETWEEN-3-5-MONTHS",
        EptsReportUtils.map(this.findPatientsOnArtOnArvDispenseBetween3And5Months(), mappings));

    cd.addSearch(
        "ART-DISPENSATION-GREAT-6-MONTHS",
        EptsReportUtils.map(this.findPatientsOnArtOnArvDispenseFor6OrMoreMonths(), mappings));

    cd.setCompositionString(
        "TX-CURR NOT (ART-DISPENSATION-BETWEEN-3-5-MONTHS OR ART-DISPENSATION-GREAT-6-MONTHS)");
    return cd;
  }
}
