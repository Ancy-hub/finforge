package com.finforge.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class that provides JDBC connections to the SQL Server database.
 * <p>
 * When running inside Tomcat the connection is obtained from the JNDI-bound
 * {@link DataSource} configured in {@code META-INF/context.xml}, which activates
 * the container-managed Tomcat DBCP2 connection pool.  If the JNDI context is not
 * available (e.g. unit-test classpath) the factory falls back to a direct
 * {@link DriverManager} connection using the settings in {@code db.properties}.
 * </p>
 */
public final class DBConnection {

    private static final String JNDI_NAME = "java:comp/env/jdbc/SmartFinForge";
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DBConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new ExceptionInInitializerError(
                        "db.properties not found on classpath");
            }
            PROPS.load(in);
            Class.forName(PROPS.getProperty("db.driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBConnection() {
        // Utility class — no instances
    }

    /**
     * Returns a live JDBC {@link Connection}.
     * <ol>
     *   <li>Tries to obtain the connection from the JNDI {@link DataSource}
     *       ({@code java:comp/env/jdbc/SmartFinForge}) — uses the Tomcat
     *       DBCP2 pool when deployed.</li>
     *   <li>Falls back to a direct {@link DriverManager} connection using
     *       {@code db.properties} when the JNDI context is not available
     *       (unit tests, standalone execution).</li>
     * </ol>
     * The caller is responsible for closing the returned connection
     * (use try-with-resources).
     *
     * @return a live {@link Connection}
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        try {
            Context    ctx = new InitialContext();
            DataSource ds  = (DataSource) ctx.lookup(JNDI_NAME);
            return ds.getConnection();
        } catch (NamingException e) {
            // JNDI context not available — fall back to direct JDBC
            DriverManager.setLoginTimeout(2);
            return DriverManager.getConnection(
                    PROPS.getProperty("db.url"),
                    PROPS.getProperty("db.username"),
                    PROPS.getProperty("db.password"));
        }
    }
}
