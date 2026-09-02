package una.sistemareservas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import una.sistemareservas.model.CategoriaRecursoDTO;
import una.sistemareservas.model.RecursoDTO;
import una.sistemareservas.service.CategoriaService;
import una.sistemareservas.service.RecursoService;

import java.util.List;

public class RecursosViewController {
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
    @FXML private TableColumn<RecursoDTO,String> colDescripcionRecursos;
    @FXML private Label lblAvisos;

    private final CategoriaService categoriaService = new CategoriaService();
    private final RecursoService recursoService = new RecursoService(categoriaService);
    private final ObservableList<RecursoDTO> datosTabla = FXCollections.observableArrayList();

    private void limpiar(){
        txtAgregarDescripcionRecursos.clear();
        txtAgregarIdRecursos.clear();
        txtBusquedaRecursos.clear();
        lblAvisos.setText("");
        cbAgregarCategoriaRecursos.setValue(null);
        cbBusquedaCategoriaRecursos.setValue(null);
        tabRecursos.getSelectionModel().clearSelection();
    }

    private void cargarTabla(List<RecursoDTO> recursos){
        datosTabla.addAll(recursos);

    }

    private void guardar(ActionEvent evento){
        String id = txtAgregarIdRecursos.getText().trim();
        String desc = txtAgregarDescripcionRecursos.getText().trim();
        CategoriaRecursoDTO cat = cbBusquedaCategoriaRecursos.getValue();


        if(id.isEmpty() || cat == null || desc.isEmpty()){
            lblAvisos.setText("Debe llenar todos los campos para agrefgar");
            return;
        }

        if(recursoService.buscarID(id) != null){
            lblAvisos.setText("Ya existe un recurso con ese ID");
            return;
        }

        RecursoDTO recurso = new RecursoDTO(id, desc, cat);

        if(recursoService.agregar(recurso)){
            cargarTabla(recursoService.listar());
            limpiar();
        }else{
            lblAvisos.setText("Error al agregar el recurso");
        }
    }


    private void borrar(ActionEvent evento){
        String id = txtAgregarIdRecursos.getText().trim();

        if(id.isEmpty()){
            lblAvisos.setText("Debe ingresar un ID");
            return;
        }


        if(recursoService.eliminar(id)){
            cargarTabla(recursoService.listar());
            limpiar();
        }else{
            lblAvisos.setText("No se borró el recurso");
        }
    }


}
