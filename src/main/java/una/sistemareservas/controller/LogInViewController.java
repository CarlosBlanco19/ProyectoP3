package una.sistemareservas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import una.sistemareservas.dto.UsuarioDTO;
import una.sistemareservas.model.AlertasModel;
import una.sistemareservas.protocolo.IService;
import una.sistemareservas.proxy.ServiceProxy;
import una.sistemareservas.utilidades.Dialogos;
import una.sistemareservas.utilidades.NavegadorPantallas;
import una.sistemareservas.utilidades.SesionGlobal;

public class LogInViewController {

    // El controlador solo conoce la interfaz IService: no sabe que por debajo hay un socket
    private final IService service = ServiceProxy.instancia();

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private Button btnIngresar;
    @FXML private Button btnCambiar;
    @FXML private Label lblAviso;

    @FXML
    private void initialize() {
        btnIngresar.setOnAction(this::intentarIngresar);
        btnIngresar.setDefaultButton(true); // Enter = Ingresar
        btnCambiar.setOnAction(evento -> NavegadorPantallas.cambiarPantalla(evento, "/ui/CambiarClaveView.fxml"));
    }

    private void intentarIngresar(ActionEvent evento) {
        String id = txtUsuario.getText().trim();
        String clave = txtClave.getText().trim();

        try {
            UsuarioDTO usuario = service.login(id, clave);
            SesionGlobal.iniciarSesion(usuario);
            AlertasModel.getInstancia().limpiar(); // Las alertas son solo de esta sesión

            Stage ventana = (Stage) btnIngresar.getScene().getWindow();
            NavegadorPantallas.cambiarPantalla(evento, "/ui/MenuPrincipalView.fxml");
            // Como en el enunciado: "SISTEMA DE RESERVAS - 222 (FUNCIONARIO)"
            ventana.setTitle("SISTEMA DE RESERVAS - " + usuario.getID() + " (" + usuario.getRol() + ")");
        } catch (Exception e) {
            if (!ServiceProxy.instancia().estaConectado()) {
                // No se pudo hablar con el Backend: se explica en una ventana (no cabe en la etiqueta)
                lblAviso.setText("Sin conexión");
                Dialogos.error(e.getMessage());
            } else {
                lblAviso.setText(e.getMessage());
            }
        }
    }
}
