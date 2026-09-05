package una.sistemareservas.service;

import una.sistemareservas.logic.CategoriaLogic;
import una.sistemareservas.dto.CategoriaRecursoDTO;

import java.util.List;

public class CategoriaService {
    private final CategoriaLogic categoriaLogic = new CategoriaLogic();

    public CategoriaRecursoDTO buscarID(String id) {
        return categoriaLogic.buscarID(id);
    }

    public List<CategoriaRecursoDTO> buscarPorDescripcion(String des) {
        return categoriaLogic.buscarDescripcion(des);
    }

    public boolean agregar(CategoriaRecursoDTO categoria) {
        return categoriaLogic.agregar(categoria);
    }

    public boolean actualizar(String id, String desc) {
        return categoriaLogic.actualizar(id, desc);
    }

    public boolean eliminar(String id) {
        return categoriaLogic.eliminar(id);
    }

    public List<CategoriaRecursoDTO> listar() {
        return categoriaLogic.listar();
    }

    public CategoriaLogic logic() {
        return categoriaLogic;
    }
}
