package una.sistemareservas.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.protocolo.IService;
import una.sistemareservas.proxy.ServiceProxy;
import una.sistemareservas.utilidades.Dialogos;
import una.sistemareservas.utilidades.Estadisticas;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class EstadisticasViewController implements Refrescable {

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

    private final IService service = ServiceProxy.instancia();

    @FXML
    private void initialize() {
        colEstCategoriaRecursos.setCellValueFactory(cell -> cell.getValue().nombreProperty());
        colEstCantidadRecursos.setCellValueFactory(cell -> cell.getValue().cantidadProperty());
        colEstSemanaActividades.setCellValueFactory(cell -> cell.getValue().nombreProperty());
        colEstCantidadActividades.setCellValueFactory(cell -> cell.getValue().cantidadProperty());

        btnCargarRecursos.setOnAction(this::cargarEstadisticasRecursos);
        btnCargarActividades.setOnAction(this::cargarEstadisticasActividades);
        btnImprimirEstadisticas.setOnAction(this::imprimirEstadisticas);

        // Para que los gráficos no salgan desordenados al recargarlos
        bcRecursos.setAnimated(false);
        bcActividades.setAnimated(false);
        if (bcRecursos.getXAxis() instanceof CategoryAxis eje) {
            eje.setTickLabelRotation(0);
        }
        bcRecursos.setCategoryGap(40);
        bcActividades.setCategoryGap(40);
    }

    // Al volver a la pestaña se recalculan los rangos que ya estaban cargados
    @Override
    public void refrescar() {
        if (rangoValido(dtDesdeRecursosFecha.getValue(), dtHastaRecursosFecha.getValue())) {
            cargarEstadisticasRecursos(null);
        }
        if (rangoValido(dtDesdeActividadesFecha.getValue(), dtHastaActividadesFecha.getValue())) {
            cargarEstadisticasActividades(null);
        }
    }

    private boolean rangoValido(LocalDate desde, LocalDate hasta) {
        return desde != null && hasta != null && !desde.isAfter(hasta);
    }

    private void cargarEstadisticasRecursos(ActionEvent event) {
        LocalDate desde = dtDesdeRecursosFecha.getValue();
        LocalDate hasta = dtHastaRecursosFecha.getValue();
        if (!rangoValido(desde, hasta)) {
            Dialogos.advertencia("Por favor seleccione un rango de fechas válido.");
            return;
        }
        try {
            List<ReservaDTO> reservas = service.listarReservas();
            actualizarTablaYGrafico(tabEstadisticasRecursos, bcRecursos,
                    Estadisticas.recursosPorCategoria(reservas, desde, hasta), "Recursos");
        } catch (Exception e) {
            Dialogos.advertencia(e.getMessage());
        }
    }

    private void cargarEstadisticasActividades(ActionEvent event) {
        LocalDate desde = dtDesdeActividadesFecha.getValue();
        LocalDate hasta = dtHastaActividadesFecha.getValue();
        if (!rangoValido(desde, hasta)) {
            Dialogos.advertencia("Por favor seleccione un rango de fechas válido.");
            return;
        }
        try {
            List<ReservaDTO> reservas = service.listarReservas();
            actualizarTablaYGrafico(tabEstadisticasActividades, bcActividades,
                    Estadisticas.actividadesPorSemana(reservas, desde, hasta), "Actividades");
        } catch (Exception e) {
            Dialogos.advertencia(e.getMessage());
        }
    }

    private void actualizarTablaYGrafico(TableView<EstadisticaItem> tabla, BarChart<String, Number> grafico,
                                         Map<String, Integer> datos, String nombreSerie) {
        ObservableList<EstadisticaItem> filas = FXCollections.observableArrayList();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName(nombreSerie);
        for (Map.Entry<String, Integer> entrada : datos.entrySet()) {
            filas.add(new EstadisticaItem(entrada.getKey(), entrada.getValue()));
            serie.getData().add(new XYChart.Data<>(entrada.getKey(), entrada.getValue()));
        }
        tabla.setItems(filas);
        grafico.getData().clear();
        grafico.layout();
        grafico.getData().add(serie);
    }

    // Una fila de las tablas de estadísticas
    public static class EstadisticaItem {
        private final SimpleStringProperty nombre;
        private final SimpleIntegerProperty cantidad;

        public EstadisticaItem(String nombre, int cantidad) {
            this.nombre = new SimpleStringProperty(nombre);
            this.cantidad = new SimpleIntegerProperty(cantidad);
        }

        public SimpleStringProperty nombreProperty() { return nombre; }
        public SimpleIntegerProperty cantidadProperty() { return cantidad; }
    }

    // =====================================================================
    //                    Reporte PDF (tablas + gráficos)
    // =====================================================================

    @FXML
    private void imprimirEstadisticas(ActionEvent event) {
        if (tabEstadisticasRecursos.getItems().isEmpty() && tabEstadisticasActividades.getItems().isEmpty()) {
            Dialogos.advertencia("No hay estadísticas cargadas para generar el reporte.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Estadísticas");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("Reporte_Estadisticas_" + LocalDate.now() + ".pdf");
        File file = fileChooser.showSaveDialog(btnImprimirEstadisticas.getScene().getWindow());
        if (file == null) {
            return;
        }
        try (FileOutputStream salida = new FileOutputStream(file)) {
            Document documento = new Document(PageSize.A4);
            PdfWriter.getInstance(documento, salida);
            documento.open();
            documento.add(new Paragraph("Reporte General de Estadísticas"));
            documento.add(new Paragraph("Fecha de impresión: " + LocalDate.now()));
            documento.add(new Paragraph(" "));

            if (!tabEstadisticasRecursos.getItems().isEmpty()) {
                documento.add(new Paragraph("1. Recursos más utilizados ("
                        + dtDesdeRecursosFecha.getValue() + " al " + dtHastaRecursosFecha.getValue() + ")"));
                documento.add(new Paragraph(" "));
                documento.add(tablaPdf("Categoría", tabEstadisticasRecursos.getItems()));
                documento.add(new Paragraph(" "));
                agregarGraficoAlDocumento(documento, bcRecursos);
                documento.add(new Paragraph(" "));
            }
            if (!tabEstadisticasActividades.getItems().isEmpty()) {
                documento.add(new Paragraph("2. Actividades por semana ("
                        + dtDesdeActividadesFecha.getValue() + " al " + dtHastaActividadesFecha.getValue() + ")"));
                documento.add(new Paragraph(" "));
                documento.add(tablaPdf("Semana", tabEstadisticasActividades.getItems()));
                documento.add(new Paragraph(" "));
                agregarGraficoAlDocumento(documento, bcActividades);
            }
            documento.close();
            Dialogos.informacion("Reporte", "¡El reporte PDF se ha guardado exitosamente!");
        } catch (Exception e) {
            Dialogos.error("Error al guardar el documento PDF: " + e.getMessage());
        }
    }

    private PdfPTable tablaPdf(String primeraColumna, List<EstadisticaItem> items) {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        for (String encabezado : new String[]{primeraColumna, "Cantidad"}) {
            PdfPCell celda = new PdfPCell(new Phrase(encabezado));
            celda.setBackgroundColor(new BaseColor(200, 200, 200));
            tabla.addCell(celda);
        }
        for (EstadisticaItem item : items) {
            tabla.addCell(item.nombreProperty().get());
            tabla.addCell(String.valueOf(item.cantidadProperty().get()));
        }
        return tabla;
    }

    // Toma una "foto" del gráfico de JavaFX y la mete en el PDF
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
