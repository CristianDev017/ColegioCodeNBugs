package config;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class DBConnectionSingleton {

    private static final String JNDI_NAME = "java:comp/env/jdbc/ColegioDB";

    private static DBConnectionSingleton instance;

    private DataSource dataSource;

    private DBConnectionSingleton() {
        try {
            dataSource = (DataSource) new InitialContext().lookup(JNDI_NAME);
        } catch (NamingException e) {
            throw new IllegalStateException("No se pudo encontrar el DataSource " + JNDI_NAME, e);
        }
    }

    public static synchronized DBConnectionSingleton getInstance() {
        if (instance == null) {
            instance = new DBConnectionSingleton();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}