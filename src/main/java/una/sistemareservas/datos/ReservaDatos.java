package una.sistemareservas.datos;

import una.sistemareservas.dto.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaDatos {
    private static final Path archivoReservas = Path.of("src/main/resources/datos/reservas.json");

    // Carga los datos de un archivo
    // Aqui recibo tres listas, con todos los funcionarios, categorias, y recursos existentes/disponibles
    public List<ReservaDTO> serializar(List<FuncionarioDTO> funcionariosList, List<CategoriaRecursoDTO> categoriasList, List<RecursoDTO> recursosList) throws IOException {
        // Crea una lista para las reservas sacadas del archivo
        List<ReservaDTO> reservas = new ArrayList<>();

        if (!Files.exists(archivoReservas)) {
            return reservas;
        }

        String contenido = Files.readString(archivoReservas);

        if (contenido.isBlank()) {
            return reservas;
        }

        JSONArray listaReservas = new JSONArray(contenido);

        // For que itera en el array JSON
        for (int i = 0; i < listaReservas.length(); i++) {
            // Agarro cada "objeto" reserva del json
            JSONObject reserva = listaReservas.getJSONObject(i);

            // Saco los datos de cada "objeto reserva"
            String id = reserva.getString("id");
            String actividad = reserva.getString("actividad");
            EstadoReserva estado = EstadoReserva.valueOf(reserva.getString("estado"));
            LocalDate fecha = LocalDate.parse(reserva.getString("fecha"));
            LocalTime hora_init = LocalTime.parse(reserva.getString("hora_init"));
            LocalTime hora_final = LocalTime.parse(reserva.getString("hora_final"));

            // Agarro el id del funcionario en la reserva, y lo busco en la lista de funcionarios pasada por parametro
            FuncionarioDTO funcionario = null;
            for (FuncionarioDTO f : funcionariosList) {
                if (f.getID().equals(reserva.getString("funcionarioId"))) {
                    funcionario = f;
                    break;
                }
            }

            // Misma idea que con la funcion de arriba, pero esta vez los Ids de las categorias, son una array, entonces se necesita un doble for
            List<CategoriaRecursoDTO> categorias = new ArrayList<>();
            // Array de los Ids
            JSONArray categoriaIds = reserva.getJSONArray("categoriaIds");
            // Itero en el array
            for (int j = 0; j < categoriaIds.length(); j++) {
                String categoriaId = categoriaIds.getString(j);
                CategoriaRecursoDTO categoria = null;
                // Agarro los Ids mientras itero
                // Reviso si el Id esta en la lista de categorias pasadas por parametro
                for (CategoriaRecursoDTO c : categoriasList) {
                    if (c.getID().equals(categoriaId)) {
                        categoria = c;
                        break;
                    }
                }
                // Lo añado a la lista final de las categorias
                categorias.add(categoria);
            }

            // Creo el reservaDTO con los datos obtenidos
            ReservaDTO reservaFinal = new ReservaDTO(id, funcionario, actividad, fecha, hora_init, hora_final, categorias);

            if (estado == EstadoReserva.CANCELADA) {
                reservaFinal.cambiarEstado();
            }

            //Misma situacion con las categorias
            JSONArray recursoIds = reserva.getJSONArray("recursoIds");
            for (int j = 0; j < recursoIds.length(); j++) {
                String recursoId = recursoIds.getString(j);
                RecursoDTO recurso = null;
                for (RecursoDTO r : recursosList) {
                    if (r.getID().equals(recursoId)) {
                        recurso = r;
                        break;
                    }
                }
                // Si el id del recurso existe en la lista pasada por parametro, lo añado a la reserva
                reservaFinal.asignarRecurso(recurso);
            }

            // Añado el objeto reserva a la listaFinal
            reservas.add(reservaFinal);
        }

        // Retorno la lista con todos los reservasDTO
        return reservas;
    }

    // Funcion para cargar datos a un archivo
    // Recibe una lista de RecursoDTO
    public void deserializar(List<ReservaDTO> reservasList) throws IOException {
        JSONArray listaReservas = new JSONArray();

        // Itero en la lista de recursos
        for (ReservaDTO reserva : reservasList) {
            // Creo un nuevo objeto JSON, y le añado los datos necesarios
            JSONObject objetoReserva = new JSONObject();
            objetoReserva.put("id", reserva.getID());
            objetoReserva.put("funcionarioId", reserva.getFuncionario().getID());
            objetoReserva.put("actividad", reserva.getActividad());
            objetoReserva.put("estado", reserva.getEstado().name());
            objetoReserva.put("fecha", reserva.getFecha().toString());
            objetoReserva.put("hora_init", reserva.getHora_init().toString());
            objetoReserva.put("hora_final", reserva.getHora_final().toString());

            // Creo un array con todos los Ids de las categorias
            JSONArray categoriaIds = new JSONArray();
            for (CategoriaRecursoDTO categoria : reserva.getCategorias()) {
                // Meto cada categoria por individual en el array
                categoriaIds.put(categoria.getID());
            }
            // Meto el array de categorias a la reserva
            objetoReserva.put("categoriaIds", categoriaIds);

            // Lo mismo de arriba pero con recursos
            JSONArray recursoIds = new JSONArray();
            for (RecursoDTO recurso : reserva.getRecursos()) {
                recursoIds.put(recurso.getID());
            }
            objetoReserva.put("recursoIds", recursoIds);

            // Añado la reserva a la lista final
            listaReservas.put(objetoReserva);
        }

        // Sobreescribo el archivo de las reservas con las nuevas
        Files.createDirectories(archivoReservas.getParent());
        Files.writeString(archivoReservas, listaReservas.toString(2));
    }
}
