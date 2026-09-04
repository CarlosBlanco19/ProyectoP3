package una.sistemareservas.service;

import una.sistemareservas.logic.UsuarioLogic;
import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.UsuarioDTO;

import java.util.List;

public class UsuarioService {

    private final UsuarioLogic usuarioLogic = new UsuarioLogic();


    public UsuarioDTO buscarID(String id){
        return usuarioLogic.buscarID(id);
    }

    public UsuarioDTO autenticar(String id, String clave){
        return usuarioLogic.autenticar(id, clave);
    }

    public boolean cambiarClave(UsuarioDTO user,String claveAct, String claveNew){
        return usuarioLogic.cambiarClave(user,claveAct,claveNew);
    }

    public List<UsuarioDTO> listar(){
        return usuarioLogic.listar();
    }


    public List<FuncionarioDTO> listarFuncionarios(){
        return usuarioLogic.listarFuncionarios();
    }

    public boolean agregar(UsuarioDTO user){
        return usuarioLogic.agregar(user);
    }

    public boolean eliminar(String id){
        return usuarioLogic.eliminar(id);
    }


    //esto es para no leer archivos varias veces por separado
    //ayuda a compartir la misma memoria
    public UsuarioLogic logic(){
        return usuarioLogic;
    }
}

