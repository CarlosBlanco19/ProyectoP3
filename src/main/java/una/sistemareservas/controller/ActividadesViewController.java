package una.sistemareservas.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import una.sistemareservas.dto.EstadoReserva;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.protocolo.IService;
import una.sistemareservas.proxy.ServiceProxy;
import una.sistemareservas.utilidades.Dialogos;
import una.sistemareservas.utilidades.ReportePDF;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ActividadesViewController implements Refrescable {

    private static final int PRIMERA_HORA = 6;
    private static final int ULTIMA_HORA = 21;

    @FXML private Button btnBuscarActividades;
    @FXML private Button btnImprimirActividades;
    @FXML private DatePicker dtActividadesFecha;
    @FXML private TableView<FilaHorario> tabActividades;
    @FXML private TableColumn<FilaHorario, String> colHoraActividades;
    @FXML private TableColumn<FilaHorario, String> colLunes;
    @FXML private TableColumn<FilaHorario, String> colMartes;
    @FXML private TableColumn<FilaHorario, String> colMiercoles;
    @FXML private TableColumn<FilaHorario, String> colJueves;
    @FXML private TableColumn<FilaHorario, String> colViernes;
    @FXML private TableColumn<FilaHorario, String> colSabado;
    @FXML private TableColumn<FilaHorario, String> colDomingo;

    private final IService service = ServiceProxy.instancia();
    private List<TableColumn<FilaHorario, String>> columnasDias;

    @FXML
    public void initialize() {
        colHoraActividades.setCellValueFactory(celda -> celda.getValue().horaProperty());
        columnasDias = List.of(colLunes, colMartes, colMiercoles, colJueves, colViernes, colSabado, colDomingo);

        for (int i = 0; i < 7; i++) {
            final int dia = i;
            TableColumn<FilaHorario, String> columna = columnasDias.get(i);
            columna.setCellValueFactory(celda -> celda.getValue().diaProperty(dia));
            columna.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.isBlank()) {
                        setText("");
                        setStyle("");
                    } else {
                        setText(item);
                        setStyle("-fx-background-color: #fff2cc; -fx-border-color: lightgray; -fx-border-width: 0.5px; -fx-alignment: center;");
                    }
                }
            });
        }
        btnBuscarActividades.setOnAction(this::cargarCalendario);
        btnImprimirActividades.setOnAction(this::imprimirActividades);
    }

    @Override
    public void refrescar() {
        if (dtActividadesFecha.getValue() != null) {
            cargarCalendario(null); // Para ver las reservas nuevas de los demás
        }
    }

    @FXML
    private void cargarCalendario(ActionEvent event) {
        LocalDate fechaSeleccionada = dtActividadesFecha.getValue();
        if (fechaSeleccionada == null) {
            Dialogos.advertencia("¡Debe seleccionar una fecha!");
            return;
        }
        LocalDate lunes = fechaSeleccionada.with(DayOfWeek.MONDAY);
        actualizarCabeceras(lunes);

        ObservableList<FilaHorario> filas = FXCollections.observableArrayList();
        for (int hora = PRIMERA_HORA; hora <= ULTIMA_HORA; hora++) {
            filas.add(new FilaHorario(String.format("%02d:00", hora)));
        }

        try {
            for (ReservaDTO reserva : service.listarReservas()) {
                LocalDate fecha = reserva.getFecha();
                boolean enLaSemana = !fecha.isBefore(lunes) && fecha.isBefore(lunes.plusDays(7));
                if (reserva.getEstado() != EstadoReserva.ACTIVA || !enLaSemana) {
                    continue;
                }
                int dia = fecha.getDayOfWeek().getValue() - 1; // Lunes = 0
                String nombre = reserva.getFuncionario() != null ? reserva.getFuncionario().getNombre() : "";
                String texto = reserva.getActividad() + "\n(" + nombre + ")";
                for (FilaHorario fila : filas) {
                    int horaFila = Integer.parseInt(fila.getHora().substring(0, 2));
                    if (horaFila >= reserva.getHora_init().getHour() && horaFila < reserva.getHora_final().getHour()) {
                        // Si a esa hora ya había otra actividad, se muestran las dos
                        String actual = fila.getDia(dia);
                        fila.setDia(dia, actual.isEmpty() ? texto : actual + "\n" + texto);
                    }
                }
            }
        } catch (Exception e) {
            Dialogos.advertencia(e.getMessage());
        }
        tabActividades.setItems(filas);
    }

    private void actualizarCabeceras(LocalDate lunes) {
        String[] prefijos = {"lun", "mar", "mié", "jue", "vie", "sáb", "dom"};
        for (int i = 0; i < 7; i++) {
            columnasDias.get(i).setText(prefijos[i] + " " + lunes.plusDays(i));
        }
    }

    @FXML
    private void imprimirActividades(ActionEvent event) {
        if (tabActividades.getItems().isEmpty()) {
            Dialogos.advertencia("Debe buscar una semana antes de imprimir.");
            return;
        }
        String[] encabezados = new String[8];
        encabezados[0] = "Hora";
        for (int i = 0; i < 7; i++) {
            encabezados[i + 1] = columnasDias.get(i).getText();
        }
        List<String[]> filas = new ArrayList<>();
        for (FilaHorario fila : tabActividades.getItems()) {
            String[] valores = new String[8];
            valores[0] = fila.getHora();
            for (int i = 0; i < 7; i++) {
                valores[i + 1] = fila.getDia(i);
            }
            filas.add(valores);
        }
        ReportePDF.exportarTabla(btnImprimirActividades.getScene().getWindow(),
                "Reporte_Actividades_" + dtActividadesFecha.getValue() + ".pdf",
                "Reporte de Calendario de Actividades", "Fecha de referencia: " + dtActividadesFecha.getValue(),
                encabezados, filas);
    }

    // Una fila de la tabla: la hora y lo que hay ese día a esa hora (lunes a domingo)
    public static class FilaHorario {
        private final SimpleStringProperty hora;
        private final SimpleStringProperty[] dias = new SimpleStringProperty[7];

        public FilaHorario(String hora) {
            this.hora = new SimpleStringProperty(hora);
            for (int i = 0; i < 7; i++) {
                dias[i] = new SimpleStringProperty("");
            }
        }

        public String getHora() { return hora.get(); }
        public SimpleStringProperty horaProperty() { return hora; }
        public String getDia(int indice) { return dias[indice].get(); }
        public void setDia(int indice, String valor) { dias[indice].set(valor); }
        public SimpleStringProperty diaProperty(int indice) { return dias[indice]; }
    }
}
