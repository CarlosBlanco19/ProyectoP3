package una.sistemareservas.dto;

public class CategoriaRecursoDTO {
    private int ID;
    private String Descripcion;
    // Cada categoria de recurso tiene minimo un id y una descripcion (Tal vez se pueda añadir algo mas despues)

    public CategoriaRecursoDTO(int ID, String Descripcion) { // Constructor
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

    @Override
    public String toString(){
        return Descripcion;
    }
}
