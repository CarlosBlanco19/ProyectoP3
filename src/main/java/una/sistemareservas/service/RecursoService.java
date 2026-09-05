package una.sistemareservas.service;

import una.sistemareservas.logic.RecursoLogic;
import una.sistemareservas.dto.RecursoDTO;

import java.util.List;

public class RecursoService {
    private final RecursoLogic recursoLogic;

    public RecursoService(CategoriaService categoriaService) {
        this.recursoLogic = new RecursoLogic(categoriaService.logic());
    }

    public RecursoDTO buscarID(String id) {
        return recursoLogic.buscarID(id);
    }
    public boolean agregar(RecursoDTO recurso) {
        return recursoLogic.agregar(recurso);
    }

    public boolean eliminar(String id) {
        return recursoLogic.eliminar(id);
    }
    public List<RecursoDTO> listar() {
        return recursoLogic.listar();
    }

    public List<RecursoDTO> listarPorCategoria(String categoria) {
        return recursoLogic.listarCategoria(categoria);
    }
    public RecursoLogic logic() {
        return recursoLogic;
    }
}
