package una.sistemareservas.servidor;

import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.dto.Rol;
import una.sistemareservas.dto.UsuarioDTO;
import una.sistemareservas.protocolo.IService;
import una.sistemareservas.protocolo.Protocolo;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


public class Worker extends Thread {

    private final Servidor servidor;
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final IService service;
    private final int sesion;

    private Socket socketAsincrono;
    private ObjectOutputStream outAsincrono;
    private final Object candadoAsincrono = new Object();

    private volatile UsuarioDTO usuario;
    private volatile boolean activo = true;
    private final AtomicBoolean cerrado = new AtomicBoolean(false);

    public Worker(Servidor servidor, Socket socket, ObjectInputStream in, ObjectOutputStream out,
                  IService service, int sesion) {
        super("worker-" + sesion);
        this.servidor = servidor;
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.service = service;
        this.sesion = sesion;
        setDaemon(true);
    }

    public int getSesion() {
        return sesion;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setCanalAsincrono(Socket socket, ObjectOutputStream out) {
        synchronized (candadoAsincrono) {
            this.socketAsincrono = socket;
            this.outAsincrono = out;
        }
    }

    @Override
    public void run() {
        try {
            while (activo) {
                int operacion = in.readInt();
                Object[] parametros = (Object[]) in.readObject();

                int codigo;
                Object respuesta;
                try {
                    respuesta = atender(operacion, parametros);
                    codigo = Protocolo.ERROR_NO_ERROR;
                } catch (Exception e) {
                    codigo = Protocolo.ERROR_ERROR;
                    respuesta = mensajeDeError(e);
                }

                out.writeInt(codigo);
                out.writeObject(respuesta);
                out.flush();
                out.reset(); // Para que la próxima vez se envíen los objetos nuevos y no copias guardadas

                // Si se creó una reserva, se les avisa a los demás Frontend
                if (operacion == Protocolo.RESERVA_RESERVAR && codigo == Protocolo.ERROR_NO_ERROR) {
                    servidor.notificarReserva((ReservaDTO) respuesta, this);
                }
            }
        } catch (EOFException | SocketException e) {
            // El Frontend se cerró: es lo normal
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("[Worker " + sesion + "] Error de comunicación: " + e.getMessage());
        } finally {
            cerrar();
        }
    }

    /** Ejecuta la operación pedida y devuelve el resultado (o null si no devuelve nada). */
    private Object atender(int operacion, Object[] p) throws Exception {
        switch (operacion) {
            // ---------------- Usuarios ----------------
            case Protocolo.LOGIN: {
                UsuarioDTO usuarioValido = service.login((String) p[0], (String) p[1]);
                usuario = usuarioValido;
                System.out.println("[Worker " + sesion + "] Inició sesión: " + usuarioValido.getID());
                return usuarioValido;
            }
            case Protocolo.LOGOUT:
                usuario = null;
                return null;
            case Protocolo.CAMBIAR_CLAVE:
                service.cambiarClave((String) p[0], (String) p[1], (String) p[2]);
                return null;

            // ---------------- Funcionarios ----------------
            case Protocolo.FUNCIONARIO_LISTAR:
                requiereSesion();
                return new ArrayList<>(service.listarFuncionarios());
            case Protocolo.FUNCIONARIO_BUSCAR:
                requiereSesion();
                return service.buscarFuncionario((String) p[0]);
            case Protocolo.FUNCIONARIO_BUSCAR_NOMBRE:
                requiereSesion();
                return new ArrayList<>(service.buscarFuncionariosPorNombre((String) p[0]));
            case Protocolo.FUNCIONARIO_AGREGAR:
                requiereAdministrador();
                service.agregarFuncionario((FuncionarioDTO) p[0]);
                return null;
            case Protocolo.FUNCIONARIO_ACTUALIZAR:
                requiereAdministrador();
                service.actualizarFuncionario((FuncionarioDTO) p[0]);
                return null;
            case Protocolo.FUNCIONARIO_ELIMINAR:
                requiereAdministrador();
                service.eliminarFuncionario((String) p[0]);
                return null;

            // ---------------- Categorías ----------------
            case Protocolo.CATEGORIA_LISTAR:
                requiereSesion();
                return new ArrayList<>(service.listarCategorias());
            case Protocolo.CATEGORIA_BUSCAR:
                requiereSesion();
                return new ArrayList<>(service.buscarCategorias((String) p[0]));
            case Protocolo.CATEGORIA_AGREGAR:
                requiereAdministrador();
                return service.agregarCategoria((String) p[0]);
            case Protocolo.CATEGORIA_ACTUALIZAR:
                requiereAdministrador();
                service.actualizarCategoria((Integer) p[0], (String) p[1]);
                return null;
            case Protocolo.CATEGORIA_ELIMINAR:
                requiereAdministrador();
                service.eliminarCategoria((Integer) p[0]);
                return null;

            // ---------------- Recursos ----------------
            case Protocolo.RECURSO_LISTAR:
                requiereSesion();
                return new ArrayList<>(service.listarRecursos());
            case Protocolo.RECURSO_LISTAR_CATEGORIA:
                requiereSesion();
                return new ArrayList<>(service.listarRecursosPorCategoria((Integer) p[0]));
            case Protocolo.RECURSO_BUSCAR:
                requiereSesion();
                return service.buscarRecurso((String) p[0]);
            case Protocolo.RECURSO_AGREGAR:
                requiereAdministrador();
                service.agregarRecurso((RecursoDTO) p[0]);
                return null;
            case Protocolo.RECURSO_ACTUALIZAR:
                requiereAdministrador();
                service.actualizarRecurso((RecursoDTO) p[0]);
                return null;
            case Protocolo.RECURSO_ELIMINAR:
                requiereAdministrador();
                service.eliminarRecurso((String) p[0]);
                return null;

            // ---------------- Reservas ----------------
            case Protocolo.RESERVA_RESERVAR: {
                FuncionarioDTO funcionario = requiereFuncionario();
                ReservaDTO solicitud = (ReservaDTO) p[0];
                if (solicitud != null) {
                    // La reserva SIEMPRE queda a nombre de quien tiene la sesión abierta
                    solicitud.setFuncionario(funcionario);
                }
                return service.reservar(solicitud);
            }
            case Protocolo.RESERVA_CANCELAR: {
                FuncionarioDTO funcionario = requiereFuncionario();
                service.cancelarReserva((String) p[0], funcionario.getID());
                return null;
            }
            case Protocolo.RESERVA_LISTAR_FUNCIONARIO: {
                UsuarioDTO actual = requiereSesion();
                String idPedido = (String) p[0];
                if (actual.getRol() == Rol.FUNCIONARIO && !actual.getID().equals(idPedido)) {
                    throw new Exception("Solo puede consultar sus propias reservas");
                }
                return new ArrayList<>(service.listarReservasPorFuncionario(idPedido));
            }
            case Protocolo.RESERVA_LISTAR:
                requiereSesion();
                return new ArrayList<>(service.listarReservas());

            default:
                throw new Exception("Operación desconocida: " + operacion);
        }
    }

    // ---------------- Permisos ----------------

    private UsuarioDTO requiereSesion() throws Exception {
        UsuarioDTO actual = usuario;
        if (actual == null) {
            throw new Exception("Debe iniciar sesión");
        }
        return actual;
    }

    private void requiereAdministrador() throws Exception {
        if (requiereSesion().getRol() != Rol.ADMINISTRADOR) {
            throw new Exception("Solo el administrador puede hacer esta acción");
        }
    }

    private FuncionarioDTO requiereFuncionario() throws Exception {
        UsuarioDTO actual = requiereSesion();
        if (!(actual instanceof FuncionarioDTO funcionario)) {
            throw new Exception("Solo los funcionarios pueden hacer reservas");
        }
        return funcionario;
    }

    private String mensajeDeError(Exception e) {
        if (e instanceof ClassCastException || e instanceof ArrayIndexOutOfBoundsException) {
            return "La petición no tiene el formato esperado";
        }
        if (e.getMessage() == null || e.getMessage().isBlank()) {
            System.err.println("[Worker " + sesion + "] Error inesperado: " + e);
            return "Ocurrió un error inesperado en el servidor";
        }
        return e.getMessage();
    }

    // ---------------- Notificaciones ----------------

    /** Envía una notificación por el canal ASYNC. Lo llaman OTROS hilos, por eso es synchronized. */
    public void notificar(int tipo, Object dato) {
        synchronized (candadoAsincrono) {
            if (outAsincrono == null) {
                return;
            }
            try {
                outAsincrono.writeInt(tipo);
                outAsincrono.writeObject(dato);
                outAsincrono.flush();
                outAsincrono.reset();
            } catch (IOException e) {
                // Ese Frontend ya no está escuchando
                outAsincrono = null;
            }
        }
    }

    public void cerrar() {
        if (!cerrado.compareAndSet(false, true)) {
            return; // Ya estaba cerrado
        }
        activo = false;
        usuario = null;
        try {
            socket.close();
        } catch (IOException ignorada) {
            // nada que hacer
        }
        synchronized (candadoAsincrono) {
            try {
                if (socketAsincrono != null) {
                    socketAsincrono.close();
                }
            } catch (IOException ignorada) {
                // nada que hacer
            }
            outAsincrono = null;
        }
        servidor.eliminarWorker(this);
    }
}
