package una.sistemareservas.datos;

import una.sistemareservas.utilidades.ConfiguracionBackend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Única clase que sabe cómo conectarse a MySQL. Solo el Backend la tiene.
public class ConexionDB {

    private static final String URL = ConfiguracionBackend.get("db.url",
            "jdbc:mysql://localhost:3306/sistemaReservas?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    private static final String USER = ConfiguracionBackend.get("db.usuario", "reservasApp");
    private static final String PASSWORD = ConfiguracionBackend.get("db.clave", "Reservas2026");

    private ConexionDB() {
    }

    // Cada operación abre su propia conexión y la cierra con try-with-resources.
    // Así varios hilos (uno por Frontend) pueden usar la base de datos al mismo tiempo sin estorbarse.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static String getUrl() {
        return URL;
    }

    public static String getUsuario() {
        return USER;
    }
}
