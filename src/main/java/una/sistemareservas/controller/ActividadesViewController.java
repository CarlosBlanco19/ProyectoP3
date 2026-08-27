package una.sistemareservas.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class ActividadesViewController {


    @FXML private Button btnBuscarActividades;
    @FXML private Button btnImprimirActividades;
    @FXML private DatePicker dtActividadesFecha;
    @FXML private TableView tabActividades;
    @FXML private TableColumn colHoraActividades;
}
