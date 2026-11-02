package una.sistemareservas.datos;

import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.RecursoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Acceso a la tabla recurso (cada recurso se devuelve con su categoría completa)
public class RecursoDatos {

    private static final String SELECT_RECURSOS =
            "SELECT r.pk_recurso, r.descripcion, c.pk_categoria, c.descripcion AS descripcion_categoria "
            + "FROM recurso r JOIN categoriaRecursos c ON c.pk_categoria = r.fk_categoria ";

    public List<RecursoDTO> listar() throws SQLException {
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_RECURSOS + "ORDER BY r.pk_recurso")) {
            return leer(ps);
        }
    }

    public List<RecursoDTO> listarPorCategoria(int idCategoria) throws SQLException {
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_RECURSOS + "WHERE r.fk_categoria = ? ORDER BY r.pk_recurso")) {
            ps.setInt(1, idCategoria);
            return leer(ps);
        }
    }

    public RecursoDTO buscarPorId(String id) throws SQLException {
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_RECURSOS + "WHERE r.pk_recurso = ?")) {
            ps.setString(1, id);
            List<RecursoDTO> lista = leer(ps);
            return lista.isEmpty() ? null : lista.get(0);
        }
    }

    /**
     * Recursos de una categoría que están LIBRES en esa fecha y horario.
     * Un recurso está ocupado si pertenece a una reserva ACTIVA del mismo día cuyo horario se traslapa:
     *     inicioExistente < finNuevo  Y  finExistente > inicioNuevo
     */
    public List<RecursoDTO> listarDisponibles(int idCategoria, LocalDate fecha, LocalTime inicio, LocalTime fin)
            throws SQLException {
        String sql = SELECT_RECURSOS
                + "WHERE r.fk_categoria = ? AND r.pk_recurso NOT IN ("
                + "   SELECT rr.fk_recurso FROM reservaRecurso rr "
                + "   JOIN reserva re ON re.pk_reserva = rr.fk_reserva "
                + "   WHERE re.estado = 'ACTIVA' AND re.fecha = ? AND re.hora_init < ? AND re.hora_final > ?) "
                + "ORDER BY r.pk_recurso";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            ps.setObject(2, fecha);
            ps.setObject(3, fin);
            ps.setObject(4, inicio);
            return leer(ps);
        }
    }

    public void insertar(RecursoDTO recurso) throws SQLException {
        String sql = "INSERT INTO recurso (pk_recurso, descripcion, fk_categoria) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, recurso.getID());
            ps.setString(2, recurso.getDescripcion());
            ps.setInt(3, recurso.getCategoria().getID());
            ps.executeUpdate();
        }
    }

    public void actualizar(RecursoDTO recurso) throws SQLException {
        String sql = "UPDATE recurso SET descripcion = ?, fk_categoria = ? WHERE pk_recurso = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, recurso.getDescripcion());
            ps.setInt(2, recurso.getCategoria().getID());
            ps.setString(3, recurso.getID());
            ps.executeUpdate();
        }
    }

    // Si el recurso está asignado a alguna reserva, MySQL lanza el error 1451 (llave foránea)
    public void eliminar(String id) throws SQLException {
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM recurso WHERE pk_recurso = ?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    private List<RecursoDTO> leer(PreparedStatement ps) throws SQLException {
        List<RecursoDTO> recursos = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CategoriaRecursoDTO categoria = new CategoriaRecursoDTO(rs.getInt("pk_categoria"),
                        rs.getString("descripcion_categoria"));
                recursos.add(new RecursoDTO(rs.getString("pk_recurso"), rs.getString("descripcion"), categoria));
            }
        }
        return recursos;
    }
}
