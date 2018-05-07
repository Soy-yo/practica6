package es.ucm.fdi.control;

import es.ucm.fdi.events.Event;
import es.ucm.fdi.events.EventBuilder;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniError;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.TrafficSimulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Clase que pone en contacto la interfaz con el simulador
 */
public class Controller {

  private TrafficSimulator simulator;
  private OutputStream outputStream;

  public Controller(TrafficSimulator simulator) {
    this.simulator = simulator;
  }

  public TrafficSimulator getSimulator() {
    return simulator;
  }

  public void setOutputStream(OutputStream os) {
    outputStream = os;
  }

  /**
   * Ejecuta la simulación tantos pasos como se le indiquen
   */
  public void run(int ticks) {
    simulator.execute(ticks, outputStream);
  }

  /**
   * Resetea el simulador
   */
  public void reset() {
    simulator.reset();
  }

  /**
   * Carga los eventos desde la entrada que se le indica
   */
  public void loadEvents(InputStream is) {
    try {
      Ini ini = new Ini(is);
      for (IniSection section : ini.getSections()) {
        try {
          Event event = EventBuilder.parse(section);
          if (event == null) {
            throw new IllegalStateException(
                "Event for section " + section.getTag() + " was not recognized");
          }
          simulator.addEvent(event);
        } catch (IllegalStateException e) {
          throw new IllegalStateException(
              "Failed while trying to load events\n" + e.getMessage(), e);
        }
      }
    } catch (IOException | IniError e) {
      throw new IllegalStateException(
          "Something went wrong while reading ini file\n" + e.getMessage(), e);
    }
  }

}
