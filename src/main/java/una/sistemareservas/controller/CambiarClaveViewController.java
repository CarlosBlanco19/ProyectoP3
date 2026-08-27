package una.sistemareservas.controller;
import una.sistemareservas.service.UsuarioService;
import una.sistemareservas.utilidades.NavegadorPantallas;
import una.sistemareservas.model.Usuario;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;




public class CambiarClaveViewController {

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML private TextField txtID;
    @FXML private PasswordField txtClaveActual;
    @FXML private PasswordField txtClaveNueva;
    @FXML private PasswordField txtConfirmarClave;
    @FXML private Button btnAceptar;
    @FXML private Button btnCancelar;
    @FXML private Label lblAvisos;

    @FXML private void initialize(){
        btnAceptar.setOnAction(this::cambiarClave);
        btnCancelar.setOnAction(evento -> NavegadorPantallas.cambiarPantalla(evento, "/ui/LogInView.fxml"));
    }

    private void cambiarClave(ActionEvent evento){
        String id = txtID.getText().trim();
        String claveActual = txtClaveActual.getText().trim();
        String claveNueva = txtClaveNueva.getText().trim();
        String confirmarClave = txtConfirmarClave.getText().trim();

        Usuario usuario = usuarioService.buscarID(id);

        if(usuario == null){
            lblAvisos.setText("ID no encontrado");
            return;
        }

        if(claveNueva.isEmpty() || confirmarClave.isEmpty() || claveActual.isEmpty()){
            lblAvisos.setText("Rellene todos los campos");
            return;
        }

        if(claveNueva.isEmpty() || !claveNueva.equals(confirmarClave)){
            lblAvisos.setText("La confirmación no coincide");
            return;
        }

       if(usuarioService.cambiarClave(usuario, claveActual, claveNueva)){
            NavegadorPantallas.cambiarPantalla(evento, "/ui/LogInView.fxml");
       }else{
            lblAvisos.setText("Clave actual incorrecta");
       }
    }

}
