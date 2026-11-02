package una.sistemareservas.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import una.sistemareservas.dto.ReservaDTO;


public class AlertasModel {

    private static final AlertasModel instancia = new AlertasModel();

    private final ObservableList<ReservaDTO> alertas = FXCollections.observableArrayList();

    private AlertasModel() {
    }

    public static AlertasModel getInstancia() {
        return instancia;
    }

    public ObservableList<ReservaDTO> getAlertas() {
        return alertas;
    }

    public void agregarAlerta(ReservaDTO reserva) {
        if (reserva != null && !alertas.contains(reserva)) {
            alertas.add(reserva);
        }
    }

    public void descartarAlerta(ReservaDTO reserva) {
        alertas.remove(reserva);
    }

    public void limpiar() {
        alertas.clear();
    }
}
