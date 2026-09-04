package una.sistemareservas.exception;

import una.sistemareservas.dto.CategoriaRecursoDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class ReservaException extends Exception {

    private final List<CategoriaRecursoDTO> categoriasNoDisponibles;

    public ReservaException(String mensaje) {
        this.categoriasNoDisponibles = null;
        super(mensaje);
    }

    public ReservaException(String mensaje, List<CategoriaRecursoDTO> categoriasNoDisponibles) {
        this.categoriasNoDisponibles = new ArrayList<>(categoriasNoDisponibles);
        super(mensaje);
    }

    public List<CategoriaRecursoDTO> getCategoriasNoDisponibles() {
        return Collections.unmodifiableList(categoriasNoDisponibles);
    }
}
