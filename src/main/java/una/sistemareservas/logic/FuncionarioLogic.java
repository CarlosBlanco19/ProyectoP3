package una.sistemareservas.logic;

import una.sistemareservas.datos.UsuarioDatos;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.exception.UsuarioException;

import java.sql.SQLException;
import java.util.List;

// Mantenimiento de funcionarios (solo el administrador)
public class FuncionarioLogic {

    private final UsuarioDatos usuarioDatos = new UsuarioDatos();

    public List<FuncionarioDTO> listar() throws UsuarioException {
        try {
            return usuarioDatos.listarFuncionarios();
        } catch (SQLException e) {
            throw new UsuarioException(ErroresBD.mensaje("cargar los funcionarios", e), e);
        }
    }

    public FuncionarioDTO buscarPorId(String id) throws UsuarioException {
        if (id == null || id.isBlank()) {
            return null;
        }
        try {
            return usuarioDatos.buscarFuncionario(id.trim());
        } catch (SQLException e) {
            throw new UsuarioException(ErroresBD.mensaje("buscar el funcionario", e), e);
        }
    }

    public List<FuncionarioDTO> buscarPorNombre(String nombre) throws UsuarioException {
        if (nombre == null || nombre.isBlank()) {
            return listar();
        }
        try {
            return usuarioDatos.buscarFuncionariosPorNombre(nombre.trim());
        } catch (SQLException e) {
            throw new UsuarioException(ErroresBD.mensaje("buscar funcionarios", e), e);
        }
    }

    public void agregar(FuncionarioDTO funcionario) throws UsuarioException {
        validar(funcionario);
        String id = funcionario.getID().trim();
        try {
            if (usuarioDatos.buscarUsuario(id) != null) {
                throw new UsuarioException("Ya existe un usuario con el ID " + id);
            }
            FuncionarioDTO limpio = new FuncionarioDTO(id, funcionario.getNombre().trim(), funcionario.getTelefono().trim());
            usuarioDatos.insertarFuncionario(limpio, id); // La clave inicial es el mismo ID
        } catch (SQLException e) {
            if (ErroresBD.esDuplicado(e)) {
                throw new UsuarioException("Ya existe un usuario con el ID " + id);
            }
            throw new UsuarioException(ErroresBD.mensaje("guardar el funcionario", e), e);
        }
    }

    public void actualizar(FuncionarioDTO funcionario) throws UsuarioException {
        validar(funcionario);
        String id = funcionario.getID().trim();
        try {
            if (usuarioDatos.buscarFuncionario(id) == null) {
                throw new UsuarioException("No existe un funcionario con el ID " + id);
            }
            usuarioDatos.actualizarFuncionario(
                    new FuncionarioDTO(id, funcionario.getNombre().trim(), funcionario.getTelefono().trim()));
        } catch (SQLException e) {
            throw new UsuarioException(ErroresBD.mensaje("actualizar el funcionario", e), e);
        }
    }

    public void eliminar(String id) throws UsuarioException {
        if (id == null || id.isBlank()) {
            throw new UsuarioException("Debe seleccionar un funcionario");
        }
        try {
            if (usuarioDatos.buscarFuncionario(id.trim()) == null) {
                throw new UsuarioException("No existe un funcionario con el ID " + id.trim());
            }
            usuarioDatos.eliminarFuncionario(id.trim());
        } catch (SQLException e) {
            if (ErroresBD.esLlaveForanea(e)) {
                throw new UsuarioException("No se puede eliminar el funcionario porque tiene reservas registradas");
            }
            throw new UsuarioException(ErroresBD.mensaje("eliminar el funcionario", e), e);
        }
    }

    // Validaciones que no necesitan la base de datos
    void validar(FuncionarioDTO funcionario) throws UsuarioException {
        if (funcionario == null) {
            throw new UsuarioException("No se recibió el funcionario");
        }
        String id = funcionario.getID();
        String nombre = funcionario.getNombre();
        String telefono = funcionario.getTelefono();

        if (id == null || id.isBlank()) {
            throw new UsuarioException("Debe ingresar un ID");
        }
        if (id.trim().contains(" ")) {
            throw new UsuarioException("El ID no puede tener espacios");
        }
        if (id.trim().length() > 30) {
            throw new UsuarioException("El ID no puede tener más de 30 caracteres");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new UsuarioException("Debe ingresar el nombre");
        }
        if (nombre.trim().length() > 50) {
            throw new UsuarioException("El nombre no puede tener más de 50 caracteres");
        }
        if (telefono == null || telefono.isBlank()) {
            throw new UsuarioException("Debe ingresar el teléfono");
        }
        if (!telefono.trim().matches("[0-9 +-]{8,20}")) {
            throw new UsuarioException("El teléfono solo puede tener números (mínimo 8)");
        }
    }
}
