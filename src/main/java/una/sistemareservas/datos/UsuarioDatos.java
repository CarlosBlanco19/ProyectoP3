package una.sistemareservas.datos;

import org.json.JSONArray;
import org.json.JSONObject;
import una.sistemareservas.model.UsuarioDTO;
import una.sistemareservas.model.AdministradorDTO;
import una.sistemareservas.model.FuncionarioDTO;
import una.sistemareservas.model.Rol;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDatos {
    private static final Path archivoUsuarios = Path.of("src/main/resources/datos/usuarios.json");

    public List<UsuarioDTO> serializar() throws IOException {
        List<UsuarioDTO> usuarios = new ArrayList<>();

        if (!Files.exists(archivoUsuarios)) {
            return usuarios;
        }

        String contenido = Files.readString(archivoUsuarios);

        if (contenido.isBlank()) {
            return usuarios;
        }

        JSONArray listaUsuarios = new JSONArray(contenido);

        for (int i = 0; i < listaUsuarios.length(); i++) {
            JSONObject usuario = listaUsuarios.getJSONObject(i);
            String id = usuario.getString("id");
            Rol rol = Rol.valueOf(usuario.getString("rol"));
            String clave = usuario.getString("clave");

            if (rol == Rol.ADMINISTRADOR) {
                usuarios.add(new AdministradorDTO(id, clave));
            }
            else {
                String nombre = usuario.getString("nombre");
                String telefono = usuario.getString("telefono");
                if (clave.length() != 0) {
                    usuarios.add(new FuncionarioDTO(id, clave, nombre, telefono));
                }
                else  {
                    usuarios.add(new FuncionarioDTO(id, nombre, telefono));
                }
            }

        }

        return usuarios;
    }

    public void deserializar(List<UsuarioDTO> usuarios) throws IOException {
        JSONArray listaUsuarios = new JSONArray();

        for (UsuarioDTO usuario : usuarios) {
            JSONObject objeto = new JSONObject();
            objeto.put("id", usuario.getID());
            objeto.put("clave", usuario.getClave());
            objeto.put("rol", usuario.getRol().name());

            if (usuario instanceof FuncionarioDTO) {
                FuncionarioDTO fun = (FuncionarioDTO) usuario;
                objeto.put("nombre", fun.getNombre());
                objeto.put("telefono", fun.getTelefono());
            }

            listaUsuarios.put(objeto);
        }

        Files.createDirectories(archivoUsuarios.getParent());
        Files.writeString(archivoUsuarios, listaUsuarios.toString(2));
    }
}
