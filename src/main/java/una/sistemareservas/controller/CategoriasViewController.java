package una.sistemareservas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.protocolo.IService;
import una.sistemareservas.proxy.ServiceProxy;
import una.sistemareservas.utilidades.ReportePDF;

import java.util.ArrayList;
import java.util.List;

public class CategoriasViewController implements Refrescable {

    @FXML private Label lblAvisos;
    @FXML private TextField txtBusquedaCategoria;
    @FXML private Button btnBuscarCategoria;
    @FXML private Button btnImprimirCategoria;
    @FXML private TextField txtAgregarIdCategoria;
    @FXML private TextField txtAgregarDescripcionCategoria;
    @FXML private Button btnGuardarCategoria;
    @FXML private Button btnBorrarCategoria;
    @FXML private Button btnLimpiarCategoria;
    @FXML private TableView<CategoriaRecursoDTO> tabCategorias;
    @FXML private TableColumn<CategoriaRecursoDTO, Integer> colIdCategoria;
    @FXML private TableColumn<CategoriaRecursoDTO, String> colDescripcionCategoria;

    private final IService service = ServiceProxy.instancia();
    private final ObservableList<CategoriaRecursoDTO> datosTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colIdCategoria.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colDescripcionCategoria.setCellValueFactory(new PropertyValueFactory<>("Descripcion"));
        tabCategorias.setItems(datosTabla);

        // El ID lo genera la base de datos: el usuario no lo escribe
        txtAgregarIdCategoria.setEditable(false);
        txtAgregarIdCategoria.setPromptText("(automático)");

        tabCategorias.getSelectionModel().selectedItemProperty().addListener((observable, anterior, nueva) -> {
            if (nueva != null) {
                txtAgregarIdCategoria.setText(String.valueOf(nueva.getID()));
                txtAgregarDescripcionCategoria.setText(nueva.getDescripcion());
            }
        });

        btnBuscarCategoria.setOnAction(this::buscar);
        btnBorrarCategoria.setOnAction(this::borrar);
        btnGuardarCategoria.setOnAction(this::guardar);
        btnLimpiarCategoria.setOnAction(evento -> limpiar());
        btnImprimirCategoria.setOnAction(this::imprimirCategorias);
    }

    @Override
    public void refrescar() {
        try {
            datosTabla.setAll(service.listarCategorias());
        } catch (Exception e) {
            lblAvisos.setText(e.getMessage());
        }
    }

    private void buscar(ActionEvent evento) {
        try {
            datosTabla.setAll(service.buscarCategorias(txtBusquedaCategoria.getText().trim()));
        } catch (Exception e) {
            lblAvisos.setText(e.getMessage());
        }
    }

    private void borrar(ActionEvent evento) {
        String id = txtAgregarIdCategoria.getText().trim();
        if (id.isEmpty()) {
            lblAvisos.setText("Debe seleccionar una categoría de la tabla");
            return;
        }
        try {
            service.eliminarCategoria(Integer.parseInt(id));
            limpiar();
            refrescar();
        } catch (Exception e) {
            lblAvisos.setText(e.getMessage());
        }
    }

    // Si hay una fila seleccionada (hay ID) se actualiza; si no, se agrega una categoría nueva
    private void guardar(ActionEvent evento) {
        String id = txtAgregarIdCategoria.getText().trim();
        String descripcion = txtAgregarDescripcionCategoria.getText().trim();
        try {
            if (id.isEmpty()) {
                service.agregarCategoria(descripcion);
            } else {
                service.actualizarCategoria(Integer.parseInt(id), descripcion);
            }
            limpiar();
            refrescar();
        } catch (Exception e) {
            lblAvisos.setText(e.getMessage());
        }
    }

    private void limpiar() {
        txtAgregarIdCategoria.clear();
        txtAgregarDescripcionCategoria.clear();
        txtBusquedaCategoria.clear();
        lblAvisos.setText("");
        tabCategorias.getSelectionModel().clearSelection();
    }

    @FXML
    private void imprimirCategorias(ActionEvent event) {
        List<String[]> filas = new ArrayList<>();
        for (CategoriaRecursoDTO categoria : tabCategorias.getItems()) {
            filas.add(new String[]{String.valueOf(categoria.getID()), categoria.getDescripcion()});
        }
        ReportePDF.exportarTabla(btnImprimirCategoria.getScene().getWindow(), "Reporte_Categorias.pdf",
                "Reporte de Categorías", null, new String[]{"Id", "Descripción"}, filas);
    }
}
