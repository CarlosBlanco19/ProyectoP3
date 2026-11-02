package una.sistemareservas.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// Toda la información de una reserva: quién la hizo, la actividad, fecha, horas,
// las categorías que pidió y los recursos que el sistema le asignó.
public class ReservaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ID;
    private FuncionarioDTO funcionario;
    private String actividad;
    private EstadoReserva estado;
    private LocalDate fecha;
    private LocalTime hora_init;
    private LocalTime hora_final;
    private List<CategoriaRecursoDTO> categorias = new ArrayList<>(); // Categorías pedidas
    private List<RecursoDTO> recursos = new ArrayList<>();            // Recursos asignados

    public ReservaDTO(String ID, FuncionarioDTO funcionario, String actividad, LocalDate fecha,
                      LocalTime hora_init, LocalTime hora_final, List<CategoriaRecursoDTO> categorias) {
        this.ID = ID;
        this.funcionario = funcionario;
        this.actividad = actividad;
        this.fecha = fecha;
        this.hora_init = hora_init;
        this.hora_final = hora_final;
        if (categorias != null) {
            this.categorias = new ArrayList<>(categorias);
        }
        this.estado = EstadoReserva.ACTIVA;
    }

    // GETS
    public String getID() {
        return ID;
    }
    public FuncionarioDTO getFuncionario() {
        return funcionario;
    }
    public String getActividad() {
        return actividad;
    }
    public EstadoReserva getEstado() {
        return estado;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public LocalTime getHora_init() {
        return hora_init;
    }
    public LocalTime getHora_final() {
        return hora_final;
    }
    public List<CategoriaRecursoDTO> getCategorias() {
        return Collections.unmodifiableList(categorias);
    }
    public List<RecursoDTO> getRecursos() {
        return Collections.unmodifiableList(recursos);
    }

    // SETS
    public void setID(String id) {
        this.ID = id;
    }
    public void setFuncionario(FuncionarioDTO funcionario) {
        this.funcionario = funcionario;
    }
    public void setActividad(String actividad) {
        this.actividad = actividad;
    }
    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public void setHora_init(LocalTime hora_init) {
        this.hora_init = hora_init;
    }
    public void setHora_final(LocalTime hora_final) {
        this.hora_final = hora_final;
    }

    // Métodos
    public void asignarRecurso(RecursoDTO recurso) {
        recursos.add(recurso);
    }

    // Dos reservas son iguales si tienen el mismo ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservaDTO otra)) return false;
        return Objects.equals(ID, otra.ID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ID);
    }
}
