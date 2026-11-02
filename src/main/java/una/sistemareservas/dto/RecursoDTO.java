package una.sistemareservas.dto;

import java.io.Serializable;
import java.util.Objects;

public class RecursoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ID;
    private String Descripcion;
    private CategoriaRecursoDTO categoria;

    public RecursoDTO(String ID, String Descripcion, CategoriaRecursoDTO categoria) {
        this.ID = ID;
        this.Descripcion = Descripcion;
        this.categoria = categoria;
    }


    public String getID() {
        return ID;
    }
    public String getDescripcion() {
        return Descripcion;
    }
    public CategoriaRecursoDTO getCategoria() {
        return categoria;
    }



    public void setID(String ID) {
        this.ID = ID;
    }
    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }
    public void setCategoria(CategoriaRecursoDTO categoria) {
        this.categoria = categoria;
    }

    //dos recursos son iguales si tiene el mismo id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecursoDTO otro)) return false;
        return Objects.equals(ID, otro.ID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ID);
    }

    @Override
    public String toString() {
        return Descripcion;
    }
}
