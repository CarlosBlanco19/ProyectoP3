package una.sistemareservas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;

import java.time.format.DateTimeFormatter;

/** Controlador de la ventana emergente con el detalle de una alerta. */
public class AlertaDetalleViewController {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    @FXML private Label lblEncargado;
    @FXML private Label lblFecha;
    @FXML private Label lblDesde;
    @FXML private Label lblHasta;
    @FXML private ListView<String> lvRecursos;
    @FXML private Button btnOk;

    @FXML
    private void initialize() {
        btnOk.setOnAction(evento -> ((Stage) btnOk.getScene().getWindow()).close());
    }

    /** El menú principal llama esto justo después de cargar el FXML. */
    public void mostrarReserva(ReservaDTO reserva) {
        lblEncargado.setText(reserva.getFuncionario() != null ? reserva.getFuncionario().getNombre() : "-");
        lblFecha.setText(reserva.getFecha() != null ? reserva.getFecha().toString() : "-");
        lblDesde.setText(reserva.getHora_init() != null ? reserva.getHora_init().format(FORMATO_HORA) : "-");
        lblHasta.setText(reserva.getHora_final() != null ? reserva.getHora_final().format(FORMATO_HORA) : "-");

        lvRecursos.getItems().clear();
        if (!reserva.getRecursos().isEmpty()) {
            for (RecursoDTO recurso : reserva.getRecursos()) {
                lvRecursos.getItems().add(recurso.getDescripcion());
            }
        } else {
            // Si no trae recursos asignados, se muestran las categorías pedidas
            for (CategoriaRecursoDTO categoria : reserva.getCategorias()) {
                lvRecursos.getItems().add(categoria.getDescripcion());
            }
        }
    }
}
