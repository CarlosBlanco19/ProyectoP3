package una.sistemareservas.datos;

import una.sistemareservas.dto.CategoriaRecursoDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import una.sistemareservas.utilidades.Busqueda;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaRecursoDatos {


    public List<CategoriaRecursoDTO> listar() throws SQLException {

        List<CategoriaRecursoDTO> categorias = new ArrayList<>();

        String sql = "SELECT pk_categoria, " +
                "descripcion FROM categoriaRecursos ORDER BY pk_categoria";


        // El try cierra solo la conexion statement y resultado al terminar
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet resSet = ps.executeQuery()) {

            while (resSet.next()) {
                int id = resSet.getInt("pk_categoria");
                String descripcion = resSet.getString("descripcion");
                categorias.add(new CategoriaRecursoDTO(id, descripcion));
            }
        }
        return categorias;
    }

    // Inserta una categoria y devuelve el id generado
    public int insertar(String descripcion) throws SQLException {
        String sql = "INSERT INTO categoriaRecursos (descripcion) VALUES (?)";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, descripcion); // reemplaza el ? por la descripcion
            ps.executeUpdate();

            ResultSet claves = ps.getGeneratedKeys(); // aqui viene el id generado
            claves.next();
            return claves.getInt(1);
        }
    }

    // Cambia la descripcion de una categoria
    public void actualizar(int id, String descripcion) throws SQLException {
        String sql = "UPDATE categoriaRecursos SET descripcion = ? WHERE pk_categoria = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, descripcion);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    // Borra una categoria
    // Si se esta usando se lanza una exception
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM categoriaRecursos WHERE pk_categoria = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }



}
