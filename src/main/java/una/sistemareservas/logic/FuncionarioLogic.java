package una.sistemareservas.logic;

import una.sistemareservas.dto.FuncionarioDTO;
import una.sistemareservas.dto.UsuarioDTO;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioLogic {

    private final UsuarioLogic usuarioLogic;

    public FuncionarioLogic(UsuarioLogic usuarioLogic) {
        this.usuarioLogic = usuarioLogic;
    }

    public boolean eliminar(String id){
        FuncionarioDTO user = buscarID(id);
        if(user == null){
            return false;
        }
        return usuarioLogic.eliminar(id);
    }

    public boolean agregar(String id, String telefono, String nombre){

        if(id == null || nombre.isBlank() || id.isBlank() || nombre == null){
            return false;
        }
        FuncionarioDTO funcionario = new FuncionarioDTO(id, nombre, telefono);
        return usuarioLogic.agregar(funcionario);
    }

    public List<FuncionarioDTO> listar(){
        return usuarioLogic.listarFuncionarios();
    }


    public FuncionarioDTO buscarID(String id){
        UsuarioDTO usuario = usuarioLogic.buscarID(id);
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
        for(FuncionarioDTO funcionario : usuarioLogic.listarFuncionarios()) {
            if (funcionario.getNombre().toLowerCase().contains(busqueda)) { //buscar si contiene el texto buscado
                res.add(funcionario);
            }
        }
        return res;
    }

}
