package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import java.util.HashSet;
import java.util.List;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.cohort.definition.ResumoMensalTransferredOutCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = ResumoMensalTransferredOutCohortDefinition.class)
public class ResumoMensalTransferredOutCohortDefinitionEvaluator
    implements CohortDefinitionEvaluator {

  private HivMetadata hivMetadata;

  private EvaluationService evaluationService;

  @Autowired
  public ResumoMensalTransferredOutCohortDefinitionEvaluator(
      HivMetadata hivMetadata, EvaluationService evaluationService) {
    this.hivMetadata = hivMetadata;
    this.evaluationService = evaluationService;
  }

  @Override
  public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context)
      throws EvaluationException {

    ResumoMensalTransferredOutCohortDefinition cd =
        (ResumoMensalTransferredOutCohortDefinition) cohortDefinition;
    EvaluatedCohort ret = new EvaluatedCohort(null, cd, context);

    SqlQueryBuilder q = new SqlQueryBuilder();

    q.append("SELECT patient_id ");
    q.append("FROM (SELECT patient_id, ");
    q.append("             Max(transferout_date) transferout_date ");
    q.append("      FROM (SELECT p.patient_id, ");
    q.append("                   ps.start_date transferout_date ");
    q.append("            FROM patient p ");
    q.append("                     JOIN patient_program pp ");
    q.append("                          ON p.patient_id = pp.patient_id ");
    q.append("                     JOIN patient_state ps ");
    q.append("                          ON pp.patient_program_id = ps.patient_program_id ");
    q.append("            WHERE p.voided = 0 ");
    q.append("              AND pp.voided = 0 ");
    q.append("              AND pp.program_id = :art ");
    q.append("              AND pp.location_id = :location ");
    q.append("              AND ps.voided = 0 ");
    q.append("              AND ps.state = :transferOutState ");
    if (cd.getOnOrAfter() == null) {
      q.append("            AND ps.start_date <= :onOrBefore ");
    } else {
      q.append("            AND ps.start_date BETWEEN :onOrAfter AND :onOrBefore ");
    }
    q.append("            UNION ");
    q.append("            SELECT p.patient_id, ");
    q.append("                   e.encounter_datetime transferout_date ");
    q.append("            FROM patient p ");
    q.append("                     JOIN encounter e ");
    q.append("                          ON p.patient_id = e.patient_id ");
    q.append("                     JOIN obs o ");
    q.append("                          ON e.encounter_id = o.encounter_id ");
    q.append("            WHERE p.voided = 0 ");
    q.append("              AND e.voided = 0 ");
    q.append("              AND e.location_id = :location ");
    q.append("              AND e.encounter_type IN (:adultSeg, :masterCard) ");
    if (cd.getOnOrAfter() == null) {
      q.append("            AND e.encounter_datetime <= :onOrBefore ");
    } else {
      q.append("            AND e.encounter_datetime BETWEEN :onOrAfter AND :onOrBefore ");
    }
    q.append("              AND o.voided = 0 ");
    q.append("              AND o.concept_id IN (:artStateOfStay, :preArtStateOfStay) ");
    q.append("              AND o.value_coded = :transfOutConcept) transferout ");
    q.append("      GROUP BY patient_id) max_transferout ");
    q.append("WHERE patient_id NOT IN (SELECT p.patient_id ");
    q.append("                         FROM patient p ");
    q.append("                                  JOIN encounter e ");
    q.append("                                       ON p.patient_id = e.patient_id ");
    q.append("                         WHERE p.voided = 0 ");
    q.append("                           AND e.voided = 0 ");
    q.append("                           AND e.encounter_type IN (:adultSeg, :childSeg, :fila) ");
    q.append("                           AND e.location_id = :location ");
    q.append("                           AND e.encounter_datetime > transferout_date ");
    q.append("                           AND e.encounter_datetime <= :onOrBefore ");
    q.append("                         UNION ");
    q.append("                         SELECT p.patient_id ");
    q.append("                         FROM patient p ");
    q.append("                                  JOIN encounter e ");
    q.append("                                       ON p.patient_id = e.patient_id ");
    q.append("                                  JOIN obs o ");
    q.append("                                       ON e.encounter_id = o.encounter_id ");
    q.append("                         WHERE p.voided = 0 ");
    q.append("                           AND e.voided = 0 ");
    q.append("                           AND e.encounter_type = :mcDrugPickup ");
    q.append("                           AND e.location_id = :location ");
    q.append("                           AND o.concept_id = :drugPickup ");
    q.append("                           AND o.value_datetime ");
    q.append("                             > transferout_date ");
    q.append("                           AND o.value_datetime ");
    q.append("                             <= :onOrBefore)");

    q.addParameter("art", hivMetadata.getARTProgram().getProgramId());

    ProgramWorkflowState transferOut =
        hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState();
    q.addParameter("transferOutState", transferOut.getProgramWorkflowStateId());

    q.addParameter("adultSeg", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    q.addParameter("masterCard", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    q.addParameter("artStateOfStay", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    q.addParameter("preArtStateOfStay", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    q.addParameter("transfOutConcept", hivMetadata.getTransferredOutConcept().getConceptId());
    q.addParameter(
        "childSeg", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    q.addParameter("fila", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    q.addParameter(
        "mcDrugPickup", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    q.addParameter("drugPickup", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    q.addParameter("location", cd.getLocation());
    q.addParameter("onOrAfter", cd.getOnOrAfter());
    q.addParameter("onOrBefore", DateUtil.getEndOfDayIfTimeExcluded(cd.getOnOrBefore()));

    List<Integer> results = evaluationService.evaluateToList(q, Integer.class, context);
    ret.setMemberIds(new HashSet<>(results));
    return ret;
  }
}