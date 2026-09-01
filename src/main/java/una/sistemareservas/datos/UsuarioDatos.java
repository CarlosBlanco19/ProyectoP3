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

        JSONArray listaUsuarios = new JSONArray(contenido);

        for (int i = 0; i < listaUsuarios.length(); i++) {
            JSONObject objeto = new JSONObject(listaUsuarios.getJSONObject(i));

            String id = objeto.getString("id");
            Rol rol = Rol.valueOf(objeto.getString("rol"));
            String clave = objeto.getString("clave");

            if (rol == Rol.ADMINISTRADOR) {
                usuarios.add(new AdministradorDTO(id, clave));
            }
            else {
                String nombre = objeto.getString("nombre");
                String telefono = objeto.getString("telefono");
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
