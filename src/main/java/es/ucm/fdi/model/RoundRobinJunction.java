package es.ucm.fdi.model;

import java.util.Map;

/**
 * Cruce que determina la duración del siguiente semáforo en verde en función del número de
 * vehículos que pasó por la última carretera con el semáforo en verde
 */
public class RoundRobinJunction extends JunctionWithTimeSlice {

  public static final String TYPE = "rr";

  private int maxTimeSlice;
  private int minTimeSlice;

  public RoundRobinJunction(String id, int minTimeSlice, int maxTimeSlice) {
    super(id, maxTimeSlice);
    this.minTimeSlice = minTimeSlice;
    this.maxTimeSlice = maxTimeSlice;
  }

  @Override
  protected void switchLights() {
    if (currentRoadOn == null) {
      super.switchLights();
    } else if (timeUnits == timeLapse - 1) {
      if (timesUsed == 0) {
        // Ningún coche ha pasado esta vez
        timeLapse = Math.max(timeLapse - 1, minTimeSlice);
      } else if (timesUsed == timeUnits) {
        // Ha pasado algún coche cada tick
        timeLapse = Math.min(timeLapse + 1, maxTimeSlice);
      }
      super.switchLights();
      timeUnits = 0;
    } else {
      timeUnits++;
    }
  }

  @Override
  public void fillReportDetails(Map<String, String> kvps) {
    super.fillReportDetails(kvps);
    kvps.put("type", TYPE);
  }

}
