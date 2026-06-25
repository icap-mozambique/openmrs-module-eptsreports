  select (@cnt := @cnt + 1) as ID, final.* from (
  
  select  coorte12meses_final.patient_id,
								coorte12meses_final.data_inicio,
                          		pad3.county_district as 'Distrito',
                                codigo_us.value_reference,
                          		pid.identifier as NID,
                          		p.gender,
                          		 p.birthdate data_nascimento,
                                 floor(datediff( :endDate ,birthdate)/365) age,
								CASE 
									WHEN gravida_lactante.orderF IS NULL THEN ''
									WHEN gravida_lactante.orderF = 1 THEN 'Grávida'
									ELSE 'Lactante'
								END AS preg_lac,
                          		regime.ultimo_regime,
                                min_consulta.data_seguimento,
                                min_consulta2.data_seguimento2,
                                min_consulta3.data_seguimento3,
                                min_consulta4.data_seguimento4,
                                min_consulta5.data_seguimento5,
                                min_consulta6.data_seguimento6,
                                ultima_cv.data_carga data_primeiro_resultado_cv, 
                                ultima_cv.Valor_carga valor_primeiro_resultado_cv,
						        data_segundo_resultado_cv.segunda_data_carga,
                                data_segundo_resultado_cv.valor_segunda_carga_cv,
                                data_terceiro_resultado_cv.valor_carga_fc valor_terceiro_resultado_cv,
                                data_terceiro_resultado_cv.data_carga data_terceiro_resultado_cv,
								data_quarta_resultado_cv.valor_carga_fc valor_quarta_resultado_cv,
                                data_quarta_resultado_cv.data_carga data_quarta_resultado_cv,
                                pedido_visita_1.data_1 pedido_1,
                                pedido_visita_2.data_1 pedido_2,
                                pedido_visita_3.data_1 pedido_3,
                                pedido_visita_4.data_1 pedido_4,
                                pedido_visita_5.data_1 pedido_5,
                                pedido_visita_6.data_1 pedido_6,
                                data_amostra_1.data_amostra_1 data_amostra_1,
                                data_amostra_2.data_amostra_1 data_amostra_2,
								data_amostra_3.data_amostra_1 data_amostra_3,
								data_amostra_4.data_amostra_1 data_amostra_4,
								data_amostra_5.data_amostra_1 data_amostra_5,
                                data_amostra_6.data_amostra_1 data_amostra_6,
                                segunda.dataLinha,
                                IF(segunda.dataLinha, 'Sim','Não') SegundaLinha,
                                motivo.motivo_saida,
                                apss_visita_1.data_1,
								apss_visita_2.data_1 data_2,
								apss_visita_3.data_1 data_3,
								apss_visita_4.data_1 data_4,
                                codigo_us.value_reference codigo_us
                                
             from                                                                                                                    
             (select inicio_fila_seg_prox.*,     
             	    GREATEST(COALESCE(data_proximo_lev,data_recepcao_levantou30), COALESCE(data_recepcao_levantou30,data_proximo_lev)) data_usar 
             from                                                                                                                        
                 (select     inicio_fila_seg.*,                                                                                          
                 max(obs_fila.value_datetime) data_proximo_lev,                                                                          
                 date_add(data_recepcao_levantou, interval 30 day) data_recepcao_levantou30                                              
              from                                                                                                                           
            (select inicio.*,                                                                                                                    
                 saida.data_estado,                                                                                                          
                 max_fila.data_fila,
                 max_recepcao.data_recepcao_levantou                                                                                         
             from                                                                                                                                
             (   
						select patient_id,data_inicio 
						from ( 
							select patient_id,data_inicio from 
							(
								  select patient_id, min(data_inicio) data_inicio 
								  from 
										(
											  select p.patient_id, min(e.encounter_datetime) data_inicio 
											  from patient p 
													inner join encounter e on p.patient_id=e.patient_id 
													inner join obs o on o.encounter_id=e.encounter_id 
											  where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (18,6,9) 
													and o.concept_id=1255 and o.value_coded=1256 and e.encounter_datetime<= :endDate  and e.location_id=:location  
													group by p.patient_id 
											  union 
											  
											  select p.patient_id, min(value_datetime) data_inicio 
											  from patient p 
													inner join encounter e on p.patient_id=e.patient_id 
													inner join obs o on e.encounter_id=o.encounter_id 
											  where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) 
													and o.concept_id=1190 and o.value_datetime is not null and o.value_datetime<= :endDate  and e.location_id=:location  
													group by p.patient_id 
											  
											  union 
								   
											  select pg.patient_id, min(date_enrolled) data_inicio 
											  from patient p 
													inner join patient_program pg on p.patient_id=pg.patient_id 
											  where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<= :endDate  and location_id=:location  
													group by pg.patient_id 
											  
											  union 
							
											  select e.patient_id, min(e.encounter_datetime) as data_inicio 
											  from patient p 
													inner join encounter e on p.patient_id=e.patient_id 
											  where p.voided=0 and e.encounter_type=18 and e.voided=0 and e.encounter_datetime<= :endDate  and e.location_id=:location  
													group by p.patient_id 
											  
											  union 
								   
											  select p.patient_id, min(value_datetime) data_inicio 
											  from patient p 
													inner join encounter e on p.patient_id=e.patient_id 
													inner join obs o on e.encounter_id=o.encounter_id 
											  where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 
													and o.concept_id=23866 and o.value_datetime is not null and o.value_datetime<= :endDate  and e.location_id=:location  
													group by p.patient_id
										) 
								  art_start group by patient_id 
							) tx_new where data_inicio <=  :endDate  and data_inicio < '2023-12-21'
							union
							select tx_new.patient_id, tx_new.data_inicio
							from 
							(
								  select tx_new.patient_id, tx_new.data_inicio 
								  from
								  ( 
										select patient_id, data_inicio from 
										(
											  select patient_id, min(data_inicio) data_inicio 
											  from 
													(
														  select e.patient_id, min(e.encounter_datetime) as data_inicio 
														  from patient p 
																inner join encounter e on p.patient_id=e.patient_id 
														  where p.voided=0 and e.encounter_type=18 and e.voided=0 and e.encounter_datetime<= :endDate  and e.location_id=:location  
																group by p.patient_id 
														  
														  union 
											   
														  select p.patient_id, min(value_datetime) data_inicio 
														  from patient p 
																inner join encounter e on p.patient_id=e.patient_id 
																inner join obs o on e.encounter_id=o.encounter_id 
														  where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 
																and o.concept_id=23866 and o.value_datetime is not null and o.value_datetime<= :endDate  and e.location_id=:location  
																group by p.patient_id
													) 
											  art_start group by patient_id 
										) tx_new where data_inicio <=  :endDate  and data_inicio >= '2023-12-21'
								  ) tx_new
								  left join
								  (
										select patient_id from 
										(
											  select patient_id, min(data_inicio) data_inicio 
											  from 
													(
														  select p.patient_id, min(e.encounter_datetime) data_inicio 
														  from patient p 
																inner join encounter e on p.patient_id=e.patient_id 
																inner join obs o on o.encounter_id=e.encounter_id 
														  where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (18,6,9) 
																and o.concept_id=1255 and o.value_coded=1256 and e.encounter_datetime<= :endDate  and e.location_id=:location  
																group by p.patient_id 
														  union 
														  
														  select p.patient_id, min(value_datetime) data_inicio 
														  from patient p 
																inner join encounter e on p.patient_id=e.patient_id 
																inner join obs o on e.encounter_id=o.encounter_id 
														  where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) 
																and o.concept_id=1190 and o.value_datetime is not null and o.value_datetime<= :endDate  and e.location_id=:location  
																group by p.patient_id 
														  
														  union 
											   
														  select pg.patient_id, min(date_enrolled) data_inicio 
														  from patient p 
																inner join patient_program pg on p.patient_id=pg.patient_id 
														  where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<= :endDate  and location_id=:location  
																group by pg.patient_id 
													) 
											  art_start group by patient_id 
										) tx_new where data_inicio < '2023-12-21'
								  ) tx_new_period_anterior on tx_new.patient_id = tx_new_period_anterior.patient_id
								   where tx_new_period_anterior.patient_id is null
							) tx_new
						) inicio
              )inicio                                                                                                                                    
              left join                                                                                                                                  
             ( 
                select patient_id,max(data_estado) data_estado                                                                                              
                     from                                                                                                                                        
                         (                                                                                                                                       
                             select distinct max_estado.patient_id, max_estado.data_estado from (                                                                
                                 select  pg.patient_id,                                                                                                          
                                         max(ps.start_date) data_estado                                                                                          
                                 from    patient p                                                                                                               
                                     inner join patient_program pg on p.patient_id = pg.patient_id                                                               
                                     inner join patient_state ps on pg.patient_program_id = ps.patient_program_id                                                
                                 where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id = 2                                        
                                     and ps.start_date<=  :endDate  and pg.location_id =:location  group by pg.patient_id                                           
                             ) max_estado                                                                                                                        
                                 inner join patient_program pp on pp.patient_id = max_estado.patient_id                                                          
                                 inner join patient_state ps on ps.patient_program_id = pp.patient_program_id and ps.start_date = max_estado.data_estado         
                             where pp.program_id = 2 and ps.state = 8 and pp.voided = 0 and ps.voided = 0 and pp.location_id = :location                  
                             
							 union                                                                                                                               
                             
							 select dead_state.patient_id, dead_state.data_estado 
							 from (
											select patient_id,max(data_estado) data_estado                                                                                              
											from (  
											
											select distinct max_estado.patient_id, max_estado.data_estado 
											from (                                                                
														select  pg.patient_id,                                                                                                          
															max(ps.start_date) data_estado                                                                                          
														from patient p                                                                                                               
															inner join patient_program pg on p.patient_id = pg.patient_id                                                               
															inner join patient_state ps on pg.patient_program_id = ps.patient_program_id                                                
														where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id = 2                                        
															and ps.start_date<=  :endDate  and pg.location_id =:location  group by pg.patient_id                                           
											) max_estado                                                                                                                        
												inner join patient_program pp on pp.patient_id = max_estado.patient_id                                                          
												inner join patient_state ps on ps.patient_program_id = pp.patient_program_id and ps.start_date = max_estado.data_estado         
											where pp.program_id = 2 and ps.state = 10 and pp.voided = 0 and ps.voided = 0 and pp.location_id = :location   
											 union
											select  p.patient_id,                                                                                                               
												max(o.obs_datetime) data_estado                                                                                             
											from patient p                                                                                                                   
												inner join encounter e on p.patient_id=e.patient_id                                                                         
												inner join obs  o on e.encounter_id=o.encounter_id                                                                          
											where e.voided=0 and o.voided=0 and p.voided=0                                                                
												and e.encounter_type in (53,6) and o.concept_id in (6272,6273) and o.value_coded = 1366                         
												and o.obs_datetime<= :endDate  and e.location_id=:location                                                                         
												group by p.patient_id                                                                                                               
											union                                                                                                                               
											select person_id as patient_id,death_date as data_estado                                                                            
											from person                                                                                                                         
											where dead=1 and voided = 0 and death_date is not null and death_date<= :endDate  
											union                                                                                                                               
											select  p.patient_id,                                                                                                               
												max(obsObito.obs_datetime) data_estado                                                                                      
											from patient p                                                                                                                   
												inner join encounter e on p.patient_id=e.patient_id                                                                         
												inner join obs obsObito on e.encounter_id=obsObito.encounter_id                                                             
											where e.voided=0 and p.voided=0 and obsObito.voided=0                                                        
												and e.encounter_type in (21,36,37) and  e.encounter_datetime<= :endDate  and  e.location_id=:location                           
												and obsObito.concept_id in (2031,23944,23945) and obsObito.value_coded=1366                                                     
											group by p.patient_id                                                                                                               
									) dead_state group by dead_state.patient_id  
							) dead_state 
							inner join
							(
									select fila_seguimento.patient_id,max(fila_seguimento.data_encountro) data_encountro
									from(
												select p.patient_id,max(encounter_datetime) data_encountro                                                                                                
												from    patient p                                                                                                                                   
														inner join encounter e on e.patient_id=p.patient_id                                                                                         
												where   p.voided=0 and e.voided=0 and e.encounter_type=18                                                                      
														and e.location_id=:location  and e.encounter_datetime<= :endDate                                                                                 
														group by p.patient_id  
												union
												
												select  p.patient_id,max(encounter_datetime) data_encountro                                                                                    
												from patient p                                                                                                                                   
													inner join encounter e on e.patient_id=p.patient_id                                                                                         
												where   p.voided=0 and e.voided=0 and e.encounter_type in (6,9)                                                                
													and e.location_id=:location  and e.encounter_datetime<= :endDate                                                                                 
													group by p.patient_id   
									) fila_seguimento	group by fila_seguimento.patient_id  
							 ) fila_seguimento on dead_state.patient_id = fila_seguimento.patient_id
								where fila_seguimento.data_encountro is null or  fila_seguimento.data_encountro <= dead_state.data_estado
							 
							 union
							 
							 select  p.patient_id,                                                                                                               
                                     max(o.obs_datetime) data_estado                                                                                             
                             from    patient p                                                                                                                   
                                     inner join encounter e on p.patient_id=e.patient_id                                                                         
                                     inner join obs  o on e.encounter_id=o.encounter_id                                                                          
                             where   e.voided=0 and o.voided=0 and p.voided=0 and                                                              
                                     e.encounter_type in (53,6) and o.concept_id in (6272,6273) and o.value_coded = 1709 and                        
                                     o.obs_datetime<=:endDate  and e.location_id=:location                                                                         
                             group by p.patient_id                                                                                                               
                             union
 					         select saidas_por_transferencia.patient_id, data_estado 
	                         from
	                        	(
		                            select saidas_por_transferencia.patient_id, max(data_estado) data_estado
		                            from
		                                (
			                           	select distinct max_estado.patient_id, max_estado.data_estado 
			                           	from 
			                           		(                                                                
				                                 select pg.patient_id, max(ps.start_date) data_estado                                                                                          
				                                 from patient p                                                                                                               
				                                 		inner join patient_program pg on p.patient_id = pg.patient_id                                                               
				                                     	inner join patient_state ps on pg.patient_program_id = ps.patient_program_id                                                
				                                 where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id = 2                                        
				                                 		and ps.start_date<=:endDate  and pg.location_id =:location  group by pg.patient_id                                           
			                             		) max_estado                                                                                                                        
			                                 		inner join patient_program pp on pp.patient_id = max_estado.patient_id                                                          
			                                 		inner join patient_state ps on ps.patient_program_id = pp.patient_program_id and ps.start_date = max_estado.data_estado         
			                             		where pp.program_id = 2 and ps.state = 7 and pp.voided = 0 and ps.voided = 0 and pp.location_id = :location                  
			                             
			                             		union                                                                                                                               
			                             		
			                             		select  p.patient_id,max(o.obs_datetime) data_estado                                                                                             
			                             		from patient p                                                                                                                   
			                                     	inner join encounter e on p.patient_id=e.patient_id                                                                         
			                                     	inner join obs o on e.encounter_id=o.encounter_id                                                                          
			                             		where e.voided=0 and o.voided=0 and p.voided=0                                                                
			                                   	and e.encounter_type in (53,6) and o.concept_id in (6272,6273) and o.value_coded = 1706                         
			                                     	and o.obs_datetime<= :endDate  and e.location_id=:location                                                                         
			                             			group by p.patient_id                                                                                                               
			                             		
			                             	     union                                                                                                                               
				                             
				                             	select ultimaBusca.patient_id, ultimaBusca.data_estado                                                                              
				                             	from (                                                                                                                              
				                                     select p.patient_id,max(e.encounter_datetime) data_estado                                                                   
				                                     from patient p                                                                                                              
				                                         inner join encounter e on p.patient_id=e.patient_id                                                                     
				                                         inner join obs o on o.encounter_id=e.encounter_id                                                                       
				                                     where e.voided=0 and p.voided=0 and e.encounter_datetime<=:endDate                                      
				                                         and e.encounter_type = 21 and  e.location_id= :location                                                                  
				                                         group by p.patient_id                                                                                                   
				                                 ) ultimaBusca                                                                                                                   
				                                     inner join encounter e on e.patient_id = ultimaBusca.patient_id                                                             
				                                     inner join obs o on o.encounter_id = e.encounter_id                                                                         
				                                where e.encounter_type = 21 and o.voided=0 and o.concept_id=2016 and o.value_coded in (1706,23863) and ultimaBusca.data_estado = e.encounter_datetime and e.location_id = :location  
		                                ) saidas_por_transferencia 
	                                	group by patient_id 
                               	) saidas_por_transferencia
                                left join
			                 	(  
				                    select patient_id, max(data_ultimo_levantamento)  data_ultimo_levantamento    
				                    from
				                    (
		                        		select p.patient_id, date_add(max(o.value_datetime), interval 1 day) data_ultimo_levantamento                                                                                            
										from patient p                                                                                                                                   
											inner join encounter e on e.patient_id= p.patient_id 
											inner join obs o on o.encounter_id = e.encounter_id                                                                                        
										where p.voided= 0 and e.voided=0 and o.voided = 0 and e.encounter_type=18 and o.concept_id = 5096                                                                  
											and e.location_id=:location  and e.encounter_datetime <=:endDate                                                                              
											group by p.patient_id 
		                        
		                        		union
		                        
			                        	select p.patient_id, date_add(max(value_datetime), interval 31 day) data_ultimo_levantamento                                                                                     
			                        	from patient p                                                                                                                                   
			                         	  inner join encounter e on p.patient_id=e.patient_id                                                                                         
			                              inner join obs o on e.encounter_id=o.encounter_id                                                                                           
			                        	where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52                                                       
			                              and o.concept_id=23866 and o.value_datetime is not null and e.location_id=:location  and o.value_datetime <=  :endDate                                                                                       
			                        	group by p.patient_id
				                    	) ultimo_levantamento group by patient_id
			                		) ultimo_levantamento on saidas_por_transferencia.patient_id = ultimo_levantamento.patient_id 
			               		where ultimo_levantamento.data_ultimo_levantamento <=:endDate  
				                                                                                                                                                                         
                         	) allSaida                                                                                                                                      
                 				group by patient_id 
                
              		) saida on inicio.patient_id = saida.patient_id                                                                                                      
             left join                                                                                                                                           
              ( select p.patient_id,max(encounter_datetime) data_fila                                                                                                
             from    patient p                                                                                                                                   
                     inner join encounter e on e.patient_id=p.patient_id                                                                                         
             where   p.voided=0 and e.voided=0 and e.encounter_type=18 and                                                                     
                     e.location_id=:location  and e.encounter_datetime<=:endDate                                                                                 
             group by p.patient_id                                                                                                                               
             ) max_fila on inicio.patient_id=max_fila.patient_id
                 inner join                     ( select glc.patient_id from (       
                             select p.patient_id, max(e.encounter_datetime) dataa 
							    from patient p 
								inner join encounter e on p.patient_id=e.patient_id 
								inner join obs o on o.encounter_id=e.encounter_id 
								where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (53,6,9) 
								and o.concept_id=6332 and o.value_coded=1065 and e.encounter_datetime
                                between :startDate and :endDate and e.location_id=:location  
								group by p.patient_id 
                            union
							select p.patient_id, max(e.encounter_datetime) dataa 
							    from patient p 
								inner join encounter e on p.patient_id=e.patient_id 
								inner join obs o on o.encounter_id=e.encounter_id 
								where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (53,6,9) 
								and o.concept_id=1982 and o.value_coded=1065 and e.encounter_datetime
                                between :startDate and :endDate and e.location_id=:location  
								group by p.patient_id 
                             union
                            select p.person_id, p.birthdate dataa 
                            from person p where floor(datediff(:endDate,p.birthdate)/365) <5 ) glc group by patient_id ) tt on tt.patient_id= inicio.patient_id          
              left join                                                                                                                                          
              (                                                                                                                                                  
             select  p.patient_id,max(value_datetime) data_recepcao_levantou                                                                                     
             from    patient p                                                                                                                                   
                     inner join encounter e on p.patient_id=e.patient_id                                                                                         
                     inner join obs o on e.encounter_id=o.encounter_id                                                                                           
             where   p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and                                                      
                     o.concept_id=23866 and o.value_datetime is not null and                                                                                     
                     o.value_datetime<= :endDate  and e.location_id=:location                                                                                       
             group by p.patient_id                                                                                                                               
              ) max_recepcao on inicio.patient_id=max_recepcao.patient_id                                                                                        
              group by inicio.patient_id                                                                                                                         
             ) inicio_fila_seg
             left join                                                                                                                                          
              encounter ultimo_fila_data_criacao on ultimo_fila_data_criacao.patient_id=inicio_fila_seg.patient_id                                                                                      
             	and ultimo_fila_data_criacao.voided=0                                     
               	and ultimo_fila_data_criacao.encounter_type = 18  
              	and ultimo_fila_data_criacao.encounter_datetime = inicio_fila_seg.data_fila                                                                                            
              	and ultimo_fila_data_criacao.location_id=:location                      
              left join                                                                                                                                          
              obs obs_fila on obs_fila.person_id=inicio_fila_seg.patient_id                                                                                      
               	and obs_fila.voided=0                                                                                                                             
              	and (obs_fila.obs_datetime=inicio_fila_seg.data_fila  or (ultimo_fila_data_criacao.date_created = obs_fila.date_created and ultimo_fila_data_criacao.encounter_id = obs_fila.encounter_id ))                                                                                                       
              	and obs_fila.concept_id=5096                                                                                                                       
              	and obs_fila.location_id=:location                                                                                                                  
             group by inicio_fila_seg.patient_id                                                                                                                 
             ) inicio_fila_seg_prox                                                                                                                              
             group by patient_id                                                                                                                                 
             ) coorte12meses_final
             inner join person p on p.person_id=coorte12meses_final.patient_id

                          left join 
                          (	select pad1.*
                          	from person_address pad1
                          	inner join 
                          	(
                          		select person_id,min(person_address_id) id 
                          		from person_address
                          		where voided=0
                          		group by person_id
                          	) pad2
                          	where pad1.person_id=pad2.person_id and pad1.person_address_id=pad2.id
                          ) pad3 on pad3.person_id=coorte12meses_final.patient_id				
                          left join 			
                          (	select pn1.*
                          	from person_name pn1
                          	inner join 
                          	(
                          		select person_id,min(person_name_id) id 
                          		from person_name
                          		where voided=0
                          		group by person_id
                          	) pn2
                          	where pn1.person_id=pn2.person_id and pn1.person_name_id=pn2.id
                          ) pn on pn.person_id=coorte12meses_final.patient_id			
                          left join
                          (   select pid1.*
                          	from patient_identifier pid1
                          	inner join
                          	(
                          		select patient_id,min(patient_identifier_id) id
                          		from patient_identifier
                          		where voided=0
                          		group by patient_id
                          	) pid2
                          	where pid1.patient_id=pid2.patient_id and pid1.patient_identifier_id=pid2.id
                          ) pid on pid.patient_id=coorte12meses_final.patient_id
                          left join
                          (
                          	select 	pg.patient_id
                          	from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id
                          	where 	pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate  and location_id=:location 
                          ) programa on programa.patient_id=coorte12meses_final.patient_id
                          left join
                          (
                          	select distinct member_id from gaac_member where voided=0
                          ) gaaac on gaaac.member_id=coorte12meses_final.patient_id
                          left join 
                          (
                          	select 	ultimo_lev.patient_id,
                          			case o.value_coded				
                          			when 1703 then 'AZT3TCEFV'
                          			when 6100 then 'AZT3TCLPV/r'
                          			when 1651 then 'AZT3TCNVP'
                          			when 6324 then 'TDF3TCEFV'
                          			when 6104 then 'ABC3TCEFV'
                          			when 23784 then 'TDF3TCDTG'
                          			when 23786 then 'ABC3TCDTG'
                          			when 6116 then 'AZT3TCABC'
                          			when 6106 then 'ABC3TCLPV/r'
                          			when 6105 then 'ABC3TCNVP'
                          			when 6108 then 'TDF3TCLPV/r'
                          			when 23790 then 'TDF3TCLPV/rRTV'
                          			when 23791 then 'TDF3TCATV/r'
                          			when 23792 then 'ABC3TCATV/r'
                          			when 23793 then 'AZT3TCATV/r'
                          			when 23795 then 'ABC3TCATV/rRAL'
                          			when 23796 then 'TDF3TCATV/rRAL'
                          			when 23801 then 'AZT3TCRAL'
                          			when 23802 then 'AZT3TCDRV/r'
                          			when 23815 then 'AZT3TCDTG'
                          			when 6329 then 'TDF3TCRALDRV/r'
                          			when 23797 then 'ABC3TCDRV/rRAL'
                          			when 23798 then '3TCRALDRV/r'
                          			when 23803 then 'AZT3TCRALDRV/r'						
                          			when 6243 then 'TDF3TCNVP'
                          			when 6103 then 'D4T3TCLPV/r'
                          			when 792 then 'D4T3TCNVP'
                          			when 1827 then 'D4T3TCEFV'
                          			when 6102 then 'D4T3TCABC'						
                          			when 1311 then 'ABC3TCLPV/r'
                          			when 1312 then 'ABC3TCNVP'
                          			when 1313 then 'ABC3TCEFV'
                          			when 1314 then 'AZT3TCLPV/r'
                          			when 1315 then 'TDF3TCEFV'						
                          			when 6330 then 'AZT3TCRALDRV/r'			
                          			when 6325 then 'D4T3TCABCLPV/r'
                          			when 6326 then 'AZT3TCABCLPV/r'
                          			when 6327 then 'D4T3TCABCEFV'
                          			when 6328 then 'AZT3TCABCEFV'
                          			when 6109 then 'AZTDDILPV/r'
                          			when 21163 then 'AZT3TCLPV/r'						
                          			when 23799 then 'TDF3TCDTG'
                          			when 23800 then 'ABC3TCDTG'				
                          			when 6110 then 'D4T203TCNVP'
                          			when 1702 then 'AZT3TCNFV'
                          			when 817  then 'AZT3TCABC'				
                          			when 6244 then 'AZT3TCRTV'
                          			when 1700 then 'AZTDDlNFV'
                          			when 633  then 'EFV'
                          			when 625  then 'D4T'
                          			when 631  then 'NVP'
                          			when 628  then '3TC'
                          			when 635  then 'NFV'
                          			when 797  then 'AZT'
                          			when 814  then 'ABC'
                          			when 6107 then 'TDFAZT3TCLPV/r'
                          			when 6236 then 'D4TDDIRTV-IP'
                          			when 1701 then 'ABCDDINFV'				
                          			when 6114 then '3DFC'
                          			when 6115 then '2DFCEFV'
                          			when 6233 then 'AZT3TCDDILPV'
                          			when 6234 then 'ABCTDFLPV'
                          			when 6242 then 'D4TDDINVP'
                          			when 6118 then 'DDI50ABCLPV'				
                          			else 'OUTRO' end as ultimo_regime,
                          			ultimo_lev.encounter_datetime data_regime
                          	from 	obs o,				
                          			(	select p.patient_id,max(encounter_datetime) as encounter_datetime
                          				from 	patient p
                          						inner join encounter e on p.patient_id=e.patient_id								
                          				where 	encounter_type in (6,9) and e.voided=0 and
                          						encounter_datetime <=:endDate  and e.location_id=:location  and p.voided=0
                          				group by patient_id
                          			) ultimo_lev
                          	where 	o.person_id=ultimo_lev.patient_id and o.obs_datetime=ultimo_lev.encounter_datetime and o.voided=0 and 
                          			o.concept_id=1087 and o.location_id=:location 
                          ) regime on regime.patient_id=coorte12meses_final.patient_id
                          left join 
                          (
                          		select 	patient_id,
                          			case value_coded			
                          				when 1377 then 'HSH'				
                          				when 1901 then 'MTS'				
                          				when 20426 then 'REC'				
                          				when 20454 then 'PID'
                          				when 5622 then 'OUTRO'
                          			else null end as keypop,
                          			case ordem			
                          				when 1 then 'Ficha Clinica'				
                          				when 2 then 'Ficha Apss'				
                          				when 3 then 'Demografico'
                          			else null end as fonte,
                          			obs_datetime as data_keypop
                          				
                          		from
                          		(
                          			select *
                          			from 
                          			(
                          				select maxkp.patient_id, o.value_coded,o.obs_datetime,if(e.encounter_type=6,1,2) ordem
                          				from 
                          				(	Select 	p.patient_id,max(e.encounter_datetime) maxkpdate
                          					from 	patient p 
                          							inner join encounter e on p.patient_id=e.patient_id
                          							inner join obs o on e.encounter_id=o.encounter_id
                          					where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id=23703 and 
                          							e.encounter_type in (6,46,35) and e.encounter_datetime<=:endDate  and 
                          							e.location_id=:location 
                          					group by p.patient_id
                          				) maxkp
                          				inner join encounter e on e.patient_id=maxkp.patient_id and maxkp.maxkpdate=e.encounter_datetime
                          				inner join obs o on o.encounter_id=e.encounter_id and maxkp.maxkpdate=o.obs_datetime 
                          				where o.concept_id=23703 and o.voided=0 and e.encounter_type in (6,46,35) and e.voided=0 and e.location_id=:location 
                          
                          				union 
                          
                          				Select 	person_id,
                          						case upper(value)
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
                          						date(date_created),
                          						3 as ordem 
                          				from 	person_attribute
                          				where 	person_attribute_type_id=24 and value is not null and value<>'' and voided=0 and date(date_created)<=:endDate 
                          			) allkpsource
                          			order by patient_id,obs_datetime,ordem
                          		) allkpsorcetakefirst
                          		group by patient_id
                          	) finalkptable on finalkptable.patient_id=coorte12meses_final.patient_id
                            
						left join (									select  p.patient_id,                                                                                                               
                                     min(o.obs_datetime) data_estados, o.value_text referred_from                                                                                            
                             from    patient p                                                                                                                   
                                     inner join encounter e on p.patient_id=e.patient_id                                                                         
                                     inner join obs  o on e.encounter_id=o.encounter_id                                                                          
                             where   e.voided=0 and o.voided=0 and p.voided=0 and                                                              
                                     e.encounter_type =53 and o.concept_id = 23811  and                        
                                     o.obs_datetime<=:endDate and e.location_id=:location  group by p.patient_id ) origem on origem.patient_id=coorte12meses_final.patient_id
                          LEFT JOIN (
                          SELECT fila.data_filas, fila.duracao, fila.code, fila.proximo_levantamento,fila.patient_id from (select distinct max_filaFinal.patient_id,max_filaFinal.data_filas, 
DATEDIFF(obs_proximo.value_datetime, max_filaFinal.data_filas) duracao,'FILA',
case  o.value_coded 
when 1703 then 'AZT+3TC+EFV' 
when 6100 then 'AZT+3TC+LPV/r' 
when 1651 then 'AZT+3TC+NVP' 
when 6324 then 'TDF+3TC+EFV' 
when 6104 then 'ABC+3TC+EFV' 
when 23784 then 'TDF+3TC+DTG' 
when 23786 then 'ABC+3TC+DTG' 
when 6116 then 'AZT+3TC+ABC' 
when 6106 then 'ABC+3TC+LPV/r' 
when 6105 then 'ABC+3TC+NVP' 
when 6108 then 'TDF+3TC+LPV/r' 
when 23790 then 'TDF+3TC+LPV/r+RTV' 
when 23791 then 'TDF+3TC+ATV/r' 
when 23792 then 'ABC+3TC+ATV/r' 
when 23793 then 'AZT+3TC+ATV/r' 
when 23795 then 'ABC+3TC+ATV/r+RAL' 
when 23796 then 'TDF+3TC+ATV/r+RAL' 
when 23801 then 'AZT+3TC+RAL' 
when 23802 then 'AZT+3TC+DRV/r' 
when 23815 then 'AZT+3TC+DTG' 
when 6329 then 'TDF+3TC+RAL+DRV/r' 
when 23797 then 'ABC+3TC+DRV/r+RAL' 
when 23798 then '3TC+RAL+DRV/r' 
when 23803 then 'AZT+3TC+RAL+DRV/r' 
when 6243 then 'TDF+3TC+NVP' 
when 6103 then 'D4T+3TC+LPV/r' 
when 792 then 'D4T+3TC+NVP' 
when 1827 then 'D4T+3TC+EFV' 
when 6102 then 'D4T+3TC+ABC' 
when 1311 then 'ABC+3TC+LPV/r' 
when 1312 then 'ABC+3TC+NVP' 
when 1313 then 'ABC+3TC+EFV' 
when 1314 then 'AZT+3TC+LPV/r' 
when 1315 then 'TDF+3TC+EFV'    
when 6330 then 'AZT+3TC+RAL+DRV/r' 
when 6325 then 'D4T+3TC+ABC+LPV/r' 
when 6326 then 'AZT+3TC+ABC+LPV/r' 
when 6327 then 'D4T+3TC+ABC+EFV' 
when 6328 then 'AZT+3TC+ABC+EFV' 
when 6109 then 'AZT+DDI+LPV/r' 
when 21163 then 'AZT+3TC+LPV/r' 
when 23799 then 'TDF+3TC+DTG ' 
when 23800 then 'ABC+3TC+DTG '  
when 6110 then  'D4T20+3TC+NVP' 
when 1702 then 'AZT+3TC+NFV' 
when 817  then 'AZT+3TC+ABC' 
when 6244 then 'AZT+3TC+RTV' 
when 1700 then 'AZT+DDl+NFV' 
when 633  then 'EFV' 
when 625  then 'D4T' 
when 631  then 'NVP' 
when 628  then '3TC' 
when 635  then 'NFV' 
when 797  then 'AZT' 
when 814  then 'ABC' 
when 6107 then 'TDF+AZT+3TC+LPV/r' 
when 6236 then 'D4T+DDI+RTV-IP' 
when 1701 then 'ABC+DDI+NFV' 
when 6114 then 'AZT60+3TC+NVP' 
when 6115 then '2DFC+EFV' 
when 6233 then 'AZT+3TC+DDI+LPV' 
when 6234 then 'ABC+TDF+LPV' 
when 6242 then 'D4T+DDI+NVP' 
when 6118 then 'DDI50+ABC+LPV' 
when 23785 then 'TDF+3TC+DTG2' 
when 5424 then 'OUTRO MEDICAMENTO ANTI-RETROVIRAL'
else null end as code,obs_proximo.value_datetime proximo_levantamento from ( 
Select p.patient_id,max(encounter_datetime) data_filas,e.encounter_id  from  patient p  
inner join encounter e on e.patient_id=p.patient_id 
where p.voided=0 and e.voided=0 and e.encounter_type in (18) and   
e.location_id=:location  and e.encounter_datetime  <=:endDate  
group by p.patient_id  
) max_filaFinal  
inner join obs o on o.person_id=max_filaFinal.patient_id and o.concept_id=1088 and o.obs_datetime=max_filaFinal.data_filas and o.voided=0 
inner join obs obs_proximo on obs_proximo.person_id=max_filaFinal.patient_id and obs_proximo.concept_id=5096 and obs_proximo.obs_datetime=max_filaFinal.data_filas and obs_proximo.voided=0 ) fila
 inner join 


                          	(select 	ultimo_lev.patient_id,
									o.value_text,
                          			case o.value_coded				
                          			when 1703 then 'AZT3TCEFV'
                          			when 6100 then 'AZT3TCLPV/r'
                          			when 1651 then 'AZT3TCNVP'
                          			when 6324 then 'TDF3TCEFV'
                          			when 6104 then 'ABC3TCEFV'
                          			when 23784 then 'TDF3TCDTG'
                          			when 23786 then 'ABC3TCDTG'
                          			when 6116 then 'AZT3TCABC'
                          			when 6106 then 'ABC3TCLPV/r'
                          			when 6105 then 'ABC3TCNVP'
                          			when 6108 then 'TDF3TCLPV/r'
                          			when 23790 then 'TDF3TCLPV/rRTV'
                          			when 23791 then 'TDF3TCATV/r'
                          			when 23792 then 'ABC3TCATV/r'
                          			when 23793 then 'AZT3TCATV/r'
                          			when 23795 then 'ABC3TCATV/rRAL'
                          			when 23796 then 'TDF3TCATV/rRAL'
                          			when 23801 then 'AZT3TCRAL'
                          			when 23802 then 'AZT3TCDRV/r'
                          			when 23815 then 'AZT3TCDTG'
                          			when 6329 then 'TDF3TCRALDRV/r'
                          			when 23797 then 'ABC3TCDRV/rRAL'
                          			when 23798 then '3TCRALDRV/r'
                          			when 23803 then 'AZT3TCRALDRV/r'						
                          			when 6243 then 'TDF3TCNVP'
                          			when 6103 then 'D4T3TCLPV/r'
                          			when 792 then 'D4T3TCNVP'
                          			when 1827 then 'D4T3TCEFV'
                          			when 6102 then 'D4T3TCABC'						
                          			when 1311 then 'ABC3TCLPV/r'
                          			when 1312 then 'ABC3TCNVP'
                          			when 1313 then 'ABC3TCEFV'
                          			when 1314 then 'AZT3TCLPV/r'
                          			when 1315 then 'TDF3TCEFV'						
                          			when 6330 then 'AZT3TCRALDRV/r'			
                          			when 6325 then 'D4T3TCABCLPV/r'
                          			when 6326 then 'AZT3TCABCLPV/r'
                          			when 6327 then 'D4T3TCABCEFV'
                          			when 6328 then 'AZT3TCABCEFV'
                          			when 6109 then 'AZTDDILPV/r'
                          			when 21163 then 'AZT3TCLPV/r'						
                          			when 23799 then 'TDF3TCDTG'
                          			when 23800 then 'ABC3TCDTG'				
                          			when 6110 then 'D4T203TCNVP'
                          			when 1702 then 'AZT3TCNFV'
                          			when 817  then 'AZT3TCABC'				
                          			when 6244 then 'AZT3TCRTV'
                          			when 1700 then 'AZTDDlNFV'
                          			when 633  then 'EFV'
                          			when 625  then 'D4T'
                          			when 631  then 'NVP'
                          			when 628  then '3TC'
                          			when 635  then 'NFV'
                          			when 797  then 'AZT'
                          			when 814  then 'ABC'
                          			when 6107 then 'TDFAZT3TCLPV/r'
                          			when 6236 then 'D4TDDIRTV-IP'
                          			when 1701 then 'ABCDDINFV'				
                          			when 6114 then '3DFC'
                          			when 6115 then '2DFCEFV'
                          			when 6233 then 'AZT3TCDDILPV'
                          			when 6234 then 'ABCTDFLPV'
                          			when 6242 then 'D4TDDINVP'
                          			when 6118 then 'DDI50ABCLPV'				
                          			else 'OUTRO' end as ultimo_regime,
                          			ultimo_lev.encounter_datetime data_regime
                          	from 	obs o,				
                          			(	select p.patient_id,max(encounter_datetime) as encounter_datetime
                          				from 	patient p
                          						inner join encounter e on p.patient_id=e.patient_id								
                          				where 	encounter_type in (6,9) and e.voided=0 and
                          						encounter_datetime <=:endDate  and e.location_id=:location  and p.voided=0
                          				group by patient_id
                          			) ultimo_lev
                          	where 	o.person_id=ultimo_lev.patient_id and o.obs_datetime=ultimo_lev.encounter_datetime and o.voided=0 and 
                          			o.concept_id in (1087) and o.location_id=:location 
                                    group by o.person_id ) consulta on fila.patient_id = consulta.patient_id AND fila.data_filas >= consulta.data_regime )
                                    diferenca on diferenca.patient_id = coorte12meses_final.patient_id
                                    LEFT JOIN
                                     (	Select 	p.patient_id,min(encounter_datetime) data_seguimento
                          	from 	patient p 
                          			inner join encounter e on e.patient_id=p.patient_id
                          	where 	p.voided=0 and e.voided=0 and e.encounter_type in (6,9) and 
                          			e.location_id=:location  and e.encounter_datetime between :startDate AND  :endDate 
                          	group by p.patient_id
                          ) min_consulta on coorte12meses_final.patient_id=min_consulta.patient_id
            
                                
							left join
                            (
               Select p.patient_id,min(o.obs_datetime) data_carga
               from patient p
                            inner join encounter e on p.patient_id=e.patient_id
                            inner join obs o on e.encounter_id=o.encounter_id
                       where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53,13,51) and  
               o.concept_id in (856,1305) and  o.obs_datetime BETWEEN :startDate AND :endDate  and
               e.location_id=:location 
               group by p.patient_id
               
               ) carga_viral on carga_viral.patient_id=coorte12meses_final.patient_id
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
               			Select p.patient_id,min(o.obs_datetime) data_carga, e.encounter_type
               			from 	patient p 
               					inner join encounter e on p.patient_id=e.patient_id 
               					inner join obs o on e.encounter_id=o.encounter_id 
               			where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (13,51,6,9,53) and  o.concept_id in (856,1305) and  
               					o.obs_datetime BETWEEN :startDate AND :endDate  and e.location_id=:location 
               			group by p.patient_id
               		) ultima_carga 
               			inner join obs on obs.person_id=ultima_carga.patient_id and obs.obs_datetime=ultima_carga.data_carga 
               		where obs.voided=0 and ((obs.concept_id=856) or obs.concept_id=1305)  and obs.location_id=:location 
               ) carga_viral
               inner join encounter_type et on et.encounter_type_id=carga_viral.encounter_type
               group by carga_viral.patient_id) ultima_cv on ultima_cv.patient_id=coorte12meses_final.patient_id and ultima_cv.data_carga =carga_viral.data_carga
                          LEFT JOIN           (	SELECT 
    p.patient_id, 
    MIN(e.encounter_datetime) AS data_seguimento2
FROM 
    patient p
INNER JOIN 
    encounter e ON e.patient_id = p.patient_id
WHERE 
    p.voided = 0 
    AND e.voided = 0 
    AND e.encounter_type IN (6, 9)
    AND e.location_id = :location  
    AND e.encounter_datetime BETWEEN :startDate AND :endDate
    AND e.patient_id IN (
        SELECT patient_id
        FROM encounter
        WHERE voided = 0
        AND encounter_type IN (6, 9)
        AND location_id = :location  
        AND encounter_datetime BETWEEN :startDate AND :endDate
        GROUP BY patient_id
        HAVING COUNT(*) >= 2
    )
    AND e.encounter_datetime > (
        SELECT MIN(e2.encounter_datetime)
        FROM encounter e2
        WHERE e2.patient_id = p.patient_id
          AND e2.voided = 0
          AND e2.encounter_type IN (6, 9)
          AND e2.location_id = :location  
          AND e2.encounter_datetime BETWEEN :startDate AND :endDate
    )
GROUP BY 
    p.patient_id
) min_consulta2  on coorte12meses_final.patient_id=min_consulta2.patient_id

