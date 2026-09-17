import org.junit.jupiter.api.Test;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.exception.ReservaException;
import una.sistemareservas.logic.CategoriaLogic;
import una.sistemareservas.logic.RecursoLogic;
import una.sistemareservas.logic.ReservaLogic;
import una.sistemareservas.logic.UsuarioLogic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReservasTest {
    private ReservaLogic crearReservaLogic() throws ReservaException {
        UsuarioLogic usuarioLogic = new UsuarioLogic();
        CategoriaLogic categoriaLogic = new CategoriaLogic();
        RecursoLogic recursoLogic = new RecursoLogic(categoriaLogic);
        return new ReservaLogic(categoriaLogic, recursoLogic, usuarioLogic);
    }

    @Test
    void reservar_conRecursoDisponible_debeCrearLaReserva() throws Exception {
        ReservaLogic logic = crearReservaLogic();
        CategoriaLogic categoriaLogic = new CategoriaLogic();
        CategoriaRecursoDTO categoriaLPT = categoriaLogic.buscarID("LPT");

        LocalDate fecha = LocalDate.of(2030, 1, 15);

        ReservaDTO reserva = logic.reservar("ISS", "Reserva de prueba", fecha,
                LocalTime.of(10, 0), LocalTime.of(11, 0), List.of(categoriaLPT));

        assertNotNull(reserva);
        assertEquals("Reserva de prueba", reserva.getActividad());

        logic.cancelar(reserva.getID()); // no existe "eliminar" para reservas, solo "cancelar"
    }

    @Test
    void reservar_sinRecursoDisponible_debeLanzarReservaException() throws Exception {
        ReservaLogic logic = crearReservaLogic();
        CategoriaLogic categoriaLogic = new CategoriaLogic();
        CategoriaRecursoDTO categoriaATV = categoriaLogic.buscarID("ATV");

        LocalDate fecha = LocalDate.of(2030, 2, 20);
        LocalTime inicio = LocalTime.of(9, 0);
        LocalTime fin = LocalTime.of(10, 0);

        // Ocupa el único recurso de la categoria ATV en ese horario
        ReservaDTO primera = logic.reservar("ISS", "Primera reserva", fecha, inicio, fin, List.of(categoriaATV));

        // Misma categoria y horario, ya no hay recurso libre
        assertThrows(ReservaException.class, () ->
                logic.reservar("ISS-1", "Segunda reserva", fecha, inicio, fin, List.of(categoriaATV))
        );

        logic.cancelar(primera.getID());
    }

    @Test
    void cancelar_conFechaPasada_debeLanzarReservaException() throws Exception {
        ReservaLogic logic = crearReservaLogic();
        // RES-000002 ya tiene fecha pasada y estado ACTIVA en reservas.json
        ReservaDTO reservaPasada = logic.buscarPorID("RES-000002");

        assertNotNull(reservaPasada, "Se espera que RES-000002 exista en reservas.json");
        assertThrows(ReservaException.class, () -> logic.cancelar(reservaPasada.getID()));
    }
}
