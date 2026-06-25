 select 	p.patient_id
          		from 	patient p				 
           					inner join encounter e on p.patient_id=e.patient_id 
           					inner join obs obs on obs.encounter_id=e.encounter_id and obs.voided=0  
           			where 	e.voided=0 and p.voided=0 and e.encounter_type in (51,6,13,90) and obs.concept_id in (165362,23952) and obs.value_coded=703 and e.location_id=:location and 
           					e.encounter_datetime between :startDate and :endDate
           			group by p.patient_id
