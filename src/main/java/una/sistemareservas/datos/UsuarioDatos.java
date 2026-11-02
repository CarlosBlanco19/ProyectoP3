package una.sistemareservas.datos;

import una.sistemareservas.dto.AdministradorDTO;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.Rol;
import una.sistemareservas.dto.UsuarioDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Acceso a las tablas usuario y funcionario
public class UsuarioDatos {

    private static final String SELECT_FUNCIONARIOS =
            "SELECT fk_usuario, nombre, telefono FROM funcionario ";

    // Busca cualquier usuario (admin o funcionario) INCLUYENDO la clave. Solo se usa para validar el login.
    public UsuarioDTO buscarUsuario(String id) throws SQLException {
        String sql = "SELECT u.pk_usuario, u.clave, u.rol, f.nombre, f.telefono "
                + "FROM usuario u LEFT JOIN funcionario f ON f.fk_usuario = u.pk_usuario "
                + "WHERE u.pk_usuario = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String clave = rs.getString("clave");
                Rol rol = Rol.valueOf(rs.getString("rol"));
                if (rol == Rol.ADMINISTRADOR) {
                    return new AdministradorDTO(rs.getString("pk_usuario"), clave);
                }
                return new FuncionarioDTO(rs.getString("pk_usuario"), clave,
                        rs.getString("nombre"), rs.getString("telefono"));
            }
        }
    }

    public void actualizarClave(String id, String claveNueva) throws SQLException {
        String sql = "UPDATE usuario SET clave = ? WHERE pk_usuario = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, claveNueva);
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    // ---------------- Funcionarios (nunca devuelven la clave) ----------------

    public List<FuncionarioDTO> listarFuncionarios() throws SQLException {
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_FUNCIONARIOS + "ORDER BY fk_usuario")) {
            return leerFuncionarios(ps);
        }
    }

    public FuncionarioDTO buscarFuncionario(String id) throws SQLException {
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_FUNCIONARIOS + "WHERE fk_usuario = ?")) {
            ps.setString(1, id);
            List<FuncionarioDTO> lista = leerFuncionarios(ps);
            return lista.isEmpty() ? null : lista.get(0);
        }
    }

    // LIKE '%texto%'. La base de datos usa una collation que no distingue mayúsculas
    public List<FuncionarioDTO> buscarFuncionariosPorNombre(String texto) throws SQLException {
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_FUNCIONARIOS + "WHERE nombre LIKE ? ORDER BY nombre")) {
            ps.setString(1, "%" + texto + "%");
            return leerFuncionarios(ps);
        }
    }

    // Inserta en usuario y en funcionario dentro de una TRANSACCIÓN: o se guardan los dos o ninguno
    public void insertarFuncionario(FuncionarioDTO funcionario, String clave) throws SQLException {
        try (Connection con = ConexionDB.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psUsuario = con.prepareStatement(
                         "INSERT INTO usuario (pk_usuario, clave, rol) VALUES (?, ?, 'FUNCIONARIO')");
                 PreparedStatement psFuncionario = con.prepareStatement(
                         "INSERT INTO funcionario (fk_usuario, nombre, telefono) VALUES (?, ?, ?)")) {
                psUsuario.setString(1, funcionario.getID());
                psUsuario.setString(2, clave);
                psUsuario.executeUpdate();

                psFuncionario.setString(1, funcionario.getID());
                psFuncionario.setString(2, funcionario.getNombre());
                psFuncionario.setString(3, funcionario.getTelefono());
                psFuncionario.executeUpdate();

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void actualizarFuncionario(FuncionarioDTO funcionario) throws SQLException {
        String sql = "UPDATE funcionario SET nombre = ?, telefono = ? WHERE fk_usuario = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, funcionario.getNombre());
            ps.setString(2, funcionario.getTelefono());
            ps.setString(3, funcionario.getID());
            ps.executeUpdate();
        }
    }

    // Borra de funcionario y de usuario en una transacción.
    // Si el funcionario tiene reservas, MySQL lanza el error 1451 (llave foránea) y no se borra nada.
    public void eliminarFuncionario(String id) throws SQLException {
        try (Connection con = ConexionDB.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psFuncionario = con.prepareStatement("DELETE FROM funcionario WHERE fk_usuario = ?");
                 PreparedStatement psUsuario = con.prepareStatement("DELETE FROM usuario WHERE pk_usuario = ?")) {
                psFuncionario.setString(1, id);
                psFuncionario.executeUpdate();
                psUsuario.setString(1, id);
                psUsuario.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private List<FuncionarioDTO> leerFuncionarios(PreparedStatement ps) throws SQLException {
        List<FuncionarioDTO> funcionarios = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                funcionarios.add(new FuncionarioDTO(rs.getString("fk_usuario"),
                        rs.getString("nombre"), rs.getString("telefono")));
            }
        }
        return funcionarios;
    }
}
