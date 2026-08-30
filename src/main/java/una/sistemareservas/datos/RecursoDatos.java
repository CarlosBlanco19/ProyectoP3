package una.sistemareservas.datos;

import org.json.JSONArray;
import org.json.JSONObject;
import una.sistemareservas.model.CategoriaRecursoDTO;
import una.sistemareservas.model.RecursoDTO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RecursoDatos {
    private static final Path archivoRecursos = Path.of("src/main/resources/datos/recursos.json");

    public List<RecursoDTO> serializar(List<CategoriaRecursoDTO> categorias) throws IOException {
        List<RecursoDTO> recursos = new ArrayList<>();

        if (!Files.exists(archivoRecursos)) {
            return recursos;
        }

        String contenido = Files.readString(archivoRecursos);

        JSONArray listaRecursos = new JSONArray(contenido);

        for (int i = 0; i < listaRecursos.length(); i++) {
            JSONObject objeto = new JSONObject(listaRecursos.getJSONObject(i));

            String id = objeto.getString("id");
            String descripcion = objeto.getString("descripcion");
            String categoriaId = objeto.getString("categoriaId");

            CategoriaRecursoDTO categoria = null;

            for (CategoriaRecursoDTO c : categorias) {
                if (c.getID() == categoriaId) {
                    categoria = c;
                    break;
                }
            }

            recursos.add(new RecursoDTO(id, descripcion, categoria));
        }

        return recursos;
    }

    public void deserializar(List<RecursoDTO> recursos) throws IOException {
        JSONArray listaRecursos = new JSONArray();

        for (RecursoDTO recurso : recursos) {
            JSONObject item = new JSONObject();
            item.put("id", recurso.getID());
            item.put("descripcion", recurso.getDescripcion());
            item.put("IDcategoria", recurso.getCategoria().getID());
            listaRecursos.put(item);
        }

        Files.createDirectories(archivoRecursos.getParent());
        Files.writeString(archivoRecursos, listaRecursos.toString(2));
    }
}

