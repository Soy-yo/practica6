package es.ucm.fdi.model;

import java.util.*;

/**
 * Clase que representa el mapa de carreteras, uniendo cruces, carrteras y vehículos
 */
public class RoadMap {

  private Map<String, Vehicle> vehicles;
  private Map<String, Road> roads;
  private Map<String, Junction> junctions;

  private Collection<Vehicle> unmodifiableVehicleList;
  private Collection<Road> unmodifiableRoadList;
  private Collection<Junction> unmodifiableJunctionList;

  public RoadMap() {
    reset();
  }

  /**
   * Devuelve el mapa a su estado inicial
   */
  public void reset() {
    vehicles = new LinkedHashMap<>();
    unmodifiableVehicleList = Collections.unmodifiableCollection(vehicles.values());
    roads = new LinkedHashMap<>();
    unmodifiableRoadList = Collections.unmodifiableCollection(roads.values());
    junctions = new LinkedHashMap<>();
    unmodifiableJunctionList = Collections.unmodifiableCollection(junctions.values());
  }

  /**
   * Determina si el objeto con el id indicado se encuentra en el mapa
   */
  public boolean contains(String id) {
    return vehicles.containsKey(id) || roads.containsKey(id) || junctions.containsKey(id);
  }

  /**
   * Añade un nuevo objeto al mapa si no se encontraba ya en él
   */
  public void addSimulatedObject(SimulatedObject o) {
    if (contains(o.getId())) {
      throw new IllegalArgumentException("Object " + o.getId() + " is already registered");
    }
    if (o instanceof Vehicle) {
      addVehicle((Vehicle) o);
    } else if (o instanceof Road) {
      addRoad((Road) o);
    } else if (o instanceof Junction) {
      addJunction((Junction) o);
    }
  }

  /**
   * Añade un nuevo vehículo al mapa si tiene un itinerario válido
   */
  private void addVehicle(Vehicle v) {
    List<Junction> itinerary = v.getItinerary();
    // Comprueba que el itinerario del vehículo es posible
    for (Junction j : itinerary) {
      if (!junctions.containsKey(j.getId())) {
        throw new IllegalArgumentException("Junction " + j + " in vehicle's " + v
            + " itinerary does not exists in the road map");
      }
    }
    v.moveToNextRoad();
    vehicles.put(v.getId(), v);
  }

  /**
   * Añade una carretera al mapa si su origen y destino existen en el mapa
   */
  private void addRoad(Road r) {
    Junction destination = junctionSearch(r.getDestiny());
    if (!junctions.containsKey(r.getSource())) {
      throw new IllegalArgumentException("Couldn't find source for road " + r.getId());
    }
    if (destination == null) {
      throw new IllegalArgumentException("Couldn't find destination for road " + r.getId());
    }
    destination.addRoad(r);
    roads.put(r.getId(), r);
  }

  /**
   * Añade un cruce al mapa
   */
  private void addJunction(Junction j) {
    junctions.put(j.getId(), j);
  }

  /**
   * Devuelve el objeto buscado si existe o null en caso contrario
   */
  public SimulatedObject searchById(String id) {
    if (vehicles.containsKey(id)) {
      return vehicles.get(id);
    }
    if (roads.containsKey(id)) {
      return roads.get(id);
    }
    if (junctions.containsKey(id)) {
      return junctions.get(id);
    }
    return null;
  }

  /**
   * Devuelve el vehículo con el id indicado si existe o null en caso contrario
   */
  public Vehicle vehicleSearch(String id) {
    return vehicles.get(id);
  }

  /**
   * Devuelve la carretera con el id indicado si existe o null en caso contrario
   */
  public Road roadSearch(String id) {
    return roads.get(id);
  }

  /**
   * Devuelve el cruce con el id indicado si existe o null en caso contrario
   */
  public Junction junctionSearch(String id) {
    return junctions.get(id);
  }

  /**
   * Devuelve una colección no modificable con todos los vehiculos del mapa
   */
  public Collection<Vehicle> getVehicles() {
    return unmodifiableVehicleList;
  }

  /**
   * Devuelve una colección no modificable con todas las carreteras del mapa
   */
  public Collection<Road> getRoads() {
    return unmodifiableRoadList;
  }

  /**
   * Devuelve una colección no modificable con todos los cruces del mapa
   */
  public Collection<Junction> getJunctions() {
    return unmodifiableJunctionList;
  }

  /**
   * Devuelve el conjunto de todas las carreteras con su luz del semáforo en verde en su cruce de
   * destino
   */
  public Set<Road> getGreenRoads() {
    Set<Road> result = new HashSet<>();
    for (Junction j : junctions.values()) {
      Road green = j.getGreenRoad();
      if (green != null) {
        result.add(green);
      }
    }
    return result;
  }

  /**
   * Devuelve una cola de cruces a partir de sus ids si todos existen y hay alguna carretera que
   * los une
   */
  public Queue<Junction> getPath(String[] path) {
    Queue<Junction> result = new ArrayDeque<>();
    String previousJunctionId = null;
    for (String id : path) {
      Junction j = junctionSearch(id);
      if (j == null) {
        throw new IllegalArgumentException("Junction " + id + " does not exit in road map");
      }
      if (previousJunctionId != null && j.getStraightRoad(previousJunctionId) == null) {
        throw new IllegalArgumentException("No road connects " + previousJunctionId + " and " + id);
      }
      result.add(j);
      previousJunctionId = id;
    }
    return result;
  }

}
