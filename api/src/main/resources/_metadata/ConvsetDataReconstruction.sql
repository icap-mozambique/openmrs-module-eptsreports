-- ----------------------------
-- PROCEDURE TO RECONSTRUCT CONVSET FOR TBLAM AND BK TESTS TO GROUP THO THEIR  POSITIVE LEVEL (EXISTING)
-- ----------------------------


DROP PROCEDURE IF EXISTS `ConvsetDataReconstruction`;
#
CREATE DEFINER=`root`@`localhost` PROCEDURE ConvsetDataReconstruction()
    READS SQL DATA
BEGIN

	DECLARE no_info, no_info1, no_info2 INT;

	DECLARE personId INT(11);
	DECLARE encounterId INT(11);
	DECLARE encounterTypeId INT(11);
	DECLARE encounterDatetime DATETIME;
	DECLARE locationId INT(11);
	DECLARE valueDatetime DATETIME;
	DECLARE creatorId INT(11);
	DECLARE dateCreated DATETIME;

	DECLARE testId INT(11);
	DECLARE testGroupId INT(11);
	DECLARE positiveLevelId INT(11);
	DECLARE positiveLevelGroupId INT(11);
	
	DECLARE lastObsId INT(11);

	DECLARE created_comments varchar(100);
	DECLARE updated_comments varchar(100);

	SET created_comments = 'Dado Reconstruido (criado) - SESP Release 3.18.0';
	SET updated_comments = 'Dado Reconstruido (actualizado) - SESP Release 3.18.0';

	BEGIN
		/* Reconstrucao dos testes BK e Niveis de positividade BK criados sem agrupamento de ConvSet nas Fichas Clinica e Laboratório Geral
		*/
		DECLARE cursor_fc_lab_bk CURSOR FOR
			select e.patient_id,
				e.encounter_id,
				e.encounter_datetime, 
				e.creator, 
				e.date_created,
				e.location_id,
				test.obs_id,
				test.obs_group_id,
				posetivityLevel.obs_id,
				posetivityLevel.obs_group_id
			from encounter e
				left join obs test on (e.encounter_id = test.encounter_id and test.voided = 0 and test.concept_id = 307) 
				left join obs posetivityLevel on (e.encounter_id = posetivityLevel.encounter_id and posetivityLevel.voided = 0  and posetivityLevel.concept_id = 165185)
			where e.voided = 0   
				and e.encounter_type in(6, 13)  
				and (test.obs_id is not null or posetivityLevel.obs_id is not null)
				and (test.obs_group_id is null and posetivityLevel.obs_group_id is null);

		DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_info=1;

		SET no_info = 0;
		OPEN cursor_fc_lab_bk;
		cur_loop_c1: WHILE(no_info = 0) DO

			FETCH cursor_fc_lab_bk INTO personId, encounterId, encounterDatetime, creatorId, dateCreated, locationId, testId, testGroupId, positiveLevelId, positiveLevelGroupId;

			IF no_info = 1 THEN
				LEAVE cur_loop_c1;
			END IF;	

			IF testId is not null or positiveLevelId is not null THEN

				insert into obs (person_id, concept_id, encounter_id, obs_datetime, location_id, creator, date_created, status, comments, uuid) 
				values (personId, 165520, encounterId, encounterDatetime, locationId, creatorId, dateCreated,'FINAL', created_comments , uuid());

				select LAST_INSERT_ID() into lastObsId;

				IF testId is not null THEN
					update obs set obs_group_id = lastObsId, comments = updated_comments where obs_id = testId;
				END IF;

				IF positiveLevelId is not null THEN
					update obs set obs_group_id = lastObsId, comments = updated_comments where obs_id = positiveLevelId;
				END IF;

			END IF;			
			
		END WHILE cur_loop_c1;
		CLOSE cursor_fc_lab_bk;
		SET no_info = 0;
	END;


	BEGIN
		/* Reconstrucao dos testes TB LAM e Nivel de Positividade TB LAM criados sem agrupamento de ConvSet nas Fichas Clinica e Laboratório Geral
		*/
		DECLARE cursor_fc_lab_tblam CURSOR FOR
			select e.patient_id,
				e.encounter_id,
				e.encounter_datetime, 
				e.creator, 
				e.date_created,
				e.location_id,
				test.obs_id,
				test.obs_group_id,
				posetivityLevel.obs_id,
				posetivityLevel.obs_group_id
			from encounter e
				left join obs test on (e.encounter_id = test.encounter_id and test.voided = 0 and test.concept_id = 23951) 
				left join obs posetivityLevelConvSet on (e.encounter_id = posetivityLevelConvSet.encounter_id and posetivityLevelConvSet.voided = 0  and posetivityLevelConvSet.concept_id = 165349)
				left join obs posetivityLevel on (e.encounter_id = posetivityLevel.encounter_id and posetivityLevel.voided = 0  and posetivityLevel.concept_id = 165185 and posetivityLevel.obs_group_id = posetivityLevelConvSet.obs_id )
			where e.voided = 0   
				and e.encounter_type in(6, 13)
				and (( test.obs_id is not null and test.obs_group_id is null) or (posetivityLevel.obs_group_id is not null and posetivityLevelConvSet.obs_id is not null))
				and test.obs_group_id is null;

		DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_info1=1;

			SET no_info1 = 0;
			
			OPEN cursor_fc_lab_tblam;
				cur_loop_c2: WHILE(no_info1 = 0) DO

					FETCH cursor_fc_lab_tblam INTO personId, encounterId, encounterDatetime, creatorId, dateCreated, locationId, testId, testGroupId, positiveLevelId, positiveLevelGroupId;

					IF no_info1 = 1 THEN
						LEAVE cur_loop_c2;
					END IF;	

					IF testId is not null and testGroupId is null and positiveLevelGroupId is null THEN

						insert into obs (person_id, concept_id, encounter_id, obs_datetime, location_id, creator, date_created, status, comments, uuid) 
						values (personId, 165349, encounterId, encounterDatetime, locationId, creatorId, dateCreated,'FINAL', created_comments, uuid());

						select LAST_INSERT_ID() into lastObsId;

						update obs set obs_group_id = lastObsId, comments = updated_comments where obs_id = testId;

					END IF;

					IF positiveLevelGroupId is not null and testId is not null and testGroupId is null THEN

						update obs set obs_group_id = positiveLevelGroupId, comments = updated_comments where obs_id = testId;

					END IF;
		
			 	END WHILE cur_loop_c2;
			CLOSE cursor_fc_lab_tblam;
			
			SET no_info1 = 0;
		END;

	BEGIN
		/* Reconstrucao dos testes TB LAM e Nivel de Positividade TB LAM criados sem agrupamento de ConvSet na Ficha e-Lab 
		*/
		DECLARE cursor_elab_tblam CURSOR FOR
			select e.patient_id,
				e.encounter_id,
				e.encounter_datetime, 
				e.creator, 
				e.date_created,
				e.location_id,
				test.obs_id,
				test.obs_group_id,
				posetivityLevel.obs_id,
				posetivityLevel.obs_group_id
			from encounter e
				left join obs test on (e.encounter_id = test.encounter_id and test.voided = 0 and test.concept_id = 23951) 
				left join obs posetivityLevel on (e.encounter_id = posetivityLevel.encounter_id and posetivityLevel.voided = 0  and posetivityLevel.concept_id = 165185)
			where e.voided = 0   
				and e.encounter_type = 51  
				and (test.obs_id is not null or posetivityLevel.obs_id is not null)
				and (test.obs_group_id is null and posetivityLevel.obs_group_id is null);

			DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_info2=1;

			SET no_info2 = 0;
			OPEN cursor_elab_tblam;
			cur_loop_c3: WHILE(no_info2 = 0) DO

				FETCH cursor_elab_tblam INTO personId, encounterId, encounterDatetime, creatorId, dateCreated, locationId, testId, testGroupId, positiveLevelId, positiveLevelGroupId;

				IF no_info2 = 1 THEN
					LEAVE cur_loop_c3;
				END IF;	

				IF testId is not null or positiveLevelId is not null THEN

					insert into obs (person_id, concept_id, encounter_id, obs_datetime, location_id, creator, date_created, status, comments, uuid) 
					values (personId, 165349, encounterId, encounterDatetime, locationId, creatorId, dateCreated,'FINAL', created_comments, uuid());

					select LAST_INSERT_ID() into lastObsId;


					IF positiveLevelId is not null THEN

						update obs set obs_group_id = lastObsId, comments = updated_comments where obs_id = positiveLevelId;

					END IF;

					IF testId is not null THEN

						update obs set obs_group_id = lastObsId, comments = updated_comments where obs_id = testId;

					END IF;
				END IF;
				
			END WHILE cur_loop_c3;
			CLOSE cursor_elab_tblam;
			SET no_info2 = 0;
		END;
	
END
#