left join (SELECT 
    p.patient_id, 
    MIN(e.encounter_datetime) AS data_seguimento3
FROM 
    patient p
INNER JOIN 
    encounter e ON e.patient_id = p.patient_id
WHERE 
    p.voided = 0 
    AND e.voided = 0 
    AND e.encounter_type IN (6, 9)
    AND e.location_id = :location  
    AND e.encounter_datetime BETWEEN :startDate AND :endDate
    AND e.patient_id IN (
        SELECT patient_id
        FROM encounter
        WHERE voided = 0
        AND encounter_type IN (6, 9)
        AND location_id = :location  
        AND encounter_datetime BETWEEN :startDate AND :endDate
        GROUP BY patient_id
        HAVING COUNT(*) >= 3
    )
    AND e.encounter_datetime > (
        SELECT MIN(e2.encounter_datetime)
        FROM encounter e2
        WHERE e2.patient_id = p.patient_id
          AND e2.voided = 0
          AND e2.encounter_type IN (6, 9)
          AND e2.location_id = :location  
          AND e2.encounter_datetime BETWEEN :startDate AND :endDate
    )
    AND e.encounter_datetime > (
        SELECT MIN(e3.encounter_datetime)
        FROM encounter e3
        WHERE e3.patient_id = p.patient_id
          AND e3.voided = 0
          AND e3.encounter_type IN (6, 9)
          AND e3.location_id = :location  
          AND e3.encounter_datetime BETWEEN :startDate AND :endDate
          AND e3.encounter_datetime > (
              SELECT MIN(e4.encounter_datetime)
              FROM encounter e4
              WHERE e4.patient_id = p.patient_id
                AND e4.voided = 0
                AND e4.encounter_type IN (6, 9)
                AND e4.location_id = :location  
                AND e4.encounter_datetime BETWEEN :startDate AND :endDate
          )
    )
GROUP BY 
    p.patient_id
) min_consulta3 on coorte12meses_final.patient_id=min_consulta3.patient_id

