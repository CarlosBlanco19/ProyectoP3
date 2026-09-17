import org.junit.jupiter.api.Test;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.logic.CategoriaLogic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoriaTest {

    @Test
    void agregar_conIdDuplicado_debeDevolverFalse() {
        CategoriaLogic logic = new CategoriaLogic();
        // "LPT" ya existe en categorias.json
        boolean resultado = logic.agregar(new CategoriaRecursoDTO("LPT", "Otra descripcion"));
        assertFalse(resultado);
    }

    @Test
    void buscarDescripcion_conTextoInexistente_debeDevolverListaVacia() {
        CategoriaLogic logic = new CategoriaLogic();
        List<CategoriaRecursoDTO> resultado = logic.buscarDescripcion("TEXTO-QUE-NO-EXISTE-EN-NINGUNA-CATEGORIA");
        assertTrue(resultado.isEmpty());
    }

    @Test
    void agregarActualizarYEliminar_debeMantenerElArchivoIgual() {
        CategoriaLogic logic = new CategoriaLogic();
        CategoriaRecursoDTO temporal = new CategoriaRecursoDTO("TEST-TEMP", "Descripcion inicial");

        assertTrue(logic.agregar(temporal));
        assertNotNull(logic.buscarID("TEST-TEMP"));

        assertTrue(logic.actualizar("TEST-TEMP", "Descripcion actualizada"));
        assertEquals("Descripcion actualizada", logic.buscarID("TEST-TEMP").getDescripcion());

        assertTrue(logic.eliminar("TEST-TEMP"));
        assertNull(logic.buscarID("TEST-TEMP"));
    }


}
