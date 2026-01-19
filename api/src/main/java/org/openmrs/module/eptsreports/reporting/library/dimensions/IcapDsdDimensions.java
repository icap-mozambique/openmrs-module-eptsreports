/** */
package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.DSDQueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.DSDQueriesInterface.QUERY.DSDModeTypeLevel1;
import org.openmrs.module.eptsreports.reporting.library.queries.DSDQueriesInterface.QUERY.DSDModelTypeLevel2;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class IcapDsdDimensions {

  @Autowired private GenericCohortQueries genericCohortQueries;

  // TODO: Voltar ao metodo findPatientsWhoAreIncludedInDSDModel
  public CohortDefinitionDimension findMDS(final String mdsName, final DSDModeTypeLevel1 dsdModel) {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName(mdsName);
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        mdsName,
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                mdsName,
                DSDQueriesInterface.QUERY.findPatientsWhoAreIncludedInDSDModelICAP(dsdModel)),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findMDS(
      final String mdsName, final DSDModelTypeLevel2 dsdModel) {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName(mdsName);
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        mdsName,
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                mdsName, DSDQueriesInterface.QUERY.findPatientsWhoAreIncludedInDSDModel(dsdModel)),
            mappings));

    return dimension;
  }
}
