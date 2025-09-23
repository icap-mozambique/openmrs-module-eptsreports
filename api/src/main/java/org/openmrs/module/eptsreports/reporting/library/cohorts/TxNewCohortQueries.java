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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.PregnantQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.ResumoMensalQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxNewQueries;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.CommunityType;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Defines all of the TxNew Cohort Definition instances we want to expose for EPTS */
@Component
public class TxNewCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private CommonMetadata commonMetadata;

  @Autowired private GenericCohortQueries genericCohorts;

  @Autowired private BreastFeedingCohortQueries breastFeedingCohortQueries;

  private static final String FIND_PATIENTS_WHO_ARE_NEWLY_ENROLLED_ON_ART =
      "TX_NEW/PATIENTS_WHO_ARE_NEWLY_ENROLLED_ON_ART.sql";

  private static final String FIND_PATIENTS_WITH_CD4_LESS_THAN_200 =
      "TX_NEW/PATIENTS_WITH_CD4_LESS_THAN_200.sql";

  private static final String FIND_PATIENTS_WITH_CD4_GREATER_OR_EQUAL_200 =
      "TX_NEW/PATIENTS_WITH_CD4_GREATER_OR_EQUAL_200.sql";

  private static final String FIND_AGE_PATIENTS_ON_TX_NEW =
      "TX_NEW/PATIENTS_AGE_AT_THE_DATE_OF_INITIATION_ON_ART.sql";

  private static final String FIND_AGE_PATIENTS_ON_STAGE_3_4 =
      "TX_NEW/PATIENTS_WHO_ARE_IN_STAGE_3_OR_4.sql";

  /**
   * PATIENTS WITH UPDATED DATE OF DEPARTURE IN THE ART SERVICE Are patients with date of delivery
   * updated in the tarv service. Note that the 'Start Date' and 'End Date' parameters refer to the
   * date of delivery and not the date of registration (update)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithUpdatedDepartureInART() {
    final DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.setName("patientsWithUpdatedDepartureInART");
    cd.setQuestion(this.commonMetadata.getPriorDeliveryDateConcept());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.ANY);

    final List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
    encounterTypes.add(this.hivMetadata.getAdultoSeguimentoEncounterType());
    encounterTypes.add(this.hivMetadata.getARVAdultInitialEncounterType());
    cd.setEncounterTypeList(encounterTypes);

    cd.setOperator1(RangeComparator.GREATER_EQUAL);
    cd.setOperator2(RangeComparator.LESS_EQUAL);

    cd.addParameter(new Parameter("value1", "After Date", Date.class));
    cd.addParameter(new Parameter("value2", "Before Date", Date.class));

    cd.addParameter(new Parameter("locationList", "Location", Location.class));

    return cd;
  }

  /**
   * PREGNANCY ENROLLED IN THE ART SERVICE These are patients who are pregnant during the initiation
   * of the process or during ART follow-up and who were notified as a new pregnancy during
   * follow-up.
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsPregnantEnrolledOnART() {
    final SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsPregnantEnrolledOnART");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setQuery(
        PregnantQueries.getPregnantWhileOnArt(
            this.commonMetadata.getPregnantConcept().getConceptId(),
            this.hivMetadata.getYesConcept().getConceptId(),
            this.hivMetadata.getNumberOfWeeksPregnant().getConceptId(),
            this.hivMetadata.getPregnancyDueDate().getConceptId(),
            this.hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            this.hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            this.hivMetadata.getPtvEtvProgram().getProgramId()));
    return cd;
  }

  /**
   * Build TxNew composition cohort definition
   *
   * @param cohortName Cohort name
   * @return CompositionQuery
   */
  public CohortDefinition getTxNewCompositionCohort(final String cohortName) {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName(cohortName);
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    txNewCompositionCohort.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoAreNewlyEnrolledOnART",
                EptsQuerysUtils.loadQuery(
                    TxNewCohortQueries.FIND_PATIENTS_WHO_ARE_NEWLY_ENROLLED_ON_ART)),
            mappings));

    txNewCompositionCohort.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod",
                TxNewQueries.QUERY.findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod),
            mappings));

    txNewCompositionCohort.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard",
                TxNewQueries.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    txNewCompositionCohort.setCompositionString(
        "START-ART NOT (TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD)");

    return txNewCompositionCohort;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreNewEnrolmentOnARTForTxTB")
  public CohortDefinition findPatientsWhoAreNewEnrolmentOnARTForTxTB() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsOnArtOnArvDispenseBetween3And5Months");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(TxNewQueries.QUERY.findPatientsWhoAreNewlyEnrolledOnART);

    return definition;
  }

  public CohortDefinition findPatientsWithCD4LessThan200() {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName("CD4 LESS THAN 200");
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    txNewCompositionCohort.addSearch(
        "CD4-LESS-200",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWithCD4LessThan200",
                EptsQuerysUtils.loadQuery(TxNewCohortQueries.FIND_PATIENTS_WITH_CD4_LESS_THAN_200)),
            mappings));

    txNewCompositionCohort.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod",
                TxNewQueries.QUERY.findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod),
            mappings));

    txNewCompositionCohort.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard",
                TxNewQueries.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    txNewCompositionCohort.setCompositionString(
        "CD4-LESS-200 NOT (TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD)");

    return txNewCompositionCohort;
  }

  public CohortDefinition findPatientsWIthCD4GreaterOrEqual200() {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName("CD4 GREATER OR EQUAL 200");
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    txNewCompositionCohort.addSearch(
        "CD4-GREATER-OR-EQUAL-200",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWithCD4LessThan200",
                EptsQuerysUtils.loadQuery(
                    TxNewCohortQueries.FIND_PATIENTS_WITH_CD4_GREATER_OR_EQUAL_200)),
            mappings));

    txNewCompositionCohort.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod",
                TxNewQueries.QUERY.findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod),
            mappings));

    txNewCompositionCohort.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard",
                TxNewQueries.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    txNewCompositionCohort.addSearch(
        "CD4-LESS-200", EptsReportUtils.map(this.findPatientsWithCD4LessThan200(), mappings));

    txNewCompositionCohort.setCompositionString(
        "CD4-GREATER-OR-EQUAL-200 NOT (CD4-LESS-200 OR TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD)");

    return txNewCompositionCohort;
  }

  public CohortDefinition findPatientsWithUnknownCD4() {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName("CD4 GREATER OR EQUAL 200");
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    txNewCompositionCohort.addSearch(
        "START-ART", EptsReportUtils.map(this.getTxNewCompositionCohort("TX_NEW"), mappings));

    txNewCompositionCohort.addSearch(
        "CD4-LESS-200", EptsReportUtils.map(this.findPatientsWithCD4LessThan200(), mappings));

    txNewCompositionCohort.addSearch(
        "CD4-GREATER-OR-EQUAL-200",
        EptsReportUtils.map(this.findPatientsWIthCD4GreaterOrEqual200(), mappings));

    txNewCompositionCohort.setCompositionString(
        "START-ART NOT (CD4-LESS-200 OR CD4-GREATER-OR-EQUAL-200)");

    return txNewCompositionCohort;
  }

  public CohortDefinition getTxNewCompositionCohortMISAU(final String cohortName) {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName(cohortName);
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    txNewCompositionCohort.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoAreNewlyEnrolledOnART",
                TxNewQueries.QUERY.findPatientsWhoAreNewlyEnrolledOnARTMISAU),
            mappings));

    txNewCompositionCohort.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod",
                ResumoMensalQueries.findPatientsWithAProgramStateMarkedAsTransferedInEndDate),
            mappings));

    txNewCompositionCohort.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard",
                ResumoMensalQueries
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardEndDate),
            mappings));

    txNewCompositionCohort.setCompositionString(
        "START-ART NOT (TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD)");

    return txNewCompositionCohort;
  }

  public CohortDefinition getTxNewCommunityCompositionCohort(final String cohortName) {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName(cohortName);
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    txNewCompositionCohort.addSearch(
        "START-ART", EptsReportUtils.map(this.getTxNewCompositionCohort(cohortName), mappings));

    txNewCompositionCohort.addSearch(
        "WITH-COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedARTWithComunnityDispensation",
                TxNewQueries.QUERY.findPatientsWhoStartedARTAtComunnity),
            mappings));

    txNewCompositionCohort.setCompositionString("START-ART AND WITH-COMMUNITY-DISPENSATION");

    return txNewCompositionCohort;
  }

  public CohortDefinition getTxNewStage3OR4CompositionCohort() {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName("Patient TX NEW IN STAGE 3 OR 4");
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    txNewCompositionCohort.addSearch(
        "START-ART", EptsReportUtils.map(this.getTxNewCompositionCohort("TX NEW"), mappings));

    txNewCompositionCohort.addSearch(
        "STAGE",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsStage3OR4",
                EptsQuerysUtils.loadQuery(TxNewCohortQueries.FIND_AGE_PATIENTS_ON_STAGE_3_4)),
            mappings));

    txNewCompositionCohort.setCompositionString("START-ART AND STAGE");

    return txNewCompositionCohort;
  }

  public CohortDefinition findPatientsWithCD4LessThan200Community() {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName("CD4 GREATER OR EQUAL 200");
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    txNewCompositionCohort.addSearch(
        "CD4-LESS-200", EptsReportUtils.map(this.findPatientsWithCD4LessThan200(), mappings));

    txNewCompositionCohort.addSearch(
        "WITH-COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedARTWithComunnityDispensation",
                TxNewQueries.QUERY.findPatientsWhoStartedARTAtComunnity),
            mappings));

    txNewCompositionCohort.setCompositionString("CD4-LESS-200 AND WITH-COMMUNITY-DISPENSATION");

    return txNewCompositionCohort;
  }

  public CohortDefinition findPatientsWIthCD4GreaterOrEqual200Community() {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName("CD4 GREATER OR EQUAL 200");
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    txNewCompositionCohort.addSearch(
        "CD4-GREATER-OR-EQUAL-200",
        EptsReportUtils.map(this.findPatientsWIthCD4GreaterOrEqual200(), mappings));

    txNewCompositionCohort.addSearch(
        "WITH-COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedARTWithComunnityDispensation",
                TxNewQueries.QUERY.findPatientsWhoStartedARTAtComunnity),
            mappings));

    txNewCompositionCohort.setCompositionString(
        "CD4-GREATER-OR-EQUAL-200 AND WITH-COMMUNITY-DISPENSATION");

    return txNewCompositionCohort;
  }

  public CohortDefinition findPatientsWithUnknownCD4Community() {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName("CD4 UNKNOWN");
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    txNewCompositionCohort.addSearch(
        "CD4-UNKNOWN", EptsReportUtils.map(this.findPatientsWithUnknownCD4(), mappings));

    txNewCompositionCohort.addSearch(
        "WITH-COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedARTWithComunnityDispensation",
                TxNewQueries.QUERY.findPatientsWhoStartedARTAtComunnity),
            mappings));

    txNewCompositionCohort.setCompositionString("CD4-UNKNOWN AND WITH-COMMUNITY-DISPENSATION");

    return txNewCompositionCohort;
  }

  public CohortDefinition getTxNewCommunityCMCompositionCohort(final String cohortName) {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName(cohortName);
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    txNewCompositionCohort.addSearch(
        "START-ART", EptsReportUtils.map(this.getTxNewCompositionCohort(cohortName), mappings));

    txNewCompositionCohort.addSearch(
        "WITH-COMMUNITY-DISPENSATION-CM",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedARTWithComunnityDispensation",
                TxNewQueries.QUERY.findPatientsWhoStartedARTAtComunnityCM),
            mappings));

    txNewCompositionCohort.setCompositionString("START-ART AND WITH-COMMUNITY-DISPENSATION-CM");

    return txNewCompositionCohort;
  }

  public CohortDefinition getTxNewCommunityBMCompositionCohort(final String cohortName) {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName(cohortName);
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    txNewCompositionCohort.addSearch(
        "START-ART", EptsReportUtils.map(this.getTxNewCompositionCohort(cohortName), mappings));

    txNewCompositionCohort.addSearch(
        "WITH-COMMUNITY-DISPENSATION-BM",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedARTWithComunnityDispensation",
                TxNewQueries.QUERY.findPatientsWhoStartedARTAtComunnityBM),
            mappings));

    txNewCompositionCohort.setCompositionString("START-ART AND WITH-COMMUNITY-DISPENSATION-BM");

    return txNewCompositionCohort;
  }

  public CohortDefinition getTxNewCommunityCompositionTypeCohort(
      final String cohortName, final CommunityType ct) {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName(cohortName);
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    txNewCompositionCohort.addSearch(
        "COMMUNITY-DISPENSATION-TYPE",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedARTWithComunnityDispensation",
                TxNewQueries.QUERY.findPatientsWhoStartedARTAtComunnity),
            mappings));

    txNewCompositionCohort.addSearch(
        "START-ART-WITH-COMMUNITY-DISPENSATION",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoStartedARTWithComunnityDispensation",
                TxNewQueries.QUERY.findPatientsInComunnityDispensationByType(ct)),
            mappings));

    txNewCompositionCohort.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod",
                TxNewQueries.QUERY.findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod),
            mappings));

    txNewCompositionCohort.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard",
                TxNewQueries.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    txNewCompositionCohort.setCompositionString(
        "(COMMUNITY-DISPENSATION-TYPE AND START-ART-WITH-COMMUNITY-DISPENSATION) NOT (TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD)");

    return txNewCompositionCohort;
  }

  public CohortDefinition findPatientsNewlyEnrolledByAgeInAPeriodExcludingBreastFeedingAndPregnant(
      final AgeRange ageRange) {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("patientsNewlyEnrolledByAgeInAPeriodExcludingBreastFeedingAndPregnant");

    definition.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    definition.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    definition.addSearch(
        "IART",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnArtByAgeRange(ageRange),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoArePregnantInAPeriod",
                PregnantQueries.findPatientsWhoArePregnantInAPeriod()),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.breastFeedingCohortQueries
                .findPatientsWhoAreBreastFeedingExcludingPregnantsInAPeriod(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));

    definition.setCompositionString("IART NOT (PREGNANT OR BREASTFEEDING)");

    return definition;
  }

  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnArtByAgeRange(final AgeRange ageRange) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = EptsQuerysUtils.loadQuery(FIND_AGE_PATIENTS_ON_TX_NEW);
    query = String.format(query, ageRange.getMin(), ageRange.getMax());

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsInComunnityDispensation")
  public CohortDefinition communityDispensation() {
    final CohortDefinition definition =
        this.genericCohorts.generalSql(
            "findPatientsInComunnityDispensation",
            TxNewQueries.QUERY.findPatientsInComunnityDispensation);

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }
}
