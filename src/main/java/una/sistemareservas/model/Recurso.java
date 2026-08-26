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

    public String toString() {
        return Descripcion;
    }
}
