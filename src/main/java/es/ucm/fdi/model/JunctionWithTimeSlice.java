package es.ucm.fdi.model;

/**
 * Clase que representa un cruce en el que los semáforos necesitan varias llamadas a advance para
 * cambiar de color
 */
public abstract class JunctionWithTimeSlice extends Junction {

  protected int timeLapse;
  protected int timeUnits;
  protected int timesUsed;

  public JunctionWithTimeSlice(String id, int timeLapse) {
    super(id);
    this.timeLapse = timeLapse;
    timeUnits = 0;
    timesUsed = 0;
  }

  /**
   * Sobreescrito para usar la nueva IncomingRoad
   */
  @Override
  public void addRoad(Road road) {
    incomingRoads.put(road, new IncomingRoad(road));
  }

  protected class IncomingRoad extends Junction.IncomingRoad {

    IncomingRoad(Road road) {
      super(road);
    }

    @Override
    void vehicleOut() {
      Vehicle vehicle = vehicleList.poll();
      vehicle.moveToNextRoad();
      timesUsed++;
    }

    @Override
    void switchLight() {
      super.switchLight();
      timesUsed = 0;
    }

    @Override
    String lightColor() {
      return greenLight ? "green:" + (timeLapse - timeUnits) : "red";
    }

  }

}
