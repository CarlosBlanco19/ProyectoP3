package una.sistemareservas.logic;

import una.sistemareservas.datos.CategoriaRecursoDatos;
import una.sistemareservas.model.CategoriaRecursoDTO;
import una.sistemareservas.utilidades.Busqueda;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class CategoriaLogic {

    private final CategoriaRecursoDatos categoriasDatos = new CategoriaRecursoDatos();
    private final List<CategoriaRecursoDTO> categorias;

    private void guardar(){
        try{
            categoriasDatos.deserializar(categorias);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public CategoriaLogic(){
        List<CategoriaRecursoDTO>  listos;
        try{
            listos = categoriasDatos.serializar();
        }catch (IOException e){
            e.printStackTrace();
            listos = new ArrayList<>();
        }
        categorias = listos;
    }

    public boolean agregar(CategoriaRecursoDTO categoria){
        if(categoria == null || buscarID(categoria.getID()) !=null){
            return false;
        }
        categorias.add(categoria);
        guardar();
        return true;
    }

    public boolean eliminar(String id){
        CategoriaRecursoDTO categoria = buscarID(id);
        if(categoria == null){
            return false;
        }
        categorias.remove(categoria);
        guardar();
        return true;
    }

    public List<CategoriaRecursoDTO> listar(){
        return categorias;
    }

    public CategoriaRecursoDTO buscarID(String id){
        return Busqueda.buscarID(categorias, id, CategoriaRecursoDTO::getID);
    }

    public List<CategoriaRecursoDTO> buscarDescripcion(String desc){
        List<CategoriaRecursoDTO> res = new ArrayList<>();

        if(desc == null || desc.isBlank()){
            return res; // vacio
        }

        String busqueda = desc.toLowerCase();
        for(CategoriaRecursoDTO categoria : categorias) {
            if (categoria.getDescripcion().toLowerCase().contains(busqueda)) {
                res.add(categoria);
            }
        }
        return res;
    }


    public boolean actualizar(String id, String desc){ //cambiar la descripcion de la categoria
        CategoriaRecursoDTO categoria = buscarID(id);
        if(categoria == null|| desc == null){
            return false;
        }
        categoria.setDescripcion(desc);
        guardar();
        return true;
    }


}
