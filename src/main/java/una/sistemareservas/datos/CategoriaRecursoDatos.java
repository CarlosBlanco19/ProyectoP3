package una.sistemareservas.datos;

import una.sistemareservas.dto.CategoriaRecursoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Acceso a la tabla categoriaRecursos
public class CategoriaRecursoDatos {

    public List<CategoriaRecursoDTO> listar() throws SQLException {
        String sql = "SELECT pk_categoria, descripcion FROM categoriaRecursos ORDER BY pk_categoria";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return leer(ps);
        }
    }

    public CategoriaRecursoDTO buscarPorId(int id) throws SQLException {
        String sql = "SELECT pk_categoria, descripcion FROM categoriaRecursos WHERE pk_categoria = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            List<CategoriaRecursoDTO> lista = leer(ps);
            return lista.isEmpty() ? null : lista.get(0);
        }
    }

    public List<CategoriaRecursoDTO> buscarPorDescripcion(String texto) throws SQLException {
        String sql = "SELECT pk_categoria, descripcion FROM categoriaRecursos WHERE descripcion LIKE ? ORDER BY pk_categoria";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            return leer(ps);
        }
    }

    // ¿Ya existe otra categoría con exactamente esa descripción? (sin distinguir mayúsculas)
    public boolean existeDescripcion(String descripcion, int idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM categoriaRecursos WHERE descripcion = ? AND pk_categoria <> ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, descripcion);
            ps.setInt(2, idExcluido);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // Inserta una categoría y devuelve el ID que generó MySQL (AUTO_INCREMENT)
    public int insertar(String descripcion) throws SQLException {
        String sql = "INSERT INTO categoriaRecursos (descripcion) VALUES (?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, descripcion);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                claves.next();
                return claves.getInt(1);
            }
        }
    }

    public void actualizar(int id, String descripcion) throws SQLException {
        String sql = "UPDATE categoriaRecursos SET descripcion = ? WHERE pk_categoria = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, descripcion);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    // Si la categoría tiene recursos o reservas, MySQL lanza el error 1451 (llave foránea)
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM categoriaRecursos WHERE pk_categoria = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private List<CategoriaRecursoDTO> leer(PreparedStatement ps) throws SQLException {
        List<CategoriaRecursoDTO> categorias = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categorias.add(new CategoriaRecursoDTO(rs.getInt("pk_categoria"), rs.getString("descripcion")));
            }
        }
        return categorias;
    }
}
