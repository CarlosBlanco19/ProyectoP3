package una.sistemareservas.model;

public class Funcionario extends Usuario {
    // Usuario FUNCIONARIO, hereda de usuario

    private String Nombre;
    private String Telefono;
    // Atributos extra requeridos por Funcionario

    public Funcionario(String ID, String Clave, String Nombre, String Telefono) { // Constructor base
        super(ID, Clave, Rol.FUNCIONARIO);
        this.Nombre = Nombre;
        this.Telefono = Telefono;
    }

    public Funcionario(String ID, String Nombre, String Telefono) { // Constructor pero sin necesidad de recibir una clave, llama al constructor original pero mandando el id como clave
        this(ID, ID, Nombre, Telefono);
    }

    // GETS
    public String getNombre() {
        return Nombre;
    }
    public String getTelefono() {
        return Telefono;
    }

    // SETS
    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }
    public void setTelefono(String Telefono) {
        this.Telefono = Telefono;
    }
}
