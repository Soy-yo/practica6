package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Car;
import es.ucm.fdi.model.TrafficSimulator;

public class NewCarEvent extends NewVehicleEvent {

  private static final String FRIENDLY_CLASS_NAME = "New Car";
  private static final String[] ATTRIBUTES = {"resistance", "fault_probability",
      "max_fault_duration", "seed"};

  private int resistance;
  private double faultProbability;
  private int maxFaultDuration;
  private long seed;

  NewCarEvent(int time, String id, int maxSpeed, String[] itinerary,
              int resistance, double faultProbability, int maxFaultDuration,
              long seed) {
    super(time, id, maxSpeed, itinerary);
    this.resistance = resistance;
    this.faultProbability = faultProbability;
    this.maxFaultDuration = maxFaultDuration;
    this.seed = seed;
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new Car(id, maxSpeed, simulator.getPath(itinerary),
        resistance, faultProbability, maxFaultDuration, seed));
  }

  @Override
  public String toString() {
    return FRIENDLY_CLASS_NAME + " " + id;
  }

  static class Builder extends NewVehicleEvent.Builder {

    @Override
    public boolean matchesType(IniSection section) {
      return Car.TYPE.equals(section.getValue("type"));
    }

    @Override
    public NewVehicleEvent parseType(IniSection section, int time, String id, int maxSpeed,
                                     String[] itinerary) {
      return new NewCarEvent(time, id, maxSpeed, itinerary,
          parsePositiveInt(section, ATTRIBUTES[0]),
          parsePositiveDouble(section, ATTRIBUTES[1], 1.0),
          parsePositiveInt(section, ATTRIBUTES[2]),
          parsePositiveLong(section, ATTRIBUTES[3], System.currentTimeMillis()));
    }

    @Override
    public String getEventName() {
      return FRIENDLY_CLASS_NAME;
    }

    @Override
    public String getEventFileTemplate() {
      return super.getEventFileTemplate() + "type = " + Car.TYPE + "\n" +
          String.join(" = \n", ATTRIBUTES) + " = \n";
    }

  }

}
