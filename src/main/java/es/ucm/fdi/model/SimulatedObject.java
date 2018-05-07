package es.ucm.fdi.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clase que representa un objeto abstracto de la simulación
 */
public abstract class SimulatedObject implements Describable {

  /**
   * Id único de cada objeto
   */
  protected final String id;

  public SimulatedObject(String id) {
    this.id = id;
  }

  /**
   * Hace a el objeto en cuestión avanzar si es un vehículo o que avancen los vehículos que están
   * en él en caso contrario
   */
  public abstract void advance();

  /**
   * Rellena los datos del objeto para poder ser impresos en su report
   */
  public abstract void fillReportDetails(Map<String, String> kvps);

  /**
   * Devuelve el nombre del tipo del objeto para el report
   */
  protected abstract String getReportHeader();

  @Override
  public abstract Map<String, String> describe();

  public String getId() {
    return id;
  }

  /**
   * Genera el informe utilizando los detalles del objeto
   */
  public Map<String, String> generateReport(int time) {
    Map<String, String> kvps = new LinkedHashMap<>();
    kvps.put("", getReportHeader());
    kvps.put("id", id);
    kvps.put("time", "" + time);
    fillReportDetails(kvps);
    return kvps;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimulatedObject other = (SimulatedObject) o;
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return id;
  }

}
