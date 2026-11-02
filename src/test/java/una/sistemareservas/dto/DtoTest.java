package una.sistemareservas.dto;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Pruebas de UNIDAD de los DTO: que se comparen por ID y que se puedan enviar por un socket (serializar)
class DtoTest {

    @Test
    void recursosSeComparanPorId() {
        CategoriaRecursoDTO laptop = new CategoriaRecursoDTO(1, "Laptop");
        assertEquals(new RecursoDTO("HP-1", "HP básica", laptop), new RecursoDTO("HP-1", "Otro texto", null));
        assertNotEquals(new RecursoDTO("HP-1", "HP", laptop), new RecursoDTO("HP-2", "HP", laptop));
    }

    @Test
    void categoriasSeComparanPorId() {
        assertEquals(new CategoriaRecursoDTO(3, "Sala"), new CategoriaRecursoDTO(3, "Sala de reuniones"));
    }

    @Test
    void reservaViajaCompletaPorElSocket() throws Exception {
        CategoriaRecursoDTO laptop = new CategoriaRecursoDTO(1, "Laptop");
        ReservaDTO original = new ReservaDTO("RES-000001", new FuncionarioDTO("ISS", "Carlos", "88887777"),
                "Reunión", LocalDate.of(2026, 11, 20), LocalTime.of(8, 0), LocalTime.of(10, 0), List.of(laptop));
        original.asignarRecurso(new RecursoDTO("HP-1", "HP básica", laptop));

        // Se escribe como lo hace el servidor y se lee como lo hace el cliente
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(original);
        }
        ReservaDTO copia;
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            copia = (ReservaDTO) in.readObject();
        }

        assertEquals(original, copia);
        assertEquals("Carlos", copia.getFuncionario().getNombre());
        assertEquals(LocalTime.of(10, 0), copia.getHora_final());
        assertEquals(EstadoReserva.ACTIVA, copia.getEstado());
        assertTrue(copia.getRecursos().contains(new RecursoDTO("HP-1", "", null)));
    }
}
