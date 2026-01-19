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
package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.PvlsCommunityCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.KeyPopulationDimension;
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
public class TxPvlsCommunityDataset extends BaseDataSet {
  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private PvlsCommunityCohortQueries pvlsCohortQueries;

  @Autowired private KeyPopulationDimension keyPopulationDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructTxPvlsDatset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dataSetDefinition.setName("TX_PVLS Data Set");

    dataSetDefinition.addParameters(this.getParameters());

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));

    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    CohortDefinition patientsWhithViralLoadDenominator =
        this.pvlsCohortQueries
            .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months();

    CohortDefinition patientsWhithViralLoadNumerator =
        this.pvlsCohortQueries
            .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months();

    final CohortIndicator patientsWhithViralLoadIndicatorDenominator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsWhithViralLoadIndicatorDenominator",
            EptsReportUtils.map(patientsWhithViralLoadDenominator, mappings));

    final CohortIndicator patientsWhithViralLoadIndicatorNumerator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsWhithViralLoadIndicatorNumerator",
            EptsReportUtils.map(patientsWhithViralLoadNumerator, mappings));

    addRow(
        dataSetDefinition,
        "DEN",
        "Number of patients on ART for at least 90 days with a VL result - Denominator",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorDenominator, mappings),
        getAdultChildrenColumns());

    addRow(
        dataSetDefinition,
        "NUM",
        "Number of patients on ART for at least 90 days with suppressed VL - Numerator",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorNumerator, mappings),
        getAdultChildrenColumns());

    dataSetDefinition.addColumn(
        "DEN-TOTAL",
        "Total missed appointments",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorDenominator, mappings),
        "");

    dataSetDefinition.addColumn(
        "NUM-TOTAL",
        "Total missed appointments",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorNumerator, mappings),
        "");

    dataSetDefinition.addColumn(
        "M1-TotalMale",
        " Age and Gender (Totals male) ",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorDenominator, mappings),
        "gender=M");
    dataSetDefinition.addColumn(
        "F1-TotalFemale",
        "Age and Gender (Totals female) ",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorDenominator, mappings),
        "gender=F");

    dataSetDefinition.addColumn(
        "M2-TotalMale",
        " Age and Gender (Totals male) ",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorNumerator, mappings),
        "gender=M");
    dataSetDefinition.addColumn(
        "F2-TotalFemale",
        "Age and Gender (Totals female) ",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorNumerator, mappings),
        "gender=F");

    dataSetDefinition.addColumn(
        "DPREGNANT",
        "Pregant routine",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregant denominator",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsDenominator(),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "NPREGNANT",
        "pregnant target",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Pregant numerator",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsNumerator(),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "DBREASTFEEDING",
        "Breastfeeding Denominator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Breastfeeding denominator",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsDenominator(),
                    mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "NBREASTFEEDING",
        "Breastfeeding Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Breastfeeding numerator",
                EptsReportUtils.map(
                    pvlsCohortQueries
                        .findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsNumerator(),
                    mappings)),
            mappings),
        "");

    // Key Population dimension

    dataSetDefinition.addDimension(
        "homosexual",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreHomosexual(), mappings));

    dataSetDefinition.addDimension(
        "drug-user",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoUseDrugs(), mappings));

    dataSetDefinition.addDimension(
        "prisioner",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreInPrison(), mappings));

    dataSetDefinition.addDimension(
        "sex-worker",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreSexWorker(), mappings));

    // Key population collumn denominator

    dataSetDefinition.addColumn(
        "DMSM",
        "Homosexual",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorDenominator, mappings),
        "gender=M|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "DPWID",
        "Drugs User",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorDenominator, mappings),
        "drug-user=drug-user");

    dataSetDefinition.addColumn(
        "DPRI",
        "Prisioners",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorDenominator, mappings),
        "prisioner=prisioner");

    dataSetDefinition.addColumn(
        "DFSW",
        "Sex Worker",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorDenominator, mappings),
        "gender=F|sex-worker=sex-worker");

    // Key population collumn Numerator

    dataSetDefinition.addColumn(
        "NPWID",
        "Drugs User",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorNumerator, mappings),
        "drug-user=drug-user");

    dataSetDefinition.addColumn(
        "NMSM",
        "Homosexual",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorNumerator, mappings),
        "gender=M|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "NFSW",
        "Sex Worker",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorNumerator, mappings),
        "gender=F|sex-worker=sex-worker");

    dataSetDefinition.addColumn(
        "NPRI",
        "Prisioners",
        EptsReportUtils.map(patientsWhithViralLoadIndicatorNumerator, mappings),
        "prisioner=prisioner");

    return dataSetDefinition;
  }

  private List<ColumnParameters> getAdultChildrenColumns() {
    // Male
    ColumnParameters under1M =
        new ColumnParameters("under1M", "under 1 year male", "gender=M|age=<1", "01");
    ColumnParameters oneTo4M =
        new ColumnParameters("oneTo4M", "1 - 4 years male", "gender=M|age=1-4", "02");
    ColumnParameters fiveTo9M =
        new ColumnParameters("fiveTo9M", "5 - 9 years male", "gender=M|age=5-9", "03");
    ColumnParameters tenTo14M =
        new ColumnParameters("tenTo14M", "10 - 14 male", "gender=M|age=10-14", "04");
    ColumnParameters fifteenTo19M =
        new ColumnParameters("fifteenTo19M", "15 - 19 male", "gender=M|age=15-19", "05");
    ColumnParameters twentyTo24M =
        new ColumnParameters("twentyTo24M", "20 - 24 male", "gender=M|age=20-24", "06");
    ColumnParameters twenty5To29M =
        new ColumnParameters("twenty4To29M", "25 - 29 male", "gender=M|age=25-29", "07");
    ColumnParameters thirtyTo34M =
        new ColumnParameters("thirtyTo34M", "30 - 34 male", "gender=M|age=30-34", "08");
    ColumnParameters thirty5To39M =
        new ColumnParameters("thirty5To39M", "35 - 39 male", "gender=M|age=35-39", "09");
    ColumnParameters foutyTo44M =
        new ColumnParameters("foutyTo44M", "40 - 44 male", "gender=M|age=40-44", "10");
    ColumnParameters fouty5To49M =
        new ColumnParameters("fouty5To49M", "45 - 49 male", "gender=M|age=45-49", "11");

    ColumnParameters fiftyT054 =
        new ColumnParameters("fiftyT054", "50 - 54 male", "gender=M|age=50-54", "12");

    ColumnParameters fiftyfiveT059 =
        new ColumnParameters("fouty5To49M", "55 - 59 male", "gender=M|age=55-59", "13");

    ColumnParameters sixtyT064 =
        new ColumnParameters("fiftyfiveT059", "60 - 64 male", "gender=M|age=60-64", "14");

    ColumnParameters above65 =
        new ColumnParameters("above65", "65+  male", "gender=M|age=65+", "15");
    ColumnParameters unknownM =
        new ColumnParameters("unknownM", "Unknown age male", "gender=M|age=UK", "16");

    // Female
    ColumnParameters under1F =
        new ColumnParameters("under1F", "under 1 year female", "gender=F|age=<1", "17");
    ColumnParameters oneTo4F =
        new ColumnParameters("oneTo4F", "1 - 4 years female", "gender=F|age=1-4", "18");
    ColumnParameters fiveTo9F =
        new ColumnParameters("fiveTo9F", "5 - 9 years female", "gender=F|age=5-9", "19");
    ColumnParameters tenTo14F =
        new ColumnParameters("tenTo14F", "10 - 14 female", "gender=F|age=10-14", "20");
    ColumnParameters fifteenTo19F =
        new ColumnParameters("fifteenTo19F", "15 - 19 female", "gender=F|age=15-19", "21");
    ColumnParameters twentyTo24F =
        new ColumnParameters("twentyTo24F", "20 - 24 female", "gender=F|age=20-24", "22");
    ColumnParameters twenty5To29F =
        new ColumnParameters("twenty4To29F", "25 - 29 female", "gender=F|age=25-29", "23");
    ColumnParameters thirtyTo34F =
        new ColumnParameters("thirtyTo34F", "30 - 34 female", "gender=F|age=30-34", "24");
    ColumnParameters thirty5To39F =
        new ColumnParameters("thirty5To39F", "35 - 39 female", "gender=F|age=35-39", "25");
    ColumnParameters foutyTo44F =
        new ColumnParameters("foutyTo44F", "40 - 44 female", "gender=F|age=40-44", "26");
    ColumnParameters fouty5To49F =
        new ColumnParameters("fouty5To49F", "45 - 49 female", "gender=F|age=45-49", "27");

    ColumnParameters fiftyT054F =
        new ColumnParameters("fiftyT054F", "50 - 54 female", "gender=F|age=50-54", "28");

    ColumnParameters fiftyfiveT059F =
        new ColumnParameters("fiftyfiveT059F", "55 - 59 female", "gender=F|age=55-59", "29");

    ColumnParameters sixtyT064F =
        new ColumnParameters("sixtyT064F", "60 - 64 female", "gender=F|age=60-64", "30");

    ColumnParameters above65F =
        new ColumnParameters("above65", "65+ female", "gender=F|age=65+", "31");

    ColumnParameters unknownF =
        new ColumnParameters("unknownF", "Unknown age female", "gender=F|age=UK", "32");

    return Arrays.asList(
        unknownM,
        under1M,
        oneTo4M,
        fiveTo9M,
        tenTo14M,
        fifteenTo19M,
        twentyTo24M,
        twenty5To29M,
        thirtyTo34M,
        thirty5To39M,
        foutyTo44M,
        fouty5To49M,
        unknownF,
        under1F,
        oneTo4F,
        fiveTo9F,
        tenTo14F,
        fifteenTo19F,
        twentyTo24F,
        twenty5To29F,
        thirtyTo34F,
        thirty5To39F,
        foutyTo44F,
        fouty5To49F,
        fiftyT054,
        fiftyfiveT059,
        sixtyT064,
        above65,
        fiftyT054F,
        fiftyfiveT059F,
        sixtyT064F,
        above65F);
  }
}
