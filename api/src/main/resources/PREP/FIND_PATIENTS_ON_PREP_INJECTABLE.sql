SELECT prep_type.patient_id FROM (

	SELECT p.patient_id, MAX(e.encounter_datetime) enconunter_date FROM patient p 
		INNER JOIN encounter e ON p.patient_id=e.patient_id	
		INNER JOIN obs o ON o.encounter_id=e.encounter_id 
			WHERE e.voided=0 AND o.voided=0 AND p.voided=0 
				AND e.encounter_type IN (80,81) AND o.concept_id= 165516 AND o.value_coded= 21959 
				AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location
					GROUP BY p.patient_id
)prep_type

INNER JOIN (
	SELECT p.patient_id, MAX(e.encounter_datetime) enconunter_date FROM patient p 
		INNER JOIN encounter e ON p.patient_id=e.patient_id	
		INNER JOIN obs o ON o.encounter_id=e.encounter_id 
			WHERE e.voided=0 AND o.voided=0 AND p.voided=0 
				AND e.encounter_type IN (80,81) AND o.concept_id=165213 AND o.value_coded= 165613 
				AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location
					GROUP BY p.patient_id
)prep_regimen ON prep_regimen.patient_id = prep_type.patient_id