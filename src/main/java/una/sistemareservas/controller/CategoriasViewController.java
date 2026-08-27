package una.sistemareservas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;


public class CategoriasViewController {

    @FXML private TextField txtBusquedaCategoria;
    @FXML private Button btnBuscarCategoria;
    @FXML private Button btnImprimirCategoria;
    @FXML private TextField txtAgregarIdCategoria;
    @FXML private TextField txtAgregarDescripcionCategoria;
    @FXML private Button btnGuardarCategoria;
    @FXML private Button btnBorrarCategoria;
    @FXML private Button btnLimpiarCategoria;
    @FXML private TableView tabCategorias;
    @FXML private TableColumn colIdCategoria;
    @FXML private TableColumn colDescripcionCategoria;
}
