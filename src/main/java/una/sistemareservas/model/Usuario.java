package una.sistemareservas.model;

// Usuario abstracto base del que heredan Funcionario y Administrador
public abstract class Usuario {
    private String ID;
    private String Clave;
    private Rol rol;
    // ID, CLAVE y ROL lo tienen todos los demas tipos de usuario

    // Constructor
    protected Usuario(String ID, String Clave, Rol rol) {
        this.ID = ID;
        this.Clave = Clave;
        this.rol = rol;
    }

    public String getClave() {
        return Clave;
    }
    public String getID() {
        return ID;
    }
    public Rol getRol() {
        return rol;
    }

    // SETS
    public void setClave(String clave) {
        Clave = clave;
    }
    public void setID(String id) {
        ID = id;
    }
    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
