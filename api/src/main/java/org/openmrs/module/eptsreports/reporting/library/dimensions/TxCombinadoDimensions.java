/** */
package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxCombinadoDenominatorListCalculator;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxCombinadoNumeratorListCalculator;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseIcapCalculationDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxCombinadoQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.TxCombinadoType;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class TxCombinadoDimensions {

  @Autowired private GenericCohortQueries genericCohortQueries;

  public CohortDefinitionDimension findPatientsWhoArePregnant(final TxCombinadoType combinadoType) {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Pregnant");
    this.addParameters(dimension);

    final String mappings =
        "startDate=${startDate},endDate=${endDate},location=${location},months=${months}";

    if (TxCombinadoType.NUMERATOR.equals(combinadoType)) {
      this.denominatorPregnant(dimension, mappings);
      return dimension;
    }

    dimension.addCohortDefinition(
        "pregnant",
        EptsReportUtils.map(
            this.addParameters(
                this.genericCohortQueries.generalSql(
                    "pregnant", TxCombinadoQueries.QUERY.findPatientsWhoPregnant(combinadoType))),
            mappings));

    return dimension;
  }

  private void denominatorPregnant(
      final CohortDefinitionDimension dimension, final String mappings) {
    final CompositionCohortDefinition compositionCohortDefinition =
        new CompositionCohortDefinition();
    compositionCohortDefinition.setName("PREGNANT_DENOMINATOR");
    this.addParameters(compositionCohortDefinition);

    compositionCohortDefinition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(
            this.addParameters(
                this.genericCohortQueries.generalSql(
                    "Patients with consultation or drug pickup - D",
                    TxCombinadoQueries.QUERY
                        .findPatientsWithConsultationOrPickUpDrugsInaSpecifiedPeriod(
                            TxCombinadoType.DENOMINATOR))),
            mappings));

    compositionCohortDefinition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.addParameters(
                this.genericCohortQueries.generalSql(
                    "pregnant",
                    TxCombinadoQueries.QUERY.findPatientsWhoPregnant(TxCombinadoType.DENOMINATOR))),
            mappings));

    compositionCohortDefinition.setCompositionString("DENOMINATOR AND PREGNANT");

    final CohortDefinition cohortDefinition = compositionCohortDefinition;

    dimension.addCohortDefinition("pregnant", EptsReportUtils.map(cohortDefinition, mappings));
  }

  public CohortDefinitionDimension findPatientsWhoAreBreastFeeding(
      final TxCombinadoType combinadoType) {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("BreastFeeding");
    this.addParameters(dimension);

    final String mappings =
        "startDate=${startDate},endDate=${endDate},location=${location},months=${months}";

    if (TxCombinadoType.NUMERATOR.equals(combinadoType)) {
      this.denominatorBreatfeeding(dimension, mappings);
      return dimension;
    }

    dimension.addCohortDefinition(
        "breastfeeding",
        EptsReportUtils.map(
            this.addParameters(
                this.genericCohortQueries.generalSql(
                    "breastfeeding",
                    TxCombinadoQueries.QUERY.findPatientsWhoAreBreastFeeding(combinadoType))),
            mappings));

    return dimension;
  }

  private void denominatorBreatfeeding(
      final CohortDefinitionDimension dimension, final String mappings) {
    final CompositionCohortDefinition compositionCohortDefinition =
        new CompositionCohortDefinition();
    compositionCohortDefinition.setName("BREASTFEEDING_NUMERATOR");
    this.addParameters(compositionCohortDefinition);

    compositionCohortDefinition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(
            this.addParameters(
                this.genericCohortQueries.generalSql(
                    "Patients with consultation or drug pickup - D",
                    TxCombinadoQueries.QUERY
                        .findPatientsWithConsultationOrPickUpDrugsInaSpecifiedPeriod(
                            TxCombinadoType.DENOMINATOR))),
            mappings));

    compositionCohortDefinition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.addParameters(
                this.genericCohortQueries.generalSql(
                    "breastfeeding",
                    TxCombinadoQueries.QUERY.findPatientsWhoAreBreastFeeding(
                        TxCombinadoType.DENOMINATOR))),
            mappings));

    compositionCohortDefinition.setCompositionString("DENOMINATOR AND BREASTFEEDING");

    final CohortDefinition cohortDefinition = compositionCohortDefinition;

    dimension.addCohortDefinition("breastfeeding", EptsReportUtils.map(cohortDefinition, mappings));
  }

  @DocumentedDefinition(value = "TxCombinadoListN")
  public DataSetDefinition getTxCombinationListNumerator() {

    final DataSetDefinition definition =
        new BaseIcapCalculationDataSetDefinition(
            "TxCombinadoList",
            Context.getRegisteredComponents(TxCombinadoNumeratorListCalculator.class).get(0));

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    definition.addParameter(new Parameter("months", "Months", Integer.class));

    return definition;
  }

  @DocumentedDefinition(value = "TxCombinadoListD")
  public DataSetDefinition getTxCombinationListDenominator() {

    final DataSetDefinition definition =
        new BaseIcapCalculationDataSetDefinition(
            "TxCombinadoList",
            Context.getRegisteredComponents(TxCombinadoDenominatorListCalculator.class).get(0));

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    definition.addParameter(new Parameter("months", "Months", Integer.class));

    return definition;
  }

  private void addParameters(final CohortDefinitionDimension dimension) {
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));
    dimension.addParameter(new Parameter("months", "Months", Integer.class));
  }

  private CohortDefinition addParameters(final CohortDefinition definition) {
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    definition.addParameter(new Parameter("months", "Months", Integer.class));

    return definition;
  }
}
