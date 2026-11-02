package una.sistemareservas.exception;

// Error de validación o de datos relacionado con recursos.
// El mensaje es el que se le muestra al usuario en el Frontend.
public class RecursoException extends Exception {
    public RecursoException(String mensaje) {
        super(mensaje);
    }

    public RecursoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
