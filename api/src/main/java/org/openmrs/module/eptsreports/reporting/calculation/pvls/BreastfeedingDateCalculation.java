package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientState;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.stereotype.Component;

/**
 * Calculates for patient eligibility to be breastfeeding
 *
 * @return CalculationResultMap
 */
@Component
public class BreastfeedingDateCalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      final Collection<Integer> cohort,
      final Map<String, Object> parameterValues,
      final PatientCalculationContext context) {

    // External Dependencies
    final HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    final EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);

    final CalculationResultMap resultMap = new CalculationResultMap();

    final Location location = (Location) context.getFromCache("location");

    final Concept viralLoadConcept = hivMetadata.getHivViralLoadConcept();
    final EncounterType labEncounterType = hivMetadata.getMisauLaboratorioEncounterType();
    final EncounterType adultFollowup = hivMetadata.getAdultoSeguimentoEncounterType();
    final EncounterType childFollowup = hivMetadata.getARVPediatriaSeguimentoEncounterType();

    final Concept breastfeedingConcept = hivMetadata.getBreastfeeding();
    final Concept yes = hivMetadata.getYesConcept();
    final Concept criteriaForHivStart = hivMetadata.getCriteriaForArtStart();
    final Concept priorDeliveryDate = hivMetadata.getPriorDeliveryDateConcept();
    final Date onOrBefore = (Date) context.getFromCache("onOrBefore");
    final Date oneYearBefore = EptsCalculationUtils.addMonths(onOrBefore, -12);

    final CalculationResultMap lactatingMap =
        ePTSCalculationService.getObs(
            breastfeedingConcept,
            null,
            cohort,
            Arrays.asList(location),
            Arrays.asList(yes),
            TimeQualifier.ANY,
            null,
            context);

    final CalculationResultMap criteriaHivStartMap =
        ePTSCalculationService.getObs(
            criteriaForHivStart,
            null,
            cohort,
            Arrays.asList(location),
            Arrays.asList(breastfeedingConcept),
            TimeQualifier.FIRST,
            null,
            context);

    final CalculationResultMap deliveryDateMap =
        ePTSCalculationService.getObs(
            priorDeliveryDate,
            null,
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    final CalculationResultMap patientStateMap =
        ePTSCalculationService.allPatientStates(
            cohort, location, hivMetadata.getPatientIsBreastfeedingWorkflowState(), context);

    final CalculationResultMap lastVl =
        ePTSCalculationService.lastObs(
            Arrays.asList(labEncounterType, adultFollowup, childFollowup),
            viralLoadConcept,
            location,
            oneYearBefore,
            onOrBefore,
            cohort,
            context);

    for (final Integer pId : cohort) {
      final Obs lastVlObs = EptsCalculationUtils.resultForPatient(lastVl, pId);
      final Date resultantDate =
          this.getResultantDate(
              lactatingMap, criteriaHivStartMap, deliveryDateMap, patientStateMap, pId, lastVlObs);
      resultMap.put(pId, new SimpleResult(resultantDate, this));
    }
    return resultMap;
  }

  private Date getResultantDate(
      final CalculationResultMap lactatingMap,
      final CalculationResultMap criteriaHivStartMap,
      final CalculationResultMap deliveryDateMap,
      final CalculationResultMap patientStateMap,
      final Integer pId,
      final Obs lastVlObs) {
    Date resultantDate = null;
    if (lastVlObs != null && lastVlObs.getObsDatetime() != null) {
      final Date lastVlDate = lastVlObs.getObsDatetime();

      final ListResult patientResult = (ListResult) patientStateMap.get(pId);

      final ListResult lactatingResults = (ListResult) lactatingMap.get(pId);
      final List<Obs> lactatingObs = EptsCalculationUtils.extractResultValues(lactatingResults);
      final Obs criteriaHivObs = EptsCalculationUtils.resultForPatient(criteriaHivStartMap, pId);
      final ListResult deliveryDateResult = (ListResult) deliveryDateMap.get(pId);
      final List<Obs> deliveryDateObsList =
          EptsCalculationUtils.extractResultValues(deliveryDateResult);
      final List<PatientState> patientStateList =
          EptsCalculationUtils.extractResultValues(patientResult);

      // get a list of all eligible dates
      final List<Date> allEligibleDates =
          Arrays.asList(
              this.isLactating(lastVlDate, lactatingObs),
              this.hasHIVStartDate(lastVlDate, criteriaHivObs),
              this.hasDeliveryDate(lastVlDate, deliveryDateObsList),
              this.isInBreastFeedingInProgram(lastVlDate, patientStateList));

      // have a resultant list of dates
      final List<Date> resultantList = new ArrayList<>();
      if (allEligibleDates.size() > 0) {
        for (final Date breastfeedingDate : allEligibleDates) {
          if (breastfeedingDate != null) {
            resultantList.add(breastfeedingDate);
          }
        }
      }
      if (resultantList.size() > 0) {
        Collections.sort(resultantList);
        // then pick the most recent entry, which is the last one
        resultantDate = resultantList.get(resultantList.size() - 1);
      }
    }
    return resultantDate;
  }

  private Date hasDeliveryDate(final Date lastVlDate, final List<Obs> deliveryDateObsList) {
    Date deliveryDate = null;
    for (final Obs deliverDateObs : deliveryDateObsList) {
      if (deliverDateObs.getValueDatetime() != null
          && this.isInBreastFeedingViralLoadRange(lastVlDate, deliverDateObs.getValueDatetime())) {
        deliveryDate = deliverDateObs.getValueDatetime();
      }
    }
    return deliveryDate;
  }

  private Date isLactating(final Date lastVlDate, final List<Obs> lactantObsList) {
    Date lactatingDate = null;
    for (final Obs lactantObs : lactantObsList) {
      if (lactantObs.getObsDatetime() != null
          && this.isInBreastFeedingViralLoadRange(
              lastVlDate, lactantObs.getEncounter().getEncounterDatetime())) {
        lactatingDate = lactantObs.getEncounter().getEncounterDatetime();
      }
    }
    return lactatingDate;
  }

  private Date hasHIVStartDate(final Date lastVlDate, final Obs hivStartDateObs) {
    Date hivStartDate = null;
    if (hivStartDateObs != null
        && this.isInBreastFeedingViralLoadRange(
            lastVlDate, hivStartDateObs.getEncounter().getEncounterDatetime())) {
      hivStartDate = hivStartDateObs.getEncounter().getEncounterDatetime();
    }
    return hivStartDate;
  }

  private Date isInBreastFeedingInProgram(
      final Date lastVlDate, final List<PatientState> patientStateList) {
    Date inProgramDate = null;
    if (!patientStateList.isEmpty()) {
      for (final PatientState patientState : patientStateList) {
        if (this.isInBreastFeedingViralLoadRange(lastVlDate, patientState.getStartDate())) {
          inProgramDate = patientState.getStartDate();
        }
      }
    }
    return inProgramDate;
  }

  private boolean isInBreastFeedingViralLoadRange(
      final Date viralLoadDate, final Date breastFeedingDate) {

    final Date startDate = EptsCalculationUtils.addMonths(viralLoadDate, -18);
    return breastFeedingDate.compareTo(startDate) >= 0
        && breastFeedingDate.compareTo(viralLoadDate) <= 0;
  }
}
