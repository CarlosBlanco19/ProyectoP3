package una.sistemareservas.service;
import una.sistemareservas.model.UsuarioDTO;
import una.sistemareservas.model.AdministradorDTO;
import una.sistemareservas.model.FuncionarioDTO;


import una.sistemareservas.utilidades.Busqueda;

import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    private final List<UsuarioDTO> usuarios =  new ArrayList<>();


    public UsuarioService(){
        //CAMBIAR CUANDO SE IMPLEMENTE PERSISTENCIA DE DATOS
        usuarios.add(new AdministradorDTO("admin", "admin123"));
        usuarios.add(new FuncionarioDTO("jdk","1234","Deivert","2112-1212"));

    }


    public UsuarioDTO buscarID(String id){
        return Busqueda.buscarID(usuarios, id, UsuarioDTO::getID);
    }


    public boolean cambiarClave(UsuarioDTO usuario, String claveActual, String nuevaClave){
        if(usuario == null || claveActual == null || nuevaClave== null) {
            return false;
        }

        if(!usuario.getClave().equals(claveActual)){
            return false;
        }
        usuario.setClave(nuevaClave);
        return true;
    }

    public UsuarioDTO autenticar(String id, String clave){
        UsuarioDTO usuario = buscarID(id);
        if(usuario == null){
            return null;
        }

        if(!usuario.getClave().equals(clave)){
            return null;
        }
        return usuario;
    }
}


