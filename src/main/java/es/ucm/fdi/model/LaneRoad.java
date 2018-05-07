package es.ucm.fdi.model;

import java.util.Map;

/**
 * Autopista con varios carriles que permite a los coches ir a mayor velocidad esquivando
 * vehículos averiados
 */
public class LaneRoad extends Road {

  public static final String TYPE = "lanes";

  private int lanes;

  public LaneRoad(String id, int length, int maxSpeed, String sourceId, String destinationId,
                  int lanes) {
    super(id, length, maxSpeed, sourceId, destinationId);
    this.lanes = lanes;
  }

  @Override
  protected int calculateBaseSpeed() {
    return Math.min(maxSpeed, maxSpeed * lanes / Math.max(vehicleList.sizeOfValues(), 1) + 1);
  }

  @Override
  protected int calculateReductionFactor(int faultyVehicles) {
    return faultyVehicles >= lanes ? 2 : 1;
  }

  @Override
  public void fillReportDetails(Map<String, String> kvps) {
    kvps.put("type", TYPE);
    super.fillReportDetails(kvps);
  }

}
