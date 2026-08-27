package una.sistemareservas.service;
import una.sistemareservas.model.Usuario;
import una.sistemareservas.model.Administrador;
import una.sistemareservas.model.Funcionario;


import una.sistemareservas.utilidades.Busqueda;

import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    private final List<Usuario> usuarios =  new ArrayList<>();


    public UsuarioService(){
        //CAMBIAR CUANDO SE IMPLEMENTE PERSISTENCIA DE DATOS
        usuarios.add(new Administrador("admin", "admin123"));
        usuarios.add(new Funcionario("jdk","1234","Deivert","2112-1212"));

    }


    public Usuario buscarID(String id){
        return Busqueda.buscarID(usuarios, id, Usuario::getID);
    }


    public boolean cambiarClave(Usuario usuario, String claveActual, String nuevaClave){
        if(usuario == null || claveActual == null || nuevaClave== null) {
            return false;
        }

        if(!usuario.getClave().equals(claveActual)){
            return false;
        }
        usuario.setClave(nuevaClave);
        return true;
    }

    public Usuario autenticar(String id, String clave){
        Usuario usuario = buscarID(id);
        if(usuario == null){
            return null;
        }

        if(!usuario.getClave().equals(clave)){
            return null;
        }
        return usuario;
    }
}


