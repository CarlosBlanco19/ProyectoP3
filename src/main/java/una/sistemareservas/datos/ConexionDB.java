package una.sistemareservas.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/sistemaReservas"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC";
    private static final String USER = "reservasApp";
    private static final String PASSWORD = "Reservas2026";


    private ConexionDB(){

    }

    public static Connection getConnection() throws SQLException{

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
