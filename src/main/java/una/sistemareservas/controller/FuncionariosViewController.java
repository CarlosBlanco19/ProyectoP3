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
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.service.FuncionarioService;
import una.sistemareservas.service.UsuarioService;

import java.util.ArrayList;
import java.util.List;

public class FuncionariosViewController {

    @FXML private Label lblAvisos;
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
    @FXML private TableView<FuncionarioDTO> tabFuncionarios;
    @FXML private TableColumn<FuncionarioDTO,String> colIdFuncionarios;
    @FXML private TableColumn<FuncionarioDTO,String> colNombreFuncionarios;
    @FXML private TableColumn<FuncionarioDTO, String> colTelefonoFuncionarios;

    private final FuncionarioService funcionarioService = new FuncionarioService(new UsuarioService());
    private final ObservableList<FuncionarioDTO> datosTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize(){

        btnBorrarFuncionario.setOnAction(this::borrar);
        btnBuscarFuncionario.setOnAction(this::buscar);
        btnGuardarFuncionario.setOnAction(this::guardar);
        btnLimpiarFuncionario.setOnAction(evento-> limpiar());

        colIdFuncionarios.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colNombreFuncionarios.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefonoFuncionarios.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        tabFuncionarios.setItems(datosTabla);
        cargarTabla(funcionarioService.listar());


        tabFuncionarios.getSelectionModel().selectedItemProperty().addListener((observable,old, nuevo) -> {
            if (nuevo != null) {
                txtAgregarId.setText(nuevo.getID());
                txtAgregarNombre.setText(nuevo.getNombre());
                txtAgregarTelefono.setText(nuevo.getTelefono());
            }
        });
    }

    private void limpiar(){
        txtAgregarId.clear();
        txtAgregarNombre.clear();
        txtAgregarTelefono.clear();
        txtBusquedaId.clear();
        txtBusquedaNombre.clear();
        tabFuncionarios.getSelectionModel().clearSelection();
        lblAvisos.setText("");
    }

    private void cargarTabla(List<FuncionarioDTO> lista){
        datosTabla.setAll(lista);
    }

    private void borrar(ActionEvent evento){
        String id = txtAgregarId.getText().trim();


        if(id.isEmpty()) {
            lblAvisos.setText("Debe ingresar un ID");
            return;
        }

        if(funcionarioService.eliminar(id)){
            cargarTabla(funcionarioService.listar());
            limpiar();
        }else{
            lblAvisos.setText("No se eliminó el funcionario");
        }
    }

    private void buscar(ActionEvent evento){
        String id = txtBusquedaId.getText().trim();
        String nom = txtBusquedaNombre.getText().trim();

        if(!id.isEmpty()){ //priorizar busqueda por id
            FuncionarioDTO fun = funcionarioService.buscarID(id);
            List<FuncionarioDTO> list = new ArrayList<>();


            if(fun != null){
                list.add(fun);
            }
            cargarTabla(list);

        }else if(!nom.isEmpty()){
            cargarTabla(funcionarioService.buscarPorNombre(nom));
        }else{
            lblAvisos.setText("Ingrese un ID o Nombre");
            cargarTabla(funcionarioService.listar());
        }


    }


    private void guardar(ActionEvent evento){
        String id = txtAgregarId.getText().trim();
        String nom = txtAgregarNombre.getText().trim();
        String tel = txtAgregarTelefono.getText().trim();

        if(nom.isEmpty() || id.isEmpty()){
            lblAvisos.setText("Debe ingresar un ID y nombre");
            return;
        }

        if(funcionarioService.buscarID(id) != null){
            lblAvisos.setText("Ya existe un funcionario con el mismo ID");
            return;
        }

        if(funcionarioService.agregar(id, tel, nom)){
            cargarTabla(funcionarioService.listar());
            limpiar();
        }else{
            lblAvisos.setText("No se pudo guardar el funcionario");
        }

    }

}
