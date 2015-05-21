import gui.controller.InteractController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.WorkSpace;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
       FXMLLoader loader = new FXMLLoader(
             Main.class.getResource("gui/view/interact.fxml"));

       primaryStage.setTitle("Neural Network Trainer");
       primaryStage.setScene(new Scene((Parent)loader.load()));

       InteractController controller = loader.getController();
       controller.setStage(primaryStage);

       WorkSpace.instance.loadNetworks();
       primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
