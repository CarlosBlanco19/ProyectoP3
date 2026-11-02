package una.sistemareservas.utilidades;

import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.Rol;
import una.sistemareservas.dto.UsuarioDTO;

// Guarda quién inició sesión, para que cualquier pantalla lo pueda consultar
public final class SesionGlobal {

    private static UsuarioDTO usuarioActual;

    private SesionGlobal() {
    }

    public static void iniciarSesion(UsuarioDTO usuario) {
        usuarioActual = usuario;
    }

    public static UsuarioDTO getUsuario() {
        return usuarioActual;
    }

    // ID del usuario que está usando el sistema (null si nadie ha iniciado sesión)
    public static String getFuncionarioActual() {
        return usuarioActual != null ? usuarioActual.getID() : null;
    }

    public static boolean esAdministrador() {
        return usuarioActual != null && usuarioActual.getRol() == Rol.ADMINISTRADOR;
    }

    public static String getNombre() {
        if (usuarioActual instanceof FuncionarioDTO funcionario) {
            return funcionario.getNombre();
        }
        return usuarioActual != null ? usuarioActual.getID() : "";
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}
