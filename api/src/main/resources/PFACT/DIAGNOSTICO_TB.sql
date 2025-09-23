select tb.patient_id from (
 select 	p.patient_id
          		from 	patient p				 
           					inner join encounter e on p.patient_id=e.patient_id 
           					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=23761 and obsScreen.value_coded = 1065 and obsScreen.voided=0  
           			where 	e.voided=0 and p.voided=0 and e.encounter_type=6 and e.location_id=:location and 
           					e.encounter_datetime between :startDate and :endDate
           			group by p.patient_id                    
		union
                select 	p.patient_id
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
							inner join obs grupo on grupo.encounter_id=e.encounter_id 
							inner join obs mdc on mdc.encounter_id=e.encounter_id 
							inner join obs obsEstado on obsEstado.encounter_id=e.encounter_id 
                     where e.voided = 0 and grupo.voided = 0 and mdc.voided = 0 and obsEstado.voided = 0 and e.encounter_type = 90 
                     and e.location_id=:location and mdc.concept_id=23761 and mdc.value_coded = 1065 and grupo.concept_id=165415  and 
                     obsEstado.concept_id=165416  and obsEstado.value_datetime between :startDate and :endDate
               	     and grupo.obs_id=mdc.obs_group_id 
         			group by p.patient_id ) tb group by patient_id
