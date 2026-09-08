//package una.sistemareservas.logic;
//
//import org.json.JSONObject;
//import una.sistemareservas.dto.CategoriaRecursoDTO;
//import una.sistemareservas.service.CategoriaService;
//
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//
//public class GeminiLogic {
//
//    private static final String MODELO = "gemini-3.6-flash";
//    private static final String ENDPOINT_BASE = "https://generativelanguage.googleapis.com/v1beta/models/";
//
//
//    private final String apiKey;
//    private final HttpClient httpCliente;
//
//    public GeminiLogic(){
//        this.apiKey = System.getenv("GEMINI_API_KEY");
//
//        if(apiKey == null || apiKey.isBlank()){
//            //tirar excepcion personalizada
//            // throw new IllegalStateException("No se encontro la variable de entorno GEMINI_API_KEY.");
//        }this.httpCliente = HttpClient.newHttpClient();
//
//    }
//
//    private String enviarMsg(String txtUsuario){
//        String url = ENDPOINT_BASE + MODELO + ":generateContent";
//
//        JSONObject parte = new JSONObject().put("text", txtUsuario);
//        JSONObject contenido = new JSONObject().put("parts", new org.json.JSONArray().put(parte));
//        JSONObject cuerpo = new JSONObject().put("contents", new org.json.JSONArray().put(contenido));
//
//
//        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type","application/json").header("x-goog-api-key",apiKey).POST(HttpRequest.BodyPublishers.ofString(cuerpo.toString())).build();
//
//
//        //try
//        HttpResponse<String> response = httpCliente.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if(response.statusCode() != 200) {
//           // throw new IOException("Error de la API (HTTP " + response.statusCode() + "): " + response.body());
//        }
//        return response.body();
//    }//catch
//
//
//    private String Prompt(){
//        StringBuilder categoriasDisponibles = new StringBuilder();
//
//        for(CategoriaRecursoDTO categoria : CategoriaLogic.)
//    }


//}
