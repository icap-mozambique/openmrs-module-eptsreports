                 select p.patient_id from patient p 
             	join encounter e on p.patient_id=e.patient_id 
             	join obs o on o.encounter_id=e.encounter_id 
             	where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type = 90 
             	and o.concept_id in (165363) and 
             	o.value_datetime between :startDate and :endDate 
             	and e.location_id=:location
                group by p.patient_id 
