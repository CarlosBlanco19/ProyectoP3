package una.sistemareservas.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.ReservaDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Pruebas de UNIDAD del modelo del área de alertas
class AlertasModelTest {

    private final AlertasModel modelo = AlertasModel.getInstancia();

    private ReservaDTO reserva(String id, String actividad) {
        return new ReservaDTO(id, new FuncionarioDTO("ISS", "Carlos", "88887777"), actividad,
                LocalDate.of(2026, 11, 20), LocalTime.of(8, 0), LocalTime.of(9, 0), List.of());
    }

    @BeforeEach
    void limpiar() {
        modelo.limpiar();
    }

    @Test
    void agregarAlertas() {
        modelo.agregarAlerta(reserva("RES-000010", "Reunión clientes"));
        modelo.agregarAlerta(reserva("RES-000011", "Capacitación"));
        assertEquals(2, modelo.getAlertas().size());
        assertEquals("Reunión clientes", modelo.getAlertas().get(0).getActividad());
    }

    @Test
    void noSeRepiteLaMismaReserva() {
        modelo.agregarAlerta(reserva("RES-000010", "Reunión clientes"));
        modelo.agregarAlerta(reserva("RES-000010", "Reunión clientes"));
        assertEquals(1, modelo.getAlertas().size());
    }

    @Test
    void nullSeIgnora() {
        modelo.agregarAlerta(null);
        assertTrue(modelo.getAlertas().isEmpty());
    }

    @Test
    void descartarSoloQuitaEsaAlerta() {
        ReservaDTO primera = reserva("RES-000010", "Reunión clientes");
        modelo.agregarAlerta(primera);
        modelo.agregarAlerta(reserva("RES-000011", "Capacitación"));

        modelo.descartarAlerta(primera);

        assertEquals(1, modelo.getAlertas().size());
        assertEquals("RES-000011", modelo.getAlertas().get(0).getID());
    }
}
