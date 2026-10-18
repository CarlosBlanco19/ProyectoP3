package una.sistemareservas.logic;

import una.sistemareservas.datos.CategoriaRecursoDatos;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.exception.CategoriaException;
import una.sistemareservas.utilidades.Busqueda;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class CategoriaLogic {


    private final CategoriaRecursoDatos categoriasDatos = new CategoriaRecursoDatos();


    // id autogenerado y se le asigna a la categoria que llega por parametro
    public void agregar(CategoriaRecursoDTO categoria) throws CategoriaException {
        if (categoria == null || categoria.getDescripcion() == null || categoria.getDescripcion().isBlank()) {
            throw new CategoriaException("La descripción es obligatoria");
        }

        try {
            int idGenerado = categoriasDatos.insertar(categoria.getDescripcion().trim());
            categoria.setID(idGenerado);
        } catch (SQLException e) {
            throw new CategoriaException("No se pudo guardar la categoría");
        }
    }

    public void actualizar(int id, String desc) throws CategoriaException {
        if (buscarID(id) == null) {
            throw new CategoriaException("La categoría no existe");
        }
        if (desc == null || desc.isBlank()) {
            throw new CategoriaException("La descripción es obligatoria");
        }

        try {
            categoriasDatos.actualizar(id, desc.trim());
        } catch (SQLException e) {
            throw new CategoriaException("No se pudo actualizar la categoría");
        }
    }

    public void eliminar(int id) throws CategoriaException {
        if (buscarID(id) == null) {
            throw new CategoriaException("La categoría no existe");
        }

        try {
            categoriasDatos.eliminar(id);
        } catch (SQLException e) {
            // este error significa que la categ es usada por otra tabla
            if (e.getErrorCode() == 1451) {
                throw new CategoriaException("No se puede eliminar la categoría porque tiene recursos o reservas asociados");
            }
            throw new CategoriaException("No se pudo eliminar la categoría");
        }
    }

    public List<CategoriaRecursoDTO> listar() {
        try {
            return categoriasDatos.listar();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public CategoriaRecursoDTO buscarID(int id) {
        for (CategoriaRecursoDTO categoria : listar()) {
            if (categoria.getID() == id) {
                return categoria;
            }
        }
        return null;
    }

    public List<CategoriaRecursoDTO> buscarDescripcion(String desc) {
        List<CategoriaRecursoDTO> res = new ArrayList<>();

        if (desc == null || desc.isBlank()) {
            return res; // vacio
        }

        String busqueda = desc.toLowerCase();
        for (CategoriaRecursoDTO categoria : listar()) {
            if (categoria.getDescripcion().toLowerCase().contains(busqueda)) {
                res.add(categoria);
            }
        }
        return res;
    }
}
