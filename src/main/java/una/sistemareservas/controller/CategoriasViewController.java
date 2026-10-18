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
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.exception.CategoriaException;
import una.sistemareservas.service.CategoriaService;
import java.io.File;
import java.io.FileOutputStream;
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
    @FXML private TableColumn<CategoriaRecursoDTO, Integer> colIdCategoria;
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
                txtAgregarIdCategoria.setText(String.valueOf(newValue.getID()));
                txtAgregarDescripcionCategoria.setText(newValue.getDescripcion());
            }
        });

        btnBuscarCategoria.setOnAction((this::buscar));
        btnBorrarCategoria.setOnAction((this::borrar));
        btnGuardarCategoria.setOnAction((this::guardar));
        btnLimpiarCategoria.setOnAction(evento -> limpiar());
        btnImprimirCategoria.setOnAction(this::imprimirCategorias);
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
            lblAvisos.setText("Debe seleccionar una categoría de la tabla");
            return;
        }

        try {
            categoriaService.eliminar(Integer.parseInt(id));
            cargarTabla(categoriaService.listar());
            limpiar();
        } catch (CategoriaException e) {
            lblAvisos.setText(e.getMessage());
        }
    }

    private void guardar(ActionEvent evento){
        // El ID solo tiene valor si se selecciono una fila de la tabla (entonces se actualiza)
        String id = txtAgregarIdCategoria.getText().trim();
        String desc = txtAgregarDescripcionCategoria.getText().trim();

        try {
            if(id.isEmpty()){
                categoriaService.agregar(new CategoriaRecursoDTO(0, desc));
            }else{
                categoriaService.actualizar(Integer.parseInt(id), desc);
            }
            cargarTabla(categoriaService.listar());
            limpiar();
        } catch (CategoriaException e) {
            lblAvisos.setText(e.getMessage());
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

    @FXML
    private void imprimirCategorias(ActionEvent event) {
        // 1. Abrir ventana para que el usuario elija dónde guardar
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Categorias");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("Reporte_Categorias.pdf");

        File file = fileChooser.showSaveDialog(btnImprimirCategoria.getScene().getWindow());

        if (file != null) {
            try {
                // 2. Crear el documento en formato horizontal (apaisado)
                Document documento = new Document(PageSize.A4.rotate());
                PdfWriter.getInstance(documento, new FileOutputStream(file));

                documento.open();

                // 3. Título y encabezado del PDF
                documento.add(new Paragraph("Reporte de Categorias"));
                documento.add(new Paragraph(" ")); // Espacio en blanco

                // 4. Crear tabla de iText con 6 columnas (igual a tu interfaz)
                PdfPTable tablaPdf = new PdfPTable(2);
                tablaPdf.setWidthPercentage(100);

                // Configurar el color y texto de las cabeceras
                String[] encabezados = {"Id", "Descripcion"};
                for (String encabezado : encabezados) {
                    PdfPCell celda = new PdfPCell(new Phrase(encabezado));
                    celda.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
                    tablaPdf.addCell(celda);
                }

                // 5. Extraer los datos de tabMisReservas fila por fila
                for (CategoriaRecursoDTO categoria : tabCategorias.getItems()) {
                    tablaPdf.addCell(String.valueOf(categoria.getID()));
                    tablaPdf.addCell(categoria.getDescripcion());


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
