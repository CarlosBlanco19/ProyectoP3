package una.sistemareservas.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.EstadoReserva;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.protocolo.IService;
import una.sistemareservas.proxy.ServiceProxy;
import una.sistemareservas.utilidades.Dialogos;
import una.sistemareservas.utilidades.ReportePDF;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarizacionViewController implements Refrescable {

    private static final int PRIMERA_HORA = 6;
    private static final int ULTIMA_HORA = 21;

    @FXML private Button btnBuscarCalendarizacion;
    @FXML private Button btnImprimirCalendarizacion;
    @FXML private DatePicker dtCalendarizacion;
    @FXML private ComboBox<CategoriaRecursoDTO> cbCategoriaCalendarizacion;
    // Cada fila es un Map: "hora" -> "08:00" y "<id del recurso>" -> texto de la celda
    @FXML private TableView<Map<String, String>> tabCalendarizacion;
    @FXML private TableColumn<Map<String, String>, String> colHoraCalendarizacion;
    @FXML private Label lblAvisos;

    private final IService service = ServiceProxy.instancia();

    @FXML
    private void initialize() {
        colHoraCalendarizacion.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().get("hora")));
        btnBuscarCalendarizacion.setOnAction(this::buscarCalendarizacion);
        btnImprimirCalendarizacion.setOnAction(this::imprimirCalendarizacion);
    }

    @Override
    public void refrescar() {
        try {
            CategoriaRecursoDTO seleccionada = cbCategoriaCalendarizacion.getValue();
            cbCategoriaCalendarizacion.setItems(FXCollections.observableArrayList(service.listarCategorias()));
            cbCategoriaCalendarizacion.setValue(seleccionada);
            if (seleccionada != null && dtCalendarizacion.getValue() != null) {
                buscarCalendarizacion(null); // Para ver las reservas nuevas de los demás
            }
        } catch (Exception e) {
            lblAvisos.setText(e.getMessage());
        }
    }

    private void buscarCalendarizacion(ActionEvent event) {
        LocalDate fecha = dtCalendarizacion.getValue();
        CategoriaRecursoDTO categoria = cbCategoriaCalendarizacion.getValue();
        if (categoria == null || fecha == null) {
            lblAvisos.setText("Debe seleccionar una fecha y categoria");
            return;
        }
        try {
            List<RecursoDTO> recursos = service.listarRecursosPorCategoria(categoria.getID());
            List<ReservaDTO> reservas = service.listarReservas();
            definirColumnas(recursos);
            definirFilas(fecha, recursos, reservas);
            lblAvisos.setText(recursos.isEmpty() ? "Esa categoría no tiene recursos" : "");
        } catch (Exception e) {
            lblAvisos.setText(e.getMessage());
        }
    }

    // Una columna por recurso de la categoría
    private void definirColumnas(List<RecursoDTO> recursos) {
        tabCalendarizacion.getColumns().setAll(colHoraCalendarizacion);
        for (RecursoDTO recurso : recursos) {
            TableColumn<Map<String, String>, String> columna = new TableColumn<>(recurso.getID());
            columna.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().get(recurso.getID())));
            columna.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.isBlank()) {
                        setText("");
                        setStyle("");
                    } else {
                        setText(item);
                        setStyle("-fx-background-color: #fff2cc; -fx-border-color: lightgray; -fx-border-width: 0.5px; -fx-alignment: center-left;");
                    }
                }
            });
            tabCalendarizacion.getColumns().add(columna);
        }
    }

    // Una fila por hora
    private void definirFilas(LocalDate fecha, List<RecursoDTO> recursos, List<ReservaDTO> reservas) {
        ObservableList<Map<String, String>> filas = FXCollections.observableArrayList();
        for (int hora = PRIMERA_HORA; hora <= ULTIMA_HORA; hora++) {
            LocalTime horaActual = LocalTime.of(hora, 0);
            Map<String, String> fila = new HashMap<>();
            fila.put("hora", String.format("%02d:00", hora));
            for (RecursoDTO recurso : recursos) {
                fila.put(recurso.getID(), textoCelda(reservas, recurso, fecha, horaActual));
            }
            filas.add(fila);
        }
        tabCalendarizacion.setItems(filas);
    }

    // "Actividad - Funcionario" si el recurso está reservado a esa hora; vacío si está libre
    private String textoCelda(List<ReservaDTO> reservas, RecursoDTO recurso, LocalDate fecha, LocalTime hora) {
        for (ReservaDTO reserva : reservas) {
            if (reserva.getEstado() != EstadoReserva.ACTIVA
                    || !reserva.getFecha().equals(fecha)
                    || !reserva.getRecursos().contains(recurso)) { // contains usa equals por ID
                continue;
            }
            if (!hora.isBefore(reserva.getHora_init()) && hora.isBefore(reserva.getHora_final())) {
                return reserva.getActividad() + " - " + reserva.getFuncionario().getNombre();
            }
        }
        return "";
    }

    @FXML
    private void imprimirCalendarizacion(ActionEvent event) {
        if (dtCalendarizacion.getValue() == null || cbCategoriaCalendarizacion.getValue() == null
                || tabCalendarizacion.getItems().isEmpty()) {
            Dialogos.advertencia("Debe buscar una calendarización antes de imprimir.");
            return;
        }
        List<TableColumn<Map<String, String>, ?>> columnas = tabCalendarizacion.getColumns();
        String[] encabezados = new String[columnas.size()];
        for (int i = 0; i < columnas.size(); i++) {
            encabezados[i] = columnas.get(i).getText();
        }
        List<String[]> filas = new ArrayList<>();
        for (Map<String, String> fila : tabCalendarizacion.getItems()) {
            String[] valores = new String[columnas.size()];
            valores[0] = fila.get("hora");
            for (int i = 1; i < columnas.size(); i++) {
                valores[i] = fila.get(columnas.get(i).getText());
            }
            filas.add(valores);
        }
        ReportePDF.exportarTabla(btnImprimirCalendarizacion.getScene().getWindow(),
                "Calendarizacion_" + dtCalendarizacion.getValue() + ".pdf",
                "Reporte de Calendarización de Recursos",
                "Fecha: " + dtCalendarizacion.getValue() + " | Categoría: " + cbCategoriaCalendarizacion.getValue().getDescripcion(),
                encabezados, filas);
    }
}
