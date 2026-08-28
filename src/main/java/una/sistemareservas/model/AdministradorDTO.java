package una.sistemareservas.model;

public class AdministradorDTO extends UsuarioDTO {
    // Usuario ADMINISTRADOR, simplemente hereda

    public AdministradorDTO(String ID, String Clave) { // Constructor que llama al constructor de Usuario con el rol Administrador
        super(ID, Clave, Rol.ADMINISTRADOR);
    }

}
