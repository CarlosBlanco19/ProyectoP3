package una.sistemareservas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;

public class RecursosViewController {
    @FXML private ComboBox cbBusquedaCategoriaRecursos;
    @FXML private TextField txtBusquedaRecursos;
    @FXML private Button btnBuscarRecursos;
    @FXML private Button btnImprimirRecursos;
    @FXML private TextField txtAgregarIdRecursos;
    @FXML private ComboBox cbAgregarCategoriaRecursos;
    @FXML private TextField txtAgregarDescripcionRecursos;
    @FXML private Button btnGuardarRecursos;
    @FXML private Button btnBorrarRecursos;
    @FXML private Button btnLimpiarRecursos;
    @FXML private TableView tabRecursos;
    @FXML private TableColumn colIdRecursos;
    @FXML private TableColumn colCategoriaRecursos;
    @FXML private TableColumn colDescripcionRecursos;
}
