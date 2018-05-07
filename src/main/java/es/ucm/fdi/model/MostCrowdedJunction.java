package es.ucm.fdi.model;

import java.util.Map;

/**
 * Cruce que actualiza el semáforo a la carretera con más vehículos a la espera más cercana en el
 * sentido de las agujas del reloj a la actual en verde y calcula el tiempo que estará en verde
 * el semáforo en función de los coches que haya en la cola
 */
public class MostCrowdedJunction extends JunctionWithTimeSlice {

  public static final String TYPE = "mc";

  public MostCrowdedJunction(String id) {
    super(id, 1);
  }

  @Override
  protected void switchLights() {
    if (timeUnits == timeLapse - 1) {
      if (currentRoadOn != null) {
        currentRoadOn.switchLight();
      }
      findNextRoad();
      currentRoadOn.switchLight();
      timeLapse = Math.max(currentRoadOn.vehicleCount() / 2, 1);
      timeUnits = 0;
    } else {
      timeUnits++;
    }
  }

  /**
   * Busca la siguiente carretera que debe ponerse en verde y la actualiza, así como el iterador
   * a la siguiente
   */
  private void findNextRoad() {
    IncomingRoad road = (IncomingRoad) getNextRoad();
    IncomingRoad mostCrowded = road;
    if (currentRoadOn == null) {
      // Si todos los semáforos están en rojo hay que buscar el que más coches tenga
      // así que suponemos que la que estaba en verde era la primera y que es la que vamos a
      // escoger si ninguna tiene más coches
      currentRoadOn = road;
      road = (IncomingRoad) getNextRoad();
    }
    while (road != currentRoadOn) {
      if (road.vehicleCount() > mostCrowded.vehicleCount()) {
        mostCrowded = road;
      }
      road = (IncomingRoad) getNextRoad();
    }
    currentRoadOn = mostCrowded;
    // Apunta a la siguiente carretera de nuevo
    while (road != currentRoadOn) {
      road = (IncomingRoad) getNextRoad();
    }
  }

  @Override
  public void fillReportDetails(Map<String, String> kvps) {
    super.fillReportDetails(kvps);
    kvps.put("type", TYPE);
  }

}
