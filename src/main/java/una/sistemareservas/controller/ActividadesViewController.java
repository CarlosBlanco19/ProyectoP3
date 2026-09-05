package una.sistemareservas.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.service.ReservaService;
import javafx.beans.property.SimpleStringProperty;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileOutputStream;

public class ActividadesViewController {


    @FXML private Button btnBuscarActividades;
    @FXML private Button btnImprimirActividades;
    @FXML private DatePicker dtActividadesFecha;
    @FXML private Label lblAvisosActividades;
    @FXML private TableView<FilaHorario> tabActividades;
    @FXML private TableColumn<FilaHorario, String> colHoraActividades;
    @FXML private TableColumn<FilaHorario, String> colLunes;
    @FXML private TableColumn<FilaHorario, String> colMartes;
    @FXML private TableColumn<FilaHorario, String> colMiercoles;
    @FXML private TableColumn<FilaHorario, String> colJueves;
    @FXML private TableColumn<FilaHorario, String> colViernes;
    @FXML private TableColumn<FilaHorario, String> colSabado;
    @FXML private TableColumn<FilaHorario, String> colDomingo;

    private ReservaService reservaLogic;
    public void setReservaLogic(ReservaService reservaLogic) {
        this.reservaLogic = reservaLogic;
    }

    @FXML
    public void initialize() {
        colHoraActividades.setCellValueFactory(cellData -> cellData.getValue().horaProperty());


        java.util.List<TableColumn<FilaHorario, String>> columnasDias = java.util.Arrays.asList(
                colLunes, colMartes, colMiercoles, colJueves, colViernes, colSabado, colDomingo
        );

        for (int i = 0; i < 7; i++) {
            final int index = i;
            TableColumn<FilaHorario, String> columnaActual = columnasDias.get(i);

            if (columnaActual != null) {
                columnaActual.setCellValueFactory(cellData -> cellData.getValue().diaProperty(index));
            }
        }

        btnBuscarActividades.setOnAction(this::cargarCalendario);
        btnImprimirActividades.setOnAction(this::imprimirActividades);
    }

    @FXML
    private void imprimirActividades(ActionEvent event) {
        // Todas estas 3 lineas es para que el usuario vea donde guarda el pdf.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Actividades");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

        // Esto es como para que le tire un nombre default
        fileChooser.setInitialFileName("Reporte_Actividades_" + dtActividadesFecha.getValue() + ".pdf");

        File file = fileChooser.showSaveDialog(btnImprimirActividades.getScene().getWindow());

        if (file != null) {
            try {
                // crea un documento horizontal para que quepa la tabla.
                Document documento = new Document(PageSize.A4.rotate());
                PdfWriter.getInstance(documento, new FileOutputStream(file));

                documento.open();

                //Todo esto es para ingreasar un titulo.
                documento.add(new Paragraph("Reporte de Calendario de Actividades"));
                documento.add(new Paragraph("Fecha de referencia: " + dtActividadesFecha.getValue()));
                documento.add(new Paragraph(" ")); // Espacio en blanco

                // 4. Crea la tabla ahi
                PdfPTable tablaPdf = new PdfPTable(8);
                tablaPdf.setWidthPercentage(100);

                // le pone titulos a las columnas
                String[] encabezados = {"Hora", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
                for (String encabezado : encabezados) {
                    PdfPCell celda = new PdfPCell(new Phrase(encabezado));
                    celda.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200)); // Fondo gris claro
                    tablaPdf.addCell(celda);
                }

                // 5. Recorre las filas de la tabla de JavaFX y agrega los datos a la tabla PDF
                for (FilaHorario fila : tabActividades.getItems()) {
                    tablaPdf.addCell(fila.getHora());

                    for (int i = 0; i < 7; i++) {
                        String textoActividad = fila.getDia(i);
                        // Si está vacío, ponemos un espacio para que la celda se dibuje bien
                        tablaPdf.addCell(textoActividad.isEmpty() ? " " : textoActividad);
                    }
                }

                documento.add(tablaPdf);
                documento.close();

                mostrarAlerta("¡El reporte PDF se ha guardado exitosamente!");

            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Ocurrió un error al generar el PDF: " + e.getMessage());
            }
        }
    }

    @FXML
    private void cargarCalendario(ActionEvent event) {
        LocalDate fechaSeleccionada = dtActividadesFecha.getValue();

        if (fechaSeleccionada == null) {
            lblAvisosActividades.setText("Por favor seleccione una fecha de referencia primero.");
            return;
        }

        // 1. Obtener el lunes de la semana seleccionada
        LocalDate lunesSemana = fechaSeleccionada.with(DayOfWeek.MONDAY);
        actualizarCabecerasColumnas(lunesSemana);

        // 2. Generar las filas base (ej. de 06:00 a 22:00)
        ObservableList<FilaHorario> filas = FXCollections.observableArrayList();
        for (int hora = 6; hora <= 22; hora++) {
            String horaStr = String.format("%02d:00", hora);
            filas.add(new FilaHorario(horaStr));
        }

        // 3. Poblar las celdas con las reservas de la lógica
        if (reservaLogic != null) {
            List<ReservaDTO> todasLasReservas = reservaLogic.getReservas();

            for (ReservaDTO reserva : todasLasReservas) {
                LocalDate fechaReserva = reserva.getFecha();

                // Si la reserva entra en la semana que estamos mostrando (entre Lunes y Domingo)
                if (!fechaReserva.isBefore(lunesSemana) && fechaReserva.isBefore(lunesSemana.plusDays(7))) {

                    int diaIndex = fechaReserva.getDayOfWeek().getValue() - 1; // Lunes = 0, Domingo = 6
                    int horaInicio = reserva.getHora_init().getHour();

                    // Encontrar la fila correspondiente a esta hora
                    for (FilaHorario fila : filas) {
                        if (fila.getHora().startsWith(String.format("%02d", horaInicio))) {
                            // Asumimos que FuncionarioDTO tiene algún getNombre(), ajusta según tu código
                            String textoActividad = reserva.getActividad() + "\n(" + reserva.getFuncionario().getID() + ")";
                            fila.setDia(diaIndex, textoActividad);
                        }
                    }
                }
            }
        }
        tabActividades.setItems(filas);
    }

    private void actualizarCabecerasColumnas(LocalDate lunesSemana) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        TableColumn[] columnas = {colLunes, colMartes, colMiercoles, colJueves, colViernes, colSabado, colDomingo};
        String[] prefijos = {"lun", "mar", "mié", "jue", "vie", "sáb", "dom"};

        for (int i = 0; i < 7; i++) {
            if (columnas[i] != null) {
                LocalDate fechaDia = lunesSemana.plusDays(i);
                columnas[i].setText(prefijos[i] + " " + fechaDia.format(format));
            }
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public class FilaHorario {
        private final SimpleStringProperty hora;
        private final SimpleStringProperty[] dias;

        public FilaHorario(String hora) {
            this.hora = new SimpleStringProperty(hora);
            this.dias = new SimpleStringProperty[7];
            for (int i = 0; i < 7; i++) {
                this.dias[i] = new SimpleStringProperty("");
            }
        }

        public String getHora() { return hora.get(); }
        public SimpleStringProperty horaProperty() { return hora; }

        public String getDia(int index) { return dias[index].get(); }
        public void setDia(int index, String valor) { this.dias[index].set(valor); }
        public SimpleStringProperty diaProperty(int index) { return dias[index]; }
    }

}
