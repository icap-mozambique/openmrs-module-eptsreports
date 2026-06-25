-- Lista 2 Educador Par, SAAJ
select finalList.*,
		if(finalList.intervencao in ('DCP','AMA','CM') and tb is null and genero='M' and idade_actual between 25 and 49,'HC','') grupo
from 
(select lista.*,
		if(ordem=1 and tipoDispensaCode=165178,'Reintegração',
			if(ordem=1 and data_carga is null and mesesTARV>6,'CM',
				if(ordem=1 and valorCV>1000 and (data_carga between date_add(:endDate, interval -90 day) and :endDate),'AMA',
					if(ordem=1,'DCP',
						if(ordem=5,'CM.',
							if(ordem=6,'AMA','Reintegração')))))) intervencao,		
		if(ordem=1 and tipoDispensaCode=165178,'Faltoso da DCP',originalTipo) TIPO_PACIENTE
from 
(
	select 	LISTA1.patient_id,
			LISTA1.TIPO_PACIENTE originalTipo,
			LISTA1.ordem,			
		timestampdiff(MONTH,inicio.data_inicio,:endDate) mesesTARV,
		pid.identifier as NID,		
		pn.given_name nome_inicial,
		pn.family_name apelido,
		concat(ifnull(pn.given_name,''),' ',ifnull(pn.middle_name,''),' ',ifnull(pn.family_name,'')) nome,
		pat.value as Telefone,
		pad3.state_province provincia,
		pad3.county_district distrito,
		pad3.address6 as localidade,
		pad3.address5 as bairro,
		pad3.address1 as Referencia,
		per.gender genero,
		timestampdiff(year,birthdate,:endDate) idade_actual,
		per.birthdate data_do_nacimento,
		levantamento.encounter_datetime ultimo_levantamento,
		levantamento.value_datetime proximo_levantamento,
		-- seguimento.encounter_datetime ultimo_seguimento,
		-- seguimento.value_datetime proximo_seguimento,
		-- if(levantamento.encounter_datetime is not null,levantamento.encounter_datetime,seguimento.encounter_datetime) data_ultima_consulta,
		-- if(levantamento.value_datetime is not null,levantamento.value_datetime,seguimento.value_datetime) data_proxima_consulta,
		if(tuberculose.patient_id is not null,'Sim',null) tb,
		modoDispensa.dispensa,
		modoDispensa.tipoDispensaCode,
		ultima_carga.data_carga,
		ultima_carga.valorCV,
		-- inicioTPI.data_inicio_tpi,
		-- fimTPI.data_final_inh,
		finalkptable.keypop,
		ccu.ultimoRastreio
		
		
from
(
	
	select 	consultaRecepcao.patient_id,			
			'Faltoso' as TIPO_PACIENTE,
			1 as ordem
			
	from 
	(
		
		-- Get Scheduled date on last Fila
		select maxFila.patient_id,maxFila.data_consulta,o.value_datetime  proximo_levantamento
		from
		(
			-- Get max fila for each patient
			Select  p.patient_id,max(e.encounter_datetime) data_consulta
			from    patient p 
					inner join encounter e on p.patient_id=e.patient_id 
			where 	e.voided=0 and p.voided=0 and e.encounter_type=18 and  
					e.encounter_datetime<=:endDate and e.location_id=:location 
			GROUP BY p.patient_id
		) maxFila 
		inner join obs o on o.person_id=maxFila.patient_id
		where 	o.obs_datetime=maxFila.data_consulta and o.concept_id=5096 and o.voided=0	
	) consultaRecepcao
	group by patient_id    
	having 	
		max(proximo_levantamento) between date_add(:endDate, interval -11 day) and date_add(:endDate, interval -5 day)	or 
		max(proximo_levantamento) between date_add(:endDate, interval -42 day) and date_add(:endDate, interval -36 day)	
	
	union	
	
	select 	consultaRecepcao.patient_id,			
			'Abandono' as TIPO_PACIENTE,
			4 as ordem
			
	from 
	(
		
		-- Get Scheduled date on last Fila
		select maxFila.patient_id,maxFila.data_consulta,o.value_datetime  proximo_levantamento
		from
		(
			-- Get max fila for each patient
			Select  p.patient_id,max(e.encounter_datetime) data_consulta
			from    patient p 
					inner join encounter e on p.patient_id=e.patient_id 
			where 	e.voided=0 and p.voided=0 and e.encounter_type=18 and  
					e.encounter_datetime<=:endDate and e.location_id=:location 
			GROUP BY p.patient_id
		) maxFila 
		inner join obs o on o.person_id=maxFila.patient_id
		where 	o.obs_datetime=maxFila.data_consulta and o.concept_id=5096 and o.voided=0		
	) consultaRecepcao
	group by patient_id    
	having 	
		max(proximo_levantamento) between date_add(:endDate, interval -73 day) and date_add(:endDate, interval -67  day) or  
		max(proximo_levantamento) between date_add(:endDate, interval -122 day) and date_add(:endDate, interval -116  day) or 
		max(proximo_levantamento) between date_add(:endDate, interval -164 day) and date_add(:endDate, interval -158  day) or  
	 	max(proximo_levantamento) between date_add(:endDate, interval -206 day) and date_add(:endDate, interval -200  day) 
	
	
		UNION
		
		select levMarcado.patient_id,			
			'Activo' as TIPO_PACIENTE,
			5 as ordem
		from 
		(
			select 	consultaRecepcao.patient_id
			
			from 
		
			(
				
				-- Get Scheduled date on last Fila
				select maxFila.patient_id,maxFila.data_consulta,o.value_datetime  proximo_levantamento
				from
				(
					-- Get max fila for each patient
					Select  p.patient_id,max(e.encounter_datetime) data_consulta
					from    patient p 
							inner join encounter e on p.patient_id=e.patient_id 
					where 	e.voided=0 and p.voided=0 and e.encounter_type=18 and  
							e.encounter_datetime<=:endDate and e.location_id=:location 
					GROUP BY p.patient_id
				) maxFila 
				inner join obs o on o.person_id=maxFila.patient_id
				where 	o.obs_datetime=maxFila.data_consulta and o.concept_id=5096 and o.voided=0	
			) consultaRecepcao
			group by patient_id    
			having 	max(proximo_levantamento) between date_add(:endDate, interval 46 day) and date_add(:endDate, interval 52 day)
		) levMarcado
		inner join 
		(	Select patient_id,min(data_inicio) data_inicio
					from
						(	
							/*Patients on ART who initiated the ARV DRUGS: ART Regimen Start Date*/
							
							Select 	p.patient_id,min(e.encounter_datetime) data_inicio
							from 	patient p 
									inner join encounter e on p.patient_id=e.patient_id	
									inner join obs o on o.encounter_id=e.encounter_id
							where 	e.voided=0 and o.voided=0 and p.voided=0 and 
									e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and 
									e.encounter_datetime<=:endDate and e.location_id=:location
							group by p.patient_id
					
							union
					
							/*Patients on ART who have art start date: ART Start date*/
							Select 	p.patient_id,min(value_datetime) data_inicio
							from 	patient p
									inner join encounter e on p.patient_id=e.patient_id
									inner join obs o on e.encounter_id=o.encounter_id
							where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and 
									o.concept_id=1190 and o.value_datetime is not null and 
									o.value_datetime<=:endDate and e.location_id=:location
							group by p.patient_id

							union

							/*Patients enrolled in ART Program: OpenMRS Program*/
							select 	pg.patient_id,min(date_enrolled) data_inicio
							from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id
							where 	pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location
							group by pg.patient_id
							
							union
							
							
							/*Patients with first drugs pick up date set in Pharmacy: First ART Start Date*/
							  SELECT 	e.patient_id, MIN(e.encounter_datetime) AS data_inicio 
							  FROM 		patient p
										inner join encounter e on p.patient_id=e.patient_id
							  WHERE		p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location
							  GROUP BY 	p.patient_id
						  
							union
							
							/*Patients with first drugs pick up date set: Recepcao Levantou ARV*/
							Select 	p.patient_id,min(value_datetime) data_inicio
							from 	patient p
									inner join encounter e on p.patient_id=e.patient_id
									inner join obs o on e.encounter_id=o.encounter_id
							where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and 
									o.concept_id=23866 and o.value_datetime is not null and 
									o.value_datetime<=:endDate and e.location_id=:location
							group by p.patient_id	



						) inicio_real
					group by patient_id
			)inicio on levMarcado.patient_id=inicio.patient_id		
			left join		
			(		Select 	p.patient_id,max(o.obs_datetime) data_carga
					from 	patient p
							inner join encounter e on p.patient_id=e.patient_id
							inner join obs o on e.encounter_id=o.encounter_id
					where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (13,51) and 
							o.concept_id in (856,1305) and 
							o.obs_datetime between date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) and :endDate and e.location_id=:location
					group by p.patient_id
			) ultima_carga on levMarcado.patient_id=ultima_carga.patient_id
			where ultima_carga.patient_id is null and timestampdiff(MONTH,inicio.data_inicio,:endDate)>6
		
		union 
		
		select 	coorte12meses_final.patient_id,
				'Activo' as TIPO_PACIENTE,
				6 as ordem
		from 
		(
			select inicio_fila_seg_prox.*,     
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
                                     inner join person pe on pe.person_id = p.patient_id                                                                         
                                     inner join patient_program pg on p.patient_id = pg.patient_id                                                               
                                     inner join patient_state ps on pg.patient_program_id = ps.patient_program_id                                                
                                 where pg.voided=0 and ps.voided=0 and p.voided=0 and pe.voided = 0 and pg.program_id = 2                                        
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
															inner join person pe on pe.person_id = p.patient_id                                                                         
															inner join patient_program pg on p.patient_id = pg.patient_id                                                               
															inner join patient_state ps on pg.patient_program_id = ps.patient_program_id                                                
														where pg.voided=0 and ps.voided=0 and p.voided=0 and pe.voided = 0 and pg.program_id = 2                                        
															and ps.start_date<= :endDate and pg.location_id =:location group by pg.patient_id                                           
											) max_estado                                                                                                                        
												inner join patient_program pp on pp.patient_id = max_estado.patient_id                                                          
												inner join patient_state ps on ps.patient_program_id = pp.patient_program_id and ps.start_date = max_estado.data_estado         
											where pp.program_id = 2 and ps.state = 10 and pp.voided = 0 and ps.voided = 0 and pp.location_id = :location  
											 union
											select  p.patient_id,                                                                                                               
												max(o.obs_datetime) data_estado                                                                                             
											from patient p                                                                                                                   
												inner join person pe on pe.person_id = p.patient_id                                                                         
												inner join encounter e on p.patient_id=e.patient_id                                                                         
												inner join obs  o on e.encounter_id=o.encounter_id                                                                          
											where e.voided=0 and o.voided=0 and p.voided=0 and pe.voided = 0                                                               
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
												inner join person pe on pe.person_id = p.patient_id                                                                         
												inner join encounter e on p.patient_id=e.patient_id                                                                         
												inner join obs obsObito on e.encounter_id=obsObito.encounter_id                                                             
											where e.voided=0 and p.voided=0 and pe.voided = 0 and obsObito.voided=0                                                        
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
														inner join person pe on pe.person_id = p.patient_id                                                                                         
														inner join encounter e on e.patient_id=p.patient_id                                                                                         
												where   p.voided=0 and pe.voided = 0 and e.voided=0 and e.encounter_type=18                                                                      
														and e.location_id=:location and e.encounter_datetime<=:endDate                                                                                  
														group by p.patient_id  
												union
												
												select  p.patient_id,max(encounter_datetime) data_encountro                                                                                    
												from patient p                                                                                                                                   
													inner join person pe on pe.person_id = p.patient_id                                                                                         
													inner join encounter e on e.patient_id=p.patient_id                                                                                         
												where   p.voided=0 and pe.voided = 0 and e.voided=0 and e.encounter_type in (6,9)                                                                
													and e.location_id=:location and e.encounter_datetime<=:endDate                                                                                  
													group by p.patient_id   
									) fila_seguimento	group by fila_seguimento.patient_id  
							 ) fila_seguimento on dead_state.patient_id = fila_seguimento.patient_id
								where fila_seguimento.data_encountro is null or  fila_seguimento.data_encountro <= dead_state.data_estado
							 
							 union
							 
							 select  p.patient_id,                                                                                                               
                                     max(o.obs_datetime) data_estado                                                                                             
                             from    patient p                                                                                                                   
                                     inner join person pe on pe.person_id = p.patient_id                                                                         
                                     inner join encounter e on p.patient_id=e.patient_id                                                                         
                                     inner join obs  o on e.encounter_id=o.encounter_id                                                                          
                             where   e.voided=0 and o.voided=0 and p.voided=0 and pe.voided = 0 and                                                              
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
				                                 		inner join person pe on pe.person_id = p.patient_id                                                                         
				                                     	inner join patient_program pg on p.patient_id = pg.patient_id                                                               
				                                     	inner join patient_state ps on pg.patient_program_id = ps.patient_program_id                                                
				                                 where pg.voided=0 and ps.voided=0 and p.voided=0 and pe.voided = 0 and pg.program_id = 2                                        
				                                 		and ps.start_date<= :endDate and pg.location_id =:location group by pg.patient_id                                           
			                             		) max_estado                                                                                                                        
			                                 		inner join patient_program pp on pp.patient_id = max_estado.patient_id                                                          
			                                 		inner join patient_state ps on ps.patient_program_id = pp.patient_program_id and ps.start_date = max_estado.data_estado         
			                             		where pp.program_id = 2 and ps.state = 7 and pp.voided = 0 and ps.voided = 0 and pp.location_id = :location                 
			                             
			                             		union                                                                                                                               
			                             		
			                             		select  p.patient_id,max(o.obs_datetime) data_estado                                                                                             
			                             		from patient p                                                                                                                   
			                                   	inner join person pe on pe.person_id = p.patient_id                                                                         
			                                     	inner join encounter e on p.patient_id=e.patient_id                                                                         
			                                     	inner join obs  o on e.encounter_id=o.encounter_id                                                                          
			                             		where e.voided=0 and o.voided=0 and p.voided=0 and pe.voided = 0                                                               
			                                   	and e.encounter_type in (53,6) and o.concept_id in (6272,6273) and o.value_coded = 1706                         
			                                     	and o.obs_datetime<=:endDate and e.location_id=:location                                                                        
			                             			group by p.patient_id                                                                                                               
			                             		
			                             	     union                                                                                                                               
				                             
				                             	select ultimaBusca.patient_id, ultimaBusca.data_estado                                                                              
				                             	from (                                                                                                                              
				                                     select p.patient_id,max(e.encounter_datetime) data_estado                                                                   
				                                     from patient p                                                                                                              
				                                         inner join person pe on pe.person_id = p.patient_id                                                                     
				                                         inner join encounter e on p.patient_id=e.patient_id                                                                     
				                                         inner join obs o on o.encounter_id=e.encounter_id                                                                       
				                                     where e.voided=0 and p.voided=0 and pe.voided = 0 and e.encounter_datetime<= :endDate                                       
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
			                         	inner join person pe on pe.person_id = p.patient_id                                                                                         
			                              inner join encounter e on p.patient_id=e.patient_id                                                                                         
			                              inner join obs o on e.encounter_id=o.encounter_id                                                                                           
			                        	where p.voided=0 and pe.voided = 0 and e.voided=0 and o.voided=0 and e.encounter_type=52                                                       
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
                     inner join person pe on pe.person_id = p.patient_id                                                                                         
                     inner join encounter e on e.patient_id=p.patient_id                                                                                         
             where   p.voided=0 and pe.voided = 0 and e.voided=0 and e.encounter_type=18 and                                                                     
                     e.location_id=:location and e.encounter_datetime<=:endDate                                                                                  
             group by p.patient_id                                                                                                                               
             ) max_fila on inicio.patient_id=max_fila.patient_id  
              left join                                                                                                                                          
              (                                                                                                                                                  
             select  p.patient_id,max(value_datetime) data_recepcao_levantou                                                                                     
             from    patient p                                                                                                                                   
                     inner join person pe on pe.person_id = p.patient_id                                                                                         
                     inner join encounter e on p.patient_id=e.patient_id                                                                                         
                     inner join obs o on e.encounter_id=o.encounter_id                                                                                           
             where   p.voided=0 and pe.voided = 0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and                                                      
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
		inner join 
		(
			select maxCargaViral.patient_id,o.concept_id,maxCargaViral.data_carga,o.value_numeric,o.value_coded
			from 
			(			
				Select 	p.patient_id,max(o.obs_datetime) data_carga
				from 	patient p
						inner join encounter e on p.patient_id=e.patient_id
						inner join obs o on e.encounter_id=o.encounter_id
				where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (13,51) and 
						o.concept_id in (856,1305) and 
						o.obs_datetime<=:endDate and e.location_id=:location
				group by p.patient_id
			) maxCargaViral 
			inner join obs o on maxCargaViral.patient_id=o.person_id and maxCargaViral.data_carga=o.obs_datetime and o.voided=0 and o.concept_id in (856,1305) and o.location_id=:location
		) ultimaCarga on coorte12meses_final.patient_id=ultimaCarga.patient_id
		left join 
		(
			select 	p.patient_id,max(encounter_datetime) as encounter_datetime
			from 	encounter e 
					inner join patient p on p.patient_id=e.patient_id 		
			where 	e.voided=0 and p.voided=0 and e.encounter_type=35 and e.location_id=:location and 
					e.encounter_datetime between date_add(:endDate, interval -14  day) and date_add(:endDate, interval -1  day)
			group by p.patient_id
		) apss on coorte12meses_final.patient_id=apss.patient_id
		where 	(data_estado is null or (data_estado is not null and  data_fila > data_estado)) and date_add(data_usar, interval 28 day) >=:endDate and ultimaCarga.value_numeric>1000 and 
				(
					ultimaCarga.data_carga between date_add(:endDate, interval -13 day) and date_add(:endDate, interval -7  day) or 
					ultimaCarga.data_carga between date_add(:endDate, interval -27 day) and date_add(:endDate, interval -22  day) or 
					ultimaCarga.data_carga between date_add(:endDate, interval -69 day) and date_add(:endDate, interval -63  day) or 
					ultimaCarga.data_carga between date_add(:endDate, interval -90 day) and date_add(:endDate, interval -84  day)  
				
				) and apss.patient_id is null 		
	
) LISTA1
left join 
(	

	-- Ficha de Seguimento: Data de Inicio de Tratamento de TB
		SELECT 	p.patient_id 
		FROM 	patient p
				INNER JOIN encounter e ON e.patient_id = p.patient_id
				INNER JOIN obs o ON o.encounter_id = e.encounter_id
		WHERE 	p.voided = 0 AND e.voided = 0 AND o.voided = 0
				AND e.encounter_type IN (6,9)
				AND o.concept_id = 1113
				AND e.location_id = :location AND o.value_datetime between date_add(:endDate, interval -8 month) and :endDate 
		GROUP BY p.patient_id
		
		
		UNION
		
		-- PATIENTS ON TB PROGRAM
		SELECT 	p.patient_id 
		FROM 	patient p
				INNER JOIN patient_program pg ON pg.patient_id = p.patient_id
		WHERE 	p.voided = 0 AND pg.voided = 0
				AND pg.program_id = 5 
				AND pg.date_enrolled BETWEEN date_add(:endDate, interval -8 month) and :endDate AND pg.location_id = :location
		GROUP BY p.patient_id
		
		UNION
		
		-- Ficha Resumo: Condicoes medicas importantes
		SELECT 	p.patient_id 
		FROM 	patient p
				INNER JOIN encounter e ON e.patient_id = p.patient_id
				INNER JOIN obs o ON o.encounter_id = e.encounter_id
		WHERE 	p.voided = 0 AND e.voided = 0 AND o.voided = 0
				AND e.encounter_type=53
				AND o.concept_id = 42
				AND o.value_coded=1065
				AND e.location_id = :location AND o.obs_datetime between date_add(:endDate, interval -8 month) and :endDate 
		GROUP BY p.patient_id
		
		UNION
		
		-- Ficha Clinica: Tratamento TB
		SELECT 	p.patient_id 
		FROM 	patient p
				INNER JOIN encounter e ON e.patient_id = p.patient_id
				INNER JOIN obs o ON o.encounter_id = e.encounter_id
		WHERE 	p.voided = 0 AND e.voided = 0 AND o.voided = 0
				AND e.encounter_type=6
				AND o.concept_id = 1268
				AND o.value_coded=1256
				AND e.location_id = :location AND o.obs_datetime between date_add(:endDate, interval -8 month) and :endDate 
		GROUP BY p.patient_id		
			
			
) tuberculose on tuberculose.patient_id=LISTA1.patient_id
left join 
(	select pid1.*
	from patient_identifier pid1
	inner join 
		(
			select patient_id,max(patient_identifier_id) id 
			from patient_identifier
			where voided=0
			group by patient_id
		) pid2
	where pid1.patient_id=pid2.patient_id and pid1.patient_identifier_id=pid2.id
) pid on pid.patient_id=LISTA1.patient_id
left join 
(	select pn1.*
	from person_name pn1
	inner join 
		(
			select person_id,max(person_name_id) id 
			from person_name
			where voided=0
			group by person_id
		) pn2
	where pn1.person_id=pn2.person_id and pn1.person_name_id=pn2.id
) pn on pn.person_id=LISTA1.patient_id
left join 
(	select pad1.*
	from person_address pad1
	inner join 
		(
			select person_id,max(person_address_id) id 
			from person_address
			where voided=0
			group by person_id
		) pad2
	where pad1.person_id=pad2.person_id and pad1.person_address_id=pad2.id
) pad3 on pad3.person_id=LISTA1.patient_id
left join person_attribute pat on pat.person_id=LISTA1.patient_id and pat.person_attribute_type_id=9 and pat.value is not null and pat.value <> '' and pat.voided=0
left join person per on per.person_id=LISTA1.patient_id
left join 
(	
	
	select patient_id,encounter_datetime,max(value_datetime) value_datetime
	from 	
		(
			Select 	ultimavisita.patient_id,ultimavisita.encounter_datetime,o.value_datetime
			from
				(	select 	p.patient_id,max(encounter_datetime) as encounter_datetime
					from 	encounter e 
							inner join patient p on p.patient_id=e.patient_id 		
					where 	e.voided=0 and p.voided=0 and e.encounter_type=18 and e.location_id=:location and e.encounter_datetime<=:endDate
					group by p.patient_id
				) ultimavisita
				inner join encounter e on e.patient_id=ultimavisita.patient_id
				inner join obs o on o.encounter_id=e.encounter_id			
			where o.concept_id=5096 and o.voided=0 and e.encounter_datetime=ultimavisita.encounter_datetime and 
					e.encounter_type=18 and e.location_id=:location
		) maxVisit
	group by patient_id		
) levantamento on levantamento.patient_id=LISTA1.patient_id
/*left join 
(	
	Select 	ultimavisita.patient_id,ultimavisita.encounter_datetime,o.value_datetime,e.location_id
	from
		(	select 	p.patient_id,max(encounter_datetime) as encounter_datetime
			from 	encounter e 
					inner join patient p on p.patient_id=e.patient_id 		
			where 	e.voided=0 and p.voided=0 and e.encounter_type in (6,9) and e.location_id=:location and e.encounter_datetime<=:endDate
			group by p.patient_id
		) ultimavisita
		inner join encounter e on e.patient_id=ultimavisita.patient_id
		left join obs o on o.encounter_id=e.encounter_id and o.concept_id=1410 and o.voided=0			
	where  	e.encounter_datetime=ultimavisita.encounter_datetime and 
			e.encounter_type in (6,9) and e.location_id=:location
) seguimento on seguimento.patient_id=LISTA1.patient_id*/
left join
(

	select maxFila.patient_id,maxFila.data_consulta,
			case obsDC.value_coded
				when 165175 then 'Horário Normal de Expediente'
				when 165176 then 'Fora do Horário'
				when 165177 then 'FARMAC/Farmácia privada'
				when 165178 then 'Dispensa Comunitária via Provedor'
				when 165179 then 'Dispensa Comunitária via APE'
				when 165180 then 'Brigadas Móveis Diurnas'
				when 165181 then 'Brigadas Móveis Nocturnas (Hotspots)'
				when 165182 then 'Clínicas Móveis Diurnas'
				when 165183 then 'Clínicas Móveis Nocturnas (Hotspots)'
			else '' end as dispensa,
			obsDC.value_coded tipoDispensaCode
		from
		(
			-- Get max fila for each patient
			Select  p.patient_id,max(e.encounter_datetime) data_consulta
			from    patient p 
					inner join encounter e on p.patient_id=e.patient_id 
			where 	e.voided=0 and p.voided=0 and e.encounter_type=18 and  
					e.encounter_datetime<=:endDate and e.location_id=:location 
			GROUP BY p.patient_id
		) maxFila 
		inner join encounter e on e.patient_id=maxFila.patient_id
		inner join obs obsDC on obsDC.encounter_id=e.encounter_id
		where 	e.encounter_datetime=maxFila.data_consulta and e.voided=0 and e.encounter_type=18 and e.location_id=:location and 
				obsDC.concept_id=165174 	

) modoDispensa on modoDispensa.patient_id=LISTA1.patient_id
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
							e.encounter_type in (6,46,35) and e.encounter_datetime<=:endDate and 
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
	) finalkptable on finalkptable.patient_id=LISTA1.patient_id
