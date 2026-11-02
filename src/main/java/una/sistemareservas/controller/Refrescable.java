package una.sistemareservas.controller;


//Pantallas que vuelven a pedirle los datos al Backend cuando el usuario entra a su pestaña
//si otra persona agregó una categoría o un recurso, se ve sin tener que reiniciar el programa

public interface Refrescable {
    void refrescar();
}
