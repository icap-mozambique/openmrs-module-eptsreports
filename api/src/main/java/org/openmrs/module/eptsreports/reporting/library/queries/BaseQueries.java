package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public class BaseQueries {
	
	// State ids are left as hard coded for now because all reference same concept
	// they map to concept_id=1369 - TRANSFER FROM OTHER FACILITY
	// TODO: Query needs to be refactored
	public static String getBaseCohortQuery(int arvAdultInitialEncounterTypeId, int arvPediatriaInitialEncounterTypeId,
	        int hivCareProgramId, int artProgramId) {
		String query = "select p.patient_id from patient p join encounter e on e.patient_id=p.patient_id "
		        + "where e.voided=0 and p.voided=0 and e.encounter_type in (%s) and e.encounter_datetime<=:endDate and e.location_id = :location "
		        + "union "
		        + "select pg.patient_id from patient p join patient_program pg on p.patient_id=pg.patient_id where pg.voided=0 and p.voided=0 and program_id=%s and date_enrolled<=:endDate and location_id=:location "
		        + "union "
		        + "select pg.patient_id from patient p join patient_program pg on p.patient_id=pg.patient_id join patient_state ps on pg.patient_program_id=ps.patient_program_id "
		        + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%s and ps.state=28 and ps.start_date=pg.date_enrolled and ps.start_date<=:endDate and location_id=:location "
		        + "union "
		        + "select pg.patient_id from patient p join patient_program pg on p.patient_id=pg.patient_id join patient_state ps on pg.patient_program_id=ps.patient_program_id "
		        + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%s and ps.state=29 and ps.start_date<=:endDate and location_id=:location "
		        + "union "
		        + "select patient.patient_id from patient join person on person.person_id = patient.patient_id where patient.voided = 0 and person.birthdate is null ";
		String encounterTypes = StringUtils.join(Arrays.asList(arvAdultInitialEncounterTypeId, arvPediatriaInitialEncounterTypeId),
		    ',');
		return String.format(query, encounterTypes, hivCareProgramId, hivCareProgramId, artProgramId);
	}
}
