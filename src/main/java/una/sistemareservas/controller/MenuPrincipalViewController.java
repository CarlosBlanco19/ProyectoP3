package una.sistemareservas.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import una.sistemareservas.dto.ReservaDTO;
import una.sistemareservas.dto.Rol;
import una.sistemareservas.dto.UsuarioDTO;
import una.sistemareservas.model.AlertasModel;
import una.sistemareservas.utilidades.Dialogos;
import una.sistemareservas.utilidades.SesionGlobal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MenuPrincipalViewController {

    @FXML private TabPane tabPaneMenuPrincipal;
    @FXML private Tab tabReservas;
    @FXML private Tab tabFuncionarios;
    @FXML private Tab tabCategorias;
    @FXML private Tab tabRecursos;
    @FXML private Tab tabCalendarizacion;
    @FXML private Tab tabActividades;
    @FXML private Tab tabEstadisticas;

    // Controladores de cada pestaña (JavaFX los inyecta por el fx:id del fx:include + "Controller")
    @FXML private ReservasViewController vistaReservasController;
    @FXML private FuncionariosViewController vistaFuncionariosController;
    @FXML private CategoriasViewController vistaCategoriasController;
    @FXML private RecursosViewController vistaRecursosController;
    @FXML private CalendarizacionViewController vistaCalendarizacionController;
    @FXML private ActividadesViewController vistaActividadesController;
    @FXML private EstadisticasViewController vistaEstadisticasController;

    // ---- Área de alertas ----
    @FXML private Label lblOpciones;
    @FXML private TableView<ReservaDTO> tabAlertas;
    @FXML private TableColumn<ReservaDTO, String> colAlertaActividad;
    @FXML private TableColumn<ReservaDTO, ReservaDTO> colAlertaVer;
    @FXML private TableColumn<ReservaDTO, ReservaDTO> colAlertaDescartar;

    private final AlertasModel modeloAlertas = AlertasModel.getInstancia();
    private final Map<Tab, Refrescable> pantallas = new HashMap<>();

    @FXML
    private void initialize() {
        UsuarioDTO usuario = SesionGlobal.getUsuario();
        if (usuario != null) {
            configurarAccesos(usuario);
            lblOpciones.setText("Opciones: " + (usuario.getRol() == Rol.ADMINISTRADOR ? "Administrador" : "Funcionario")
                    + " - " + SesionGlobal.getNombre());
        }
        configurarTablaAlertas();
        configurarRefresco();
    }

    private void configurarAccesos(UsuarioDTO usuario) {
        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            tabPaneMenuPrincipal.getTabs().remove(tabReservas);
        } else {
            tabPaneMenuPrincipal.getTabs().removeAll(tabCategorias, tabFuncionarios, tabRecursos);
        }
    }

    // Cada vez que el usuario entra a una pestaña, esa pantalla le vuelve a pedir los datos al Backend
    private void configurarRefresco() {
        pantallas.put(tabReservas, vistaReservasController);
        pantallas.put(tabFuncionarios, vistaFuncionariosController);
        pantallas.put(tabCategorias, vistaCategoriasController);
        pantallas.put(tabRecursos, vistaRecursosController);
        pantallas.put(tabCalendarizacion, vistaCalendarizacionController);
        pantallas.put(tabActividades, vistaActividadesController);
        pantallas.put(tabEstadisticas, vistaEstadisticasController);

        tabPaneMenuPrincipal.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nueva) -> refrescar(nueva));
        refrescar(tabPaneMenuPrincipal.getSelectionModel().getSelectedItem());
    }

    private void refrescar(Tab tab) {
        Refrescable pantalla = pantallas.get(tab);
        if (pantalla != null) {
            pantalla.refrescar();
        }
    }

    // ===================================================================
    //                          ÁREA DE ALERTAS
    // ===================================================================

    private void configurarTablaAlertas() {
        // La tabla queda enlazada a la lista del modelo: lo que se agregue o quite allí se ve aquí
        tabAlertas.setItems(modeloAlertas.getAlertas());

        // Columna "Actividad":  "Juan Mora - Reunion clientes"
        colAlertaActividad.setCellValueFactory(fila -> {
            ReservaDTO reserva = fila.getValue();
            String nombre = reserva.getFuncionario() != null ? reserva.getFuncionario().getNombre() : "?";
            return new SimpleStringProperty(nombre + " - " + reserva.getActividad());
        });

        // Columnas con íconos (Scene Builder no puede poner un botón en cada fila, se hace aquí)
        configurarColumnaIcono(colAlertaVer, "/iconos/verAlerta.png", "Ver alerta", this::verAlerta);
        configurarColumnaIcono(colAlertaDescartar, "/iconos/descartarAlerta.png", "Descartar alerta", this::descartarAlerta);
    }

    /** Pone en cada fila de la columna un botón transparente con un ícono. */
    private void configurarColumnaIcono(TableColumn<ReservaDTO, ReservaDTO> columna, String rutaIcono,
                                        String textoAyuda, Consumer<ReservaDTO> accion) {
        Image icono = new Image(getClass().getResourceAsStream(rutaIcono));

        // Cada celda recibe la reserva completa de su fila
        columna.setCellValueFactory(fila -> new ReadOnlyObjectWrapper<>(fila.getValue()));

        columna.setCellFactory(col -> new TableCell<>() {
            private final Button boton = new Button();

            {
                ImageView imagen = new ImageView(icono);
                imagen.setFitWidth(18);
                imagen.setFitHeight(18);
                imagen.setPreserveRatio(true);
                imagen.setSmooth(true);
                boton.setGraphic(imagen);
                boton.setTooltip(new Tooltip(textoAyuda));
                boton.setStyle("-fx-background-color: transparent; -fx-padding: 2; -fx-cursor: hand;");
                boton.setOnAction(evento -> {
                    ReservaDTO reserva = getItem();
                    if (reserva != null) {
                        accion.accept(reserva);
                    }
                });
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(ReservaDTO reserva, boolean vacia) {
                super.updateItem(reserva, vacia);
                setGraphic(vacia || reserva == null ? null : boton);
            }
        });
    }

    /** Ícono de carpeta: abre la ventana emergente con el detalle. */
    private void verAlerta(ReservaDTO reserva) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/ui/AlertaDetalleView.fxml"));
            Parent raiz = cargador.load();

            AlertaDetalleViewController controlador = cargador.getController();
            controlador.mostrarReserva(reserva);

            Stage ventana = new Stage();
            ventana.setTitle(reserva.getActividad());
            ventana.getIcons().add(new Image(getClass().getResourceAsStream("/iconos/reserva.png")));
            ventana.initOwner(tabAlertas.getScene().getWindow());
            ventana.initModality(Modality.WINDOW_MODAL);
            ventana.setResizable(false);
            ventana.setScene(new Scene(raiz));
            ventana.showAndWait();
        } catch (IOException e) {
            Dialogos.error("No se pudo abrir el detalle de la alerta.");
        }
    }

    /** Ícono rojo: pide confirmación y quita la alerta SOLO de la lista local (no se avisa al Backend). */
    private void descartarAlerta(ReservaDTO reserva) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, reserva.getActividad(), ButtonType.YES, ButtonType.NO);
        confirmacion.setTitle("Descartar?");
        confirmacion.setHeaderText(null);
        confirmacion.initOwner(tabAlertas.getScene().getWindow());

        confirmacion.showAndWait()
                .filter(respuesta -> respuesta == ButtonType.YES)
                .ifPresent(respuesta -> modeloAlertas.descartarAlerta(reserva));
    }
}
