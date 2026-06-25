/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.PrepNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.PrepNewEligibilidadeSectorType;
import org.openmrs.module.eptsreports.reporting.utils.PrepNewEnrollemntStatus;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Abdul Sacur */
@Component
public class PrepNewLigacaoCPNeATSDataSet extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private PrepNewCohortQueries prepNew;

  private final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

  /**
   * @param nomeSector
   * @param conceitoSectorInicio
   * @return
   */
  public DataSetDefinition constructDatset(
      final String nomeSector, final Integer conceitoSectorInicio) {
    CohortIndicatorDataSetDefinition definition = new CohortIndicatorDataSetDefinition();
    definition.setName("PrEP NEW Data Set Start Sector and Key Population");
    definition.addParameters(getParameters());

    /*
     * Metodo para adicionar e calcular de forma dinamica as desagregações do
     * conceito grupo ALVO PREP
     */
    addColumns(
        nomeSector,
        "SAAJ",
        conceitoSectorInicio,
        definition,
        mappings,
        PrepNewEligibilidadeSectorType.SAAJ);

    addColumns(
        nomeSector,
        "TA",
        conceitoSectorInicio,
        definition,
        mappings,
        PrepNewEligibilidadeSectorType.TRIAGEM_ADULTO);

    addColumns(
        nomeSector,
        "DC",
        conceitoSectorInicio,
        definition,
        mappings,
        PrepNewEligibilidadeSectorType.DOENCAS_CRONICAS);

    addColumns(
        nomeSector,
        "CPN",
        conceitoSectorInicio,
        definition,
        mappings,
        PrepNewEligibilidadeSectorType.CPN);

    addColumns(
        nomeSector,
        "CPF",
        conceitoSectorInicio,
        definition,
        mappings,
        PrepNewEligibilidadeSectorType.CPF);

    addColumns(
        nomeSector,
        "OUTRO",
        conceitoSectorInicio,
        definition,
        mappings,
        PrepNewEligibilidadeSectorType.OUTRO);

    return definition;
  }

  public DataSetDefinition constructDatasetSubTotal() {
    CohortIndicatorDataSetDefinition definition = new CohortIndicatorDataSetDefinition();
    definition.setName("PrEP NEW SubTotal");
    definition.addParameters(getParameters());

    definition.addColumn(
        "ATSC",
        "PrEP_NEW_ATSC_Subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "PrEP_NEW_ATSC_Subtotal",
                EptsReportUtils.map(
                    prepNew.getSectorClientsNewlyEnrolledbyEligibility(6245), mappings)),
            mappings),
        "");

    definition.addColumn(
        "UATS",
        "PrEP_NEW_UATS_Subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "PrEP_NEW_UATS_Subtotal",
                EptsReportUtils.map(
                    prepNew.getSectorClientsNewlyEnrolledbyEligibility(1597), mappings)),
            mappings),
        "");

    definition.addColumn(
        "CPN",
        "PrEP_NEW_CPN_Subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "PrEP_NEW_CPN_Subtotal",
                EptsReportUtils.map(
                    prepNew.getSectorClientsNewlyEnrolledbyEligibility(1978), mappings)),
            mappings),
        "");

    definition.addColumn(
        "TOTAL",
        "PrEP_NEW_Sero_Descordante_TOTAL",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "PrEP_NEW_Sero_Descordante_TOTAL",
                EptsReportUtils.map(
                    prepNew.getSectorClientsNewlyEnrolledInPrepSeroDescordante(), mappings)),
            mappings),
        "");

    return definition;
  }

  public DataSetDefinition constructDatasetEnrollemntPrep(
      final String nomeSector, final Integer conceitoSectorInicio) {
    CohortIndicatorDataSetDefinition definition = new CohortIndicatorDataSetDefinition();
    definition.setName("PrEP NEW by Enrollment Status");
    definition.addParameters(getParameters());

    addColumns(
        nomeSector, "YES", conceitoSectorInicio, definition, mappings, PrepNewEnrollemntStatus.SIM);

    addColumns(
        nomeSector, "NO", conceitoSectorInicio, definition, mappings, PrepNewEnrollemntStatus.NAO);

    addColumns(
        nomeSector,
        "MAYBE",
        conceitoSectorInicio,
        definition,
        mappings,
        PrepNewEnrollemntStatus.TALVEZ);

    return definition;
  }

  private void addColumns(
      final String nomeSector,
      String prefixo,
      final Integer conceitoSector,
      CohortIndicatorDataSetDefinition definition,
      String mappings,
      final PrepNewEligibilidadeSectorType keypop) {

    definition.addColumn(
        nomeSector + prefixo,
        nomeDataSet(nomeSector, keypop),
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                nomeDataSet(nomeSector, keypop),
                EptsReportUtils.map(
                    prepNew.getSectorClientsNewlyEnrolledbyEligibility(conceitoSector, keypop),
                    mappings)),
            mappings),
        "");
  }

  private void addColumns(
      final String nomeSector,
      String prefixo,
      final Integer conceitoSector,
      CohortIndicatorDataSetDefinition definition,
      String mappings,
      final PrepNewEnrollemntStatus status) {

    definition.addColumn(
        nomeSector + prefixo,
        nomeDataSet(nomeSector, status),
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                nomeDataSet(nomeSector, status),
                EptsReportUtils.map(
                    prepNew.getSectorClientsNewlybyEnrollmentStatus(conceitoSector, status),
                    mappings)),
            mappings),
        "");
  }

  private String nomeDataSet(final String nomeSector, final Enum<?> porta) {
    return "PrEP_NEW_"
        + nomeSector
        + "_"
        + porta.toString()
        + ": "
        + "Number of new clients in PrEP_NEW_"
        + nomeSector
        + "_"
        + porta.toString();
  }
}
