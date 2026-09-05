package una.sistemareservas.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.service.CategoriaService;
import una.sistemareservas.service.RecursoService;

import java.util.ArrayList;
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
    private final ObservableList<RecursoDTO> datosEnTabla = FXCollections.observableArrayList();


    @FXML
    private void initialize(){
        colIdRecursos.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colDescripcionRecursos.setCellValueFactory(new PropertyValueFactory<>("Descripcion"));

        btnBorrarRecursos.setOnAction(this::borrar);
        btnBuscarRecursos.setOnAction(this::buscar);
        btnGuardarRecursos.setOnAction(this::guardar);
        btnLimpiarRecursos.setOnAction(evento-> limpiar());

        //debo de implementar esto asi para no incluir mas metodos innecesarios en dto
        colCategoriaRecursos.setCellValueFactory(datos ->new javafx.beans.property.SimpleStringProperty(datos.getValue().getCategoria()
                != null ? datos.getValue().getCategoria().getDescripcion(): ""));
        tabRecursos.setItems(datosEnTabla);

        tabRecursos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtAgregarIdRecursos.setText(newValue.getID());
                txtAgregarDescripcionRecursos.setText(newValue.getDescripcion());
                cbAgregarCategoriaRecursos.setValue(newValue.getCategoria());
            }
        });

        cargarCategoria();
        cargarTabla(recursoService.listar());
    }

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
        datosEnTabla.setAll(recursos);

    }

    //categorias en cb
    private void cargarCategoria(){
        ObservableList<CategoriaRecursoDTO> categorias = FXCollections.observableArrayList(categoriaService.listar());
        cbBusquedaCategoriaRecursos.setItems(categorias);
        cbAgregarCategoriaRecursos.setItems(categorias);
    }

    private void guardar(ActionEvent evento){

        CategoriaRecursoDTO cat = cbAgregarCategoriaRecursos.getValue();
        String id = txtAgregarIdRecursos.getText().trim();
        String desc = txtAgregarDescripcionRecursos.getText().trim();



        if(id.isEmpty() || cat == null || desc.isEmpty()){
            lblAvisos.setText("Debe llenar todos los campos para agregar");
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

    private void buscar(ActionEvent evento){
        String desc = txtBusquedaRecursos.getText().trim();

        List<RecursoDTO> res;
        //guarda la categoria seleccionada en cb
        CategoriaRecursoDTO cat = cbBusquedaCategoriaRecursos.getValue();


        if(cat != null){
            //lista todos con la categoria seleccionada
            res = recursoService.listarPorCategoria(cat.getID());

        }else{
            //lista todos si el usuario no
            res = recursoService.listar();
        }

        //en caso de no seleccionar una categoria
        if(!desc.isEmpty()){
            List<RecursoDTO> resultadoFiltrado = new ArrayList<>();

            String busqueda = desc.toLowerCase();//min y mayus por igual

            //filtra sobre categorias, no en toda la lista
            for(RecursoDTO recurso : res){
                if(recurso.getDescripcion().toLowerCase().contains(busqueda)){
                    resultadoFiltrado.add(recurso);
                }
            }
            res = resultadoFiltrado;
        }
        cargarTabla(res);
    }
}
