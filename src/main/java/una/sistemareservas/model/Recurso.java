package una.sistemareservas.model;

public class Recurso {
    private String ID;
    private CategoriaRecurso categoria;
    private String Descripcion;

    public Recurso(String ID, String Descripcion,  CategoriaRecurso categoria) {
        this.ID = ID;
        this.Descripcion = Descripcion;
        this.categoria = categoria;
    }

    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }
    public String getDescripcion() {
        return Descripcion;
    }
    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }
    public CategoriaRecurso getCategoria() {
        return categoria;
    }
    public void setCategoria(CategoriaRecurso categoria) {
        this.categoria = categoria;
    }

    // Metodo para comparar si dos recursos son iguales, basandose en el ID
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Recurso)) {
            return false;
        }
        Recurso otro = (Recurso) o;
        return ID.equals(otro.ID);
    }

    public String toString() {
        return Descripcion;
    }
}
