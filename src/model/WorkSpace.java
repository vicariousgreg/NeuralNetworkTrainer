package model;

import gui.controller.MainController;
import javafx.application.Platform;
import model.network.Network;
import model.network.Parameters;
import model.network.memory.MemoryModule;
import model.network.schema.ColorSchema;
import model.network.schema.Schema;

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

   private boolean hasChanged = false;

   private WorkSpace() {
      interact = new Interact();
      examine = new Examine();
      setParameters = new SetParameters();
      addObserver(interact);
      addObserver(examine);
      addObserver(setParameters);
   }

   public boolean hasChanged() {
      return hasChanged;
   }

   public void setController(MainController controller) {
      this.controller = controller;
      addObserver(controller);
   }

   public boolean openNetwork() {
      return network != null;
   }

   public MemoryModule getNetworkMemory() {
      if (network != null)
         return network.getMemoryModule();
      else
         return null;
   }
   public Schema getNetworkSchema() {
      if (network != null)
         return network.schema;
      else
         return null;
   }
   public Parameters getNetworkParameters() {
      if (network != null)
         return network.getParameters();
      else
         return null;
   }
   public void restoreNetwork() {
      network = backupNetwork.clone();
      updateUI();
   }

   private void setNetwork(Network network) {
      this.network = network;
      this.backupNetwork = network.clone();
      updateUI();
   }

   public void newNetwork() {
      hasChanged = false;
      network = new Network(new ColorSchema());
      backupNetwork = network.clone();
      updateUI();
   }

   public void closeNetwork() {
      hasChanged = false;
      network = null;
      backupNetwork = null;
      updateUI();
   }

   public void loadNetwork(File file) throws Exception {
      hasChanged = false;
      FileInputStream fin = new FileInputStream(file);
      ObjectInputStream ois = new ObjectInputStream(fin);
      setNetwork((Network) ois.readObject());
      ois.close();
   }

   public void saveNetwork(File file) throws Exception {
      hasChanged = false;
      FileOutputStream fos = new FileOutputStream(file);
      ObjectOutputStream out = new ObjectOutputStream(fos);
      out.writeObject(network);
      out.close();
   }

   public void consolidateMemory() {
      System.out.println("Training network...");
      if (network != null) {
         network.train();
         hasChanged = true;
      }
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
         hasChanged = true;
         updateUI();
      } catch (Exception e) {
         signalUIError("Error loading memories!");
      }
   }

   public void wipeMemory() {
      network.wipeMemory();
      hasChanged = true;
      updateUI();
   }

   public void addMemory(Object input, Object output) {
      try {
         network.addMemory(input, output);
         hasChanged = true;
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
