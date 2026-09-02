package una.sistemareservas.logic;

import una.sistemareservas.datos.RecursoDatos;
import una.sistemareservas.model.RecursoDTO;
import una.sistemareservas.utilidades.Busqueda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecursoLogic {

    private final RecursoDatos recursosDatos = new RecursoDatos();
    private final List<RecursoDTO> recursos;

    private void guardar(){
        try{
            recursosDatos.deserializar(recursos);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public RecursoLogic(CategoriaLogic categoriasLogic) {
        List<RecursoDTO> listos;

        try{
            listos = recursosDatos.serializar(categoriasLogic.listar());

        }catch(IOException e){
            e.printStackTrace();
            listos = new ArrayList<>();
        }
        recursos = listos;
    }


    public RecursoDTO buscarID(String id){
        return Busqueda.buscarID(recursos, id, RecursoDTO::getID);
    }

    public boolean agregar(RecursoDTO recurso){
        if(recurso == null || buscarID(recurso.getID()) != null){
            return false;
        }
        recursos.add(recurso);
        guardar();
        return true;
    }

    public boolean eliminar(String id){
        RecursoDTO recurso = buscarID(id);
        if(recurso == null) {
            return false;
        }
        recursos.remove(recurso);
        guardar();
        return true;
    }

    public List<RecursoDTO> listar(){
        return recursos;
    }

    public List<RecursoDTO> listarCategoria(String idCategoria) {
        List<RecursoDTO> res = new ArrayList<>();
        for (RecursoDTO recurso : recursos) {
            if (recurso.getCategoria() != null && recurso.getCategoria().getID().equals(idCategoria)) {
                res.add(recurso);
            }
        }
        return res;
    }
}
