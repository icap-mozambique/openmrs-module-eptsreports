        SELECT p.patient_id, pi.identifier, CONCAT(pn.given_name, ' ', COALESCE(pn.middle_name, ''), ' ', pn.family_name)name, pe.gender, (YEAR( :endDate) - YEAR(pe.birthdate)) age,
                    art_start_date, art_line, last_viral_load_date, case IFNULL(last_viral_load_value, IFNULL(last_viral_load_value_coded, NUll))
             										 WHEN 1306 THEN 'NIVEL BAIXO DE DETECCAO' 
             										 WHEN 23814 THEN 'INDETECTAVEL' 
             										 WHEN 23905 THEN 'Menor que 10 copias/ml'
             										 WHEN 23906 THEN 'Menor que 20 copias/ml'
             										 WHEN 23907 THEN 'Menor que 40 copias/ml'
             										 WHEN 23908 THEN 'Menor que 400 copias/ml'
             										 WHEN 23904 THEN 'Menor que 839 copias/ml' 
                                                      ELSE last_viral_load_value END
                    last_viral_load_value, last_follow_up_date, next_follow_up_date,
                    last_pickup_date, next_pickup_date, restart_art_date, last_regimen_change_date, regimen_name, 
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
                 INNER JOIN
                 (
                     SELECT patient_id, MIN(art_start_date) art_start_date FROM
                         (	
             				SELECT p.patient_id,MIN(e.encounter_datetime) art_start_date FROM patient p 
             					INNER JOIN encounter e ON p.patient_id=e.patient_id	
             					INNER JOIN obs o ON o.encounter_id=e.encounter_id 
             						WHERE e.voided=0 AND o.voided=0 AND p.voided=0 
             						AND e.encounter_type IN (18,6,9) AND o.concept_id=1255 AND o.value_coded=1256 
             						AND e.encounter_datetime <= :endDate AND e.location_id = :location
             							GROUP BY p.patient_id
             				UNION
             		
             				SELECT p.patient_id,MIN(value_datetime) art_start_date FROM patient p
             					INNER JOIN encounter e ON p.patient_id=e.patient_id
             					INNER JOIN obs o ON e.encounter_id=o.encounter_id
             						WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type in (18,6,9,53) 
             						AND o.concept_id=1190 AND o.value_datetime is NOT NULL 
             						AND o.value_datetime <= :endDate AND e.location_id = :location
             							GROUP BY p.patient_id
             				
             				UNION
             
             				SELECT pg.patient_id,MIN(date_enrolled) art_start_date FROM patient p 
             					INNER JOIN patient_program pg ON p.patient_id=pg.patient_id
             						WHERE pg.voided=0 AND p.voided=0 AND program_id=2 AND date_enrolled <= :endDate AND pg.location_id = :location
             							GROUP BY pg.patient_id
             				
             				UNION
             				
             				SELECT e.patient_id, MIN(e.encounter_datetime) AS art_start_date FROM patient p
             					INNER JOIN encounter e ON p.patient_id=e.patient_id
             				  		WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 AND e.encounter_datetime <= :endDate AND e.location_id = :location
             				  			GROUP BY p.patient_id
             			  
             				UNION
             				
             				SELECT p.patient_id,MIN(value_datetime) art_start_date FROM patient p
             					INNER JOIN encounter e ON p.patient_id=e.patient_id
             					INNER JOIN obs o ON e.encounter_id=o.encounter_id
             						WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=52 
             						AND o.concept_id=23866 and o.value_datetime is NOT NULL 
             						AND o.value_datetime <= :endDate AND e.location_id = :location
             							GROUP BY p.patient_id	
             	
             		)min_art_start_date GROUP BY min_art_start_date.patient_id
             	)start_art ON start_art.patient_id = p.patient_id 
                 LEFT JOIN 
                 (    
                     SELECT patient_id, MAX(state_date) state_date FROM 
                     (
                         SELECT pg.patient_id, MAX(ps.start_date) state_date FROM patient p 
                             INNER JOIN patient_program pg ON p.patient_id=pg.patient_id 
                             INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id 
                                 WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND pg.program_id=2 
                                 AND ps.state in (7,8,10) AND ps.end_date IS NULL 
                                 AND ps.start_date <= :endDate AND pg.location_id = :location 
                                     GROUP BY pg.patient_id           
                         UNION 
                                     
                         SELECT p.patient_id, MAX(o.obs_datetime) state_date FROM patient p 
                             INNER JOIN encounter e ON p.patient_id=e.patient_id 
                             INNER JOIN obs o ON e.encounter_id=o.encounter_id 
                                 WHERE e.voided=0 AND o.voided=0 AND p.voided=0 AND e.encounter_type IN (53,6) 
                                 AND o.concept_id IN (6272,6273) AND o.value_coded IN (1706,1366,1709) 
                                 AND o.obs_datetime <= :endDate AND e.location_id = :location 
                                     GROUP BY p.patient_id                   
                         UNION
             
                         SELECT person_id as patient_id,death_date as state_date FROM person 
                             WHERE dead=1 AND death_date IS NOT NULL AND death_date <= :endDate 
                         
                         UNION 
                         
                         SELECT p.patient_id, MAX(o.obs_datetime) state_date FROM patient p 
                             INNER JOIN encounter e ON p.patient_id=e.patient_id 
                             INNER JOIN obs o ON e.encounter_id=o.encounter_id 
                                 WHERE e.voided=0 AND p.voided=0 AND o.voided=0 AND e.encounter_type=21 
                                 AND e.encounter_datetime <= :endDate AND e.location_id = :location 
                                 AND o.concept_id IN (2031, 23944, 23945) AND o.value_coded=1366 
                                     GROUP BY p.patient_id
                     ) max_exits GROUP BY patient_id
                 )exits ON exits.patient_id = p.patient_id
                 LEFT JOIN 
                 (
                     SELECT max_expected.patient_id,MAX(last_encounter_date) last_encounter_date ,MAX(expected_date) expected_date FROM 
                     (
                         SELECT last_encounter.patient_id, last_encounter_date, expected_follow_up.expected_date FROM  
             	        (
                             SELECT MAX(e.encounter_datetime) last_encounter_date, e.patient_id FROM encounter e
                                 WHERE e.encounter_type IN(6,9)
                                     AND e.voided = 0 AND e.location_id = :location AND e.encounter_datetime <= :endDate
                                         GROUP BY e.patient_id
                         ) last_encounter
                         LEFT JOIN 
                         (
                             SELECT o.person_id patient_id, o.value_datetime expected_date, o.obs_datetime FROM obs o
                                     INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                         WHERE o.concept_id = 1410 AND e.encounter_type IN(6,9) 
                                         AND o.voided = 0 AND e.voided = 0
                         ) expected_follow_up ON expected_follow_up.patient_id = last_encounter.patient_id AND expected_follow_up.obs_datetime = last_encounter.last_encounter_date 
             
                         UNION
             
                         SELECT last_pickup.patient_id, last_encounter_date, expected_pickup.expected_date FROM  
             	        (
                             SELECT MAX(e.encounter_datetime) last_encounter_date, e.patient_id FROM encounter e
                                 WHERE e.encounter_type = 18
                                     AND e.voided = 0 AND e.location_id = :location AND e.encounter_datetime <= :endDate
                                         GROUP BY e.patient_id
                         ) last_pickup
                         LEFT JOIN 
                         (
                             SELECT o.person_id patient_id, o.value_datetime expected_date, o.obs_datetime FROM obs o
                                     INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                         WHERE o.concept_id = 5096 AND e.encounter_type = 18
                                         AND o.voided = 0 AND e.voided = 0
                         ) expected_pickup ON expected_pickup.patient_id = last_pickup.patient_id AND expected_pickup.obs_datetime = last_pickup.last_encounter_date 
             
                         UNION
             
                         SELECT o.person_id patient_id, MAX(o.value_datetime) as last_encounter_date, MAX((o.value_datetime + INTERVAL 30 DAY)) expected_date FROM obs o
                             INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                 WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                 AND o.voided = 0 AND e.voided = 0 AND o.location_id = :location AND o.value_datetime <= :endDate
                                     GROUP BY o.person_id
                     )max_expected GROUP BY max_expected.patient_id
                 )expected ON expected.patient_id = p.patient_id
                 LEFT JOIN
                 (
                     SELECT last_viral_load_date.patient_id,last_viral_load_date, last_viral_load_value, last_viral_load_value_coded FROM
                     (
                         SELECT o.person_id patient_id, MAX(o.obs_datetime) last_viral_load_date FROM obs o
             	            WHERE o.voided = 0 AND o.concept_id IN (856,1305) 
                             AND o.obs_datetime <= :endDate
                                 GROUP BY o.person_id
                     )last_viral_load_date 
                     LEFT JOIN
                     (
                         SELECT o.person_id patient_id, o.obs_datetime ,o.value_numeric last_viral_load_value, o.value_coded last_viral_load_value_coded FROM obs o
             	            WHERE o.voided = 0 AND o.concept_id IN (856,1305)
                     )last_viral_load_value ON last_viral_load_date.patient_id = last_viral_load_value.patient_id AND last_viral_load_value.obs_datetime = last_viral_load_date
                 )last_viral_load_date_value ON last_viral_load_date_value.patient_id = p.patient_id AND last_viral_load_date BETWEEN art_start_date AND :endDate
                 LEFT JOIN
                 (
                     SELECT patient_id, MAX(last_regimen_change_date) last_regimen_change_date FROM
                     (
                         SELECT o.person_id patient_id, MAX(o.obs_datetime) last_regimen_change_date FROM obs o
                             WHERE o.voided = 0 AND o.concept_id = 23742 
                                 AND o.value_coded IN (1371, 23741)
                                 AND o.obs_datetime <= :endDate
                                     GROUP BY o.person_id 
                         UNION
             
                         SELECT o.person_id patient_id, MAX(o.obs_datetime) last_regimen_change_date FROM obs o
                             WHERE o.voided = 0 AND o.concept_id IN (21188, 21190, 21187)
                                 AND o.obs_datetime <= :endDate
                                     GROUP BY o.person_id 
                     )max_regimen_date GROUP BY max_regimen_date.patient_id
                 )regimen_change ON regimen_change.patient_id = p.patient_id
                 LEFT JOIN
                 (
                    SELECT o.person_id patient_id, MIN(o.obs_datetime) first_viral_load_date_regimen FROM obs o
             	        WHERE o.voided = 0 AND o.concept_id IN (856,1305)
                                 GROUP BY o.person_id
                 )viral_load_regimen ON viral_load_regimen.patient_id = p.patient_id AND first_viral_load_date_regimen BETWEEN last_regimen_change_date AND :endDate
                 LEFT JOIN 
                 (
                     SELECT o.person_id patient_id, MAX(o.obs_datetime) restart_art_date FROM obs o
             	        WHERE o.voided = 0 AND o.concept_id = 6273 AND o.value_coded = 1705
             		        GROUP BY o.person_id
                 )restart_art ON restart_art.patient_id = p.patient_id AND restart_art_date BETWEEN art_start_date AND :endDate
                 LEFT JOIN
                 (
                     SELECT o.person_id patient_id, MAX(o.obs_datetime) abandonment_date FROM obs o
             	        WHERE o.voided = 0 AND o.concept_id = 6273 AND o.value_coded = 1705
             		        GROUP BY o.person_id
                 )abandonment ON abandonment.patient_id = p.patient_id AND abandonment_date BETWEEN art_start_date AND restart_art_date
                 LEFT JOIN (
                     SELECT max_art_line.patient_id, art_line, line_date FROM
                     (
             	        SELECT o.person_id patient_id, MAX(o.obs_datetime) line_date FROM obs o
                             WHERE o.voided = 0 AND o.concept_id = 21151
                                 AND o.value_coded IN (21150, 21148, 21149)
                                     GROUP BY o.person_id
                     ) max_art_line
                     LEFT JOIN 
                     (
                         SELECT o.person_id patient_id, CASE o.value_coded WHEN 21150 THEN 'PRIMEIRA LINHA' 
             										WHEN 21148 THEN 'SEGUNDA LINHA' 
             										WHEN 21149 THEN 'TERCEIRA LINHA'
             										END AS art_line, o.obs_datetime FROM obs o
             	            WHERE o.voided = 0 AND o.concept_id = 21151
             	                AND o.value_coded IN (21150, 21148, 21149)
                     ) art_line ON art_line.patient_id = max_art_line.patient_id AND art_line.obs_datetime = max_art_line.line_date
                 )art_line ON art_line.patient_id = p.patient_id AND line_date <= :endDate
                 LEFT JOIN (
                     SELECT last_encounter.patient_id, last_follow_up_date, next_follow_up.next_follow_up_date FROM  
             	        (
                             SELECT MAX(e.encounter_datetime) last_follow_up_date, e.patient_id FROM encounter e
                                 WHERE e.encounter_type IN(6,9)
                                     AND e.voided = 0 AND e.location_id = :location AND e.encounter_datetime <= :endDate
                                         GROUP BY e.patient_id
                         ) last_encounter
                         LEFT JOIN 
                         (
                             SELECT o.person_id patient_id, o.value_datetime next_follow_up_date, o.obs_datetime FROM obs o
                                     INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                         WHERE o.concept_id = 1410 AND e.encounter_type IN(6,9) 
                                         AND o.voided = 0 AND e.voided = 0
                         ) next_follow_up ON next_follow_up.patient_id = last_encounter.patient_id AND next_follow_up.obs_datetime = last_encounter.last_follow_up_date 
                 )last_encounter_and_follow_up ON last_encounter_and_follow_up.patient_id = p.patient_id
                 LEFT JOIN 
                 (
                     SELECT last_pickup.patient_id, MAX(last_pickup_date) last_pickup_date, MAX(next_pickup_date) next_pickup_date FROM  
             	        (
                             SELECT e.patient_id, MAX(e.encounter_datetime) last_pickup_date FROM encounter e
                                 WHERE e.encounter_type = 18
                                     AND e.voided = 0 AND e.location_id = :location AND e.encounter_datetime <= :endDate
                                         GROUP BY e.patient_id
                         ) last_pickup
                         LEFT JOIN 
                         (
                             SELECT o.person_id patient_id, o.value_datetime next_pickup_date, o.obs_datetime FROM obs o
                                     INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                         WHERE o.concept_id = 5096 AND e.encounter_type = 18
                                         AND o.voided = 0 AND e.voided = 0
                         ) next_pickup ON next_pickup.patient_id = last_pickup.patient_id AND next_pickup.obs_datetime = last_pickup.last_pickup_date 
             
                         UNION
             
                         SELECT o.person_id patient_id, MAX(o.value_datetime) as last_pickup_date, MAX((o.value_datetime + INTERVAL 30 DAY)) next_pickup_date FROM obs o
                             INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                 WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                 AND o.voided = 0 AND e.voided = 0 AND o.location_id = :location AND o.value_datetime <= :endDate
                                     GROUP BY o.person_id
                 )last_and_next_pickup ON last_and_next_pickup.patient_id = p.patient_id
                 LEFT JOIN (
                     SELECT max_regimen_date.patient_id, last_regimen_date, regimen_name FROM 
                     (
                         SELECT o.person_id patient_id, MAX(o.obs_datetime) last_regimen_date FROM obs o
                             WHERE o.voided = 0 AND o.concept_id IN (1088, 21188, 21190, 21187, 1087)
                             AND o.obs_datetime <= :endDate
                                 GROUP BY o.person_id
                     )max_regimen_date
                     LEFT JOIN 
                     (
                         SELECT o.person_id patient_id, o.value_coded, cn.name regimen_name, o.obs_datetime FROM obs o
                             INNER JOIN concept_name cn ON o.value_coded = cn.concept_id
                                 WHERE o.voided = 0 AND o.concept_id IN (1088, 21188, 21190, 21187, 1087) AND cn.voided = 0 AND cn.locale_preferred = 1 AND cn.locale = 'pt'
                     )regimen_name ON regimen_name.patient_id = max_regimen_date.patient_id AND regimen_name.obs_datetime = max_regimen_date.last_regimen_date
                 )regimen ON regimen.patient_id = p.patient_id
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
                 
             WHERE (exits.state_date IS NULL OR (exits.state_date IS NOT NULL AND expected.last_encounter_date > exits.state_date)) 
             AND (next_pickup_date + INTERVAL 28 DAY) >= :endDate
             AND ((DATEDIFF( :endDate, art_start_date) >= 180 AND last_regimen_change_date IS NULL AND restart_art_date IS NULL AND (last_viral_load_date IS NULL OR DATEDIFF( :endDate, last_viral_load_date) >= 365 AND (last_viral_load_value < 1000 OR last_viral_load_value_coded IN (1306,23907,23905,23904,23906,23814,23908,1087))))
                 OR (DATEDIFF( :endDate, last_regimen_change_date) >= 180 AND (last_viral_load_date IS NUll OR (DATEDIFF( :endDate, last_viral_load_date) >= 365 AND (last_viral_load_value < 1000 OR last_viral_load_value_coded IN (1306,23907,23905,23904,23906,23814,23908,1087)))))
                 OR (DATEDIFF( :endDate, restart_art_date) >= 180 AND (abandonment_date IS NOT NULL AND (last_viral_load_date IS NULL OR DATEDIFF( :endDate, last_viral_load_date) >= 365 AND (last_viral_load_value < 1000 OR last_viral_load_value_coded IN (1306,23907,23905,23904,23906,23814,23908,1087))))))  
             AND p.patient_id NOT IN
                 (
                     SELECT o.person_id patient_id FROM obs o
                         WHERE o.voided = 0 AND o.concept_id = 1982
                         AND o.value_coded = 1065
                             GROUP BY o.person_id
                     UNION
                     
                     SELECT o.person_id patient_id FROM obs o
                         WHERE o.voided = 0 AND o.concept_id = 6332 
                         AND o.value_coded = 1065
                             GROUP BY o.person_id
                 )
                 GROUP BY p.patient_id
                 ORDER BY pi.identifier;
