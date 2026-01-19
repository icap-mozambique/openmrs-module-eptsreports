/** */
package org.openmrs.module.eptsreports.reporting.cohort.definition;

import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/** @author Stélio Moiane */
@Caching(strategy = ConfigurationPropertyAndParameterCachingStrategy.class)
public class BaseIcapCalculationDataSetDefinition extends BaseDataSetDefinition {

  private static final long serialVersionUID = 1L;

  @ConfigurationProperty(required = true, group = "calculation")
  private BaseFghCalculation calculation;

  public BaseIcapCalculationDataSetDefinition() {}

  public BaseIcapCalculationDataSetDefinition(
      final String name, final BaseFghCalculation calculation) {
    this.calculation = calculation;
    this.setName(name);
  }

  public BaseFghCalculation getCalculation() {
    return this.calculation;
  }

  public void setCalculation(final BaseFghCalculation calculation) {
    this.calculation = calculation;
  }
}
