package es.ucm.fdi.model;

import java.util.*;

import static java.util.stream.Collectors.joining;

/**
 * Clase que representa un cruce de carreteras
 */
public class Junction extends SimulatedObject {

  private static final String SECTION_TAG_NAME = "junction_report";
  public static final String[] INFO = {"ID", "Green", "Red"};

  protected Map<Road, IncomingRoad> incomingRoads;
  /**
   * Carretera con semáforo en verde actualmente
   */
  protected IncomingRoad currentRoadOn;
  /**
   * Siguiente carretera a la del semáforo en verde
   */
  protected Iterator<IncomingRoad> nextRoad;

  public Junction(String id) {
    super(id);
    incomingRoads = new LinkedHashMap<>();
  }

  /**
   * Añade una carretera entrante al cruce
   */
  public void addRoad(Road road) {
    incomingRoads.put(road, new IncomingRoad(road));
  }

  /**
   * Introduce un vehículo en el cruce
   */
  public void vehicleIn(Vehicle vehicle) {
    incomingRoads.get(vehicle.getRoad()).vehicleIn(vehicle);
  }

  /**
   * Devuelve la carretera con el semáforo en verde actualmente
   */
  public Road getGreenRoad() {
    return currentRoadOn == null ? null : currentRoadOn.road;
  }

  @Override
  public void advance() {
    if (!incomingRoads.isEmpty()) {
      if (currentRoadOn != null && !currentRoadOn.isEmpty()) {
        currentRoadOn.vehicleOut();
      }
      switchLights();
    }
  }

  /**
   * Actualiza las luces de los semáforos y pone en verde el siguiente
   */
  protected void switchLights() {
    IncomingRoad previous = currentRoadOn;
    currentRoadOn = getNextRoad();
    if (previous != null) {
      previous.switchLight();
    }
    currentRoadOn.switchLight();
  }

  /**
   * Devuelve la carretera que debe ponerse en verde (produce cambios en el iterador)
   */
  protected IncomingRoad getNextRoad() {
    // Reinicia el iterador cada vuelta
    if (nextRoad == null || !nextRoad.hasNext()) {
      nextRoad = incomingRoads.values().iterator();
    }
    return nextRoad.next();
  }

  /**
   * Devuelve la carretera que une el cruce con id previousJunction con esta (si existe)
   */
  public Road getStraightRoad(String previousJunction) {
    for (Road r : incomingRoads.keySet()) {
      if (r.getSource().equals(previousJunction)) {
        return r;
      }
    }
    return null;
  }

  @Override
  public void fillReportDetails(Map<String, String> kvps) {
    kvps.put("queues", incomingRoads.entrySet().stream()
        .map(e -> "(" + e.getKey() + "," + e.getValue().lightColor() + ",["
            + e.getValue().vehicleList.stream()
            .map(Vehicle::toString)
            .collect(joining(",")) + "])")
        .collect(joining(",")));
  }

  @Override
  protected String getReportHeader() {
    return SECTION_TAG_NAME;
  }

  @Override
  public Map<String, String> describe() {
    Map<String, String> result = new HashMap<>();
    result.put(INFO[0], id);
    result.put(INFO[1], currentRoadOn == null ? "[]" : "[(" + currentRoadOn.road + "," +
        currentRoadOn.lightColor() + ",[" + currentRoadOn.vehicleList.stream()
        .map(Vehicle::toString)
        .collect(joining(",")) + "])]");
    result.put(INFO[2], "[" + incomingRoads.entrySet().stream()
        .filter(r -> r.getValue() != currentRoadOn)
        .map(r -> "(" + r.getKey() + ",red,[" + (r.getValue().vehicleList.stream()
            .map(Vehicle::toString)
            .collect(joining(",")) + "])"))
        .collect(joining(",")) + "]");
    return result;
  }

  /**
   * Clase interna que representa una carretera entrante (carretera con la cola de vehículos a la
   * espera y ciertas operaciones)
   */
  protected class IncomingRoad {

    Road road;
    Queue<Vehicle> vehicleList;
    boolean greenLight;

    IncomingRoad(Road road) {
      this.road = road;
      this.vehicleList = new ArrayDeque<>();
      greenLight = false;
    }

    /**
     * Introduce un vehículo en la cola de vehículos
     */
    void vehicleIn(Vehicle vehicle) {
      vehicleList.add(vehicle);
    }

    /**
     * Mueve el primer vehículo de la cola a su siguiente carretera
     */
    void vehicleOut() {
      Vehicle vehicle = vehicleList.poll();
      vehicle.moveToNextRoad();
    }

    /**
     * Devuelve el número de vehículos esperando en esta carretera
     */
    int vehicleCount() {
      return vehicleList.size();
    }

    /**
     * Determina si la cola de vehículos está vacía
     */
    boolean isEmpty() {
      return vehicleList.size() == 0;
    }

    /**
     * Cambia la luz del semáforo de esta carretera
     */
    void switchLight() {
      greenLight = !greenLight;
    }

    /**
     * Devuelve el color del semáforo como texto
     */
    String lightColor() {
      return greenLight ? "green" : "red";
    }

  }

}
