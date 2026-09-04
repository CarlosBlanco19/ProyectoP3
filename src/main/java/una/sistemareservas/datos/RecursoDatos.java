package una.sistemareservas.datos;

import org.json.JSONArray;
import org.json.JSONObject;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.RecursoDTO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RecursoDatos {
    // Archivo de los recursos
    private static final Path archivoRecursos = Path.of("src/main/resources/datos/recursos.json");

    // Carga los datos de un archivo
    // Recibo la lista con todas las categorias existentes de parametro
    public List<RecursoDTO> serializar(List<CategoriaRecursoDTO> categorias) throws IOException {
        // Crea una lista para los recursos sacados del archivo
        List<RecursoDTO> recursos = new ArrayList<>();

        // Si el archivo no existe devuelvo la lista vacia
        if (!Files.exists(archivoRecursos)) {
            return recursos;
        }

        // Leo el archivo
        String contenido = Files.readString(archivoRecursos);

        if (contenido.isBlank()) {
            return recursos;
        }

        // Array JSON para manejar el contenido
        JSONArray listaRecursos = new JSONArray(contenido);

        // For que itera en el array JSON
        for (int i = 0; i < listaRecursos.length(); i++) {
            // Obtengo un "objeto" json de la lista
            JSONObject objeto = listaRecursos.getJSONObject(i);

            // Saco los datos de cada "objeto recurso"
            String id = objeto.getString("id");
            String descripcion = objeto.getString("descripcion");
            String categoriaId = objeto.getString("categoriaId");

            // Reviso si el ID de la categoria esta en la lista de todas las categorias
            CategoriaRecursoDTO categoria = null;
            for (CategoriaRecursoDTO c : categorias) {
                if (c.getID().equals(categoriaId)) {
                    categoria = c;
                    break;
                }
            }

            // Construyo el RecursoDTO y lo añado a la lista final
            recursos.add(new RecursoDTO(id, descripcion, categoria));
        }

        // Devuelvo la lista de recursos
        return recursos;
    }

    // Funcion para cargar datos a un archivo
    // Recibe una lista de RecursoDTO
    public void deserializar(List<RecursoDTO> recursos) throws IOException {
        // Array en JSON para guardar en el archivo
        JSONArray listaRecursos = new JSONArray();

        // Itero en la lista de recursos
        for (RecursoDTO recurso : recursos) {
            // Creo un nuevo objeto JSON, y le añado los datos necesarios
            JSONObject item = new JSONObject();
            item.put("id", recurso.getID());
            item.put("descripcion", recurso.getDescripcion());
            item.put("categoriaId", recurso.getCategoria().getID());
            // Inserto el "objeto" completo en la lista final, eso para cada RecursoDTO de la lista a guardar
            listaRecursos.put(item);
        }

        // Por si no existen el archivo y/o las carpetas, los crea
        Files.createDirectories(archivoRecursos.getParent());

        // Sobrescribe el archivo con los datos de la lista pasada por parametro
        Files.writeString(archivoRecursos, listaRecursos.toString(2));
    }
}

