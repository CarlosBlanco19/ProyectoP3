package una.sistemareservas.model;

public class RecursoDTO {
    private String ID;
    private CategoriaRecursoDTO categoria;
    private String Descripcion;

    public RecursoDTO(String ID, String Descripcion, CategoriaRecursoDTO categoria) {
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
    public CategoriaRecursoDTO getCategoria() {
        return categoria;
    }
    public void setCategoria(CategoriaRecursoDTO categoria) {
        this.categoria = categoria;
    }
}
