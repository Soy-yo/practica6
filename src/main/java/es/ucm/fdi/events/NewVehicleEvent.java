package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.TrafficSimulator;
import es.ucm.fdi.model.Vehicle;

public class NewVehicleEvent extends Event {

  private static final String FRIENDLY_CLASS_NAME = "New Vehicle";
  protected static final String SECTION_TAG_NAME = "new_vehicle";
  protected static final String[] ATTRIBUTES = {"time", "id", "max_speed", "itinerary"};

  protected int maxSpeed;
  protected String[] itinerary;

  NewVehicleEvent(int time, String id, int maxSpeed, String[] itinerary) {
    super(time, id);
    this.maxSpeed = maxSpeed;
    this.itinerary = itinerary;
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new Vehicle(id, maxSpeed, simulator.getPath(itinerary)));
  }

  @Override
  public String toString() {
    return FRIENDLY_CLASS_NAME + " " + id;
  }

  static class Builder implements Event.Builder {

    @Override
    public Event parse(IniSection section) {
      if (!section.getTag().equals(SECTION_TAG_NAME) || !matchesType(section)) {
        return null;
      }
      int time = parsePositiveInt(section, ATTRIBUTES[0], 0);
      String id = getId(section);
      int maxSpeed = parsePositiveInt(section, ATTRIBUTES[2]);
      String[] itinerary = parseIdList(section, ATTRIBUTES[3], 2);
      return parseType(section, time, id, maxSpeed, itinerary);
    }

    public NewVehicleEvent parseType(IniSection section, int time, String id, int maxSpeed,
                                     String[] itinerary) {
      return new NewVehicleEvent(time, id, maxSpeed, itinerary);
    }

    @Override
    public String getEventName() {
      return FRIENDLY_CLASS_NAME;
    }

    @Override
    public String getEventFileTemplate() {
      return "[" + SECTION_TAG_NAME + "]\n" + String.join(" = \n", ATTRIBUTES) + " = \n";
    }

  }

}
