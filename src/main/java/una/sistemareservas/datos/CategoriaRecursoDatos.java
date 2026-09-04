package una.sistemareservas.datos;

import una.sistemareservas.dto.CategoriaRecursoDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CategoriaRecursoDatos {
    // Archivo de las categorias de recursos
    private static final Path archivoCategoriasR = Path.of("src/main/resources/datos/categorias.json");

    // Carga los datos de un archivo
    public List<CategoriaRecursoDTO> serializar() throws IOException {
        // Crea una lista para las categorias sacadas del archivo
        List<CategoriaRecursoDTO> categorias = new ArrayList<>();

        // Si el archivo no existe devuelvo la lista vacia
        if (!Files.exists(archivoCategoriasR)) {
            return categorias;
        }

        // Leo el archivo
        String contenido = Files.readString(archivoCategoriasR);

        if (contenido.isBlank()) {
            return categorias;
        }

        // Array JSON para manejar el contenido
        JSONArray listaCategoriasR = new JSONArray(contenido);

        // For que itera en el array JSON, y obtiene los datos para construir un DTO de categoria recurso y añadirlo a la lista
        for (int i = 0; i < listaCategoriasR.length(); i++) {
            categorias.add(new CategoriaRecursoDTO(listaCategoriasR.getJSONObject(i).getString("id"), listaCategoriasR.getJSONObject(i).getString("descripcion")));
        }

        // Devuelve la lista
        return categorias;
    }

    // Funcion para cargar datos a un archivo
    // Recibe una lista de CategoriasRecursoDTO
    public void deserializar(List<CategoriaRecursoDTO> categoriasR) throws IOException {
        // Array en JSON para guardar en el archivo
        JSONArray listaCategoriasR = new JSONArray();

        // Itero en la lista de las categorias
        for (CategoriaRecursoDTO categoriaR : categoriasR) {
            // Creo un nuevo objeto JSON, y le añado los datos necesarios
            JSONObject objeto = new JSONObject();
            objeto.put("id", categoriaR.getID());
            objeto.put("descripcion", categoriaR.getDescripcion());
            // Inserto el "objeto" completo en la lista, eso para cada categoriaRecursoDTO de la lista
            listaCategoriasR.put(objeto);
        }

        // Por si no existen el archivo y/o las carpetas, los crea
        Files.createDirectories(archivoCategoriasR.getParent());

        // Sobrescribe el archivo con los datos de la lista pasada por parametro
        Files.writeString(archivoCategoriasR, listaCategoriasR.toString(2));
    }
}
