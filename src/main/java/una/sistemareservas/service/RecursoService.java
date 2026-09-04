package una.sistemareservas.service;

import una.sistemareservas.logic.RecursoLogic;
import una.sistemareservas.dto.RecursoDTO;

import java.util.List;

public class RecursoService {
    private final RecursoLogic recurseLogic;


    public RecursoService(CategoriaService categoriaService) {
        this.recurseLogic = new RecursoLogic(categoriaService.logic());
    }



    public RecursoDTO buscarID(String id) {
        return recurseLogic.buscarID(id);
    }
    public boolean agregar(RecursoDTO recurso) {
        return recurseLogic.agregar(recurso);
    }

    public boolean eliminar(String id) {
        return recurseLogic.eliminar(id);
    }
    public List<RecursoDTO> listar() {
        return recurseLogic.listar();
    }


    public List<RecursoDTO> listarPorCategoria(String categoria) {
        return recurseLogic.listarCategoria(categoria);
    }
}
