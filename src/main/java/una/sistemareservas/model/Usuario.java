package una.sistemareservas.model;

// Usuario abstracto base del que heredan Funcionario y Administrador
public abstract class Usuario {
    private String ID;
    private String Clave;
    private final Rol rol;
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

    // Metodo para ver si dos usuarios son iguales, basandose en el ID
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Usuario)) {
            return false;
        }
        Usuario otro = (Usuario) o;
        return ID.equals(otro.ID);
    }
}
