package una.sistemareservas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import una.sistemareservas.utilidades.NavegadorPantallas;


public class LogInViewController {

    //manejo de usurio y contraseña
    //se cambia cuando se implemente
    private static final String USUARIO_VALIDO = "admin";
    private static final String CONTRASEÑA_VALIDA = "1234";

    //bandera

    public static boolean sesionIniciada = false;

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private Button btnIngresar;
    @FXML private Button btnCancelar;
    @FXML private Button btnCambiar;
    @FXML private Label lblAviso;

    @FXML private void initialize(){
        btnIngresar.setOnAction(this::intentarIngresar);
        btnCancelar.setOnAction(evento -> NavegadorPantallas.cambiarPantalla(evento, "/ui/MenuPrincipalView.fxml"));
        btnCambiar.setOnAction(evento -> NavegadorPantallas.cambiarPantalla(evento, "/ui/CambiarClaveView.fxml"));
    }

    private void intentarIngresar(ActionEvent evento){
        String usuario = txtUsuario.getText().trim();
        String clave = txtClave.getText().trim();

        if(usuario.equals(USUARIO_VALIDO) && clave.equals(CONTRASEÑA_VALIDA)){
            sesionIniciada = true;
            NavegadorPantallas.cambiarPantalla(evento, "/ui/MenuPrincipalView.fxml");
        }else{
            lblAviso.setText("Usuario o contraseña incorrectos");
        }
    }


}
