import gui.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
       Parent root = FXMLLoader.load(
             Main.class.getResource("gui/view/main.fxml"));
       primaryStage.setTitle("Load Network");
       primaryStage.setScene(new Scene(root));

       MainController.setStage(primaryStage);
       MainController.openLoader();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
