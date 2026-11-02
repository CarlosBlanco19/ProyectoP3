package una.sistemareservas.utilidades;

import javafx.scene.control.Alert;

public final class Dialogos {

    private Dialogos() {
    }

    public static void advertencia(String mensaje) {
        mostrar(Alert.AlertType.WARNING, "Advertencia", mensaje);
    }

    public static void error(String mensaje) {
        mostrar(Alert.AlertType.ERROR, "Error", mensaje);
    }

    public static void informacion(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
