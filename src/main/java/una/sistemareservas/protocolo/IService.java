package una.sistemareservas.protocolo;

import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.dto.UsuarioDTO;

import java.util.List;


public interface IService {

    UsuarioDTO login(String id, String clave) throws Exception;

    void cambiarClave(String id, String claveActual, String claveNueva) throws Exception;

    List<FuncionarioDTO> listarFuncionarios() throws Exception;

    FuncionarioDTO buscarFuncionario(String id) throws Exception;

    List<FuncionarioDTO> buscarFuncionariosPorNombre(String nombre) throws Exception;

    void agregarFuncionario(FuncionarioDTO funcionario) throws Exception;

    void actualizarFuncionario(FuncionarioDTO funcionario) throws Exception;

    void eliminarFuncionario(String id) throws Exception;

    List<CategoriaRecursoDTO> listarCategorias() throws Exception;

    List<CategoriaRecursoDTO> buscarCategorias(String descripcion) throws Exception;

    CategoriaRecursoDTO agregarCategoria(String descripcion) throws Exception;

    void actualizarCategoria(int id, String descripcion) throws Exception;

    void eliminarCategoria(int id) throws Exception;

    List<RecursoDTO> listarRecursos() throws Exception;

    List<RecursoDTO> listarRecursosPorCategoria(int idCategoria) throws Exception;

    RecursoDTO buscarRecurso(String id) throws Exception;

    void agregarRecurso(RecursoDTO recurso) throws Exception;

    void actualizarRecurso(RecursoDTO recurso) throws Exception;

    void eliminarRecurso(String id) throws Exception;

    ReservaDTO reservar(ReservaDTO solicitud) throws Exception;

    void cancelarReserva(String idReserva, String idFuncionario) throws Exception;

    List<ReservaDTO> listarReservasPorFuncionario(String idFuncionario) throws Exception;

    List<ReservaDTO> listarReservas() throws Exception;
}
