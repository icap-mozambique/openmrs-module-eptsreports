-- ----------------------------
-- PROCEDURE TO RECONSTRUCT TB RESULT TESTS ON LAB FORM 
-- ----------------------------


DROP PROCEDURE IF EXISTS `TbResultsDataReconstruction`;
#
CREATE DEFINER=`root`@`localhost` PROCEDURE TbResultsDataReconstruction()
    READS SQL DATA
BEGIN

	DECLARE no_info, no_info1, no_info2, no_info3,  no_info4,  no_info5, no_info165191 INT;

	DECLARE personId INT(11);
	DECLARE encounterId INT(11);
	DECLARE encounterDatetime DATETIME;
	DECLARE locationId INT(11);
	DECLARE creatorId INT(11);
	DECLARE dateCreated DATETIME;
    DECLARE obsId INT(11);
    DECLARE valueCoded INT(11);
    DECLARE result165191 varchar(255);
    DECLARE valueToInsert INT(11);

	DECLARE created_comments varchar(100);
	DECLARE updated_comments varchar(100);
	DECLARE voided_reason varchar(100);

	SET created_comments = 'Dado Reconstruido (criado) - SESP Release 4.2.0';
	SET updated_comments = 'Dado Voidado (actualizado) - SESP Release 4.2.0';
	SET voided_reason = 'Dado voidado no ambito da reconstrucao dos resultados de TB(LAB FORM) - SESP Release 4.2.0';


	BEGIN
	/* Reconstrucao dos resultados de XPert MTB/RIF - Presença de MTB detectada(165189) para passarem a ser respostas do TIPO DE TESTE PCR TB(165586) na ficha Laboratório Geral
	*/
	DECLARE cursor_lab_rif CURSOR FOR
	select e.patient_id,
				e.encounter_id,
				e.encounter_datetime, 
				e.location_id,
				e.creator, 
				e.date_created,
				o.obs_id,
				o.value_coded
				from obs o 
	inner join encounter e on o.encounter_id = e.encounter_id
	where o.voided=0 and e.voided=0 and e.encounter_type=13 and o.concept_id=165189 and o.value_coded in (1065,1066);


	DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_info=1;


			SET no_info = 0;
		OPEN cursor_lab_rif;
		cur_loop_c1: WHILE(no_info = 0) DO

			FETCH cursor_lab_rif INTO personId, encounterId, encounterDatetime, locationId, creatorId, dateCreated, obsId, valueCoded;

			IF no_info = 1 THEN
				LEAVE cur_loop_c1;
			END IF;	

				insert into obs (person_id, concept_id, encounter_id, obs_datetime, value_coded, location_id, creator, date_created, status, comments, uuid) 
				values (personId, 165586, encounterId, encounterDatetime, 165189, locationId, creatorId, dateCreated,'FINAL', created_comments , uuid());		
			
		END WHILE cur_loop_c1;
		CLOSE cursor_lab_rif;
		SET no_info = 0;
	END;


	BEGIN
	/* Reconstrucao da resposta NAO(1066) do XPert MTB/RIF - Presença de MTB detectada(165189) para passar a preencher a resposta do PCR TB Resultado(165588) - Nao Detectado(664)
	*/
	DECLARE cursor_nivel_detectado CURSOR FOR
	select e.patient_id,
				e.encounter_id,
				e.encounter_datetime, 
				e.location_id,
				e.creator, 
				e.date_created,
				o.obs_id,
				o.value_coded
				from obs o 
	inner join encounter e on o.encounter_id = e.encounter_id
	where o.voided=0 and e.voided=0 and e.encounter_type=13 and o.concept_id=165189 and o.value_coded = 1066;


	DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_info4=1;


			SET no_info4 = 0;
		OPEN cursor_nivel_detectado;
		cur_loop_c4: WHILE(no_info4 = 0) DO

			FETCH cursor_nivel_detectado INTO personId, encounterId, encounterDatetime, locationId, creatorId, dateCreated, obsId, valueCoded;

			IF no_info4 = 1 THEN
				LEAVE cur_loop_c4;
			END IF;	

			        INSERT INTO obs (
			            person_id, concept_id, encounter_id, obs_datetime, value_coded, location_id,
			            creator, date_created, status, comments, uuid
			        ) VALUES (
			            personId, 165588, encounterId, encounterDatetime, 664, locationId,
			            creatorId, dateCreated, 'FINAL', created_comments, uuid()
			        );

			END WHILE cur_loop_c4;
			CLOSE cursor_nivel_detectado;
			SET no_info4 = 0;
	END;


	BEGIN
	    /* Reconstrucao das respostas do nivel de positividade(703,6230,6229,6228,165190) quando XPert MTB/RIF. 
	       for (concept_id = 165189 || value_coded = 1065), para preencher PCR TB Resultado (165588) 
	       conforme o nivel de positividade seleccionad (165191)
	    */

	    DECLARE cur_nivel_preenchido CURSOR FOR
	        SELECT e.patient_id,
	               e.encounter_id,
	               e.encounter_datetime, 
	               e.location_id,
	               e.creator, 
	               e.date_created,
	               o.obs_id,
	               o.value_coded
	        FROM obs o 
	        INNER JOIN encounter e ON o.encounter_id = e.encounter_id
	        WHERE o.voided = 0 
	          AND e.voided = 0 
	          AND e.encounter_type = 13 
	          AND o.concept_id = 165189 
	          AND o.value_coded = 1065;

	    DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_info5 = 1;

	    -- Controlando exception caso nao exista o nivel de positividade
	    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET no_info165191 = 1;

	    SET no_info5 = 0;
	    OPEN cur_nivel_preenchido;

	    cur_loop_c5: WHILE no_info5 = 0 DO
	        FETCH cur_nivel_preenchido 
	        INTO personId, encounterId, encounterDatetime, locationId, creatorId, dateCreated, obsId, valueCoded;

	        IF no_info5 = 1 THEN
	            LEAVE cur_loop_c5;
	        END IF;

	        -- Reset value
	        SET valueToInsert = NULL;
	        SET result165191 = NULL;
	        SET no_info165191 = 0;

	        -- Select 165191(nivel de positividade caso exista)
	        BEGIN
	            DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_info165191 = 1;

	            SELECT o.value_coded
	            INTO result165191
	            FROM obs o
	            WHERE o.voided = 0 
	              AND o.concept_id = 165191 
	              AND o.encounter_id = encounterId
	            LIMIT 1;
	        END;

	        SET valueToInsert = CASE
	            WHEN result165191 IS NULL THEN 703          -- Detectado
	            WHEN result165191 = 6230 THEN 6230          -- Detectado - Alto
	            WHEN result165191 = 6229 THEN 6229          -- Detectado - Médio
	            WHEN result165191 = 6228 THEN 6228          -- Detectado - Baixo
	            WHEN result165191 = 165190 THEN 165190      -- Traços
	            ELSE 703                                    -- Default
	        END;

	        -- Criando observacao para cada nivel de positividade encontrado
	        IF valueToInsert IS NOT NULL THEN
	            INSERT INTO obs (
	                person_id, concept_id, encounter_id, obs_datetime, value_coded, location_id,
	                creator, date_created, status, comments, uuid
	            ) VALUES (
	                personId, 165588, encounterId, encounterDatetime, valueToInsert, locationId,
	                creatorId, dateCreated, 'FINAL', created_comments, uuid()
	            );
	        END IF;

	    END WHILE cur_loop_c5;

	    CLOSE cur_nivel_preenchido;
	    SET no_info5 = 0;
	END;



	BEGIN
	/* Voidando o Xpert MTB/Rif como conceito
	*/
	DECLARE cursor_lab_update_1 CURSOR FOR
	select e.patient_id,
				e.encounter_id,
				e.encounter_datetime, 
				e.location_id,
				e.creator, 
				e.date_created,
				o.obs_id,
				o.value_coded
				from obs o 
	inner join encounter e on o.encounter_id = e.encounter_id
	where o.voided=0 and e.voided=0 and e.encounter_type=13 and o.concept_id=165189 and o.value_coded in (1065,1066);


	DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_info2=1;


			SET no_info2 = 0;
		OPEN cursor_lab_update_1;
		cur_loop_c3: WHILE(no_info2 = 0) DO

			FETCH cursor_lab_update_1 INTO personId, encounterId, encounterDatetime, locationId, creatorId, dateCreated, obsId, valueCoded;

			IF no_info2 = 1 THEN
				LEAVE cur_loop_c3;
			END IF;	

			update obs set voided = true, voided_by = creatorId, date_voided = now(), void_reason = voided_reason, comments = updated_comments where obs_id = obsId;	
			
		END WHILE cur_loop_c3;
		CLOSE cursor_lab_update_1;
		SET no_info2 = 0;
	END;



	BEGIN
	/* Voidando o Nivel de Positividade
	*/
	DECLARE cursor_lab_update_2 CURSOR FOR
	select e.patient_id,
				e.encounter_id,
				e.encounter_datetime, 
				e.location_id,
				e.creator, 
				e.date_created,
				o.obs_id,
				o.value_coded from obs o 
	inner join encounter e on o.encounter_id = e.encounter_id
	where o.voided=0 and e.voided=0 and e.encounter_type=13 and o.concept_id=165191 and o.value_coded in (6230,6229,6228,165190);


	DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_info3=1;


			SET no_info3 = 0;
		OPEN cursor_lab_update_2;
		cur_loop_c4: WHILE(no_info3 = 0) DO

			FETCH cursor_lab_update_2 INTO personId, encounterId, encounterDatetime, locationId, creatorId, dateCreated, obsId, valueCoded;

			IF no_info3 = 1 THEN
				LEAVE cur_loop_c4;
			END IF;	

			update obs set voided = true, voided_by = creatorId, date_voided = now(), void_reason = voided_reason, comments = updated_comments where obs_id = obsId;	
			
		END WHILE cur_loop_c4;
		CLOSE cursor_lab_update_2;
		SET no_info3 = 0;
	END;
	
END
#
