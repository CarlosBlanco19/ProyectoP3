package una.sistemareservas.protocolo;

public final class Protocolo {

    private Protocolo() {
    }

    public static final String SERVIDOR = "localhost";
    public static final int PUERTO = 12345;

    public static final int SYNC = 1;
    public static final int ASYNC = 2;

    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_ERROR = 1;

    public static final int LOGIN = 100;
    public static final int LOGOUT = 101;
    public static final int CAMBIAR_CLAVE = 102;

    public static final int FUNCIONARIO_LISTAR = 200;
    public static final int FUNCIONARIO_BUSCAR = 201;
    public static final int FUNCIONARIO_BUSCAR_NOMBRE = 202;
    public static final int FUNCIONARIO_AGREGAR = 203;
    public static final int FUNCIONARIO_ACTUALIZAR = 204;
    public static final int FUNCIONARIO_ELIMINAR = 205;

    public static final int CATEGORIA_LISTAR = 300;
    public static final int CATEGORIA_BUSCAR = 301;
    public static final int CATEGORIA_AGREGAR = 302;
    public static final int CATEGORIA_ACTUALIZAR = 303;
    public static final int CATEGORIA_ELIMINAR = 304;

    public static final int RECURSO_LISTAR = 400;
    public static final int RECURSO_LISTAR_CATEGORIA = 401;
    public static final int RECURSO_BUSCAR = 402;
    public static final int RECURSO_AGREGAR = 403;
    public static final int RECURSO_ACTUALIZAR = 404;
    public static final int RECURSO_ELIMINAR = 405;

    public static final int RESERVA_RESERVAR = 500;
    public static final int RESERVA_CANCELAR = 501;
    public static final int RESERVA_LISTAR_FUNCIONARIO = 502;
    public static final int RESERVA_LISTAR = 503;

    public static final int NOTIFICACION_RESERVA = 900;
}
