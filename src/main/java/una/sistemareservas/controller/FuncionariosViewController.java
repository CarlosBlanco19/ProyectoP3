package una.sistemareservas.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
import javafx.stage.FileChooser;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.service.FuncionarioService;
import una.sistemareservas.service.UsuarioService;
import java.io.File;
import java.io.FileOutputStream;
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
        btnImprimirFuncionarios.setOnAction(this::imprimirFuncionarios);

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

        boolean pt;

        if(funcionarioService.buscarID(id) != null){
            pt = funcionarioService.actualizar(id, nom, tel);

        }else{
            pt = funcionarioService.agregar(id, nom, tel);
        }

        if(pt){
            cargarTabla(funcionarioService.listar());
            limpiar();
        }else{
            lblAvisos.setText("No se pudo guardar el funcionario");
        }

    }


    @FXML
    private void imprimirFuncionarios(ActionEvent event) {
        // 1. Abrir ventana para que el usuario elija dónde guardar
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Funcionarios");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("Reporte_Funcionarios.pdf");

        File file = fileChooser.showSaveDialog(btnImprimirFuncionarios.getScene().getWindow());

        if (file != null) {
            try {
                // 2. Crear el documento en formato horizontal (apaisado)
                Document documento = new Document(PageSize.A4.rotate());
                PdfWriter.getInstance(documento, new FileOutputStream(file));

                documento.open();

                // 3. Título y encabezado del PDF
                documento.add(new Paragraph("Reporte de Funcionarios"));
                // Si implementaste la SesionGlobal, puedes poner el ID aquí:
                documento.add(new Paragraph(" ")); // Espacio en blanco

                // 4. Crear tabla de iText con 6 columnas (igual a tu interfaz)
                PdfPTable tablaPdf = new PdfPTable(3);
                tablaPdf.setWidthPercentage(100);

                // Configurar el color y texto de las cabeceras
                String[] encabezados = {"Id", "Nombre", "Telefono"};
                for (String encabezado : encabezados) {
                    PdfPCell celda = new PdfPCell(new Phrase(encabezado));
                    celda.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
                    tablaPdf.addCell(celda);
                }

                // 5. Extraer los datos de tabMisReservas fila por fila
                for (FuncionarioDTO funcionario : tabFuncionarios.getItems()) {
                    tablaPdf.addCell(funcionario.getID());
                    tablaPdf.addCell(funcionario.getNombre());
                    tablaPdf.addCell(funcionario.getTelefono());
                }

                // 6. Añadir tabla y cerrar documento
                documento.add(tablaPdf);
                documento.close();

                lblAvisos.setText("¡El reporte PDF se ha guardado exitosamente!");

            } catch (Exception e) {
                lblAvisos.setText("Ocurrió un error al generar el PDF: " + e.getMessage());
            }
        }
    }




}
