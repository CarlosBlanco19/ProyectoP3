package una.sistemareservas.exception;

// Error de validación o de datos relacionado con usuarios.
// El mensaje es el que se le muestra al usuario en el Frontend.
public class UsuarioException extends Exception {
    public UsuarioException(String mensaje) {
        super(mensaje);
    }

    public UsuarioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
