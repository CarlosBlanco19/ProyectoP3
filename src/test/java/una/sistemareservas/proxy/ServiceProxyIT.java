package una.sistemareservas.proxy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.dto.UsuarioDTO;
import una.sistemareservas.protocolo.Protocolo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba de INTEGRACIÓN del ServiceProxy con sockets de verdad.
 * En vez del Backend real se levanta un servidor "falso" muy pequeño que habla el mismo protocolo,
 * así la prueba no necesita tener el Backend corriendo.
 */
class ServiceProxyIT {

    private ServerSocket servidorFalso;
    private Thread hiloServidor;
    private volatile ObjectOutputStream canalAsincrono;
    private final CountDownLatch asincronoListo = new CountDownLatch(1);

    @BeforeEach
    void levantarServidorFalso() throws Exception {
        servidorFalso = new ServerSocket(0);
        hiloServidor = new Thread(this::atender);
        hiloServidor.setDaemon(true);
        hiloServidor.start();
    }

    @AfterEach
    void apagarServidorFalso() throws Exception {
        servidorFalso.close();
    }

    // Atiende el canal SYNC (responde LOGIN y CATEGORIA_LISTAR) y guarda el canal ASYNC
    private void atender() {
        try {
            while (true) {
                Socket socket = servidorFalso.accept();
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                int tipo = in.readInt();
                if (tipo == Protocolo.ASYNC) {
                    in.readInt(); // número de sesión
                    canalAsincrono = out;
                    asincronoListo.countDown();
                    continue;
                }
                out.writeInt(77); // número de sesión
                out.flush();
                Thread peticiones = new Thread(() -> responder(in, out));
                peticiones.setDaemon(true);
                peticiones.start();
            }
        } catch (Exception e) {
            // Se cerró el servidor falso
        }
    }

    private void responder(ObjectInputStream in, ObjectOutputStream out) {
        try {
            while (true) {
                int operacion = in.readInt();
                Object[] parametros = (Object[]) in.readObject();
                if (operacion == Protocolo.LOGIN && "1234".equals(parametros[1])) {
                    out.writeInt(Protocolo.ERROR_NO_ERROR);
                    out.writeObject(new FuncionarioDTO((String) parametros[0], "Carlos", "88887777"));
                } else if (operacion == Protocolo.CATEGORIA_LISTAR) {
                    out.writeInt(Protocolo.ERROR_NO_ERROR);
                    out.writeObject(new java.util.ArrayList<>(List.of(new CategoriaRecursoDTO(1, "Laptop"))));
                } else {
                    out.writeInt(Protocolo.ERROR_ERROR);
                    out.writeObject("Usuario o clave incorrectos");
                }
                out.flush();
            }
        } catch (Exception e) {
            // El cliente se desconectó
        }
    }

    @Test
    void loginYListarPorElSocket() throws Exception {
        ServiceProxy proxy = new ServiceProxy("localhost", servidorFalso.getLocalPort());
        try {
            UsuarioDTO usuario = proxy.login("ISS", "1234");
            assertEquals("ISS", usuario.getID());
            assertTrue(proxy.estaConectado());

            List<CategoriaRecursoDTO> categorias = proxy.listarCategorias();
            assertEquals("Laptop", categorias.get(0).getDescripcion());
        } finally {
            proxy.desconectar();
        }
    }

    @Test
    void elErrorDelServidorLlegaComoExcepcion() throws Exception {
        ServiceProxy proxy = new ServiceProxy("localhost", servidorFalso.getLocalPort());
        try {
            Exception e = assertThrows(Exception.class, () -> proxy.login("ISS", "mala"));
            assertEquals("Usuario o clave incorrectos", e.getMessage());
        } finally {
            proxy.desconectar();
        }
    }

    @Test
    void lasNotificacionesLleganAlOyente() throws Exception {
        ServiceProxy proxy = new ServiceProxy("localhost", servidorFalso.getLocalPort());
        AtomicReference<ReservaDTO> recibida = new AtomicReference<>();
        CountDownLatch llego = new CountDownLatch(1);
        proxy.setOyenteReservas(reserva -> {
            recibida.set(reserva);
            llego.countDown();
        });
        try {
            proxy.login("ISS", "1234");
            assertTrue(asincronoListo.await(5, TimeUnit.SECONDS), "El proxy debe abrir el canal ASYNC");

            // El servidor avisa de una reserva nueva sin que nadie se lo pida
            ReservaDTO nueva = new ReservaDTO("RES-000099", new FuncionarioDTO("ISS-2", "Juan", "1"), "Entrevistas",
                    LocalDate.of(2026, 11, 20), LocalTime.of(10, 0), LocalTime.of(11, 0), List.of());
            canalAsincrono.writeInt(Protocolo.NOTIFICACION_RESERVA);
            canalAsincrono.writeObject(nueva);
            canalAsincrono.flush();

            assertTrue(llego.await(5, TimeUnit.SECONDS), "La notificación debe llegar al oyente");
            assertEquals("Entrevistas", recibida.get().getActividad());
        } finally {
            proxy.desconectar();
        }
    }

    @Test
    void sinServidorDaUnMensajeClaro() throws Exception {
        int puertoSinServidor;
        try (ServerSocket temporal = new ServerSocket(0)) {
            puertoSinServidor = temporal.getLocalPort();
        }
        ServiceProxy proxy = new ServiceProxy("localhost", puertoSinServidor);
        Exception e = assertThrows(Exception.class, () -> proxy.login("ISS", "1234"));
        assertTrue(e.getMessage().startsWith("No se pudo conectar con el servidor"));
        assertFalse(proxy.estaConectado());
    }
}
