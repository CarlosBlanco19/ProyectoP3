package una.sistemareservas.exception;

public class CategoriaException extends RuntimeException {
    public CategoriaException(String message) {
        super(message);
    }

    public CategoriaException(String msg, Throwable e){
        super(msg, e);
    }
}
