package una.sistemareservas.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.service.ReservaService;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.service.CategoriaService;
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
import java.util.stream.Collectors;


public class ReservasViewController {
    @FXML private Label lblAvisosReservas;
    @FXML private TextArea txtPromptFrase;
    @FXML private TextField txtActividad;
    @FXML private Button btnExtraerIA;
    @FXML private DatePicker dtReservarFecha;
    @FXML private ComboBox<String> cbHoraInicio;
    @FXML private ComboBox<String> cbHoraFinal;
    @FXML private ListView<CategoriaRecursoDTO> lvListaCategorias;
    @FXML private Button btnGuardarReserva;
    @FXML private Button btnCancelarReserva;
    @FXML private Button btnLimpiarReserva;
    @FXML private TableView<ReservaDTO> tabMisReservas;
    @FXML private TableColumn<ReservaDTO, String> colIdReservas;
    @FXML private TableColumn<ReservaDTO, String> colActividadReservas;
    @FXML private TableColumn<ReservaDTO, String> colFechaReservas;
    @FXML private TableColumn<ReservaDTO, String> colHorarioReservas;
    @FXML private TableColumn<ReservaDTO, String> colRecursosReservas;
    @FXML private TableColumn<ReservaDTO, String> colEstadoReservas;
    @FXML private Button btnImprimirReservas;

    private ReservaService reservaLogic;
    private CategoriaService categoriaLogic;

    public void setLogicas(ReservaService reservaLogic, CategoriaService categoriaLogic) {
        this.reservaLogic = reservaLogic;
        this.categoriaLogic = categoriaLogic;
        cargarDatosIniciales();
    }

    @FXML
    public void initialize(){
        for (int h = 6; h < 22; h++) {
            String horaFormateada = String.format("%02d:00", h);
            cbHoraFinal.getItems().add(horaFormateada);
            cbHoraInicio.getItems().add(horaFormateada);
        }
        //Esto es para que se pueda seleccionar multiples categorias.
        lvListaCategorias.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // 3. Configurar cómo se ve el texto en el ListView (para que muestre la descripción)
        lvListaCategorias.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(CategoriaRecursoDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDescripcion());
            }
        });

        // 4. Configurar las columnas de la tabla "Mis reservas"
        colIdReservas.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getID()));
        colActividadReservas.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getActividad()));
        colFechaReservas.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFecha().toString()));
        colHorarioReservas.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getHora_init() + " - " + cell.getValue().getHora_final()
        ));
        colEstadoReservas.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEstado().toString()));

        // Extraer y unir los IDs de los recursos asignados
        colRecursosReservas.setCellValueFactory(cell -> {
            List<RecursoDTO> recursos = cell.getValue().getRecursos();
            String idsUnidos = recursos.stream().map(RecursoDTO::getID).collect(Collectors.joining(", "));
            return new SimpleStringProperty(idsUnidos);
        });

        // 5. Asignar acciones a los botones
        btnGuardarReserva.setOnAction(e -> reservar());
        //btnLimpiarReserva.setOnAction(e -> limpiarFormulario());
        //btnCancelarReserva.setOnAction(e -> cancelarReserva());


    }

    private void cargarDatosIniciales() {
        if (categoriaLogic != null) {
            lvListaCategorias.getItems().setAll(categoriaLogic.listar());
        }
        actualizarTablaReservas();
    }

    private void actualizarTablaReservas() {
        if (reservaLogic != null) {
            List<ReservaDTO> misReservas = reservaLogic.listarPorFuncionario("FUNC-001");
            ObservableList<ReservaDTO> listaObservable = FXCollections.observableArrayList(misReservas);
            tabMisReservas.setItems(listaObservable);
        }
    }

    private void reservar(){
        String actividad = txtActividad.getText().trim();
        LocalDate fecha = dtReservarFecha.getValue();
        String horaInicioStr = cbHoraInicio.getValue();
        String horaFinalStr = cbHoraFinal.getValue();
        List<CategoriaRecursoDTO> categoriasSeleccionadas = lvListaCategorias.getSelectionModel().getSelectedItems();
        if (horaInicioStr == null || horaFinalStr == null) {
            lblAvisosReservas.setText("Debe seleccionar la hora de inicio y fin.");
            return;
        }
        // Convertir strings a LocalTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime horaInicio = LocalTime.parse(horaInicioStr, formatter);
        LocalTime horaFinal = LocalTime.parse(horaFinalStr, formatter);

        //reservaLogic.reservar(, actividad, fecha, horaInicio, horaFinal, categoriasSeleccionadas);


    }

}
