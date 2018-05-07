package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.DirtRoad;
import es.ucm.fdi.model.TrafficSimulator;

public class NewDirtRoadEvent extends NewRoadEvent {

  private static final String FRIENDLY_CLASS_NAME = "New Dirt Road";

  NewDirtRoadEvent(int time, String id, String sourceId, String destinationId, int maxSpeed,
                   int length) {
    super(time, id, sourceId, destinationId, maxSpeed, length);
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new DirtRoad(id, length, maxSpeed, sourceId, destinationId));
  }

  @Override
  public String toString() {
    return FRIENDLY_CLASS_NAME + " " + id;
  }

  static class Builder extends NewRoadEvent.Builder {


    @Override
    public boolean matchesType(IniSection section) {
      return DirtRoad.TYPE.equals(section.getValue("type"));
    }

    @Override
    public NewRoadEvent parseType(IniSection section, int time, String id, String src,
                                  String dest, int maxSpeed, int length) {
      return new NewDirtRoadEvent(time, id, src, dest, maxSpeed, length);
    }

    @Override
    public String getEventName() {
      return FRIENDLY_CLASS_NAME;
    }

    @Override
    public String getEventFileTemplate() {
      return super.getEventFileTemplate() + "type = " + DirtRoad.TYPE + "\n";
    }

  }

}
