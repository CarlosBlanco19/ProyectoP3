package una.sistemareservas.model;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;

// El formato principal de la reserva, podrian añadirse mas cosas despues
// Contiene toda la informacion de la reserva, el funcionario que hizo la reserva, la descripcion de la reserva, fecha y horas
public class Reserva {
    private Funcionario funcionario;
    private String descripcion;
    private LocalDate fecha;
    private LocalTime hora_init;
    private LocalTime hora_final;
    private List<CategoriaRecurso> categorias = new ArrayList<>(); // La lista de las categorias requeridas
    private List<Recurso> recursos = new ArrayList<>(); // Lista de los recursos disponibles que pedian las categorias

    // Constructor
    public Reserva(Funcionario funcionario, String descripcion, LocalDate fecha, LocalTime hora_init, LocalTime hora_final, List<CategoriaRecurso> categorias) {
        this.funcionario = funcionario;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.hora_init = hora_init;
        this.hora_final = hora_final;
        this.categorias = categorias;
    }

    // GETS
    public Funcionario getFuncionario() {
        return funcionario;
    }
    public String getDescripcion() {
        return descripcion;
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
    public List<CategoriaRecurso> getCategorias() {
        return categorias;
    }
    public List<Recurso> getRecursos() {
        return recursos;
    }

    // SETS
    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    // Informacion base de la reserva, por ahora solo de ejemplo
    public String toString() {
        return funcionario + ", " + descripcion + ", " + fecha + ", " + hora_init + " - " + hora_final;
    }
}
