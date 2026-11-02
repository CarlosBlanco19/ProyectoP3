package una.sistemareservas.service;

import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.dto.UsuarioDTO;
import una.sistemareservas.logic.CategoriaLogic;
import una.sistemareservas.logic.FuncionarioLogic;
import una.sistemareservas.logic.RecursoLogic;
import una.sistemareservas.logic.ReservaLogic;
import una.sistemareservas.logic.UsuarioLogic;
import una.sistemareservas.protocolo.IService;

import java.util.List;

/**
 * El Service "real" (del lado del Backend). Implementa IService delegando en las clases de lógica.
 * Hay UNA sola instancia para todo el servidor; los Worker (uno por Frontend) la comparten.
 */
public class Service implements IService {

    private final UsuarioLogic usuarioLogic = new UsuarioLogic();
    private final FuncionarioLogic funcionarioLogic = new FuncionarioLogic();
    private final CategoriaLogic categoriaLogic = new CategoriaLogic();
    private final RecursoLogic recursoLogic = new RecursoLogic();
    private final ReservaLogic reservaLogic = new ReservaLogic();

    // ---------------- Usuarios ----------------
    @Override
    public UsuarioDTO login(String id, String clave) throws Exception {
        return usuarioLogic.login(id, clave);
    }

    @Override
    public void cambiarClave(String id, String claveActual, String claveNueva) throws Exception {
        usuarioLogic.cambiarClave(id, claveActual, claveNueva);
    }

    // ---------------- Funcionarios ----------------
    @Override
    public List<FuncionarioDTO> listarFuncionarios() throws Exception {
        return funcionarioLogic.listar();
    }

    @Override
    public FuncionarioDTO buscarFuncionario(String id) throws Exception {
        return funcionarioLogic.buscarPorId(id);
    }

    @Override
    public List<FuncionarioDTO> buscarFuncionariosPorNombre(String nombre) throws Exception {
        return funcionarioLogic.buscarPorNombre(nombre);
    }

    @Override
    public void agregarFuncionario(FuncionarioDTO funcionario) throws Exception {
        funcionarioLogic.agregar(funcionario);
    }

    @Override
    public void actualizarFuncionario(FuncionarioDTO funcionario) throws Exception {
        funcionarioLogic.actualizar(funcionario);
    }

    @Override
    public void eliminarFuncionario(String id) throws Exception {
        funcionarioLogic.eliminar(id);
    }

    // ---------------- Categorías ----------------
    @Override
    public List<CategoriaRecursoDTO> listarCategorias() throws Exception {
        return categoriaLogic.listar();
    }

    @Override
    public List<CategoriaRecursoDTO> buscarCategorias(String descripcion) throws Exception {
        return categoriaLogic.buscarPorDescripcion(descripcion);
    }

    @Override
    public CategoriaRecursoDTO agregarCategoria(String descripcion) throws Exception {
        return categoriaLogic.agregar(descripcion);
    }

    @Override
    public void actualizarCategoria(int id, String descripcion) throws Exception {
        categoriaLogic.actualizar(id, descripcion);
    }

    @Override
    public void eliminarCategoria(int id) throws Exception {
        categoriaLogic.eliminar(id);
    }

    // ---------------- Recursos ----------------
    @Override
    public List<RecursoDTO> listarRecursos() throws Exception {
        return recursoLogic.listar();
    }

    @Override
    public List<RecursoDTO> listarRecursosPorCategoria(int idCategoria) throws Exception {
        return recursoLogic.listarPorCategoria(idCategoria);
    }

    @Override
    public RecursoDTO buscarRecurso(String id) throws Exception {
        return recursoLogic.buscarPorId(id);
    }

    @Override
    public void agregarRecurso(RecursoDTO recurso) throws Exception {
        recursoLogic.agregar(recurso);
    }

    @Override
    public void actualizarRecurso(RecursoDTO recurso) throws Exception {
        recursoLogic.actualizar(recurso);
    }

    @Override
    public void eliminarRecurso(String id) throws Exception {
        recursoLogic.eliminar(id);
    }

    // ---------------- Reservas ----------------
    @Override
    public ReservaDTO reservar(ReservaDTO solicitud) throws Exception {
        return reservaLogic.reservar(solicitud);
    }

    @Override
    public void cancelarReserva(String idReserva, String idFuncionario) throws Exception {
        reservaLogic.cancelar(idReserva, idFuncionario);
    }

    @Override
    public List<ReservaDTO> listarReservasPorFuncionario(String idFuncionario) throws Exception {
        return reservaLogic.listarPorFuncionario(idFuncionario);
    }

    @Override
    public List<ReservaDTO> listarReservas() throws Exception {
        return reservaLogic.listar();
    }
}
