package colegio.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLOutput;

public class Conexion {


    private static final String IP = "localhost";
    private static final int PUERTO = 3306;
    private static final String SCHEMA = "ColegioDB";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "F8BE464369!ch";

    private static final String URL = "jdbc:mysql://" + IP + ":" + PUERTO + "/" + SCHEMA
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver no encontrado en el classpath: " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }



}



