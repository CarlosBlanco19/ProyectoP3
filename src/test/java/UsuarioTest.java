import org.junit.jupiter.api.Test;
import una.sistemareservas.dto.UsuarioDTO;
import una.sistemareservas.logic.UsuarioLogic;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    @Test
    void autenticar_conClaveIncorrecta_debeDevolverNull() {
        UsuarioLogic logic = new UsuarioLogic();
        UsuarioDTO resultado = logic.autenticar("admin", "claveQueNoExiste");
        assertNull(resultado);
    }

    @Test
    void buscarID_conIdInexistente_debeDevolverNull() {
        UsuarioLogic logic = new UsuarioLogic();
        assertNull(logic.buscarID("ID-QUE-NO-EXISTE"));
    }

    @Test
    void agregarYEliminar_debeMantenerElArchivoIgual() {
        UsuarioLogic logic = new UsuarioLogic();
        una.sistemareservas.dto.FuncionarioDTO temporal =
                new una.sistemareservas.dto.FuncionarioDTO("TEST-TEMP", "Temporal", "0000-0000");

        assertTrue(logic.agregar(temporal));
        assertNotNull(logic.buscarID("TEST-TEMP"));

        assertTrue(logic.eliminar("TEST-TEMP"));
        assertNull(logic.buscarID("TEST-TEMP"));
    }
}



