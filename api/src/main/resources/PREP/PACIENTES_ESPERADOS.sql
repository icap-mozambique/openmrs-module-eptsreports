                    SELECT consulta_esperada.patient_id,
                   inicio_real.data_inicio_prep,
                   consulta_esperada.data_ultima_consulta ultima_consulta,
                   consulta_esperada.data_proxima data_agendada,
                   IF(sector.sector is not null,sector.sector,'') sector,
                   IF(consentimento.data_consentimento is not null, 'SIM', '') consentimento,
                   concat(ifnull(pn.given_name,''),' ',ifnull(pn.middle_name,'')) nome,
                   pn.family_name apelido,
                   -- Caso ele nao tiver o primeiro contacto preenchido verifica
                   -- se o segundo tem valor do contrario mostra um texto vazio
                   IF(patient_contact.patient_contact  is not null, patient_contact.patient_contact,
                   IF(patient_contact2.patient_contact  is not null, patient_contact2.patient_contact, '')) contacto,
           		 pid.identifier as NID,
                   per.gender,
            		 pad3.address5, pad3.address1, pad3.address3,  
           		 round(datediff(:endDate,per.birthdate)/365) idade_actual,
                 tipo_prep.tipo,
                 tipo_prep.ultimaConsulta
           		
            FROM
           (
           -- Procura consultas esperadas num determinado periodo
           select p.patient_id, max(obsContinued.value_datetime) data_proxima,max(obsContinued.obs_datetime) data_ultima_consulta from patient p 
                       inner join encounter e on p.patient_id=e.patient_id 
           			inner join obs obsContinued on e.encounter_id= obsContinued.encounter_id 
           			where e.voided=0 and obsContinued.voided=0 and p.voided=0 
                       and e.encounter_type in (80,81) and obsContinued.concept_id=165228 and obsContinued.value_datetime between :startDate
                       and  :endDate and e.location_id=:location group by p.patient_id ) consulta_esperada
           INNER JOIN
           (
           	-- Procura data de inicio de PreP
            	select patient_id, min(data_inicio_prep) data_inicio_prep 										
             		from	(	select utent.patient_id, min(o.obs_datetime) data_inicio_prep   					
             				from patient utent 																	
             					inner join encounter e on e.patient_id = utent.patient_id 							
             					inner join obs o on o.encounter_id = e.encounter_id 								
             				where utent.voided = 0 and e.voided = 0 and o.voided = 0 								
             					and e.encounter_type = 80   and o.concept_id =165296 and o.value_coded = 1256 		
             					and e.location_id = :location and o.obs_datetime <= :endDate					
             					group by utent.patient_id 															
             				union 																					
             				select utent.patient_id, min(o.value_datetime) data_inicio_prep  						
             				from patient utent 																	
             					inner join encounter e on e.patient_id = utent.patient_id 							
             					inner join obs o on o.encounter_id = e.encounter_id 								
             				where utent.voided = 0 and e.voided = 0 and o.voided = 0 								
             					and e.encounter_type = 80   and o.concept_id =165211 and e.location_id = :location 
             					and o.value_datetime <= :endDate														
             					group by utent.patient_id 	
                               
           				union
                           select pg.patient_id,min(date_enrolled) data_inicio
           				from patient p inner join patient_program pg on p.patient_id=pg.patient_id
           				where pg.voided=0 and p.voided=0 and program_id=25 and date_enrolled<=:endDate and location_id=:location
           				group by pg.patient_id
             			)  																							
             		inicio_prep group by inicio_prep.patient_id 													
             	) inicio_real on consulta_esperada.patient_id = inicio_real.patient_id
             left join 
             (
             -- Data da Ultima Consulta
           		select p.patient_id, max(obsContinued.obs_datetime) data_ultima_consulta from patient p 
                       inner join encounter e on p.patient_id=e.patient_id 
           			inner join obs obsContinued on e.encounter_id= obsContinued.encounter_id 
           			where e.voided=0 and obsContinued.voided=0 and p.voided=0 
                       and e.encounter_type in (80,81) and  obsContinued.obs_datetime  <= :endDate and 
                       e.location_id=:location group by p.patient_id ) ultima_consulta on ultima_consulta.patient_id = consulta_esperada.patient_id 
            
            left join (
            -- Procura o consentimento do Utente
             select p.patient_id, max(o.obs_datetime) data_consentimento
           from patient p
           inner join encounter e on p.patient_id=e.patient_id
           inner join obs  o on e.encounter_id=o.encounter_id
           where e.voided=0 and o.voided=0 and p.voided=0 and
           e.encounter_type = 80 and o.concept_id = 6309 and o.value_coded in (6307,23719) and  
           o.obs_datetime<=:endDate and e.location_id=:location
           group by p.patient_id ) consentimento on consulta_esperada.patient_id=consentimento.patient_id
           
           -- Procura tipo de PREP que o utente esta fazer na ultima consulta dentro do periodo
           left join (
           select ultima_consulta.patient_id,obs.value_coded,
           case obs.value_coded
           when 165517 then 'ORAL - Sob Demanda'
           when 165518 then 'ORAL - Diario'
           when 165514 then 'Anel Vaginal'
           when 21959 then 'Injectavel '
           else 'OUTRO' end as tipo,
           ultima_consulta.ultimaConsulta from 
           ( select 	p.patient_id,
    		max(e.encounter_datetime) ultimaConsulta
    	from 	patient p 
    			inner join encounter e on p.patient_id = e.patient_id
    			inner join obs o on e.encounter_id = o.encounter_id
    	where 	p.voided = 0 and e.voided = 0 and o.voided = 0  and 
    			e.encounter_type = 81 and
    			e.location_id = :location
    	group by p.patient_id ) ultima_consulta
		inner join obs on obs.person_id=ultima_consulta.patient_id and obs.obs_datetime=ultima_consulta.ultimaConsulta 
        and obs.obs_datetime <= :endDate
		where obs.voided=0 and obs.concept_id = 165516 and obs.value_coded in (165517,165518,165514,21959)  and obs.location_id=:location 
        group by patient_id  ) tipo_prep on tipo_prep.patient_id=consulta_esperada.patient_id
           
           left join
           ( select sector_inicial.patient_id,
           case o.value_coded
           when 1987 then 'SAAJ'
           when 1978 then 'CPN'
           when 165206 then 'Doenças Crónicas'
           when 23873 then 'Triagem Adulto '
           when 5483 then 'CPF'
           else 'OUTRO' end as sector
           from obs o,
           ( select p.patient_id,max(encounter_datetime) as encounter_datetime
           from patient p
           inner join encounter e on p.patient_id=e.patient_id
           where encounter_type=80 and e.voided=0 and
           encounter_datetime <=:endDate and e.location_id=:location and p.voided=0
           group by patient_id
           ) sector_inicial
           where o.person_id=sector_inicial.patient_id and o.obs_datetime=sector_inicial.encounter_datetime and o.voided=0 and
           o.concept_id=165291 and o.location_id=:location group by patient_id
           ) sector on sector.patient_id=consulta_esperada.patient_id 
           
           -- Junção para pegar dados do Utente ( idade e sexo)
             inner join (
            select person_id,gender,birthdate from person pe where 
            ((pe.birthdate is not null and timestampdiff(year,pe.birthdate,:startDate) >= 15) or pe.birthdate is  null)
            ) per on per.person_id=consulta_esperada.patient_id  
            
           left join 
           	(	select pid1.*
           		from patient_identifier pid1
           		inner join 
           			(
           				select patient_id,min(patient_identifier_id) id 
           				from patient_identifier
           				where voided=0
           				group by patient_id
           			) pid2
           		where pid1.patient_id=pid2.patient_id and pid1.patient_identifier_id=pid2.id
           	) pid on pid.patient_id=consulta_esperada.patient_id
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
           	) pn on pn.person_id=consulta_esperada.patient_id
           
              left join 
              ( select pad1.* 
              from person_address pad1 
              inner join 
              ( 
              select person_id,min(person_address_id) id 
              from person_address 
              where voided=0 
              group by person_id 
              ) pad2 
              where pad1.person_id=pad2.person_id and pad1.person_address_id=pad2.id 
              ) pad3 on pad3.person_id=consulta_esperada.patient_id 
           left join 
           	(	SELECT pa.person_id patient_id, pa.value patient_contact FROM person_attribute pa
                                WHERE pa.person_attribute_type_id = 9
                              GROUP BY pa.person_id
                      	)patient_contact ON patient_contact.patient_id = consulta_esperada.patient_id
                       
           left join 
           	(	SELECT pa.person_id patient_id, pa.value patient_contact FROM person_attribute pa
                                WHERE pa.person_attribute_type_id = 30
                              GROUP BY pa.person_id
                      	)patient_contact2 ON patient_contact2.patient_id = consulta_esperada.patient_id
           
           
             left join
             
            ( select patient_id,max(data_estado) data_estado
           from
           (
           /*Estado no programa*/
           select maxEstado.patient_id,
           maxEstado.data_estado
           from
           (
           select pg.patient_id,max(ps.start_date) data_estado
           from   patient p
           inner join patient_program pg on p.patient_id=pg.patient_id
           inner join patient_state ps on pg.patient_program_id=ps.patient_program_id
           where pg.voided=0 and ps.voided=0 and p.voided=0 and
           pg.program_id=25 and ps.start_date<=:endDate and pg.location_id=:location
           group by p.patient_id
           ) maxEstado
           inner join patient_program pg2 on pg2.patient_id=maxEstado.patient_id
           inner join patient_state ps2 on pg2.patient_program_id=ps2.patient_program_id
           where pg2.voided=0 and ps2.voided=0 and pg2.program_id=25 and
           ps2.start_date=maxEstado.data_estado and pg2.location_id=:location and ps2.state in (77,78,79,80,81,82)
           
           union
           
           /*Obito demografico*/
           
           select person_id as patient_id,death_date as data_estado
           from person
           where dead=1 and death_date is not null and death_date<=:endDate
           
           union
           -- Saida na Ficha Inicial
           select p.patient_id,
           max(o.obs_datetime) data_estado
           from patient p
           inner join encounter e on p.patient_id=e.patient_id
           inner join obs  o on e.encounter_id=o.encounter_id
           where e.voided=0 and o.voided=0 and p.voided=0 and
           e.encounter_type = 80 and o.concept_id = 165292 and o.value_coded = 1260 and  
           o.obs_datetime<=:endDate and e.location_id=:location
           group by p.patient_id
           
           union
           
           -- Saida na Ficha Seguimento PreP
           select p.patient_id,
           max(o.obs_datetime) data_estado
           from patient p
           inner join encounter e on p.patient_id=e.patient_id
           inner join obs  o on e.encounter_id=o.encounter_id
           where e.voided=0 and o.voided=0 and p.voided=0 and
           e.encounter_type = 81 and o.concept_id = 165225 and  
           o.obs_datetime<=:endDate and e.location_id=:location
           group by p.patient_id
           
           ) allSaida
           group by patient_id
           ) saida on consulta_esperada.patient_id=saida.patient_id where saida.patient_id is null;


