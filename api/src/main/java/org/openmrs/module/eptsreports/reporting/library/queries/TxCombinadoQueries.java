/** */
package org.openmrs.module.eptsreports.reporting.library.queries;

import org.openmrs.module.eptsreports.reporting.utils.TxCombinadoType;

/** @author Stélio Moiane */
public interface TxCombinadoQueries {

  class QUERY {

    public static final String findPatientsWithConsultationOrPickUpDrugsInaSpecifiedPeriod(
        final TxCombinadoType combinadoType) {

      String query =
          "SELECT patient_id FROM\n"
              + "( \n"
              + "	SELECT max_encounter_date.patient_id, MAX(last_encounter_date) last_encounter_date FROM \n"
              + "		   (\n"
              + "			SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_date FROM patient p\n"
              + "				INNER JOIN encounter e ON p.patient_id=e.patient_id\n"
              + "						WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) \n"
              + "							AND e.encounter_datetime BETWEEN ( :startDate - INTERVAL ( :months) MONTH) AND ( :endDate - INTERVAL ( :months) MONTH) AND e.location_id = :location\n"
              + "								GROUP BY p.patient_id\n"
              + "			UNION\n"
              + "			\n"
              + "			SELECT o.person_id patient_id, MAX(o.value_datetime) last_encounter_date FROM obs o\n"
              + "			                INNER JOIN encounter e ON e.encounter_id = o.encounter_id\n"
              + "			                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 \n"
              + "			                    AND o.voided = 0 AND e.voided = 0 \n"
              + "			                    AND o.value_datetime BETWEEN( :startDate - INTERVAL ( :months) MONTH) AND ( :endDate - INTERVAL ( :months) MONTH) AND e.location_id = :location\n"
              + "			                        GROUP BY o.person_id\n"
              + "	     )max_encounter_date GROUP BY patient_id \n"
              + ")patients_with_encounter_or_pickup_durgs";

      query =
          query
              + " WHERE patient_id NOT IN ("
              + QUERY
                  .findPatientsWhoWhereTransferredOutDeathOrSuspendedTreatmentInSpecificPeriodAndStage(
                      ErimType.TOTAL)
              + " )";

      if (TxCombinadoType.NUMERATOR.equals(combinadoType)) {
        query = query.replace("( :months)", "0");
      }

      return query;
    }

