import application.DialogFactory;
import application.FileManager;
import gui.controller.NetworkControllerStack;
import gui.controller.component.NetworkSelectorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.WorkSpace;
import model.geneticAlgorithm.GeneticAdapter;
import model.geneticAlgorithm.GeneticAlgorithm;
import model.geneticAlgorithm.NetworkGeneticAdapter;
import model.network.Network;
import model.network.Parameters;
import model.network.memory.Memory;
import model.network.schema.ColorInputAdapter;
import model.network.schema.Schema;

import java.util.List;

public class Main extends Application {
   @Override
   public void start(Stage primaryStage) throws Exception {
      genAlgTest();
      /*
      // Load networks.
      WorkSpace.instance.loadNetworks();

      FXMLLoader loader = new FXMLLoader(
            Main.class.getResource("gui/view/main.fxml"));

      primaryStage.setTitle("Neural Network Trainer");
      primaryStage.setScene(new Scene((Parent)loader.load()));
      NetworkControllerStack.instance.setStage(primaryStage);

      // Push main interaction screen.
      NetworkControllerStack.instance.push(
            Main.class.getResource("gui/view/networkSelector.fxml"));

      NetworkSelectorController nsc = (NetworkSelectorController) NetworkControllerStack.instance.peekController();
      try {
         nsc.setChild(Main.class.getResource("gui/view/interact.fxml"));
      } catch (Exception e) {
         e.printStackTrace();
      }
      */
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
      model.network.Parameters params = new model.network.Parameters();
      if (!params.setParameter(model.network.Parameters.kAcceptablePercentCorrect, new Double(80.0)))
         return;
      if (!params.setParameter(model.network.Parameters.kIterationCap, new Integer(100)))
         return;
      if (!params.setParameter(model.network.Parameters.kLearningConstant, new Double(0.25)))
         return;

      NetworkGeneticAdapter adapter = new NetworkGeneticAdapter(
            schema, params, training, test, fitness);
      GeneticAlgorithm<Network> algorithm = new GeneticAlgorithm(adapter);
      //algorithm.setGenerationCap(15);
      algorithm.setPopulationSize(50);
      algorithm.setAcceptableFitness(.90);

      System.out.println(algorithm.run().getParameters().getParameterValue(model.network.Parameters.kAcceptablePercentCorrect));
   }

   public static void main(String[] args) {
      launch(args);
   }
}
