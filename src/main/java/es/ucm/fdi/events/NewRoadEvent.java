package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Road;
import es.ucm.fdi.model.TrafficSimulator;

public class NewRoadEvent extends Event {

  private static final String FRIENDLY_CLASS_NAME = "New Road";
  protected static final String SECTION_TAG_NAME = "new_road";
  protected static final String[] ATTRIBUTES = {"time", "id", "src", "dest", "max_speed", "length"};

  protected String sourceId;
  protected String destinationId;
  protected int maxSpeed;
  protected int length;

  NewRoadEvent(int time, String id, String sourceId, String destinationId, int maxSpeed,
               int length) {
    super(time, id);
    this.sourceId = sourceId;
    this.destinationId = destinationId;
    this.maxSpeed = maxSpeed;
    this.length = length;
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new Road(id, length, maxSpeed, sourceId, destinationId));
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
      String src = parseString(section, ATTRIBUTES[2]);
      String dest = parseString(section, ATTRIBUTES[3]);
      int maxSpeed = parsePositiveInt(section, ATTRIBUTES[4]);
      int length = parsePositiveInt(section, ATTRIBUTES[5]);
      return parseType(section, time, id, src, dest, maxSpeed, length);
    }

    public NewRoadEvent parseType(IniSection section, int time, String id, String src,
                                  String dest, int maxSpeed, int length) {
      return new NewRoadEvent(time, id, src, dest, maxSpeed, length);
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
