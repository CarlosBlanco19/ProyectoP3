package una.sistemareservas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.chart.BarChart;

public class EstadisticasViewController {

    @FXML private DatePicker dtDesdeRecursosFecha;
    @FXML private DatePicker dtHastaRecursosFecha;
    @FXML private Button btnCargarRecursos;
    @FXML private TableView tabEstadisticasRecursos;
    @FXML private TableColumn colEstCategoriaRecursos;
    @FXML private TableColumn colEstCantidadRecursos;
    @FXML private BarChart bcRecursos;

    @FXML private DatePicker dtDesdeActividadesFecha;
    @FXML private DatePicker dtHastaActividadesFecha;
    @FXML private Button btnCargarActividades;
    @FXML private TableView tabEstadisticasActividades;
    @FXML private TableColumn colEstSemanaActividades;
    @FXML private TableColumn colEstCantidadActividades;
    @FXML private BarChart bcActividades;
}
