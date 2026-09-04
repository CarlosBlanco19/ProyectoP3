package una.sistemareservas.dto;

import java.util.List;
import una.sistemareservas.dto.ReservaDTO;
import java.util.List;
import java.util.ArrayList;

public class FuncionarioDTO extends UsuarioDTO {
    // Usuario FUNCIONARIO, hereda de usuario
    private String Nombre;
    private String Telefono;
    //private List<ReservaDTO> reservas;
    // Atributos extra requeridos por Funcionario

    public FuncionarioDTO(String ID, String Clave, String Nombre, String Telefono) { // Constructor base
        super(ID, Clave, Rol.FUNCIONARIO);
        this.Nombre = Nombre;
        this.Telefono = Telefono;
        //this.reservas = new ArrayList<>();
    }

    public FuncionarioDTO(String ID, String Nombre, String Telefono) { // Constructor pero sin necesidad de recibir una clave, llama al constructor original pero mandando el id como clave
        this(ID, ID, Nombre, Telefono);
    }

    // GETS
    public String getNombre() {
        return Nombre;
    }
    public String getTelefono() {
        return Telefono;
    }
    //public List<ReservaDTO> getReservas() {
    //    return reservas;
    //}

    // SETS
    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }
    public void setTelefono(String Telefono) {
        this.Telefono = Telefono;
    }
    //public void setReservas(List<ReservaDTO> reservas) {this.reservas = reservas;}
}
