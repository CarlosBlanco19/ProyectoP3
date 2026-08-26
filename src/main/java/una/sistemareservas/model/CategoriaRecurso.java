package una.sistemareservas.model;

public class CategoriaRecurso {
    private String ID;
    private String Descripcion;
    // Cada categoria de recurso tiene minimo un id y una descripcion (Tal vez se pueda añadir algo mas despues)

    public CategoriaRecurso(String ID, String Descripcion) { // Constructor
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

    // Devuelve la descripcion, que vendria siendo como su informacion, se puede añadir mas detalle despues
    public String toString(){
        return Descripcion;
    }
}
