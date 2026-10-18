package una.sistemareservas.service;

import una.sistemareservas.exception.CategoriaException;
import una.sistemareservas.logic.CategoriaLogic;
import una.sistemareservas.dto.CategoriaRecursoDTO;

import java.util.List;

public class CategoriaService {
    private final CategoriaLogic categoriaLogic = new CategoriaLogic();

    public CategoriaRecursoDTO buscarID(int id) {
        return categoriaLogic.buscarID(id);
    }

    public List<CategoriaRecursoDTO> buscarPorDescripcion(String des) {
        return categoriaLogic.buscarDescripcion(des);
    }

    public void agregar(CategoriaRecursoDTO categoria) {categoriaLogic.agregar(categoria);
    }

    public void actualizar(int id, String desc) throws CategoriaException {
        categoriaLogic.actualizar(id, desc);
    }

    public void eliminar(int id) throws CategoriaException {
        categoriaLogic.eliminar(id);
    }


    public List<CategoriaRecursoDTO> listar() {
        return categoriaLogic.listar();
    }

    public CategoriaLogic logic() {
        return categoriaLogic;
    }
}
