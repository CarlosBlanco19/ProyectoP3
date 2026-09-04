package una.sistemareservas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import una.sistemareservas.dto.Rol;
import una.sistemareservas.dto.UsuarioDTO;

public class MenuPrincipalViewController {

    @FXML private TabPane tabPaneMenuPrincipal;
    @FXML private Tab tabReservas;
    @FXML private Tab tabFuncionarios;
    @FXML private Tab tabCategorias;
    @FXML private Tab tabRecursos;
    @FXML private Tab tabCalendarizacion;
    @FXML private Tab tabActividades;
    @FXML private Tab tabEstadisticas;

    @FXML private void initialize (){
        UsuarioDTO usuario = LogInViewController.usuarioLogueado;
        if (usuario != null) {
            configurarAccesos(usuario);
        } else {
            //tirar excepcion
        }
    }

    private void configurarAccesos(UsuarioDTO usuario){

        if(usuario.getRol() == Rol.ADMINISTRADOR && usuario.getRol() != null){
            tabPaneMenuPrincipal.getTabs().remove(tabReservas);
        } else if (usuario.getRol() == Rol.FUNCIONARIO && usuario.getRol() != null) {
            tabPaneMenuPrincipal.getTabs().remove(tabCategorias);
            tabPaneMenuPrincipal.getTabs().remove(tabFuncionarios);
            tabPaneMenuPrincipal.getTabs().remove(tabRecursos);
        }

    }

}
