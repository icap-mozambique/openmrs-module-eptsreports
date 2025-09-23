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

package org.openmrs.module.eptsreports.reporting;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.util.IOUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsReportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.util.OpenmrsClassLoader;

public class EptsReportInitializer {
  private final Log log = LogFactory.getLog(this.getClass());

  /** Initializes all EPTS reports and remove deprocated reports from database. */
  public void initializeReports() {
    for (final ReportManager reportManager :
        Context.getRegisteredComponents(EptsReportManager.class)) {
      if (reportManager.getClass().getAnnotation(Deprecated.class) != null) {
        // remove depricated reports
        EptsReportUtils.purgeReportDefinition(reportManager);
        this.log.info(
            "Report " + reportManager.getName() + " is deprecated.  Removing it from database.");
      } else {
        // setup EPTS active reports
        EptsReportUtils.setupReportDefinition(reportManager);
        this.log.info("Setting up report " + reportManager.getName() + "...");
      }
    }

    ReportUtil.updateGlobalProperty(
        ReportingConstants.GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE, "-1");

    this.updateFSRTemplate();
  }

  private void updateFSRTemplate() {
    final ReportService reportService = Context.getService(ReportService.class);
    final ReportDesign reportDesign =
        reportService.getReportDesignByUuid("03375b3c-8770-4c63-a371-e832a41e388f");

    if (reportDesign == null) {
      return;
    }

    final ReportDesignResource resource =
        reportDesign.getResourceByUuid("38db6bdc-e57d-401c-a9d7-d65be11cd9cc");

    try (final InputStream is =
        OpenmrsClassLoader.getInstance()
            .getResourceAsStream("LISTA_DE_PACIENTES_COM_EXAME_DE_CARGA_VIRAL_FSR.xls")) {
      resource.setContents(IOUtils.toByteArray(is));
    } catch (final IOException e) {
      e.printStackTrace();
    }

    reportDesign.addResource(resource);
    reportService.saveReportDesign(reportDesign);
  }

  /** Purges all EPTS reports from database. */
  public void purgeReports() {
    for (final ReportManager reportManager :
        Context.getRegisteredComponents(EptsReportManager.class)) {
      EptsReportUtils.purgeReportDefinition(reportManager);
      this.log.info("Report " + reportManager.getName() + " removed from database.");
    }
  }

  public void removeEptsReportsGlobalProperties() {
    final List<GlobalProperty> eptsreports =
        Context.getAdministrationService().getGlobalPropertiesByPrefix("eptsreports");
    Context.getAdministrationService().purgeGlobalProperties(eptsreports);

    this.log.info("Removing all eptsreports global properties from database.");
  }

  public void purgOldReports() {
    final List<GlobalProperty> globalPropertiesByPrefix =
        Context.getAdministrationService().getGlobalPropertiesByPrefix("eptsoldreports");
    final ReportDefinitionService reportService = Context.getService(ReportDefinitionService.class);

    for (final GlobalProperty globalProperty : globalPropertiesByPrefix) {
      try {
        final ReportDefinition findDefinition =
            EptsReportUtils.findReportDefinition(globalProperty.getPropertyValue());
        if (findDefinition != null) {
          try {
            reportService.purgeDefinition(findDefinition);
            Context.getAdministrationService().purgeGlobalProperty(globalProperty);
            this.log.info("Report " + findDefinition.getName() + " removed from database.");

          } catch (final Exception e) {
            this.log.error(
                String.format(
                    "Unable to remove the Report %s , StackTrace: %s ",
                    findDefinition.getName(), e.getMessage()));
          }
        }

      } catch (final Exception e) {
        this.log.error(
            String.format(
                "Unable to find the Definition for the report with UUID  %s. StackTrace: %s ",
                globalProperty.getPropertyValue(), e.getMessage()));
      }
    }
  }
}
