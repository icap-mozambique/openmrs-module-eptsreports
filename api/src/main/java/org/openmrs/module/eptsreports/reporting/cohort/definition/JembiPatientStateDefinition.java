package org.openmrs.module.eptsreports.reporting.cohort.definition;

import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.JembiPatientStateDefinition")
public class JembiPatientStateDefinition extends BaseDataDefinition
    implements PatientDataDefinition {

  @ConfigurationProperty(required = true)
  private List<ProgramWorkflowState> states;

  @ConfigurationProperty(required = true)
  private Date startedOnOrBefore;

  @ConfigurationProperty(group = "location")
  private Location location;

  @ConfigurationProperty private TimeQualifier which;

  @Override
  public Class<?> getDataType() {
    return PatientState.class;
  }

  public List<ProgramWorkflowState> getStates() {
    return this.states;
  }

  public void setStates(final List<ProgramWorkflowState> states) {
    this.states = states;
  }

  public Date getStartedOnOrBefore() {
    return this.startedOnOrBefore;
  }

  public void setStartedOnOrBefore(final Date startedOnOrBefore) {
    this.startedOnOrBefore = startedOnOrBefore;
  }

  public Location getLocation() {
    return this.location;
  }

  public void setLocation(final Location location) {
    this.location = location;
  }

  public TimeQualifier getWhich() {
    return this.which;
  }

  public void setWhich(final TimeQualifier which) {
    this.which = which;
  }
}
