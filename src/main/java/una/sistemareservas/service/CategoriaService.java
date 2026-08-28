package una.sistemareservas.service;

import una.sistemareservas.model.CategoriaRecursoDTO;
import una.sistemareservas.utilidades.Busqueda;

import java.util.ArrayList;
import java.util.List;

public class CategoriaService {

    private final List<CategoriaRecursoDTO> categorias = new ArrayList<>();

    public CategoriaService(){
        //implementar cuando exista persistencia
        //PROVISIONAL
        categorias.add(new CategoriaRecursoDTO("c1","Laptop"));
    }

    public boolean agregar(CategoriaRecursoDTO categoria){
        if(categoria == null || buscarID(categoria.getID()) !=null){
            return false;
        }
        categorias.add(categoria);
        return true;
    }

    public boolean eliminar(String id){
        CategoriaRecursoDTO categoria = buscarID(id);
        if(categoria == null){
            return false;
        }
        categorias.remove(categoria);
        return true;
    }

    public List<CategoriaRecursoDTO> listar(){
        return categorias;
    }

    public CategoriaRecursoDTO buscarID(String id){
        return Busqueda.buscarID(categorias, id, CategoriaRecursoDTO::getID);
    }
}
