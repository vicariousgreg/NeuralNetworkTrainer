package model;

import gui.controller.MainController;
import javafx.application.Platform;
import model.network.Network;
import model.network.memory.MemoryModule;
import model.network.schema.ColorSchema;

import java.io.*;
import java.util.Observable;

/**
 * Created by gpdavis on 5/2/15.
 */
public class WorkSpace extends Observable {
   public static final WorkSpace instance = new WorkSpace();

   private MainController controller;

   public final Interact interact;
   public final Examine examine;
   public final SetParameters setParameters;

   private Network network;
   private Network backupNetwork;

   private WorkSpace() {
      interact = new Interact();
      examine = new Examine();
      setParameters = new SetParameters();
      addObserver(interact);
      addObserver(examine);
      addObserver(setParameters);
   }

   public void setController(MainController controller) {
      this.controller = controller;
      addObserver(controller);
   }

   public Network getNetwork() {
      return network;
   }

   public void restoreNetwork() {
//      network = backupNetwork.clone();
      updateUI();
   }

   private void setNetwork(Network network) {
      this.network = network;
//      this.backupNetwork = network.clone();
      updateUI();
   }

   public void newNetwork() {
      network = new Network(new ColorSchema());
//      backupNetwork = network.clone();
      updateUI();
   }

   public void closeNetwork() {
      network = null;
//      backupNetwork = null;
      updateUI();
   }

   public void loadNetwork(File file) {
      try {
         FileInputStream fin = new FileInputStream(file);
         ObjectInputStream ois = new ObjectInputStream(fin);
         setNetwork((Network) ois.readObject());
         ois.close();
      } catch (Exception e) {
         signalUIError("Error loading network!");
      }
   }

   public void saveNetwork(File file) {
      try {
         FileOutputStream fos = new FileOutputStream(file);
         ObjectOutputStream out = new ObjectOutputStream(fos);
         out.writeObject(network);
         out.close();
         updateUI();
      } catch (Exception e) {
         signalUIError("Error saving network!");
      }
   }

   public void consolidateMemory() {
      System.out.println("Training network...");
      if (network != null) network.train();
      System.out.println("...done!");
      updateUI();
   }

   public void saveMemory(File file) {
      try {
         FileOutputStream fos = new FileOutputStream(file);
         ObjectOutputStream out = new ObjectOutputStream(fos);
         out.writeObject(network.getMemoryModule());
         out.close();
         updateUI();
      } catch (Exception e) {
         signalUIError("Error saving memories!");
      }
   }

   public void loadMemory(File file) {
      try {
         FileInputStream fin = new FileInputStream(file);
         ObjectInputStream ois = new ObjectInputStream(fin);
         network.setMemoryModule(((MemoryModule) ois.readObject()));
         ois.close();
         updateUI();
      } catch (Exception e) {
         signalUIError("Error loading network!");
      }
   }

   public void wipeMemory() {
      network.wipeMemory();
      updateUI();
   }

   public void addMemory(Object input, Object output) {
      try {
         network.addMemory(input, output);
         updateUI();
      } catch (Exception e) {
         signalUIError("Error adding memory!");
      }
   }

   public Object queryNetwork(Object input) {
      try {
         return network.query(input);
      } catch(Exception e) {
         signalUIError("Error querying network!");
         return "Unknown";
      }
   }

   private void updateUI() {
       Platform.runLater(new Runnable() {
         @Override
         public void run() {
            setChanged();
            notifyObservers();
         }
      });
   }

   private void signalUIError(String message) {
      final String errorMessage = message;
      Platform.runLater(new Runnable() {
         @Override
         public void run() {
            setChanged();
            notifyObservers(errorMessage);
         }
      });
   }
}
