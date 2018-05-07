package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Bicycle;
import es.ucm.fdi.model.TrafficSimulator;

public class NewBicycleEvent extends NewVehicleEvent {

  private static final String FRIENDLY_CLASS_NAME = "New Bike";

  NewBicycleEvent(int time, String id, int maxSpeed, String[] itinerary) {
    super(time, id, maxSpeed, itinerary);
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new Bicycle(id, maxSpeed, simulator.getPath(itinerary)));
  }

  @Override
  public String toString() {
    return FRIENDLY_CLASS_NAME + " " + id;
  }

  static class Builder extends NewVehicleEvent.Builder {

    @Override
    public boolean matchesType(IniSection section) {
      return Bicycle.TYPE.equals(section.getValue("type"));
    }

    @Override
    public NewVehicleEvent parseType(IniSection section, int time, String id, int maxSpeed,
                                     String[] itinerary) {
      return new NewBicycleEvent(time, id, maxSpeed, itinerary);
    }

    @Override
    public String getEventName() {
      return FRIENDLY_CLASS_NAME;
    }

    @Override
    public String getEventFileTemplate() {
      return super.getEventFileTemplate() + "type = " + Bicycle.TYPE + "\n";
    }

  }

}
