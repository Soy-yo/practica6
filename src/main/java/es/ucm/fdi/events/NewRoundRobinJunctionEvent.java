package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.RoundRobinJunction;
import es.ucm.fdi.model.TrafficSimulator;

public class NewRoundRobinJunctionEvent extends NewJunctionEvent {

  private static final String FRIENDLY_CLASS_NAME = "New RR Junction";
  private static final String[] ATTRIBUTES = {"min_time_slice", "max_time_slice"};

  private int maxTimeSlice;
  private int minTimeSlice;

  NewRoundRobinJunctionEvent(int time, String id, int minTimeSlice, int maxTimeSlice) {
    super(time, id);
    this.maxTimeSlice = maxTimeSlice;
    this.minTimeSlice = minTimeSlice;
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new RoundRobinJunction(id, minTimeSlice, maxTimeSlice));
  }

  @Override
  public String toString() {
    return FRIENDLY_CLASS_NAME + " " + id;
  }

  static class Builder extends NewJunctionEvent.Builder {

    @Override
    public boolean matchesType(IniSection section) {
      return RoundRobinJunction.TYPE.equals(section.getValue("type"));
    }

    @Override
    public NewJunctionEvent parseType(IniSection section, int time, String id) {
      return new NewRoundRobinJunctionEvent(time, id,
          parsePositiveInt(section, ATTRIBUTES[0]),
          parsePositiveInt(section, ATTRIBUTES[1]));
    }

    @Override
    public String getEventName() {
      return FRIENDLY_CLASS_NAME;
    }

    @Override
    public String getEventFileTemplate() {
      return super.getEventFileTemplate() + "type = " + RoundRobinJunction.TYPE + "\n" +
          String.join(" = \n", ATTRIBUTES) + " = \n";
    }

  }

}
