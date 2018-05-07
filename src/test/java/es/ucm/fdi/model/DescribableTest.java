package es.ucm.fdi.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import static org.junit.Assert.assertEquals;

public class DescribableTest {

  private Junction[] junctions;
  private Road[] roads;
  private Vehicle[] vehicles;

  @Before
  public void initialize() {
    junctions = new Junction[4];
    junctions[0] = new Junction("j1");
    junctions[1] = new Junction("j2");
    junctions[2] = new RoundRobinJunction("j3", 3, 3);
    junctions[3] = new MostCrowdedJunction("j4");

    roads = new Road[6];
    roads[0] = new Road("r1", 60, 20, "j1", "j2");
    roads[1] = new Road("r2", 80, 20, "j1", "j3");
    roads[2] = new DirtRoad("r3", 30, 10, "j1", "j4");
    roads[3] = new DirtRoad("r4", 30, 5, "j2", "j3");
    roads[4] = new LaneRoad("r5", 200, 50, "j2", "j4", 2);
    roads[5] = new LaneRoad("r6", 150, 30, "j3", "j4", 3);

    junctions[1].addRoad(roads[0]);
    junctions[2].addRoad(roads[1]);
    junctions[2].addRoad(roads[3]);
    junctions[3].addRoad(roads[2]);
    junctions[3].addRoad(roads[4]);
    junctions[3].addRoad(roads[5]);

    vehicles = new Vehicle[4];
    // j1 -(r1)> j2 -(r4)> j3 -(r6)> j4
    Queue<Junction> itinerary = new ArrayDeque<>();
    itinerary.add(junctions[0]);
    itinerary.add(junctions[1]);
    itinerary.add(junctions[2]);
    itinerary.add(junctions[3]);
    vehicles[0] = new Vehicle("v1", 30, itinerary);
    vehicles[0].moveToNextRoad();
    // j1 -(r3)> j4
    itinerary = new ArrayDeque<>();
    itinerary.add(junctions[0]);
    itinerary.add(junctions[3]);
    vehicles[1] = new Bicycle("v2", 10, itinerary);
    vehicles[1].moveToNextRoad();
    // j1 -(r2)> j3 -(r6)> j4
    itinerary = new ArrayDeque<>();
    itinerary.add(junctions[0]);
    itinerary.add(junctions[2]);
    itinerary.add(junctions[3]);
    vehicles[2] = new Car("v3", 80, itinerary, 1000, .0, 0, 0);
    vehicles[2].moveToNextRoad();
    // j1 -(r1)> j2
    itinerary = new ArrayDeque<>();
    itinerary.add(junctions[0]);
    itinerary.add(junctions[1]);
    vehicles[3] = new Vehicle("v4", 20, itinerary);

    for (Junction j : junctions) {
      j.advance();
    }

    for (Road r : roads) {
      r.advance();
      r.advance();
      r.advance();
    }

    // Para que v2 llegue a su destino (y además desaparezca de la carretera y la cola)
    junctions[3].advance();

    // Sólo para tener alguna carretera con dos vehículos, pero que no molesten en los cálculos
    vehicles[3].moveToNextRoad();
    vehicles[3].setFaulty(5);

  }

  @Test
  public void describeJunction() {

    Map<String, String> expected = new HashMap<>();

    expected.put(Junction.INFO[0], "j1");
    expected.put(Junction.INFO[1], "[]");
    expected.put(Junction.INFO[2], "[]");
    assertEquals("j1 has properly been described", expected, junctions[0].describe());

    expected.put(Junction.INFO[0], "j2");
    expected.put(Junction.INFO[1], "[(r1,green,[v1])]");
    expected.put(Junction.INFO[2], "[]");
    assertEquals("j2 has properly been described", expected, junctions[1].describe());

    expected.put(Junction.INFO[0], "j3");
    expected.put(Junction.INFO[1], "[(r2,green:3,[])]");
    expected.put(Junction.INFO[2], "[(r4,red,[])]");
    assertEquals("j3 has properly been described", expected, junctions[2].describe());

    expected.put(Junction.INFO[0], "j4");
    expected.put(Junction.INFO[1], "[(r5,green:1,[])]");
    expected.put(Junction.INFO[2], "[(r3,red,[]),(r6,red,[])]");
    assertEquals("j4 has properly been described", expected, junctions[3].describe());

  }

