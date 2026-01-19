/** */
package org.openmrs.module.eptsreports.reporting.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;

/** @author Stélio Moiane */
public class EptsListUtils {

  public static final <T> int listSlices(final Collection<T> list, final int chunk) {
    final BigDecimal listSize = new BigDecimal(list.size());
    final BigDecimal chunkSize = new BigDecimal(chunk);
    return listSize.divide(chunkSize, RoundingMode.UP).intValue();
  }

  public static void addColumsByDimensionAndGender(
      final String prefixName,
      final String numeratorPrefix,
      final CohortIndicatorDataSetDefinition dataSetDefinition,
      final String mappings,
      final CohortIndicator cohortIndicator,
      final String dimension,
      final Gender gender,
      final AgeRange... ranges) {

    for (final AgeRange range : ranges) {

      final String name = EptsListUtils.getColumnName(numeratorPrefix, range, gender);

      dataSetDefinition.addColumn(
          prefixName + name,
          name.replace("-", " "),
          EptsReportUtils.map(cohortIndicator, mappings),
          name + "=" + name + "|" + dimension + "=" + dimension);
    }
  }

  public static String getColumnName(
      final String prefix, final AgeRange range, final Gender gender) {
    return range.getDesagregationColumnName(prefix, gender);
  }
}
