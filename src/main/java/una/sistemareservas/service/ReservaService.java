package una.sistemareservas.service;

import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.exception.RecursoException;
import una.sistemareservas.exception.ReservaException;
import una.sistemareservas.exception.UsuarioException;
import una.sistemareservas.logic.ReservaLogic;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservaService {

    private final ReservaLogic reservaLogic;

    public ReservaService(UsuarioService usuarioService, CategoriaService categoriaService, RecursoService recursoService) throws ReservaException {
        this.reservaLogic = new ReservaLogic(categoriaService.logic(), recursoService.logic(), usuarioService.logic());
    }

    public ReservaDTO reservar(String funcionarioId, String actividad, LocalDate fecha, LocalTime hora_init, LocalTime hora_final, List<CategoriaRecursoDTO> categorias) throws RecursoException, UsuarioException, ReservaException {
        return reservaLogic.reservar(funcionarioId, actividad, fecha, hora_init, hora_final, categorias);
    }

    public void cancelar(String idReserva) throws ReservaException {
        reservaLogic.cancelar(idReserva);
    }

    public List<ReservaDTO> listarPorFuncionario(String funcionarioId) {
        return reservaLogic.listarPorFuncionario(funcionarioId);
    }

    public ReservaDTO buscarPorID(String id) {
        return reservaLogic.buscarPorID(id);
    }

    public List<ReservaDTO> getReservas() {
        return reservaLogic.getReservas();
    }
}
