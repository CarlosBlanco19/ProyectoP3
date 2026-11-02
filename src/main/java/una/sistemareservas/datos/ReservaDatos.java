package una.sistemareservas.datos;

import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.EstadoReserva;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Acceso a las tablas reserva, reservaCategoria y reservaRecurso
public class ReservaDatos {

    private static final String SELECT_RESERVAS =
            "SELECT re.pk_reserva, re.actividad, re.fecha, re.hora_init, re.hora_final, re.estado, "
            + "f.fk_usuario, f.nombre, f.telefono "
            + "FROM reserva re JOIN funcionario f ON f.fk_usuario = re.fk_funcionario ";

    public List<ReservaDTO> listar() throws SQLException {
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_RESERVAS + "ORDER BY re.fecha, re.hora_init")) {
            return leer(con, ps);
        }
    }

    public List<ReservaDTO> listarPorFuncionario(String idFuncionario) throws SQLException {
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_RESERVAS
                     + "WHERE re.fk_funcionario = ? ORDER BY re.fecha, re.hora_init")) {
            ps.setString(1, idFuncionario);
            return leer(con, ps);
        }
    }

    public ReservaDTO buscarPorId(String id) throws SQLException {
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_RESERVAS + "WHERE re.pk_reserva = ?")) {
            ps.setString(1, id);
            List<ReservaDTO> lista = leer(con, ps);
            return lista.isEmpty() ? null : lista.get(0);
        }
    }

    // El ID más alto que existe (por ejemplo "RES-000008"), o null si no hay reservas.
    // Como todos tienen el mismo formato con ceros a la izquierda, el orden de texto sirve.
    public String ultimoId() throws SQLException {
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT pk_reserva FROM reserva WHERE pk_reserva LIKE 'RES-%' ORDER BY pk_reserva DESC LIMIT 1");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString(1) : null;
        }
    }

    // Guarda la reserva, sus categorías y sus recursos en una TRANSACCIÓN (todo o nada)
    public void insertar(ReservaDTO reserva) throws SQLException {
        try (Connection con = ConexionDB.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psReserva = con.prepareStatement(
                         "INSERT INTO reserva (pk_reserva, fk_funcionario, actividad, fecha, hora_init, hora_final, estado) "
                         + "VALUES (?, ?, ?, ?, ?, ?, ?)");
                 PreparedStatement psCategoria = con.prepareStatement(
                         "INSERT INTO reservaCategoria (fk_reserva, fk_categoria) VALUES (?, ?)");
                 PreparedStatement psRecurso = con.prepareStatement(
                         "INSERT INTO reservaRecurso (fk_reserva, fk_recurso) VALUES (?, ?)")) {

                psReserva.setString(1, reserva.getID());
                psReserva.setString(2, reserva.getFuncionario().getID());
                psReserva.setString(3, reserva.getActividad());
                psReserva.setObject(4, reserva.getFecha());
                psReserva.setObject(5, reserva.getHora_init());
                psReserva.setObject(6, reserva.getHora_final());
                psReserva.setString(7, reserva.getEstado().name());
                psReserva.executeUpdate();

                for (CategoriaRecursoDTO categoria : reserva.getCategorias()) {
                    psCategoria.setString(1, reserva.getID());
                    psCategoria.setInt(2, categoria.getID());
                    psCategoria.executeUpdate();
                }
                for (RecursoDTO recurso : reserva.getRecursos()) {
                    psRecurso.setString(1, reserva.getID());
                    psRecurso.setString(2, recurso.getID());
                    psRecurso.executeUpdate();
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // Cancelar = cambiar el estado. Los recursos quedan libres porque la búsqueda
    // de disponibles solo toma en cuenta reservas ACTIVAS.
    public void cancelar(String idReserva) throws SQLException {
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE reserva SET estado = 'CANCELADA' WHERE pk_reserva = ?")) {
            ps.setString(1, idReserva);
            ps.executeUpdate();
        }
    }

    // Lee las reservas del ResultSet y les carga sus categorías y recursos
    private List<ReservaDTO> leer(Connection con, PreparedStatement ps) throws SQLException {
        List<ReservaDTO> reservas = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                FuncionarioDTO funcionario = new FuncionarioDTO(rs.getString("fk_usuario"),
                        rs.getString("nombre"), rs.getString("telefono"));
                ReservaDTO reserva = new ReservaDTO(
                        rs.getString("pk_reserva"),
                        funcionario,
                        rs.getString("actividad"),
                        rs.getObject("fecha", LocalDate.class),
                        rs.getObject("hora_init", LocalTime.class),
                        rs.getObject("hora_final", LocalTime.class),
                        new ArrayList<>());
                reserva.setEstado(EstadoReserva.valueOf(rs.getString("estado")));
                reservas.add(reserva);
            }
        }

        if (reservas.isEmpty()) {
            return reservas;
        }

        String sqlCategorias = "SELECT c.pk_categoria, c.descripcion FROM reservaCategoria rc "
                + "JOIN categoriaRecursos c ON c.pk_categoria = rc.fk_categoria "
                + "WHERE rc.fk_reserva = ? ORDER BY c.pk_categoria";
        String sqlRecursos = "SELECT r.pk_recurso, r.descripcion, c.pk_categoria, c.descripcion AS descripcion_categoria "
                + "FROM reservaRecurso rr JOIN recurso r ON r.pk_recurso = rr.fk_recurso "
                + "JOIN categoriaRecursos c ON c.pk_categoria = r.fk_categoria "
                + "WHERE rr.fk_reserva = ? ORDER BY c.pk_categoria";

        List<ReservaDTO> completas = new ArrayList<>();
        try (PreparedStatement psCategorias = con.prepareStatement(sqlCategorias);
             PreparedStatement psRecursos = con.prepareStatement(sqlRecursos)) {
            for (ReservaDTO reserva : reservas) {
                List<CategoriaRecursoDTO> categorias = new ArrayList<>();
                psCategorias.setString(1, reserva.getID());
                try (ResultSet rs = psCategorias.executeQuery()) {
                    while (rs.next()) {
                        categorias.add(new CategoriaRecursoDTO(rs.getInt("pk_categoria"), rs.getString("descripcion")));
                    }
                }

                // Se vuelve a crear la reserva con sus categorías (la lista de categorías se pasa en el constructor)
                ReservaDTO completa = new ReservaDTO(reserva.getID(), reserva.getFuncionario(), reserva.getActividad(),
                        reserva.getFecha(), reserva.getHora_init(), reserva.getHora_final(), categorias);
                completa.setEstado(reserva.getEstado());

                psRecursos.setString(1, reserva.getID());
                try (ResultSet rs = psRecursos.executeQuery()) {
                    while (rs.next()) {
                        CategoriaRecursoDTO categoria = new CategoriaRecursoDTO(rs.getInt("pk_categoria"),
                                rs.getString("descripcion_categoria"));
                        completa.asignarRecurso(new RecursoDTO(rs.getString("pk_recurso"), rs.getString("descripcion"), categoria));
                    }
                }
                completas.add(completa);
            }
        }
        return completas;
    }
}
