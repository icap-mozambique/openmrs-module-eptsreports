
SELECT p.patient_id, SHA2(p.patient_id, 256) AS encrypted_id, pi.identifier, CONCAT(pn.given_name, ' ', COALESCE(pn.middle_name, ''), pn.family_name) name, pe.gender, (YEAR( :endDate) - YEAR(pe.birthdate)) age, 
       art_start.data_inicio,
       sep_encounter_2021_date, oct_encounter_2021_date, nov_encounter_2021_date, dec_encounter_2021_date,
       jan_encounter_date, feb_encounter_date, mar_encounter_date, apr_encounter_date, may_encounter_date, jun_encounter_date, 
       jul_encounter_date, aug_encounter_date, sep_encounter_date, oct_encounter_date, nov_encounter_date, dec_encounter_date,
       jan_encounter_2023_date, feb_encounter_2023_date, mar_encounter_2023_date, apr_encounter_2023_date, may_encounter_2023_date,
       jun_encounter_2023_date, jul_encounter_2023_date FROM patient p
	INNER JOIN patient_identifier pi ON p.patient_id = pi.patient_id
	INNER JOIN person_name pn ON pn.person_id = p.patient_id
	INNER JOIN person pe ON pe.person_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_2021_date.patient_id, MAX(last_encounter_2021_date) sep_encounter_2021_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_2021_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 9 AND YEAR(e.encounter_datetime) = 2021 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) sep_encounter_2021_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 9 AND YEAR(o.value_datetime) = 2021 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_2021_date GROUP BY patient_id 
	)sep_encounter_2021 ON sep_encounter_2021.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_2021_date.patient_id, MAX(last_encounter_2021_date) oct_encounter_2021_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_2021_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 10 AND YEAR(e.encounter_datetime) = 2021 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) oct_encounter_2021_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 10 AND YEAR(o.value_datetime) = 2021 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_2021_date GROUP BY patient_id 
	)oct_encounter_2021 ON oct_encounter_2021.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_2021_date.patient_id, MAX(last_encounter_2021_date) nov_encounter_2021_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_2021_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 11 AND YEAR(e.encounter_datetime) = 2021 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) nov_encounter_2021_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 11 AND YEAR(o.value_datetime) = 2021 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_2021_date GROUP BY patient_id 
	)nov_encounter_2021 ON nov_encounter_2021.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_2021_date.patient_id, MAX(last_encounter_2021_date) dec_encounter_2021_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_2021_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 12 AND YEAR(e.encounter_datetime) = 2021 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) dec_encounter_2021_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 12 AND YEAR(o.value_datetime) = 2021 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_2021_date GROUP BY patient_id 
	)dec_encounter_2021 ON dec_encounter_2021.patient_id = p.patient_id
	LEFT JOIN 
	(
        SELECT max_encounter_date.patient_id, MAX(last_encounter_date) jan_encounter_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 1 AND YEAR(e.encounter_datetime) = 2022 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) jan_encounter_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 1 AND YEAR(o.value_datetime) = 2022 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_date GROUP BY patient_id
	)jan_encounter ON jan_encounter.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_date.patient_id, MAX(last_encounter_date) feb_encounter_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 2 AND YEAR(e.encounter_datetime) = 2022 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) feb_encounter_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 2 AND YEAR(o.value_datetime) = 2022 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_date GROUP BY patient_id 
	)feb_encounter ON feb_encounter.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_date.patient_id, MAX(last_encounter_date) mar_encounter_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 3 AND YEAR(e.encounter_datetime) = 2022 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) mar_encounter_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 3 AND YEAR(o.value_datetime) = 2022 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_date GROUP BY patient_id 
	)mar_encounter ON mar_encounter.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_date.patient_id, MAX(last_encounter_date) apr_encounter_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 4 AND YEAR(e.encounter_datetime) = 2022 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) apr_encounter_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 4 AND YEAR(o.value_datetime) = 2022 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_date GROUP BY patient_id 
	)apr_encounter ON apr_encounter.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_date.patient_id, MAX(last_encounter_date) may_encounter_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 5 AND YEAR(e.encounter_datetime) = 2022 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) may_encounter_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 5 AND YEAR(o.value_datetime) = 2022 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_date GROUP BY patient_id 
	)may_encounter ON may_encounter.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_date.patient_id, MAX(last_encounter_date) jun_encounter_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 6 AND YEAR(e.encounter_datetime) = 2022 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) jun_encounter_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 6 AND YEAR(o.value_datetime) = 2022 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_date GROUP BY patient_id 
	)jun_encounter ON jun_encounter.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_date.patient_id, MAX(last_encounter_date) jul_encounter_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 7 AND YEAR(e.encounter_datetime) = 2022 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) jul_encounter_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 7 AND YEAR(o.value_datetime) = 2022 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_date GROUP BY patient_id 
	)jul_encounter ON jul_encounter.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_date.patient_id, MAX(last_encounter_date) aug_encounter_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 6 AND YEAR(e.encounter_datetime) = 2022 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) aug_encounter_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 8 AND YEAR(o.value_datetime) = 2022 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_date GROUP BY patient_id 
	)aug_encounter ON aug_encounter.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_date.patient_id, MAX(last_encounter_date) sep_encounter_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 9 AND YEAR(e.encounter_datetime) = 2022 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) sep_encounter_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 9 AND YEAR(o.value_datetime) = 2022 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_date GROUP BY patient_id 
	)sep_encounter ON sep_encounter.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_date.patient_id, MAX(last_encounter_date) oct_encounter_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 10 AND YEAR(e.encounter_datetime) = 2022 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) oct_encounter_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 10 AND YEAR(o.value_datetime) = 2022 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_date GROUP BY patient_id 
	)oct_encounter ON oct_encounter.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_date.patient_id, MAX(last_encounter_date) nov_encounter_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 11 AND YEAR(e.encounter_datetime) = 2022 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) nov_encounter_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 11 AND YEAR(o.value_datetime) = 2022 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_date GROUP BY patient_id 
	)nov_encounter ON nov_encounter.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_date.patient_id, MAX(last_encounter_date) dec_encounter_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 12 AND YEAR(e.encounter_datetime) = 2022 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) dec_encounter_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 12 AND YEAR(o.value_datetime) = 2022 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_date GROUP BY patient_id 
	)dec_encounter ON dec_encounter.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_2023_date.patient_id, MAX(last_encounter_2023_date) jan_encounter_2023_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_2023_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 1 AND YEAR(e.encounter_datetime) = 2023 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) jan_encounter_2023_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 1 AND YEAR(o.value_datetime) = 2023 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_2023_date GROUP BY patient_id
	)jan_encounter_2023 ON jan_encounter_2023.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_2023_date.patient_id, MAX(last_encounter_2023_date) feb_encounter_2023_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_2023_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 2 AND YEAR(e.encounter_datetime) = 2023 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) feb_encounter_2023_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 2 AND YEAR(o.value_datetime) = 2023 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_2023_date GROUP BY patient_id 
	)feb_encounter_2023 ON feb_encounter_2023.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_2023_date.patient_id, MAX(last_encounter_2023_date) mar_encounter_2023_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_2023_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 3 AND YEAR(e.encounter_datetime) = 2023 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) mar_encounter_2023_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 3 AND YEAR(o.value_datetime) = 2023 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_2023_date GROUP BY patient_id 
	)mar_encounter_2023 ON mar_encounter_2023.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_2023_date.patient_id, MAX(last_encounter_2023_date) apr_encounter_2023_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_2023_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 4 AND YEAR(e.encounter_datetime) = 2023 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) apr_encounter_2023_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 4 AND YEAR(o.value_datetime) = 2023 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_2023_date GROUP BY patient_id 
	)apr_encounter_2023 ON apr_encounter_2023.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_2023_date.patient_id, MAX(last_encounter_2023_date) may_encounter_2023_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_2023_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 5 AND YEAR(e.encounter_datetime) = 2023 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) may_encounter_2023_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 5 AND YEAR(o.value_datetime) = 2023 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_2023_date GROUP BY patient_id 
	)may_encounter_2023 ON may_encounter_2023.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_2023_date.patient_id, MAX(last_encounter_2023_date) jun_encounter_2023_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_2023_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 6 AND YEAR(e.encounter_datetime) = 2023 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) jun_encounter_2023_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 6 AND YEAR(o.value_datetime) = 2023 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_2023_date GROUP BY patient_id
	)jun_encounter_2023 ON jun_encounter_2023.patient_id = p.patient_id
    LEFT JOIN 
	(
        SELECT max_encounter_2023_date.patient_id, MAX(last_encounter_2023_date) jul_encounter_2023_date FROM
            (
                SELECT p.patient_id, MAX(e.encounter_datetime) last_encounter_2023_date FROM patient p
                    INNER JOIN encounter e ON p.patient_id=e.patient_id
                            WHERE p.voided= 0 AND e.voided= 0 AND e.encounter_type in (18,6,9,53) 
                                AND MONTH(e.encounter_datetime) = 7 AND YEAR(e.encounter_datetime) = 2023 AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate AND e.location_id = :location
                                    GROUP BY p.patient_id
                UNION
                
                SELECT o.person_id patient_id, MAX(o.value_datetime) jul_encounter_2023_date FROM obs o
                                INNER JOIN encounter e ON e.encounter_id = o.encounter_id
                                    WHERE o.concept_id = 23866 AND e.encounter_type = 52 
                                    AND o.voided = 0 AND e.voided = 0 
                                    AND MONTH(o.value_datetime) = 7 AND YEAR(o.value_datetime) = 2023 AND o.value_datetime >= :startDate AND o.value_datetime <= :endDate AND e.location_id = :location
                                        GROUP BY o.person_id
            )max_encounter_2023_date GROUP BY patient_id
	)jul_encounter_2023 ON jul_encounter_2023.patient_id = p.patient_id
    LEFT JOIN 
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
                    and e.encounter_datetime<= :endDate and e.location_id = :location
                    group by p.patient_id     
            union   
            select  p.patient_id,min(value_datetime) data_inicio          
            from patient p
                    inner join person pe on pe.person_id = p.patient_id   
                    inner join encounter e on p.patient_id=e.patient_id 
                    inner join obs o on e.encounter_id=o.encounter_id     
            where p.voided=0 and pe.voided = 0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53)           
                    and o.concept_id=1190 and o.value_datetime is not null 
                    and o.value_datetime<= :endDate and e.location_id = :location
                    group by p.patient_id     
            union   
            select  pg.patient_id,min(date_enrolled) data_inicio          
            from patient p         
                    inner join person pe on pe.person_id = p.patient_id       
                    inner join patient_program pg on p.patient_id=pg.patient_id   
            where   pg.voided=0 and p.voided=0 and pe.voided = 0 and program_id=2 and date_enrolled<= :endDate and location_id = :location
                    group by pg.patient_id    
            union   
            select e.patient_id, min(e.encounter_datetime) as data_inicio
            from patient p     
                    inner join person pe on pe.person_id = p.patient_id   
                    inner join encounter e on p.patient_id=e.patient_id   
            where p.voided=0 and pe.voided = 0 and e.encounter_type=18 and e.voided=0 and e.encounter_datetime<= :endDate and e.location_id = :location 
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
        ) inicio_real group by patient_id
    )art_start ON art_start.patient_id = p.patient_id
        WHERE p.patient_id IN ( :patientIds) GROUP BY p.patient_id