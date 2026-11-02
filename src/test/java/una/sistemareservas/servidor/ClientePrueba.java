package una.sistemareservas.servidor;

import una.sistemareservas.protocolo.Protocolo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Cliente mínimo para las pruebas: se conecta al servidor igual que el ServiceProxy del Frontend
 * (canal SYNC + canal ASYNC) y guarda en una cola las notificaciones que le lleguen.
 */
class ClientePrueba implements AutoCloseable {

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final Socket socketAsincrono;
    private final BlockingQueue<Object> notificaciones = new LinkedBlockingQueue<>();

    ClientePrueba(int puerto) throws IOException {
        socket = new Socket("localhost", puerto);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        out.writeInt(Protocolo.SYNC);
        out.flush();
        int sesion = in.readInt();

        socketAsincrono = new Socket("localhost", puerto);
        ObjectOutputStream outAsincrono = new ObjectOutputStream(socketAsincrono.getOutputStream());
        outAsincrono.flush();
        ObjectInputStream inAsincrono = new ObjectInputStream(socketAsincrono.getInputStream());
        outAsincrono.writeInt(Protocolo.ASYNC);
        outAsincrono.writeInt(sesion);
        outAsincrono.flush();

        Thread escucha = new Thread(() -> {
            try {
                while (true) {
                    int tipo = inAsincrono.readInt();
                    Object dato = inAsincrono.readObject();
                    if (tipo == Protocolo.NOTIFICACION_RESERVA) {
                        notificaciones.add(dato);
                    }
                }
            } catch (Exception e) {
                // Se cerró la conexión
            }
        });
        escucha.setDaemon(true);
        escucha.start();
    }

    /** Envía una petición y devuelve la respuesta; si el servidor responde con error, lanza Exception. */
    Object pedir(int operacion, Object... parametros) throws Exception {
        out.writeInt(operacion);
        out.writeObject(parametros);
        out.flush();
        out.reset();
        int codigo = in.readInt();
        Object respuesta = in.readObject();
        if (codigo != Protocolo.ERROR_NO_ERROR) {
            throw new Exception((String) respuesta);
        }
        return respuesta;
    }

    /** Espera una notificación hasta cierta cantidad de milisegundos (null si no llegó). */
    Object esperarNotificacion(long milisegundos) throws InterruptedException {
        return notificaciones.poll(milisegundos, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() throws IOException {
        socket.close();
        socketAsincrono.close();
    }
}
