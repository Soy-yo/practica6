package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.TrafficSimulator;

public class MakeVehicleFaultyEvent extends Event {

  private static final String SECTION_TAG_NAME = "make_vehicle_faulty";
  private static final String[] ATTRIBUTES = {"time", "vehicles", "duration"};

  private String[] vehicles;
  private int duration;

  MakeVehicleFaultyEvent(int time, String id, String[] vehicles, int duration) {
    super(time, id);
    this.vehicles = vehicles;
    this.duration = duration;
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    for (String id : vehicles) {
      simulator.makeVehicleFaulty(id, duration);
    }
  }

  @Override
  public String toString() {
    return "Break Vehicles [" + String.join(",", vehicles) + "]";
  }

  static class Builder implements Event.Builder {

    @Override
    public Event parse(IniSection section) {
      if (!section.getTag().equals(SECTION_TAG_NAME)) {
        return null;
      }
      int time = parsePositiveInt(section, ATTRIBUTES[0], 0);
      String[] vehicles = parseIdList(section, ATTRIBUTES[1], 1);
      int duration = parsePositiveInt(section, ATTRIBUTES[2]);
      return new MakeVehicleFaultyEvent(time, "", vehicles, duration);
    }

    @Override
    public String getEventName() {
      return "Make Vehicle Faulty";
    }

    @Override
    public String getEventFileTemplate() {
      return "[" + SECTION_TAG_NAME + "]\n" + String.join(" = \n", ATTRIBUTES) + " = \n";
    }

  }

}
