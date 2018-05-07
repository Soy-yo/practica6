package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Describable;
import es.ucm.fdi.model.TrafficSimulator;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa un evento abstracto en la simulación
 */
public abstract class Event implements Describable {

  /**
   * Títulos de la descripción
   */
  public static final String[] INFO = {"#", "Time", "Type"};

  protected final int time;
  protected final String id;

  Event(int time, String id) {
    this.time = time;
    this.id = id;
  }

  public int getTime() {
    return time;
  }

  public String getId() {
    return id;
  }

  /**
   * Ejecuta la acción del evento
   */
  public abstract void execute(TrafficSimulator simulator);

  public Map<String, String> describe() {
    Map<String, String> result = new HashMap<>();
    result.put(INFO[0], "");
    result.put(INFO[1], "" + time);
    result.put(INFO[2], toString());
    return result;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + id;
  }

  /**
   * Permite construir eventos a partir de una sección de un archivo ini
   */
  public interface Builder {

    /**
     * Lee los datos del ini y devuelve el evento creado con esos datos
     */
    Event parse(IniSection section);

    /**
     * Devuelve el nombre del evento que construye
     */
    String getEventName();

    /**
     * Devuelve la plantilla que tiene una sección de este evento
     */
    String getEventFileTemplate();

    /**
     * Comprueba que el tipo especificado sea el mismo que la subclase de Event concreta
     */
    default boolean matchesType(IniSection section) {
      return section.getValue("type") == null;
    }

    /**
     * Comprueba que el id es válido
     */
    default boolean isValid(String id) {
      return id.matches("[a-zA-Z1-9_]++");
    }

    /**
     * Lee una cadena de texto o lanza un error si no existe tal clave en la sección
     */
    default String parseString(IniSection section, String key) {
      String result = section.getValue(key);
      if (result == null) {
        throw new IllegalArgumentException("Missing " + key);
      }
      return result;
    }

    /**
     * Lee un entero del ini y devuelve dicho entero o un valor por defecto si no hay entrada o
     * un error si no es un número o es negativo
     */
    default int parsePositiveInt(IniSection section, String key, int defaultValue) {
      try {
        int result = Integer.parseInt(parseString(section, key));
        if (result >= 0) {
          return result;
        }
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(key + " must be a number", e);
      } catch (IllegalArgumentException e) {
        // No existía la clave, devuelve el valor por defecto
        return defaultValue;
      }
      // Si ha llegado aquí es que es negativo
      // No se lanza antes para que no lo capture el catch
      throw new IllegalArgumentException(key + " must be positive or zero");
    }

    /**
     * Lee un entero positivo del ini y devuelve dicho entero o un error si no es un numero o es
     * negativo
     */
    default int parsePositiveInt(IniSection section, String key) {
      int result;
      try {
        result = Integer.parseInt(parseString(section, key));
        if (result <= 0) {
          throw new IllegalArgumentException(key + " must be positive");
        }
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(key + " must be a number", e);
      }
      return result;
    }

    /**
     * Lee un long del ini y devuelve dicho entero o un valor por defecto si no hay entrada o un
     * error si no es un número o es negativo
     */
    default long parsePositiveLong(IniSection section, String key, long defaultValue) {
      try {
        long result = Long.parseLong(parseString(section, key));
        if (result > 0) {
          return result;
        }
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(key + " must be a number", e);
      } catch (IllegalArgumentException e) {
        // No existía la clave, devuelve el valor por defecto
        return defaultValue;
      }
      throw new IllegalArgumentException(key + " must be positive");
    }

    /**
     * Lee un double positivo del ini y devuelve dicho entero o un error si no es un numero, es
     * negativo o mayor que un máximo dado
     */
    default double parsePositiveDouble(IniSection section, String key, double maxValue) {
      double result;
      try {
        result = Double.parseDouble(parseString(section, key));
        if (result < 0 || result > maxValue) {
          throw new IllegalArgumentException(key + " has to be between 0 and " + maxValue);
        }
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(key + " must be a number");
      }
      return result;
    }

    /**
     * Devuelve un array con una lista de ids que estaban separados por comas (al menos minElements)
     */
    default String[] parseIdList(IniSection section, String key, int minElements) {
      String values = parseString(section, key);
      String[] result = values.split("[, ]+");
      if (result.length < minElements) {
        throw new IllegalArgumentException(
            "The id list for " + key + " must contain at list " + minElements + " elements");
      }
      return result;
    }

    /**
     * Devuelve el campo id de la sección section
     */
    default String getId(IniSection section) {
      String id = parseString(section, "id");
      if (!isValid(id)) {
        throw new IllegalArgumentException("Id \"" + id + "\" is not a valid id");
      }
      return id;
    }

  }

}
