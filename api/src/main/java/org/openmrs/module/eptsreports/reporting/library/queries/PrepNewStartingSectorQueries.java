package org.openmrs.module.eptsreports.reporting.library.queries;

import org.openmrs.module.eptsreports.reporting.utils.PrepNewEligibilidadeSectorType;
import org.openmrs.module.eptsreports.reporting.utils.PrepNewEnrollemntStatus;
import org.openmrs.module.eptsreports.reporting.utils.PrepNewKeyPopType;

public interface PrepNewStartingSectorQueries {

  class QUERY {

    public static String findClientsNewlyEnrolledInPrepAtCPN(
        Integer sector, Integer conceitoKeyPop, PrepNewKeyPopType keyPop) {

      if (!keyPop.equals(PrepNewKeyPopType.OTHER)) {
        String query =
            "SELECT sector_inicio.patient_id\n"
                + "FROM\n"
                + "(select p.patient_id, max(encounter_datetime) data_sector_inicio\n"
                + "from patient p\n"
                + "inner join encounter e on p.patient_id=e.patient_id\n"
                + "inner join obs  o on e.encounter_id=o.encounter_id\n"
                + "where e.voided=0 and o.voided=0 and p.voided=0 and\n"
                + "e.encounter_type = 80 and o.concept_id = 165291 and o.value_coded ="
                + sector
                + " and  \n"
                + "o.obs_datetime<=:endDate and e.location_id=:location\n"
                + "group by p.patient_id) sector_inicio \n"
                + "\n"
                + "inner join \n"
                + "(\n"
                + "select p.patient_id, max(encounter_datetime) data_keypop\n"
                + "from patient p\n"
                + "inner join encounter e on p.patient_id=e.patient_id\n"
                + "inner join obs  o on e.encounter_id=o.encounter_id\n"
                + "where e.voided=0 and o.voided=0 and p.voided=0 and\n"
                + "e.encounter_type = 80 and o.concept_id ="
                + conceitoKeyPop
                + " and o.value_coded in (1903,6332,165287,1902,1908,1995,1982)"
                + " and o.obs_datetime<=:endDate and e.location_id=:location\n"
                + "group by p.patient_id ) keypop on sector_inicio.patient_id = keypop.patient_id and sector_inicio.data_sector_inicio=keypop.data_keypop  ";

        switch (keyPop) {
          case PREGNANT:
            query = query.replace("in (1903,6332,165287,1902,1908,1995,1982)", "=1982");
            break;

          case LACTATION:
            query = query.replace("in (1903,6332,165287,1902,1908,1995,1982)", "=6332");
            break;

          case ADOLESCENTS_YOUTH_RISK:
            query = query.replace("in (1903,6332,165287,1902,1908,1995,1982)", "=165287");
            break;

          case MILITARY:
            query = query.replace("in (1903,6332,165287,1902,1908,1995,1982)", "=1902");
            break;

          case MINER:
            query = query.replace("in (1903,6332,165287,1902,1908,1995,1982)", "=1908");
            break;

          case DRIVER:
            query = query.replace("in (1903,6332,165287,1902,1908,1995,1982)", "=1903");
            break;

          case CASAIS_SERODISCORDANTE:
            query = query.replace("in (1903,6332,165287,1902,1908,1995,1982)", "=1995");
            break;

          case HOMOSEXUAL:
            query = query.replace("in (1903,6332,165287,1902,1908,1995,1982)", "=1377");
            break;

          case PRISIONER:
            query = query.replace("in (1903,6332,165287,1902,1908,1995,1982)", "=20426");
            break;

          case SEXWORKER:
            query = query.replace("in (1903,6332,165287,1902,1908,1995,1982)", "=1901");
            break;

          case DRUGUSER:
            query = query.replace("in (1903,6332,165287,1902,1908,1995,1982)", "=20454");
            break;

          case TRANSGENDER:
            query = query.replace("in (1903,6332,165287,1902,1908,1995,1982)", "=165205");
            break;

          case SPECIAL_CASE:
            query = query.replace("in (1903,6332,165287,1902,1908,1995,1982)", "=1065");
            break;
        }

        return query;
      } else {
        String query =
            "SELECT sector_inicio.patient_id\n"
                + "FROM\n"
                + "(select p.patient_id, max(encounter_datetime) data_sector_inicio\n"
                + "from patient p\n"
                + "inner join encounter e on p.patient_id=e.patient_id\n"
                + "inner join obs  o on e.encounter_id=o.encounter_id\n"
                + "where e.voided=0 and o.voided=0 and p.voided=0 and\n"
                + "e.encounter_type = 80 and o.concept_id = 165291 and o.value_coded ="
                + sector
                + " and  \n"
                + "o.obs_datetime<=:endDate and e.location_id=:location\n"
                + "group by p.patient_id) sector_inicio \n";

        return query;
      }
    }

