package una.sistemareservas.exception;

public class RecursoException extends Exception {
    public RecursoException(String mensaje) {
        super(mensaje);
    }

    public RecursoException(String mensaje, Throwable error) {
        super(mensaje, error);
    }
}
