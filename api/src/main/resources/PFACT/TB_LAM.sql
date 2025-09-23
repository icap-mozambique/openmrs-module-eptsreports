		select tblam.patient_id
                    from (	
            select 	p.patient_id
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=23722 and
                             obsScreen.value_coded = 23951 and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type in (6,9) and e.location_id= :location and
         					e.encounter_datetime between :startDate and :endDate
         			group by p.patient_id
            union        
			select 	p.patient_id
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=23951 and
                             obsScreen.value_coded in ( 703,664) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type in (6,9) and e.location_id= :location and
         					e.encounter_datetime between :startDate and :endDate
         			group by p.patient_id  ) tblam group by patient_id 
