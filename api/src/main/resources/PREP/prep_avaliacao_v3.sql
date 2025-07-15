SELECT pa.patient_id,
	pid.identifier AS _NID,
    (SELECT 
		CASE
			WHEN obs_in.value_coded = 165287 THEN "Adolescentes e Jovens em Risco"
			ELSE obs_in.value_coded
		END _grupo	
		FROM obs obs_in 
		WHERE obs_in.encounter_id = (
				SELECT ec.encounter_id FROM encounter ec
				WHERE ec.encounter_type=80 AND ec.patient_id=pa.patient_id
				AND ec.encounter_id  IN (
						-- consulta com resposta do grupo de analise
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165287,1982,6332,1377,1901)
						AND obs.concept_id IN (165196,23703)
				)  AND ec.encounter_id IN (
						-- Consulta que levantou pelo menos 1 regime de medicamento
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165214,165215,165216)
						AND obs.concept_id = 165213
			)
				ORDER BY ec.encounter_datetime ASC limit 0,1
           -- END da Primeira consulta que satisfaz os criterios exigidos    
        ) 
        AND obs_in.concept_id=165196
		AND obs_in.voided=0
        AND obs_in.value_coded = 165287
    ) AS _GRUPO_ALVO_ADOLESCENTE_JOVEM,
    (SELECT 
		CASE
			WHEN obs_in.value_coded = 1982 THEN "GESTANTE"
			ELSE obs_in.value_coded
		END _grupo	
		FROM obs obs_in 
		WHERE obs_in.encounter_id = (
				SELECT ec.encounter_id FROM encounter ec
				WHERE ec.encounter_type=80 AND ec.patient_id=pa.patient_id
				AND ec.encounter_id  IN (
						-- consulta com resposta do grupo de analise
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165287,1982,6332,1377,1901)
						AND obs.concept_id IN (165196,23703)
				)  AND ec.encounter_id IN (
						-- Consulta que levantou pelo menos 1 regime de medicamento
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165214,165215,165216)
						AND obs.concept_id = 165213
			)
				ORDER BY ec.encounter_datetime ASC limit 0,1
           -- END da Primeira consulta que satisfaz os criterios exigidos
        ) 
        AND obs_in.concept_id=165196
		AND obs_in.voided=0
        AND obs_in.value_coded =1982
    ) AS _GRUPO_ALVO_GESTANTE,
    (SELECT 
		CASE
			WHEN obs_in.value_coded = 6332 THEN "LACTANTE"
			ELSE obs_in.value_coded
		END _grupo	
		FROM obs obs_in 
		WHERE obs_in.encounter_id = (
				SELECT ec.encounter_id FROM encounter ec
				WHERE ec.encounter_type=80 AND ec.patient_id=pa.patient_id
				AND ec.encounter_id  IN (
						-- consulta com resposta do grupo de analise
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165287,1982,6332,1377,1901)
						AND obs.concept_id IN (165196,23703)
				)  AND ec.encounter_id IN (
						-- Consulta que levantou pelo menos 1 regime de medicamento
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165214,165215,165216)
						AND obs.concept_id = 165213
			)
				ORDER BY ec.encounter_datetime ASC limit 0,1
           -- END da Primeira consulta que satisfaz os criterios exigidos
        ) 
        AND obs_in.concept_id=165196
		AND obs_in.voided=0
        AND obs_in.value_coded =6332
    ) AS _GRUPO_ALVO_LACTANTE,
    (SELECT 
		CASE
			/*Keypop CODED*/
			WHEN obs_in.value_coded = 1377 THEN "Homens que fazem sexo com Homens"
			ELSE obs_in.value_coded
		END _keypop	
		FROM obs obs_in 
		WHERE obs_in.encounter_id = (
				SELECT ec.encounter_id FROM encounter ec
				WHERE ec.encounter_type=80 AND ec.patient_id=pa.patient_id
				AND ec.encounter_id  IN (
						-- consulta com resposta do grupo de analise
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165287,1982,6332,1377,1901)
						AND obs.concept_id IN (165196,23703)
				)  AND ec.encounter_id IN (
						-- Consulta que levantou pelo menos 1 regime de medicamento
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165214,165215,165216)
						AND obs.concept_id = 165213
			)
				ORDER BY ec.encounter_datetime ASC limit 0,1
           -- END da Primeira consulta que satisfaz os criterios exigidos
        ) AND obs_in.concept_id = 23703
		AND obs_in.voided=0
        AND obs_in.value_coded =1377
    ) AS _GRUPO_ALVO_HOMEM_SEXO_HOMEM,
    (SELECT 
		CASE
			/*Keypop CODED*/
			WHEN obs_in.value_coded = 1901 THEN "TRABALHADOR DE SEXO"
			ELSE obs_in.value_coded
		END _keypop	
		FROM obs obs_in 
		WHERE obs_in.encounter_id = (
				SELECT ec.encounter_id FROM encounter ec
				WHERE ec.encounter_type=80 AND ec.patient_id=pa.patient_id
				AND ec.encounter_id  IN (
						-- consulta com resposta do grupo de analise
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165287,1982,6332,1377,1901)
						AND obs.concept_id IN (165196,23703)
				)  AND ec.encounter_id IN (
						-- Consulta que levantou pelo menos 1 regime de medicamento
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165214,165215,165216)
						AND obs.concept_id = 165213
			)
				ORDER BY ec.encounter_datetime ASC limit 0,1
           -- END da Primeira consulta que satisfaz os criterios exigidos
        ) AND obs_in.concept_id = 23703
		AND obs_in.voided=0
        AND obs_in.value_coded =1901
    ) AS _GRUPO_ALVO_TRABALHADOR_SEXO,
    TIMESTAMPDIFF(YEAR, pe.birthdate, :endDate) AS _IDADE,
	pe.birthdate AS _DATA_NASCIMENTO,
	pe.gender AS _SEXO,
	(SELECT 
		CASE
			WHEN obs.value_coded = 1056 THEN "SEPARADO"
			WHEN obs.value_coded = 1057 THEN "SOLTEIRO"
			WHEN obs.value_coded = 1058 THEN "DIVORCIADO"
			WHEN obs.value_coded = 1059 THEN "VIUVO"
			WHEN obs.value_coded = 1060 THEN "UNIÃO DE FACTO"
			WHEN obs.value_coded = 5555 THEN "CASADO"
			WHEN obs.value_coded = 1175 THEN "NÃO APLICAVEL"
			WHEN obs.value_coded = 5622 THEN "OUTRO, NAO CODIFICADO"
		ELSE obs.value_coded
		END 	
		FROM obs 
		WHERE obs.voided=0 AND obs.encounter_id = (
				SELECT ec.encounter_id FROM encounter ec
				WHERE ec.encounter_type=80 AND ec.patient_id=pa.patient_id
				AND ec.encounter_id  IN (
						-- consulta com resposta do grupo de analise
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165287,1982,6332,1377,1901)
						AND obs.concept_id IN (165196,23703)
				)  AND ec.encounter_id IN (
						-- Consulta que levantou pelo menos 1 regime de medicamento
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165214,165215,165216)
						AND obs.concept_id = 165213
			)
				ORDER BY ec.encounter_datetime ASC limit 0,1
           -- END da Primeira consulta que satisfaz os criterios exigidos
        ) 
        AND obs.concept_id=1054
	) AS _ESTADO_CIVIL,
    (
		SELECT 
			CASE WHEN 
					(
						SELECT COUNT(*) FROM relationship rel
						INNER JOIN relationship_type relT ON relT.relationship_type_id = rel.relationship
						WHERE relT.uuid = '2f7d5778-0c80-11eb-b335-9f16b42e3b00' AND rel.person_a = pa.patient_id
					) >0 THEN "POSITIVO"
				 ELSE "DESCONHECIDO"
                 END
    ) AS _ESTADO_HIV_DO_PARCEIRO,
    (
		-- Data da consulta de PrEP
		SELECT 
			e.encounter_datetime	AS _data_consulta_prep
		FROM encounter e
		WHERE e.encounter_id = (
				SELECT ec.encounter_id FROM encounter ec
				WHERE ec.encounter_type=80 AND ec.patient_id=pa.patient_id
				AND ec.encounter_id  IN (
						-- consulta com resposta do grupo de analise
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165287,1982,6332,1377,1901)
						AND obs.concept_id IN (165196,23703)
				)  AND ec.encounter_id IN (
						-- Consulta que levantou pelo menos 1 regime de medicamento
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165214,165215,165216)
						AND obs.concept_id = 165213
			)
				ORDER BY ec.encounter_datetime ASC limit 0,1
           -- END da Primeira consulta que satisfaz os criterios exigidos
        )
	) AS _DATA_CONSULTA_INICAL_PREP,
    (
		-- Data de inicio de PrEP
		SELECT 
			obs.obs_datetime
		FROM obs
		WHERE obs.voided=0 AND obs.encounter_id = (
				SELECT ec.encounter_id FROM encounter ec
				WHERE ec.encounter_type=80 AND ec.patient_id=pa.patient_id
				AND ec.encounter_id  IN (
						-- consulta com resposta do grupo de analise
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165287,1982,6332,1377,1901)
						AND obs.concept_id IN (165196,23703)
				)  AND ec.encounter_id IN (
						-- Consulta que levantou pelo menos 1 regime de medicamento
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165214,165215,165216)
						AND obs.concept_id = 165213
			)
				ORDER BY ec.encounter_datetime ASC limit 0,1
           -- END da Primeira consulta que satisfaz os criterios exigidos
        ) 
        AND obs.value_coded=1256
        AND obs.concept_id=165296
	) AS _DATA_INICIO_PREP,
    (
		-- Data de Re-inicio de PrEP
		SELECT 
			obs.obs_datetime	AS _data_reinicio_prep
		FROM obs
		WHERE obs.voided=0 AND obs.encounter_id = (
				SELECT ec.encounter_id FROM encounter ec
				WHERE ec.encounter_type=80 AND ec.patient_id=pa.patient_id
				AND ec.encounter_id  IN (
						-- consulta com resposta do grupo de analise
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165287,1982,6332,1377,1901)
						AND obs.concept_id IN (165196,23703)
				)  AND ec.encounter_id IN (
						-- Consulta que levantou pelo menos 1 regime de medicamento
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165214,165215,165216)
						AND obs.concept_id = 165213
			)
				ORDER BY ec.encounter_datetime ASC limit 0,1
           -- END da Primeira consulta que satisfaz os criterios exigidos
        ) 
        AND obs.value_coded=1705
        AND obs.concept_id=165296
	) AS _DATA_REINICIO_PREP,
    (
		-- Data do Teste de HIV Negativo
		SELECT 
			obs.value_datetime	AS _data_teste_hiv
		FROM obs
		WHERE obs.voided=0 AND obs.encounter_id = (
				SELECT ec.encounter_id FROM encounter ec
				WHERE ec.encounter_type=80 AND ec.patient_id=pa.patient_id
				AND ec.encounter_id  IN (
						-- consulta com resposta do grupo de analise
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165287,1982,6332,1377,1901)
						AND obs.concept_id IN (165196,23703)
				)  AND ec.encounter_id IN (
						-- Consulta que levantou pelo menos 1 regime de medicamento
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165214,165215,165216)
						AND obs.concept_id = 165213
			)
				ORDER BY ec.encounter_datetime ASC limit 0,1
           -- END da Primeira consulta que satisfaz os criterios exigidos
        )
        AND obs.concept_id=165194
	) AS _DATA_TESTE_HIV_NEGATIVO,
    (
		-- Data de levantamento e a mesma que a da consulta de inicio de PrEP se tiver levantado no minimo 1 medicamento nos 3 regimes
		SELECT 
			obs.obs_datetime
		FROM obs
		WHERE obs.voided=0 AND obs.encounter_id = (
				SELECT ec.encounter_id FROM encounter ec
				WHERE ec.encounter_type=80 AND ec.patient_id=pa.patient_id
				AND ec.encounter_id  IN (
						-- consulta com resposta do grupo de analise
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165287,1982,6332,1377,1901)
						AND obs.concept_id IN (165196,23703)
				)  AND ec.encounter_id IN (
						-- Consulta que levantou pelo menos 1 regime de medicamento
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165214,165215,165216)
						AND obs.concept_id = 165213
			)
				ORDER BY ec.encounter_datetime ASC limit 0,1
           -- END da Primeira consulta que satisfaz os criterios exigidos
        ) 
        AND obs.value_coded=1256
        AND obs.concept_id=165296
	) AS _DATA_LEVANTAMENTO_ARV,
    (
		-- Numero de frascos levantados TDF_3TC
		SELECT 
			CASE WHEN obs_out.value_coded = 165214 THEN 
				CAST(obs_out.comments as signed integer)
			ELSE 0
			END
        FROM obs obs_out
		WHERE obs_out.voided=0 AND obs_out.encounter_id = (
				SELECT ec.encounter_id FROM encounter ec
				WHERE ec.encounter_type=80 AND ec.patient_id=pa.patient_id
				AND ec.encounter_id  IN (
						-- consulta com resposta do grupo de analise
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165287,1982,6332,1377,1901)
						AND obs.concept_id IN (165196,23703)
				)  AND ec.encounter_id IN (
						-- Consulta que levantou pelo menos 1 regime de medicamento
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165214,165215,165216)
						AND obs.concept_id = 165213
			)
				ORDER BY ec.encounter_datetime ASC limit 0,1
           -- END da Primeira consulta que satisfaz os criterios exigidos
		) 
        AND obs_out.value_coded = 165214
        AND obs_out.concept_id=165213
           
    ) AS _NUMERO_FRASCOS_LEVANTADOS_TDF_3TC,
    (
		-- Numero de frascos levantados TDF/FTC
		SELECT 
			CASE WHEN obs_out.value_coded = 165215 THEN 
				CAST(obs_out.comments as signed integer)
			ELSE 0
			END
        FROM obs obs_out
		WHERE obs_out.voided=0 AND obs_out.encounter_id = (
				SELECT ec.encounter_id FROM encounter ec
				WHERE ec.encounter_type=80 AND ec.patient_id=pa.patient_id
				AND ec.encounter_id  IN (
						-- consulta com resposta do grupo de analise
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165287,1982,6332,1377,1901)
						AND obs.concept_id IN (165196,23703)
				)  AND ec.encounter_id IN (
						-- Consulta que levantou pelo menos 1 regime de medicamento
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165214,165215,165216)
						AND obs.concept_id = 165213
			)
				ORDER BY ec.encounter_datetime ASC limit 0,1
           -- END da Primeira consulta que satisfaz os criterios exigidos
		) 
        AND obs_out.value_coded = 165215
        AND obs_out.concept_id = 165213
           
    ) AS _NUMERO_FRASCOS_LEVANTADOS_TDF_FTC,
    (
		-- Numero de frascos levantados OUTRO
		SELECT 
			CASE WHEN obs_out.value_coded = 165216 THEN 
				(SELECT 
						obs_reg_3.value_numeric AS _numero_frascos_regime_outro
					FROM obs obs_reg_3
					WHERE obs_reg_3.voided=0 AND obs_reg_3.encounter_id = (
						SELECT ec.encounter_id FROM encounter ec
							WHERE ec.encounter_type=80 AND ec.patient_id=pa.patient_id
							AND ec.encounter_id  IN (
							-- consulta com resposta do grupo de analise
							SELECT enc.encounter_id FROM encounter enc 
							INNER JOIN obs on obs.encounter_id = enc.encounter_id
							WHERE enc.patient_id = pa.patient_id
							AND enc.encounter_type=80 
							AND enc.encounter_datetime between :startDate AND :endDate  
							AND enc.location_id =:location 
							AND enc.voided=0
							AND obs.voided=0
							AND obs.value_coded IN (165287,1982,6332,1377,1901)
							AND obs.concept_id IN (165196,23703)
					)  AND ec.encounter_id IN (
							-- Consulta que levantou pelo menos 1 regime de medicamento
							SELECT enc.encounter_id FROM encounter enc 
							INNER JOIN obs on obs.encounter_id = enc.encounter_id
							WHERE enc.patient_id = pa.patient_id
							AND enc.encounter_type=80 
							AND enc.encounter_datetime between :startDate AND :endDate  
							AND enc.location_id =:location 
							AND enc.voided=0
							AND obs.voided=0
							AND obs.value_coded IN (165214,165215,165216)
							AND obs.concept_id = 165213
				)
				ORDER BY ec.encounter_datetime ASC limit 0,1
				-- END da Primeira consulta que satisfaz os criterios exigidos
			) 
			AND obs_reg_3.concept_id = 165217)
			ELSE 0
			END
        FROM obs obs_out
		WHERE obs_out.voided=0 AND obs_out.encounter_id = (
				SELECT ec.encounter_id FROM encounter ec
				WHERE ec.encounter_type=80 AND ec.patient_id=pa.patient_id
				AND ec.encounter_id  IN (
						-- consulta com resposta do grupo de analise
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165287,1982,6332,1377,1901)
						AND obs.concept_id IN (165196,23703)
				) AND ec.encounter_id IN (
						-- Consulta que levantou pelo menos 1 regime de medicamento
						SELECT enc.encounter_id FROM encounter enc 
						INNER JOIN obs on obs.encounter_id = enc.encounter_id
						WHERE enc.patient_id = pa.patient_id
						AND enc.encounter_type=80 
						AND enc.encounter_datetime between :startDate AND :endDate  
						AND enc.location_id =:location 
						AND enc.voided=0
						AND obs.voided=0
						AND obs.value_coded IN (165214,165215,165216)
						AND obs.concept_id = 165213
			)
				ORDER BY ec.encounter_datetime ASC limit 0,1
           -- END da Primeira consulta que satisfaz os criterios exigidos
        ) 
        AND obs_out.value_coded = 165216
        AND obs_out.concept_id = 165213
           
    ) AS _NUMERO_FRASCOS_LEVANTADOS_OUTRO,
    -- Colunas da consulta de seguimento Numero 1
    (
		SELECT ec.encounter_datetime FROM encounter ec WHERE ec.encounter_id = (
						SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 0,1
		)
    ) AS _VISITA_1_DATA_CONSULTA_PREP,
    (
		SELECT obs_s.obs_datetime FROM obs obs_s WHERE obs_s.encounter_id = (
				SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 0,1
		) AND obs_s.concept_id = 1040 AND obs_s.voided=0
    ) AS _VISITA_1_DATA_TESTE_HIV,
    (
		SELECT 
			CASE
			WHEN obs_s.value_coded = 664 THEN "NEGATIVO"
			WHEN obs_s.value_coded = 703 THEN "POSITIVO"
			WHEN obs_s.value_coded = 1138 THEN "INDETERMINADO"
			ELSE obs_s.value_coded
			END _resultado
        FROM obs obs_s WHERE obs_s.encounter_id = (
				SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 0,1
		) AND obs_s.concept_id = 1040  AND obs_s.voided=0
    ) AS _VISITA_1_RESULTADO_TESTE_HIV,
    (
		SELECT ec.encounter_datetime FROM encounter ec 
        INNER JOIN obs obs_s ON obs_s.encounter_id = ec.encounter_id
        WHERE ec.encounter_id = (
				SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 0,1
		) AND obs_s.concept_id = 165213 AND obs_s.value_coded IN (165214,165215,165224) AND obs_s.voided=0
    ) AS _VISITA_1_DATA_LVT_ARV,
    (
		SELECT obs_s.value_numeric FROM encounter ec 
        INNER JOIN obs obs_s ON obs_s.encounter_id = ec.encounter_id
        WHERE ec.encounter_id = (
				SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 0,1
		) AND obs_s.concept_id = 165217  AND obs_s.voided=0
    ) AS _VISITA_1_NUMERO_FRASCOS,
    (
		SELECT obs_s.value_datetime FROM encounter ec 
        INNER JOIN obs obs_s ON obs_s.encounter_id = ec.encounter_id
        WHERE ec.encounter_id = (
				SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 0,1
		) AND obs_s.concept_id = 165228  AND obs_s.voided=0
    ) AS _VISITA_1_DATA_PROXIMA_CONSULTA_MARCADA,
    -- Colunas da consulta de seguimento Numero 2
    (
		SELECT ec.encounter_datetime FROM encounter ec WHERE ec.encounter_id = (
						SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 1,1
		)
    ) AS _VISITA_2_DATA_CONSULTA_PREP,
    (
		SELECT obs_s.obs_datetime FROM obs obs_s WHERE obs_s.encounter_id = (
				SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 1,1
		) AND obs_s.concept_id = 1040 AND obs_s.voided=0
    ) AS _VISITA_2_DATA_TESTE_HIV,
    (
		SELECT 
			CASE
			WHEN obs_s.value_coded = 664 THEN "NEGATIVO"
			WHEN obs_s.value_coded = 703 THEN "POSITIVO"
			WHEN obs_s.value_coded = 1138 THEN "INDETERMINADO"
			ELSE obs_s.value_coded
			END _resultado
        FROM obs obs_s WHERE obs_s.encounter_id = (
				SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 1,1
		) AND obs_s.concept_id = 1040  AND obs_s.voided=0
    ) AS _VISITA_2_RESULTADO_TESTE_HIV,
    (
		SELECT ec.encounter_datetime FROM encounter ec 
        INNER JOIN obs obs_s ON obs_s.encounter_id = ec.encounter_id
        WHERE ec.encounter_id = (
				SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 1,1
		) AND obs_s.concept_id = 165213 AND obs_s.value_coded IN (165214,165215,165224) AND obs_s.voided=0
    ) AS _VISITA_2_DATA_LVT_ARV,
    (
		SELECT obs_s.value_numeric FROM encounter ec 
        INNER JOIN obs obs_s ON obs_s.encounter_id = ec.encounter_id
        WHERE ec.encounter_id = (
				SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 1,1
		) AND obs_s.concept_id = 165217  AND obs_s.voided=0
    ) AS _VISITA_2_NUMERO_FRASCOS,
    (
		SELECT obs_s.value_datetime FROM encounter ec 
        INNER JOIN obs obs_s ON obs_s.encounter_id = ec.encounter_id
        WHERE ec.encounter_id = (
				SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 1,1
		) AND obs_s.concept_id = 165228  AND obs_s.voided=0
    ) AS _VISITA_2_DATA_PROXIMA_CONSULTA_MARCADA,
     -- Colunas da consulta de seguimento Numero 3
    (
		SELECT ec.encounter_datetime FROM encounter ec WHERE ec.encounter_id = (
						SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 2,1
		)
    ) AS _VISITA_3_DATA_CONSULTA_PREP,
    (
		SELECT obs_s.obs_datetime FROM obs obs_s WHERE obs_s.encounter_id = (
				SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 2,1
		) AND obs_s.concept_id = 1040 AND obs_s.voided=0
    ) AS _VISITA_3_DATA_TESTE_HIV,
    (
		SELECT 
			CASE
			WHEN obs_s.value_coded = 664 THEN "NEGATIVO"
			WHEN obs_s.value_coded = 703 THEN "POSITIVO"
			WHEN obs_s.value_coded = 1138 THEN "INDETERMINADO"
			ELSE obs_s.value_coded
			END _resultado
        FROM obs obs_s WHERE obs_s.encounter_id = (
				SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 2,1
		) AND obs_s.concept_id = 1040  AND obs_s.voided=0
    ) AS _VISITA_3_RESULTADO_TESTE_HIV,
    (
		SELECT ec.encounter_datetime FROM encounter ec 
        INNER JOIN obs obs_s ON obs_s.encounter_id = ec.encounter_id
        WHERE ec.encounter_id = (
				SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 2,1
		) AND obs_s.concept_id = 165213 AND obs_s.value_coded IN (165214,165215,165224) AND obs_s.voided=0
    ) AS _VISITA_3_DATA_LVT_ARV,
    (
		SELECT obs_s.value_numeric FROM encounter ec 
        INNER JOIN obs obs_s ON obs_s.encounter_id = ec.encounter_id
        WHERE ec.encounter_id = (
				SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 2,1
		) AND obs_s.concept_id = 165217  AND obs_s.voided=0
    ) AS _VISITA_3_NUMERO_FRASCOS,
    (
		SELECT obs_s.value_datetime FROM encounter ec 
        INNER JOIN obs obs_s ON obs_s.encounter_id = ec.encounter_id
        WHERE ec.encounter_id = (
				SELECT seg.encounter_id FROM encounter seg
						WHERE seg.voided=0 AND seg.encounter_type=81 AND seg.patient_id = pa.patient_id
                        AND seg.encounter_datetime between :startDate AND :endDate order by seg.encounter_datetime  ASC limit 2,1
		) AND obs_s.concept_id = 165228  AND obs_s.voided=0
    ) AS _VISITA_3_DATA_PROXIMA_CONSULTA_MARCADA,
    (
		SELECT MIN(tabela_interrupccao.data_int) FROM (
			select 	p.patient_id, min(obsScreen.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
         					e.encounter_datetime between :startDate and :endDate
         			group by p.patient_id
                    union
                     select 	p.patient_id, min(obs_data.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622)  and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id 
			) AS tabela_interrupccao WHERE tabela_interrupccao.patient_id=pa.patient_id group by tabela_interrupccao.patient_id
    ) AS _DATA_PREP_INTERROMPIDA,
    (select "INFECTADO COM HIV"  from (
           
           select seguimento.patient_id, seguimento.data_int from 
           
			( select 	p.patient_id, min(obsScreen.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
         					e.encounter_datetime between :startDate and :endDate
         			group by p.patient_id ) interno inner join (  
									select 	p.patient_id, min(obsScreen.obs_datetime) data_int
									from 	patient p				
									inner join encounter e on p.patient_id=e.patient_id
									inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
									obsScreen.value_coded =1169 and  obsScreen.voided=0
									where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
										e.encounter_datetime between :startDate and :endDate
										group by p.patient_id) seguimento on seguimento.patient_id = interno.patient_id
												where seguimento.data_int = interno.data_int
    
				union
					select xx.patient_id,  xx.data_int from (
							select 	p.patient_id, min(obs_data.obs_datetime) data_int
							from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id ) interrup inner join (
                    
                    select 	p.patient_id, min(obs_data.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded =1169 and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id 
                    ) xx on interrup.patient_id = xx.patient_id where interrup.data_int = xx.data_int 

           ) interrupa inner join ( 		
				select 		dentro.patient_id, min(dentro.data_int) data_int from (
			select 	p.patient_id, min(obsScreen.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
         					e.encounter_datetime between :startDate and :endDate
         			group by p.patient_id
                    union
                     select 	p.patient_id, min(obs_data.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622)  and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id ) dentro group by dentro.patient_id
			) tabela_interrupccao on tabela_interrupccao.patient_id = interrupa.patient_id
					where  interrupa.patient_id = pa.patient_id and  tabela_interrupccao.data_int = interrupa.data_int  
                    group by pa.patient_id
    ) AS _MOTIVO_INFECTADO_HIV,
    (select "EFEITOS SECUNDARIOS ARV"  from (
           
           select seguimento.patient_id, seguimento.data_int from 
           
			( select 	p.patient_id, min(obsScreen.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
         					e.encounter_datetime between :startDate and :endDate
         			group by p.patient_id ) interno inner join (  
									select 	p.patient_id, min(obsScreen.obs_datetime) data_int
									from 	patient p				
									inner join encounter e on p.patient_id=e.patient_id
									inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
									obsScreen.value_coded =2015 and  obsScreen.voided=0
									where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
										e.encounter_datetime between :startDate and :endDate
										group by p.patient_id) seguimento on seguimento.patient_id = interno.patient_id
												where seguimento.data_int = interno.data_int
    
				union
					select xx.patient_id,  xx.data_int from (
							select 	p.patient_id, min(obs_data.obs_datetime) data_int
							from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id ) interrup inner join (
                    
                    select 	p.patient_id, min(obs_data.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded =2015 and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id 
                    ) xx on interrup.patient_id = xx.patient_id where interrup.data_int = xx.data_int 

           ) interrupa inner join ( 		
				select 		dentro.patient_id, min(dentro.data_int) data_int from (
			select 	p.patient_id, min(obsScreen.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
         					e.encounter_datetime between :startDate and :endDate
         			group by p.patient_id
                    union
                     select 	p.patient_id, min(obs_data.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622)  and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id ) dentro group by dentro.patient_id
			) tabela_interrupccao on tabela_interrupccao.patient_id = interrupa.patient_id
					where  interrupa.patient_id = pa.patient_id and  tabela_interrupccao.data_int = interrupa.data_int  
                    group by pa.patient_id
    ) AS _MOTIVO_EFEITO_SECUNDARIO,
    (select "SEM MAIS RISCOS SUBSTANCIAIS"  from (
           
           select seguimento.patient_id, seguimento.data_int from 
           
			( select 	p.patient_id, min(obsScreen.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
         					e.encounter_datetime between :startDate and :endDate
         			group by p.patient_id ) interno inner join (  
									select 	p.patient_id, min(obsScreen.obs_datetime) data_int
									from 	patient p				
									inner join encounter e on p.patient_id=e.patient_id
									inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
									obsScreen.value_coded =165226 and  obsScreen.voided=0
									where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
										e.encounter_datetime between :startDate and :endDate
										group by p.patient_id) seguimento on seguimento.patient_id = interno.patient_id
												where seguimento.data_int = interno.data_int
    
				union
					select xx.patient_id,  xx.data_int from (
							select 	p.patient_id, min(obs_data.obs_datetime) data_int
							from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id ) interrup inner join (
                    
                    select 	p.patient_id, min(obs_data.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded =165226 and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id 
                    ) xx on interrup.patient_id = xx.patient_id where interrup.data_int = xx.data_int 

           ) interrupa inner join ( 		
				select 		dentro.patient_id, min(dentro.data_int) data_int from (
			select 	p.patient_id, min(obsScreen.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
         					e.encounter_datetime between :startDate and :endDate
         			group by p.patient_id
                    union
                     select 	p.patient_id, min(obs_data.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622)  and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id ) dentro group by dentro.patient_id
			) tabela_interrupccao on tabela_interrupccao.patient_id = interrupa.patient_id
					where  interrupa.patient_id = pa.patient_id and  tabela_interrupccao.data_int = interrupa.data_int  
                    group by pa.patient_id
    ) AS _MOTIVO_SEM_MAIS_RISCO,
    (select "PREFERENCIA DO UTENTE"  from (
           
           select seguimento.patient_id, seguimento.data_int from 
           
			( select 	p.patient_id, min(obsScreen.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
         					e.encounter_datetime between :startDate and :endDate
         			group by p.patient_id ) interno inner join (  
									select 	p.patient_id, min(obsScreen.obs_datetime) data_int
									from 	patient p				
									inner join encounter e on p.patient_id=e.patient_id
									inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
									obsScreen.value_coded =165227 and  obsScreen.voided=0
									where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
										e.encounter_datetime between :startDate and :endDate
										group by p.patient_id) seguimento on seguimento.patient_id = interno.patient_id
												where seguimento.data_int = interno.data_int
    
				union
					select xx.patient_id,  xx.data_int from (
							select 	p.patient_id, min(obs_data.obs_datetime) data_int
							from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id ) interrup inner join (
                    
                    select 	p.patient_id, min(obs_data.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded =165227 and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id 
                    ) xx on interrup.patient_id = xx.patient_id where interrup.data_int = xx.data_int 

           ) interrupa inner join ( 		
				select 		dentro.patient_id, min(dentro.data_int) data_int from (
			select 	p.patient_id, min(obsScreen.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
         					e.encounter_datetime between :startDate and :endDate
         			group by p.patient_id
                    union
                     select 	p.patient_id, min(obs_data.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622)  and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id ) dentro group by dentro.patient_id
			) tabela_interrupccao on tabela_interrupccao.patient_id = interrupa.patient_id
					where  interrupa.patient_id = pa.patient_id and  tabela_interrupccao.data_int = interrupa.data_int  
                    group by pa.patient_id
    ) AS _MOTIVO_PREFERENCIA_UTENTE,
    (select "OUTRO"  from (
           
           select seguimento.patient_id, seguimento.data_int from 
           
			( select 	p.patient_id, min(obsScreen.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
         					e.encounter_datetime between :startDate and :endDate
         			group by p.patient_id ) interno inner join (  
									select 	p.patient_id, min(obsScreen.obs_datetime) data_int
									from 	patient p				
									inner join encounter e on p.patient_id=e.patient_id
									inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
									obsScreen.value_coded =5622 and  obsScreen.voided=0
									where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
										e.encounter_datetime between :startDate and :endDate
										group by p.patient_id) seguimento on seguimento.patient_id = interno.patient_id
												where seguimento.data_int = interno.data_int
    
				union
					select xx.patient_id,  xx.data_int from (
							select 	p.patient_id, min(obs_data.obs_datetime) data_int
							from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id ) interrup inner join (
                    
                    select 	p.patient_id, min(obs_data.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded =5622 and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id 
                    ) xx on interrup.patient_id = xx.patient_id where interrup.data_int = xx.data_int 

           ) interrupa inner join ( 		
				select 		dentro.patient_id, min(dentro.data_int) data_int from (
			select 	p.patient_id, min(obsScreen.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id and obsScreen.concept_id=165225 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622) and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =81 and e.location_id=:location and
         					e.encounter_datetime between :startDate and :endDate
         			group by p.patient_id
                    union
                     select 	p.patient_id, min(obs_data.obs_datetime) data_int
         			from 	patient p				
         					inner join encounter e on p.patient_id=e.patient_id
         					inner join obs obsScreen on obsScreen.encounter_id=e.encounter_id
                            inner join obs obs_data on obs_data.encounter_id = e.encounter_id
                            and obsScreen.concept_id=165225 and obs_data.concept_id = 165292 and obs_data.value_coded = 1260 and
                             obsScreen.value_coded in (1169,2015,165226,165227,5622)  and  obsScreen.voided=0
         			where 	e.voided=0 and p.voided=0 and e.encounter_type =80 and e.location_id=:location and
         					obs_data.obs_datetime between :startDate and :endDate
         			group by p.patient_id ) dentro group by dentro.patient_id
			) tabela_interrupccao on tabela_interrupccao.patient_id = interrupa.patient_id
					where  interrupa.patient_id = pa.patient_id and  tabela_interrupccao.data_int = interrupa.data_int  
                    group by pa.patient_id
    ) AS _MOTIVO_OUTRO
    
FROM patient pa
INNER JOIN person pe on pe.person_id = pa.patient_id
LEFT JOIN 
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
           	) pid on pid.patient_id=pa.patient_id
			
where pa.patient_id IN (
	-- Pacientes que Pertencem ao grupo de analise
	SELECT enc.patient_id FROM encounter enc 
    INNER JOIN obs on obs.encounter_id = enc.encounter_id
    WHERE 
		enc.encounter_type=80 
		AND enc.encounter_datetime between :startDate AND :endDate  
        AND enc.location_id =:location 
        AND enc.voided=0
        AND obs.voided=0
		AND obs.concept_id IN (165196,23703)
        AND obs.value_coded IN (165287,1982,6332,1377,1901)
)  AND pa.patient_id IN (
	-- Pacientes que Levantaram pelo menos 1 regime de medicamento
	SELECT enc.patient_id FROM encounter enc 
    INNER JOIN obs on obs.encounter_id = enc.encounter_id
    WHERE 
		enc.encounter_type=80 
		AND enc.encounter_datetime between :startDate AND :endDate 
        AND enc.location_id =:location 
        AND enc.voided=0
        AND obs.voided=0
        AND obs.value_coded IN (165214,165215,165216)
        AND obs.concept_id = 165213
) 
