package una.sistemareservas.logic;

import una.sistemareservas.datos.CategoriaRecursoDatos;
import una.sistemareservas.datos.RecursoDatos;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.exception.RecursoException;

import java.sql.SQLException;
import java.util.List;

// Mantenimiento de recursos (solo el administrador)
public class RecursoLogic {

    private final RecursoDatos recursosDatos = new RecursoDatos();
    private final CategoriaRecursoDatos categoriasDatos = new CategoriaRecursoDatos();

    public List<RecursoDTO> listar() throws RecursoException {
        try {
            return recursosDatos.listar();
        } catch (SQLException e) {
            throw new RecursoException(ErroresBD.mensaje("cargar los recursos", e), e);
        }
    }

    public List<RecursoDTO> listarPorCategoria(int idCategoria) throws RecursoException {
        try {
            return recursosDatos.listarPorCategoria(idCategoria);
        } catch (SQLException e) {
            throw new RecursoException(ErroresBD.mensaje("cargar los recursos", e), e);
        }
    }

    public RecursoDTO buscarPorId(String id) throws RecursoException {
        if (id == null || id.isBlank()) {
            return null;
        }
        try {
            return recursosDatos.buscarPorId(id.trim());
        } catch (SQLException e) {
            throw new RecursoException(ErroresBD.mensaje("buscar el recurso", e), e);
        }
    }

    public void agregar(RecursoDTO recurso) throws RecursoException {
        RecursoDTO limpio = validar(recurso);
        try {
            validarCategoriaExiste(limpio.getCategoria());
            if (recursosDatos.buscarPorId(limpio.getID()) != null) {
                throw new RecursoException("Ya existe un recurso con el ID " + limpio.getID());
            }
            recursosDatos.insertar(limpio);
        } catch (SQLException e) {
            if (ErroresBD.esDuplicado(e)) {
                throw new RecursoException("Ya existe un recurso con el ID " + limpio.getID());
            }
            throw new RecursoException(ErroresBD.mensaje("guardar el recurso", e), e);
        }
    }

    public void actualizar(RecursoDTO recurso) throws RecursoException {
        RecursoDTO limpio = validar(recurso);
        try {
            validarCategoriaExiste(limpio.getCategoria());
            if (recursosDatos.buscarPorId(limpio.getID()) == null) {
                throw new RecursoException("No existe un recurso con el ID " + limpio.getID());
            }
            recursosDatos.actualizar(limpio);
        } catch (SQLException e) {
            throw new RecursoException(ErroresBD.mensaje("actualizar el recurso", e), e);
        }
    }

    public void eliminar(String id) throws RecursoException {
        if (id == null || id.isBlank()) {
            throw new RecursoException("Debe ingresar el ID del recurso");
        }
        try {
            if (recursosDatos.buscarPorId(id.trim()) == null) {
                throw new RecursoException("No existe un recurso con el ID " + id.trim());
            }
            recursosDatos.eliminar(id.trim());
        } catch (SQLException e) {
            if (ErroresBD.esLlaveForanea(e)) {
                throw new RecursoException("No se puede eliminar el recurso porque está asignado a reservas");
            }
            throw new RecursoException(ErroresBD.mensaje("eliminar el recurso", e), e);
        }
    }

    // Validaciones que no necesitan la base de datos. Devuelve una copia con los textos sin espacios sobrantes.
    RecursoDTO validar(RecursoDTO recurso) throws RecursoException {
        if (recurso == null) {
            throw new RecursoException("No se recibió el recurso");
        }
        if (recurso.getID() == null || recurso.getID().isBlank()) {
            throw new RecursoException("Debe ingresar el ID del recurso");
        }
        if (recurso.getID().trim().length() > 30) {
            throw new RecursoException("El ID no puede tener más de 30 caracteres");
        }
        if (recurso.getDescripcion() == null || recurso.getDescripcion().isBlank()) {
            throw new RecursoException("Debe ingresar la descripción del recurso");
        }
        if (recurso.getDescripcion().trim().length() > 150) {
            throw new RecursoException("La descripción no puede tener más de 150 caracteres");
        }
        if (recurso.getCategoria() == null) {
            throw new RecursoException("Debe seleccionar una categoría");
        }
        return new RecursoDTO(recurso.getID().trim(), recurso.getDescripcion().trim(), recurso.getCategoria());
    }

    private void validarCategoriaExiste(CategoriaRecursoDTO categoria) throws SQLException, RecursoException {
        if (categoriasDatos.buscarPorId(categoria.getID()) == null) {
            throw new RecursoException("La categoría seleccionada ya no existe");
        }
    }
}
