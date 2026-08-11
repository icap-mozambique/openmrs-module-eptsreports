
SELECT prep_start.patient_id FROM (
	SELECT p.patient_id, MIN(value_datetime) prep_start_date FROM patient p
		INNER JOIN encounter e ON p.patient_id=e.patient_id
		INNER JOIN obs o ON e.encounter_id=o.encounter_id
			WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type = 80
				AND o.concept_id = 165296 AND o.value_coded IN (1256,1705,1257) AND o.value_datetime is NOT NULL 
				AND o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location
					GROUP BY p.patient_id
)prep_start

INNER JOIN (
	SELECT p.patient_id, MAX(e.encounter_datetime) enconunter_date FROM patient p 
		INNER JOIN encounter e ON p.patient_id=e.patient_id	
		INNER JOIN obs o ON o.encounter_id=e.encounter_id 
			WHERE e.voided=0 AND o.voided=0 AND p.voided=0 
				AND e.encounter_type IN (80,81) AND o.concept_id= 165516 AND o.value_coded= 21959 
				AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location
					GROUP BY p.patient_id
)prep_type ON prep_start.patient_id = prep_type.patient_id

INNER JOIN (
	SELECT p.patient_id, MAX(e.encounter_datetime) enconunter_date FROM patient p 
		INNER JOIN encounter e ON p.patient_id=e.patient_id	
		INNER JOIN obs o ON o.encounter_id=e.encounter_id 
			WHERE e.voided=0 AND o.voided=0 AND p.voided=0 
				AND e.encounter_type IN (80,81) AND o.concept_id=165213 AND o.value_coded= 165613 
				AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location
					GROUP BY p.patient_id
)prep_regimen ON prep_start.patient_id = prep_regimen.patient_id