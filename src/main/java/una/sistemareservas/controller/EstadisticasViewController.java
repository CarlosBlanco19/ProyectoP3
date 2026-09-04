package una.sistemareservas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.chart.BarChart;
import javafx.scene.control.cell.PropertyValueFactory;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.service.CategoriaService;
import una.sistemareservas.dto.ReservaDTO;
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

    private final CategoriaService categoriaService = new CategoriaService();
    private final ObservableList<CategoriaRecursoDTO> datosTablacategorias = FXCollections.observableArrayList();
    //Aqui van el logic de reservas (service) despues
    private final ObservableList<ReservaDTO> datosTablaReservas = FXCollections.observableArrayList();

    @FXML
    private void initialize (){




    }



}
