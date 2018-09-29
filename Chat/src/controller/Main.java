package controller;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import view.HomeController;

public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws IOException {
       FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/home.fxml"));
       Parent root = loader.load();
       Scene scene = new Scene(root);
       stage.setScene(scene);
       stage.setResizable(false);
       stage.sizeToScene();
       stage.setTitle("CHÁ TCHÊ");

       HomeController controller = loader.getController();
       stage.setOnHidden(e -> controller.shutdown());
       stage.getIcons().add(new Image("/view/chimarrao.png"));
       stage.show();
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
    
}
