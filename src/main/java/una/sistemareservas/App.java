package una.sistemareservas;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class App extends Application {

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {

        Parent raiz = FXMLLoader.load(getClass().getResource("/ui/LogInView.fxml"));

        }

        public static void main(String[] args){ launch(args);}
}
