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
package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.Ec15Queries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.springframework.stereotype.Component;

@Component
public class Ec15PatientListDataset extends BaseDataSet {

  /**
   * @param parameterList
   * @return
   */
  public DataSetDefinition ec15PatientListDataset(final List<Parameter> parameterList) {
    final SqlDataSetDefinition dsd = new SqlDataSetDefinition();
    dsd.setName("EC15");
    dsd.addParameters(parameterList);
    dsd.setSqlQuery(Ec15Queries.getEc15CombinedQuery());

    return dsd;
  }
}
