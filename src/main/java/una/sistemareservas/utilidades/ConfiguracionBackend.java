package una.sistemareservas.utilidades;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public final class ConfiguracionBackend {

    private static final Properties propiedades = new Properties();

    static {
        try (InputStream entrada = ConfiguracionBackend.class.getResourceAsStream("/backend.properties")) {
            if (entrada != null) {
                propiedades.load(entrada);
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer backend.properties: " + e.getMessage());
        }
    }

    private ConfiguracionBackend() {
    }

    public static String get(String clave, String porDefecto) {
        return System.getProperty(clave, propiedades.getProperty(clave, porDefecto));
    }

    public static int getInt(String clave, int porDefecto) {
        try {
            return Integer.parseInt(get(clave, String.valueOf(porDefecto)).trim());
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}
