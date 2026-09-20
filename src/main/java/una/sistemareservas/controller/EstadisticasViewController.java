package una.sistemareservas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.chart.BarChart;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.EstadoReserva;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.exception.ReservaException;
import una.sistemareservas.service.CategoriaService;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.service.RecursoService;
import una.sistemareservas.service.ReservaService;
import una.sistemareservas.service.UsuarioService;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileOutputStream;
import javafx.scene.image.WritableImage;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import com.itextpdf.text.Image;
import com.itextpdf.text.Element;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class EstadisticasViewController {
    @FXML private DatePicker dtDesdeRecursosFecha;
    @FXML private DatePicker dtHastaRecursosFecha;
    @FXML private Button btnCargarRecursos;
    @FXML private TableView<EstadisticaItem> tabEstadisticasRecursos;
    @FXML private TableColumn<EstadisticaItem, String> colEstCategoriaRecursos;
    @FXML private TableColumn<EstadisticaItem, Number> colEstCantidadRecursos;
    @FXML private BarChart<String, Number> bcRecursos;
    @FXML private DatePicker dtDesdeActividadesFecha;
    @FXML private DatePicker dtHastaActividadesFecha;
    @FXML private Button btnCargarActividades;
    @FXML private TableView<EstadisticaItem> tabEstadisticasActividades;
    @FXML private TableColumn<EstadisticaItem, String> colEstSemanaActividades;
    @FXML private TableColumn<EstadisticaItem, Number> colEstCantidadActividades;
    @FXML private BarChart<String, Number> bcActividades;
    @FXML private Button btnImprimirEstadisticas;

    private final CategoriaService categoriaService = new CategoriaService();
    private final ObservableList<CategoriaRecursoDTO> datosTablacategorias = FXCollections.observableArrayList();
    private final RecursoService recursoService = new RecursoService(categoriaService);
    private final UsuarioService usuarioService = new UsuarioService();
    private ReservaService reservaService;

    @FXML
    private void initialize() {
        //construccion con exception
        try {
            reservaService = new ReservaService(usuarioService, categoriaService, recursoService);
        } catch (ReservaException e) {
            mostrarAlerta("No se cargaron los datos");
        }

        //Configura columnas de tabla de Recursos
        colEstCategoriaRecursos.setCellValueFactory(cell -> cell.getValue().nombreProperty());
        colEstCantidadRecursos.setCellValueFactory(cell -> cell.getValue().cantidadProperty());

        //Configura columnas de tabla de Actividades
        colEstSemanaActividades.setCellValueFactory(cell -> cell.getValue().nombreProperty());
        colEstCantidadActividades.setCellValueFactory(cell -> cell.getValue().cantidadProperty());

        btnCargarRecursos.setOnAction(this::cargarEstadisticasRecursos);
        btnCargarActividades.setOnAction(this::cargarEstadisticasActividades);
        btnImprimirEstadisticas.setOnAction(this::imprimirEstadisticas);
        //Todo esto es para que el grafico no salga bugueado.
        bcRecursos.setAnimated(false);
        bcActividades.setAnimated(false);
        if (bcRecursos.getXAxis() instanceof javafx.scene.chart.CategoryAxis) {
            ((javafx.scene.chart.CategoryAxis) bcRecursos.getXAxis()).setTickLabelRotation(0);
        }
        bcRecursos.setCategoryGap(40);
        bcActividades.setCategoryGap(40);

    }

    private void cargarEstadisticasRecursos(ActionEvent event) {

        LocalDate desde = dtDesdeRecursosFecha.getValue();
        LocalDate hasta = dtHastaRecursosFecha.getValue();

        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            mostrarAlerta("Por favor seleccione un rango de fechas válido.");
            return;
        }

        // Mapa para contar: "Nombre de la Categoria" -> Cantidad
        Map<String, Integer> conteoCategorias = new HashMap<>();

        if (reservaService != null) {
            for (ReservaDTO reserva : reservaService.getReservas()) {
                if (reserva.getEstado() == EstadoReserva.ACTIVA
                        && !reserva.getFecha().isBefore(desde)
                        && !reserva.getFecha().isAfter(hasta)) {

                    // Cuenta cada recurso por su categoría
                    for (RecursoDTO recurso : reserva.getRecursos()) {
                        String nombreCat = recurso.getCategoria().getDescripcion();
                        conteoCategorias.put(nombreCat, conteoCategorias.getOrDefault(nombreCat, 0) + 1);
                    }
                }
            }
        }

        actualizarTablaYGrafico(tabEstadisticasRecursos, bcRecursos, conteoCategorias, "Recurso");
    }

    private void cargarEstadisticasActividades(ActionEvent event) {
        LocalDate desde = dtDesdeActividadesFecha.getValue();
        LocalDate hasta = dtHastaActividadesFecha.getValue();

        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            mostrarAlerta("Por favor seleccione un rango de fechas válido.");
            return;
        }

        Map<String, Integer> conteoSemanas = new HashMap<>();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (reservaService != null) {
            for (ReservaDTO reserva : reservaService.getReservas()) {
                if (reserva.getEstado() == EstadoReserva.ACTIVA
                        && !reserva.getFecha().isBefore(desde)
                        && !reserva.getFecha().isAfter(hasta)) {

                    // Saca el lunes de la semana de la reserva para agrupar por SEMANAS
                    LocalDate lunesSemana = reserva.getFecha().with(DayOfWeek.MONDAY);
                    String semanaStr = lunesSemana.format(formato);

                    conteoSemanas.put(semanaStr, conteoSemanas.getOrDefault(semanaStr, 0) + 1);
                }
            }
        }
        actualizarTablaYGrafico(tabEstadisticasActividades, bcActividades, conteoSemanas, "Semana");
    }

    private void actualizarTablaYGrafico(TableView<EstadisticaItem> tabla, BarChart<String, Number> grafico,
                                         Map<String, Integer> datos, String nombreSerie) {
        ObservableList<EstadisticaItem> listaItems = FXCollections.observableArrayList();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName(nombreSerie);
        for (Map.Entry<String, Integer> entrada : datos.entrySet()) {
            listaItems.add(new EstadisticaItem(entrada.getKey(), entrada.getValue()));
            serie.getData().add(new XYChart.Data<>(entrada.getKey(), entrada.getValue()));
        }

        // Actualiza Tabla
        tabla.setItems(listaItems);

        // Limpia y actualiza le grafico
        grafico.getData().clear();
        grafico.layout();
        grafico.getData().add(serie);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    //Esta clase hace que la tabla pueda mostrar los datos de manera correcta.
    public static class EstadisticaItem {
        private final javafx.beans.property.SimpleStringProperty nombre;
        private final javafx.beans.property.SimpleIntegerProperty cantidad;

        public EstadisticaItem(String nombre, int cantidad) {
            this.nombre = new javafx.beans.property.SimpleStringProperty(nombre);
            this.cantidad = new javafx.beans.property.SimpleIntegerProperty(cantidad);
        }

        public javafx.beans.property.SimpleStringProperty nombreProperty() { return nombre; }
        public javafx.beans.property.SimpleIntegerProperty cantidadProperty() { return cantidad; }
    }

    @FXML
    private void imprimirEstadisticas(ActionEvent event) {
        if (tabEstadisticasRecursos.getItems().isEmpty() && tabEstadisticasActividades.getItems().isEmpty()) {
            mostrarAlerta("No hay estadísticas cargadas para generar el reporte.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Estadísticas");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("Reporte_Estadisticas_" + LocalDate.now() + ".pdf");

        File file = fileChooser.showSaveDialog(btnImprimirEstadisticas.getScene().getWindow());

        if (file != null) {
            try {
                Document documento = new Document(PageSize.A4);
                PdfWriter.getInstance(documento, new FileOutputStream(file));
                documento.open();

                documento.add(new Paragraph("Reporte General de Estadísticas"));
                documento.add(new Paragraph("Fecha de impresión: " + LocalDate.now()));
                documento.add(new Paragraph(" "));

                // --- SECCIÓN 1: RECURSOS ---
                if (!tabEstadisticasRecursos.getItems().isEmpty()) {
                    documento.add(new Paragraph("1. Recursos más utilizados ("
                            + dtDesdeRecursosFecha.getValue() + " al " + dtHastaRecursosFecha.getValue() + ")"));
                    documento.add(new Paragraph(" "));

                    PdfPTable tablaRecursos = new PdfPTable(2);
                    tablaRecursos.setWidthPercentage(100);

                    PdfPCell celdaCat = new PdfPCell(new Phrase("Categoría"));
                    PdfPCell celdaCant = new PdfPCell(new Phrase("Cantidad"));
                    celdaCat.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
                    celdaCant.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
                    tablaRecursos.addCell(celdaCat);
                    tablaRecursos.addCell(celdaCant);

                    for (EstadisticaItem item : tabEstadisticasRecursos.getItems()) {
                        tablaRecursos.addCell(item.nombreProperty().get());
                        tablaRecursos.addCell(String.valueOf(item.cantidadProperty().get()));
                    }
                    documento.add(tablaRecursos);
                    documento.add(new Paragraph(" "));

                    agregarGraficoAlDocumento(documento, bcRecursos);
                    documento.add(new Paragraph(" "));
                }

                // --- SECCIÓN 2: ACTIVIDADES ---
                if (!tabEstadisticasActividades.getItems().isEmpty()) {
                    documento.add(new Paragraph("2. Actividades por semana ("
                            + dtDesdeActividadesFecha.getValue() + " al " + dtHastaActividadesFecha.getValue() + ")"));
                    documento.add(new Paragraph(" "));

                    PdfPTable tablaActividades = new PdfPTable(2);
                    tablaActividades.setWidthPercentage(100);

                    PdfPCell celdaSemana = new PdfPCell(new Phrase("Semana"));
                    PdfPCell celdaCantAct = new PdfPCell(new Phrase("Cantidad"));
                    celdaSemana.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
                    celdaCantAct.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
                    tablaActividades.addCell(celdaSemana);
                    tablaActividades.addCell(celdaCantAct);

                    for (EstadisticaItem item : tabEstadisticasActividades.getItems()) {
                        tablaActividades.addCell(item.nombreProperty().get());
                        tablaActividades.addCell(String.valueOf(item.cantidadProperty().get()));
                    }
                    documento.add(tablaActividades);
                    documento.add(new Paragraph(" "));

                    agregarGraficoAlDocumento(documento, bcActividades);
                }

                documento.close();
                mostrarAlerta("¡El reporte PDF se ha guardado exitosamente!");

            } catch (Exception e) {
                mostrarAlerta("Error al guardar el documento PDF: " + e.getMessage());
            }
        }
    }

    private void agregarGraficoAlDocumento(Document documento, BarChart<String, Number> grafico) throws Exception {
        WritableImage fxImage = grafico.snapshot(new SnapshotParameters(), null);

        int width = (int) fxImage.getWidth();
        int height = (int) fxImage.getHeight();

        PixelReader reader = fxImage.getPixelReader();
        byte[] rgbData = new byte[width * height * 3];

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                // Convertir el color (que va de 0.0 a 1.0) a bytes (de 0 a 255)
                rgbData[index++] = (byte) (color.getRed() * 255);
                rgbData[index++] = (byte) (color.getGreen() * 255);
                rgbData[index++] = (byte) (color.getBlue() * 255);
            }
        }


        Image pdfImage = Image.getInstance(width, height, 3, 8, rgbData);

        pdfImage.scaleToFit(450, 300);
        pdfImage.setAlignment(Element.ALIGN_CENTER);

        documento.add(pdfImage);
    }


}
