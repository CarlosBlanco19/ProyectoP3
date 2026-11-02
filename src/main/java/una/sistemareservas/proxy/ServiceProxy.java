package una.sistemareservas.proxy;

import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.dto.UsuarioDTO;
import una.sistemareservas.protocolo.IService;
import una.sistemareservas.protocolo.Protocolo;
import una.sistemareservas.utilidades.Configuracion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;

public class ServiceProxy implements IService {

    private static ServiceProxy instancia;

    private final String host;
    private final int puerto;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private Socket socketAsincrono;
    private Thread hiloNotificaciones;
    private volatile Consumer<ReservaDTO> oyenteReservas;

    public static synchronized ServiceProxy instancia() {
        if (instancia == null) {
            instancia = new ServiceProxy(
                    Configuracion.get("servidor.host", Protocolo.SERVIDOR),
                    Configuracion.getInt("servidor.puerto", Protocolo.PUERTO));
        }
        return instancia;
    }

    public ServiceProxy(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    public void setOyenteReservas(Consumer<ReservaDTO> oyente) {
        this.oyenteReservas = oyente;
    }

    public String getDireccion() {
        return host + ":" + puerto;
    }

    public synchronized boolean estaConectado() {
        return socket != null && !socket.isClosed();
    }

    public synchronized void conectar() throws Exception {
        if (estaConectado()) {
            return;
        }
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, puerto), 5000);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            out.writeInt(Protocolo.SYNC);
            out.flush();
            int sesion = in.readInt();

            socketAsincrono = new Socket();
            socketAsincrono.connect(new InetSocketAddress(host, puerto), 5000);
            ObjectOutputStream outAsincrono = new ObjectOutputStream(socketAsincrono.getOutputStream());
            outAsincrono.flush();
            ObjectInputStream inAsincrono = new ObjectInputStream(socketAsincrono.getInputStream());
            outAsincrono.writeInt(Protocolo.ASYNC);
            outAsincrono.writeInt(sesion);
            outAsincrono.flush();

