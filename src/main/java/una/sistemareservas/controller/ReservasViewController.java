package una.sistemareservas.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.ia.GeminiService;
import una.sistemareservas.protocolo.IService;
import una.sistemareservas.proxy.ServiceProxy;
import una.sistemareservas.utilidades.Dialogos;
import una.sistemareservas.utilidades.ReportePDF;
import una.sistemareservas.utilidades.SesionGlobal;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservasViewController implements Refrescable {

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

    private final IService service = ServiceProxy.instancia();

    @FXML
    public void initialize() {
        for (int h = 6; h < 22; h++) {
            String horaFormateada = String.format("%02d:00", h);
            cbHoraFinal.getItems().add(horaFormateada);
            cbHoraInicio.getItems().add(horaFormateada);
        }
        // Para que se puedan seleccionar varias categorías (Ctrl + clic)
        lvListaCategorias.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvListaCategorias.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(CategoriaRecursoDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDescripcion());
            }
        });

        // No se pueden escoger fechas que ya pasaron
        dtReservarFecha.setDayCellFactory(selector -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate fecha, boolean vacia) {
                super.updateItem(fecha, vacia);
                setDisable(vacia || fecha.isBefore(LocalDate.now()));
            }
        });

        // Columnas de la tabla "Mis reservas"
        colIdReservas.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getID()));
        colActividadReservas.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getActividad()));
        colFechaReservas.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFecha().toString()));
        colHorarioReservas.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getHora_init() + " - " + cell.getValue().getHora_final()));
        colEstadoReservas.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEstado().toString()));
        colRecursosReservas.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getRecursos().stream().map(RecursoDTO::getID).collect(Collectors.joining(", "))));

        btnGuardarReserva.setOnAction(e -> reservar());
        btnLimpiarReserva.setOnAction(e -> limpiarFormulario());
        btnCancelarReserva.setOnAction(e -> cancelarReserva());
        btnImprimirReservas.setOnAction(this::imprimirReservas);
        btnExtraerIA.setOnAction(e -> reservarConIA());
    }

    // Se llama cada vez que el usuario entra a la pestaña
    @Override
    public void refrescar() {
        try {
            List<CategoriaRecursoDTO> seleccionadas = new ArrayList<>(lvListaCategorias.getSelectionModel().getSelectedItems());
            lvListaCategorias.getItems().setAll(service.listarCategorias());
            for (CategoriaRecursoDTO categoria : seleccionadas) {
                lvListaCategorias.getSelectionModel().select(categoria); // Se conserva lo que había escogido
            }
            actualizarTablaReservas();
        } catch (Exception e) {
            lblAvisosReservas.setText(e.getMessage());
        }
    }

    private void actualizarTablaReservas() throws Exception {
        List<ReservaDTO> misReservas = service.listarReservasPorFuncionario(SesionGlobal.getFuncionarioActual());
        tabMisReservas.setItems(FXCollections.observableArrayList(misReservas));
    }

    private void reservar() {
        String horaInicioStr = cbHoraInicio.getValue();
        String horaFinalStr = cbHoraFinal.getValue();
        if (horaInicioStr == null || horaFinalStr == null) {
            lblAvisosReservas.setText("Debe seleccionar la hora de inicio y fin.");
            return;
        }
        try {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");
            ReservaDTO solicitud = new ReservaDTO(null,
                    (FuncionarioDTO) SesionGlobal.getUsuario(),
                    txtActividad.getText().trim(),
                    dtReservarFecha.getValue(),
                    LocalTime.parse(horaInicioStr, formato),
                    LocalTime.parse(horaFinalStr, formato),
                    new ArrayList<>(lvListaCategorias.getSelectionModel().getSelectedItems()));

            ReservaDTO creada = service.reservar(solicitud);

            String recursos = creada.getRecursos().stream().map(RecursoDTO::getDescripcion).collect(Collectors.joining(", "));
            Dialogos.informacion("Reserva realizada", "Reserva " + creada.getID() + " realizada con éxito.\nRecursos asignados: " + recursos);
            limpiarFormulario();
            actualizarTablaReservas();
        } catch (Exception ex) {
            Dialogos.advertencia(ex.getMessage());
        }
    }

    private void cancelarReserva() {
        ReservaDTO seleccionada = tabMisReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            Dialogos.advertencia("Debe seleccionar una reserva de la tabla para cancelar.");
            return;
        }
        try {
            service.cancelarReserva(seleccionada.getID(), SesionGlobal.getFuncionarioActual());
            Dialogos.informacion("Reserva cancelada", "La reserva ha sido cancelada.");
            actualizarTablaReservas();
        } catch (Exception ex) {
            Dialogos.advertencia(ex.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtActividad.clear();
        dtReservarFecha.setValue(null);
        cbHoraInicio.getSelectionModel().clearSelection();
        cbHoraFinal.getSelectionModel().clearSelection();
        lvListaCategorias.getSelectionModel().clearSelection();
        txtPromptFrase.clear();
        lblAvisosReservas.setText("");
    }

    @FXML
    private void imprimirReservas(ActionEvent event) {
        List<String[]> filas = new ArrayList<>();
        for (ReservaDTO reserva : tabMisReservas.getItems()) {
            String recursos = reserva.getRecursos().stream().map(RecursoDTO::getID).collect(Collectors.joining(", "));
            filas.add(new String[]{reserva.getID(), reserva.getActividad(), reserva.getFecha().toString(),
                    reserva.getHora_init() + " - " + reserva.getHora_final(),
                    recursos.isEmpty() ? "Ninguno" : recursos, reserva.getEstado().toString()});
        }
        ReportePDF.exportarTabla(btnImprimirReservas.getScene().getWindow(), "Reporte_Mis_Reservas.pdf",
                "Reporte de Mis Reservas",
                "Generado para: " + SesionGlobal.getNombre() + " (" + SesionGlobal.getFuncionarioActual() + ")",
                new String[]{"Id", "Actividad", "Fecha", "Horario", "Recursos", "Estado"}, filas);
    }

    // =====================================================================
    //                    Llenar la reserva con IA (Gemini)
    // =====================================================================

    private String construirPromptIA(String promptUsuario) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un asistente de inteligencia artificial que extrae datos de una reserva de espacios y recursos\n");
        prompt.append("Hoy es ").append(LocalDate.now()).append(".\n");
        prompt.append("Para las categorias, solo puedes usar los ID numericos que aparecen en la lista de categorias disponibles. Jamas inventes nuevas categorias:\n");
        prompt.append("Lista de categorias disponibles (ID: descripcion): \n");
        for (CategoriaRecursoDTO categoria : lvListaCategorias.getItems()) {
            prompt.append("- ").append(categoria.getID()).append(": ").append(categoria.getDescripcion()).append("\n");
        }
        prompt.append("Prompt usuario: \"").append(promptUsuario).append("\"\n");
        prompt.append("Responde con un JSON con esta forma exacta, sin texto adicional:\n");
        prompt.append("{\"actividad\": \"\", \"fecha\": \"YYYY-MM-DD\", \"hora_inicio\": \"HH:00\", \"hora_final\": \"HH:00\", \"categorias\": [1, 2]}\n");
        prompt.append("Si algun dato no esta en el prompt, dejalo como cadena vacia o lista vacia");
        return prompt.toString();
    }

    private void llenarReservaIA(JSONObject datos) {
        try {
            if (datos.has("actividad") && !datos.getString("actividad").isBlank()) {
                txtActividad.setText(datos.getString("actividad"));
            }
            if (datos.has("fecha") && !datos.getString("fecha").isBlank()) {
                dtReservarFecha.setValue(LocalDate.parse(datos.getString("fecha")));
            }
            if (datos.has("hora_inicio") && !datos.getString("hora_inicio").isBlank()) {
                cbHoraInicio.setValue(datos.getString("hora_inicio"));
            }
            if (datos.has("hora_final") && !datos.getString("hora_final").isBlank()) {
                cbHoraFinal.setValue(datos.getString("hora_final"));
            }
            lvListaCategorias.getSelectionModel().clearSelection();
            if (datos.has("categorias")) {
                JSONArray categoriasIA = datos.getJSONArray("categorias");
                for (int i = 0; i < categoriasIA.length(); i++) {
                    int idCategoria = categoriasIA.getInt(i);
                    for (CategoriaRecursoDTO categoria : lvListaCategorias.getItems()) {
                        if (categoria.getID() == idCategoria) {
                            lvListaCategorias.getSelectionModel().select(categoria);
                        }
                    }
                }
            }
            lblAvisosReservas.setText("Revise los datos y presione Guardar para reservar.");
        } catch (Exception e) {
            Dialogos.advertencia("La IA devolvió datos que no se pudieron usar. Llene la reserva manualmente.");
        }
    }

    @FXML
    private void reservarConIA() {
        String prompt = txtPromptFrase.getText();
        if (prompt == null || prompt.isBlank()) {
            lblAvisosReservas.setText("Primero tiene que escribir un prompt");
            return;
        }
        btnExtraerIA.setDisable(true);
        String instrucciones = construirPromptIA(prompt);

        // La consulta a Gemini tarda: se hace en otro hilo para no congelar la ventana
        Thread hiloIA = new Thread(() -> {
            try {
                JSONObject respuesta = consultarIA(instrucciones);
                Platform.runLater(() -> {
                    btnExtraerIA.setDisable(false);
                    llenarReservaIA(respuesta);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    btnExtraerIA.setDisable(false);
                    Dialogos.advertencia(e.getMessage());
                });
            }
        });
        hiloIA.setDaemon(true);
        hiloIA.start();
    }

    private JSONObject consultarIA(String prompt) throws Exception {
        GeminiService geminiService;
        try {
            geminiService = new GeminiService();
        } catch (IllegalStateException e) {
            throw new Exception(e.getMessage(), e);
        }
        String respuestaJson;
        try {
            respuestaJson = geminiService.enviarMensaje(prompt);
        } catch (IOException | InterruptedException e) {
            throw new Exception("No se pudo comunicar con el servicio de Gemini: " + e.getMessage(), e);
        }
        try {
            return new JSONObject(respuestaJson);
        } catch (JSONException e) {
            throw new Exception("Gemini devolvió un formato incorrecto", e);
        }
    }
}
