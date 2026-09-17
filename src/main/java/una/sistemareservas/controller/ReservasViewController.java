package una.sistemareservas.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.exception.ReservaException;
import una.sistemareservas.service.*;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    //UsuarioDTO usuario = LogInViewController.usuarioLogueado;

    GeminiService geminiService;

    @FXML
    public void initialize(){
        this.categoriaLogic = new CategoriaService();
        RecursoService recursoLogic = new RecursoService(categoriaLogic);
        UsuarioService usuarioLogic = new UsuarioService();
        try {
            this.reservaLogic = new ReservaService(usuarioLogic, this.categoriaLogic, recursoLogic);
        } catch (Exception e) {
            // Si el archivo de reservas falla al cargar, mostramos el error sin que la app colapse
            mostrarAlerta("Error al cargar los datos de reservas: " + e.getMessage());
        }
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
        btnLimpiarReserva.setOnAction(e -> limpiarFormulario());
        btnCancelarReserva.setOnAction(e -> cancelarReserva());
        btnImprimirReservas.setOnAction(this::imprimirReservas);
        btnExtraerIA.setOnAction(e -> reservarConIA());

        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        if (categoriaLogic != null) {
            lvListaCategorias.getItems().setAll(categoriaLogic.listar());
        }
        actualizarTablaReservas();
    }

    private void actualizarTablaReservas() {
        if (reservaLogic != null) {
            String idFuncionario = una.sistemareservas.utilidades.SesionGlobal.getFuncionarioActual();
            List<ReservaDTO> misReservas = reservaLogic.listarPorFuncionario(idFuncionario);
            ObservableList<ReservaDTO> listaObservable = FXCollections.observableArrayList(misReservas);
            tabMisReservas.setItems(listaObservable);
        }
    }

    private void reservar(){
        try{
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

            String idFuncionario = una.sistemareservas.utilidades.SesionGlobal.getFuncionarioActual();
            reservaLogic.reservar(idFuncionario, actividad, fecha, horaInicio, horaFinal, categoriasSeleccionadas);
            mostrarMensaje("Reserva realizada", "Reserva realizada con éxito.");
            limpiarFormulario();
            actualizarTablaReservas();
        } catch (Exception ex) {
            mostrarAlerta(ex.getMessage());
        }
    }

    private void cancelarReserva() {
        ReservaDTO seleccionada = tabMisReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Debe seleccionar una reserva de la tabla para cancelar.");
            return;
        }

        try {
            reservaLogic.cancelar(seleccionada.getID());
            mostrarMensaje("Reserva cancelada", "La reserva ha sido cancelada.");
            actualizarTablaReservas();
        } catch (Exception ex) {
            mostrarAlerta(ex.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtActividad.clear();
        dtReservarFecha.setValue(null);
        cbHoraInicio.getSelectionModel().clearSelection();
        cbHoraFinal.getSelectionModel().clearSelection();
        lvListaCategorias.getSelectionModel().clearSelection();
        txtPromptFrase.clear();
    }

    @FXML
    private void imprimirReservas(ActionEvent event) {
        // 1. Abrir ventana para que el usuario elija dónde guardar
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Mis Reservas");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("Reporte_Mis_Reservas.pdf");

        File file = fileChooser.showSaveDialog(btnImprimirReservas.getScene().getWindow());

        if (file != null) {
            try {
                // 2. Crear el documento en formato horizontal (apaisado)
                Document documento = new Document(PageSize.A4.rotate());
                PdfWriter.getInstance(documento, new FileOutputStream(file));

                documento.open();
                String idFuncionario = una.sistemareservas.utilidades.SesionGlobal.getFuncionarioActual();

                // 3. Título y encabezado del PDF
                documento.add(new Paragraph("Reporte de Mis Reservas"));
                // Si implementaste la SesionGlobal, puedes poner el ID aquí:
                documento.add(new Paragraph("Generado para el Funcionario ID: " + idFuncionario));
                documento.add(new Paragraph(" ")); // Espacio en blanco

                // 4. Crear tabla de iText con 6 columnas (igual a tu interfaz)
                PdfPTable tablaPdf = new PdfPTable(6);
                tablaPdf.setWidthPercentage(100);

                // Configurar el color y texto de las cabeceras
                String[] encabezados = {"Id", "Actividad", "Fecha", "Horario", "Recursos", "Estado"};
                for (String encabezado : encabezados) {
                    PdfPCell celda = new PdfPCell(new Phrase(encabezado));
                    celda.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
                    tablaPdf.addCell(celda);
                }

                // 5. Extraer los datos de tabMisReservas fila por fila
                for (ReservaDTO reserva : tabMisReservas.getItems()) {
                    tablaPdf.addCell(reserva.getID());
                    tablaPdf.addCell(reserva.getActividad());
                    tablaPdf.addCell(reserva.getFecha().toString());
                    tablaPdf.addCell(reserva.getHora_init() + " - " + reserva.getHora_final());

                    // Extraemos los IDs de los recursos igual que en el TableColumn
                    String recursosIds = reserva.getRecursos().stream()
                            .map(RecursoDTO::getID)
                            .collect(Collectors.joining(", "));

                    tablaPdf.addCell(recursosIds.isEmpty() ? "Ninguno" : recursosIds);
                    tablaPdf.addCell(reserva.getEstado().toString());
                }

                // 6. Añadir tabla y cerrar documento
                documento.add(tablaPdf);
                documento.close();

                mostrarMensaje("Éxito", "¡El reporte PDF se ha guardado exitosamente!");

            } catch (Exception e) {
                mostrarAlerta("Ocurrió un error al generar el PDF: " + e.getMessage());
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

    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private String construirPromptIA(String promptUsuario) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un asistente de inteligencia artificial que extrae datos de una reserva de espacios y recursos\n");
        prompt.append("Para las categoriaRecurso, solo puedes usar los nombres exactos que aparezcan en lalista de categorias disponibles. Jamas inventes nuevas categorias:\n");
        prompt.append("Lista de categorias disponibles: \n");

        for (CategoriaRecursoDTO categoria : lvListaCategorias.getItems()) {
            prompt.append("- ").append(categoria.getID()).append(": ").append(categoria.getDescripcion()).append("\n");
        }

        prompt.append("Prompt usuario: \"").append(promptUsuario).append("\"\n");
        prompt.append("Responde con un JSON con esta forma exacta, sin texto adicional:\n");
        prompt.append("{\"actividad\": \"\", \"fecha\": \"YYYY-MM-DD\", \"hora_inicio\": \"HH:mm\", \"hora_final\": \"HH:mm\", \"categorias\": [\"ID1\", \"ID2\"]}\n");
        prompt.append("Si algun dato no esta en el prompt, dejalo como cadena vacia o lista vacia");

        return prompt.toString();
    }

    // Falta manejar las excepciones
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
                    String idCategoria = categoriasIA.getString(i);
                    for (CategoriaRecursoDTO categoria : lvListaCategorias.getItems()) {
                        if (categoria.getID().equals(idCategoria)) {
                            lvListaCategorias.getSelectionModel().select(categoria);
                        }
                    }
                }
            }
        } catch (Exception e) {
            mostrarAlerta("No se pudo hacer la reserva");
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

        Thread hiloIA = new Thread(() -> {
            try {
                JSONObject respuesta = consultarIA(prompt);
                Platform.runLater(() -> {
                    btnExtraerIA.setDisable(false);
                    llenarReservaIA(respuesta);
                });
            } catch (ReservaException e) {
                Platform.runLater(() -> {
                    mostrarAlerta(e.getMessage());
                    btnExtraerIA.setDisable(false);
                });
            }
        });
        hiloIA.start();
    }

    private JSONObject consultarIA(String frase) throws ReservaException {
        try {
            geminiService = new GeminiService();
        } catch (IllegalStateException e) {
            throw new ReservaException(e.getMessage(), e);
        }
        String prompt = construirPromptIA(frase);
        String respuestaJson;
        try {
            respuestaJson = geminiService.enviarMensaje(prompt);
        } catch (IOException | InterruptedException e) {
            throw new ReservaException("No se pudo comunicar con el servicio de Gemini", e);
        }
        try {
            return new JSONObject(respuestaJson);
        } catch (JSONException e) {
            throw new ReservaException("Gemini devolvio un formato incorrecto", e);
        }
    }
}
