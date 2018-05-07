package es.ucm.fdi.model;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

import static org.junit.Assert.assertEquals;

public class JunctionTest {

  @Test
  public void simpleAdvance() {
    Junction source = new Junction("jt1");
    Junction mid = new Junction("jt2");
    Junction dest = new Junction("jt3");
    Queue<Junction> queue = new ArrayDeque<>();
    queue.add(source);
    queue.add(mid);
    queue.add(dest);

    Vehicle vehicle = new Vehicle("vt1", 20, queue);
    Road road1 = new Road("rt1", 20, 20, "jt1", "jt2");
    Road road2 = new Road("rt2", 20, 20, "jt2", "jt3");
    mid.addRoad(road1);
    dest.addRoad(road2);
    vehicle.moveToNextRoad();
    road1.advance();

    Map<String, String> correct = new LinkedHashMap<>();
    correct.put("", "junction_report");
    correct.put("id", "jt2");
    correct.put("time", "1");
    correct.put("queues", "(rt1,red,[vt1])");

    Map<String, String> result = mid.generateReport(1);

    assertEquals(correct, result);

    mid.advance(); // red -> green
    mid.advance();

    correct.put("queues", "(rt1,green,[])");

    result = mid.generateReport(1);

    assertEquals(correct, result);
  }

  @Test
  public void reportTest() {
    Junction source = new Junction("jt1");
    Junction dest = new Junction("jt2");
    Queue<Junction> queue = new ArrayDeque<>();
    queue.add(source);
    queue.add(dest);

    Road road1 = new Road("rt1", 100, 50, "jt1", "jt2");
    Road road2 = new Road("rt2", 100, 50, "", "");

    dest.addRoad(road1);
    dest.addRoad(road2);

    Vehicle vehicle1 = new Vehicle("vt1", 20, queue);
    Vehicle vehicle2 = new Vehicle("vt2", 20, new ArrayDeque<>(queue));
    vehicle1.moveToNextRoad();
    vehicle2.moveToNextRoad();

    dest.vehicleIn(vehicle1);
    dest.vehicleIn(vehicle2);

    Map<String, String> correct = new LinkedHashMap<>();
    correct.put("", "junction_report");
    correct.put("id", "jt2");
    correct.put("time", "1");
    correct.put("queues", "(rt1,red,[vt1,vt2]),(rt2,red,[])");

    Map<String, String> result = dest.generateReport(1);

    assertEquals(correct, result);
  }

  @Test
  public void roundRobinTest() {
    Junction source1 = new Junction("jt1");
    Junction source2 = new Junction("jt2");
    Junction dest = new RoundRobinJunction("jt4", 1, 3);

    dest.addRoad(new Road("rt1", 10, 100, "jt1", "jt4"));
    dest.addRoad(new Road("rt2", 10, 100, "jt2", "jt4"));
    dest.addRoad(new Road("rt3", 10, 100, "jt3", "jt4"));

    Queue<Junction> queue = new ArrayDeque<>();
    queue.add(source1);
    queue.add(dest);
    // se usa cada vez
    for (int i = 1; i <= 3; i++) {
      Vehicle v = new Vehicle("vt1" + i, 100, new ArrayDeque<>(queue));
      v.moveToNextRoad();
      dest.vehicleIn(v);
    }

    queue.clear();
    queue.add(source2);
    queue.add(dest);
    // no se usa cada vez
    for (int i = 1; i <= 2; i++) {
      Vehicle v = new Vehicle("vt2" + i, 100, new ArrayDeque<>(queue));
      v.moveToNextRoad();
      dest.vehicleIn(v);
    }

    // switch lights
    for (int i = 0; i < 4; i++) {
      dest.advance();
    }

    Map<String, String> correct = new LinkedHashMap<>();
    correct.put("", "junction_report");
    correct.put("id", "jt4");
    correct.put("time", "1");
    correct.put("queues", "(rt1,red,[]),(rt2,green:3,[vt21,vt22]),(rt3,red,[])");
    correct.put("type", "rr");

    Map<String, String> result = dest.generateReport(1);

    assertEquals(correct, result);

    // switch lights
    for (int i = 0; i < 3; i++) {
      dest.advance();
    }

    correct.put("queues", "(rt1,red,[]),(rt2,red,[]),(rt3,green:3,[])");

    result = dest.generateReport(1);

    assertEquals(correct, result);

    // switch lights
    for (int i = 0; i < 3; i++) {
      dest.advance();
    }

    correct.put("queues", "(rt1,green:2,[]),(rt2,red,[]),(rt3,red,[])");

    result = dest.generateReport(1);

    assertEquals(correct, result);
  }

  @Test
  public void mostCrowdedTest() {
    Junction source1 = new Junction("jt1");
    Junction source2 = new Junction("jt2");
    Junction source3 = new Junction("jt3");
    Junction dest = new MostCrowdedJunction("jt4");

    dest.addRoad(new Road("rt1", 10, 100, "jt1", "jt4"));
    dest.addRoad(new Road("rt2", 10, 100, "jt2", "jt4"));
    dest.addRoad(new Road("rt3", 10, 100, "jt3", "jt4"));

    Queue<Junction> queue = new ArrayDeque<>();
    queue.add(source1);
    queue.add(dest);
    for (int i = 1; i <= 3; i++) {
      Vehicle v = new Vehicle("vt1" + i, 100, new ArrayDeque<>(queue));
      v.moveToNextRoad();
      dest.vehicleIn(v);
    }

    queue.clear();
    queue.add(source2);
    queue.add(dest);
    for (int i = 1; i <= 8; i++) {
      Vehicle v = new Vehicle("vt2" + i, 100, new ArrayDeque<>(queue));
      v.moveToNextRoad();
      dest.vehicleIn(v);
    }

    queue.clear();
    queue.add(source3);
    queue.add(dest);
    for (int i = 1; i <= 2; i++) {
      Vehicle v = new Vehicle("vt3" + i, 100, new ArrayDeque<>(queue));
      v.moveToNextRoad();
      dest.vehicleIn(v);
    }

    dest.advance();

    Map<String, String> correct = new LinkedHashMap<>();
    correct.put("", "junction_report");
    correct.put("id", "jt4");
    correct.put("time", "1");
    correct.put("queues",
        "(rt1,red,[vt11,vt12,vt13]),(rt2,green:4,[vt21,vt22,vt23,vt24,vt25,vt26,vt27,vt28])," +
            "(rt3,red,[vt31,vt32])");
    correct.put("type", "mc");

    Map<String, String> result = dest.generateReport(1);

    assertEquals(correct, result);

    for (int i = 0; i < 4; i++) {
      dest.advance();
    }

    correct.put("queues",
        "(rt1,green:1,[vt11,vt12,vt13]),(rt2,red,[vt25,vt26,vt27,vt28]),(rt3,red,[vt31,vt32])");

    result = dest.generateReport(1);

    assertEquals(correct, result);

    dest.advance();

    correct.put("queues",
        "(rt1,red,[vt12,vt13]),(rt2,green:2,[vt25,vt26,vt27,vt28]),(rt3,red,[vt31,vt32])");

    result = dest.generateReport(1);

    assertEquals(correct, result);

    for (int i = 0; i < 2; i++) {
      dest.advance();
    }

    correct.put("queues", "(rt1,red,[vt12,vt13]),(rt2,red,[vt27,vt28]),(rt3,green:1,[vt31,vt32])");

    result = dest.generateReport(1);

    assertEquals(correct, result);

  }

}