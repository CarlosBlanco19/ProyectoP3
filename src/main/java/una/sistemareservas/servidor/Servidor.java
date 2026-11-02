package una.sistemareservas.servidor;

import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.protocolo.IService;
import una.sistemareservas.protocolo.Protocolo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servidor: escucha en un puerto y atiende a los Frontend.
 *
 * Por cada Frontend que se conecta se crea un Worker (un hilo) que atiende sus peticiones.
 * El servidor guarda la lista de Workers para poder enviarles notificaciones a todos
 * (por ejemplo cuando alguien crea una reserva).
 */
public class Servidor {

    private final int puerto;
    private final IService service;
    private final List<Worker> workers = new ArrayList<>();

    private ServerSocket serverSocket;
    private volatile boolean activo;
    private int siguienteSesion = 1;

    public Servidor(int puerto, IService service) {
        this.puerto = puerto;
        this.service = service;
    }

    // Abre el puerto. Si está ocupado lanza IOException.
    public void iniciar() throws IOException {
        serverSocket = new ServerSocket(puerto);
        activo = true;
    }

    public int getPuerto() {
        return serverSocket != null ? serverSocket.getLocalPort() : puerto;
    }

    /** Ciclo principal: espera conexiones. Se queda aquí hasta que se llame detener(). */
    public void atenderConexiones() {
        while (activo) {
            try {
                Socket socket = serverSocket.accept();
                atenderNuevaConexion(socket);
            } catch (SocketException e) {
                // Se cerró el ServerSocket con detener()
            } catch (IOException e) {
                System.err.println("[Servidor] Error aceptando conexión: " + e.getMessage());
            }
        }
    }

    /** Inicia el servidor en un hilo aparte (útil para las pruebas). */
    public Thread iniciarEnHilo() throws IOException {
        iniciar();
        Thread hilo = new Thread(this::atenderConexiones, "servidor");
        hilo.setDaemon(true);
        hilo.start();
        return hilo;
    }

    /**
     * Cada socket nuevo dice primero qué tipo de conexión es:
     *  - SYNC: un Frontend nuevo. Se crea su Worker y se le responde con su número de sesión.
     *  - ASYNC + número de sesión: el canal de notificaciones de un Frontend que ya se conectó.
     */
    private void atenderNuevaConexion(Socket socket) {
        try {
            socket.setSoTimeout(5000); // Si no dice qué es en 5 segundos, se descarta
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            socket.setSoTimeout(0);

            int tipo = in.readInt();
            if (tipo == Protocolo.SYNC) {
                Worker worker;
                synchronized (workers) {
                    worker = new Worker(this, socket, in, out, service, siguienteSesion++);
                    workers.add(worker);
                }
                out.writeInt(worker.getSesion());
                out.flush();
                worker.start();
                System.out.println("[Servidor] Frontend conectado desde " + socket.getInetAddress().getHostAddress()
                        + " (sesión " + worker.getSesion() + ")");
            } else if (tipo == Protocolo.ASYNC) {
                int sesion = in.readInt();
                Worker worker = buscarWorker(sesion);
                if (worker == null) {
                    socket.close();
                } else {
                    worker.setCanalAsincrono(socket, out);
                }
            } else {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("[Servidor] Conexión inválida: " + e.getMessage());
            try {
                socket.close();
            } catch (IOException ignorada) {
                // nada que hacer
            }
        }
    }

    private Worker buscarWorker(int sesion) {
        synchronized (workers) {
            for (Worker worker : workers) {
                if (worker.getSesion() == sesion) {
                    return worker;
                }
            }
        }
        return null;
    }

    /**
     * Envía la reserva nueva a TODOS los demás Frontend que tienen sesión iniciada,
     * menos al que la creó (él ya la ve en "Mis reservas").
     */
    public void notificarReserva(ReservaDTO reserva, Worker origen) {
        List<Worker> copia;
        synchronized (workers) {
            copia = new ArrayList<>(workers);
        }
        String idCreador = reserva.getFuncionario() != null ? reserva.getFuncionario().getID() : null;
        for (Worker worker : copia) {
            if (worker == origen || worker.getUsuario() == null) {
                continue;
            }
            if (idCreador != null && idCreador.equals(worker.getUsuario().getID())) {
                continue; // Es el mismo usuario en otra computadora: no es "otro usuario"
            }
            worker.notificar(Protocolo.NOTIFICACION_RESERVA, reserva);
        }
    }

    public void eliminarWorker(Worker worker) {
        synchronized (workers) {
            workers.remove(worker);
        }
        System.out.println("[Servidor] Frontend desconectado (sesión " + worker.getSesion() + ")");
    }

    public int cantidadConectados() {
        synchronized (workers) {
            return workers.size();
        }
    }

    public void detener() {
        activo = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignorada) {
            // nada que hacer
        }
        List<Worker> copia;
        synchronized (workers) {
            copia = new ArrayList<>(workers);
        }
        for (Worker worker : copia) {
            worker.cerrar();
        }
    }
}
