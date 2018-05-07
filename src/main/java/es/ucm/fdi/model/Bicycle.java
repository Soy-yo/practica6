package es.ucm.fdi.model;

import java.util.Map;
import java.util.Queue;

/**
 * Vehículo "más lento" que sólo se puede estropear cuando va a velocidades altas
 */
public class Bicycle extends Vehicle {

  public static final String TYPE = "bike";

  public Bicycle(String id, int maxSpeed, Queue<Junction> itinerary) {
    super(id, maxSpeed, itinerary);
  }

  @Override
  public void setFaulty(int faulty) {
    if (currentSpeed > (maxSpeed / 2)) {
      super.setFaulty(faulty);
    }
  }

  @Override
  public void fillReportDetails(Map<String, String> kvps) {
    kvps.put("type", TYPE);
    super.fillReportDetails(kvps);
  }

}
