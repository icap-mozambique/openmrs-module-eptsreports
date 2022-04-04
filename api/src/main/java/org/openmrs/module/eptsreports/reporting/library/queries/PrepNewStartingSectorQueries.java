package org.openmrs.module.eptsreports.reporting.library.queries;

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
  }
}