LEFT JOIN ( SELECT 
    ranked.patient_id, 
    ranked.encounter_datetime AS data_seguimento4
FROM (
    SELECT 
        e.patient_id,
        e.encounter_datetime,
        @row_number := IF(@prev_patient = e.patient_id, @row_number + 1, 1) AS row_num,
        @prev_patient := e.patient_id
    FROM 
        encounter e
    CROSS JOIN 
        (SELECT @row_number := 0, @prev_patient := NULL) AS vars
    WHERE 
        e.voided = 0
        AND e.encounter_type IN (6, 9)
        AND e.location_id = :location 
        AND e.encounter_datetime BETWEEN :startDate AND :endDate
    ORDER BY 
        e.patient_id, e.encounter_datetime
) AS ranked
WHERE 
    ranked.row_num = 4
) min_consulta4 on coorte12meses_final.patient_id=min_consulta4.patient_id

LEFT JOIN (SELECT 
    ranked.patient_id, 
    ranked.encounter_datetime AS data_seguimento5
FROM (
    SELECT 
        e.patient_id,
        e.encounter_datetime,
        @row_number := IF(@prev_patient = e.patient_id, @row_number + 1, 1) AS row_num,
        @prev_patient := e.patient_id
    FROM 
        encounter e
    CROSS JOIN 
        (SELECT @row_number := 0, @prev_patient := NULL) AS vars
    WHERE 
        e.voided = 0
        AND e.encounter_type IN (6, 9)
        AND e.location_id = :location 
        AND e.encounter_datetime BETWEEN :startDate AND :endDate
    ORDER BY 
        e.patient_id, e.encounter_datetime
) AS ranked
WHERE 
    ranked.row_num = 5
) min_consulta5 on coorte12meses_final.patient_id=min_consulta5.patient_id

