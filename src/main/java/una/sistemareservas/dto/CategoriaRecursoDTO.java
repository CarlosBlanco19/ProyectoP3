package una.sistemareservas.dto;

import java.io.Serializable;

// Categoría de recurso (Laptop, Proyector, ...). El ID lo genera la base de datos (AUTO_INCREMENT).
public class CategoriaRecursoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int ID;
    private String Descripcion;

    public CategoriaRecursoDTO(int ID, String Descripcion) {
        this.ID = ID;
        this.Descripcion = Descripcion;
    }

    // GETS
    public int getID() {
        return ID;
    }
    public String getDescripcion() {
        return Descripcion;
    }

    // SETS
    public void setID(int ID) {
        this.ID = ID;
    }
    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    // Dos categorías son iguales si tienen el mismo ID
    // (necesario porque cada respuesta del servidor trae objetos nuevos)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoriaRecursoDTO otra)) return false;
        return ID == otra.ID;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(ID);
    }

    @Override
    public String toString() {
        return Descripcion;
    }
}
