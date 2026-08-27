package una.sistemareservas.utilidades;

import java.util.List;
import java.util.function.Function;

public class Busqueda {
    private Busqueda(){}

    public static <T> T buscarID(List<T> lista, String id, Function<T, String> getID) {
        if(id == null){
            return null;
        }
        for(T e : lista){
            if(getID.apply(e).equals(id)){
                return e;
            }
        }
        return null;
    }
}
