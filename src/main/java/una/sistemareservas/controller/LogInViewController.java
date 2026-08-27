package una.sistemareservas.controller;

import una.sistemareservas.model.Usuario;
import una.sistemareservas.service.UsuarioService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import una.sistemareservas.utilidades.NavegadorPantallas;


public class LogInViewController {


    //bandera

    private final UsuarioService usuarioService = new UsuarioService();

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
        String id = txtUsuario.getText().trim();
        String clave = txtClave.getText().trim();

        Usuario usuario =  usuarioService.autenticar(id, clave);

        if(usuario != null){
            sesionIniciada = true;
            NavegadorPantallas.cambiarPantalla(evento, "/ui/MenuPrincipalView.fxml");
        }else{
            lblAviso.setText("Usuario o contraseña incorrectos");
        }
    }


}
