package una.sistemareservas.servidor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import una.sistemareservas.DatosPrueba;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.dto.UsuarioDTO;
import una.sistemareservas.protocolo.Protocolo;
import una.sistemareservas.service.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Prueba de INTEGRACIÓN de la arquitectura completa del Backend:
 * servidor con sockets + un hilo por cliente + notificaciones asíncronas + MySQL.
 */
class ServidorIT {

    private static final Service service = new Service();
    private static Servidor servidor;
    private static CategoriaRecursoDTO categoria;

    @BeforeAll
    static void iniciarServidor() throws Exception {
        servidor = new Servidor(0, service); // Puerto 0 = cualquier puerto libre
        servidor.iniciarEnHilo();

        String sufijo = DatosPrueba.sufijo();
        categoria = service.agregarCategoria("IT servidor " + sufijo);
        service.agregarRecurso(new RecursoDTO("IT-S-" + sufijo, "Recurso del servidor", categoria));
    }

    @AfterAll
    static void detenerServidor() throws Exception {
        servidor.detener();
        DatosPrueba.borrarCategoria(categoria.getID());
    }

    @Test
    void sinLoginNoSePuedeUsar() throws Exception {
        try (ClientePrueba cliente = new ClientePrueba(servidor.getPuerto())) {
            Exception e = assertThrows(Exception.class, () -> cliente.pedir(Protocolo.CATEGORIA_LISTAR));
            assertEquals("Debe iniciar sesión", e.getMessage());
        }
    }

    @Test
    void funcionarioNoPuedeHacerCosasDeAdministrador() throws Exception {
        try (ClientePrueba cliente = new ClientePrueba(servidor.getPuerto())) {
            cliente.pedir(Protocolo.LOGIN, "ISS", "ISS");
            Exception e = assertThrows(Exception.class, () -> cliente.pedir(Protocolo.CATEGORIA_AGREGAR, "Hackeo"));
            assertEquals("Solo el administrador puede hacer esta acción", e.getMessage());
        }
    }

    @Test
    void laReservaNuevaLesLlegaALosDemasUsuarios() throws Exception {
        try (ClientePrueba carlos = new ClientePrueba(servidor.getPuerto());
             ClientePrueba anthony = new ClientePrueba(servidor.getPuerto());
             ClientePrueba admin = new ClientePrueba(servidor.getPuerto());
             ClientePrueba sinSesion = new ClientePrueba(servidor.getPuerto())) {

            UsuarioDTO usuarioCarlos = (UsuarioDTO) carlos.pedir(Protocolo.LOGIN, "ISS", "ISS");
            anthony.pedir(Protocolo.LOGIN, "ISS-1", "ISS-1");
            admin.pedir(Protocolo.LOGIN, "admin", "1234");

            ReservaDTO solicitud = new ReservaDTO(null, (FuncionarioDTO) usuarioCarlos, "Reunión por sockets",
                    LocalDate.now().plusYears(4), LocalTime.of(9, 0), LocalTime.of(10, 0), List.of(categoria));
            ReservaDTO creada = (ReservaDTO) carlos.pedir(Protocolo.RESERVA_RESERVAR, solicitud);

            // A Anthony y al administrador les llega la alerta...
            assertEquals(creada, anthony.esperarNotificacion(5000));
            ReservaDTO alertaAdmin = (ReservaDTO) admin.esperarNotificacion(5000);
            assertEquals("Reunión por sockets", alertaAdmin.getActividad());
            assertEquals("Carlos", alertaAdmin.getFuncionario().getNombre());

            // ...pero no a quien la creó ni a quien no ha iniciado sesión
            assertNull(carlos.esperarNotificacion(500));
            assertNull(sinSesion.esperarNotificacion(500));
        }
    }

    @Test
    void laReservaQuedaANombreDeQuienTieneLaSesion() throws Exception {
        try (ClientePrueba anthony = new ClientePrueba(servidor.getPuerto())) {
            anthony.pedir(Protocolo.LOGIN, "ISS-1", "ISS-1");
            // Intenta reservar a nombre de otra persona
            ReservaDTO solicitud = new ReservaDTO(null, new FuncionarioDTO("CEO", "Deivert", "88886666"),
                    "Suplantación", LocalDate.now().plusYears(4), LocalTime.of(15, 0), LocalTime.of(16, 0),
                    List.of(categoria));
            ReservaDTO creada = (ReservaDTO) anthony.pedir(Protocolo.RESERVA_RESERVAR, solicitud);
            assertEquals("ISS-1", creada.getFuncionario().getID());
        }
    }
}
