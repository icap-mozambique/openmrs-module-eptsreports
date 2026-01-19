   select coorte12meses_final.patient_id,
                             		coorte12meses_final.data_inicio,
                          		pad3.county_district as 'Distrito',
                          		pad3.address2 as 'PAdministrativo',
                          		pad3.address6 as 'Localidade',
                          		pad3.address5 as 'Bairro',
                          		pad3.address1 as 'PontoReferencia',
                          		concat(ifnull(pn.given_name,''),' ',ifnull(pn.middle_name,''),' ',ifnull(pn.family_name,'')) as 'NomeCompleto',
                          		pid.identifier as NID,
                          		p.gender,
                          		 p.birthdate data_nascimento,
                                DATE_FORMAT(p.birthdate, '%d/%m/%Y') data_nascimentos,
                          		coorte12meses_final.data_usar as proximo_marcado,
                                DATE_FORMAT(coorte12meses_final.data_usar, '%d/%m/%Y') proximo_marcados,
                          		regime.ultimo_regime,
                          		regime.data_regime,
				DATE_FORMAT(regime.data_regime, '%d/%m/%Y') data_regimes,
                          		coorte12meses_final.data_fila,
                                DATE_FORMAT(coorte12meses_final.data_fila, '%d/%m/%Y') data_fila_,
                          		coorte12meses_final.data_proximo_lev,
                                 DATE_FORMAT(coorte12meses_final.data_proximo_lev, '%d/%m/%Y') data_proximo_levs,
                                origem.referred_from,
                                diferenca.duracao,
				diferenca.data_filas,
				diferenca.code,
                                diferenca.proximo_levantamento,
                                DATE_FORMAT(max_consulta.data_seguimento, '%d/%m/%Y') data_seguimentos,
                                max_consulta.data_seguimento,
                                obs_seguimento.value_datetime data_proxima_consulta,
                                DATE_FORMAT(obs_seguimento.value_datetime, '%d/%m/%Y') data_prox_consult,
                                ultima_cv.data_carga, 
								DATE_FORMAT(ultima_cv.data_carga, '%d/%m/%Y') ult_carg,
                                case (saidaFC.value_coded)
 	when 1707 then 'ABANDONO'
 	when 1366 then 'OBITO'
 	when 1706 then 'TRANSFERIDO PARA'
 	when 1709 then 'SUSPENDEU'
 	else null end as estado,
    saidaFC.data_saida,
    DATE_FORMAT(saidaFC.data_saida, '%d/%m/%Y') data_saidas,
    fila_quantidade.numero,
    fila_dosagem.dosagem,
    case(tipo_dispensa_fora.value_coded)
    when 1098 then 'DM'
 	when 23720 then 'DT'
 	when 23888 then 'DS'
 	else null end as estado_dt,
    ficha_clinica.data_inicio ficha_data_inicio,
    DATE_FORMAT(ficha_clinica.data_inicio, '%d/%m/%Y') ficha_data_inicios,
    max_recepcao_ficha_recepcao.data_recepcao_levantou_ficha recepcao,
              case(saidaPR.estado)
 	when 9 then 'ABANDONO'
 	when 10 then 'OBITO'
 	when 7 then 'TRANSFERIDO PARA'
 	when 8 then 'SUSPENDEU'
 	else null end as estadoPR,
    case (saidaFRM.value_coded)
 	when 1707 then 'ABANDONO'
 	when 1366 then 'OBITO'
 	when 1706 then 'TRANSFERIDO PARA'
 	when 1709 then 'SUSPENDEU'
 	else null end as estadoFRM,
    saidaFRM.datas_saida FRM_data_saida,
    ultima_cv_fc.data_carga data_carga_fc,
    max_filaFinal_Inclusao.data_filas_rev
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
													and o.concept_id=1255 and o.value_coded=1256 and e.encounter_datetime<=:endDate  and e.location_id=:location 
													group by p.patient_id 
											  union 
											  
											  select p.patient_id, min(value_datetime) data_inicio 
											  from patient p 
													inner join encounter e on p.patient_id=e.patient_id 
													inner join obs o on e.encounter_id=o.encounter_id 
											  where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) 
													and o.concept_id=1190 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location 
													group by p.patient_id 
											  
											  union 
								   
											  select pg.patient_id, min(date_enrolled) data_inicio 
											  from patient p 
													inner join patient_program pg on p.patient_id=pg.patient_id 
											  where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location 
													group by pg.patient_id 
											  
											  union 
							
											  select e.patient_id, min(e.encounter_datetime) as data_inicio 
											  from patient p 
													inner join encounter e on p.patient_id=e.patient_id 
											  where p.voided=0 and e.encounter_type=18 and e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location 
													group by p.patient_id 
											  
											  union 
								   
											  select p.patient_id, min(value_datetime) data_inicio 
											  from patient p 
													inner join encounter e on p.patient_id=e.patient_id 
													inner join obs o on e.encounter_id=o.encounter_id 
											  where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 
													and o.concept_id=23866 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location 
													group by p.patient_id
										) 
								  art_start group by patient_id 
							) tx_new where data_inicio <= :endDate and data_inicio < '2023-12-21'
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
														  where p.voided=0 and e.encounter_type=18 and e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location 
																group by p.patient_id 
														  
														  union 
											   
														  select p.patient_id, min(value_datetime) data_inicio 
														  from patient p 
																inner join encounter e on p.patient_id=e.patient_id 
																inner join obs o on e.encounter_id=o.encounter_id 
														  where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 
																and o.concept_id=23866 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location 
																group by p.patient_id
													) 
											  art_start group by patient_id 
										) tx_new where data_inicio <= :endDate and data_inicio >= '2023-12-21'
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
																and o.concept_id=1255 and o.value_coded=1256 and e.encounter_datetime<=:endDate and e.location_id=:location 
																group by p.patient_id 
														  union 
														  
														  select p.patient_id, min(value_datetime) data_inicio 
														  from patient p 
																inner join encounter e on p.patient_id=e.patient_id 
																inner join obs o on e.encounter_id=o.encounter_id 
														  where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) 
																and o.concept_id=1190 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location 
																group by p.patient_id 
														  
														  union 
											   
														  select pg.patient_id, min(date_enrolled) data_inicio 
														  from patient p 
																inner join patient_program pg on p.patient_id=pg.patient_id 
														  where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location 
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
                                     and ps.start_date<= :endDate and pg.location_id =:location group by pg.patient_id                                           
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
															and ps.start_date<= :endDate and pg.location_id =:location group by pg.patient_id                                           
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
												and o.obs_datetime<=:endDate and e.location_id=:location                                                                        
												group by p.patient_id                                                                                                               
											union                                                                                                                               
											select person_id as patient_id,death_date as data_estado                                                                            
											from person                                                                                                                         
											where dead=1 and voided = 0 and death_date is not null and death_date<=:endDate 
											union                                                                                                                               
											select  p.patient_id,                                                                                                               
												max(obsObito.obs_datetime) data_estado                                                                                      
											from patient p                                                                                                                   
												inner join encounter e on p.patient_id=e.patient_id                                                                         
												inner join obs obsObito on e.encounter_id=obsObito.encounter_id                                                             
											where e.voided=0 and p.voided=0 and obsObito.voided=0                                                        
												and e.encounter_type in (21,36,37) and  e.encounter_datetime<=:endDate and  e.location_id=:location                          
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
														and e.location_id=:location and e.encounter_datetime<=:endDate                                                                                  
														group by p.patient_id  
												union
												
												select  p.patient_id,max(encounter_datetime) data_encountro                                                                                    
												from patient p                                                                                                                                   
													inner join encounter e on e.patient_id=p.patient_id                                                                                         
												where   p.voided=0 and e.voided=0 and e.encounter_type in (6,9)                                                                
													and e.location_id=:location and e.encounter_datetime<=:endDate                                                                                  
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
                                     o.obs_datetime<=:endDate and e.location_id=:location                                                                        
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
				                                 		and ps.start_date<= :endDate and pg.location_id =:location group by pg.patient_id                                           
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
			                                     	and o.obs_datetime<=:endDate and e.location_id=:location                                                                        
			                             			group by p.patient_id                                                                                                               
			                             		
			                             	     union                                                                                                                               
				                             
				                             	select ultimaBusca.patient_id, ultimaBusca.data_estado                                                                              
				                             	from (                                                                                                                              
				                                     select p.patient_id,max(e.encounter_datetime) data_estado                                                                   
				                                     from patient p                                                                                                              
				                                         inner join encounter e on p.patient_id=e.patient_id                                                                     
				                                         inner join obs o on o.encounter_id=e.encounter_id                                                                       
				                                     where e.voided=0 and p.voided=0 and e.encounter_datetime<= :endDate                                       
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
											and e.location_id=:location and e.encounter_datetime <= :endDate                                                                               
											group by p.patient_id 
		                        
		                        		union
		                        
			                        	select p.patient_id, date_add(max(value_datetime), interval 31 day) data_ultimo_levantamento                                                                                     
			                        	from patient p                                                                                                                                   
			                         	  inner join encounter e on p.patient_id=e.patient_id                                                                                         
			                              inner join obs o on e.encounter_id=o.encounter_id                                                                                           
			                        	where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52                                                       
			                              and o.concept_id=23866 and o.value_datetime is not null and e.location_id=:location and o.value_datetime <= :endDate                                                                                        
			                        	group by p.patient_id
				                    	) ultimo_levantamento group by patient_id
			                		) ultimo_levantamento on saidas_por_transferencia.patient_id = ultimo_levantamento.patient_id 
			               		where ultimo_levantamento.data_ultimo_levantamento <= :endDate 
				                                                                                                                                                                         
                         	) allSaida                                                                                                                                      
                 				group by patient_id 
                
              		) saida on inicio.patient_id = saida.patient_id                                                                                                      
             left join                                                                                                                                           
              ( select p.patient_id,max(encounter_datetime) data_fila                                                                                                
             from    patient p                                                                                                                                   
                     inner join encounter e on e.patient_id=p.patient_id                                                                                         
             where   p.voided=0 and e.voided=0 and e.encounter_type=18 and                                                                     
                     e.location_id=:location and e.encounter_datetime<=:endDate                                                                                  
             group by p.patient_id                                                                                                                               
             ) max_fila on inicio.patient_id=max_fila.patient_id  
              left join                                                                                                                                          
              (                                                                                                                                                  
             select  p.patient_id,max(value_datetime) data_recepcao_levantou                                                                                     
             from    patient p                                                                                                                                   
                     inner join encounter e on p.patient_id=e.patient_id                                                                                         
                     inner join obs o on e.encounter_id=o.encounter_id                                                                                           
             where   p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and                                                      
                     o.concept_id=23866 and o.value_datetime is not null and                                                                                     
                     o.value_datetime<=:endDate and e.location_id=:location                                                                                      
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
                          	where 	pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:evaluationDate  and location_id=:location
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
                          						encounter_datetime <=:evaluationDate  and e.location_id=:location and p.voided=0
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
                          							e.encounter_type in (6,46,35) and e.encounter_datetime<=:evaluationDate  and 
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
                          				where 	person_attribute_type_id=24 and value is not null and value<>'' and voided=0 and date(date_created)<=:evaluationDate 
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
                                     o.obs_datetime<=:evaluationDate and e.location_id=:location group by p.patient_id ) origem on origem.patient_id=coorte12meses_final.patient_id
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
e.location_id=:location and e.encounter_datetime  <=:evaluationDate  
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
                          						encounter_datetime <=:evaluationDate  and e.location_id=:location and p.voided=0
                          				group by patient_id
                          			) ultimo_lev
                          	where 	o.person_id=ultimo_lev.patient_id and o.obs_datetime=ultimo_lev.encounter_datetime and o.voided=0 and 
                          			o.concept_id in (1087) and o.location_id=:location
                                    group by o.person_id ) consulta on fila.patient_id = consulta.patient_id AND fila.data_filas >= consulta.data_regime )
                                    diferenca on diferenca.patient_id = coorte12meses_final.patient_id
                                    LEFT JOIN
                                     (	Select 	p.patient_id,max(encounter_datetime) data_seguimento
                          	from 	patient p 
                          			inner join encounter e on e.patient_id=p.patient_id
                          	where 	p.voided=0 and e.voided=0 and e.encounter_type in (6,9) and 
                          			e.location_id=:location and e.encounter_datetime<=:evaluationDate 
                          	group by p.patient_id
                          ) max_consulta on coorte12meses_final.patient_id=max_consulta.patient_id
                                                    	 left join
                          	obs obs_seguimento on obs_seguimento.person_id=coorte12meses_final.patient_id
                          	and obs_seguimento.voided=0
                          	and obs_seguimento.obs_datetime=max_consulta.data_seguimento
                          	and obs_seguimento.concept_id=1410
                          	and obs_seguimento.location_id=:location
                                
							left join
                            (
               Select p.patient_id,max(o.obs_datetime) data_carga
               from patient p
                            inner join encounter e on p.patient_id=e.patient_id
                            inner join obs o on e.encounter_id=o.encounter_id
                       where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9,53,13,51) and  
               o.concept_id in (856,1305) and  o.obs_datetime between date_add(date_add(:evaluationDate , interval -12 MONTH), interval 1 day) and :evaluationDate  and
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
               			Select p.patient_id,max(o.obs_datetime) data_carga, e.encounter_type
               			from 	patient p 
               					inner join encounter e on p.patient_id=e.patient_id 
               					inner join obs o on e.encounter_id=o.encounter_id 
               			where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (13,51,6,9) and  o.concept_id in (856,1305) and  
               					o.obs_datetime between date_add(date_add(:evaluationDate , interval -12 MONTH), interval 1 day) and :evaluationDate  and e.location_id=:location
               			group by p.patient_id
               		) ultima_carga 
               			inner join obs on obs.person_id=ultima_carga.patient_id and obs.obs_datetime=ultima_carga.data_carga 
               		where obs.voided=0 and ((obs.concept_id=856) or obs.concept_id=1305)  and obs.location_id=:location
               ) carga_viral
               inner join encounter_type et on et.encounter_type_id=carga_viral.encounter_type
               group by carga_viral.patient_id) ultima_cv on ultima_cv.patient_id=coorte12meses_final.patient_id and ultima_cv.data_carga =carga_viral.data_carga
            						LEFT JOIN (
					Select 	p.patient_id,max(o.obs_datetime) data_saida, o.value_coded 
									from 	patient p
											inner join encounter e on p.patient_id=e.patient_id
											inner join obs o on e.encounter_id=o.encounter_id
									where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type = 6 and 
											o.concept_id = 6273 and o.value_coded in (1707, 1366, 1706, 1709) and
											o.obs_datetime <=:evaluationDate  and e.location_id=:location
									group by p.patient_id

					) saidaFC on saidaFC.patient_id = coorte12meses_final.patient_id
                    left join
                    (select maxx_fila.patient_id, obs.value_numeric numero from
                   (SELECT 	e.patient_id, MAX(o.obs_datetime) AS max_date
                            						  FROM 		patient p
                            									inner join encounter e on p.patient_id=e.patient_id
                                                               inner join obs o on o.encounter_id = e.encounter_id
                            						  WHERE		p.voided=0 and e.encounter_type=18 AND e.voided=0   AND
                                                      o.voided=0 and e.encounter_datetime<=:evaluationDate  and e.location_id=:location 
                            						  GROUP BY 	p.patient_id) maxx_fila
                                                      
                                                      inner join obs on obs.person_id=maxx_fila.patient_id  and obs.obs_datetime=maxx_fila.max_date
               		where obs.voided=0 and   obs.concept_id=1715    and obs.location_id=:location) fila_quantidade on  fila_quantidade.patient_id = coorte12meses_final.patient_id
 
  left join
                    (select maxx_fila.patient_id, obs.value_text dosagem from
                   (SELECT 	e.patient_id, MAX(o.obs_datetime) AS max_date
                            						  FROM 		patient p
                            									inner join encounter e on p.patient_id=e.patient_id
                                                               inner join obs o on o.encounter_id = e.encounter_id
                            						  WHERE		p.voided=0 and e.encounter_type=18 AND e.voided=0   AND
                                                      o.voided=0 and e.encounter_datetime<=:evaluationDate  and e.location_id=:location 
                            						  GROUP BY 	p.patient_id) maxx_fila
                                                      
                                                      inner join obs on obs.person_id=maxx_fila.patient_id  and obs.obs_datetime=maxx_fila.max_date
               		where obs.voided=0 and   obs.concept_id=1711 and (obs.value_text like '%Tomar%' or obs.value_text like '%-%')   and obs.location_id=:location) fila_dosagem on  fila_dosagem.patient_id = coorte12meses_final.patient_id
					left join 
                    ( select tipo_dispensa.patient_id, obs.value_coded from
                   (SELECT 	e.patient_id, MAX(e.encounter_datetime) AS max_date
                            						  FROM 		patient p
                            									inner join encounter e on p.patient_id=e.patient_id
                                                               inner join obs o on o.encounter_id = e.encounter_id
                            						  WHERE		p.voided=0 and e.encounter_type=6 AND e.voided=0  and
                                                      o.voided=0 and e.encounter_datetime<=:evaluationDate  and e.location_id=:location
                            						  GROUP BY 	p.patient_id) tipo_dispensa
                                                      
                                                      
                                                      inner join obs on obs.person_id=tipo_dispensa.patient_id  and obs.obs_datetime=tipo_dispensa.max_date
               		where obs.voided=0 and   obs.concept_id=23739   and obs.location_id=:location) tipo_dispensa_fora on tipo_dispensa_fora.patient_id = coorte12meses_final.patient_id
                    
                    left join 
                    
                    			(								  select p.patient_id, min(e.encounter_datetime) data_inicio 
											  from patient p 
													inner join encounter e on p.patient_id=e.patient_id 
													inner join obs o on o.encounter_id=e.encounter_id 
											  where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (6,9,53) 
													and o.concept_id=1255 and o.value_coded=1256 and e.encounter_datetime<=:evaluationDate and e.location_id=:location 
													group by p.patient_id ) ficha_clinica on ficha_clinica.patient_id = coorte12meses_final.patient_id 
                                                    
                                                    
						left join (             
									select  p.patient_id,max(value_datetime) data_recepcao_levantou_ficha                                                                                     
										 from    patient p                                                                                                                                   
												 inner join encounter e on p.patient_id=e.patient_id                                                                                         
												 inner join obs o on e.encounter_id=o.encounter_id                                                                                           
										 where   p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and                                                      
												 o.concept_id=23866 and o.value_datetime is not null and                                                                                     
												 o.value_datetime<=:evaluationDate and e.location_id=:location                                                                                      
										 group by p.patient_id ) max_recepcao_ficha_recepcao on max_recepcao_ficha_recepcao.patient_id = coorte12meses_final.patient_id                                                                                                                               
                                						LEFT JOIN (
					Select 	p.patient_id,max(o.obs_datetime) datas_saida, o.value_coded 
									from 	patient p
											inner join encounter e on p.patient_id=e.patient_id
											inner join obs o on e.encounter_id=o.encounter_id
									where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (53) and   e.encounter_datetime<=:evaluationDate and 
											o.concept_id = 6272 and o.value_coded in (1707, 1366, 1706, 1709) and e.location_id=:location
									group by p.patient_id

					) saidaFRM on saidaFRM.patient_id = coorte12meses_final.patient_id
			LEFT JOIN (
								select 	pg.patient_id,ps.state estado
								from 	patient p 
										inner join patient_program pg on p.patient_id=pg.patient_id
										inner join patient_state ps on pg.patient_program_id=ps.patient_program_id
								where 	pg.voided=0 and ps.voided=0 and p.voided=0 and 
										pg.program_id=2 and ps.state in (7,8,9,10) and ps.end_date is null and location_id=:location and 
										ps.start_date<= :evaluationDate 
					)saidaPR on saidaPR.patient_id = coorte12meses_final.patient_id
                    
                    	left join
                            (
               Select p.patient_id,max(o.obs_datetime) data_carga
               from patient p
                            inner join encounter e on p.patient_id=e.patient_id
                            inner join obs o on e.encounter_id=o.encounter_id
                       where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9) and  
               o.concept_id in (856,1305) and  o.obs_datetime between date_add(date_add(:evaluationDate , interval -12 MONTH), interval 1 day) and :evaluationDate  and
               e.location_id=:location
               group by p.patient_id
               
               ) carga_viral_fc on carga_viral_fc.patient_id=coorte12meses_final.patient_id
               left join
               (
               select 	
               		carga_viral_fc.patient_id, 
               		carga_viral_fc.Valor_carga,
                       carga_viral_fc.data_carga 
                       
               		
               
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
               			where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (6,9) and  o.concept_id in (856,1305) and  
               					o.obs_datetime between date_add(date_add(:evaluationDate , interval -12 MONTH), interval 1 day) and :evaluationDate  and e.location_id=:location
               			group by p.patient_id
               		) ultima_carga 
               			inner join obs on obs.person_id=ultima_carga.patient_id and obs.obs_datetime=ultima_carga.data_carga 
               		where obs.voided=0 and ((obs.concept_id=856) or obs.concept_id=1305)  and obs.location_id=:location
               ) carga_viral_fc
               inner join encounter_type et on et.encounter_type_id=carga_viral_fc.encounter_type
               group by carga_viral_fc.patient_id) ultima_cv_fc on ultima_cv_fc.patient_id=coorte12meses_final.patient_id and ultima_cv.data_carga =carga_viral_fc.data_carga
                    
                    left join (
                    Select p.patient_id,max(encounter_datetime) data_filas_rev 
                    from  patient p  
					inner join encounter e on e.patient_id=p.patient_id 
					where p.voided=0 and e.voided=0 and e.encounter_type in (18) and   
					e.location_id=:location and e.encounter_datetime  <=:endDate  
					group by p.patient_id  
					) max_filaFinal_Inclusao on max_filaFinal_Inclusao.patient_id =coorte12meses_final.patient_id   
                    
                    
                    
                          where (data_estado is null or (data_estado is not null and  data_fila>data_estado)) and date_add(data_usar, interval 28 day) >=:endDate 
                      group by coorte12meses_final.patient_id;