    public static String findClientsNewlyEnrolledInPrepbyEligibility(
        Integer sectorElegibilidade, PrepNewEligibilidadeSectorType keyPop) {

      String query =
          "   SELECT sector_elegivel.patient_id                                                                   	"
              + " 	 FROM                                                                                               "
              + "																									    "
              + "                                                               										"
              + " (select p.patient_id, min(encounter_datetime) data_sector_elegivel                                    "
              + " from patient p                                                                                        "
              + " inner join encounter e on p.patient_id=e.patient_id                                                   "
              + " inner join obs  o on e.encounter_id=o.encounter_id                                                    "
              + " where e.voided=0 and o.voided=0 and p.voided=0 and	                                                "
              + " e.encounter_type = 80 and o.concept_id = 23783 and o.value_coded ="
              + sectorElegibilidade
              + "  and        "
              + " o.obs_datetime between :startDate and :endDate and e.location_id=:location                            "
              + " group by p.patient_id) sector_elegivel                                                                "
              + "																									    "
              + " inner join                                                                                            "
              + " (                                                                                                     "
              + "                                                                										"
              + " select p.patient_id, min(encounter_datetime) data_keypop                                              "
              + " from patient p                                                                                        "
              + " inner join encounter e on p.patient_id=e.patient_id                                                   "
              + " inner join obs  o on e.encounter_id=o.encounter_id                                                    "
              + " where e.voided=0 and o.voided=0 and p.voided=0 and                                                    "
              + " e.encounter_type = 80 and o.concept_id = 165196 and o.value_coded =1995    and                        "
              + " o.obs_datetime between :startDate and :endDate and e.location_id=:location                            "
              + " group by p.patient_id  ) keypop on sector_elegivel.patient_id = keypop.patient_id                     "
              + " and sector_elegivel.data_sector_elegivel=keypop.data_keypop                                           "
              + " 																									    "
              + " 																									    "
              + " inner join                                                                                            "
              + " (                                                                                                     "
              + "                                                                             							"
              + " select p.patient_id, min(encounter_datetime) data_inicio                                              "
              + " from patient p                                                                                        "
              + " inner join encounter e on p.patient_id=e.patient_id                                                   "
              + " inner join obs  o on e.encounter_id=o.encounter_id                                                    "
              + " where e.voided=0 and o.voided=0 and p.voided=0 and                                                    "
              + " e.encounter_type = 80 and o.concept_id=165291 and o.value_coded=23913  and                            "
              + " o.obs_datetime between :startDate and :endDate and e.location_id=:location                            "
              + " group by p.patient_id) inicio on sector_elegivel.patient_id = inicio.patient_id                       "
              + "  and sector_elegivel.data_sector_elegivel=inicio.data_inicio                                          ";

      switch (keyPop) {
        case SAAJ:
          query = query.replace("23913", "1987");
          break;
        case TRIAGEM_ADULTO:
          query = query.replace("23913", "23873");
          break;
        case DOENCAS_CRONICAS:
          query = query.replace("23913", "165206");
          break;
        case CPN:
          query = query.replace("23913", "1978");
          break;
        case CPF:
          query = query.replace("23913", "5483");
          break;

        default:
          return query;
      }

      return query;
    }

    public static String findClientsNewlyEnrolledInPrepbyEligibility(Integer sectorElegibilidade) {
      String query =
          "   SELECT sector_elegivel.patient_id                                                                	"
              + " 	 FROM                                                                                               "
              + "																									    "
              + "                                                               										"
              + " (select p.patient_id, min(encounter_datetime) data_sector_elegivel                                    "
              + " from patient p                                                                                        "
              + " inner join encounter e on p.patient_id=e.patient_id                                                   "
              + " inner join obs  o on e.encounter_id=o.encounter_id                                                    "
              + " where e.voided=0 and o.voided=0 and p.voided=0 and	                                                "
              + " e.encounter_type = 80 and o.concept_id = 23783 and o.value_coded ="
              + sectorElegibilidade
              + "  and        "
              + " o.obs_datetime between :startDate and :endDate and e.location_id=:location                            "
              + " group by p.patient_id) sector_elegivel                                                                "
              + "																									    "
              + " inner join                                                                                            "
              + " (                                                                                                     "
              + "                                                                										"
              + " select p.patient_id, min(encounter_datetime) data_keypop                                              "
              + " from patient p                                                                                        "
              + " inner join encounter e on p.patient_id=e.patient_id                                                   "
              + " inner join obs  o on e.encounter_id=o.encounter_id                                                    "
              + " where e.voided=0 and o.voided=0 and p.voided=0 and                                                    "
              + " e.encounter_type = 80 and o.concept_id = 165196 and o.value_coded =1995    and                        "
              + " o.obs_datetime between :startDate and :endDate and e.location_id=:location                            "
              + " group by p.patient_id  ) keypop on sector_elegivel.patient_id = keypop.patient_id                     "
              + " and sector_elegivel.data_sector_elegivel=keypop.data_keypop                                           ";

      return query;
    }

