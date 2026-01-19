package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.TestUtil;

public abstract class DefinitionsFGHLiveTest extends DefinitionsTest {
  /** @see BaseContextSensitiveTest#useInMemoryDatabase() */
  @Override
  public Boolean useInMemoryDatabase() {
    /*
     * ensure ~/.OpenMRS/openmrs-runtime.properties exists with your properties such as; connection.username=openmrs
     * connection.url=jdbc:mysql://127.0.0.1:3316/openmrs connection.password=wTV.Tpp0|Q&c
     */

    final Properties properties = TestUtil.getRuntimeProperties(this.getWebappName());

    System.setProperty("databaseUrl", properties.getProperty("connection.url"));
    System.setProperty("databaseUsername", properties.getProperty("connection.username"));
    System.setProperty("databasePassword", properties.getProperty("connection.password"));

    System.setProperty("databaseDriver", "com.mysql.jdbc.Driver");
    System.setProperty("databaseDialect", "org.hibernate.dialect.MySQLDialect");

    return false;
  }

  @Override
  public void deleteAllData() {
    System.out.println("Do not delete DATA...");
  }

  @Override
  public void baseSetupWithStandardDataAndAuthentication() throws SQLException {
    System.out.println("Do not add DATA...");
  }

  protected abstract String username();

  protected abstract String password();

  @Before
  public void initialize() throws ContextAuthenticationException {
    Context.openSession();
    Context.authenticate(this.username(), this.password());
  }

  @After
  public void close() {
    Context.closeSession();
  }

  @Override
  protected Date getStartDate() {
    return DateUtil.getDateTime(2013, 2, 6);
  }

  @Override
  protected Date getEndDate() {
    return DateUtil.getDateTime(2019, 3, 6);
  }

  @Override
  protected Location getLocation() {
    return Context.getLocationService().getLocation(103);
  }
}
