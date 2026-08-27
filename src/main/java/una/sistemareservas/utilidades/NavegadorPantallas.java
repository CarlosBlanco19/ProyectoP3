package una.sistemareservas.utilidades;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class NavegadorPantallas {

    private NavegadorPantallas(){
    }

    public static void cambiarPantalla(ActionEvent evento, String archivoFXML){
        try{
            Parent raiz = FXMLLoader.load(NavegadorPantallas.class.getResource(archivoFXML));
            Stage stage = (Stage)((Node)evento.getSource()).getScene().getWindow();
            stage.getScene().setRoot(raiz);
            stage.sizeToScene();
        }catch(IOException e){
            e.printStackTrace(); //CAMBIAR LUEGO
        }
    }
}
