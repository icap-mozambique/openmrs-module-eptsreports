            select dah.patient_id from (        
						select 	p.patient_id
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
							inner join obs grupo on grupo.encounter_id=e.encounter_id 
							inner join obs mdc on mdc.encounter_id=e.encounter_id 
							inner join obs obsEstado on obsEstado.encounter_id=e.encounter_id 
                     where e.voided = 0 and grupo.voided = 0 and mdc.voided = 0 and obsEstado.voided = 0 and e.encounter_type = 6 and e.location_id= :location 
               	     and mdc.concept_id=165174 and mdc.value_coded = 165321 and grupo.concept_id=165323  and obsEstado.concept_id=165322  and obsEstado.value_coded in(1256,1257) 
               	   and grupo.obs_id=mdc.obs_group_id and grupo.obs_id=obsEstado.obs_group_id  and
         					e.encounter_datetime between :startDate and :endDate
         			group by p.patient_id
                    
                    union
                     select 	p.patient_id
          		from 	patient p				 
           					inner join encounter e on p.patient_id=e.patient_id 
           					inner join obs obs on obs.encounter_id=e.encounter_id and obs.voided=0  
           			where 	e.voided=0 and p.voided=0 and e.encounter_type=90 and e.location_id=:location and 
           					e.encounter_datetime between :startDate and :endDate
           			group by p.patient_id ) dah group by patient_id 