LEFT JOIN (SELECT 
    ranked.patient_id, 
    ranked.encounter_datetime AS data_seguimento6
FROM (
    SELECT 
        e.patient_id,
        e.encounter_datetime,
        @row_number := IF(@prev_patient = e.patient_id, @row_number + 1, 1) AS row_num,
        @prev_patient := e.patient_id
    FROM 
        encounter e
    CROSS JOIN 
        (SELECT @row_number := 0, @prev_patient := NULL) AS vars
    WHERE 
        e.voided = 0
        AND e.encounter_type IN (6, 9)
        AND e.location_id = :location 
        AND e.encounter_datetime BETWEEN :startDate AND :endDate
    ORDER BY 
        e.patient_id, e.encounter_datetime
) AS ranked
WHERE 
    ranked.row_num = 6) min_consulta6 on coorte12meses_final.patient_id=min_consulta6.patient_id
    
    LEFT JOIN (
    select preg_or_lac.patient_id, preg_or_lac.data_consulta,preg_or_lac.orderF  from 
                           (
                          select final.patient_id,final.data_consulta,final.orderF 

                          from 
                          (
                           select p.patient_id,max(e.encounter_datetime) data_consulta, 1 orderF from patient p 
                           inner join encounter e on e.patient_id=p.patient_id
                           inner join obs o on o.encounter_id=e.encounter_id
                           where e.encounter_type in(9,6,53) and o.concept_id=1982 and o.value_coded=1065 and p.voided=0 and e.voided=0 and o.voided=0 
                           and e.encounter_datetime between :startDate and :endDate
                           group by p.patient_id

                           union

                           select p.patient_id,max(e.encounter_datetime) data_consulta,2 orderF from patient p 
                           inner join encounter e on e.patient_id=p.patient_id
                           inner join obs o on o.encounter_id=e.encounter_id
                           where e.encounter_type in(9,6,53) and o.concept_id=6332 and o.value_coded=1065 and p.voided=0 and e.voided=0 and o.voided=0 
                           and e.encounter_datetime   between :startDate and :endDate
                           group by p.patient_id

                          
                              )final
                             order by patient_id,data_consulta desc,orderF
                         )preg_or_lac inner join person pe on pe.person_id = preg_or_lac.patient_id where pe.gender = 'F'
                          group by preg_or_lac.patient_id) gravida_lactante on gravida_lactante.patient_id = coorte12meses_final.patient_id
                          
                          
                          LEFT JOIN (
							SELECT 
    p.patient_id,
    MIN(o.obs_datetime) AS segunda_data_carga,
    IF(o.value_numeric IS NULL OR o.value_numeric < 0, 'Indetectavel', o.value_numeric) AS valor_segunda_carga_cv
FROM 
    patient p
INNER JOIN 
    encounter e ON p.patient_id = e.patient_id 
INNER JOIN 
    obs o ON e.encounter_id = o.encounter_id 
WHERE 
    p.voided = 0 
    AND e.voided = 0 
    AND o.voided = 0 
    AND e.encounter_type IN (6, 9, 51,13) 
    AND o.concept_id IN (856, 1305) 
    AND o.obs_datetime BETWEEN :startDate AND :endDate 
    AND e.location_id = :location 
    AND o.obs_datetime > (
        SELECT 
            MIN(o2.obs_datetime)
        FROM 
            obs o2
        INNER JOIN 
            encounter e2 ON o2.encounter_id = e2.encounter_id 
        WHERE 
            o2.person_id = p.patient_id
            AND o2.voided = 0 
            AND e2.voided = 0 
            AND e2.encounter_type IN (6, 9, 51,13) 
            AND o2.concept_id IN (856, 1305) 
            AND o2.obs_datetime BETWEEN :startDate AND :endDate 
            AND e2.location_id = :location 
    )
GROUP BY 
    p.patient_id) data_segundo_resultado_cv on  data_segundo_resultado_cv.patient_id = coorte12meses_final.patient_id
    
    LEFT JOIN (
    SELECT 
    final.patient_id,
    final.valor_carga_fc,
    final.data_carga 
FROM (
    SELECT 
        carga_viral_fc.patient_id, 
        carga_viral_fc.Valor_carga_fc,
        carga_viral_fc.data_carga 
    FROM (
        SELECT 
            terceira_carga.patient_id,
            terceira_carga.data_carga,
            IF(obs.value_numeric IS NULL OR obs.value_numeric < 0, 'Indetectavel', obs.value_numeric) AS Valor_carga_fc, 
            terceira_carga.encounter_type
        FROM (
            SELECT 
                p.patient_id,
                MIN(o.obs_datetime) AS data_carga, 
                e.encounter_type
            FROM 
                patient p 
            INNER JOIN 
                encounter e ON p.patient_id = e.patient_id 
            INNER JOIN 
                obs o ON e.encounter_id = o.encounter_id 
            WHERE 
                p.voided = 0 
                AND e.voided = 0 
                AND o.voided = 0 
                AND e.encounter_type IN (6, 9, 51,13) 
                AND o.concept_id IN (856, 1305) 
                AND o.obs_datetime BETWEEN :startDate AND :endDate 
                AND o.obs_datetime > (
                    SELECT MIN(o2.obs_datetime)
                    FROM obs o2
                    INNER JOIN encounter e2 ON o2.encounter_id = e2.encounter_id
                    WHERE 
                        o2.person_id = p.patient_id
                        AND o2.voided = 0 
                        AND e2.voided = 0 
                        AND e2.encounter_type IN (6, 9, 51,13) 
                        AND o2.concept_id IN (856, 1305) 
                        AND o2.obs_datetime BETWEEN :startDate AND :endDate 
                        AND e2.location_id = :location 
                )
                AND o.obs_datetime > (
                    SELECT MIN(o3.obs_datetime)
                    FROM obs o3
                    INNER JOIN encounter e3 ON o3.encounter_id = e3.encounter_id
                    WHERE 
                        o3.person_id = p.patient_id
                        AND o3.voided = 0 
                        AND e3.voided = 0 
                        AND e3.encounter_type IN (6, 9, 51,13) 
                        AND o3.concept_id IN (856, 1305) 
                        AND o3.obs_datetime BETWEEN :startDate AND :endDate 
                        AND e3.location_id = :location 
                        AND o3.obs_datetime > (
                            SELECT MIN(o4.obs_datetime)
                            FROM obs o4
                            INNER JOIN encounter e4 ON o4.encounter_id = e4.encounter_id
                            WHERE 
                                o4.person_id = p.patient_id
                                AND o4.voided = 0 
                                AND e4.voided = 0 
                                AND e4.encounter_type IN (6, 9, 51,13) 
                                AND o4.concept_id IN (856, 1305) 
                                AND o4.obs_datetime BETWEEN :startDate AND :endDate 
                                AND e4.location_id = :location 
                        )
                )
            GROUP BY 
                p.patient_id
        ) terceira_carga 
        INNER JOIN 
            obs ON obs.person_id = terceira_carga.patient_id AND obs.obs_datetime = terceira_carga.data_carga 
        WHERE 
            obs.voided = 0 
            AND (obs.concept_id = 856 OR obs.concept_id = 1305) 
            AND obs.location_id = :location 
    ) carga_viral_fc
    INNER JOIN 
        encounter_type et ON et.encounter_type_id = carga_viral_fc.encounter_type
    GROUP BY 
        carga_viral_fc.patient_id
) final ) data_terceiro_resultado_cv on  data_terceiro_resultado_cv.patient_id = coorte12meses_final.patient_id

