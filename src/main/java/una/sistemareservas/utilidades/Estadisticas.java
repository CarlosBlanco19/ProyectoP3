package una.sistemareservas.utilidades;

import una.sistemareservas.dto.EstadoReserva;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public final class Estadisticas {

    private Estadisticas() {
    }

    public static Map<String, Integer> recursosPorCategoria(List<ReservaDTO> reservas, LocalDate desde, LocalDate hasta) {
        Map<String, Integer> conteo = new TreeMap<>(); // TreeMap = ordenado alfabéticamente
        for (ReservaDTO reserva : reservas) {
            if (!cuenta(reserva, desde, hasta)) {
                continue;
            }
            for (RecursoDTO recurso : reserva.getRecursos()) {
                String categoria = recurso.getCategoria() != null ? recurso.getCategoria().getDescripcion() : "Sin categoría";
                conteo.merge(categoria, 1, Integer::sum);
            }
        }
        return conteo;
    }

    public static Map<String, Integer> actividadesPorSemana(List<ReservaDTO> reservas, LocalDate desde, LocalDate hasta) {
        Map<String, Integer> conteo = new LinkedHashMap<>(); // Mantiene el orden en que se agregan
        for (LocalDate lunes = desde.with(DayOfWeek.MONDAY); !lunes.isAfter(hasta); lunes = lunes.plusWeeks(1)) {
            conteo.put(lunes.toString(), 0);
        }
        for (ReservaDTO reserva : reservas) {
            if (cuenta(reserva, desde, hasta)) {
                String semana = reserva.getFecha().with(DayOfWeek.MONDAY).toString();
                conteo.merge(semana, 1, Integer::sum);
            }
        }
        return conteo;
    }

    private static boolean cuenta(ReservaDTO reserva, LocalDate desde, LocalDate hasta) {
        return reserva.getEstado() == EstadoReserva.ACTIVA
                && !reserva.getFecha().isBefore(desde)
                && !reserva.getFecha().isAfter(hasta);
    }
}
