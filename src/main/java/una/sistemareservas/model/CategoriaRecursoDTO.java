package una.sistemareservas.model;

public class CategoriaRecursoDTO {
    private String ID;
    private String Descripcion;
    // Cada categoria de recurso tiene minimo un id y una descripcion (Tal vez se pueda añadir algo mas despues)

    public CategoriaRecursoDTO(String ID, String Descripcion) { // Constructor
        this.ID = ID;
        this.Descripcion = Descripcion;
    }

    // GETS
    public String getID() {
        return ID;
    }
    public String getDescripcion() {
        return Descripcion;
    }

    // SETS
    public void setID(String ID) {
        this.ID = ID;
    }
    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }
}