    public static String findClientsNewlyEnrolledInPrepSeroDescordante() {
      String query =
          "   SELECT sector_elegivel.patient_id                                                                	"
              + " 	 FROM                                                                                           "
              + "																									    "
              + "                                                               										"
              + " (select p.patient_id, min(encounter_datetime) data_sector_elegivel                                  "
              + " from patient p                                                                                      "
              + " inner join encounter e on p.patient_id=e.patient_id                                                 "
              + " inner join obs  o on e.encounter_id=o.encounter_id                                                  "
              + " where e.voided=0 and o.voided=0 and p.voided=0 and	                                                "
              + " e.encounter_type = 80 and o.concept_id = 165196 and o.value_coded = 1995							"
              + "  and        																						"
              + " o.obs_datetime between :startDate and :endDate and e.location_id=:location                          "
              + " group by p.patient_id) sector_elegivel                                                              ";

      return query;
    }

    public static String findClientsbyEnrollmentStatus(
        Integer sectorElegibilidade, PrepNewEnrollemntStatus keyPop) {

      String query =
          "   SELECT sector_elegivel.patient_id                                                                   	"
              + " 	 FROM                                                                                               "
              + "																									    "
              + "                                                               										"
              + " (select p.patient_id, min(encounter_datetime) data_sector_elegivel                                    "
              + " from patient p                                                                                        "
              + " inner join encounter e on p.patient_id=e.patient_id                                                   "
              + " inner join obs  o on e.encounter_id=o.encounter_id                                                    "
              + " where e.voided=0 and o.voided=0 and p.voided=0 and	                                                "
              + " e.encounter_type = 80 and o.concept_id = 23783 and o.value_coded ="
              + sectorElegibilidade
              + "  and        "
              + " o.obs_datetime between :startDate and :endDate and e.location_id=:location                            "
              + " group by p.patient_id) sector_elegivel                                                                "
              + "																									    "
              + " inner join                                                                                            "
              + " (                                                                                                     "
              + "                                                                										"
              + " select p.patient_id, min(encounter_datetime) data_keypop                                              "
              + " from patient p                                                                                        "
              + " inner join encounter e on p.patient_id=e.patient_id                                                   "
              + " inner join obs  o on e.encounter_id=o.encounter_id                                                    "
              + " where e.voided=0 and o.voided=0 and p.voided=0 and                                                    "
              + " e.encounter_type = 80 and o.concept_id = 165196 and o.value_coded =1995    and                        "
              + " o.obs_datetime between :startDate and :endDate and e.location_id=:location                            "
              + " group by p.patient_id  ) keypop on sector_elegivel.patient_id = keypop.patient_id                     "
              + " and sector_elegivel.data_sector_elegivel=keypop.data_keypop                                           "
              + " 																									    "
              + " 																									    "
              + " inner join                                                                                            "
              + " (                                                                                                     "
              + "                                                                             							"
              + " select p.patient_id, min(encounter_datetime) data_inicio                                              "
              + " from patient p                                                                                        "
              + " inner join encounter e on p.patient_id=e.patient_id                                                   "
              + " inner join obs  o on e.encounter_id=o.encounter_id                                                    "
              + " where e.voided=0 and o.voided=0 and p.voided=0 and                                                    "
              + " e.encounter_type = 80 and o.concept_id=165289 and o.value_coded=165288  and                            "
              + " o.obs_datetime between :startDate and :endDate and e.location_id=:location                            "
              + " group by p.patient_id) inicio on sector_elegivel.patient_id = inicio.patient_id                       "
              + "  and sector_elegivel.data_sector_elegivel=inicio.data_inicio                                          ";

      switch (keyPop) {
        case SIM:
          query = query.replace("165288", "1065");
          break;
        case NAO:
          query = query.replace("165288", "1066");
          break;

        default:
          return query;
      }

      return query;
    }
  }
}
