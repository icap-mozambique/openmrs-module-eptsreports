  SELECT p.patient_id, pi.identifier, CONCAT(pn.given_name, ' ', COALESCE(pn.middle_name, ''))name, pn.family_name, pe.gender, (YEAR( :endDate) - YEAR(pe.birthdate)) age, 
                       	start_art.art_start_date, expected.expected_date, scheduled_follow_up.scheduled_followup_date, scheduled_pick_up.scheduled_pick_up_date, follow_up_before.followup_date, 
                         	pick_up.pick_up_date,  carga_viral.data_carga, ultima_cv.Valor_carga,
                         	gaac_model.mds_gaac, af_model.mds_af, ca_model.mds_ca, pu_model.mds_pu, fr_model.mds_fr, dt_model.mds_dt, dc_model.mds_dc, ds_model.mds_ds, patient_contact.patient_contact, 
                         	confident.confident_contact, patient_sector.sector_clinic, start_tpi.start_tpi_date, end_tpi.end_tpi_date, pickup_tpi.pickup_tpi_date,
                             case kp.value_coded
								  when 20454  then 'PID'
                                  when 1377   then 'HSM'
                                  when 1901   then 'MTS'
								  when 20426  then 'REC'
                                  when 5622   then 'Outros'
							END AS KP,
                            case mds_tipo.tipo_dispensa
								  when 3  then 'APE'
                                  when 7  then 'BM'
                                  when 4  then 'BM'
								  when 5  then 'CM'
                                  when 8  then 'CM'
							END AS mds_type
                            
                            FROM patient p
                         	INNER JOIN patient_identifier pi ON p.patient_id = pi.patient_id
                         	INNER JOIN person_name pn ON pn.person_id = p.patient_id
                         	INNER JOIN person pe ON pe.person_id = p.patient_id
                         	INNER JOIN (
                                 SELECT patient_id, MIN(art_start_date) art_start_date FROM
                                     (	
                         				SELECT p.patient_id,MIN(e.encounter_datetime) art_start_date FROM patient p 
                         					INNER JOIN encounter e ON p.patient_id=e.patient_id	
                         					INNER JOIN obs o on o.encounter_id=e.encounter_id 
                         						WHERE e.voided=0 AND o.voided=0 AND p.voided=0 
                         						AND e.encounter_type IN (18,6,9) AND o.concept_id=1255 AND o.value_coded=1256 
                         						AND e.encounter_datetime<= :endDate AND e.location_id = :location
                         							GROUP BY p.patient_id
                         				UNION
                         		
                         				SELECT p.patient_id,MIN(value_datetime) art_start_date FROM patient p
                         					INNER JOIN encounter e ON p.patient_id=e.patient_id
                         					INNER JOIN obs o ON e.encounter_id=o.encounter_id
                         						WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type in (18,6,9,53) 
                         						AND o.concept_id=1190 AND o.value_datetime is NOT NULL 
                         						AND o.value_datetime<= :endDate AND e.location_id = :location
                         							GROUP BY p.patient_id
                         				
                         				UNION
                         
                         				SELECT pg.patient_id,MIN(date_enrolled) art_start_date FROM patient p 
                         					INNER JOIN patient_program pg ON p.patient_id=pg.patient_id
                         						WHERE pg.voided=0 AND p.voided=0 AND program_id=2 AND date_enrolled<= :endDate AND pg.location_id = :location
                         							GROUP BY pg.patient_id
                         				
                         				UNION
                         				
                         				SELECT e.patient_id, MIN(e.encounter_datetime) AS art_start_date FROM patient p
                         					INNER JOIN encounter e ON p.patient_id=e.patient_id
                         				  		WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 AND e.encounter_datetime<= :endDate AND e.location_id = :location
                         				  			GROUP BY p.patient_id
                         			  
                         				UNION
                         				
                         				SELECT p.patient_id,MIN(value_datetime) art_start_date FROM patient p
                         					INNER JOIN encounter e ON p.patient_id=e.patient_id
                         					INNER JOIN obs o ON e.encounter_id=o.encounter_id
                         						WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=52 
                         						AND o.concept_id=23866 and o.value_datetime is NOT NULL 
                         						AND o.value_datetime<= :endDate AND e.location_id = :location
                         							GROUP BY p.patient_id	
                         	
                         		)min_art_start_date GROUP BY min_art_start_date.patient_id
                         	
                         	)start_art ON start_art.patient_id = p.patient_id
                         	INNER JOIN (
                                 SELECT max_expected.patient_id, MAX(expected_date) expected_date FROM (
                         
                                     SELECT o.person_id patient_id, MAX(o.value_datetime) expected_date FROM obs o
                                         INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                             WHERE o.concept_id = 1410 AND e.encounter_type IN(6,9) 
                                             AND o.voided = 0 AND e.voided = 0 AND e.location_id = :location
                                                 GROUP BY o.person_id
                                     UNION
                         
                                     SELECT o.person_id patient_id, MAX(o.value_datetime) expected_date FROM obs o
                                         INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                             WHERE o.concept_id = 5096 AND e.encounter_type = 18 
                                             AND o.voided = 0 AND e.voided = 0 AND e.location_id = :location
                                                 GROUP BY o.person_id
                                     UNION
                         
                                     SELECT o.person_id patient_id, MAX((o.value_datetime + INTERVAL 28 DAY)) expected_date FROM obs o
                                         INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                             WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                             AND o.voided = 0 AND e.voided = 0 AND e.location_id = :location
                                                 GROUP BY o.person_id
                         
                                     )max_expected GROUP BY max_expected.patient_id
                         				HAVING expected_date BETWEEN :startDate AND :endDate 
                         	)expected ON expected.patient_id = p.patient_id
                         
                         	LEFT JOIN (
                         		SELECT o.person_id patient_id, o.value_datetime scheduled_followup_date, e.encounter_datetime FROM obs o
                         		 INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                         		     WHERE o.concept_id = 1410 AND e.encounter_type IN(6,9) 
                         		     AND o.voided = 0 AND e.voided = 0 AND e.location_id = :location
                         		     AND o.value_datetime BETWEEN :startDate AND :endDate
                         		         GROUP BY o.person_id
                         	)scheduled_follow_up ON scheduled_follow_up.patient_id = p.patient_id AND scheduled_follow_up.scheduled_followup_date = expected.expected_date
                         
                         	LEFT JOIN (
                         	
                         		 SELECT o.person_id patient_id, o.value_datetime scheduled_pick_up_date, e.encounter_datetime FROM obs o
                                         INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                             WHERE o.concept_id = 5096 AND e.encounter_type = 18 
                                             AND o.voided = 0 AND e.voided = 0 AND e.location_id = :location
                                             AND o.value_datetime BETWEEN :startDate AND :endDate
                                                 GROUP BY o.person_id
                                     UNION
                         
                                     SELECT o.person_id patient_id, (o.value_datetime  + INTERVAL 28 DAY) scheduled_pick_up_date, e.encounter_datetime FROM obs o
                                         INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                             WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                             AND o.voided = 0 AND e.voided = 0 AND e.location_id = :location
                                             AND (o.value_datetime + INTERVAL 28 DAY) BETWEEN :startDate AND :endDate
                                                 GROUP BY o.person_id
                         	)scheduled_pick_up ON scheduled_pick_up.patient_id = p.patient_id AND scheduled_pick_up.scheduled_pick_up_date = expected.expected_date
                         
                         	LEFT JOIN(
                         		SELECT e.patient_id, MAX(e.encounter_datetime) followup_date FROM encounter e
                         		     WHERE e.encounter_type IN(6,9) AND e.voided = 0 AND e.location_id = :location
                         		         GROUP BY e.patient_id
                         	)follow_up_before ON follow_up_before.patient_id = p.patient_id AND follow_up_before.followup_date > scheduled_follow_up.encounter_datetime AND follow_up_before.followup_date <= scheduled_follow_up.scheduled_followup_date
                         
                         	LEFT JOIN (
                         		SELECT max_pick_up.patient_id, MAX(pick_up_date) pick_up_date  FROM (
                         			SELECT e.patient_id, MAX(e.encounter_datetime) pick_up_date FROM encounter e
                         			     WHERE e.encounter_type = 18 AND e.voided = 0 AND e.location_id = :location
                         			         GROUP BY e.patient_id
                         			 UNION
                         	
                         			  SELECT o.person_id patient_id, MAX(o.value_datetime) pick_up_date FROM obs o
                         	                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                         	                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                         	                    AND o.voided = 0 AND e.voided = 0 AND e.location_id = :location
                         	                        GROUP BY o.person_id
                         		)max_pick_up GROUP BY max_pick_up.patient_id
                         		 
                         	)pick_up ON pick_up.patient_id = p.patient_id AND pick_up.pick_up_date > scheduled_pick_up.encounter_datetime AND pick_up.pick_up_date <= scheduled_pick_up.scheduled_pick_up_date
                         
                            left join
                            
            										(
            						Select p.patient_id,max(o.obs_datetime) data_carga
            						from patient p
            									 inner join encounter e on p.patient_id=e.patient_id
            									 inner join obs o on e.encounter_id=o.encounter_id
            								where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53,13,51) and  
            						o.concept_id in (856,1305) and  o.obs_datetime between date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) and :endDate and
            						e.location_id=:location
            						group by p.patient_id
            
            						) carga_viral on carga_viral.patient_id=p.patient_id
            						left join
            						(
            						select 	
            								carga_viral.patient_id, 
            								carga_viral.Valor_carga,
            								carga_viral.data_carga
            						from 	
            						(
            							 select ultima_carga.patient_id,ultima_carga.data_carga,
            							 if(obs.value_numeric is null or obs.value_numeric<0,'Indetectavel',obs.value_numeric)  Valor_carga, 
            									ultima_carga.encounter_type,
            									obs.creator,
            									obs.date_created data_registo
            							 from 		
            								( 
            									Select p.patient_id,max(o.obs_datetime) data_carga, e.encounter_type
            									from 	patient p 
            											inner join encounter e on p.patient_id=e.patient_id 
            											inner join obs o on e.encounter_id=o.encounter_id 
            									where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (13,51,6,9) and  o.concept_id in (856,1305) and  
            											o.obs_datetime between date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) and :endDate and e.location_id=:location
            									group by p.patient_id
            								) ultima_carga 
            									inner join obs on obs.person_id=ultima_carga.patient_id and obs.obs_datetime=ultima_carga.data_carga 
            								where obs.voided=0 and ((obs.concept_id=856) or obs.concept_id=1305)  and obs.location_id=:location
            						) carga_viral
            						inner join encounter_type et on et.encounter_type_id=carga_viral.encounter_type
            						group by carga_viral.patient_id) ultima_cv on ultima_cv.patient_id=p.patient_id and ultima_cv.data_carga =carga_viral.data_carga
                         
                         	LEFT JOIN (		
                         		SELECT o.person_id patient_id, IF(MAX(o.obs_datetime) IS NOT NULL, 'GA', NULL ) mds_gaac FROM obs o
                         			INNER JOIN encounter e ON o.encounter_id = e.encounter_id
                         				WHERE o.voided = 0 AND e.voided = 0 AND concept_id = 23724 
                         				AND value_coded IN (1256, 1257) AND o.obs_datetime <= :endDate AND e.location_id = :location
                         					GROUP BY o.person_id
                         	)gaac_model ON gaac_model.patient_id = p.patient_id
                         
                         	LEFT JOIN (		
                         		SELECT o.person_id patient_id, IF(MAX(o.obs_datetime) IS NOT NULL, 'AF', NULL ) mds_af FROM obs o
                         			INNER JOIN encounter e ON o.encounter_id = e.encounter_id
                         				WHERE o.voided = 0 AND e.voided = 0 AND concept_id = 23725
                         				AND value_coded IN (1256, 1257) AND o.obs_datetime <= :endDate AND e.location_id = :location
                         					GROUP BY o.person_id
                         	)af_model ON af_model.patient_id = p.patient_id
                         
                         	LEFT JOIN (		
                         		SELECT o.person_id patient_id, IF(MAX(o.obs_datetime) IS NOT NULL, 'CA', NULL ) mds_ca FROM obs o
                         			INNER JOIN encounter e ON o.encounter_id = e.encounter_id
                         				WHERE o.voided = 0 AND e.voided = 0 AND concept_id = 23726
                         				AND value_coded IN (1256, 1257) AND o.obs_datetime <= :endDate AND e.location_id = :location
                         					GROUP BY o.person_id
                         	)ca_model ON ca_model.patient_id = p.patient_id
                         
                         	LEFT JOIN (		
                         		SELECT o.person_id patient_id, IF(MAX(o.obs_datetime) IS NOT NULL, 'PU', NULL ) mds_pu FROM obs o
                         			INNER JOIN encounter e ON o.encounter_id = e.encounter_id
                         				WHERE o.voided = 0 AND e.voided = 0 AND concept_id = 23727
                         				AND value_coded IN (1256, 1257) AND o.obs_datetime <= :endDate AND e.location_id = :location
                         					GROUP BY o.person_id
                         	)pu_model ON pu_model.patient_id = p.patient_id
                         
                         	LEFT JOIN (		
                         		SELECT o.person_id patient_id, IF(MAX(o.obs_datetime) IS NOT NULL, 'FR', NULL ) mds_fr FROM obs o
                         			INNER JOIN encounter e ON o.encounter_id = e.encounter_id
                         				WHERE o.voided = 0 AND e.voided = 0 AND concept_id = 23729
                         				AND value_coded IN (1256, 1257) AND o.obs_datetime <= :endDate AND e.location_id = :location
                         					GROUP BY o.person_id
                         	)fr_model ON fr_model.patient_id = p.patient_id
                         
                         	LEFT JOIN (		
                         		SELECT o.person_id patient_id, IF(MAX(o.obs_datetime) IS NOT NULL, 'DT', NULL ) mds_dt FROM obs o
                         			INNER JOIN encounter e ON o.encounter_id = e.encounter_id
                         				WHERE o.voided = 0 AND e.voided = 0 AND concept_id = 23730
                         				AND value_coded IN (1256, 1257) AND o.obs_datetime <= :endDate AND e.location_id = :location
                         					GROUP BY o.person_id
                         	)dt_model ON dt_model.patient_id = p.patient_id
                         
                         	LEFT JOIN (		
                         		SELECT o.person_id patient_id, IF(MAX(o.obs_datetime) IS NOT NULL, 'DC', NULL ) mds_dc FROM obs o
                         			INNER JOIN encounter e ON o.encounter_id = e.encounter_id
                         				WHERE o.voided = 0 AND e.voided = 0 AND concept_id = 23731
                         				AND value_coded IN (1256, 1257) AND o.obs_datetime <= :endDate AND e.location_id = :location
                         					GROUP BY o.person_id
                         	)dc_model ON dc_model.patient_id = p.patient_id
                         
                         	LEFT JOIN (		
                         		SELECT o.person_id patient_id, IF(MAX(o.obs_datetime) IS NOT NULL, 'DS', NULL ) mds_ds FROM obs o
                         			INNER JOIN encounter e ON o.encounter_id = e.encounter_id
                         				WHERE o.voided = 0 AND e.voided = 0 AND concept_id = 23888
                         				AND value_coded IN (1256, 1257) AND o.obs_datetime <= :endDate AND e.location_id = :location
                         					GROUP BY o.person_id
                         	)ds_model ON ds_model.patient_id = p.patient_id
                         
                         	LEFT JOIN (
                         		SELECT pa.person_id patient_id, pa.value patient_contact FROM person_attribute pa
                         	         WHERE pa.person_attribute_type_id = 9
                         	         GROUP BY pa.person_id
                         	)patient_contact ON patient_contact.patient_id = p.patient_id
                         
                         	LEFT JOIN (
                         		SELECT max_confident.patient_id, max_confident.max_contat, confident_contact.confident_contact FROM (
                         			SELECT o.person_id patient_id, MAX(o.obs_datetime) max_contat FROM obs o
                         			INNER JOIN encounter e ON o.encounter_id = e.encounter_id
                         				WHERE o.voided = 0 AND e.voided = 0 AND o.concept_id IN(1611,6224) AND e.encounter_type IN (21,34,53)
                         				AND o.obs_datetime <= :endDate AND e.location_id = :location
                         					GROUP BY o.person_id
                         		)max_confident 
                         		
                         		INNER JOIN (
                         			SELECT o.person_id patient_id, o.obs_datetime, o.value_text confident_contact FROM obs o
                         				INNER JOIN encounter e ON o.encounter_id = e.encounter_id
                         					WHERE o.voided = 0 AND e.voided = 0 AND o.concept_id IN(1611,6224) AND e.encounter_type IN (21,34,53)
                         					AND o.obs_datetime <= :endDate AND e.location_id = :location
                         						GROUP BY o.person_id
                         		)confident_contact ON confident_contact.patient_id = max_confident.patient_id AND confident_contact.obs_datetime = max_confident.max_contat
                         		GROUP BY max_confident.patient_id
                         	
                         	)confident ON confident.patient_id = p.patient_id
                         
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
                         	)patient_sector ON patient_sector.patient_id = p.patient_id
                         	LEFT JOIN (
                         		SELECT min_start_tpi_date.patient_id, MIN(start_tpi_date) start_tpi_date FROM (
                         			SELECT o.person_id patient_id, MIN(o.obs_datetime) start_tpi_date FROM obs o
                         				INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                         					WHERE o.voided = 0 AND e.voided = 0 AND o.concept_id = 6122 AND o.value_coded = 1256
                         					AND e.encounter_type IN (6,9) AND o.obs_datetime <= :endDate
                         						GROUP BY o.person_id
                         
                         			UNION
                         				
                         			SELECT o.person_id patient_id, MIN(o.value_datetime) start_tpi_date FROM obs o
                         				INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                         					WHERE o.voided = 0 AND e.voided = 0 AND o.concept_id = 6128
                         					AND e.encounter_type IN (6,9,53) AND o.value_datetime <= :endDate
                         						GROUP BY o.person_id
                         				
                         		)min_start_tpi_date GROUP BY min_start_tpi_date.patient_id
                         
                         	)start_tpi ON start_tpi.patient_id = p.patient_id
                         	LEFT JOIN (
                         
                         		SELECT max_end_tpi_date.patient_id, MAX(end_tpi_date) end_tpi_date FROM (
                         			SELECT o.person_id patient_id, MAX(o.obs_datetime) end_tpi_date FROM obs o
                         				INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                         					WHERE o.voided = 0 AND e.voided = 0 AND o.concept_id = 6122 AND o.value_coded = 1267
                         					AND e.encounter_type IN (6,9) AND o.obs_datetime <= :endDate
                         						GROUP BY o.person_id
                         
                         			UNION
                         				
                         			SELECT o.person_id patient_id, MAX(o.value_datetime) end_tpi_date FROM obs o
                         				INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                         					WHERE o.voided = 0 AND e.voided = 0 AND o.concept_id = 6129
                         					AND e.encounter_type IN (6,9,53) AND o.value_datetime <= :endDate
                         						GROUP BY o.person_id
                         				
                         		)max_end_tpi_date GROUP BY max_end_tpi_date.patient_id
                         	)end_tpi ON end_tpi.patient_id = p.patient_id
                         	LEFT JOIN (
                         		
                         		SELECT max_pickup_tpi_date.patient_id, MAX(pickup_tpi_date) pickup_tpi_date FROM (
                         			SELECT o.person_id patient_id, MAX(o.obs_datetime) pickup_tpi_date FROM obs o
                         				INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                         					WHERE o.voided = 0 AND e.voided = 0 AND o.concept_id = 6122 AND o.value_coded = 1257
                         					AND e.encounter_type = 6 AND o.obs_datetime <= :endDate
                         						GROUP BY o.person_id
                         
                         			UNION
                         				
                         			SELECT e.patient_id, MAX(e.encounter_datetime) pickup_tpi_date FROM encounter e
                         					WHERE e.voided = 0 AND e.encounter_type = 60 AND e.encounter_datetime <= :endDate
                         						GROUP BY e.patient_id
                         				
                         		)max_pickup_tpi_date GROUP BY max_pickup_tpi_date.patient_id
                         	)pickup_tpi ON pickup_tpi.patient_id = p.patient_id
                            LEFT JOIN (
                            select   finalkptable.patient_id, finalkptable.value_coded                                                             
        from (                                                                                                                                                      
                select * from   (                                                                                                                                       
                    select * from   (                                                                                                                                   
                        select maxkp.patient_id, o.value_coded,o.obs_datetime,1 ordemSource,if(o.value_coded=20454,2,4) ordemKp from    (                           
                            select p.patient_id,max(e.encounter_datetime) maxkpdate                                                                                 
                            from patient p                                                                                                                          
                                inner join encounter e on p.patient_id=e.patient_id                                                                                 
                                inner join obs o on e.encounter_id=o.encounter_id                                                                                       
                            where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=23703 and  e.encounter_type=6 and e.encounter_datetime<=:endDate and          
                            e.location_id=:location                                                                                                                     
                                group by p.patient_id                                                                                                                   
                            )                                                                                                                                           
                        maxkp                                                                                                                                           
                            inner join encounter e on e.patient_id=maxkp.patient_id and maxkp.maxkpdate=e.encounter_datetime                                            
                            inner join obs o on o.encounter_id=e.encounter_id and maxkp.maxkpdate=o.obs_datetime                                                        
                        where o.concept_id=23703 and o.voided=0 and e.encounter_type=6 and e.voided=0 and e.location_id=:location and o.value_coded in (20454,20426,5622)    
                        union                                                                                                                                           
                        select maxkp.patient_id, o.value_coded,o.obs_datetime,1 ordemSource,3 ordemKp from (                                                            
                            select p.patient_id,max(e.encounter_datetime) maxkpdate from patient p                                                                      
                                inner join encounter e on p.patient_id=e.patient_id                                                                                     
                                inner join obs o on e.encounter_id=o.encounter_id                                                                                       
                            where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=23703 and  e.encounter_type=6 and e.encounter_datetime<=:endDate and  o.value_coded in(1901,1377)  and     
                                  e.location_id=:location                                                                                                           
                                group by p.patient_id                                                                                                               
                            )                                                                                                                                       
                        maxkp                                                                                                                                       
                            inner join encounter e on e.patient_id=maxkp.patient_id and maxkp.maxkpdate=e.encounter_datetime                                            
                            inner join obs o on o.encounter_id=e.encounter_id and maxkp.maxkpdate=o.obs_datetime                                                        
                            inner join person pe on pe.person_id=maxkp.patient_id                                                                                       
                        where o.concept_id=23703 and o.voided=0 and e.encounter_type=6 and e.voided=0 and e.location_id=:location and pe.voided=0                   
                            and ((pe.gender='F' and o.value_coded=1901) or  (pe.gender='M' and o.value_coded=1377))                                                     
                        union                                                                                                                                       
                                                                                                                                                       
                        select maxkp.patient_id, o.value_coded,o.obs_datetime,2 ordemSource,5 ordemKp from (                                                        
                            select p.patient_id,max(e.encounter_datetime) maxkpdate from patient p                                                                  
                                inner join encounter e on p.patient_id=e.patient_id                                                                                 
                                inner join obs o on e.encounter_id=o.encounter_id                                                                                   
                            where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=23703 and  e.encounter_type=35 and e.encounter_datetime<=:endDate and     
                                e.location_id=:location                                                                                                             
                                group by p.patient_id                                                                                                               
                            )                                                                                                                                       
                        maxkp                                                                                                                                       
                            inner join encounter e on e.patient_id=maxkp.patient_id and maxkp.maxkpdate=e.encounter_datetime                                            
                            inner join obs o on o.encounter_id=e.encounter_id and maxkp.maxkpdate=o.obs_datetime                                                        
                            inner join person pe on pe.person_id=maxkp.patient_id                                                                                       
                        where o.concept_id=23703 and o.voided=0 and e.encounter_type=35 and e.voided=0 and e.location_id=:location and pe.voided=0 and o.value_coded=23885 
                        union                                                                                                                                       
                        select maxkp.patient_id, o.value_coded,o.obs_datetime,2 ordemSource,if(o.value_coded=20454,2,4) ordemKp from  (                             
                            select p.patient_id,max(e.encounter_datetime) maxkpdate from patient p                                                                  
                                inner join encounter e on p.patient_id=e.patient_id                                                                                 
                                inner join obs o on e.encounter_id=o.encounter_id                                                                                   
                            where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=23703 and  e.encounter_type in (46,35) and e.encounter_datetime<=:endDate 
                                        and e.location_id=:location                                                                                                     
                                group by p.patient_id                                                                                                               
                            )                                                                                                                                       
                        maxkp                                                                                                                                       
                            inner join encounter e on e.patient_id=maxkp.patient_id and maxkp.maxkpdate=e.encounter_datetime                                        
                            inner join obs o on o.encounter_id=e.encounter_id and maxkp.maxkpdate=o.obs_datetime                                                    
                        where o.concept_id=23703 and o.voided=0 and e.encounter_type in (46,35) and e.voided=0 and e.location_id=:location                      
                                and o.value_coded in (20454,20426)                                                                                                  
                        union                                                                                                                                       
                        select maxkp.patient_id, o.value_coded,o.obs_datetime,2 ordemSource,3 ordemKp from (                                                        
                            select p.patient_id,max(e.encounter_datetime) maxkpdate from patient p                                                                  
                                inner join encounter e on p.patient_id=e.patient_id                                                                                 
                                inner join obs o on e.encounter_id=o.encounter_id                                                                                   
                            where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=23703 and  e.encounter_type in (46,35) and  o.value_coded in(1377,1901)                            
                                    and e.encounter_datetime<=:endDate and e.location_id=:location                                                                      
                                group by p.patient_id                                                                                                               
                            )                                                                                                                                       
                        maxkp                                                                                                                                       
                            inner join encounter e on e.patient_id=maxkp.patient_id and maxkp.maxkpdate=e.encounter_datetime                                            
                            inner join obs o on o.encounter_id=e.encounter_id and maxkp.maxkpdate=o.obs_datetime                                                    
                            inner join person pe on pe.person_id=maxkp.patient_id                                                                                   
                        where o.concept_id=23703 and o.voided=0 and e.encounter_type in (46,35) and e.voided=0 and e.location_id=:location and pe.voided=0          
                            and ((pe.gender='F' and o.value_coded=1901) or  (pe.gender='M' and o.value_coded=1377))                                                 
                        union                                                                                                                                       
                        Select pa.person_id,                                                                                                                        
                            case upper(pa.value)                                                                                                                    
                                when 'HSM' then 1377                                                                                                                
                                when 'HSH' then 1377                                                                                                                
                                when 'MSM' then 1377                                                                                                                
                                when 'MTS' then 1901                                                                                                                
                                when 'CSW' then 1901                                                                                                                
                                when 'TS' then 1901                                                                                                                 
                                when 'PRISONER' then 20426                                                                                                          
                                when 'REC' then 20426                                                                                                               
                                when 'RC' then 20426                                                                                                                
                                when 'PID' then 20454                                                                                                               
                            else null end as estado,                                                                                                                
                            date(pa.date_created),                                                                                                                  
                            3 as ordemSource,                                                                                                                       
                            5 as ordemKp from person_attribute pa                                                                                                   
                            inner join person_attribute_type pat on pa.person_attribute_type_id=pat.person_attribute_type_id                                        
                        where pat.uuid='c89c90eb-5b03-4899-ab9f-06fecd123511' and pa.value is not null and pa.value<>''                                             
                                and pa.voided=0 and date(pa.date_created)<=:endDate                                                                                     
                    )                                                                                                                                               
                allkpsource                                                                                                                                         
    order by patient_id, obs_datetime desc, ordemSource,ordemKp                                                                                     
           )                                                                                                                                                    
        allkpsorcetakefirst group by patient_id                                                                                                                 
        ) finalkptable ) kp  ON kp.patient_id = p.patient_id
			LEFT JOIN (
            
                      select dispensa.patient_id,dispensa.tipo_dispensa  
               from 
               ( 
                   select * 
                   from 
                   ( 
                       select * 
                       from 
                       ( 
                
                       select fila.patient_id,fila.data_consulta,fila.tipo_dispensa,fila.fonte,fila.ordem_mdc  from ( 
                           select ultimo_fila.patient_id, 
                                 max(data_proximo_levantamento.value_datetime) data_consulta, 
                                 case modo_dispensa.value_coded 
                                   when 165177 then 1 
                                   when 165179 then 2 
                                   when 165178 then 3 
                                   when 165181 then 4 
                                   when 165182 then 5 
                                   when 165176 then 6 
                                   when 165180 then 7 
                                   when 165183 then 8 
                                 else 10 end as tipo_dispensa, 
                                 1  as fonte, 
                           1  as ordem_mdc 
                           from 
                           ( select patient_id, data_ultimo_levantamento,encounter_id from ( 
                               select patient_id,data_ultimo_levantamento,filas.encounter_id from ( 
                               select p.patient_id,encounter_datetime data_ultimo_levantamento, e.encounter_id 
                               from patient p 
                               inner join encounter e on e.patient_id=p.patient_id 
                               where p.voided=0 and e.voided=0 and e.encounter_type=18 and e.location_id=:location  
                               and e.encounter_datetime <=:endDate
                               )filas 
                               inner join obs data_proximo_levantamento on data_proximo_levantamento.encounter_id = filas.encounter_id 
                               where data_proximo_levantamento.concept_id =5096 
                               order by filas.patient_id, filas.data_ultimo_levantamento desc, data_proximo_levantamento.value_datetime desc 
                               ) maxFila group by maxFila.patient_id 
                           ) ultimo_fila 
                             inner join encounter e on e.encounter_id = ultimo_fila.encounter_id 
                               inner join obs data_proximo_levantamento on data_proximo_levantamento.encounter_id = e.encounter_id 
                               inner join obs modo_dispensa on modo_dispensa.encounter_id = e.encounter_id 
                           where e.voided = 0 and data_proximo_levantamento.voided = 0 and modo_dispensa.voided = 0 and e.encounter_type=18 
                             and date(e.encounter_datetime) = date(ultimo_fila.data_ultimo_levantamento) and modo_dispensa.concept_id =165174 
                               and data_proximo_levantamento.concept_id =5096 and e.location_id = :location  group by ultimo_fila.patient_id 
                               ) fila left join ( 
                                select ultimo_mdc.patient_id, 
                                  ultimo_mdc.data_consulta 
                                 from 
                                ( 
                               select p.patient_id, max(e.encounter_datetime) data_consulta 
                               from patient p 
                                 inner join encounter e on p.patient_id=e.patient_id 
                                 inner join obs o on o.encounter_id=e.encounter_id 
                               where p.voided = 0 and e.voided=0 and o.voided=0 
                                 and e.encounter_type = 6 and e.encounter_datetime<=:endDate and e.location_id=:location  
                                 group by p.patient_id 
                           ) ultimo_mdc 
                             inner join encounter e on ultimo_mdc.patient_id=e.patient_id 
                         inner join obs grupo on grupo.encounter_id=e.encounter_id 
                            inner join obs o on o.encounter_id=e.encounter_id 
                             inner join obs obsEstado on obsEstado.encounter_id=e.encounter_id 
                           where e.encounter_type=6 and e.location_id=:location  
                             and o.concept_id=165174 and o.voided=0 
                               and grupo.concept_id=165323  and grupo.voided=0 
                               and obsEstado.concept_id=165322  and obsEstado.value_coded in (1267) 
                               and obsEstado.voided=0  and grupo.voided=0 
                               and grupo.obs_id=o.obs_group_id and grupo.obs_id=obsEstado.obs_group_id 
                               and e.encounter_datetime=ultimo_mdc.data_consulta  and o.value_coded 
                               ) mdc on mdc.patient_id = fila.patient_id 
                               where (fila.data_consulta>mdc.data_consulta or mdc.data_consulta is null) 
                            union 
                               select tipoDispensa.patient_id,data_consulta,tipo_dispensa,fonte,ordem_mdc 
               				from ( 
               			    select ultimo_mdc.patient_id, 
                                  ultimo_mdc.data_consulta, 
                                  case o.value_coded 
                                   when 165315 then 1 
                                   when 165179 then 2 
                                   when 165178 then 3 
                                   when 165264 then 4 
                                   when 165265 then 5 
                                   when 165316 then 6 
                                   else 10 end as tipo_dispensa, 
                                   2  as fonte, 
                                   case o.value_coded 
                                   when 165315 then 1 
                                   when 165179 then 1 
                                   when 165178 then 1 
                                   when 165264 then 1 
                                   when 165265 then 1 
                                   when 165316 then 1 
                                  else 2 end as ordem_mdc 
                                 from 
                                ( 
                               select p.patient_id, max(e.encounter_datetime) data_consulta 
                               from patient p 
                                 inner join encounter e on p.patient_id=e.patient_id 
                                 inner join obs o on o.encounter_id=e.encounter_id 
                               where p.voided = 0 and e.voided=0 and o.voided=0 
                                 and e.encounter_type = 6 and e.encounter_datetime<=:endDate and e.location_id=:location  
                                 group by p.patient_id 
                           ) ultimo_mdc 
                             inner join encounter e on ultimo_mdc.patient_id=e.patient_id 
                         inner join obs grupo on grupo.encounter_id=e.encounter_id 
                            inner join obs o on o.encounter_id=e.encounter_id 
                             inner join obs obsEstado on obsEstado.encounter_id=e.encounter_id 
                           where e.encounter_type=6 and e.location_id=:location  
                             and o.concept_id=165174 and o.voided=0 
                               and grupo.concept_id=165323  and grupo.voided=0 
                               and obsEstado.concept_id=165322  and obsEstado.value_coded in(1256,1257) 
                               and obsEstado.voided=0  and grupo.voided=0 
                               and grupo.obs_id=o.obs_group_id and grupo.obs_id=obsEstado.obs_group_id 
                               and e.encounter_datetime=ultimo_mdc.data_consulta  and o.value_coded 
                               ) tipoDispensa left join 
                               ( 
                               select max_fila.patient_id, data_proximo_levantamento from ( 
                              select max_fila.patient_id,  max(data_proximo_levantamento.value_datetime) data_proximo_levantamento  from ( 
                              select p.patient_id, max(encounter_datetime) 
                               data_ultimo_levantamento 
                               from patient p 
                                 inner join encounter e on e.patient_id=p.patient_id 
                               where p.voided=0 and e.voided=0 and e.encounter_type=18 and e.location_id=:location  
                                 and e.encounter_datetime <=:endDate
                                 group by p.patient_id 
                                 ) max_fila 
                                 inner join encounter e on e.patient_id = max_fila.patient_id 
                               inner join obs data_proximo_levantamento on data_proximo_levantamento.encounter_id = e.encounter_id 
                              where e.voided = 0 and data_proximo_levantamento.voided = 0 and e.encounter_type=18 
                             and date(e.encounter_datetime) = date(max_fila.data_ultimo_levantamento) 
                               and data_proximo_levantamento.concept_id =5096 and e.location_id = :location  group by max_fila.patient_id 
                               ) max_fila 
                                 left join ( 
                                 select ultimo_fila.patient_id, 
                                 max(data_proximo_levantamento.value_datetime) data_consulta 
                           from 
                           ( 
                              select patient_id, data_ultimo_levantamento,encounter_id from ( 
                               select patient_id,data_ultimo_levantamento,filas.encounter_id from ( 
                               select p.patient_id,encounter_datetime data_ultimo_levantamento, e.encounter_id 
                               from patient p 
                               inner join encounter e on e.patient_id=p.patient_id 
                               where p.voided=0 and e.voided=0 and e.encounter_type=18 and e.location_id=:location  
                               and e.encounter_datetime <=:endDate
                               )filas 
                               inner join obs data_proximo_levantamento on data_proximo_levantamento.encounter_id = filas.encounter_id 
                               where data_proximo_levantamento.concept_id =5096 
                               order by filas.patient_id, filas.data_ultimo_levantamento desc, data_proximo_levantamento.value_datetime desc 
                               ) maxFila group by maxFila.patient_id 
                           ) ultimo_fila 
                             inner join encounter e on e.encounter_id = ultimo_fila.encounter_id 
                               inner join obs data_proximo_levantamento on data_proximo_levantamento.encounter_id = e.encounter_id 
                               inner join obs modo_dispensa on modo_dispensa.encounter_id = e.encounter_id 
                           where e.voided = 0 and data_proximo_levantamento.voided = 0 and e.encounter_type=18 
                             and date(e.encounter_datetime) = date(ultimo_fila.data_ultimo_levantamento) 
                             and modo_dispensa.concept_id = 165174 and modo_dispensa.voided = 0 
                               and data_proximo_levantamento.concept_id =5096 and e.location_id = :location  group by ultimo_fila.patient_id 
                               )filaWithMDS on filaWithMDS.patient_id = max_fila.patient_id 
                               where filaWithMDS.patient_id is null 
                               )filaWithoutMDS on filaWithoutMDS.patient_id = tipoDispensa.patient_id 
                               where (tipoDispensa.data_consulta > filaWithoutMDS.data_proximo_levantamento OR filaWithoutMDS.data_proximo_levantamento is null) 
                       ) todas_fontes 
                       order by patient_id,data_consulta desc, fonte, ordem_mdc 
                   ) primeira_fonte 
                   group by patient_id 
               ) dispensa 
               where dispensa.tipo_dispensa <> 10
        
        ) mds_tipo on mds_tipo.patient_id = p.patient_id
                         WHERE p.voided = 0 AND pi.voided = 0 AND pi.identifier_type = 2 AND pn.voided = 0 AND pe.voided = 0
                         	GROUP BY p.patient_id
                         	ORDER BY pi.identifier 
