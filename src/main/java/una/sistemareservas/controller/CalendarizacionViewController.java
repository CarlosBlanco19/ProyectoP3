package una.sistemareservas.controller;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.EstadoReserva;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.exception.ReservaException;
import una.sistemareservas.service.CategoriaService;
import una.sistemareservas.service.RecursoService;
import una.sistemareservas.service.ReservaService;
import una.sistemareservas.service.UsuarioService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarizacionViewController {

    @FXML private Button btnBuscarCalendarizacion;
    @FXML private Button btnImprimirCalendarizacion;
    @FXML private DatePicker dtCalendarizacion;
    @FXML private ComboBox<CategoriaRecursoDTO> cbCategoriaCalendarizacion;
    //map hace dinamicas las columnas segun la cantidad de recursos en cada hora
    @FXML private TableView<Map<String, String>> tabCalendarizacion;
    @FXML private TableColumn<Map<String,String>, String> colHoraCalendarizacion;
    @FXML private Label lblAvisos;

    private final CategoriaService categoriaService = new CategoriaService();
    private final UsuarioService usuarioService = new UsuarioService();
    private ReservaService reservaService;

    private final RecursoService recursoService   = new RecursoService(categoriaService);

    @FXML
    private void initialize(){

        //construccion con exception
        try{
            reservaService = new ReservaService(usuarioService,categoriaService, recursoService);

        }catch(ReservaException e){
            lblAvisos.setText("No se cargaron los datos");

        }
        //saca la data de hora
        colHoraCalendarizacion.setCellValueFactory(cellDato -> new SimpleStringProperty(cellDato.getValue().get("hora")));

        cbCategoriaCalendarizacion.setItems(FXCollections.observableArrayList(categoriaService.listar()));

        btnBuscarCalendarizacion.setOnAction(this::buscarCalendarizacion);
    }



    private void defColumna(List<RecursoDTO> recursos){
        //limpia toda data excepto horas
        tabCalendarizacion.getColumns().setAll(colHoraCalendarizacion);

        for(RecursoDTO recurso : recursos){
            TableColumn<Map<String,String>, String> colRecurso = new TableColumn<>(recurso.getID());
            colRecurso.setCellValueFactory(celDato -> new SimpleStringProperty(celDato.getValue().get(recurso.getID())));
            tabCalendarizacion.getColumns().add(colRecurso);
        }
    }

    private String txtCelda(RecursoDTO recurso, LocalDate date, LocalTime hora){
        for(ReservaDTO reserva : reservaService.getReservas()){
            //descarta inactivas, distintas fechas y recursos
            if(reserva.getEstado() != EstadoReserva.ACTIVA){continue;}
            if(!reserva.getFecha().equals(date)){continue;}
            if(!reserva.getRecursos().contains(recurso)){continue;}

            //verifica horas para devolver la actividad y el nombre txt
            if(!hora.isBefore(reserva.getHora_init()) && hora.isBefore(reserva.getHora_final())){

                return reserva.getActividad() + "-" + reserva.getFuncionario().getNombre();
            }
        }
        return "";
    }

    private void defFila(LocalDate time, List<RecursoDTO> recursos){

        ObservableList<Map<String, String>> filas = FXCollections.observableArrayList();

        //se arma una fila por cada hora
        for(int hora = 7; hora <= 23; hora++) {

            LocalTime tActual = LocalTime.of(hora, 0);
            Map<String, String> fila = new HashMap<>();
            fila.put("hora", String.format("%02d:00", hora));

            //por cada recurso hay una entrada
            //trae el texto de la celda (recurso, hora o fecha)
            for (RecursoDTO recurso : recursos) {
                fila.put(recurso.getID(), txtCelda(recurso, time, tActual));
            }
            filas.add(fila);
        }
        tabCalendarizacion.setItems(filas);
    }


    private void buscarCalendarizacion(ActionEvent event){
        if(reservaService == null){
            lblAvisos.setText("No se cargaron las reservas");
            return;
        }

        LocalDate fecha = dtCalendarizacion.getValue();
        CategoriaRecursoDTO categoria = cbCategoriaCalendarizacion.getValue();

        if(categoria == null || fecha == null){
            lblAvisos.setText("Debe seleccionar una fecha y categoria");
            return;
        }

        List<RecursoDTO> recursos= recursoService.listarPorCategoria(categoria.getID());
        defColumna(recursos);
        defFila(fecha,recursos);
    }
}
