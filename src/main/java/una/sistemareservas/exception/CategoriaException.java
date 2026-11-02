package una.sistemareservas.exception;

public class CategoriaException extends Exception {
    public CategoriaException(String mensaje) {
        super(mensaje);
    }

    public CategoriaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
