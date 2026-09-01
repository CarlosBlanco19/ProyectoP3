package una.sistemareservas.service;

import una.sistemareservas.model.FuncionarioDTO;
import una.sistemareservas.model.UsuarioDTO;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioService {

    private final UsuarioService usuarioService;

    public FuncionarioService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public boolean eliminar(String id){
        FuncionarioDTO user = buscarID(id);
        if(user == null){
            return false;
        }
        return usuarioService.eliminar(id);
    }

    public boolean agregar(String id, String telefono, String nombre){

        if(id == null || nombre.isBlank() || id.isBlank() || nombre == null){
            return false;
        }
        FuncionarioDTO funcionario = new FuncionarioDTO(id, nombre, telefono);
        return usuarioService.agregar(funcionario);
    }

    public List<FuncionarioDTO> lsitar(){
        return usuarioService.listarFuncionarios();
    }


    public FuncionarioDTO buscarID(String id){
        UsuarioDTO usuario = usuarioService.buscarID(id);
        if(usuario instanceof FuncionarioDTO){
            return (FuncionarioDTO) usuario;
        }
        return null;
    }

    public List<FuncionarioDTO> buscarNombre(String nombre){
        List<FuncionarioDTO> res = new ArrayList<>(); // lista para ir guardado coincidencias

        if(nombre == null || nombre.isBlank()){
            return res; //list vacia
        }

        String busqueda = nombre.toLowerCase(); // para no distinguir minusculas y mayusculas
        for(FuncionarioDTO funcionario : usuarioService.listarFuncionarios()) {
            if (funcionario.getNombre().toLowerCase().contains(busqueda)) { //buscar si contiene el texto buscado
                res.add(funcionario);
            }
        }
        return res;
    }

}
