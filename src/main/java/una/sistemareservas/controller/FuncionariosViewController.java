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
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.protocolo.IService;
import una.sistemareservas.proxy.ServiceProxy;
import una.sistemareservas.utilidades.ReportePDF;

import java.util.ArrayList;
import java.util.List;

public class FuncionariosViewController implements Refrescable {

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
    @FXML private TableColumn<FuncionarioDTO, String> colIdFuncionarios;
    @FXML private TableColumn<FuncionarioDTO, String> colNombreFuncionarios;
    @FXML private TableColumn<FuncionarioDTO, String> colTelefonoFuncionarios;

    private final IService service = ServiceProxy.instancia();
    private final ObservableList<FuncionarioDTO> datosTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        btnBorrarFuncionario.setOnAction(this::borrar);
        btnBuscarFuncionario.setOnAction(this::buscar);
        btnGuardarFuncionario.setOnAction(this::guardar);
        btnLimpiarFuncionario.setOnAction(evento -> limpiar());
        btnImprimirFuncionarios.setOnAction(this::imprimirFuncionarios);

        colIdFuncionarios.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colNombreFuncionarios.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefonoFuncionarios.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        tabFuncionarios.setItems(datosTabla);

        tabFuncionarios.getSelectionModel().selectedItemProperty().addListener((observable, anterior, nuevo) -> {
            if (nuevo != null) {
                txtAgregarId.setText(nuevo.getID());
                txtAgregarNombre.setText(nuevo.getNombre());
                txtAgregarTelefono.setText(nuevo.getTelefono());
            }
        });
    }

    @Override
    public void refrescar() {
        try {
            datosTabla.setAll(service.listarFuncionarios());
        } catch (Exception e) {
            lblAvisos.setText(e.getMessage());
        }
    }

    private void limpiar() {
        txtAgregarId.clear();
        txtAgregarNombre.clear();
        txtAgregarTelefono.clear();
        txtBusquedaId.clear();
        txtBusquedaNombre.clear();
        tabFuncionarios.getSelectionModel().clearSelection();
        lblAvisos.setText("");
    }

    private void borrar(ActionEvent evento) {
        String id = txtAgregarId.getText().trim();
        if (id.isEmpty()) {
            lblAvisos.setText("Debe ingresar un ID");
            return;
        }
        try {
            service.eliminarFuncionario(id);
            limpiar();
            refrescar();
        } catch (Exception e) {
            lblAvisos.setText(e.getMessage());
        }
    }

    private void buscar(ActionEvent evento) {
        String id = txtBusquedaId.getText().trim();
        String nombre = txtBusquedaNombre.getText().trim();
        try {
            if (!id.isEmpty()) { // Se le da prioridad a la búsqueda por ID
                List<FuncionarioDTO> resultado = new ArrayList<>();
                FuncionarioDTO funcionario = service.buscarFuncionario(id);
                if (funcionario != null) {
                    resultado.add(funcionario);
                }
                datosTabla.setAll(resultado);
                lblAvisos.setText(resultado.isEmpty() ? "No existe un funcionario con ese ID" : "");
            } else if (!nombre.isEmpty()) {
                datosTabla.setAll(service.buscarFuncionariosPorNombre(nombre));
                lblAvisos.setText("");
            } else {
                lblAvisos.setText("Ingrese un ID o Nombre");
                refrescar();
            }
        } catch (Exception e) {
            lblAvisos.setText(e.getMessage());
        }
    }

    // Si el ID ya existe se actualiza; si no, se agrega (la clave inicial es el mismo ID)
    private void guardar(ActionEvent evento) {
        FuncionarioDTO funcionario = new FuncionarioDTO(txtAgregarId.getText().trim(),
                txtAgregarNombre.getText().trim(), txtAgregarTelefono.getText().trim());
        try {
            if (!funcionario.getID().isEmpty() && service.buscarFuncionario(funcionario.getID()) != null) {
                service.actualizarFuncionario(funcionario);
            } else {
                service.agregarFuncionario(funcionario);
            }
            limpiar();
            refrescar();
        } catch (Exception e) {
            lblAvisos.setText(e.getMessage());
        }
    }

    @FXML
    private void imprimirFuncionarios(ActionEvent event) {
        List<String[]> filas = new ArrayList<>();
        for (FuncionarioDTO funcionario : tabFuncionarios.getItems()) {
            filas.add(new String[]{funcionario.getID(), funcionario.getNombre(), funcionario.getTelefono()});
        }
        ReportePDF.exportarTabla(btnImprimirFuncionarios.getScene().getWindow(), "Reporte_Funcionarios.pdf",
                "Reporte de Funcionarios", null, new String[]{"Id", "Nombre", "Teléfono"}, filas);
    }
}
