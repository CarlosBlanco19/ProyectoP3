package una.sistemareservas.utilidades;

public class SesionGlobal {

    private static String idFuncionarioActual;

    // Método para guardar el ID cuando inicie sesión
    public static void setFuncionarioActual(String id) {
        idFuncionarioActual = id;
    }

    // Método para preguntar quién está usando el sistema en cualquier pantalla
    public static String getFuncionarioActual() {
        return idFuncionarioActual;
    }

    public static void cerrarSesion() {
        idFuncionarioActual = null;
    }
}
