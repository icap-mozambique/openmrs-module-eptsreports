                    select cd4.patient_id
                    from (
                       
                         select p.patient_id, max(e.encounter_datetime) data_cd4 
							    from patient p 
								inner join encounter e on p.patient_id=e.patient_id 
								inner join obs o on o.encounter_id=e.encounter_id 
								where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (6,13,51,53) 
								and o.concept_id=1695 and o.value_numeric < 200 and e.encounter_datetime
                                between :startDate and :endDate and e.location_id=:location  
								group by p.patient_id 
                                
                                union 
				select p.patient_id, max(artStartDate.value_datetime) data_cd4
					from patient p
					   inner join encounter e on e.patient_id = p.patient_id
					   inner join obs cd4ArtStart on cd4ArtStart.encounter_id = e.encounter_id
					   inner join obs artStartDate on artStartDate.encounter_id = e.encounter_id
					where p.voided is false and e.voided is false and cd4ArtStart.voided is false and artStartDate.voided 
                    is false and e.encounter_type = 53 and e.location_id=:location
					   and cd4ArtStart.concept_id = 23896 and cd4ArtStart.value_numeric < 200 and 
                       artStartDate.concept_id = 1190 and artStartDate.value_datetime between :startDate and :endDate
                       group by p.patient_id ) cd4 group by patient_id
