package una.sistemareservas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;

public class ReservasViewController {
    @FXML private TextArea txtPromptFrase;
    @FXML private TextField txtActividad;
    @FXML private Button btnExtraerIA;
    @FXML private DatePicker dtReservarFecha;
    @FXML private ComboBox cbHoraInicio;
    @FXML private ComboBox cbHoraFinal;
    @FXML private ListView lvListaCategorias;
    @FXML private Button btnGuardarReserva;
    @FXML private Button btnCancelarReserva;
    @FXML private Button btnLimpiarReserva;
    @FXML private TableView tabMisReservas;
    @FXML private TableColumn colIdReservas;
    @FXML private TableColumn colActividadReservas;
    @FXML private TableColumn colFechaReservas;
    @FXML private TableColumn colHorarioReservas;
    @FXML private TableColumn colRecursosReservas;
    @FXML private TableColumn colEstadoReservas;
    @FXML private Button btnImprimirReservas;
}
