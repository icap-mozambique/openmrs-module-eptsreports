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

package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.PrepCtCommunityCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.PrepKeyPopulationDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PrepCtCommunityDataset extends BaseDataSet {

  @Autowired private PrepCtCommunityCohortQueries prepCommunityCohortQueries;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired private PrepKeyPopulationDimension prepKeyPopulationDimension;

  public DataSetDefinition constructPrepCtDataset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("PREP_CT Data Set");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition clientsNewlyEnrolledInPrep =
        this.prepCommunityCohortQueries.getClientsNewlyEnrolledInPrep();

    final CohortDefinition clientsWithPositiveTestResult =
        this.prepCommunityCohortQueries.getClientsWithPositiveTestResult();

    final CohortDefinition clientsWithNegativeTestResult =
        this.prepCommunityCohortQueries.getClientsWithNegativeTestResult();

    final CohortDefinition clientsWithIndeterminateTestResult =
        this.prepCommunityCohortQueries.getClientsWithIndeterminateTestResult();

    final CohortDefinition clientsWithPregnancyStatus =
        this.prepCommunityCohortQueries.getClientsWithPregnancyStatusDuringReportingPeriod();

    final CohortDefinition clientsWithBreastfeedingStatus =
        this.prepCommunityCohortQueries.getClientsWithBreastfeedingStatusDuringReportingPeriod();

    final CohortDefinition clientsWithPrepInterruptionReasonInfected =
        this.prepCommunityCohortQueries.getClientsWithPrepInterruptionReasonInfected();

    final CohortDefinition clientsWithPrepInterruptionReasonSideEffects =
        this.prepCommunityCohortQueries.getClientsWithPrepInterruptionReasonSideEffects();

    final CohortDefinition clientsWithPrepInterruptionReasonNoRisks =
        this.prepCommunityCohortQueries.getClientsWithPrepInterruptionReasonNoRisks();

    final CohortDefinition clientsWithPrepInterruptionReasonUserPreference =
        this.prepCommunityCohortQueries.getClientsWithPrepInterruptionReasonUserPreference();

    final CohortDefinition clientsWithPrepInterruptionReasonUserOther =
        this.prepCommunityCohortQueries.getClientsWithPrepInterruptionReasonUserOther();

    final CohortIndicator clientsNewlyEnrolledInPrepIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "clientsNewlyEnrolledInPrepIndicator",
            EptsReportUtils.map(clientsNewlyEnrolledInPrep, mappings));

    final CohortIndicator clientsWithPositiveTestResultIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "clientsNewlyEnrolledInPrepIndicator",
            EptsReportUtils.map(clientsWithPositiveTestResult, mappings));

    final CohortIndicator clientsWithNegativeTestResultIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "clientsNewlyEnrolledInPrepIndicator",
            EptsReportUtils.map(clientsWithNegativeTestResult, mappings));

    final CohortIndicator clientsWithIndeterminateTestResultIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "clientsNewlyEnrolledInPrepIndicator",
            EptsReportUtils.map(clientsWithIndeterminateTestResult, mappings));

    final CohortIndicator clientsWithPregnancyStatusIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "clientsNewlyEnrolledInPrepIndicator",
            EptsReportUtils.map(clientsWithPregnancyStatus, mappings));

    final CohortIndicator clientsWithBreastfeedingStatusIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "clientsNewlyEnrolledInPrepIndicator",
            EptsReportUtils.map(clientsWithBreastfeedingStatus, mappings));

    final CohortIndicator clientsWithPrepInterruptionReasonInfectedIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "clientsNewlyEnrolledInPrepIndicator",
            EptsReportUtils.map(clientsWithPrepInterruptionReasonInfected, mappings));

    final CohortIndicator clientsWithPrepInterruptionReasonSideEffectsIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "clientsNewlyEnrolledInPrepIndicator",
            EptsReportUtils.map(clientsWithPrepInterruptionReasonSideEffects, mappings));

    final CohortIndicator clientsWithPrepInterruptionReasonNoRisksIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "clientsNewlyEnrolledInPrepIndicator",
            EptsReportUtils.map(clientsWithPrepInterruptionReasonNoRisks, mappings));

    final CohortIndicator clientsWithPrepInterruptionReasonUserPreferenceIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "clientsNewlyEnrolledInPrepIndicator",
            EptsReportUtils.map(clientsWithPrepInterruptionReasonUserPreference, mappings));

    final CohortIndicator clientsWithPrepInterruptionReasonUserOtherIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "clientsNewlyEnrolledInPrepIndicator",
            EptsReportUtils.map(clientsWithPrepInterruptionReasonUserOther, mappings));

    dataSetDefinition.addDimension(
        "gender", EptsReportUtils.map(this.eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            this.eptsCommonDimension.age(this.ageDimensionCohort), "effectiveDate=${endDate}"));

    dataSetDefinition.addColumn(
        "P1",
        "Total Patients Initiated PREP",
        EptsReportUtils.map(clientsNewlyEnrolledInPrepIndicator, mappings),
        "");
    super.addRow(
        dataSetDefinition,
        "P2",
        "Age and Gender",
        EptsReportUtils.map(clientsNewlyEnrolledInPrepIndicator, mappings),
        this.getColumnsForAgeAndGender());

    dataSetDefinition.addColumn(
        "P2-TotalMale",
        " Age and Gender (Totals male) ",
        EptsReportUtils.map(clientsNewlyEnrolledInPrepIndicator, mappings),
        "gender=M");
    dataSetDefinition.addColumn(
        "P2-TotalFemale",
        "Age and Gender (Totals female) ",
        EptsReportUtils.map(clientsNewlyEnrolledInPrepIndicator, mappings),
        "gender=F");

    dataSetDefinition.addDimension(
        "homosexual",
        EptsReportUtils.map(
            this.prepKeyPopulationDimension.findPatientsWhoAreHomosexual(), mappings));

    dataSetDefinition.addDimension(
        "drug-user",
        EptsReportUtils.map(this.prepKeyPopulationDimension.findPatientsWhoUseDrugs(), mappings));

    dataSetDefinition.addDimension(
        "prisioner",
        EptsReportUtils.map(
            this.prepKeyPopulationDimension.findPatientsWhoAreInPrison(), mappings));

    dataSetDefinition.addDimension(
        "sex-worker",
        EptsReportUtils.map(
            this.prepKeyPopulationDimension.findPatientsWhoAreSexWorker(), mappings));

    dataSetDefinition.addDimension(
        "transgender",
        EptsReportUtils.map(
            this.prepKeyPopulationDimension.findPatientsWhoAreTransGender(), mappings));

    dataSetDefinition.addColumn(
        "PREP-C-All",
        "PREP_CT: PREP CT",
        EptsReportUtils.map(clientsNewlyEnrolledInPrepIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "PREP-C-POSITIVE",
        "PREP_CT: POSITIVE RESULT",
        EptsReportUtils.map(clientsWithPositiveTestResultIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "PREP-C-NEGATIVE",
        "PREP_CT: NEGATIVE RESULT",
        EptsReportUtils.map(clientsWithNegativeTestResultIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "PREP-C-INDETERMINATE",
        "PREP_CT: INDETERMINATE RESULT",
        EptsReportUtils.map(clientsWithIndeterminateTestResultIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "PREP-C-HSH",
        "Homosexual",
        EptsReportUtils.map(clientsNewlyEnrolledInPrepIndicator, mappings),
        "gender=M|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "PREP-C-PWID",
        "Drugs User",
        EptsReportUtils.map(clientsNewlyEnrolledInPrepIndicator, mappings),
        "drug-user=drug-user");

    dataSetDefinition.addColumn(
        "PREP-C-PRI",
        "Prisioners",
        EptsReportUtils.map(clientsNewlyEnrolledInPrepIndicator, mappings),
        "prisioner=prisioner");

    dataSetDefinition.addColumn(
        "PREP-C-FSW",
        "Sex Worker",
        EptsReportUtils.map(clientsNewlyEnrolledInPrepIndicator, mappings),
        "gender=F|sex-worker=sex-worker");

    dataSetDefinition.addColumn(
        "PREP-C-MSW",
        "Sex Worker",
        EptsReportUtils.map(clientsNewlyEnrolledInPrepIndicator, mappings),
        "gender=M|sex-worker=sex-worker");

    dataSetDefinition.addColumn(
        "PREP-C-TG",
        "Transgender",
        EptsReportUtils.map(clientsNewlyEnrolledInPrepIndicator, mappings),
        "transgender=transgender");

    dataSetDefinition.addColumn(
        "PREP-C-PREGNANT",
        "PREP_CT: PREGNANT STATUS",
        EptsReportUtils.map(clientsWithPregnancyStatusIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "PREP-C-BREASTFEEDING",
        "PREP_CT: BREASTFEEDING STATUS",
        EptsReportUtils.map(clientsWithBreastfeedingStatusIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "PREP-C-INFECTED",
        "Prep Interruption Reason - INFECTED",
        EptsReportUtils.map(clientsWithPrepInterruptionReasonInfectedIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "PREP-C-NORISKS",
        "Prep Interruption Reason - NO RISKS",
        EptsReportUtils.map(clientsWithPrepInterruptionReasonNoRisksIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "PREP-C-SIDEEFFECTS",
        "Prep Interruption Reason - SIDE EFFECTS",
        EptsReportUtils.map(clientsWithPrepInterruptionReasonSideEffectsIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "PREP-C-USERPREFERENCE",
        "Prep Interruption Reason - USER PREFERENCE",
        EptsReportUtils.map(clientsWithPrepInterruptionReasonUserPreferenceIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "PREP-C-OTHER",
        "Prep Interruption Reason - OTHER",
        EptsReportUtils.map(clientsWithPrepInterruptionReasonUserOtherIndicator, mappings),
        "");

    return dataSetDefinition;
  }

  private List<ColumnParameters> getColumnsForAgeAndGender() {

    final ColumnParameters fifteenTo19M =
        new ColumnParameters("fifteenTo19M", "15 - 19 male", "gender=M|age=15-19", "01");
    final ColumnParameters twentyTo24M =
        new ColumnParameters("twentyTo24M", "20 - 24 male", "gender=M|age=20-24", "02");
    final ColumnParameters twenty5To29M =
        new ColumnParameters("twenty4To29M", "25 - 29 male", "gender=M|age=25-29", "03");
    final ColumnParameters thirtyTo34M =
        new ColumnParameters("thirtyTo34M", "30 - 34 male", "gender=M|age=30-34", "04");
    final ColumnParameters thirty5To39M =
        new ColumnParameters("thirty5To39M", "35 - 39 male", "gender=M|age=35-39", "05");
    final ColumnParameters foutyTo44M =
        new ColumnParameters("foutyTo44M", "40 - 44 male", "gender=M|age=40-44", "06");
    final ColumnParameters fouty5To49M =
        new ColumnParameters("fouty5To49M", "45 - 49 male", "gender=M|age=45-49", "07");
    final ColumnParameters above50M =
        new ColumnParameters("above50M", "50+ male", "gender=M|age=50+", "08");
    final ColumnParameters unknownM =
        new ColumnParameters("unknownM", "Unknown age male", "gender=M|age=UK", "09");

    final ColumnParameters fifteenTo19F =
        new ColumnParameters("fifteenTo19F", "15 - 19 female", "gender=F|age=15-19", "10");
    final ColumnParameters twentyTo24F =
        new ColumnParameters("twentyTo24F", "20 - 24 female", "gender=F|age=20-24", "11");
    final ColumnParameters twenty5To29F =
        new ColumnParameters("twenty4To29F", "25 - 29 female", "gender=F|age=25-29", "12");
    final ColumnParameters thirtyTo34F =
        new ColumnParameters("thirtyTo34F", "30 - 34 female", "gender=F|age=30-34", "13");
    final ColumnParameters thirty5To39F =
        new ColumnParameters("thirty5To39F", "35 - 39 female", "gender=F|age=35-39", "14");
    final ColumnParameters foutyTo44F =
        new ColumnParameters("foutyTo44F", "40 - 44 female", "gender=F|age=40-44", "15");
    final ColumnParameters fouty5To49F =
        new ColumnParameters("fouty5To49F", "45 - 49 female", "gender=F|age=45-49", "16");
    final ColumnParameters above50F =
        new ColumnParameters("above50F", "50+ female", "gender=F|age=50+", "17");
    final ColumnParameters unknownF =
        new ColumnParameters("unknownF", "Unknown age female", "gender=F|age=UK", "18");
    final ColumnParameters total = new ColumnParameters("totals", "Totals", "", "19");

    return Arrays.asList(
        fifteenTo19M,
        twentyTo24M,
        twenty5To29M,
        thirtyTo34M,
        thirty5To39M,
        foutyTo44M,
        fouty5To49M,
        above50M,
        unknownM,
        fifteenTo19F,
        twentyTo24F,
        twenty5To29F,
        thirtyTo34F,
        thirty5To39F,
        foutyTo44F,
        fouty5To49F,
        above50F,
        unknownF,
        total);
  }
}
