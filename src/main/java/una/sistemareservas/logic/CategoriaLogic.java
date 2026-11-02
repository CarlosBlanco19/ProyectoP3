package una.sistemareservas.logic;

import una.sistemareservas.datos.CategoriaRecursoDatos;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.exception.CategoriaException;

import java.sql.SQLException;
import java.util.List;

// Mantenimiento de categorías de recursos (solo el administrador)
public class CategoriaLogic {

    private final CategoriaRecursoDatos categoriasDatos = new CategoriaRecursoDatos();

    public List<CategoriaRecursoDTO> listar() throws CategoriaException {
        try {
            return categoriasDatos.listar();
        } catch (SQLException e) {
            throw new CategoriaException(ErroresBD.mensaje("cargar las categorías", e), e);
        }
    }

    public CategoriaRecursoDTO buscarPorId(int id) throws CategoriaException {
        try {
            return categoriasDatos.buscarPorId(id);
        } catch (SQLException e) {
            throw new CategoriaException(ErroresBD.mensaje("buscar la categoría", e), e);
        }
    }

    public List<CategoriaRecursoDTO> buscarPorDescripcion(String texto) throws CategoriaException {
        if (texto == null || texto.isBlank()) {
            return listar();
        }
        try {
            return categoriasDatos.buscarPorDescripcion(texto.trim());
        } catch (SQLException e) {
            throw new CategoriaException(ErroresBD.mensaje("buscar categorías", e), e);
        }
    }

    // El ID lo genera la base de datos; se devuelve la categoría ya con su ID
    public CategoriaRecursoDTO agregar(String descripcion) throws CategoriaException {
        String limpia = validarDescripcion(descripcion);
        try {
            if (categoriasDatos.existeDescripcion(limpia, 0)) {
                throw new CategoriaException("Ya existe una categoría llamada \"" + limpia + "\"");
            }
            int id = categoriasDatos.insertar(limpia);
            return new CategoriaRecursoDTO(id, limpia);
        } catch (SQLException e) {
            throw new CategoriaException(ErroresBD.mensaje("guardar la categoría", e), e);
        }
    }

    public void actualizar(int id, String descripcion) throws CategoriaException {
        String limpia = validarDescripcion(descripcion);
        try {
            if (categoriasDatos.buscarPorId(id) == null) {
                throw new CategoriaException("La categoría no existe");
            }
            if (categoriasDatos.existeDescripcion(limpia, id)) {
                throw new CategoriaException("Ya existe otra categoría llamada \"" + limpia + "\"");
            }
            categoriasDatos.actualizar(id, limpia);
        } catch (SQLException e) {
            throw new CategoriaException(ErroresBD.mensaje("actualizar la categoría", e), e);
        }
    }

    public void eliminar(int id) throws CategoriaException {
        try {
            if (categoriasDatos.buscarPorId(id) == null) {
                throw new CategoriaException("La categoría no existe");
            }
            categoriasDatos.eliminar(id);
        } catch (SQLException e) {
            if (ErroresBD.esLlaveForanea(e)) {
                throw new CategoriaException("No se puede eliminar la categoría porque tiene recursos o reservas asociados");
            }
            throw new CategoriaException(ErroresBD.mensaje("eliminar la categoría", e), e);
        }
    }

    // Validación que no necesita la base de datos
    String validarDescripcion(String descripcion) throws CategoriaException {
        if (descripcion == null || descripcion.isBlank()) {
            throw new CategoriaException("La descripción es obligatoria");
        }
        String limpia = descripcion.trim();
        if (limpia.length() > 150) {
            throw new CategoriaException("La descripción no puede tener más de 150 caracteres");
        }
        return limpia;
    }
}
