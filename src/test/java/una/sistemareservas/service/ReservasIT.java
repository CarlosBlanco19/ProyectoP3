package una.sistemareservas.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import una.sistemareservas.DatosPrueba;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.EstadoReserva;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.exception.ReservaException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de INTEGRACIÓN de reservas contra MySQL.
 * Se crea una categoría con UN solo recurso para poder probar choques de horario.
 */
class ReservasIT {

    private static final Service service = new Service();
    private static final String SUFIJO = DatosPrueba.sufijo();
    private static final FuncionarioDTO CARLOS = new FuncionarioDTO("ISS", "Carlos", "88887777");
    private static final FuncionarioDTO ANTHONY = new FuncionarioDTO("ISS-1", "Anthony", "88889999");
    private static final LocalDate FECHA = LocalDate.now().plusYears(3);

    private static CategoriaRecursoDTO categoria;
    private static RecursoDTO recurso;

    @BeforeAll
    static void crearDatos() throws Exception {
        categoria = service.agregarCategoria("IT reservas " + SUFIJO);
        recurso = new RecursoDTO("IT-RR-" + SUFIJO, "Único recurso", categoria);
        service.agregarRecurso(recurso);
    }

    @AfterAll
    static void limpiar() throws Exception {
        DatosPrueba.borrarCategoria(categoria.getID());
    }

    private ReservaDTO solicitud(FuncionarioDTO funcionario, int horaInicio, int horaFinal) {
        return new ReservaDTO(null, funcionario, "Prueba " + horaInicio, FECHA,
                LocalTime.of(horaInicio, 0), LocalTime.of(horaFinal, 0), List.of(categoria));
    }

    @Test
    void reservarAsignaRecursoYNoPermiteChoques() throws Exception {
        ReservaDTO primera = service.reservar(solicitud(CARLOS, 8, 10));
        assertNotNull(primera.getID());
        assertTrue(primera.getID().startsWith("RES-"));
        assertEquals(List.of(recurso), primera.getRecursos());
        assertEquals("Carlos", primera.getFuncionario().getNombre());

        // Mismo recurso, horario que se traslapa (9 a 11): no hay recursos libres
        ReservaException choque = assertThrows(ReservaException.class, () -> service.reservar(solicitud(ANTHONY, 9, 11)));
        assertTrue(choque.getMessage().startsWith("No hay recursos disponibles"));
        assertEquals(List.of(categoria), choque.getCategoriasNoDisponibles());

        // Justo cuando termina la otra (10 a 11): sí se puede
        ReservaDTO seguida = service.reservar(solicitud(ANTHONY, 10, 11));
        assertEquals(List.of(recurso), seguida.getRecursos());

        // Aparece en "mis reservas" de cada uno
        assertTrue(service.listarReservasPorFuncionario("ISS").contains(primera));
        assertTrue(service.listarReservasPorFuncionario("ISS-1").contains(seguida));
    }

    @Test
    void cancelarLiberaElRecurso() throws Exception {
        ReservaDTO reserva = service.reservar(solicitud(CARLOS, 14, 15));

        // Solo el dueño la puede cancelar
        Exception ajeno = assertThrows(Exception.class, () -> service.cancelarReserva(reserva.getID(), "ISS-1"));
        assertEquals("Solo puede cancelar sus propias reservas", ajeno.getMessage());

        service.cancelarReserva(reserva.getID(), "ISS");
        ReservaDTO cancelada = service.listarReservasPorFuncionario("ISS").stream()
                .filter(r -> r.equals(reserva)).findFirst().orElseThrow();
        assertEquals(EstadoReserva.CANCELADA, cancelada.getEstado());

        // No se puede cancelar dos veces
        assertThrows(Exception.class, () -> service.cancelarReserva(reserva.getID(), "ISS"));

        // El recurso quedó libre en ese horario
        ReservaDTO otra = service.reservar(solicitud(ANTHONY, 14, 15));
        assertEquals(List.of(recurso), otra.getRecursos());
    }

    @Test
    void noSePuedeCancelarUnaReservaPasada() {
        // RES-000001 es de Carlos y fue en octubre de 2026 (datos de prueba)
        Exception e = assertThrows(Exception.class, () -> service.cancelarReserva("RES-000001", "ISS"));
        assertEquals("Solo se pueden cancelar reservas que todavía no han comenzado", e.getMessage());
    }
}
