package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestiona la conexión con la base de datos MySQL mediante JDBC.
 */
public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/speedfast_db";
    private static final String USER = "root";
    private static final String PASSWORD = "speedfast2026";

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de MySQL", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Prueba rápida de conexión, útil para verificar la configuración.
     */
    public static void main(String[] args) {
        try (Connection con = conectar()) {
            System.out.println("Conexion exitosa a speedfast_db");
        } catch (SQLException e) {
            System.out.println("Error de conexion: " + e.getMessage());
        }
    }
}