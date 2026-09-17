import org.junit.jupiter.api.Test;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.dto.EstadoReserva;
import una.sistemareservas.logic.CategoriaLogic;
import una.sistemareservas.logic.RecursoLogic;
import una.sistemareservas.logic.ReservaLogic;
import una.sistemareservas.logic.UsuarioLogic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReservaIntegracion {

    @Test
    void flujoCompleto_categoriaRecursoReservaYCancelacion()
            throws Exception {

        CategoriaLogic categoriaLogic = new CategoriaLogic();
        RecursoLogic recursoLogic =
                new RecursoLogic(categoriaLogic);
        UsuarioLogic usuarioLogic = new UsuarioLogic();

        String sufijo = String.valueOf(System.nanoTime());

        String categoriaId = "IT-CAT-" + sufijo;
        String recursoId = "IT-REC-" + sufijo;

        CategoriaRecursoDTO categoria =
                new CategoriaRecursoDTO(
                        categoriaId,
                        "Categoria de integracion"
                );

        assertTrue(categoriaLogic.agregar(categoria));

        RecursoDTO recurso =
                new RecursoDTO(
                        recursoId,
                        "Recurso de integracion",
                        categoria
                );

        assertTrue(recursoLogic.agregar(recurso));

        ReservaLogic reservaLogic =
                new ReservaLogic(
                        categoriaLogic,
                        recursoLogic,
                        usuarioLogic
                );

        ReservaDTO reserva = reservaLogic.reservar(
                "ISS",
                "Actividad de integracion",
                LocalDate.of(2037, 4, 15),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                List.of(categoria)
        );

        assertNotNull(reserva);

        assertTrue(
                reservaLogic.listarPorFuncionario("ISS")
                        .stream()
                        .anyMatch(r ->
                                r.getID().equals(reserva.getID())
                        )
        );

        reservaLogic.cancelar(reserva.getID());

        assertEquals(
                EstadoReserva.CANCELADA,
                reservaLogic.buscarPorID(reserva.getID())
                        .getEstado()
        );

        assertTrue(recursoLogic.eliminar(recursoId));

        assertTrue(categoriaLogic.eliminar(categoriaId));
    }
}
