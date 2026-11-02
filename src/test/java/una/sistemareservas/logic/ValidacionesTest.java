package una.sistemareservas.logic;

import org.junit.jupiter.api.Test;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.exception.CategoriaException;
import una.sistemareservas.exception.RecursoException;
import una.sistemareservas.exception.UsuarioException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Pruebas de UNIDAD de las validaciones de usuarios, funcionarios, categorías y recursos (sin MySQL)
class ValidacionesTest {

    // ---------- Usuarios ----------
    @Test
    void loginSinDatos() {
        UsuarioLogic logic = new UsuarioLogic();
        UsuarioException e = assertThrows(UsuarioException.class, () -> logic.login("", ""));
        assertEquals("Debe ingresar el usuario y la clave", e.getMessage());
    }

    @Test
    void cambiarClaveConEspacios() {
        UsuarioLogic logic = new UsuarioLogic();
        assertThrows(UsuarioException.class, () -> logic.cambiarClave("ISS", "ISS", "nueva clave"));
    }

    @Test
    void cambiarClaveCamposVacios() {
        UsuarioLogic logic = new UsuarioLogic();
        assertThrows(UsuarioException.class, () -> logic.cambiarClave("ISS", "", "nueva"));
    }

    // ---------- Funcionarios ----------
    @Test
    void funcionarioSinNombre() {
        FuncionarioLogic logic = new FuncionarioLogic();
        UsuarioException e = assertThrows(UsuarioException.class,
                () -> logic.agregar(new FuncionarioDTO("NUEVO", " ", "88887777")));
        assertEquals("Debe ingresar el nombre", e.getMessage());
    }

    @Test
    void funcionarioTelefonoInvalido() {
        FuncionarioLogic logic = new FuncionarioLogic();
        assertThrows(UsuarioException.class, () -> logic.agregar(new FuncionarioDTO("NUEVO", "Ana", "abc")));
    }

    @Test
    void funcionarioIdConEspacios() {
        FuncionarioLogic logic = new FuncionarioLogic();
        assertThrows(UsuarioException.class, () -> logic.agregar(new FuncionarioDTO("ID MALO", "Ana", "88887777")));
    }

    // ---------- Categorías ----------
    @Test
    void categoriaSinDescripcion() {
        CategoriaLogic logic = new CategoriaLogic();
        CategoriaException e = assertThrows(CategoriaException.class, () -> logic.agregar("   "));
        assertEquals("La descripción es obligatoria", e.getMessage());
    }

    @Test
    void categoriaDescripcionMuyLarga() {
        CategoriaLogic logic = new CategoriaLogic();
        assertThrows(CategoriaException.class, () -> logic.agregar("a".repeat(151)));
    }

    // ---------- Recursos ----------
    @Test
    void recursoSinCategoria() {
        RecursoLogic logic = new RecursoLogic();
        RecursoException e = assertThrows(RecursoException.class,
                () -> logic.agregar(new RecursoDTO("HP-9", "Laptop HP", null)));
        assertEquals("Debe seleccionar una categoría", e.getMessage());
    }

    @Test
    void recursoSinId() {
        RecursoLogic logic = new RecursoLogic();
        assertThrows(RecursoException.class,
                () -> logic.agregar(new RecursoDTO("", "Laptop HP", new CategoriaRecursoDTO(1, "Laptop"))));
    }
}
