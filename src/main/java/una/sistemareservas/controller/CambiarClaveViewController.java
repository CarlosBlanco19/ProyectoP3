package una.sistemareservas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import una.sistemareservas.protocolo.IService;
import una.sistemareservas.proxy.ServiceProxy;
import una.sistemareservas.utilidades.Dialogos;
import una.sistemareservas.utilidades.NavegadorPantallas;

public class CambiarClaveViewController {

    private final IService service = ServiceProxy.instancia();

    @FXML private TextField txtID;
    @FXML private PasswordField txtClaveActual;
    @FXML private PasswordField txtClaveNueva;
    @FXML private PasswordField txtConfirmarClave;
    @FXML private Button btnAceptar;
    @FXML private Button btnCancelar;
    @FXML private Label lblAvisos;

    @FXML
    private void initialize() {
        btnAceptar.setOnAction(this::cambiarClave);
        btnCancelar.setOnAction(evento -> NavegadorPantallas.cambiarPantalla(evento, "/ui/LogInView.fxml"));
    }

    private void cambiarClave(ActionEvent evento) {
        String id = txtID.getText().trim();
        String claveActual = txtClaveActual.getText().trim();
        String claveNueva = txtClaveNueva.getText().trim();
        String confirmarClave = txtConfirmarClave.getText().trim();

        // Validaciones de la pantalla (las demás las hace el Backend)
        if (id.isEmpty() || claveActual.isEmpty() || claveNueva.isEmpty() || confirmarClave.isEmpty()) {
            lblAvisos.setText("Rellene todos los campos");
            return;
        }
        if (!claveNueva.equals(confirmarClave)) {
            lblAvisos.setText("La confirmación no coincide");
            return;
        }

        try {
            service.cambiarClave(id, claveActual, claveNueva);
            Dialogos.informacion("Cambio de clave", "La clave se cambió correctamente.");
            NavegadorPantallas.cambiarPantalla(evento, "/ui/LogInView.fxml");
        } catch (Exception e) {
            lblAvisos.setText(e.getMessage());
        }
    }
}
