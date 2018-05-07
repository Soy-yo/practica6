package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.LaneRoad;
import es.ucm.fdi.model.TrafficSimulator;

public class NewLaneRoadEvent extends NewRoadEvent {

  private static final String FRIENDLY_CLASS_NAME = "New Lane Road";
  private static final String[] ATTRIBUTES = {"lanes"};

  private int lanes;

  NewLaneRoadEvent(int time, String id, String sourceId, String destinationId, int maxSpeed,
                   int length, int lanes) {
    super(time, id, sourceId, destinationId, maxSpeed, length);
    this.lanes = lanes;
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new LaneRoad(id, length, maxSpeed, sourceId, destinationId,
        lanes));
  }

  @Override
  public String toString() {
    return FRIENDLY_CLASS_NAME + " " + id;
  }

  static class Builder extends NewRoadEvent.Builder {

    @Override
    public boolean matchesType(IniSection section) {
      return LaneRoad.TYPE.equals(section.getValue("type"));
    }

    @Override
    public NewRoadEvent parseType(IniSection section, int time, String id, String src,
                                  String dest, int maxSpeed, int length) {
      return new NewLaneRoadEvent(time, id, src, dest, maxSpeed, length,
          parsePositiveInt(section, ATTRIBUTES[0]));
    }

    @Override
    public String getEventName() {
      return FRIENDLY_CLASS_NAME;
    }

    @Override
    public String getEventFileTemplate() {
      return super.getEventFileTemplate() + "type = " + LaneRoad.TYPE + "\n" +
          String.join(" = \n", ATTRIBUTES) + " = \n";
    }

  }

}
