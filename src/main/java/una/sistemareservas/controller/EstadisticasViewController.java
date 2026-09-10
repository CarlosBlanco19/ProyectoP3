package una.sistemareservas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.chart.BarChart;
import una.sistemareservas.dto.CategoriaRecursoDTO;
import una.sistemareservas.dto.EstadoReserva;
import una.sistemareservas.dto.RecursoDTO;
import una.sistemareservas.exception.ReservaException;
import una.sistemareservas.service.CategoriaService;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.service.RecursoService;
import una.sistemareservas.service.ReservaService;
import una.sistemareservas.service.UsuarioService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class EstadisticasViewController {
    @FXML private DatePicker dtDesdeRecursosFecha;
    @FXML private DatePicker dtHastaRecursosFecha;
    @FXML private Button btnCargarRecursos;
    @FXML private TableView<EstadisticaItem> tabEstadisticasRecursos;
    @FXML private TableColumn<EstadisticaItem, String> colEstCategoriaRecursos;
    @FXML private TableColumn<EstadisticaItem, Number> colEstCantidadRecursos;
    @FXML private BarChart<String, Number> bcRecursos;
    @FXML private DatePicker dtDesdeActividadesFecha;
    @FXML private DatePicker dtHastaActividadesFecha;
    @FXML private Button btnCargarActividades;
    @FXML private TableView<EstadisticaItem> tabEstadisticasActividades;
    @FXML private TableColumn<EstadisticaItem, String> colEstSemanaActividades;
    @FXML private TableColumn<EstadisticaItem, Number> colEstCantidadActividades;
    @FXML private BarChart<String, Number> bcActividades;

    private final CategoriaService categoriaService = new CategoriaService();
    private final ObservableList<CategoriaRecursoDTO> datosTablacategorias = FXCollections.observableArrayList();
    private final RecursoService recursoService = new RecursoService(categoriaService);
    private final UsuarioService usuarioService = new UsuarioService();
    private ReservaService reservaService;

    @FXML
    private void initialize() {
        //construccion con exception
        try {
            reservaService = new ReservaService(usuarioService, categoriaService, recursoService);
        } catch (ReservaException e) {
            mostrarAlerta("No se cargaron los datos");
        }

        //Configura columnas de tabla de Recursos
        colEstCategoriaRecursos.setCellValueFactory(cell -> cell.getValue().nombreProperty());
        colEstCantidadRecursos.setCellValueFactory(cell -> cell.getValue().cantidadProperty());

        //Configura columnas de tabla de Actividades
        colEstSemanaActividades.setCellValueFactory(cell -> cell.getValue().nombreProperty());
        colEstCantidadActividades.setCellValueFactory(cell -> cell.getValue().cantidadProperty());

        btnCargarRecursos.setOnAction(this::cargarEstadisticasRecursos);
        btnCargarActividades.setOnAction(this::cargarEstadisticasActividades);

        //Todo esto es para que el grafico no salga bugueado.
        bcRecursos.setAnimated(false);
        bcActividades.setAnimated(false);
        if (bcRecursos.getXAxis() instanceof javafx.scene.chart.CategoryAxis) {
            ((javafx.scene.chart.CategoryAxis) bcRecursos.getXAxis()).setTickLabelRotation(0);
        }
        bcRecursos.setCategoryGap(40);
        bcActividades.setCategoryGap(40);

    }

    private void cargarEstadisticasRecursos(ActionEvent event) {

        LocalDate desde = dtDesdeRecursosFecha.getValue();
        LocalDate hasta = dtHastaRecursosFecha.getValue();

        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            mostrarAlerta("Por favor seleccione un rango de fechas válido.");
            return;
        }

        // Mapa para contar: "Nombre de la Categoria" -> Cantidad
        Map<String, Integer> conteoCategorias = new HashMap<>();

        if (reservaService != null) {
            for (ReservaDTO reserva : reservaService.getReservas()) {
                if (reserva.getEstado() == EstadoReserva.ACTIVA
                        && !reserva.getFecha().isBefore(desde)
                        && !reserva.getFecha().isAfter(hasta)) {

                    // Cuenta cada recurso por su categoría
                    for (RecursoDTO recurso : reserva.getRecursos()) {
                        String nombreCat = recurso.getCategoria().getDescripcion();
                        conteoCategorias.put(nombreCat, conteoCategorias.getOrDefault(nombreCat, 0) + 1);
                    }
                }
            }
        }

        actualizarTablaYGrafico(tabEstadisticasRecursos, bcRecursos, conteoCategorias, "Recurso");
    }

    private void cargarEstadisticasActividades(ActionEvent event) {
        LocalDate desde = dtDesdeActividadesFecha.getValue();
        LocalDate hasta = dtHastaActividadesFecha.getValue();

        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            mostrarAlerta("Por favor seleccione un rango de fechas válido.");
            return;
        }

        Map<String, Integer> conteoSemanas = new HashMap<>();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (reservaService != null) {
            for (ReservaDTO reserva : reservaService.getReservas()) {
                if (reserva.getEstado() == EstadoReserva.ACTIVA
                        && !reserva.getFecha().isBefore(desde)
                        && !reserva.getFecha().isAfter(hasta)) {

                    // Saca el lunes de la semana de la reserva para agrupar por SEMANAS
                    LocalDate lunesSemana = reserva.getFecha().with(DayOfWeek.MONDAY);
                    String semanaStr = lunesSemana.format(formato);

                    conteoSemanas.put(semanaStr, conteoSemanas.getOrDefault(semanaStr, 0) + 1);
                }
            }
        }
        actualizarTablaYGrafico(tabEstadisticasActividades, bcActividades, conteoSemanas, "Semana");
    }

    private void actualizarTablaYGrafico(TableView<EstadisticaItem> tabla, BarChart<String, Number> grafico,
                                         Map<String, Integer> datos, String nombreSerie) {
        ObservableList<EstadisticaItem> listaItems = FXCollections.observableArrayList();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName(nombreSerie);
        for (Map.Entry<String, Integer> entrada : datos.entrySet()) {
            listaItems.add(new EstadisticaItem(entrada.getKey(), entrada.getValue()));
            serie.getData().add(new XYChart.Data<>(entrada.getKey(), entrada.getValue()));
        }

        // Actualiza Tabla
        tabla.setItems(listaItems);

        // Limpia y actualiza le grafico
        grafico.getData().clear();
        grafico.layout();
        grafico.getData().add(serie);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    //Esta clase hace que la tabla pueda mostrar los datos de manera correcta.
    public static class EstadisticaItem {
        private final javafx.beans.property.SimpleStringProperty nombre;
        private final javafx.beans.property.SimpleIntegerProperty cantidad;

        public EstadisticaItem(String nombre, int cantidad) {
            this.nombre = new javafx.beans.property.SimpleStringProperty(nombre);
            this.cantidad = new javafx.beans.property.SimpleIntegerProperty(cantidad);
        }

        public javafx.beans.property.SimpleStringProperty nombreProperty() { return nombre; }
        public javafx.beans.property.SimpleIntegerProperty cantidadProperty() { return cantidad; }
    }

}
