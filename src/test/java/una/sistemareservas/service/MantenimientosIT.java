package una.sistemareservas.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import una.sistemareservas.DatosPrueba;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.UsuarioDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de INTEGRACIÓN (Failsafe): Service + lógica + MySQL de verdad.
 * Requieren MySQL encendido con los scripts de la carpeta database ya corridos.
 */
class MantenimientosIT {

    private static final Service service = new Service();
    private static final String SUFIJO = DatosPrueba.sufijo();
    private static CategoriaRecursoDTO categoria;
    private static final String ID_FUNCIONARIO = "IT-F-" + SUFIJO;

    @BeforeAll
    static void crearCategoria() throws Exception {
        categoria = service.agregarCategoria("IT categoria " + SUFIJO);
    }

    @AfterAll
    static void limpiar() throws Exception {
        DatosPrueba.borrarCategoria(categoria.getID());
        DatosPrueba.borrarFuncionario(ID_FUNCIONARIO);
    }

    @Test
    void loginCorrectoNoDevuelveLaClave() throws Exception {
        UsuarioDTO admin = service.login("admin", "1234");
        assertEquals("admin", admin.getID());
        assertNull(admin.getClave());
    }

    @Test
    void loginIncorrecto() {
        Exception e = assertThrows(Exception.class, () -> service.login("admin", "clave-mala"));
        assertEquals("Usuario o clave incorrectos", e.getMessage());
    }

    @Test
    void categoriaSeGuardaYNoSeRepite() throws Exception {
        assertTrue(categoria.getID() > 0, "La base de datos debe generar el ID");
        assertTrue(service.listarCategorias().contains(categoria));
        assertThrows(Exception.class, () -> service.agregarCategoria("IT categoria " + SUFIJO));
    }

    @Test
    void categoriaSeActualiza() throws Exception {
        service.actualizarCategoria(categoria.getID(), "IT categoria " + SUFIJO + " editada");
        assertEquals(1, service.buscarCategorias(SUFIJO + " editada").size());
        service.actualizarCategoria(categoria.getID(), "IT categoria " + SUFIJO);
    }

    @Test
    void recursoCompleto() throws Exception {
        RecursoDTO recurso = new RecursoDTO("IT-R-" + SUFIJO, "Recurso de prueba", categoria);
        service.agregarRecurso(recurso);

        RecursoDTO guardado = service.buscarRecurso(recurso.getID());
        assertNotNull(guardado);
        assertEquals(categoria.getDescripcion(), guardado.getCategoria().getDescripcion());
        assertEquals(1, service.listarRecursosPorCategoria(categoria.getID()).size());

        // No se puede repetir el ID
        Exception repetido = assertThrows(Exception.class, () -> service.agregarRecurso(recurso));
        assertEquals("Ya existe un recurso con el ID " + recurso.getID(), repetido.getMessage());

        // No se puede borrar una categoría que tiene recursos
        Exception conRecursos = assertThrows(Exception.class, () -> service.eliminarCategoria(categoria.getID()));
        assertEquals("No se puede eliminar la categoría porque tiene recursos o reservas asociados", conRecursos.getMessage());

        service.eliminarRecurso(recurso.getID());
        assertNull(service.buscarRecurso(recurso.getID()));
    }

    @Test
    void funcionarioCompleto() throws Exception {
        service.agregarFuncionario(new FuncionarioDTO(ID_FUNCIONARIO, "Prueba Integración", "8888-1234"));

        // La clave inicial es el mismo ID
        UsuarioDTO usuario = service.login(ID_FUNCIONARIO, ID_FUNCIONARIO);
        assertEquals("Prueba Integración", ((FuncionarioDTO) usuario).getNombre());

        service.actualizarFuncionario(new FuncionarioDTO(ID_FUNCIONARIO, "Prueba Editada", "8888-4321"));
        assertEquals("Prueba Editada", service.buscarFuncionario(ID_FUNCIONARIO).getNombre());

        service.cambiarClave(ID_FUNCIONARIO, ID_FUNCIONARIO, "nueva123");
        assertNotNull(service.login(ID_FUNCIONARIO, "nueva123"));

        service.eliminarFuncionario(ID_FUNCIONARIO);
        assertNull(service.buscarFuncionario(ID_FUNCIONARIO));
    }

    @Test
    void noSeBorraFuncionarioConReservas() {
        // Carlos (ISS) tiene reservas en los datos de prueba
        Exception e = assertThrows(Exception.class, () -> service.eliminarFuncionario("ISS"));
        assertEquals("No se puede eliminar el funcionario porque tiene reservas registradas", e.getMessage());
    }
}