    public static final String
        findPatientsWhoWhereTransferredOutDeathOrSuspendedTreatmentInSpecificPeriodAndStage(
            final ErimType stateType) {

      String query =
          "SELECT patient_id FROM (\n"
              + "	SELECT patient_id,MAX(data_estado) data_estado, state_id FROM \n"
              + "	    ( \n"
              + "	        SELECT pg.patient_id, MAX(ps.start_date) data_estado, ps.state AS state_id FROM patient p \n"
              + "	            INNER JOIN patient_program pg ON p.patient_id=pg.patient_id \n"
              + "	            INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id \n"
              + "	                WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND pg.program_id=2 AND ps.state in (7,8,10) AND ps.end_date IS NULL \n"
              + "	                AND ps.start_date BETWEEN ( :startDate - INTERVAL ( :months) MONTH) AND ( :endDate - INTERVAL ( :months) MONTH) AND location_id= :location \n"
              + "	                    GROUP BY pg.patient_id \n"
              + "	        UNION \n"
              + "	               \n"
              + "	        SELECT p.patient_id, MAX(o.obs_datetime) data_estado, IF(o.value_coded=1706,7,if(o.value_coded=1366,10,8)) as state_id FROM patient p\n"
              + "	            INNER JOIN encounter e ON p.patient_id=e.patient_id \n"
              + "	            INNER JOIN obs  o on e.encounter_id=o.encounter_id\n"
              + "	                WHERE e.voided=0 AND o.voided=0 AND p.voided=0 AND e.encounter_type IN (53,6) \n"
              + "	                AND o.concept_id IN (6272,6273) AND o.value_coded IN (1706,1366,1709) \n"
              + "	                AND o.obs_datetime BETWEEN ( :startDate - INTERVAL ( :months) MONTH) AND ( :endDate - INTERVAL ( :months) MONTH) AND e.location_id= :location \n"
              + "	                    GROUP BY p.patient_id \n"
              + "	        UNION \n"
              + "	        \n"
              + "	        SELECT person_id AS patient_id, death_date AS data_estado,10 AS state_id FROM person \n"
              + "	            WHERE dead=1 AND death_date is not null AND death_date BETWEEN ( :startDate - INTERVAL ( :months) MONTH) AND ( :endDate - INTERVAL ( :months) MONTH) \n"
              + "	        \n"
              + "	        UNION \n"
              + "	               \n"
              + "	        SELECT p.patient_id, MAX(obs_death.obs_datetime) data_estado,10 AS state_id FROM patient p \n"
              + "	            INNER JOIN encounter e ON p.patient_id=e.patient_id \n"
              + "	            INNER JOIN obs obs_death ON e.encounter_id=obs_death.encounter_id \n"
              + "	                WHERE e.voided=0 AND p.voided=0 AND obs_death.voided=0 AND e.encounter_type IN (21,36,37) \n"
              + "	                    AND e.encounter_datetime BETWEEN ( :startDate - INTERVAL ( :months) MONTH) AND ( :endDate - INTERVAL ( :months) MONTH) AND e.location_id= :location AND obs_death.concept_id in (2031,23944,23945) AND obs_death.value_coded=1366 \n"
              + "	                            GROUP BY p.patient_id\n"
              + "	    )patient_max_state_date GROUP BY patient_id\n"
              + ")patient_transferred_out_death_or_suspended";

      switch (stateType) {
        case DEAD:
          query = query + " WHERE state_id = 10";
          query =
              query.replace(
                  "BETWEEN ( :startDate - INTERVAL ( :months) MONTH) AND ( :endDate - INTERVAL ( :months) MONTH)",
                  "BETWEEN ( :startDate - INTERVAL ( :months) MONTH) AND :endDate");
          break;

        case TRANFERED_OUT:
          query = query + " WHERE state_id = 7";
          query =
              query.replace(
                  "BETWEEN ( :startDate - INTERVAL ( :months) MONTH) AND ( :endDate - INTERVAL ( :months) MONTH)",
                  "BETWEEN ( :startDate - INTERVAL ( :months) MONTH) AND :endDate");
          break;

        case SPTOPPED_TREATMENT:
          query = query + " WHERE state_id = 8";
          query =
              query.replace(
                  "BETWEEN ( :startDate - INTERVAL ( :months) MONTH) AND ( :endDate - INTERVAL ( :months) MONTH)",
                  "BETWEEN ( :startDate - INTERVAL ( :months) MONTH) AND :endDate");
          break;

        default:
          query = query + "";
          break;
      }

      return query;
    }

    public static final String findPatientsWhoAreBreastFeeding(
        final TxCombinadoType txCombinadoType) {
      String query = BreastfeedingQueries.findPatientsWhoAreBreastfeeding();

      if (TxCombinadoType.DENOMINATOR.equals(txCombinadoType)) {
        query =
            query.replace(
                "between :startDate and :endDate",
                "BETWEEN ( :startDate - INTERVAL ( :months) MONTH) AND ( :endDate - INTERVAL ( :months) MONTH)");

        return query;
      }

      return query;
    }

    public static final String findPatientsWhoPregnant(final TxCombinadoType txCombinadoType) {
      String query = PregnantQueries.findPatientsWhoArePregnantInAPeriod();

      if (TxCombinadoType.DENOMINATOR.equals(txCombinadoType)) {
        query =
            query.replace(
                "between :startDate and :endDate",
                "BETWEEN ( :startDate - INTERVAL ( :months) MONTH) AND ( :endDate - INTERVAL ( :months) MONTH)");

        return query;
      }

      return query;
    }
  }
}