  @Test
  public void describeRoad() {

    Map<String, String> expected = new HashMap<>();

    expected.put(Road.INFO[0], "r1");
    expected.put(Road.INFO[1], "j1");
    expected.put(Road.INFO[2], "j2");
    expected.put(Road.INFO[3], "60");
    expected.put(Road.INFO[4], "20");
    expected.put(Road.INFO[5], "[v1,v4]");
    assertEquals("r1 has properly been described", expected, roads[0].describe());

    expected.put(Road.INFO[0], "r2");
    expected.put(Road.INFO[1], "j1");
    expected.put(Road.INFO[2], "j3");
    expected.put(Road.INFO[3], "80");
    expected.put(Road.INFO[4], "20");
    expected.put(Road.INFO[5], "[v3]");
    assertEquals("r2 has properly been described", expected, roads[1].describe());

    expected.put(Road.INFO[0], "r3");
    expected.put(Road.INFO[1], "j1");
    expected.put(Road.INFO[2], "j4");
    expected.put(Road.INFO[3], "30");
    expected.put(Road.INFO[4], "10");
    expected.put(Road.INFO[5], "[]");
    assertEquals("r3 has properly been described", expected, roads[2].describe());

    expected.put(Road.INFO[0], "r4");
    expected.put(Road.INFO[1], "j2");
    expected.put(Road.INFO[2], "j3");
    expected.put(Road.INFO[3], "30");
    expected.put(Road.INFO[4], "5");
    expected.put(Road.INFO[5], "[]");
    assertEquals("r4 has properly been described", expected, roads[3].describe());

    expected.put(Road.INFO[0], "r5");
    expected.put(Road.INFO[1], "j2");
    expected.put(Road.INFO[2], "j4");
    expected.put(Road.INFO[3], "200");
    expected.put(Road.INFO[4], "50");
    expected.put(Road.INFO[5], "[]");
    assertEquals("r5 has properly been described", expected, roads[4].describe());

    expected.put(Road.INFO[0], "r6");
    expected.put(Road.INFO[1], "j3");
    expected.put(Road.INFO[2], "j4");
    expected.put(Road.INFO[3], "150");
    expected.put(Road.INFO[4], "30");
    expected.put(Road.INFO[5], "[]");
    assertEquals("r6 has properly been described", expected, roads[5].describe());

  }

  @Test
  public void describeVehicle() {

    Map<String, String> expected = new HashMap<>();

    expected.put(Vehicle.INFO[0], "v1");
    expected.put(Vehicle.INFO[1], "r1");
    expected.put(Vehicle.INFO[2], "60");
    expected.put(Vehicle.INFO[3], "0");
    expected.put(Vehicle.INFO[4], "60");
    expected.put(Vehicle.INFO[5], "0");
    expected.put(Vehicle.INFO[6], "[j2,j3,j4]");
    assertEquals("v1 has properly been described", expected, vehicles[0].describe());

    expected.put(Vehicle.INFO[0], "v2");
    expected.put(Vehicle.INFO[1], "arrived");
    expected.put(Vehicle.INFO[2], "arrived");
    expected.put(Vehicle.INFO[3], "0");
    expected.put(Vehicle.INFO[4], "30");
    expected.put(Vehicle.INFO[5], "0");
    expected.put(Vehicle.INFO[6], "[]");
    assertEquals("v2 has properly been described", expected, vehicles[1].describe());

    expected.put(Vehicle.INFO[0], "v3");
    expected.put(Vehicle.INFO[1], "r2");
    expected.put(Vehicle.INFO[2], "60");
    expected.put(Vehicle.INFO[3], "20");
    expected.put(Vehicle.INFO[4], "60");
    expected.put(Vehicle.INFO[5], "0");
    expected.put(Vehicle.INFO[6], "[j3,j4]");
    assertEquals("v3 has properly been described", expected, vehicles[2].describe());

    expected.put(Vehicle.INFO[0], "v4");
    expected.put(Vehicle.INFO[1], "r1");
    expected.put(Vehicle.INFO[2], "0");
    expected.put(Vehicle.INFO[3], "0");
    expected.put(Vehicle.INFO[4], "0");
    expected.put(Vehicle.INFO[5], "5");
    expected.put(Vehicle.INFO[6], "[j2]");
    assertEquals("v4 has properly been described", expected, vehicles[3].describe());

  }

}