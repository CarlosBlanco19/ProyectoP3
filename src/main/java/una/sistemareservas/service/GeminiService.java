package una.sistemareservas.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiService {
    private static final String MODELO = "gemini-3.6-flash";
    private static final String ENDPOINT_BASE = "https://generativelanguage.googleapis.com/v1beta/models/";

    private final String apiKey;
    private final HttpClient httpClient;

    public GeminiService() {
        this.apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "No se encontró la variable de entorno GEMINI_API_KEY. " +
                            "Configúrela antes de ejecutar la aplicación (no escriba la clave en el código).");
        }
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Envía un mensaje de texto a Gemini y retorna la respuesta como texto plano.
     *
     * @param textoUsuario el prompt / mensaje del usuario
     * @return el texto de la respuesta generada por el modelo
     * @throws IOException          si falla la comunicación de red o la API responde con error
     * @throws InterruptedException si el hilo es interrumpido mientras espera la respuesta
     */
    // Cambiado para obligar al LLM a devolver un JSON
    public String enviarMensaje(String textoUsuario) throws IOException, InterruptedException {
        String url = ENDPOINT_BASE + MODELO + ":generateContent";

        JSONObject parte = new JSONObject().put("text", textoUsuario);
        JSONObject contenido = new JSONObject().put("parts", new JSONArray().put(parte));
        JSONObject generationConfig = new JSONObject().put("responseMimeType", "application/json");
        JSONObject cuerpo = new JSONObject().put("contents", new JSONArray().put(contenido)).put("generationConfig", generationConfig);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Error de la API de Gemini (HTTP " + response.statusCode() + "): " + response.body());
        }

        return extraerTexto(response.body());
    }

    private String extraerTexto(String jsonRespuesta) {
        JSONObject raiz = new JSONObject(jsonRespuesta);
        JSONArray candidatos = raiz.getJSONArray("candidates");
        JSONObject primerCandidato = candidatos.getJSONObject(0);
        JSONObject contenido = primerCandidato.getJSONObject("content");
        JSONArray partes = contenido.getJSONArray("parts");
        return partes.getJSONObject(0).getString("text");
    }
}
