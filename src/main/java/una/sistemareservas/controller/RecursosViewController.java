package una.sistemareservas.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.protocolo.IService;
import una.sistemareservas.proxy.ServiceProxy;
import una.sistemareservas.utilidades.ReportePDF;

import java.util.ArrayList;
import java.util.List;

public class RecursosViewController implements Refrescable {

    @FXML private ComboBox<CategoriaRecursoDTO> cbBusquedaCategoriaRecursos;
    @FXML private TextField txtBusquedaRecursos;
    @FXML private Button btnBuscarRecursos;
    @FXML private Button btnImprimirRecursos;
    @FXML private TextField txtAgregarIdRecursos;
    @FXML private ComboBox<CategoriaRecursoDTO> cbAgregarCategoriaRecursos;
    @FXML private TextField txtAgregarDescripcionRecursos;
    @FXML private Button btnGuardarRecursos;
    @FXML private Button btnBorrarRecursos;
    @FXML private Button btnLimpiarRecursos;
    @FXML private TableView<RecursoDTO> tabRecursos;
    @FXML private TableColumn<RecursoDTO, String> colIdRecursos;
    @FXML private TableColumn<RecursoDTO, String> colCategoriaRecursos;
    @FXML private TableColumn<RecursoDTO, String> colDescripcionRecursos;
    @FXML private Label lblAvisos;

    private final IService service = ServiceProxy.instancia();
    private final ObservableList<RecursoDTO> datosEnTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colIdRecursos.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colDescripcionRecursos.setCellValueFactory(new PropertyValueFactory<>("Descripcion"));
        colCategoriaRecursos.setCellValueFactory(datos -> new SimpleStringProperty(
                datos.getValue().getCategoria() != null ? datos.getValue().getCategoria().getDescripcion() : ""));
        tabRecursos.setItems(datosEnTabla);

        tabRecursos.getSelectionModel().selectedItemProperty().addListener((observable, anterior, nuevo) -> {
            if (nuevo != null) {
                txtAgregarIdRecursos.setText(nuevo.getID());
                txtAgregarDescripcionRecursos.setText(nuevo.getDescripcion());
                cbAgregarCategoriaRecursos.setValue(nuevo.getCategoria());
            }
        });

        btnBorrarRecursos.setOnAction(this::borrar);
        btnBuscarRecursos.setOnAction(this::buscar);
        btnGuardarRecursos.setOnAction(this::guardar);
        btnLimpiarRecursos.setOnAction(evento -> limpiar());
        btnImprimirRecursos.setOnAction(this::imprimirRecursos);
    }

    // Se vuelven a pedir las categorías (por si el administrador agregó una nueva) y los recursos
    @Override
    public void refrescar() {
        try {
            ObservableList<CategoriaRecursoDTO> categorias = FXCollections.observableArrayList(service.listarCategorias());
            CategoriaRecursoDTO busqueda = cbBusquedaCategoriaRecursos.getValue();
            CategoriaRecursoDTO agregar = cbAgregarCategoriaRecursos.getValue();
            cbBusquedaCategoriaRecursos.setItems(categorias);
            cbAgregarCategoriaRecursos.setItems(categorias);
            cbBusquedaCategoriaRecursos.setValue(busqueda);
            cbAgregarCategoriaRecursos.setValue(agregar);
            datosEnTabla.setAll(service.listarRecursos());
        } catch (Exception e) {
            lblAvisos.setText(e.getMessage());
        }
    }

    private void limpiar() {
        txtAgregarDescripcionRecursos.clear();
        txtAgregarIdRecursos.clear();
        txtBusquedaRecursos.clear();
        lblAvisos.setText("");
        cbAgregarCategoriaRecursos.setValue(null);
        cbBusquedaCategoriaRecursos.setValue(null);
        tabRecursos.getSelectionModel().clearSelection();
    }

    // Si el ID ya existe se actualiza; si no, se agrega
    private void guardar(ActionEvent evento) {
        RecursoDTO recurso = new RecursoDTO(txtAgregarIdRecursos.getText().trim(),
                txtAgregarDescripcionRecursos.getText().trim(), cbAgregarCategoriaRecursos.getValue());
        try {
            if (!recurso.getID().isEmpty() && service.buscarRecurso(recurso.getID()) != null) {
                service.actualizarRecurso(recurso);
            } else {
                service.agregarRecurso(recurso);
            }
            limpiar();
            refrescar();
        } catch (Exception e) {
            lblAvisos.setText(e.getMessage());
        }
    }

    private void borrar(ActionEvent evento) {
        String id = txtAgregarIdRecursos.getText().trim();
        if (id.isEmpty()) {
            lblAvisos.setText("Debe ingresar un ID");
            return;
        }
        try {
            service.eliminarRecurso(id);
            limpiar();
            refrescar();
        } catch (Exception e) {
            lblAvisos.setText(e.getMessage());
        }
    }

    // Filtra por categoría (si se escogió una) y por texto de la descripción (si se escribió)
    private void buscar(ActionEvent evento) {
        CategoriaRecursoDTO categoria = cbBusquedaCategoriaRecursos.getValue();
        String texto = txtBusquedaRecursos.getText().trim().toLowerCase();
        try {
            List<RecursoDTO> resultado = categoria != null
                    ? service.listarRecursosPorCategoria(categoria.getID())
                    : service.listarRecursos();
            if (!texto.isEmpty()) {
                resultado.removeIf(recurso -> !recurso.getDescripcion().toLowerCase().contains(texto));
            }
            datosEnTabla.setAll(resultado);
        } catch (Exception e) {
            lblAvisos.setText(e.getMessage());
        }
    }

    @FXML
    private void imprimirRecursos(ActionEvent event) {
        List<String[]> filas = new ArrayList<>();
        for (RecursoDTO recurso : tabRecursos.getItems()) {
            filas.add(new String[]{recurso.getID(),
                    recurso.getCategoria() != null ? recurso.getCategoria().getDescripcion() : "",
                    recurso.getDescripcion()});
        }
        ReportePDF.exportarTabla(btnImprimirRecursos.getScene().getWindow(), "Reporte_Recursos.pdf",
                "Reporte de Recursos", null, new String[]{"Id", "Categoría", "Descripción"}, filas);
    }
}
