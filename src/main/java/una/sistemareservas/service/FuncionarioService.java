package una.sistemareservas.service;

import una.sistemareservas.logic.FuncionarioLogic;
import una.sistemareservas.dto.FuncionarioDTO;

import java.util.List;

public class FuncionarioService {
    private final FuncionarioLogic funcionarioLogic;

    public FuncionarioService(UsuarioService usuarioService) {
        this.funcionarioLogic = new FuncionarioLogic(usuarioService.logic());
    }

    public List<FuncionarioDTO> listar() {
        return funcionarioLogic.listar();
    }

    public FuncionarioDTO buscarID(String id) {
        return funcionarioLogic.buscarID(id);
    }

    public List<FuncionarioDTO> buscarPorNombre(String nom) {
        return funcionarioLogic.buscarNombre(nom);
    }

    public boolean agregar(String id,String nom,String tel) {
        return funcionarioLogic.agregar(id, nom, tel);
    }

    public boolean eliminar(String id) {
        return funcionarioLogic.eliminar(id);
    }
}
