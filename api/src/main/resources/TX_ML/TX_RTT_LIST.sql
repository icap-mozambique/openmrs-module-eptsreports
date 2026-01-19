SELECT art_start.patient_id, pi.identifier, CONCAT(pn.given_name, ' ', COALESCE(pn.middle_name, ''))name, pn.family_name, pe.gender, (YEAR( :endDate) - YEAR(pe.birthdate)) age, art_start.data_inicio, patient_sector.sector_clinic,
	   CASE 
		WHEN last_state.state = 9 THEN 'ABANDONO'
		WHEN last_state.state = 10 THEN 'OBITO' 
		WHEN last_state.state = 8 THEN 'SUSPENSO' 
		WHEN last_state.state = 7 THEN 'TRANSFERIDO PARA' END AS state, before_consultation_date, before_pickup_date, before_pickup_date_mc, after_consultation_date, after_pickup_date, after_pickup_date_mc, dispensation_type, 
		address3, patient_contact, confidant_name, confidant_contact, dispensation_type_mc FROM
(
	select patient_id,min(data_inicio) data_inicio from 
	(
		select  p.patient_id,min(e.encounter_datetime) data_inicio    
		from patient p         
				inner join person pe on pe.person_id = p.patient_id   
				inner join encounter e on p.patient_id=e.patient_id   
				inner join obs o on o.encounter_id=e.encounter_id     
		where e.voided=0 and o.voided=0 and p.voided=0 and pe.voided = 0           
				and e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256              
				and e.encounter_datetime<= :endDate and e.location_id= :location
				group by p.patient_id     
		union   
		select  p.patient_id,min(value_datetime) data_inicio          
		from patient p
				inner join person pe on pe.person_id = p.patient_id   
				inner join encounter e on p.patient_id=e.patient_id 
				inner join obs o on e.encounter_id=o.encounter_id     
		where p.voided=0 and pe.voided = 0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53)           
				and o.concept_id=1190 and o.value_datetime is not null 
				and o.value_datetime<= :endDate and e.location_id= :location
				group by p.patient_id     
		union   
		select  pg.patient_id,min(date_enrolled) data_inicio          
		from patient p         
				inner join person pe on pe.person_id = p.patient_id       
				inner join patient_program pg on p.patient_id=pg.patient_id   
		where   pg.voided=0 and p.voided=0 and pe.voided = 0 and program_id=2 
					and date_enrolled<= :endDate and location_id= :location 
				group by pg.patient_id    
		union   
		select e.patient_id, min(e.encounter_datetime) as data_inicio
		from patient p     
				inner join person pe on pe.person_id = p.patient_id   
				inner join encounter e on p.patient_id=e.patient_id   
		where p.voided=0 and pe.voided = 0 and e.encounter_type=18 and e.voided=0 
				and e.encounter_datetime<= :endDate and e.location_id= :location  
				group by  p.patient_id
		union 
		select  p.patient_id,min(value_datetime) data_inicio    
		from patient p   
				inner join person pe on pe.person_id = p.patient_id 
				inner join encounter e on p.patient_id=e.patient_id 
				inner join obs o on e.encounter_id=o.encounter_id   
		where   p.voided=0 and pe.voided = 0 and e.voided=0 and o.voided=0 and e.encounter_type=52   
				and o.concept_id=23866 and o.value_datetime is not null            
				and o.value_datetime<= :endDate and e.location_id= :location            
				group by p.patient_id   
	)inicio_real  group by patient_id
) art_start
INNER JOIN patient_identifier pi ON art_start.patient_id = pi.patient_id
INNER JOIN person_name pn ON pn.person_id = art_start.patient_id
INNER JOIN person pe ON pe.person_id = art_start.patient_id
INNER JOIN person_address pa ON pa.person_id = art_start.patient_id
LEFT JOIN (
    SELECT last_sector_clinic.patient_id,last_sector_clinic.encounter_datetime, last_sector_value.obs_datetime, last_sector_value.concept_id, last_sector_value.value_coded,
    CASE
        WHEN concept_id = 1982 AND value_coded = 1065 THEN 'CPN/G'
        WHEN concept_id = 6332 AND value_coded = 1065 THEN 'CCR/L'
        WHEN concept_id = 1268 AND value_coded IN (1256,1257) THEN 'TB'
    ELSE null END AS sector_clinic FROM (
        SELECT e.patient_id, MAX(e.encounter_datetime) encounter_datetime FROM encounter e 
            WHERE e.voided = 0 AND e.encounter_type IN (6,9) 
                AND e.encounter_datetime <= :endDate
                    GROUP BY e.patient_id
    )last_sector_clinic
    INNER JOIN (
        SELECT o.person_id patient_id, o.obs_datetime, o.concept_id, o.value_coded, o.encounter_id FROM obs o
                WHERE o.voided = 0 AND o.concept_id IN(1268, 6332, 1982) 
                    AND o.value_coded IN (1065, 1256, 1257)
                    AND o.obs_datetime <= :endDate
    )last_sector_value ON last_sector_value.patient_id = last_sector_clinic.patient_id 
        AND last_sector_clinic.encounter_datetime = last_sector_value.obs_datetime 
            GROUP BY last_sector_clinic.patient_id
)patient_sector ON patient_sector.patient_id = art_start.patient_id
LEFT JOIN (
	select distinct max_estado.patient_id, max_estado.data_estado, ps.state, ps.start_date 
    from (                                          						
            select pg.patient_id,																											
                max(ps.start_date) data_estado																							
            from	patient p																												
                inner join patient_program pg on p.patient_id = pg.patient_id																
                inner join patient_state ps on pg.patient_program_id = ps.patient_program_id												
            where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id = 2  																				
                and ps.start_date < :startDate and pg.location_id = :location group by pg.patient_id                                              
        ) 
    max_estado                                                                                                                        
        inner join patient_program pp on pp.patient_id = max_estado.patient_id															
        inner join patient_state ps on ps.patient_program_id = pp.patient_program_id and ps.start_date = max_estado.data_estado	        
    where pp.program_id = 2 and ps.state in (9,10,8,7) and pp.voided = 0 and ps.voided = 0 and pp.location_id = :location 
)last_state ON last_state.patient_id = art_start.patient_id
LEFT JOIN(
	SELECT e.patient_id, MAX(e.encounter_datetime) before_consultation_date FROM encounter e
		WHERE e.voided = 0 AND e.encounter_type IN (6,9) 
			AND e.location_id = :location AND e.encounter_datetime < :startDate
				GROUP BY e.patient_id
)consultation_before_rtt ON consultation_before_rtt.patient_id = art_start.patient_id
LEFT JOIN(
	SELECT e.patient_id, MAX(e.encounter_datetime) before_pickup_date FROM encounter e
		WHERE e.voided = 0 AND e.encounter_type = 18 
			AND e.location_id = :location AND e.encounter_datetime < :startDate
				GROUP BY e.patient_id
)pickup_before_rtt ON pickup_before_rtt.patient_id = art_start.patient_id
LEFT JOIN(
	SELECT p.patient_id, MAX(value_datetime) before_pickup_date_mc FROM patient p
		INNER JOIN encounter e ON p.patient_id=e.patient_id 
		INNER join obs o ON e.encounter_id=o.encounter_id   
			WHERE p.voided=0 AND e.voided=0 AND o.voided=0 and e.encounter_type=52   
				AND o.concept_id=23866 AND o.value_datetime IS NOT NULL          
				AND o.value_datetime < :startDate AND e.location_id= :location            
					GROUP BY p.patient_id
)pickup_before_rtt_mc ON pickup_before_rtt_mc.patient_id = art_start.patient_id
LEFT JOIN(
	SELECT e.patient_id, MAX(e.encounter_datetime) after_consultation_date FROM encounter e
		WHERE e.voided = 0 AND e.encounter_type IN (6,9) 
			AND e.location_id = :location 
			AND e.encounter_datetime BETWEEN :startDate AND :endDate
				GROUP BY e.patient_id
)consultation_after_rtt ON consultation_after_rtt.patient_id = art_start.patient_id
LEFT JOIN(
	SELECT e.patient_id, MAX(e.encounter_datetime) after_pickup_date FROM encounter e
		WHERE e.voided = 0 AND e.encounter_type = 18 
			AND e.location_id = :location 
			AND e.encounter_datetime BETWEEN :startDate AND :endDate
				GROUP BY e.patient_id
)pickup_after_rtt ON pickup_after_rtt.patient_id = art_start.patient_id
LEFT JOIN(
	SELECT p.patient_id, MAX(value_datetime) after_pickup_date_mc FROM patient p
		INNER JOIN encounter e ON p.patient_id=e.patient_id 
		INNER join obs o ON e.encounter_id=o.encounter_id   
			WHERE p.voided=0 AND e.voided=0 AND o.voided=0 and e.encounter_type=52   
				AND o.concept_id=23866 AND o.value_datetime IS NOT NULL          
				AND o.value_datetime BETWEEN :startDate AND :endDate
				AND e.location_id= :location            
					GROUP BY p.patient_id
)pickup_after_rtt_mc ON pickup_after_rtt_mc.patient_id = art_start.patient_id
LEFT JOIN (
	SELECT last_pickup.patient_id,
    CASE
        WHEN value_coded = 165175 THEN 'HORÁRIO NORMAL DE EXPEDIENTE'
        WHEN value_coded = 165176 THEN 'FORA DO HORÁRIO'
        WHEN value_coded = 165177 THEN 'FARMAC/FARMÁCIA PRIVADA'
        WHEN value_coded = 165178 THEN 'DISPENSA COMUNITÁRIA VIA PROVEDOR'
        WHEN value_coded = 165179 THEN 'DISPENSA COMUNITÁRIA VIA APE'
        WHEN value_coded = 165180 THEN 'BRIGADAS MÓVEIS DIURNAS'
        WHEN value_coded = 165181 THEN 'BRIGADAS MÓVEIS NOCTURNAS (HOTSPOTS)'
        WHEN value_coded = 165182 THEN 'CLÍNICAS MÓVEIS DIURNAS' 
        WHEN value_coded = 165183 THEN 'CLÍNICAS MÓVEIS NOCTURNAS (HOTSPOTS)'       
    ELSE null END AS dispensation_type FROM (
        SELECT e.patient_id, MAX(e.encounter_datetime) encounter_datetime FROM encounter e 
            WHERE e.voided = 0 AND e.encounter_type = 18
                AND e.encounter_datetime <= :endDate
                    GROUP BY e.patient_id
    )last_pickup
    INNER JOIN (
        SELECT o.person_id patient_id, o.obs_datetime, o.concept_id, o.value_coded, o.encounter_id FROM obs o
                WHERE o.voided = 0 AND o.concept_id = 165174
                    AND o.value_coded IN (165175, 165176, 165177, 165178, 165179, 165180, 165181, 165182, 165183)
                    AND o.obs_datetime <= :endDate
    )dispensation_value ON dispensation_value.patient_id = last_pickup.patient_id 
        AND last_pickup.encounter_datetime = dispensation_value.obs_datetime 
            GROUP BY last_pickup.patient_id
)dispensation_type ON dispensation_type.patient_id = art_start.patient_id
LEFT JOIN(
	SELECT pa.person_id patient_id, pa.value patient_contact FROM person_attribute pa 
		WHERE pa.person_attribute_type_id = 9 
			GROUP BY pa.person_id 
)patient_contact ON patient_contact.patient_id = art_start.patient_id
LEFT JOIN (
	SELECT o.person_id patient_id, o.value_text confidant_name FROM obs o
		INNER JOIN encounter e ON e.encounter_id = o.encounter_id
			WHERE  o.voided = 0 AND o.concept_id = 1740 AND e.encounter_type = 53
				GROUP BY o.person_id
)confidant_name ON confidant_name.patient_id = art_start.patient_id
LEFT JOIN(
	SELECT o.person_id patient_id, o.value_text confidant_contact FROM obs o
		INNER JOIN encounter e ON e.encounter_id = o.encounter_id
			WHERE o.voided = 0 AND o.concept_id = 6224 AND e.encounter_type = 53
				GROUP BY o.person_id
)confidant_contact ON confidant_contact.patient_id = art_start.patient_id
LEFT JOIN(
	SELECT last_encounter.patient_id, last_encounter_date ,CASE
							    WHEN mds_mode_mc.value_coded=165340 THEN 'DB - DISPENSA BIMESTRAL'
							    WHEN mds_mode_mc.value_coded=23730  THEN 'DT - DISPENSA TRIMESTRAL DE ARV'
							    WHEN mds_mode_mc.value_coded=23888  THEN 'DS - DISPENSA SEMESTRAL DE ARV'
							    WHEN mds_mode_mc.value_coded=165314 THEN 'DA - DISPENSA ANUAL DE ARV'
							    WHEN mds_mode_mc.value_coded=165315 THEN 'DD - DISPENSA DESCENTRALIZADA DE ARV'
							    WHEN mds_mode_mc.value_coded=165178 THEN 'DCP - DISPENSA COMUNITÁRIA ATRAVÉS DO PROVEDOR'
							    WHEN mds_mode_mc.value_coded=165179 THEN 'DCA - DISPENSA COMUNITÁRIA ATRAVÉS DO APE'
							    WHEN mds_mode_mc.value_coded=165264 THEN 'BM - DISPENSA COMUNITÁRIA ATRAVÉS DE BRIGADAS MÓVEIS'
							    WHEN mds_mode_mc.value_coded=165265 THEN 'CM - DISPENSA COMUNITÁRIA ATRAVÉS DE CM'
							    WHEN mds_mode_mc.value_coded=23725  THEN 'AF - ABORDAGEM FAMILIAR'
							    WHEN mds_mode_mc.value_coded=23729  THEN 'FR - FLUXO RÁPIDO'
							    WHEN mds_mode_mc.value_coded=23724  THEN 'GA - GRUPOS DE APOIO PARA ADESÃO COMUNITÁRIA'
							    WHEN mds_mode_mc.value_coded=23726  THEN 'CA - CLUBES DE ADESÃO'
							    WHEN mds_mode_mc.value_coded=165316 THEN 'EH - EXTENSÃO DE HORÁRIO'
							    WHEN mds_mode_mc.value_coded=165317 THEN 'TB - PARAGEM ÚNICA NO SECTOR DA TB'
							    WHEN mds_mode_mc.value_coded=165318 THEN 'CT - PARAGEM ÚNICA NOS SERVIÇOS TARV'
							    WHEN mds_mode_mc.value_coded=165319 THEN 'SAAJ - PARAGEM ÚNICA NO SAAJ'
							    WHEN mds_mode_mc.value_coded=165320 THEN 'SMI - PARAGEM UNICA NA SMI'
							    WHEN mds_mode_mc.value_coded=165321 THEN 'DAH - DOENÇA AVANÇADA POR HIV'
							ELSE NULL END AS dispensation_type_mc FROM (
								SELECT e.patient_id, MAX(e.encounter_datetime) last_encounter_date FROM encounter e
									WHERE e.voided = 0 AND e.encounter_type IN (6,9) AND e.location_id = :location
										GROUP BY e.patient_id
							)last_encounter
							INNER JOIN(
								SELECT mds_mode.person_id patient_id, mds_mode.obs_datetime, mds_mode.value_coded FROM obs mds_mode
									WHERE mds_mode.voided = 0 AND mds_mode.concept_id = 165174
										AND mds_mode.value_coded IN (165340, 23730, 23888, 165314, 165315, 165178, 165179, 165264, 165265, 23725, 23729, 23724, 23726, 165316, 165317, 165318, 165319, 165320, 165321)
										AND mds_mode.obs_datetime <= :endDate
											GROUP BY mds_mode.person_id
							)mds_mode_mc ON last_encounter.patient_id = mds_mode_mc.patient_id AND mds_mode_mc.obs_datetime = last_encounter.last_encounter_date
							INNER JOIN (
								SELECT mds_state.person_id patient_id, mds_state.obs_datetime FROM obs mds_state
									WHERE mds_state.concept_id = 165322 AND mds_state.value_coded IN (1256, 1257)
										AND mds_state.obs_datetime <= :endDate
											GROUP BY mds_state.person_id
							)mds_state_mc ON mds_state_mc.patient_id = last_encounter.patient_id AND mds_state_mc.obs_datetime = last_encounter.last_encounter_date
								GROUP BY last_encounter.patient_id
)dispensation_type_mc ON dispensation_type_mc.patient_id = art_start.patient_id
WHERE art_start.patient_id IN ( :patientIds) GROUP BY art_start.patient_id