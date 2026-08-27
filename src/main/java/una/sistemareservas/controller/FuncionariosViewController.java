package una.sistemareservas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

public class FuncionariosViewController {

    @FXML private TextField txtBusquedaId;
    @FXML private TextField txtBusquedaNombre;
    @FXML private Button btnBuscarFuncionario;
    @FXML private Button btnImprimirFuncionarios;
    @FXML private TextField txtAgregarId;
    @FXML private TextField txtAgregarNombre;
    @FXML private TextField txtAgregarTelefono;
    @FXML private Button btnGuardarFuncionario;
    @FXML private Button btnBorrarFuncionario;
    @FXML private Button btnLimpiarFuncionario;
    @FXML private TableView tabFuncionarios;
    @FXML private TableColumn colIdFuncionarios;
    @FXML private TableColumn colNombreFuncionarios;
    @FXML private TableColumn colTelefonoFuncionarios;
}