left join 
(
	select 	lastVL.patient_id,lastVL.data_carga,
		if(o.concept_id=1305,'Indetectavel',
		if(o.value_numeric<0,'Indetectavel',o.value_numeric)) valorCV
	from 
	(
		Select 	p.patient_id,max(o.obs_datetime) data_carga
		from 	patient p
				inner join encounter e on p.patient_id=e.patient_id
				inner join obs o on e.encounter_id=o.encounter_id
		where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (13,51) and 
				o.concept_id in (856,1305) and 
				o.obs_datetime between date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) and :endDate and e.location_id=:location
		group by p.patient_id
	) lastVL
	inner join encounter e on e.patient_id=lastVL.patient_id
	inner join obs o on o.encounter_id=e.encounter_id
	where 	e.voided=0 and o.voided=0 and e.encounter_type in (13,51) and 
			o.concept_id in (856,1305) and o.obs_datetime=lastVL.data_carga and e.location_id=:location
) ultima_carga on ultima_carga.patient_id=LISTA1.patient_id
/*left join
(
	select 	p.patient_id,max(o.value_datetime) data_inicio_tpi
	from	patient p
			inner join encounter e on p.patient_id=e.patient_id
			inner join obs o on o.encounter_id=e.encounter_id
	where 	e.voided=0 and p.voided=0 and o.value_datetime<=:endDate and
			o.voided=0 and o.concept_id=6128 and e.encounter_type=53 and e.location_id=:location
	group by p.patient_id
) inicioTPI on inicioTPI.patient_id=LISTA1.patient_id
left join 
(
	select 	p.patient_id, max(o.value_datetime) data_final_inh 																		
	from 	patient p 																												
			inner join encounter e on p.patient_id=e.patient_id 																	
			inner join obs o on o.encounter_id=e.encounter_id 																		
	where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=53 and o.concept_id=6129 						
			and o.value_datetime <= :endDate and e.location_id=:location 
	group by p.patient_id
) fimTPI on fimTPI.patient_id=LISTA1.patient_id*/
left join 
(
	Select 	p.patient_id,
		max(e.encounter_datetime) ultimoRastreio
	from 	patient p 
			inner join encounter e on p.patient_id=e.patient_id
			inner join obs o on e.encounter_id=o.encounter_id
	where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id=2094 and value_coded in (703,664,2093) and 
			e.encounter_type=28 and e.encounter_datetime between date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) and :endDate and e.location_id=:location
	group by p.patient_id
	
) ccu on ccu.patient_id=LISTA1.patient_id
left join 
(	Select patient_id,min(data_inicio) data_inicio
	from
		(	
			/*Patients on ART who initiated the ARV DRUGS: ART Regimen Start Date*/
			
			Select 	p.patient_id,min(e.encounter_datetime) data_inicio
			from 	patient p 
					inner join encounter e on p.patient_id=e.patient_id	
					inner join obs o on o.encounter_id=e.encounter_id
			where 	e.voided=0 and o.voided=0 and p.voided=0 and 
					e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and 
					e.encounter_datetime<=:endDate and e.location_id=:location
			group by p.patient_id
	
			union
	
			/*Patients on ART who have art start date: ART Start date*/
			Select 	p.patient_id,min(value_datetime) data_inicio
			from 	patient p
					inner join encounter e on p.patient_id=e.patient_id
					inner join obs o on e.encounter_id=o.encounter_id
			where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and 
					o.concept_id=1190 and o.value_datetime is not null and 
					o.value_datetime<=:endDate and e.location_id=:location
			group by p.patient_id

			union

			/*Patients enrolled in ART Program: OpenMRS Program*/
			select 	pg.patient_id,min(date_enrolled) data_inicio
			from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id
			where 	pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location
			group by pg.patient_id
			
			union
			
			
			/*Patients with first drugs pick up date set in Pharmacy: First ART Start Date*/
			  SELECT 	e.patient_id, MIN(e.encounter_datetime) AS data_inicio 
			  FROM 		patient p
						inner join encounter e on p.patient_id=e.patient_id
			  WHERE		p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location
			  GROUP BY 	p.patient_id
		  
			union
			
			/*Patients with first drugs pick up date set: Recepcao Levantou ARV*/
			Select 	p.patient_id,min(value_datetime) data_inicio
			from 	patient p
					inner join encounter e on p.patient_id=e.patient_id
					inner join obs o on e.encounter_id=o.encounter_id
			where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and 
					o.concept_id=23866 and o.value_datetime is not null and 
					o.value_datetime<=:endDate and e.location_id=:location
			group by p.patient_id	



		) inicio_real
	group by patient_id
)inicio on inicio.patient_id=LISTA1.patient_id
left join 
(
					
		-- =====================GRAVIDAS==================
			/*Enrolled on PTV/ETC program with ``pregnancy`` as current state at report end date, ultimos 9 meses */
			select 	maxEstado.patient_id
			from 
				( 
					select 	pg.patient_id,max(ps.start_date) data_estado 
					from  	patient p 
							inner join patient_program pg on p.patient_id=pg.patient_id 
							inner join patient_state ps on pg.patient_program_id=ps.patient_program_id 
					where 	pg.voided=0 and ps.voided=0 and p.voided=0 and 
							pg.program_id=8 and ps.start_date BETWEEN date_add(:endDate, interval -9 month) and :endDate and pg.location_id=:location 
					group by p.patient_id 
				) maxEstado 
				inner join patient_program pg2 on pg2.patient_id=maxEstado.patient_id 
				inner join patient_state ps2 on pg2.patient_program_id=ps2.patient_program_id 
			where 	pg2.voided=0 and ps2.voided=0 and pg2.program_id=8 and 
					ps2.start_date=maxEstado.data_estado and pg2.location_id=:location and ps2.state=25
			
			UNION 
			/*Patients with (date of last menstrual period + 9 meses) > report end date*/
			Select 	p.patient_id
			from 	patient p 
					inner join encounter e on p.patient_id=e.patient_id
					inner join obs o on e.encounter_id=o.encounter_id
			where 	p.voided=0 and e.voided=0 and o.voided=0 and o.concept_id=1465 and 
					e.encounter_type=6 and o.value_datetime between date_add(:endDate, interval -10 month) AND :endDate and e.location_id=:location
			group by p.patient_id
			having date_add(max(o.value_datetime), interval 9 month)>:endDate
			
			UNION 
			/*``Pregnant`` registered in most recent ficha clinica falling 
			before end date and this most recent ficha clinica encounter is more recent than end date – 9 months*/
			select maxClinca.patient_id
			from 
			(
				Select 	p.patient_id,max(encounter_datetime) data_seguimento
				from 	patient p 
						inner join encounter e on e.patient_id=p.patient_id
				where 	p.voided=0 and e.voided=0 and e.encounter_type=6 and 
						e.location_id=:location and e.encounter_datetime BETWEEN date_add(:endDate, interval -9 month) and :endDate
				group by p.patient_id
			) maxClinca
			inner join encounter e on e.patient_id=maxClinca.patient_id
			inner join obs o on o.encounter_id=e.encounter_id
			where 	e.voided=0 and o.voided=0 and e.encounter_type=6 and 
					e.encounter_datetime=maxClinca.data_seguimento and 
					o.concept_id=1982 and o.value_coded=1065 and e.location_id=:location			
			
			UNION
			-- ===========================LACTANTES================================		
			
			/*
				Patients who were enrolled on PTV/ETC program with state 27 (gave birth) between 
				(reporting end date – 18 months) and  reporting end date  
			*/
			select 	maxEstado.patient_id
			from 
				( 
					select 	pg.patient_id,max(ps.start_date) data_estado 
					from  	patient p 
							inner join patient_program pg on p.patient_id=pg.patient_id 
							inner join patient_state ps on pg.patient_program_id=ps.patient_program_id 
					where 	pg.voided=0 and ps.voided=0 and p.voided=0 and 
							pg.program_id=8 and ps.start_date BETWEEN date_add(:endDate, interval -18 month) and :endDate and pg.location_id=:location 
					group by p.patient_id 
				) maxEstado 
				inner join patient_program pg2 on pg2.patient_id=maxEstado.patient_id 
				inner join patient_state ps2 on pg2.patient_program_id=ps2.patient_program_id 
			where 	pg2.voided=0 and ps2.voided=0 and pg2.program_id=8 and 
					ps2.start_date=maxEstado.data_estado and pg2.location_id=:location and ps2.state=27
					
			UNION 
			
			/*
				Patients who were enrolled on PTV/ETC program with state 27 (gave birth) between 
				(reporting end date – 18 months) and  reporting end date 
			*/
			select maxClinca.patient_id
			from 
			(
				Select 	p.patient_id,max(encounter_datetime) data_seguimento
				from 	patient p 
						inner join encounter e on e.patient_id=p.patient_id
				where 	p.voided=0 and e.voided=0 and e.encounter_type=6 and 
						e.location_id=:location and e.encounter_datetime BETWEEN date_add(:endDate, interval -18 month) and :endDate
				group by p.patient_id
			) maxClinca
			inner join encounter e on e.patient_id=maxClinca.patient_id
			inner join obs o on o.encounter_id=e.encounter_id
			where 	e.voided=0 and o.voided=0 and e.encounter_type=6 and 
					e.encounter_datetime=maxClinca.data_seguimento and 
					o.concept_id=6332 and o.value_coded=1065 and e.location_id=:location
			
			/*UNION 
			
			
				Mulheres não-gravidas e não-lactantes com ultimo levantamento de TARV < (data de parto + 18 meses)
				que tenham tido um levantamento marcado 8 á 14 dias antes da Segunda-feira da semana em curso e 
				essa levantamento marcado tem data > (data de parto + 18 meses) e que são faltosas			
			
			select lactanteMaxFila.patient_id
			from 
			(
				Select lactante.patient_id,lactante.dataParto,lactante.dataFinalLactante,max(e.encounter_datetime) ultimoLevantamennto
				from 
				(
					select 	maxEstado.patient_id,maxEstado.data_estado dataParto,date_add(data_estado, interval 18 month) dataFinalLactante
					from 
						( 
							select 	pg.patient_id,max(ps.start_date) data_estado 
							from  	patient p 
									inner join patient_program pg on p.patient_id=pg.patient_id 
									inner join patient_state ps on pg.patient_program_id=ps.patient_program_id 
							where 	pg.voided=0 and ps.voided=0 and p.voided=0 and 
									pg.program_id=8 and ps.start_date BETWEEN date_add(:endDate, interval -20 month) AND :endDate  and pg.location_id=:location 
							group by p.patient_id 
						) maxEstado 
						inner join patient_program pg2 on pg2.patient_id=maxEstado.patient_id 
						inner join patient_state ps2 on pg2.patient_program_id=ps2.patient_program_id 
					where 	pg2.voided=0 and ps2.voided=0 and pg2.program_id=8 and 
							ps2.start_date=maxEstado.data_estado and pg2.location_id=:location and ps2.state=27
				)lactante
				inner join encounter e on e.patient_id=lactante.patient_id
				where 	e.voided=0 and e.encounter_type=18 and e.location_id=:location and e.encounter_datetime<=:endDate
				group by lactante.patient_id
			) lactanteMaxFila
			inner join encounter lev on lev.patient_id=lactanteMaxFila.patient_id 
			inner join obs o on lev.encounter_id=o.encounter_id
			where 	lev.voided=0 and o.voided=0 and lev.encounter_type=18 and lev.encounter_datetime=lactanteMaxFila.ultimoLevantamennto and 
					lev.encounter_datetime BETWEEN lactanteMaxFila.dataParto and lactanteMaxFila.dataFinalLactante and 
					o.concept_id=5096 and lev.location_id=:location and o.value_datetime BETWEEN lactanteMaxFila.dataFinalLactante and :endDate
			group by lactanteMaxFila.patient_id
			having 	max(o.value_datetime) between date_add(:endDate, interval -14 day) and date_add(:endDate, interval -8 day)*/		
				
		
														 
		UNION 
		
		SELECT person_id 
		FROM   person 
		WHERE  dead = 1 AND voided = 0
		
		union
		
		select transferidopara.patient_id
		from 
		(
			select patient_id,max(data_transferidopara) data_transferidopara
			from 
			(
				/*Programa*/
				select 	maxEstado.patient_id,maxEstado.data_transferidopara 
				from 	( 
							select pg.patient_id,max(ps.start_date) data_transferidopara 
							from 	patient p 
									inner join patient_program pg on p.patient_id=pg.patient_id 
									inner join patient_state ps on pg.patient_program_id=ps.patient_program_id 
							where 	pg.voided=0 and ps.voided=0 and p.voided=0 and 
									pg.program_id=2 and ps.start_date<=:endDate and pg.location_id=:location 
							group by p.patient_id 
						) maxEstado 
						inner join patient_program pg2 on pg2.patient_id=maxEstado.patient_id 
						inner join patient_state ps2 on pg2.patient_program_id=ps2.patient_program_id 
				where 	pg2.voided=0 and ps2.voided=0 and pg2.program_id=2 and 
						ps2.start_date=maxEstado.data_transferidopara and pg2.location_id=:location and ps2.state in (7,8,10)

				union
				/*Ficha Resumo*/
				select maxEstado.patient_id,maxEstado.data_transferidopara
				from 
				(
					select 	p.patient_id,max(o.obs_datetime) data_transferidopara
					from	patient p
							inner join encounter e on p.patient_id=e.patient_id
							inner join obs o on o.encounter_id=e.encounter_id
					where 	e.voided=0 and p.voided=0 and o.obs_datetime<=:endDate and
							o.voided=0 and o.concept_id=6272 and e.encounter_type=53 and  e.location_id=:location
					group by p.patient_id
				) maxEstado
				inner join obs obsTrans on maxEstado.patient_id=obsTrans.person_id
				where 	obsTrans.voided=0 and obsTrans.concept_id=6272 and obsTrans.obs_datetime=maxEstado.data_transferidopara and 
						obsTrans.value_coded=1706 and obsTrans.location_id=:location

				union
				/*Ficha Clinica*/
				select maxEstado.patient_id,maxEstado.data_transferidopara
				from 
				(
					select 	p.patient_id,max(o.obs_datetime) data_transferidopara
					from	patient p
							inner join encounter e on p.patient_id=e.patient_id
							inner join obs o on o.encounter_id=e.encounter_id
					where 	e.voided=0 and p.voided=0 and o.obs_datetime<=:endDate and
							o.voided=0 and o.concept_id=6273 and e.encounter_type=6 and  e.location_id=:location
					group by p.patient_id
				) maxEstado
				inner join obs obsTrans on maxEstado.patient_id=obsTrans.person_id
				where 	obsTrans.voided=0 and obsTrans.concept_id=6273 and obsTrans.obs_datetime=maxEstado.data_transferidopara and 
						obsTrans.value_coded=1706 and obsTrans.location_id=:location	
				
			) transferido
			group by patient_id
		) transferidopara
		inner join 
		(
			select patient_id,max(encounter_datetime) encounter_datetime
			from 
			(
				select p.patient_id,max(e.encounter_datetime) encounter_datetime
				from 	patient p
						inner join encounter e on e.patient_id=p.patient_id
				where 	p.voided=0 and e.voided=0 and e.encounter_datetime<=:endDate and 
						e.location_id=:location and e.encounter_type in (18,6,9)
				group by p.patient_id

				/*
				Removed with Cella email 12.07.2022
				union

				Select 	p.patient_id,max(value_datetime) encounter_datetime
				from 	patient p
						inner join encounter e on p.patient_id=e.patient_id
						inner join obs o on e.encounter_id=o.encounter_id
				where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and 
						o.concept_id=23866 and o.value_datetime is not null and 
						o.value_datetime<=:endDate and e.location_id=:location
				group by p.patient_id*/
			) maxFilaRecepcao
			group by patient_id	
		) consultaOuARV on transferidopara.patient_id=consultaOuARV.patient_id
		where consultaOuARV.encounter_datetime<=transferidopara.data_transferidopara
		
	) gravidaLactante on gravidaLactante.patient_id=LISTA1.patient_id
	left join 
	(
		Select 	p.patient_id,count(*) nrFilhos
		from 	patient p
				inner join encounter e on p.patient_id=e.patient_id
				inner join obs o on e.encounter_id=o.encounter_id
		where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=53 and 
				o.concept_id=23704 and o.value_coded=23707 and o.obs_group_id is not null and e.location_id=:location
		group by p.patient_id
	) filhos on filhos.patient_id=LISTA1.patient_id
	
where ((timestampdiff(year,birthdate,:endDate)>=25) or ((timestampdiff(year,birthdate,:endDate) between 15 and 24) and filhos.nrFilhos>=2))  and gravidaLactante.patient_id is null
				
) lista
group by patient_id
order by ordem,TIPO_PACIENTE,NID
) finalList
