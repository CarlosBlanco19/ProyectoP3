package una.sistemareservas.dto;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

// El formato principal de la reserva, podrian añadirse mas cosas despues
// Contiene toda la informacion de la reserva, el funcionario que hizo la reserva, la descripcion de la reserva, fecha y horas
public class ReservaDTO {
    private String ID;
    private FuncionarioDTO funcionario;
    private String actividad;
    private EstadoReserva estado;
    private LocalDate fecha;
    private LocalTime hora_init;
    private LocalTime hora_final;
    private List<CategoriaRecursoDTO> categorias = new ArrayList<>(); // La lista de las categorias requeridas
    private List<RecursoDTO> recursos = new ArrayList<>(); // Lista de los recursos disponibles que pedian las categorias

    // Constructor
    public ReservaDTO(String ID, FuncionarioDTO funcionario, String actividad, LocalDate fecha, LocalTime hora_init, LocalTime hora_final, List<CategoriaRecursoDTO> categorias) {
        this.ID = ID;
        this.funcionario = funcionario;
        this.actividad = actividad;
        this.fecha = fecha;
        this.hora_init = hora_init;
        this.hora_final = hora_final;
        this.categorias = new ArrayList<>(categorias);
        this.estado = EstadoReserva.ACTIVA;
    }

    // GETS
    public FuncionarioDTO getFuncionario() {
        return funcionario;
    }
    public String getActividad() {
        return actividad;
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
    public String getID() {
        return ID;
    }
    public EstadoReserva getEstado() {
        return estado;
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
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public void setHora_init(LocalTime hora_init) {
        this.hora_init = hora_init;
    }
    public void setHora_final(LocalTime hora_final) {
        this.hora_final = hora_final;
    }


    // Metodos
    public void asignarRecurso(RecursoDTO recurso) {
        recursos.add(recurso); // Añade un recurso a la lista de recursos
    }
    public void cambiarEstado() {
        this.estado = EstadoReserva.CANCELADA;
        this.recursos.clear();
    } // Tengo que revisar si tener estos metodos aqui se puede, o mejor se mete a logica
}
