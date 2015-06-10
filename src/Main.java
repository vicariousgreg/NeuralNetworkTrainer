import application.FileManager;
import gui.controller.NetworkControllerStack;
import gui.controller.component.NetworkSelectorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.geneticAlgorithm.GeneticAlgorithm;
import model.geneticAlgorithm.NetworkGeneticAdapter;
import model.network.Network;
import model.network.memory.Memory;
import model.network.schema.ColorInputAdapter;
import model.network.schema.Schema;

import java.util.List;

public class Main extends Application {
   @Override
   public void start(Stage primaryStage) throws Exception {
      //genAlgTest();

      FXMLLoader loader = new FXMLLoader(
            Main.class.getResource("gui/view/main.fxml"));

      primaryStage.setTitle("Neural Network Trainer");
      primaryStage.setScene(new Scene((Parent)loader.load()));
      NetworkControllerStack.instance.setStage(primaryStage);

      // Push main interaction screen.
      NetworkControllerStack.instance.push(
            Main.class.getResource("gui/view/networkSelector.fxml"));

      // Add in interaction screen.
      NetworkSelectorController nsc = (NetworkSelectorController) NetworkControllerStack.instance.peekController();
      try {
         nsc.setChild(Main.class.getResource("gui/view/interact.fxml"));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void genAlgTest() throws Exception{
      List<Memory> training = FileManager.loadMemories("training");
      List<Memory> test = FileManager.loadMemories("testMemories");
      List<Memory> fitness = FileManager.loadMemories("fitness");

      Schema schema = new Schema("Schema", new ColorInputAdapter(), new Object[] {
            "Red",
            "Orange",
            "Yellow",
            "Green",
            "Blue",
            "Purple"
      });
      model.network.parameters.Parameters params = new model.network.parameters.Parameters();
      if (!params.getParameter(model.network.parameters.Parameters.kAcceptablePercentCorrect).setValue(new Double(90.0)))
         return;
      if (!params.getParameter(model.network.parameters.Parameters.kIterationCap).setValue(new Integer(100)))
         return;
      if (!params.getParameter(model.network.parameters.Parameters.kLearningConstant).setValue(new Double(0.35)))
         return;

      NetworkGeneticAdapter adapter = new NetworkGeneticAdapter(
            schema, params, training, test, fitness);
      GeneticAlgorithm<Network> algorithm = new GeneticAlgorithm(adapter);
      //algorithm.setGenerationCap(1);
      algorithm.setPopulationSize(50);
      algorithm.setAcceptableFitness(.90);

      algorithm.run();
      System.exit(0);
   }

   public static void main(String[] args) {
      launch(args);
   }
}
