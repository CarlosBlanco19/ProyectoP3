package una.sistemareservas.exception;

public class UsuarioException extends Exception {
    public UsuarioException(String mensaje) {
        super(mensaje);
    }

    public UsuarioException(String mensaje, Throwable error) {
        super(mensaje, error);
    }
}
