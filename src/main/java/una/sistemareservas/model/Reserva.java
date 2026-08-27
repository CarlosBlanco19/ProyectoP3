package una.sistemareservas.model;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

// El formato principal de la reserva, podrian añadirse mas cosas despues
// Contiene toda la informacion de la reserva, el funcionario que hizo la reserva, la descripcion de la reserva, fecha y horas
public class Reserva {
    private String ID;
    private Funcionario funcionario;
    private String actividad;
    private EstadoReserva estado;
    private LocalDate fecha;
    private LocalTime hora_init;
    private LocalTime hora_final;
    private List<CategoriaRecurso> categorias = new ArrayList<>(); // La lista de las categorias requeridas
    private List<Recurso> recursos = new ArrayList<>(); // Lista de los recursos disponibles que pedian las categorias

    // Constructor
    public Reserva(String ID, Funcionario funcionario, String actividad, LocalDate fecha, LocalTime hora_init, LocalTime hora_final, List<CategoriaRecurso> categorias) {
        if (actividad == null || actividad.isBlank()) {
            throw new IllegalArgumentException("La actividad no puede estar vacía");
        }
        if (fecha == null || hora_init == null || hora_final == null) {
            throw new IllegalArgumentException("Fecha, hora de inicio y hora de fin son obligatorias");
        }
        if (!hora_final.isAfter(hora_init)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }
        if (categorias == null || categorias.isEmpty()) {
            throw new IllegalArgumentException("Debe solicitar al menos una categoría de recurso");
        }

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
    public Funcionario getFuncionario() {

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
    public List<CategoriaRecurso> getCategorias() {
        return Collections.unmodifiableList(categorias);
    }

    public List<Recurso> getRecursos() {
        return Collections.unmodifiableList(recursos);
    }

    // SETS
    public void setID(String id) {
        this.ID = id;
    }
    public void setFuncionario(Funcionario funcionario) {
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
    public void asignarRecurso(Recurso recurso) {
        recursos.add(recurso); // Añade un recurso a la lista de recursos
    }

    public void cancelar() {
        this.estado = EstadoReserva.CANCELADA; // Cancela la reserva
        recursos.clear();
    }

    // Metodo para comparar dos reservas, basandose en el ID
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reserva)) {
            return false;
        }
        Reserva otra = (Reserva) o;
        return ID.equals(otra.ID);
    }

    // Informacion base de la reserva
    public String toString() {
        return "Informacion de la reserva: ID: '" + ID + "', actividad: '" + actividad + "', fecha :" + fecha +
                ", hora:" + hora_init + " - " + hora_final + ", estado: " + estado;
    }
}
