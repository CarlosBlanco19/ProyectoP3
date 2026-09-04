package una.sistemareservas.controller;

import una.sistemareservas.dto.UsuarioDTO;
import una.sistemareservas.logic.UsuarioLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import una.sistemareservas.utilidades.NavegadorPantallas;


public class LogInViewController {


    //bandera

    private final UsuarioLogic usuarioService = new UsuarioLogic();

    public static UsuarioDTO usuarioLogueado = null;

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

        UsuarioDTO usuario =  usuarioService.autenticar(id, clave);

        if(usuario != null){
            usuarioLogueado = usuario;
            NavegadorPantallas.cambiarPantalla(evento, "/ui/MenuPrincipalView.fxml");
        }else{
            lblAviso.setText("Usuario o contraseña incorrectos");
        }
    }


}
