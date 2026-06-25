select p.patient_id
  from patient p 
		inner join encounter e on p.patient_id=e.patient_id 
		inner join obs o on o.encounter_id=e.encounter_id 
  where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (53,6,9) 
		and o.concept_id=5356 and o.value_coded in (1206,1207) and e.encounter_datetime between :startDate and :endDate  and e.location_id= :location
		group by p.patient_id