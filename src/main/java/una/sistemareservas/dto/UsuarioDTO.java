package una.sistemareservas.dto;

import java.io.Serializable;
import java.util.Objects;

public abstract class UsuarioDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ID;
    private String Clave;
    private final Rol rol;

    protected UsuarioDTO(String ID, String Clave, Rol rol) {
        this.ID = ID;
        this.Clave = Clave;
        this.rol = rol;
    }

    public String getID() {
        return ID;
    }
    public String getClave() {
        return Clave;
    }
    public Rol getRol() {
        return rol;
    }

    public void setID(String id) {
        ID = id;
    }
    public void setClave(String clave) {
        Clave = clave;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioDTO otro)) return false;
        return Objects.equals(ID, otro.ID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ID);
    }
}
