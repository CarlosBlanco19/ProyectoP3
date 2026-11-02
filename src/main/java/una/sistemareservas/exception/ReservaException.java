package una.sistemareservas.exception;

import una.sistemareservas.dto.CategoriaRecursoDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Error de validación o de datos relacionado con reservas.
// El mensaje es el que se le muestra al usuario en el Frontend.
public class ReservaException extends Exception {

    // Categorías para las que no había recursos libres (si ese fue el problema)
    private final List<CategoriaRecursoDTO> categoriasNoDisponibles;

    public ReservaException(String mensaje) {
        super(mensaje);
        this.categoriasNoDisponibles = new ArrayList<>();
    }

    public ReservaException(String mensaje, List<CategoriaRecursoDTO> categoriasNoDisponibles) {
        super(mensaje);
        this.categoriasNoDisponibles = new ArrayList<>(categoriasNoDisponibles);
    }

    public ReservaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.categoriasNoDisponibles = new ArrayList<>();
    }

    public List<CategoriaRecursoDTO> getCategoriasNoDisponibles() {
        return Collections.unmodifiableList(categoriasNoDisponibles);
    }
}
