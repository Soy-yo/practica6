package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.MostCrowdedJunction;
import es.ucm.fdi.model.TrafficSimulator;

public class NewMostCrowdedJunctionEvent extends NewJunctionEvent {

  private static final String FRIENDLY_CLASS_NAME = "New MC Junction";

  NewMostCrowdedJunctionEvent(int time, String id) {
    super(time, id);
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new MostCrowdedJunction(id));
  }

  @Override
  public String toString() {
    return FRIENDLY_CLASS_NAME + " " + id;
  }

  static class Builder extends NewJunctionEvent.Builder {

    @Override
    public boolean matchesType(IniSection section) {
      return MostCrowdedJunction.TYPE.equals(section.getValue("type"));
    }

    @Override
    public NewJunctionEvent parseType(IniSection section, int time, String id) {
      return new NewMostCrowdedJunctionEvent(time, id);
    }

    @Override
    public String getEventName() {
      return FRIENDLY_CLASS_NAME;
    }

    @Override
    public String getEventFileTemplate() {
      return super.getEventFileTemplate() + "type = " + MostCrowdedJunction.TYPE + "\n";
    }

  }

}
