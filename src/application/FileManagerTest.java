package application;

import model.network.Network;
import model.network.schema.ColorInputAdapter;
import model.network.schema.Schema;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import static org.junit.Assert.*;

public class FileManagerTest {
   private static String networkName = "junitTestNetwork";

   private Network createNetwork(String suffix) {
      return new Network(networkName + suffix,
            new Schema("Test", new ColorInputAdapter(),
                  new String[] {"Red", "Orange", "Yellow"}));
   }

   @Test
   public void test() throws Exception {
   }
}