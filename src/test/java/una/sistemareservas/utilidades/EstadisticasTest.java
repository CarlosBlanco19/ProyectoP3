package una.sistemareservas.utilidades;

import org.junit.jupiter.api.Test;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.EstadoReserva;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Pruebas de UNIDAD de los cálculos de la pestaña Estadísticas
class EstadisticasTest {

    private final CategoriaRecursoDTO laptop = new CategoriaRecursoDTO(1, "Laptop");
    private final CategoriaRecursoDTO sala = new CategoriaRecursoDTO(3, "Sala de reuniones");

    private ReservaDTO reserva(LocalDate fecha, RecursoDTO... recursos) {
        ReservaDTO reserva = new ReservaDTO("RES-" + fecha + recursos.length, new FuncionarioDTO("ISS", "Carlos", "1"),
                "Actividad", fecha, LocalTime.of(8, 0), LocalTime.of(9, 0), List.of());
        for (RecursoDTO recurso : recursos) {
            reserva.asignarRecurso(recurso);
        }
        return reserva;
    }

    @Test
    void cuentaRecursosPorCategoria() {
        RecursoDTO hp = new RecursoDTO("HP-1", "HP", laptop);
        RecursoDTO dell = new RecursoDTO("DELL-1", "Dell", laptop);
        RecursoDTO sala1 = new RecursoDTO("SALA-1", "Sala 1", sala);

        List<ReservaDTO> reservas = List.of(
                reserva(LocalDate.of(2026, 11, 2), hp, sala1),
                reserva(LocalDate.of(2026, 11, 3), dell),
                reserva(LocalDate.of(2026, 12, 1), hp)); // Fuera del rango

        Map<String, Integer> conteo = Estadisticas.recursosPorCategoria(reservas,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30));

        assertEquals(2, (int) conteo.get("Laptop"));
        assertEquals(1, (int) conteo.get("Sala de reuniones"));
    }

    @Test
    void lasCanceladasNoCuentan() {
        ReservaDTO cancelada = reserva(LocalDate.of(2026, 11, 2), new RecursoDTO("HP-1", "HP", laptop));
        cancelada.setEstado(EstadoReserva.CANCELADA);

        assertTrue(Estadisticas.recursosPorCategoria(List.of(cancelada),
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30)).isEmpty());
    }

    @Test
    void semanasEnOrdenEIncluyendoLasQueTienenCero() {
        List<ReservaDTO> reservas = List.of(
                reserva(LocalDate.of(2026, 11, 4)),   // semana del lunes 2026-11-02
                reserva(LocalDate.of(2026, 11, 6)),   // misma semana
                reserva(LocalDate.of(2026, 11, 18))); // semana del lunes 2026-11-16

        Map<String, Integer> conteo = Estadisticas.actividadesPorSemana(reservas,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 20));

        assertEquals(List.of("2026-10-26", "2026-11-02", "2026-11-09", "2026-11-16"), List.copyOf(conteo.keySet()));
        assertEquals(0, (int) conteo.get("2026-10-26"));
        assertEquals(2, (int) conteo.get("2026-11-02"));
        assertEquals(0, (int) conteo.get("2026-11-09"));
        assertEquals(1, (int) conteo.get("2026-11-16"));
    }
}
