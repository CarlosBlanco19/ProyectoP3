package una.sistemareservas.logic;

import una.sistemareservas.dto.*;
import una.sistemareservas.datos.ReservaDatos;
import una.sistemareservas.exception.RecursoException;
import una.sistemareservas.exception.ReservaException;
import una.sistemareservas.exception.UsuarioException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaLogic {

    private final ReservaDatos reservasDatos = new ReservaDatos();
    private final CategoriaLogic categoriaLogic;
    private final RecursoLogic recursoLogic;
    private final UsuarioLogic usuarioLogic;
    private final List<ReservaDTO> reservas;

    public ReservaLogic(CategoriaLogic categoriaLogic, RecursoLogic recursoLogic, UsuarioLogic usuarioLogic) throws ReservaException {
        this.categoriaLogic = categoriaLogic;
        this.recursoLogic = recursoLogic;
        this.usuarioLogic = usuarioLogic;

        List<ReservaDTO> listas;
        try {
            listas = reservasDatos.serializar(usuarioLogic.listarFuncionarios(), categoriaLogic.listar(), recursoLogic.listar());
        } catch (IOException e) {
            throw new ReservaException("No se pudieron cargar las reservas", e);
        }
        reservas = listas;
    }

    public ReservaDTO reservar(String funcionarioId, String actividad, LocalDate fecha, LocalTime hora_init, LocalTime hora_final, List<CategoriaRecursoDTO> categorias) throws UsuarioException, ReservaException, RecursoException {
        String ID = generarID();
        FuncionarioDTO funcionario = null;
        for (FuncionarioDTO f : usuarioLogic.listarFuncionarios()) {
            if (f.getID().equals(funcionarioId)) {
                funcionario = f;
                break;
            }
        }
        if (funcionario == null) {
            throw new UsuarioException("El funcionario no esta registrado en el sistema");
        }

        if (actividad == null || actividad.isBlank()) {
            throw new ReservaException("La actividad esta vacia");
        }
        if (fecha == null || hora_init == null || hora_final == null) {
            throw new ReservaException("Fecha, hora de inicio y la hora final son obligatorias");
        }
        if (!hora_final.isAfter(hora_init)) {
            throw new ReservaException("La hora de final debe ser posterior a la hora de inicio");
        }
        if (categorias == null || categorias.isEmpty()) {
            throw new ReservaException("Debe enviar al menos una categoria de recurso");
        }

        for (CategoriaRecursoDTO categoria : categorias) {
            if (categoria == null || categoriaLogic.buscarID(categoria.getID()) == null) {
                throw new RecursoException("Alguna de las categorias solicitadas no existe");
            }
        }

        List<CategoriaRecursoDTO> categoriasNoDispo = new ArrayList<>();
        List<RecursoDTO> recursosAsignados = new ArrayList<>();

        for (CategoriaRecursoDTO categoria : categorias) {
            RecursoDTO disponible = buscarDisponible(categoria, fecha, hora_init, hora_final, recursosAsignados);
            if (disponible == null) {
                categoriasNoDispo.add(categoria);
            } else {
                recursosAsignados.add(disponible);
            }
        }

        if (!categoriasNoDispo.isEmpty()) {
            throw new ReservaException("No hay recursos disponibles para una o varias de las categorias solicitadas", categoriasNoDispo);
        }

        ReservaDTO nuevaReserva = new ReservaDTO(ID, funcionario, actividad, fecha, hora_init, hora_final, categorias);
        for (RecursoDTO recurso : recursosAsignados) {
            nuevaReserva.asignarRecurso(recurso);
        }

        reservas.add(nuevaReserva);
        guardar();
        return nuevaReserva;
    }

    private RecursoDTO buscarDisponible(CategoriaRecursoDTO categoria, LocalDate fecha, LocalTime hora_init, LocalTime hora_final, List<RecursoDTO> yaAsignadosEnEstaReserva) {
        for (RecursoDTO recurso : recursoLogic.listarCategoria(categoria.getID())) {
            if (yaAsignadosEnEstaReserva.contains(recurso)) {
                continue;
            } else if (!ocupado(recurso, fecha, hora_init, hora_final)) {
                return recurso;
            }
        }
        return null;
    }

    private boolean ocupado(RecursoDTO recurso, LocalDate fecha, LocalTime hora_init, LocalTime hora_final) {
        for (ReservaDTO reserva : reservas) {
            if (reserva.getEstado() != EstadoReserva.ACTIVA | !reserva.getFecha().equals(fecha) | !reserva.getRecursos().contains(recurso)) {
                continue;
            }
            if (hora_init.isBefore(reserva.getHora_final()) && reserva.getHora_init().isBefore(hora_final)) {
                return true;
            }
        }
        return false;
    }

    private void guardar() throws ReservaException {
        try {
            reservasDatos.deserializar(reservas);
        } catch (IOException e) {
            throw new ReservaException("No se pudo guardar la reserva en el archivo", e);
        }
    }

    private String generarID() {
        int maximo = 0;
        for (ReservaDTO reserva : reservas) {
            String numeroTexto = reserva.getID().substring(4);
            int numero = Integer.parseInt(numeroTexto);
            if (numero > maximo) {
                maximo = numero;
            }
        }
        int siguiente = maximo + 1;
        return String.format("RES-%06d", siguiente);
    }

    public List<ReservaDTO> listarPorFuncionario(String funcionarioId) {
        List<ReservaDTO> resultado = new ArrayList<>();
        for (ReservaDTO reserva : reservas) {
            if (reserva.getFuncionario() != null && reserva.getFuncionario().getID().equals(funcionarioId)) {
                resultado.add(reserva);
            }
        }
        return resultado;
    }

    public ReservaDTO buscarPorID(String id) {
        for (ReservaDTO reserva : reservas) {
            if (reserva.getID().equals(id)) {
                return reserva;
            }
        }
        return null;
    }

    public List<ReservaDTO> getReservas() {
        return reservas;
    }

    public void cancelar(String idReserva) throws ReservaException {
        ReservaDTO reserva = buscarPorID(idReserva);
        if (reserva == null) {
            throw new ReservaException("No existe una reserva con el ID: " + idReserva);
        }
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ReservaException("La reserva ya estaba cancelada");
        }
        LocalDateTime inicioReserva = LocalDateTime.of(reserva.getFecha(), reserva.getHora_init());
        if (!inicioReserva.isAfter(LocalDateTime.now())) {
            throw new ReservaException("Solo se pueden cancelar reservas posteriores a la fecha actual");
        }

        reserva.cambiarEstado();
        guardar();
    }
}
