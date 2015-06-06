package gui.controller.widget;

/**
 * Created by gpdavis on 6/5/15.
 */
public abstract class Randomizer<T> {
   protected GenericHandler<T> handler;
   protected T value;

   public void randomize() {
      render();
      if (handler != null)
         handler.handle(value);
   }

   public T getValue() {
      return value;
   }

   public void setValue(T value) {
      this.value = value;
      render();
      if (handler != null)
         handler.handle(value);
   }

   public void addListener(GenericHandler<T> handler) {
      this.handler = handler;
   }

   public abstract void render();
}
