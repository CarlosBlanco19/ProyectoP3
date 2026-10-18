package una.sistemareservas.datos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PRUEBA_CONEXION {

    public static void main(String[] args) {
        try (Connection c = ConexionDB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM categoriaRecursos")) {
            rs.next();
            System.out.println("Conexión OK. Categorías en la BD: " + rs.getInt(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
