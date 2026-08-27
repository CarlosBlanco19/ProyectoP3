package una.sistemareservas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

public class CalendarizacionViewController {

    @FXML private Button btnBuscarCalendarizacion;
    @FXML private Button btnImprimirCalendarizacion;
    @FXML private DatePicker dtCalendarizacion;
    @FXML private ComboBox cbCategoriaCalendarizacion;
    @FXML private TableView tabCalendarizacion;
    @FXML private TableColumn colHoraCalendarizacion;
}
