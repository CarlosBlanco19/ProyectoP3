package una.sistemareservas;

import una.sistemareservas.datos.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Ayuda para las pruebas de integración: borra de MySQL lo que crearon las pruebas,
 * para no dejar basura en la base de datos.
 */
public final class DatosPrueba {

    private DatosPrueba() {
    }

    // Un texto distinto en cada corrida, para no chocar con datos de otras pruebas
    public static String sufijo() {
        return Long.toString(System.nanoTime(), 36).toUpperCase();
    }

    /** Borra la categoría, sus recursos y las reservas que usaron esos recursos o esa categoría. */
    public static void borrarCategoria(int idCategoria) throws SQLException {
        try (Connection con = ConexionDB.getConnection()) {
            String reservas = "SELECT fk_reserva FROM reservaCategoria WHERE fk_categoria = ? "
                    + "UNION SELECT rr.fk_reserva FROM reservaRecurso rr JOIN recurso r ON r.pk_recurso = rr.fk_recurso "
                    + "WHERE r.fk_categoria = ?";
            ejecutar(con, "DELETE FROM reservaRecurso WHERE fk_reserva IN (SELECT x.fk_reserva FROM (" + reservas + ") x)", idCategoria);
            ejecutar(con, "DELETE FROM reservaCategoria WHERE fk_reserva IN (SELECT x.fk_reserva FROM (" + reservas + ") x)", idCategoria);
            ejecutar(con, "DELETE FROM reserva WHERE pk_reserva NOT IN (SELECT fk_reserva FROM reservaCategoria) "
                    + "AND pk_reserva NOT IN (SELECT fk_reserva FROM reservaRecurso)");
            ejecutar(con, "DELETE FROM recurso WHERE fk_categoria = ?", idCategoria);
            ejecutar(con, "DELETE FROM categoriaRecursos WHERE pk_categoria = ?", idCategoria);
        }
    }

    public static void borrarFuncionario(String id) throws SQLException {
        try (Connection con = ConexionDB.getConnection()) {
            ejecutar(con, "DELETE FROM funcionario WHERE fk_usuario = ?", id);
            ejecutar(con, "DELETE FROM usuario WHERE pk_usuario = ?", id);
        }
    }

    private static void ejecutar(Connection con, String sql, Object... parametros) throws SQLException {
        // Los "?" se llenan con los parámetros (si hay dos "?" con el mismo valor, se repite)
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int signos = sql.length() - sql.replace("?", "").length();
            for (int i = 0; i < signos; i++) {
                ps.setObject(i + 1, parametros[Math.min(i, parametros.length - 1)]);
            }
            ps.executeUpdate();
        }
    }
}
