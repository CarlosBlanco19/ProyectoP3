import org.junit.jupiter.api.Test;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.logic.FuncionarioLogic;
import una.sistemareservas.logic.UsuarioLogic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FuncionarioTest {

    @Test
    void agregarIdDuplicado() {
        UsuarioLogic usuarioLogic = new UsuarioLogic();
        FuncionarioLogic logic = new FuncionarioLogic(usuarioLogic);

        // "ISS" ya existe en usuarios.json
        boolean resultado = logic.agregar("ISS", "Otro nombre", "00000000");
        assertFalse(resultado);
    }

    @Test
    void buscarNombreTextoInexistente() {
        UsuarioLogic usuarioLogic = new UsuarioLogic();
        FuncionarioLogic logic = new FuncionarioLogic(usuarioLogic);

        List<FuncionarioDTO> resultado = logic.buscarNombre("NOMBRE-QUE-NO-EXISTE-EN-NINGUN-FUNCIONARIO");
        assertTrue(resultado.isEmpty());
    }

    @Test
    void agregarEliminar() {
        UsuarioLogic usuarioLogic = new UsuarioLogic();
        FuncionarioLogic logic = new FuncionarioLogic(usuarioLogic);

        assertTrue(logic.agregar("TEST-TEMP", "Funcionario de prueba", "11112222"));
        assertNotNull(logic.buscarID("TEST-TEMP"));

        assertTrue(logic.eliminar("TEST-TEMP"));
        assertNull(logic.buscarID("TEST-TEMP"));
    }

}
