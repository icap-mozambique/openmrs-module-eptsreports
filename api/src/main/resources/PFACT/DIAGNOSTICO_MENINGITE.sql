        select 	p.patient_id
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
							inner join obs grupo on grupo.encounter_id=e.encounter_id 
							inner join obs mdc on mdc.encounter_id=e.encounter_id 
							inner join obs obsEstado on obsEstado.encounter_id=e.encounter_id 
                     where e.voided = 0 and grupo.voided = 0 and mdc.voided = 0 and obsEstado.voided = 0 and e.encounter_type = 90 
                     and e.location_id=:location and mdc.concept_id=1294 and mdc.value_coded = 1065 and grupo.concept_id=165415  and 
                     obsEstado.concept_id=165416  and obsEstado.value_datetime between :startDate and :endDate
               	     and grupo.obs_id=mdc.obs_group_id 
        
