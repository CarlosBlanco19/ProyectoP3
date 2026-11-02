package una.sistemareservas.logic;

import java.sql.SQLException;

final class ErroresBD {

    private static final int LLAVE_FORANEA = 1451; // Cannot delete or update a parent row
    private static final int LLAVE_DUPLICADA = 1062; // Duplicate entry

    private ErroresBD() {
    }

    static boolean esLlaveForanea(SQLException e) {
        return e.getErrorCode() == LLAVE_FORANEA
                || (e.getMessage() != null && e.getMessage().toLowerCase().contains("foreign key"));
    }

    static boolean esDuplicado(SQLException e) {
        return e.getErrorCode() == LLAVE_DUPLICADA
                || (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate"));
    }

    static String mensaje(String accion, SQLException e) {
        System.err.println("[BD] Error al " + accion + ": " + e.getMessage());
        return "No se pudo " + accion + " por un error en la base de datos.";
    }
}