LEFT JOIN (  SELECT 
    p.patient_id,
    o.obs_datetime AS data_carga,
    IF(o.value_numeric IS NULL OR o.value_numeric < 0, 'Indetectavel', o.value_numeric) AS valor_carga_fc
FROM 
    patient p
INNER JOIN 
    encounter e ON p.patient_id = e.patient_id
INNER JOIN 
    obs o ON e.encounter_id = o.encounter_id
WHERE 
    p.voided = 0
    AND e.voided = 0
    AND o.voided = 0
    AND e.encounter_type IN (6, 9, 51)
    AND o.concept_id IN (856, 1305)
    AND o.obs_datetime BETWEEN :startDate AND :endDate
    AND p.patient_id IN (
        SELECT 
            sub.person_id
        FROM (
            SELECT 
                o2.person_id,
                o2.obs_datetime
            FROM 
                obs o2
            INNER JOIN 
                encounter e2 ON o2.encounter_id = e2.encounter_id
            WHERE 
                o2.voided = 0 
                AND e2.voided = 0 
                AND e2.encounter_type IN (6, 9, 51)
                AND o2.concept_id IN (856, 1305)
                AND o2.obs_datetime BETWEEN :startDate AND :endDate
                AND e2.location_id = :location 
            ORDER BY 
                o2.obs_datetime ASC
        ) AS sub
        GROUP BY 
            sub.person_id
        HAVING 
            COUNT(*) >= 4
    )
    AND o.obs_datetime > (
        SELECT MIN(o2.obs_datetime)
        FROM obs o2
        INNER JOIN encounter e2 ON o2.encounter_id = e2.encounter_id
        WHERE 
            o2.person_id = p.patient_id
            AND o2.voided = 0
            AND e2.voided = 0
            AND e2.encounter_type IN (6, 9, 51)
            AND o2.concept_id IN (856, 1305)
            AND o2.obs_datetime BETWEEN :startDate AND :endDate
    )
    AND o.obs_datetime > (
        SELECT MIN(o3.obs_datetime)
        FROM obs o3
        INNER JOIN encounter e3 ON o3.encounter_id = e3.encounter_id
        WHERE 
            o3.person_id = p.patient_id
            AND o3.voided = 0
            AND e3.voided = 0
            AND e3.encounter_type IN (6, 9, 51)
            AND o3.concept_id IN (856, 1305)
            AND o3.obs_datetime BETWEEN :startDate AND :endDate
            AND o3.obs_datetime > (
                SELECT MIN(o4.obs_datetime)
                FROM obs o4
                INNER JOIN encounter e4 ON o4.encounter_id = e4.encounter_id
                WHERE 
                    o4.person_id = p.patient_id
                    AND o4.voided = 0
                    AND e4.voided = 0
                    AND e4.encounter_type IN (6, 9, 51)
                    AND o4.concept_id IN (856, 1305)
                    AND o4.obs_datetime BETWEEN :startDate AND :endDate
            )
    )
    AND o.obs_datetime > (
        SELECT MIN(o5.obs_datetime)
        FROM obs o5
        INNER JOIN encounter e5 ON o5.encounter_id = e5.encounter_id
        WHERE 
            o5.person_id = p.patient_id
            AND o5.voided = 0
            AND e5.voided = 0
            AND e5.encounter_type IN (6, 9, 51)
            AND o5.concept_id IN (856, 1305)
            AND o5.obs_datetime BETWEEN :startDate AND :endDate
    )
GROUP BY 
    p.patient_id ) data_quarta_resultado_cv on data_quarta_resultado_cv.patient_id = coorte12meses_final.patient_id

				LEFT JOIN ( 
				SELECT p.patient_id, consulta_1.data_visita_1 data_1 FROM patient p
					LEFT JOIN ( SELECT e.patient_id,o.obs_datetime AS data_visita_1,e.encounter_id
					FROM encounter e INNER JOIN obs o ON e.encounter_id = o.encounter_id WHERE 
						e.voided = 0 AND o.voided = 0 AND e.encounter_type IN (6, 9)  AND o.concept_id = 23722  AND o.value_coded = 856 AND 
						o.obs_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location  ORDER BY o.obs_datetime ) consulta_1 
						ON consulta_1.patient_id = p.patient_id 
						AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e INNER JOIN  obs o ON e.encounter_id = o.encounter_id 
						WHERE  e.voided = 0  AND o.voided = 0 AND e.encounter_type IN (6, 9) AND e.patient_id = p.patient_id AND o.concept_id = 23722
						AND o.value_coded = 856 AND o.obs_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location 
						ORDER BY  o.obs_datetime LIMIT 0,1) WHERE consulta_1.data_visita_1 IS NOT NULL
							GROUP BY p.patient_id) pedido_visita_1 on pedido_visita_1.patient_id = coorte12meses_final.patient_id
				LEFT JOIN ( 
				SELECT p.patient_id, consulta_1.data_visita_1 data_1 FROM patient p
					LEFT JOIN ( SELECT e.patient_id,o.obs_datetime AS data_visita_1,e.encounter_id
					FROM encounter e INNER JOIN obs o ON e.encounter_id = o.encounter_id WHERE 
						e.voided = 0 AND o.voided = 0 AND e.encounter_type IN (6, 9)  AND o.concept_id = 23722  AND o.value_coded = 856 AND 
						o.obs_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location  ORDER BY o.obs_datetime ) consulta_1 
						ON consulta_1.patient_id = p.patient_id 
						AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e INNER JOIN  obs o ON e.encounter_id = o.encounter_id 
						WHERE  e.voided = 0  AND o.voided = 0 AND e.encounter_type IN (6, 9) AND e.patient_id = p.patient_id AND o.concept_id = 23722
						AND o.value_coded = 856 AND o.obs_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location 
						ORDER BY  o.obs_datetime LIMIT 1,1) WHERE consulta_1.data_visita_1 IS NOT NULL
							GROUP BY p.patient_id) pedido_visita_2 on pedido_visita_2.patient_id = coorte12meses_final.patient_id
					LEFT JOIN ( 
				SELECT p.patient_id, consulta_1.data_visita_1 data_1 FROM patient p
					LEFT JOIN ( SELECT e.patient_id,o.obs_datetime AS data_visita_1,e.encounter_id
					FROM encounter e INNER JOIN obs o ON e.encounter_id = o.encounter_id WHERE 
						e.voided = 0 AND o.voided = 0 AND e.encounter_type IN (6, 9)  AND o.concept_id = 23722  AND o.value_coded = 856 AND 
						o.obs_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location  ORDER BY o.obs_datetime ) consulta_1 
						ON consulta_1.patient_id = p.patient_id 
						AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e INNER JOIN  obs o ON e.encounter_id = o.encounter_id 
						WHERE  e.voided = 0  AND o.voided = 0 AND e.encounter_type IN (6, 9) AND e.patient_id = p.patient_id AND o.concept_id = 23722
						AND o.value_coded = 856 AND o.obs_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location 
						ORDER BY  o.obs_datetime LIMIT 2,1) WHERE consulta_1.data_visita_1 IS NOT NULL
							GROUP BY p.patient_id) pedido_visita_3 on pedido_visita_3.patient_id = coorte12meses_final.patient_id
							LEFT JOIN ( 
				SELECT p.patient_id, consulta_1.data_visita_1 data_1 FROM patient p
					LEFT JOIN ( SELECT e.patient_id,o.obs_datetime AS data_visita_1,e.encounter_id
					FROM encounter e INNER JOIN obs o ON e.encounter_id = o.encounter_id WHERE 
						e.voided = 0 AND o.voided = 0 AND e.encounter_type IN (6, 9)  AND o.concept_id = 23722  AND o.value_coded = 856 AND 
						o.obs_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location  ORDER BY o.obs_datetime ) consulta_1 
						ON consulta_1.patient_id = p.patient_id 
						AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e INNER JOIN  obs o ON e.encounter_id = o.encounter_id 
						WHERE  e.voided = 0  AND o.voided = 0 AND e.encounter_type IN (6, 9) AND e.patient_id = p.patient_id AND o.concept_id = 23722
						AND o.value_coded = 856 AND o.obs_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location 
						ORDER BY  o.obs_datetime LIMIT 3,1) WHERE consulta_1.data_visita_1 IS NOT NULL
							GROUP BY p.patient_id) pedido_visita_4 on pedido_visita_4.patient_id = coorte12meses_final.patient_id
								LEFT JOIN ( 
				SELECT p.patient_id, consulta_1.data_visita_1 data_1 FROM patient p
					LEFT JOIN ( SELECT e.patient_id,o.obs_datetime AS data_visita_1,e.encounter_id
					FROM encounter e INNER JOIN obs o ON e.encounter_id = o.encounter_id WHERE 
						e.voided = 0 AND o.voided = 0 AND e.encounter_type IN (6, 9)  AND o.concept_id = 23722  AND o.value_coded = 856 AND 
						o.obs_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location  ORDER BY o.obs_datetime ) consulta_1 
						ON consulta_1.patient_id = p.patient_id 
						AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e INNER JOIN  obs o ON e.encounter_id = o.encounter_id 
						WHERE  e.voided = 0  AND o.voided = 0 AND e.encounter_type IN (6, 9) AND e.patient_id = p.patient_id AND o.concept_id = 23722
						AND o.value_coded = 856 AND o.obs_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location 
						ORDER BY  o.obs_datetime LIMIT 4,1) WHERE consulta_1.data_visita_1 IS NOT NULL
							GROUP BY p.patient_id) pedido_visita_5 on pedido_visita_5.patient_id = coorte12meses_final.patient_id
                    
                    								LEFT JOIN ( 
				SELECT p.patient_id, consulta_1.data_visita_1 data_1 FROM patient p
					LEFT JOIN ( SELECT e.patient_id,o.obs_datetime AS data_visita_1,e.encounter_id
					FROM encounter e INNER JOIN obs o ON e.encounter_id = o.encounter_id WHERE 
						e.voided = 0 AND o.voided = 0 AND e.encounter_type IN (6, 9)  AND o.concept_id = 23722  AND o.value_coded = 856 AND 
						o.obs_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location  ORDER BY o.obs_datetime ) consulta_1 
						ON consulta_1.patient_id = p.patient_id 
						AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e INNER JOIN  obs o ON e.encounter_id = o.encounter_id 
						WHERE  e.voided = 0  AND o.voided = 0 AND e.encounter_type IN (6, 9) AND e.patient_id = p.patient_id AND o.concept_id = 23722
						AND o.value_coded = 856 AND o.obs_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location 
						ORDER BY  o.obs_datetime LIMIT 5,1) WHERE consulta_1.data_visita_1 IS NOT NULL
							GROUP BY p.patient_id) pedido_visita_6 on pedido_visita_6.patient_id = coorte12meses_final.patient_id
                    
                    LEFT JOIN (       SELECT p.patient_id, consulta_1.data_visita_1 data_amostra_1 FROM patient p
							LEFT JOIN ( SELECT e.patient_id,o.value_datetime AS data_visita_1,e.encounter_id
							FROM encounter e INNER JOIN obs o ON e.encounter_id = o.encounter_id WHERE 
								e.voided = 0 AND o.voided = 0 AND e.encounter_type IN (13, 51)  AND o.concept_id = 23821  AND 
								o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location  ORDER BY o.obs_datetime ) consulta_1 
								ON consulta_1.patient_id = p.patient_id 
								AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e INNER JOIN  obs o ON e.encounter_id = o.encounter_id 
								WHERE  e.voided = 0  AND o.voided = 0 AND e.encounter_type IN (13, 51) AND e.patient_id = p.patient_id AND o.concept_id = 23821
								AND o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location 
								ORDER BY  o.obs_datetime LIMIT 0,1) WHERE consulta_1.data_visita_1 IS NOT NULL
								GROUP BY p.patient_id    ) data_amostra_1 on data_amostra_1.patient_id = coorte12meses_final.patient_id
                                
                                  LEFT JOIN (       SELECT p.patient_id, consulta_1.data_visita_1 data_amostra_1 FROM patient p
							LEFT JOIN ( SELECT e.patient_id,o.value_datetime AS data_visita_1,e.encounter_id
							FROM encounter e INNER JOIN obs o ON e.encounter_id = o.encounter_id WHERE 
								e.voided = 0 AND o.voided = 0 AND e.encounter_type IN (13, 51)  AND o.concept_id = 23821  AND 
								o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location  ORDER BY o.obs_datetime ) consulta_1 
								ON consulta_1.patient_id = p.patient_id 
								AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e INNER JOIN  obs o ON e.encounter_id = o.encounter_id 
								WHERE  e.voided = 0  AND o.voided = 0 AND e.encounter_type IN (13, 51) AND e.patient_id = p.patient_id AND o.concept_id = 23821
								AND o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location 
								ORDER BY  o.obs_datetime LIMIT 1,1) WHERE consulta_1.data_visita_1 IS NOT NULL
								GROUP BY p.patient_id    ) data_amostra_2 on data_amostra_2.patient_id = coorte12meses_final.patient_id
                                
                                  LEFT JOIN (       SELECT p.patient_id, consulta_1.data_visita_1 data_amostra_1 FROM patient p
							LEFT JOIN ( SELECT e.patient_id,o.value_datetime AS data_visita_1,e.encounter_id
							FROM encounter e INNER JOIN obs o ON e.encounter_id = o.encounter_id WHERE 
								e.voided = 0 AND o.voided = 0 AND e.encounter_type IN (13, 51)  AND o.concept_id = 23821  AND 
								o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location  ORDER BY o.obs_datetime ) consulta_1 
								ON consulta_1.patient_id = p.patient_id 
								AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e INNER JOIN  obs o ON e.encounter_id = o.encounter_id 
								WHERE  e.voided = 0  AND o.voided = 0 AND e.encounter_type IN (13, 51) AND e.patient_id = p.patient_id AND o.concept_id = 23821
								AND o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location 
								ORDER BY  o.obs_datetime LIMIT 2,1) WHERE consulta_1.data_visita_1 IS NOT NULL
								GROUP BY p.patient_id    ) data_amostra_3 on data_amostra_3.patient_id = coorte12meses_final.patient_id
                                
                                  LEFT JOIN (       SELECT p.patient_id, consulta_1.data_visita_1 data_amostra_1 FROM patient p
							LEFT JOIN ( SELECT e.patient_id,o.value_datetime AS data_visita_1,e.encounter_id
							FROM encounter e INNER JOIN obs o ON e.encounter_id = o.encounter_id WHERE 
								e.voided = 0 AND o.voided = 0 AND e.encounter_type IN (13, 51)  AND o.concept_id = 23821  AND 
								o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location  ORDER BY o.obs_datetime ) consulta_1 
								ON consulta_1.patient_id = p.patient_id 
								AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e INNER JOIN  obs o ON e.encounter_id = o.encounter_id 
								WHERE  e.voided = 0  AND o.voided = 0 AND e.encounter_type IN (13, 51) AND e.patient_id = p.patient_id AND o.concept_id = 23821
								AND o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location 
								ORDER BY  o.obs_datetime LIMIT 3,1) WHERE consulta_1.data_visita_1 IS NOT NULL
								GROUP BY p.patient_id    ) data_amostra_4 on data_amostra_4.patient_id = coorte12meses_final.patient_id
                                
                                  LEFT JOIN (       SELECT p.patient_id, consulta_1.data_visita_1 data_amostra_1 FROM patient p
							LEFT JOIN ( SELECT e.patient_id,o.value_datetime AS data_visita_1,e.encounter_id
							FROM encounter e INNER JOIN obs o ON e.encounter_id = o.encounter_id WHERE 
								e.voided = 0 AND o.voided = 0 AND e.encounter_type IN (13, 51)  AND o.concept_id = 23821  AND 
								o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location  ORDER BY o.obs_datetime ) consulta_1 
								ON consulta_1.patient_id = p.patient_id 
								AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e INNER JOIN  obs o ON e.encounter_id = o.encounter_id 
								WHERE  e.voided = 0  AND o.voided = 0 AND e.encounter_type IN (13, 51) AND e.patient_id = p.patient_id AND o.concept_id = 23821
								AND o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location 
								ORDER BY  o.obs_datetime LIMIT 4,1) WHERE consulta_1.data_visita_1 IS NOT NULL
								GROUP BY p.patient_id    ) data_amostra_5 on data_amostra_5.patient_id = coorte12meses_final.patient_id
                                
                                  LEFT JOIN (       SELECT p.patient_id, consulta_1.data_visita_1 data_amostra_1 FROM patient p
							LEFT JOIN ( SELECT e.patient_id,o.value_datetime AS data_visita_1,e.encounter_id
							FROM encounter e INNER JOIN obs o ON e.encounter_id = o.encounter_id WHERE 
								e.voided = 0 AND o.voided = 0 AND e.encounter_type IN (13, 51)  AND o.concept_id = 23821  AND 
								o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location  ORDER BY o.obs_datetime ) consulta_1 
								ON consulta_1.patient_id = p.patient_id 
								AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e INNER JOIN  obs o ON e.encounter_id = o.encounter_id 
								WHERE  e.voided = 0  AND o.voided = 0 AND e.encounter_type IN (13, 51) AND e.patient_id = p.patient_id AND o.concept_id = 23821
								AND o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id = :location 
								ORDER BY  o.obs_datetime LIMIT 5,1) WHERE consulta_1.data_visita_1 IS NOT NULL
								GROUP BY p.patient_id    ) data_amostra_6 on data_amostra_6.patient_id = coorte12meses_final.patient_id
                          LEFT JOIN (   Select segundaLinha.patient_id, dataLinha  from ( 
                  Select enc.patient_id, enc.encounter_datetime ultimaConsulta, max(obsLinha.obs_datetime) dataLinha from ( 
                  Select p.patient_id, max(e.encounter_datetime) encounter_datetime from patient p 
                  inner join encounter e on p.patient_id=e.patient_id 
                  where p.voided = 0 and e.voided = 0 and e.encounter_type = 6   and 
                  e.location_id = :location  
                  group by p.patient_id 
                  ) enc 
                  inner join encounter e on e.patient_id = enc.patient_id 
                  inner join obs obsLinha on obsLinha.encounter_id = e.encounter_id 
                  where obsLinha.concept_id = 21187 and e.encounter_type in (53) 
                  and obsLinha.voided = 0 and e.voided = 0 and obsLinha.obs_datetime BETWEEN :startDate AND :endDate and e.location_id = :location  
                  group by enc.patient_id 
                  ) segundaLinha
                  union
                 
				Select 	p.patient_id,max(o.obs_datetime) dataLinha
					from 	patient p
							inner join encounter e on p.patient_id=e.patient_id
							inner join obs o on e.encounter_id=o.encounter_id
					where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (9,6) 
					and o.obs_datetime BETWEEN :startDate AND :endDateand 
							o.concept_id = 21151 and o.value_coded=21148  and e.location_id=:location 
					group by p.patient_id) segunda on segunda.patient_id = coorte12meses_final.patient_id    
			
            LEFT JOIN (				
              select os.value_coded motivo_saida, segunda_linha.patient_id from
              (Select 	p.patient_id,max(o.obs_datetime) dataLinha
					from 	patient p
							inner join encounter e on p.patient_id=e.patient_id
							inner join obs o on e.encounter_id=o.encounter_id
					where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (9,6) 
					and o.obs_datetime BETWEEN :startDate AND :endDateand 
							o.concept_id = 21151 and o.value_coded=21148  and e.location_id=:location 
					group by p.patient_id) segunda_linha
                   inner join obs os on os.person_id=segunda_linha.patient_id and os.concept_id=1792
                   and segunda_linha.dataLinha = os.obs_datetime ) motivo on motivo.patient_id = coorte12meses_final.patient_id 
                    
			LEFT JOIN (
            SELECT p.patient_id, consulta_1.encounter_datetime data_1,consulta_1.encounter_id FROM patient p
				LEFT JOIN (SELECT * FROM encounter e WHERE e.voided = 0 AND e.encounter_datetime between :startDate AND  :endDate AND
						e.location_id=:location AND e.encounter_type =35 ORDER BY e.encounter_datetime ASC) consulta_1 
						ON consulta_1.patient_id = p.patient_id 
						AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e WHERE e.voided = 0 AND e.patient_id = p.patient_id
						AND e.encounter_datetime between :startDate AND  :endDate AND e.location_id=:location  AND e.encounter_type =35 
						ORDER BY e.encounter_datetime ASC LIMIT 0,1)
						GROUP BY p.patient_id
            ) apss_visita_1 on apss_visita_1.patient_id = coorte12meses_final.patient_id
          			
                    LEFT JOIN (
            SELECT p.patient_id, consulta_1.encounter_datetime data_1,consulta_1.encounter_id FROM patient p
				LEFT JOIN (SELECT * FROM encounter e WHERE e.voided = 0 AND e.encounter_datetime between :startDate AND  :endDate AND
						e.location_id=:location AND e.encounter_type =35 ORDER BY e.encounter_datetime ASC) consulta_1 
						ON consulta_1.patient_id = p.patient_id 
						AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e WHERE e.voided = 0 AND e.patient_id = p.patient_id
						AND e.encounter_datetime between :startDate AND  :endDate AND e.location_id=:location  AND e.encounter_type =35 
						ORDER BY e.encounter_datetime ASC LIMIT 1,1)
						GROUP BY p.patient_id
            ) apss_visita_2 on apss_visita_2.patient_id = coorte12meses_final.patient_id
            
            			LEFT JOIN (
            SELECT p.patient_id, consulta_1.encounter_datetime data_1,consulta_1.encounter_id FROM patient p
				LEFT JOIN (SELECT * FROM encounter e WHERE e.voided = 0 AND e.encounter_datetime between :startDate AND  :endDate AND
						e.location_id=:location AND e.encounter_type =35 ORDER BY e.encounter_datetime ASC) consulta_1 
						ON consulta_1.patient_id = p.patient_id 
						AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e WHERE e.voided = 0 AND e.patient_id = p.patient_id
						AND e.encounter_datetime between :startDate AND  :endDate AND e.location_id=:location  AND e.encounter_type =35 
						ORDER BY e.encounter_datetime ASC LIMIT 2,1)
						GROUP BY p.patient_id
            ) apss_visita_3 on apss_visita_3.patient_id = coorte12meses_final.patient_id
            
            			LEFT JOIN (
            SELECT p.patient_id, consulta_1.encounter_datetime data_1,consulta_1.encounter_id FROM patient p
				LEFT JOIN (SELECT * FROM encounter e WHERE e.voided = 0 AND e.encounter_datetime between :startDate AND  :endDate AND
						e.location_id=:location AND e.encounter_type =35 ORDER BY e.encounter_datetime ASC) consulta_1 
						ON consulta_1.patient_id = p.patient_id 
						AND consulta_1.encounter_id = (SELECT e.encounter_id FROM encounter e WHERE e.voided = 0 AND e.patient_id = p.patient_id
						AND e.encounter_datetime between :startDate AND  :endDate AND e.location_id=:location  AND e.encounter_type =35 
						ORDER BY e.encounter_datetime ASC LIMIT 3,1)
						GROUP BY p.patient_id
            ) apss_visita_4 on apss_visita_4.patient_id = coorte12meses_final.patient_id
            
            LEFT JOIN (
            select patient_id, l.value_reference from encounter e inner join location_attribute l on e.location_id = l.location_id  
					where e.location_id = :location and l.attribute_type_id =1 and e.voided = 0  group by patient_id
            ) codigo_us on codigo_us.patient_id = coorte12meses_final.patient_id

            
                          where (data_estado is null or (data_estado is not null and  data_fila>data_estado)) and date_add(data_usar, interval 28 day) >= :endDate  
                      group by coorte12meses_final.patient_id ) final
              
              cross join(SELECT@cnt := 0)demmy

