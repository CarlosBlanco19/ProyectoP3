package una.sistemareservas.utilidades;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class NavegadorPantallas {

    private NavegadorPantallas() {
    }

    public static void cambiarPantalla(ActionEvent evento, String archivoFXML) {
        try {
            Parent raiz = FXMLLoader.load(NavegadorPantallas.class.getResource(archivoFXML));
            Stage stage = (Stage) ((Node) evento.getSource()).getScene().getWindow();
            stage.getScene().setRoot(raiz);
            stage.sizeToScene();
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            Dialogos.error("No se pudo abrir la pantalla " + archivoFXML + ": " + e.getMessage());
        }
    }
}
