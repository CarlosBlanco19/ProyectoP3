package una.sistemareservas.logic;

import org.junit.jupiter.api.Test;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.exception.ReservaException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Pruebas de UNIDAD: las validaciones fallan antes de tocar la base de datos, así que no necesitan MySQL
class ReservaLogicTest {

    private final ReservaLogic logic = new ReservaLogic();
    private final FuncionarioDTO funcionario = new FuncionarioDTO("ISS", "Carlos", "88887777");
    private final List<CategoriaRecursoDTO> categorias = List.of(new CategoriaRecursoDTO(1, "Laptop"));
    private final LocalDate manana = LocalDate.now().plusDays(1);

    @Test
    void horaFinalAntesDeInicio() {
        ReservaDTO solicitud = new ReservaDTO(null, funcionario, "Reunión", manana,
                LocalTime.of(10, 0), LocalTime.of(9, 0), categorias);
        ReservaException e = assertThrows(ReservaException.class, () -> logic.reservar(solicitud));
        assertEquals("La hora final debe ser posterior a la hora de inicio", e.getMessage());
    }

    @Test
    void horaFinalIgualAInicio() {
        ReservaDTO solicitud = new ReservaDTO(null, funcionario, "Reunión", manana,
                LocalTime.of(10, 0), LocalTime.of(10, 0), categorias);
        assertThrows(ReservaException.class, () -> logic.reservar(solicitud));
    }

    @Test
    void fechaPasada() {
        ReservaDTO solicitud = new ReservaDTO(null, funcionario, "Reunión", LocalDate.now().minusDays(1),
                LocalTime.of(9, 0), LocalTime.of(10, 0), categorias);
        ReservaException e = assertThrows(ReservaException.class, () -> logic.reservar(solicitud));
        assertEquals("No se pueden hacer reservas en fechas u horas que ya pasaron", e.getMessage());
    }

    @Test
    void sinCategorias() {
        ReservaDTO solicitud = new ReservaDTO(null, funcionario, "Reunión", manana,
                LocalTime.of(9, 0), LocalTime.of(10, 0), List.of());
        ReservaException e = assertThrows(ReservaException.class, () -> logic.reservar(solicitud));
        assertEquals("Debe seleccionar al menos una categoría de recurso", e.getMessage());
    }

    @Test
    void actividadVacia() {
        ReservaDTO solicitud = new ReservaDTO(null, funcionario, "   ", manana,
                LocalTime.of(9, 0), LocalTime.of(10, 0), categorias);
        assertThrows(ReservaException.class, () -> logic.reservar(solicitud));
    }

    @Test
    void actividadMuyLarga() {
        ReservaDTO solicitud = new ReservaDTO(null, funcionario, "x".repeat(101), manana,
                LocalTime.of(9, 0), LocalTime.of(10, 0), categorias);
        assertThrows(ReservaException.class, () -> logic.reservar(solicitud));
    }

    @Test
    void sinFecha() {
        ReservaDTO solicitud = new ReservaDTO(null, funcionario, "Reunión", null,
                LocalTime.of(9, 0), LocalTime.of(10, 0), categorias);
        assertThrows(ReservaException.class, () -> logic.reservar(solicitud));
    }

    @Test
    void sinFuncionario() {
        ReservaDTO solicitud = new ReservaDTO(null, null, "Reunión", manana,
                LocalTime.of(9, 0), LocalTime.of(10, 0), categorias);
        assertThrows(ReservaException.class, () -> logic.reservar(solicitud));
    }

    @Test
    void cancelarSinId() {
        assertThrows(ReservaException.class, () -> logic.cancelar("  ", "ISS"));
    }
}
