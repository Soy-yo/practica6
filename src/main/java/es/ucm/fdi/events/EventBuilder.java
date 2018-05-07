package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;

/**
 * Constructora de eventos
 */
public class EventBuilder {

  public static final Event.Builder[] SUPPORTED_EVENTS = {
      new NewCarEvent.Builder(),
      new NewBicycleEvent.Builder(),
      new NewVehicleEvent.Builder(),
      new NewLaneRoadEvent.Builder(),
      new NewDirtRoadEvent.Builder(),
      new NewRoadEvent.Builder(),
      new NewRoundRobinJunctionEvent.Builder(),
      new NewMostCrowdedJunctionEvent.Builder(),
      new NewJunctionEvent.Builder(),
      new MakeVehicleFaultyEvent.Builder()
  };

  /**
   * Dado un ini decide de qué evento se trata (null si no se corresponde con ninguno) y lo
   * devuelve ya creado
   */
  public static Event parse(IniSection section) throws IllegalStateException {
    int i = 0;
    Event result = null;
    try {
      while (i < SUPPORTED_EVENTS.length && result == null) {
        result = SUPPORTED_EVENTS[i].parse(section);
        i++;
      }
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException(
          "Something went wrong while parsing section\n" + section + "\n" + e.getMessage(), e);
    }
    return result;
  }

}
