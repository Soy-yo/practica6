package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Junction;
import es.ucm.fdi.model.TrafficSimulator;

public class NewJunctionEvent extends Event {

  private static final String FRIENDLY_CLASS_NAME = "New Junction";
  protected static final String SECTION_TAG_NAME = "new_junction";
  protected static final String[] ATTRIBUTES = {"time", "id"};

  NewJunctionEvent(int time, String id) {
    super(time, id);
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new Junction(id));
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
      return parseType(section, time, id);
    }

    public NewJunctionEvent parseType(IniSection section, int time, String id) {
      return new NewJunctionEvent(time, id);
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
