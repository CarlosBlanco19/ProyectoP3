package una.sistemareservas.logic;
import una.sistemareservas.datos.UsuarioDatos;
import una.sistemareservas.dto.UsuarioDTO;
import una.sistemareservas.dto.AdministradorDTO;
import una.sistemareservas.dto.FuncionarioDTO;


import una.sistemareservas.utilidades.Busqueda;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class UsuarioLogic {

    private List<UsuarioDTO> usuarios =  new ArrayList<>();
    private final UsuarioDatos usuarioDatos = new UsuarioDatos();

    private void guardar(){
        try{
            usuarioDatos.deserializar(usuarios);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public UsuarioLogic(){
        List<UsuarioDTO> listos;
        try{
            listos = usuarioDatos.serializar();
        }catch (IOException e){
            e.printStackTrace();
            listos = new ArrayList<>();
        }
        usuarios = listos;

        if(usuarios.isEmpty()){
            usuarios.add(new AdministradorDTO("admin","1234")); //agrega un admin predeterminado
            guardar();
        }
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
        guardar();
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

    public boolean agregar(UsuarioDTO usuario){
        if(usuario == null || buscarID(usuario.getID()) != null){
            return false;
        }
        usuarios.add(usuario);
        guardar();
        return true;
    }

    public boolean eliminar (String id){
        UsuarioDTO usuario = buscarID(id);
        if(usuario == null) {
            return false;
        }

        usuarios.remove(usuario);
        guardar();
        return true;
    }

    public List<UsuarioDTO> listar(){
        return usuarios;
    }

    public List<FuncionarioDTO> listarFuncionarios(){
        List<FuncionarioDTO> funcionarios = new ArrayList<>();
        for(UsuarioDTO usuario : usuarios) {
            if (usuario instanceof FuncionarioDTO) {
                funcionarios.add((FuncionarioDTO) usuario);
            }
        }
        return funcionarios;
     }

}


