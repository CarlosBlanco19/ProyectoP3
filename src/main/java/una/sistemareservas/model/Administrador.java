package una.sistemareservas.model;

public class Administrador extends Usuario {
    // Usuario ADMINISTRADOR, simplemente hereda

    public Administrador(String ID, String Clave) { // Constructor que llama al constructor de Usuario con el rol Administrador
        super(ID, Clave, Rol.ADMINISTRADOR);
    }

}
