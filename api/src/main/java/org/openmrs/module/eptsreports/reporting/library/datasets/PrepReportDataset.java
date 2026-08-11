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

import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.ABOVE_FIFTY;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FIFTEEN_TO_NINETEEN;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FORTY_FIVE_TO_FORTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FORTY_TO_FORTY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.THIRTY_FIVE_TO_THIRTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.THIRTY_TO_THRITY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.TWENTY_FIVE_TO_TWENTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.TWENTY_TO_TWENTY_FOUR;

import org.openmrs.module.eptsreports.reporting.library.cohorts.PrepKp;
import org.openmrs.module.eptsreports.reporting.library.cohorts.PrepReportCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.KeyPopulationDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.Gender;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PrepReportDataset extends BaseDataSet {

	@Autowired
	private PrepReportCohortQueries prepReportCohortQueries;

	@Autowired
	private EptsGeneralIndicator eptsGeneralIndicator;

	@Autowired
	private EptsCommonDimension eptsCommonDimension;

	@Autowired
	private KeyPopulationDimension keyPopulationDimension;

	@Autowired
	@Qualifier("commonAgeDimensionCohort")
	private AgeDimensionCohortInterface ageDimensionCohort;

	public CohortIndicatorDataSetDefinition getPrepReportDataSetDefinition() {

		final CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
		dataSetDefinition.setName("PREP CDC REPORTData Set");
		dataSetDefinition.addParameters(this.getParameters());

		final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

		final CohortDefinition cohortDefinition = this.prepReportCohortQueries.findPatientsOnPrepInjectable();

		final CohortIndicator indicator = this.eptsGeneralIndicator.getIndicator(
				"findPatientsOnPrepInjectable",
				EptsReportUtils.map(cohortDefinition, mappings));

		dataSetDefinition.addDimension(
				"gender", EptsReportUtils.map(this.eptsCommonDimension.gender(), ""));

		dataSetDefinition.addDimension(
				"age",
				EptsReportUtils.map(
						this.eptsCommonDimension.age(this.ageDimensionCohort), "effectiveDate=${endDate}"));

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

		dataSetDefinition.addDimension(
				"military",
				EptsReportUtils.map(this.prepReportCohortQueries.getPrepKp(PrepKp.MILITARY), mappings));

		dataSetDefinition.addDimension(
				"miner",
				EptsReportUtils.map(this.prepReportCohortQueries.getPrepKp(PrepKp.MINER), mappings));

		dataSetDefinition.addDimension(
				"driver",
				EptsReportUtils.map(this.prepReportCohortQueries.getPrepKp(PrepKp.DRIVER), mappings));

		dataSetDefinition.addDimension(
				"couples-result",
				EptsReportUtils.map(this.prepReportCohortQueries.getPrepKp(PrepKp.COUPLES_RESULT_ARE_DIFFERENT), mappings));

		dataSetDefinition.addDimension(
				"pregnant",
				EptsReportUtils.map(this.prepReportCohortQueries.getPrepKp(PrepKp.PREGNANT), mappings));

		dataSetDefinition.addDimension(
				"breastfeeding",
				EptsReportUtils.map(this.prepReportCohortQueries.getPrepKp(PrepKp.BREASTFEEDING), mappings));

		this.addDimensions(
				dataSetDefinition,
				mappings,
				FIFTEEN_TO_NINETEEN,
				TWENTY_TO_TWENTY_FOUR,
				TWENTY_FIVE_TO_TWENTY_NINE,
				THIRTY_TO_THRITY_FOUR,
				THIRTY_FIVE_TO_THIRTY_NINE,
				FORTY_TO_FORTY_FOUR,
				FORTY_FIVE_TO_FORTY_NINE,
				ABOVE_FIFTY);

		dataSetDefinition.addDimension(
				this.getName(Gender.MALE, AgeRange.UNKNOWN),
				EptsReportUtils.map(
						this.eptsCommonDimension.findPatientsWithUnknownAgeByGender(
								this.getName(Gender.MALE, AgeRange.UNKNOWN), Gender.MALE),
						""));

		dataSetDefinition.addDimension(
				this.getName(Gender.FEMALE, AgeRange.UNKNOWN),
				EptsReportUtils.map(
						this.eptsCommonDimension.findPatientsWithUnknownAgeByGender(
								this.getName(Gender.FEMALE, AgeRange.UNKNOWN), Gender.FEMALE),
						""));

		this.addColums(
				dataSetDefinition,
				mappings,
				indicator,
				FIFTEEN_TO_NINETEEN,
				TWENTY_TO_TWENTY_FOUR,
				TWENTY_FIVE_TO_TWENTY_NINE,
				THIRTY_TO_THRITY_FOUR,
				THIRTY_FIVE_TO_THIRTY_NINE,
				FORTY_TO_FORTY_FOUR,
				FORTY_FIVE_TO_FORTY_NINE,
				ABOVE_FIFTY);

		dataSetDefinition.addColumn(
				"P-malesUnknownM",
				"unknownM",
				EptsReportUtils.map(indicator, mappings),
				this.getName(Gender.MALE, AgeRange.UNKNOWN)
				+ "="
				+ this.getName(Gender.MALE, AgeRange.UNKNOWN));

		dataSetDefinition.addColumn(
				"P-femalesUnknownF",
				"unknownF",
				EptsReportUtils.map(indicator, mappings),
				this.getName(Gender.FEMALE, AgeRange.UNKNOWN)
				+ "="
				+ this.getName(Gender.FEMALE, AgeRange.UNKNOWN));

		dataSetDefinition.addColumn(
				"P-MSM",
				"Men who have sex with men (MSM)",
				EptsReportUtils.map(indicator, mappings),
				"gender=M|homosexual=homosexual");

		dataSetDefinition.addColumn(
				"P-PWID",
				"People who inject drugs (PWID)",
				EptsReportUtils.map(indicator, mappings),
				"drug-user=drug-user");

		dataSetDefinition.addColumn(
				"P-PRI",
				"People in prison and other closed settings",
				EptsReportUtils.map(indicator, mappings),
				"prisioner=prisioner");

		dataSetDefinition.addColumn(
				"P-FSW",
				"Female sex workers (FSW)",
				EptsReportUtils.map(indicator, mappings),
				"gender=F|sex-worker=sex-worker");

		dataSetDefinition.addColumn(
				"P-MILITARY",
				"Military",
				EptsReportUtils.map(indicator, mappings),
				"military=military");

		dataSetDefinition.addColumn(
				"P-MINER",
				"Miner",
				EptsReportUtils.map(indicator, mappings),
				"miner=miner");

		dataSetDefinition.addColumn(
				"P-DRIVER",
				"Driver",
				EptsReportUtils.map(indicator, mappings),
				"driver=driver");

		dataSetDefinition.addColumn(
				"P-COUPLES",
				"Couples result are different",
				EptsReportUtils.map(indicator, mappings),
				"couples-result=couples-result");

		dataSetDefinition.addColumn(
				"P-PREGNANT",
				"Pregnant",
				EptsReportUtils.map(indicator, mappings),
				"pregnant=pregnant");

		dataSetDefinition.addColumn(
				"P-BREASTFEEDING",
				"Breastfeeding",
				EptsReportUtils.map(indicator, mappings),
				"breastfeeding=breastfeeding");

		return dataSetDefinition;
	}

	private void addColums(
			final CohortIndicatorDataSetDefinition dataSetDefinition,
			final String mappings,
			final CohortIndicator cohortIndicator,
			final AgeRange... rannges) {

		for (final AgeRange range : rannges) {

			final String maleName = this.getName(Gender.MALE, range);
			final String femaleName = this.getName(Gender.FEMALE, range);

			dataSetDefinition.addColumn(
					maleName,
					maleName.replace("-", " "),
					EptsReportUtils.map(cohortIndicator, mappings),
					maleName + "=" + maleName);

			dataSetDefinition.addColumn(
					femaleName,
					femaleName.replace("-", " "),
					EptsReportUtils.map(cohortIndicator, mappings),
					femaleName + "=" + femaleName);
		}
	}

	private void addDimensions(
			final CohortIndicatorDataSetDefinition cohortIndicatorDataSetDefinition,
			final String mappings,
			final AgeRange... ranges) {

		for (final AgeRange range : ranges) {

			cohortIndicatorDataSetDefinition.addDimension(
					this.getName(Gender.MALE, range),
					EptsReportUtils.map(
							this.eptsCommonDimension.findPatientsByGenderAndRange(
									this.getName(Gender.MALE, range), range, Gender.MALE),
							mappings));

			cohortIndicatorDataSetDefinition.addDimension(
					this.getName(Gender.FEMALE, range),
					EptsReportUtils.map(
							this.eptsCommonDimension.findPatientsByGenderAndRange(
									this.getName(Gender.FEMALE, range), range, Gender.FEMALE),
							mappings));
		}
	}

	private String getName(final Gender gender, final AgeRange ageRange) {
		String name = "P-males-" + ageRange.getName() + "" + gender.getName();

		if (gender.equals(Gender.FEMALE)) {
			name = "P-females-" + ageRange.getName() + "" + gender.getName();
		}

		return name;
	}
}
