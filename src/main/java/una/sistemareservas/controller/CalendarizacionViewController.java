package una.sistemareservas.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
        btnImprimirCalendarizacion.setOnAction(this::imprimirCalendarizacion);
    }



    private void defColumna(List<RecursoDTO> recursos){
        //limpia toda data excepto horas
        tabCalendarizacion.getColumns().setAll(colHoraCalendarizacion);

        for(RecursoDTO recurso : recursos){
            TableColumn<Map<String,String>, String> colRecurso = new TableColumn<>(recurso.getID());
            colRecurso.setCellValueFactory(celDato -> new SimpleStringProperty(celDato.getValue().get(recurso.getID())));
            //Esto es para colorear las celdas.
            colRecurso.setCellFactory(columna -> new javafx.scene.control.TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.trim().isEmpty()) {
                        setText("");
                        setStyle(""); // Blanco por defecto
                    } else {
                        setText(item);
                        // Fondo amarillo pastel, igual al de la imagen
                        setStyle("-fx-background-color: #fff2cc; -fx-border-color: lightgray; -fx-border-width: 0.5px; -fx-alignment: center-left;");
                    }
                }
            });

            tabCalendarizacion.getColumns().add(colRecurso);
        }
    }

    private String txtCelda(RecursoDTO recurso, LocalDate date, LocalTime hora){
        for(ReservaDTO reserva : reservaService.getReservas()){
            //quita inactivas, distintas fechas y recursos
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

    @FXML
    private void imprimirCalendarizacion(ActionEvent event) {
        // tira exception si no hay data cargada.
        if (dtCalendarizacion.getValue() == null || tabCalendarizacion.getItems().isEmpty()) {
            mostrarMensaje("Advertencia", "Debe buscar una calendarización antes de imprimir.");
            return;
        }
        //Esto es para que le salga para ver donde guardar el archivo y con que nombre en la com´pu.
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar Reporte de Calendarización");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("Calendarizacion_" + dtCalendarizacion.getValue() + ".pdf");

        java.io.File file = fileChooser.showSaveDialog(btnImprimirCalendarizacion.getScene().getWindow());

        if (file != null) {
            try {
                com.itextpdf.text.Document documento = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4.rotate());
                com.itextpdf.text.pdf.PdfWriter.getInstance(documento, new java.io.FileOutputStream(file));
                documento.open();

                // Títulos del PDF
                documento.add(new com.itextpdf.text.Paragraph("Reporte de Calendarizacion de Recursos"));
                documento.add(new com.itextpdf.text.Paragraph("Fecha: " + dtCalendarizacion.getValue() + " | Categoria: " + cbCategoriaCalendarizacion.getValue().getDescripcion()));
                documento.add(new com.itextpdf.text.Paragraph(" "));

                // Cuantas columnas tenga la tabla en JavaFX, tendrá la del PDF
                int numColumnas = tabCalendarizacion.getColumns().size();
                com.itextpdf.text.pdf.PdfPTable tablaPdf = new com.itextpdf.text.pdf.PdfPTable(numColumnas);
                tablaPdf.setWidthPercentage(100);

                // 1. Imprimir encabezados
                for (TableColumn<Map<String, String>, ?> col : tabCalendarizacion.getColumns()) {
                    com.itextpdf.text.pdf.PdfPCell celda = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(col.getText()));
                    celda.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
                    tablaPdf.addCell(celda);
                }

                // 2. Imprimir contenido fila por fila
                for (Map<String, String> fila : tabCalendarizacion.getItems()) {
                    for (TableColumn<Map<String, String>, ?> col : tabCalendarizacion.getColumns()) {
                        // Comprobamos si es la columna de "Hora" o una de recurso
                        String key = (col == colHoraCalendarizacion) ? "hora" : col.getText();
                        String valor = fila.get(key);

                        tablaPdf.addCell(valor == null || valor.isEmpty() ? " " : valor);
                    }
                }

                documento.add(tablaPdf);
                documento.close();

                mostrarMensaje("Éxito", "¡Reporte PDF generado exitosamente!");

            } catch (Exception e) {
                mostrarMensaje("Error", "Error al guardar el PDF: " + e.getMessage());
            }
        }
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
