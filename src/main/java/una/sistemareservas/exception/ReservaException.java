package una.sistemareservas.exception;

import una.sistemareservas.dto.CategoriaRecursoDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class ReservaException extends Exception {

    private final List<CategoriaRecursoDTO> categoriasNoDisponibles;

    public ReservaException(String mensaje) {
        super(mensaje);
        this.categoriasNoDisponibles = new ArrayList<>();
    }

    public ReservaException(String mensaje, List<CategoriaRecursoDTO> categoriasNoDisponibles) {
        super(mensaje);
        this.categoriasNoDisponibles = new ArrayList<>(categoriasNoDisponibles);
    }

    public ReservaException(String mensaje, Throwable error) {
        super(mensaje, error);
        this.categoriasNoDisponibles = new ArrayList<>();
    }

    public List<CategoriaRecursoDTO> getCategoriasNoDisponibles() {
        return Collections.unmodifiableList(categoriasNoDisponibles);
    }
}
