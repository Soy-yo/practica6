package es.ucm.fdi.model;

import es.ucm.fdi.util.MultiTreeMap;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 * Clase que representa una carretera
 */
public class Road extends SimulatedObject {

  private static final String SECTION_TAG_NAME = "road_report";
  public static final String[] INFO = {"ID", "Source", "Target", "Length",
      "Max Speed", "Vehicles"};

  protected int length;
  protected int maxSpeed;
  protected MultiTreeMap<Integer, Vehicle> vehicleList;
  protected String sourceId;
  protected String destinationId;

  public Road(String id, int length, int maxSpeed, String sourceId, String destinationId) {
    super(id);
    this.length = length;
    this.maxSpeed = maxSpeed;
    vehicleList = new MultiTreeMap<>(Comparator.comparing(Integer::intValue).reversed());
    this.sourceId = sourceId;
    this.destinationId = destinationId;
  }

  public int getLength() {
    return length;
  }

  public String getSource() {
    return sourceId;
  }

  public String getDestiny() {
    return destinationId;
  }

  public void vehicleIn(Vehicle vehicle) {
    vehicleList.putValue(0, vehicle);
  }

  public void vehicleOut(Vehicle vehicle) {
    vehicleList.removeValue(vehicle.getLocation(), vehicle);
  }

  /**
   * Hace avanzar a todos los vehículos que se encuentran en la carretera dependiendo de su
   * coeficiente de reducción
   */
  @Override
  public void advance() {
    if (vehicleList.sizeOfValues() > 0) {
      int baseSpeed = calculateBaseSpeed();
      int faultyVehicles = 0;
      // Nuevo mapa donde almacenar los vehículos
      MultiTreeMap<Integer, Vehicle> temp =
          new MultiTreeMap<>(Comparator.comparing(Integer::intValue).reversed());
      for (Vehicle v : vehicleList.innerValues()) {
        int reductionFactor = calculateReductionFactor(faultyVehicles);
        if (v.getFaulty() > 0) {
          faultyVehicles++;
        }
        v.setCurrentSpeed(baseSpeed / reductionFactor);
        v.advance();
        temp.putValue(v.getLocation(), v);
      }
      vehicleList = temp;
    }
  }

  /**
   * Calcula la velocidad base que tienen los vehículo de la carrtera
   */
  protected int calculateBaseSpeed() {
    return Math.min(maxSpeed, maxSpeed / Math.max(vehicleList.sizeOfValues(), 1) + 1);
  }

  /**
   * Calcula el factor de reducción de velocidad que quizá haga que los vehículos vayan más lentos
   */
  protected int calculateReductionFactor(int faultyVehicles) {
    return faultyVehicles > 0 ? 2 : 1;
  }

  @Override
  public void fillReportDetails(Map<String, String> kvps) {
    kvps.put("state", vehicleList.valuesList().stream()
        .map(v -> "(" + v + "," + v.getLocation() + ")")
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
    result.put(INFO[1], sourceId);
    result.put(INFO[2], destinationId);
    result.put(INFO[3], "" + length);
    result.put(INFO[4], "" + maxSpeed);
    result.put(INFO[5], "[" + vehicleList.valuesList().stream()
        .map(SimulatedObject::getId)
        .collect(joining(",")) + "]");
    return result;
  }

}
