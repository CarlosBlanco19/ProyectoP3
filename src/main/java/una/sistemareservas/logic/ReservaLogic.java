package una.sistemareservas.logic;

import una.sistemareservas.datos.CategoriaRecursoDatos;
import una.sistemareservas.datos.RecursoDatos;
import una.sistemareservas.datos.ReservaDatos;
import una.sistemareservas.datos.UsuarioDatos;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.EstadoReserva;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.exception.ReservaException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReservaLogic {

    private final ReservaDatos reservasDatos = new ReservaDatos();
    private final RecursoDatos recursosDatos = new RecursoDatos();
    private final CategoriaRecursoDatos categoriasDatos = new CategoriaRecursoDatos();
    private final UsuarioDatos usuarioDatos = new UsuarioDatos();

    public synchronized ReservaDTO reservar(ReservaDTO solicitud) throws ReservaException {
        validarSolicitud(solicitud);
        List<CategoriaRecursoDTO> categorias = sinRepetidas(solicitud.getCategorias());

        try {
            FuncionarioDTO funcionario = usuarioDatos.buscarFuncionario(solicitud.getFuncionario().getID());
            if (funcionario == null) {
                throw new ReservaException("El funcionario no está registrado en el sistema");
            }
            for (CategoriaRecursoDTO categoria : categorias) {
                if (categoriasDatos.buscarPorId(categoria.getID()) == null) {
                    throw new ReservaException("La categoría \"" + categoria.getDescripcion() + "\" ya no existe");
                }
            }

            List<RecursoDTO> asignados = new ArrayList<>();
            List<CategoriaRecursoDTO> sinRecursos = new ArrayList<>();
            for (CategoriaRecursoDTO categoria : categorias) {
                RecursoDTO libre = null;
                for (RecursoDTO recurso : recursosDatos.listarDisponibles(categoria.getID(), solicitud.getFecha(),
                        solicitud.getHora_init(), solicitud.getHora_final())) {
                    if (!asignados.contains(recurso)) {
                        libre = recurso;
                        break;
                    }
                }
                if (libre == null) {
                    sinRecursos.add(categoria);
                } else {
                    asignados.add(libre);
                }
            }
            if (!sinRecursos.isEmpty()) {
                String nombres = sinRecursos.stream().map(CategoriaRecursoDTO::getDescripcion)
                        .collect(Collectors.joining(", "));
                throw new ReservaException("No hay recursos disponibles en ese horario para: " + nombres, sinRecursos);
            }

            // 4) Guardar
            ReservaDTO nueva = new ReservaDTO(generarId(), funcionario, solicitud.getActividad().trim(),
                    solicitud.getFecha(), solicitud.getHora_init(), solicitud.getHora_final(), categorias);
            for (RecursoDTO recurso : asignados) {
                nueva.asignarRecurso(recurso);
            }
            reservasDatos.insertar(nueva);

            // Se devuelve tal como quedó en la base de datos
            return reservasDatos.buscarPorId(nueva.getID());
        } catch (SQLException e) {
            throw new ReservaException(ErroresBD.mensaje("guardar la reserva", e), e);
        }
    }

    public synchronized void cancelar(String idReserva, String idFuncionario) throws ReservaException {
        if (idReserva == null || idReserva.isBlank()) {
            throw new ReservaException("Debe seleccionar una reserva");
        }
        try {
            ReservaDTO reserva = reservasDatos.buscarPorId(idReserva);
            if (reserva == null) {
                throw new ReservaException("No existe una reserva con el ID: " + idReserva);
            }
            if (idFuncionario == null || !reserva.getFuncionario().getID().equals(idFuncionario)) {
                throw new ReservaException("Solo puede cancelar sus propias reservas");
            }
            if (reserva.getEstado() == EstadoReserva.CANCELADA) {
                throw new ReservaException("La reserva ya estaba cancelada");
            }
            LocalDateTime inicio = LocalDateTime.of(reserva.getFecha(), reserva.getHora_init());
            if (!inicio.isAfter(LocalDateTime.now())) {
                throw new ReservaException("Solo se pueden cancelar reservas que todavía no han comenzado");
            }
            reservasDatos.cancelar(idReserva);
        } catch (SQLException e) {
            throw new ReservaException(ErroresBD.mensaje("cancelar la reserva", e), e);
        }
    }

    public List<ReservaDTO> listar() throws ReservaException {
        try {
            return reservasDatos.listar();
        } catch (SQLException e) {
            throw new ReservaException(ErroresBD.mensaje("cargar las reservas", e), e);
        }
    }

    public List<ReservaDTO> listarPorFuncionario(String idFuncionario) throws ReservaException {
        try {
            return reservasDatos.listarPorFuncionario(idFuncionario);
        } catch (SQLException e) {
            throw new ReservaException(ErroresBD.mensaje("cargar las reservas", e), e);
        }
    }

    // Validaciones que no necesitan la base de datos
    void validarSolicitud(ReservaDTO solicitud) throws ReservaException {
        if (solicitud == null) {
            throw new ReservaException("No se recibió la reserva");
        }
        if (solicitud.getFuncionario() == null || solicitud.getFuncionario().getID() == null) {
            throw new ReservaException("Debe haber un funcionario con sesión iniciada para reservar");
        }
        if (solicitud.getActividad() == null || solicitud.getActividad().isBlank()) {
            throw new ReservaException("Debe escribir la actividad");
        }
        if (solicitud.getActividad().trim().length() > 100) {
            throw new ReservaException("La actividad no puede tener más de 100 caracteres");
        }
        if (solicitud.getFecha() == null) {
            throw new ReservaException("Debe seleccionar la fecha");
        }
        if (solicitud.getHora_init() == null || solicitud.getHora_final() == null) {
            throw new ReservaException("Debe seleccionar la hora de inicio y la hora final");
        }
        if (!solicitud.getHora_final().isAfter(solicitud.getHora_init())) {
            throw new ReservaException("La hora final debe ser posterior a la hora de inicio");
        }
        if (LocalDateTime.of(solicitud.getFecha(), solicitud.getHora_init()).isBefore(LocalDateTime.now())) {
            throw new ReservaException("No se pueden hacer reservas en fechas u horas que ya pasaron");
        }
        if (solicitud.getCategorias() == null || solicitud.getCategorias().isEmpty()) {
            throw new ReservaException("Debe seleccionar al menos una categoría de recurso");
        }
        for (CategoriaRecursoDTO categoria : solicitud.getCategorias()) {
            if (categoria == null) {
                throw new ReservaException("Alguna de las categorías solicitadas no es válida");
            }
        }
    }

    private List<CategoriaRecursoDTO> sinRepetidas(List<CategoriaRecursoDTO> categorias) {
        Map<Integer, CategoriaRecursoDTO> unicas = new LinkedHashMap<>();
        for (CategoriaRecursoDTO categoria : categorias) {
            unicas.putIfAbsent(categoria.getID(), categoria);
        }
        return new ArrayList<>(unicas.values());
    }

    // RES-000001, RES-000002, ...
    private String generarId() throws SQLException {
        String ultimo = reservasDatos.ultimoId();
        int numero = 0;
        if (ultimo != null) {
            try {
                numero = Integer.parseInt(ultimo.substring(4));
            } catch (NumberFormatException e) {
                numero = 0;
            }
        }
        return String.format("RES-%06d", numero + 1);
    }
}
