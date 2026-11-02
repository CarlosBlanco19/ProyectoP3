package una.sistemareservas.dto;

// Usuario FUNCIONARIO: además de ID y clave tiene nombre y teléfono
public class FuncionarioDTO extends UsuarioDTO {
    private static final long serialVersionUID = 1L;

    private String Nombre;
    private String Telefono;

    public FuncionarioDTO(String ID, String Clave, String Nombre, String Telefono) {
        super(ID, Clave, Rol.FUNCIONARIO);
        this.Nombre = Nombre;
        this.Telefono = Telefono;
    }

    // Constructor sin clave (por ejemplo, cuando se lista: la clave nunca viaja al Frontend)
    public FuncionarioDTO(String ID, String Nombre, String Telefono) {
        this(ID, null, Nombre, Telefono);
    }

    // GETS
    public String getNombre() {
        return Nombre;
    }
    public String getTelefono() {
        return Telefono;
    }

    // SETS
    public void setNombre(String nombre) {
        Nombre = nombre;
    }
    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    @Override
    public String toString() {
        return Nombre;
    }
}
