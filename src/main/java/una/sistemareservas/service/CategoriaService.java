package una.sistemareservas.service;

import una.sistemareservas.model.CategoriaRecurso;
import una.sistemareservas.utilidades.Busqueda;

import java.util.ArrayList;
import java.util.List;

public class CategoriaService {

    private final List<CategoriaRecurso> categorias = new ArrayList<>();

    public CategoriaService(){
        //implementar cuando exista persistencia
        //PROVISIONAL
        categorias.add(new CategoriaRecurso("c1","Laptop"));
    }

    public boolean agregar(CategoriaRecurso categoria){
        if(categoria == null || buscarID(categoria.getID()) !=null){
            return false;
        }
        categorias.add(categoria);
        return true;
    }

    public boolean eliminar(String id){
        CategoriaRecurso categoria = buscarID(id);
        if(categoria == null){
            return false;
        }
        categorias.remove(categoria);
        return true;
    }

    public List<CategoriaRecurso> listar(){
        return categorias;
    }

    public CategoriaRecurso buscarID(String id){
        return Busqueda.buscarID(categorias, id, CategoriaRecurso::getID);
    }
}