            hiloNotificaciones = new Thread(() -> escucharNotificaciones(inAsincrono), "escucha-notificaciones");
            hiloNotificaciones.setDaemon(true); // No impide que la aplicación se cierre
            hiloNotificaciones.start();
        } catch (IOException e) {
            cerrarSockets();
            throw new Exception("No se pudo conectar con el servidor (" + getDireccion()
                    + "). Verifique que el Backend esté corriendo.");
        }
    }

    public synchronized void desconectar() {
        if (estaConectado()) {
            try {
                out.writeInt(Protocolo.LOGOUT);
                out.writeObject(new Object[0]);
                out.flush();
            } catch (IOException ignorada) {
                // Si ya no hay conexión no importa
            }
        }
        cerrarSockets();
    }

    private void cerrarSockets() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignorada) {
            // nada que hacer
        }
        try {
            if (socketAsincrono != null) socketAsincrono.close();
        } catch (IOException ignorada) {
            // nada que hacer
        }
        socket = null;
        socketAsincrono = null;
        out = null;
        in = null;
    }

    private void escucharNotificaciones(ObjectInputStream inAsincrono) {
        try {
            while (true) {
                int tipo = inAsincrono.readInt();
                Object dato = inAsincrono.readObject();
                if (tipo == Protocolo.NOTIFICACION_RESERVA && dato instanceof ReservaDTO reserva) {
                    Consumer<ReservaDTO> oyente = oyenteReservas;
                    if (oyente != null) {
                        oyente.accept(reserva);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            //a
        }
    }

    private synchronized Object enviar(int operacion, Object... parametros) throws Exception {
        if (!estaConectado()) {
            throw new Exception("No hay conexión con el servidor. Cierre la aplicación y vuelva a ingresar.");
        }
        int codigo;
        Object respuesta;
        try {
            out.writeInt(operacion);
            out.writeObject(parametros);
            out.flush();
            out.reset(); // Para que la próxima vez se envíen los objetos nuevos y no copias guardadas
            codigo = in.readInt();
            respuesta = in.readObject();
        } catch (IOException e) {
            cerrarSockets();
            throw new Exception("Se perdió la conexión con el servidor. Cierre la aplicación y vuelva a ingresar.");
        }
        if (codigo != Protocolo.ERROR_NO_ERROR) {
            throw new Exception(String.valueOf(respuesta));
        }
        return respuesta;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> enviarLista(int operacion, Object... parametros) throws Exception {
        return (List<T>) enviar(operacion, parametros);
    }

    @Override
    public UsuarioDTO login(String id, String clave) throws Exception {
        conectar(); // La primera vez se conecta aquí
        return (UsuarioDTO) enviar(Protocolo.LOGIN, id, clave);
    }

    @Override
    public void cambiarClave(String id, String claveActual, String claveNueva) throws Exception {
        conectar();
        enviar(Protocolo.CAMBIAR_CLAVE, id, claveActual, claveNueva);
    }

    // ---------------- Funcionarios ----------------
    @Override
    public List<FuncionarioDTO> listarFuncionarios() throws Exception {
        return enviarLista(Protocolo.FUNCIONARIO_LISTAR);
    }

    @Override
    public FuncionarioDTO buscarFuncionario(String id) throws Exception {
        return (FuncionarioDTO) enviar(Protocolo.FUNCIONARIO_BUSCAR, id);
    }

    @Override
    public List<FuncionarioDTO> buscarFuncionariosPorNombre(String nombre) throws Exception {
        return enviarLista(Protocolo.FUNCIONARIO_BUSCAR_NOMBRE, nombre);
    }

    @Override
    public void agregarFuncionario(FuncionarioDTO funcionario) throws Exception {
        enviar(Protocolo.FUNCIONARIO_AGREGAR, funcionario);
    }

    @Override
    public void actualizarFuncionario(FuncionarioDTO funcionario) throws Exception {
        enviar(Protocolo.FUNCIONARIO_ACTUALIZAR, funcionario);
    }

    @Override
    public void eliminarFuncionario(String id) throws Exception {
        enviar(Protocolo.FUNCIONARIO_ELIMINAR, id);
    }

    // ---------------- Categorías ----------------
    @Override
    public List<CategoriaRecursoDTO> listarCategorias() throws Exception {
        return enviarLista(Protocolo.CATEGORIA_LISTAR);
    }

    @Override
    public List<CategoriaRecursoDTO> buscarCategorias(String descripcion) throws Exception {
        return enviarLista(Protocolo.CATEGORIA_BUSCAR, descripcion);
    }

    @Override
    public CategoriaRecursoDTO agregarCategoria(String descripcion) throws Exception {
        return (CategoriaRecursoDTO) enviar(Protocolo.CATEGORIA_AGREGAR, descripcion);
    }

    @Override
    public void actualizarCategoria(int id, String descripcion) throws Exception {
        enviar(Protocolo.CATEGORIA_ACTUALIZAR, id, descripcion);
    }

    @Override
    public void eliminarCategoria(int id) throws Exception {
        enviar(Protocolo.CATEGORIA_ELIMINAR, id);
    }

    // ---------------- Recursos ----------------
    @Override
    public List<RecursoDTO> listarRecursos() throws Exception {
        return enviarLista(Protocolo.RECURSO_LISTAR);
    }

    @Override
    public List<RecursoDTO> listarRecursosPorCategoria(int idCategoria) throws Exception {
        return enviarLista(Protocolo.RECURSO_LISTAR_CATEGORIA, idCategoria);
    }

    @Override
    public RecursoDTO buscarRecurso(String id) throws Exception {
        return (RecursoDTO) enviar(Protocolo.RECURSO_BUSCAR, id);
    }

    @Override
    public void agregarRecurso(RecursoDTO recurso) throws Exception {
        enviar(Protocolo.RECURSO_AGREGAR, recurso);
    }

    @Override
    public void actualizarRecurso(RecursoDTO recurso) throws Exception {
        enviar(Protocolo.RECURSO_ACTUALIZAR, recurso);
    }

    @Override
    public void eliminarRecurso(String id) throws Exception {
        enviar(Protocolo.RECURSO_ELIMINAR, id);
    }

    // ---------------- Reservas ----------------
    @Override
    public ReservaDTO reservar(ReservaDTO solicitud) throws Exception {
        return (ReservaDTO) enviar(Protocolo.RESERVA_RESERVAR, solicitud);
    }

    @Override
    public void cancelarReserva(String idReserva, String idFuncionario) throws Exception {
        enviar(Protocolo.RESERVA_CANCELAR, idReserva, idFuncionario);
    }

    @Override
    public List<ReservaDTO> listarReservasPorFuncionario(String idFuncionario) throws Exception {
        return enviarLista(Protocolo.RESERVA_LISTAR_FUNCIONARIO, idFuncionario);
    }

    @Override
    public List<ReservaDTO> listarReservas() throws Exception {
        return enviarLista(Protocolo.RESERVA_LISTAR);
    }
}
