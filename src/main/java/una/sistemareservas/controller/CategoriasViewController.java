package una.sistemareservas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.service.CategoriaService;

import java.util.List;


public class CategoriasViewController {

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
    @FXML private TableColumn<CategoriaRecursoDTO, String> colIdCategoria;
    @FXML private TableColumn<CategoriaRecursoDTO, String> colDescripcionCategoria;

    private final CategoriaService categoriaService = new CategoriaService();
    private final ObservableList<CategoriaRecursoDTO> datosTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize(){
        colIdCategoria.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colDescripcionCategoria.setCellValueFactory(new PropertyValueFactory<>("Descripcion"));

        cargarTabla(categoriaService.listar());
        tabCategorias.setItems(datosTabla);


        tabCategorias.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtAgregarIdCategoria.setText(newValue.getID());
                txtAgregarDescripcionCategoria.setText(newValue.getDescripcion());
            }
        });




        btnBuscarCategoria.setOnAction((this::buscar));
        btnBorrarCategoria.setOnAction((this::borrar));
        btnGuardarCategoria.setOnAction((this::guardar));
        btnLimpiarCategoria.setOnAction(evento -> limpiar());
    }

    private void cargarTabla(List<CategoriaRecursoDTO> categorias){
        datosTabla.setAll(categorias);
    }

    private void buscar(ActionEvent evento){
        String desc = txtBusquedaCategoria.getText().trim();
        if(desc.isEmpty()){
            cargarTabla(categoriaService.listar());
        }else{
            cargarTabla(categoriaService.buscarPorDescripcion(desc));
        }
    }

    private void borrar(ActionEvent evento){
        String id = txtAgregarIdCategoria.getText().trim();
        if(id.isEmpty()){
            lblAvisos.setText("Debe agregar una categoría");
            return;
        }

        if(categoriaService.eliminar(id)){
            cargarTabla(categoriaService.listar());
            limpiar();
        }else{
            lblAvisos.setText("No se pudo eliminar la categoría");
        }
    }

    private void guardar(ActionEvent evento){
        String id = txtAgregarIdCategoria.getText().trim();
        String desc = txtAgregarDescripcionCategoria.getText().trim();
        if(id.isEmpty()) {
            lblAvisos.setText("Debe agregar un id");
            return;
        }

        boolean bandera; //para determinar si se guardo o no

        if(categoriaService.buscarID(id) != null){
            bandera = categoriaService.actualizar(id, desc);
        }else{
            bandera = categoriaService.agregar(new CategoriaRecursoDTO(id, desc));

        }

        if(bandera){
            cargarTabla(categoriaService.listar());
            limpiar();
        }else{
            lblAvisos.setText("No se guardó la categoría");
        }


    }

    //limpia avisos, listas de catalogos
    private void limpiar(){
        txtAgregarIdCategoria.clear();
        txtAgregarDescripcionCategoria.clear();
        txtBusquedaCategoria.clear();

        lblAvisos.setText("");
        tabCategorias.getSelectionModel().clearSelection();
    }
}
