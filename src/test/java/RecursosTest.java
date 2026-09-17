import org.junit.jupiter.api.Test;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.logic.CategoriaLogic;
import una.sistemareservas.logic.RecursoLogic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecursosTest {

    @Test
    void listarPorCategoria_conCategoriaExistente_debeDevolverSoloEsaCategoria() {
        CategoriaLogic categoriaLogic = new CategoriaLogic();
        RecursoLogic recursoLogic = new RecursoLogic(categoriaLogic);

        // "LPT" ya existe en categorias.json, con al menos un recurso en recursos.json
        List<RecursoDTO> resultado = recursoLogic.listarCategoria("LPT");

        assertFalse(resultado.isEmpty());
        for (RecursoDTO recurso : resultado) {
            assertEquals("LPT", recurso.getCategoria().getID());
        }
    }


    @Test
    void buscarID_conIdInexistente_debeDevolverNull() {
        CategoriaLogic categoriaLogic = new CategoriaLogic();
        RecursoLogic recursoLogic = new RecursoLogic(categoriaLogic);

        assertNull(recursoLogic.buscarID("ID-QUE-NO-EXISTE"));
    }

    @Test
    void agregarYEliminar_debeMantenerElArchivoIgual() {
        CategoriaLogic categoriaLogic = new CategoriaLogic();
        RecursoLogic recursoLogic = new RecursoLogic(categoriaLogic);

        CategoriaRecursoDTO categoriaExistente = categoriaLogic.buscarID("LPT");
        RecursoDTO temporal = new RecursoDTO("TEST-TEMP", "Recurso de prueba", categoriaExistente);

        assertTrue(recursoLogic.agregar(temporal));
        assertNotNull(recursoLogic.buscarID("TEST-TEMP"));

        assertTrue(recursoLogic.eliminar("TEST-TEMP"));
        assertNull(recursoLogic.buscarID("TEST-TEMP"));
    }


}



