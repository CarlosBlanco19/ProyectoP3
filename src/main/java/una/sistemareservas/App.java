package una.sistemareservas;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import una.sistemareservas.model.AlertasModel;
import una.sistemareservas.proxy.ServiceProxy;

public class App extends Application {

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        // Las reservas nuevas que avisa el Backend llegan por el hilo del socket.
        // Platform.runLater las pasa al hilo de JavaFX, que es el único que puede tocar la pantalla.
        ServiceProxy.instancia().setOyenteReservas(reserva ->
                Platform.runLater(() -> AlertasModel.getInstancia().agregarAlerta(reserva)));

        Parent raiz = FXMLLoader.load(getClass().getResource("/ui/LogInView.fxml"));
        escenarioPrincipal.setTitle("Sistema de Reservas");
        escenarioPrincipal.getIcons().add(new Image(getClass().getResourceAsStream("/iconos/reserva.png")));
        escenarioPrincipal.setScene(new Scene(raiz));
        escenarioPrincipal.show();
    }

    // Al cerrar la ventana se le avisa al Backend y se cierran los sockets
    @Override
    public void stop() {
        ServiceProxy.instancia().desconectar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
