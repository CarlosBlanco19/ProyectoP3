package una.sistemareservas.logic;

import una.sistemareservas.datos.UsuarioDatos;
import una.sistemareservas.dto.AdministradorDTO;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.UsuarioDTO;
import una.sistemareservas.exception.UsuarioException;

import java.sql.SQLException;

// Login y cambio de clave
public class UsuarioLogic {

    private static final int LARGO_MAXIMO_CLAVE = 50;

    private final UsuarioDatos usuarioDatos = new UsuarioDatos();


    public UsuarioDTO login(String id, String clave) throws UsuarioException {
        if (estaVacio(id) || estaVacio(clave)) {
            throw new UsuarioException("Debe ingresar el usuario y la clave");
        }

        UsuarioDTO usuario;
        try {
            usuario = usuarioDatos.buscarUsuario(id.trim());
        } catch (SQLException e) {
            throw new UsuarioException(ErroresBD.mensaje("validar el usuario", e), e);
        }

        // Mismo mensaje en los dos casos para no darle pistas a alguien que esté adivinando usuarios
        if (usuario == null || !usuario.getClave().equals(clave)) {
            throw new UsuarioException("Usuario o clave incorrectos");
        }
        return sinClave(usuario);
    }

    public void cambiarClave(String id, String claveActual, String claveNueva) throws UsuarioException {
        if (estaVacio(id) || estaVacio(claveActual) || estaVacio(claveNueva)) {
            throw new UsuarioException("Rellene todos los campos");
        }
        if (claveNueva.length() > LARGO_MAXIMO_CLAVE) {
            throw new UsuarioException("La clave nueva no puede tener más de " + LARGO_MAXIMO_CLAVE + " caracteres");
        }
        if (claveNueva.contains(" ")) {
            throw new UsuarioException("La clave nueva no puede tener espacios");
        }

        try {
            UsuarioDTO usuario = usuarioDatos.buscarUsuario(id.trim());
            if (usuario == null) {
                throw new UsuarioException("ID no encontrado");
            }
            if (!usuario.getClave().equals(claveActual)) {
                throw new UsuarioException("Clave actual incorrecta");
            }
            if (claveActual.equals(claveNueva)) {
                throw new UsuarioException("La clave nueva debe ser diferente a la actual");
            }
            usuarioDatos.actualizarClave(usuario.getID(), claveNueva);
        } catch (SQLException e) {
            throw new UsuarioException(ErroresBD.mensaje("cambiar la clave", e), e);
        }
    }

    // Copia del usuario sin la clave
    private UsuarioDTO sinClave(UsuarioDTO usuario) {
        if (usuario instanceof FuncionarioDTO funcionario) {
            return new FuncionarioDTO(funcionario.getID(), funcionario.getNombre(), funcionario.getTelefono());
        }
        return new AdministradorDTO(usuario.getID(), null);
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
