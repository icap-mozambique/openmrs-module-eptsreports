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
import org.openmrs.module.eptsreports.reporting.utils.PrepNewKeyPopType;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Abdul Sacur */
@Component
public class PrepNewSectorDataSet extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private PrepNewCohortQueries prepNew;

  /**
   * @param nomeSector
   * @param conceitokeypop
   * @return
   */
  public DataSetDefinition constructDatset(final String nomeSector, final Integer conceitokeypop) {
    CohortIndicatorDataSetDefinition definition = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    definition.setName("PrEP NEW Data Set Start Sector and Key Population");
    definition.addParameters(getParameters());

    /*
     * Metodo para adicionar e calcular de forma dinamica as desagregações do
     * conceito grupo ALVO PREP
     */
    addColumns(
        nomeSector, "MG", conceitokeypop, 165196, definition, mappings, PrepNewKeyPopType.PREGNANT);

    addColumns(
        nomeSector,
        "ML",
        conceitokeypop,
        165196,
        definition,
        mappings,
        PrepNewKeyPopType.LACTATION);

    addColumns(
        nomeSector,
        "AJ",
        conceitokeypop,
        165196,
        definition,
        mappings,
        PrepNewKeyPopType.ADOLESCENTS_YOUTH_RISK);

    addColumns(
        nomeSector, "HM", conceitokeypop, 165196, definition, mappings, PrepNewKeyPopType.MILITARY);

    addColumns(
        nomeSector, "HMO", conceitokeypop, 165196, definition, mappings, PrepNewKeyPopType.MINER);

    addColumns(
        nomeSector, "HC", conceitokeypop, 165196, definition, mappings, PrepNewKeyPopType.DRIVER);

    addColumns(
        nomeSector,
        "CS",
        conceitokeypop,
        165196,
        definition,
        mappings,
        PrepNewKeyPopType.CASAIS_SERODISCORDANTE);

    /*
     * Metodo para adicionar e calcular de forma dinamica as desagregações do
     * conceito Populacao CHAVE
     */
    addColumns(
        nomeSector, "RE", conceitokeypop, 23703, definition, mappings, PrepNewKeyPopType.PRISIONER);

    addColumns(
        nomeSector,
        "HSH",
        conceitokeypop,
        23703,
        definition,
        mappings,
        PrepNewKeyPopType.HOMOSEXUAL);

    addColumns(
        nomeSector,
        "HT",
        conceitokeypop,
        23703,
        definition,
        mappings,
        PrepNewKeyPopType.TRANSGENDER);

    addColumns(
        nomeSector, "TS", conceitokeypop, 23703, definition, mappings, PrepNewKeyPopType.SEXWORKER);

    addColumns(
        nomeSector, "PID", conceitokeypop, 23703, definition, mappings, PrepNewKeyPopType.DRUGUSER);

    /* TO BE USED IF NECESSARY Caso Especial
     * addColumns( nomeSector, "CE", conceitokeypop, 165285, definition, mappings,
     * PrepNewKeyPopType.SPECIAL_CASE);
     */

    /*
     * TO BE USED IF NECESSARY definition .addColumn(nomeSector + "All",
     * "PrEP_NEW_", EptsReportUtils.map(
     * eptsGeneralIndicator.getIndicator("PREP NEW",
     * EptsReportUtils.map(prepNew.getClientsNewlyEnrolledInPrep(), mappings)),
     * mappings), "");
     */

    return definition;
  }

  public DataSetDefinition OtherDatset(final Integer conceitoSector) {
    CohortIndicatorDataSetDefinition definition = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    definition.setName("PrEP NEW Data Set Start Sector and Key Population");
    definition.addParameters(getParameters());

    definition.addColumn(
        "OUTRO",
        "PrEP_NEW_Outro",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "PREP_NEW_Outro",
                EptsReportUtils.map(
                    prepNew.getSectorClientsNewlyEnrolledInPrep(
                        23913, null, PrepNewKeyPopType.OTHER),
                    mappings)),
            mappings),
        "");

    return definition;
  }

  private void addColumns(
      final String nomeSector,
      String prefixo,
      final Integer conceitoSector,
      Integer conceitoPopulacao,
      CohortIndicatorDataSetDefinition definition,
      String mappings,
      final PrepNewKeyPopType keypop) {

    definition.addColumn(
        nomeSector + prefixo,
        nomeDataSet(nomeSector, keypop),
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                nomeDataSet(nomeSector, keypop),
                EptsReportUtils.map(
                    prepNew.getSectorClientsNewlyEnrolledInPrep(
                        conceitoSector, conceitoPopulacao, keypop),
                    mappings)),
            mappings),
        "");
  }

  private String nomeDataSet(final String nomeSector, final PrepNewKeyPopType keypop) {
    return "PrEP_NEW_"
        + nomeSector
        + "_"
        + keypop.toString()
        + ": "
        + "Number of new clients in PrEP_NEW_"
        + nomeSector
        + "_"
        + keypop.toString();
  }
}
