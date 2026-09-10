package una.sistemareservas.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.EstadoReserva;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.exception.ReservaException;
import una.sistemareservas.service.CategoriaService;
import una.sistemareservas.service.RecursoService;
import una.sistemareservas.service.ReservaService;
import una.sistemareservas.service.UsuarioService;

import java.io.File;
import java.io.FileOutputStream;
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
        btnBuscarCalendarizacion.setOnAction(this::buscarCalendarizacion);

        //construccion con exception
        try{
            reservaService = new ReservaService(usuarioService,categoriaService, recursoService);

        }catch(ReservaException e){
            lblAvisos.setText("No se cargaron los datos");

        }
        //saca la data de hora
        colHoraCalendarizacion.setCellValueFactory(cellDato -> new SimpleStringProperty(cellDato.getValue().get("hora")));

        cbCategoriaCalendarizacion.setItems(FXCollections.observableArrayList(categoriaService.listar()));

    }



    private void defColumna(List<RecursoDTO> recursos){

        //limpia toda ddata excepto horas
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
        for(int hora = 7; hora <= 23; hora++){

            LocalTime tActual = LocalTime.of(hora, 0);
            Map<String,String> fila = new HashMap<>();
            fila.put("hora", String.format("%02d:00", hora));

            //por cada recurso hay una entrada
            //trae el texto de la celda (recurso, hora o fecha)
            for(RecursoDTO recurso : recursos){
                fila.put(recurso.getID(), txtCelda(recurso,time, tActual));
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

        List<RecursoDTO> recuross= recursoService.listarPorCategoria(categoria.getID());
        defColumna(recuross);
        defFila(fecha,recuross);
    }
    /*

    private void setBtnImprimirCalendarizacion(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Actividades");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

        fileChooser.setInitialFileName("Reporte_Actividades_" + dtCalendarizacion.getValue() + ".pdf");

        File file = fileChooser.showSaveDialog(btnImprimirCalendarizacion.getScene().getWindow());

        if (file != null) {
            try {
                Document documento = new Document(PageSize.A4);
                PdfWriter.getInstance(documento, new FileOutputStream(file));
                documento.open();

                documento.add(new Paragraph("Reporte de Caledarizacion"));
                documento.add(new Paragraph("Fecha de referencia: " + dtCalendarizacion.getValue()));
                documento.add(new Paragraph(" ")); // Espacio en blanco

                // 4. Crea la tabla ahi
                PdfPTable tablaPdf = new PdfPTable(8);
                tablaPdf.setWidthPercentage(100);

                // le pone titulos a las columnas
                String[] encabezados = {"Hora", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
                for (String encabezado : encabezados) {
                    PdfPCell celda = new PdfPCell(new Phrase(encabezado));
                    celda.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200)); // Fondo gris claro
                    tablaPdf.addCell(celda);
                }

                // 5. Recorre las filas de la tabla de JavaFX y agrega los datos a la tabla PDF
                for (CalendarizacionViewController.map fila : tabCalendarizacion.getItems()) {
                    tablaPdf.addCell(fila.getHora());

                    for (int i = 0; i < 7; i++) {
                        String textoActividad = fila.getDia(i);
                        // Si está vacío, ponemos un espacio para que la celda se dibuje bien
                        tablaPdf.addCell(textoActividad.isEmpty() ? " " : textoActividad);
                    }
                }

                documento.add(tablaPdf);
                documento.close();

                mostrarAlerta("¡El reporte PDF se ha guardado exitosamente!");

            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Ocurrió un error al generar el PDF: " + e.getMessage());
            }
        }
    }
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

     */
}
