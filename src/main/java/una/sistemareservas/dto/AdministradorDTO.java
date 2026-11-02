package una.sistemareservas.dto;

// Usuario ADMINISTRADOR: solo tiene ID, clave y rol
public class AdministradorDTO extends UsuarioDTO {
    private static final long serialVersionUID = 1L;

    public AdministradorDTO(String ID, String Clave) {
        super(ID, Clave, Rol.ADMINISTRADOR);
    }
}
