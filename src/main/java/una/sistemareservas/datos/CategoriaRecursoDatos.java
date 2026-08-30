package una.sistemareservas.datos;

import una.sistemareservas.model.CategoriaRecursoDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CategoriaRecursoDatos {
    private static final Path archivoCategoriasR = Path.of("src/main/resources/datos/categorias.json");

    public List<CategoriaRecursoDTO> serializar() throws IOException {
        List<CategoriaRecursoDTO> categorias = new ArrayList<>();

        if (!Files.exists(archivoCategoriasR)) {
            return categorias;
        }

        String contenido = Files.readString(archivoCategoriasR);

        JSONArray listaCategoriasR = new JSONArray(contenido);

        for (int i = 0; i < listaCategoriasR.length(); i++) {
            categorias.add(new CategoriaRecursoDTO(listaCategoriasR.getJSONObject(i).getString("id"), listaCategoriasR.getJSONObject(i).getString("descripcion")));
        }

        return categorias;
    }

    public void deserializar(List<CategoriaRecursoDTO> categoriasR) throws IOException {
        JSONArray listaCategoriasR = new JSONArray();

        for (CategoriaRecursoDTO categoriaR : categoriasR) {
            JSONObject objeto = new JSONObject();
            objeto.put("id", categoriaR.getID());
            objeto.put("descripcion", categoriaR.getDescripcion());
            listaCategoriasR.put(objeto);
        }

        Files.createDirectories(archivoCategoriasR.getParent());
        Files.writeString(archivoCategoriasR, listaCategoriasR.toString(2));
    }
}
