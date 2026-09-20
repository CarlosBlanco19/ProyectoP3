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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.service.CategoriaService;
import una.sistemareservas.service.RecursoService;

import java.io.File;
import java.io.FileOutputStream;
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
        btnImprimirRecursos.setOnAction(this::imprimirRecursos);

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

        boolean pt;

        if(recursoService.buscarID(id) != null){
            pt = recursoService.actualizar(id, desc, cat);
        }else {

            RecursoDTO recurso = new RecursoDTO(id, desc, cat);
            pt = recursoService.agregar(recurso);
        }

        if(pt){
            cargarTabla(recursoService.listar());
            limpiar();
        }else{
            lblAvisos.setText("Error al agregar/actualizar el recurso");
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

    @FXML
    private void imprimirRecursos(ActionEvent event) {
        // 1. Abrir ventana para que el usuario elija dónde guardar
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Recursos");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("Reporte_Recursos.pdf");

        File file = fileChooser.showSaveDialog(btnImprimirRecursos.getScene().getWindow());

        if (file != null) {
            try {
                // 2. Crear el documento en formato horizontal (apaisado)
                Document documento = new Document(PageSize.A4.rotate());
                PdfWriter.getInstance(documento, new FileOutputStream(file));

                documento.open();

                // 3. Título y encabezado del PDF
                documento.add(new Paragraph("Reporte de Recursos"));
                // Si implementaste la SesionGlobal, puedes poner el ID aquí:
                documento.add(new Paragraph(" ")); // Espacio en blanco

                // 4. Crear tabla de iText con 6 columnas (igual a tu interfaz)
                PdfPTable tablaPdf = new PdfPTable(3);
                tablaPdf.setWidthPercentage(100);

                // Configurar el color y texto de las cabeceras
                String[] encabezados = {"Id", "Categoria", "Descripcion"};
                for (String encabezado : encabezados) {
                    PdfPCell celda = new PdfPCell(new Phrase(encabezado));
                    celda.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
                    tablaPdf.addCell(celda);
                }

                // 5. Extraer los datos de tabMisReservas fila por fila
                for (RecursoDTO recurso : tabRecursos.getItems()) {
                    tablaPdf.addCell(recurso.getID());
                    tablaPdf.addCell(recurso.getCategoria().toString());
                    tablaPdf.addCell(recurso.getDescripcion());

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
