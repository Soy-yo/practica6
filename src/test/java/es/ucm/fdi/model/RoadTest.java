package es.ucm.fdi.model;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

import static org.junit.Assert.assertEquals;

public class RoadTest {

  @Test
  public void simpleMoving() {
    Junction source = new Junction("jt1");
    Junction dest = new Junction("jt2");
    Queue<Junction> queue = new ArrayDeque<>();
    queue.add(source);
    queue.add(dest);

    Road road = new Road("rt1", 100, 20, "jt1", "jt2");
    dest.addRoad(road);
    Vehicle vehicle1 = new Vehicle("vt1", 20, queue);
    Vehicle vehicle2 = new Vehicle("vt2", 20, new ArrayDeque<>(queue));

    vehicle1.moveToNextRoad();
    vehicle2.moveToNextRoad();

    road.advance();

    Map<String, String> correct = new LinkedHashMap<>();
    correct.put("", "road_report");
    correct.put("id", "rt1");
    correct.put("time", "1");
    correct.put("state", "(vt1,11),(vt2,11)");

    Map<String, String> result = road.generateReport(1);

    assertEquals(correct, result);
  }

  @Test
  public void faultyVehicle() {
    Junction source = new Junction("jt1");
    Junction dest = new Junction("jt2");
    Queue<Junction> queue = new ArrayDeque<>();
    queue.add(source);
    queue.add(dest);

    Road road = new Road("rt1", 100, 20, "jt1", "jt2");
    dest.addRoad(road);

    Vehicle broken = new Vehicle("vt1", 20, queue);
    Vehicle v = new Vehicle("vt2", 15, new ArrayDeque<>(queue));
    broken.moveToNextRoad();
    road.advance();

    v.moveToNextRoad();
    broken.setFaulty(2);

    road.advance();
    road.advance();
    road.advance();

    Map<String, String> correct = new LinkedHashMap<>();
    correct.put("", "road_report");
    correct.put("id", "rt1");
    correct.put("time", "1");
    correct.put("state", "(vt1,31),(vt2,21)");

    Map<String, String> result = road.generateReport(1);

    assertEquals(correct, result);
  }

  @Test
  public void dirtTest() {
    Junction source = new Junction("jt1");
    Junction dest = new Junction("jt2");
    Queue<Junction> queue = new ArrayDeque<>();
    queue.add(source);
    queue.add(dest);

    Road road = new DirtRoad("rt1", 100, 20, "jt1", "jt2");
    dest.addRoad(road);
    Vehicle vehicle1 = new Vehicle("vt1", 20, queue);
    Vehicle vehicle2 = new Vehicle("vt2", 20, new ArrayDeque<>(queue));
    Vehicle vehicle3 = new Vehicle("vt3", 20, new ArrayDeque<>(queue));
    Vehicle vehicle4 = new Vehicle("vt4", 20, new ArrayDeque<>(queue));
    Vehicle vehicle5 = new Vehicle("vt5", 20, new ArrayDeque<>(queue));

    vehicle1.moveToNextRoad();
    vehicle2.moveToNextRoad();
    vehicle3.moveToNextRoad();
    vehicle4.moveToNextRoad();
    vehicle5.moveToNextRoad();

    road.advance();

    Map<String, String> correct = new LinkedHashMap<>();
    correct.put("", "road_report");
    correct.put("id", "rt1");
    correct.put("time", "1");
    correct.put("type", "dirt");
    correct.put("state", "(vt1,20),(vt2,20),(vt3,20),(vt4,20),(vt5,20)");

    Map<String, String> result = road.generateReport(1);

    assertEquals(correct, result);

    vehicle5.setFaulty(10);
    road.advance();

    correct.put("state", "(vt1,40),(vt2,40),(vt3,40),(vt4,40),(vt5,20)");

    result = road.generateReport(1);

    assertEquals(correct, result);

    vehicle1.setFaulty(10);
    vehicle2.setFaulty(10);
    vehicle3.setFaulty(10);
    road.advance();

    correct.put("state", "(vt4,45),(vt1,40),(vt2,40),(vt3,40),(vt5,20)");

    result = road.generateReport(1);

    assertEquals(correct, result);
  }

  @Test
  public void laneTest() {
    Junction source = new Junction("jt1");
    Junction dest = new Junction("jt2");
    Queue<Junction> queue = new ArrayDeque<>();
    queue.add(source);
    queue.add(dest);

    Road road = new LaneRoad("rt1", 100, 40, "jt1", "jt2", 3);
    dest.addRoad(road);
    Vehicle vehicle1 = new Vehicle("vt1", 20, queue);
    Vehicle vehicle2 = new Vehicle("vt2", 20, new ArrayDeque<>(queue));
    Vehicle vehicle3 = new Vehicle("vt3", 20, new ArrayDeque<>(queue));
    Vehicle vehicle4 = new Vehicle("vt4", 20, new ArrayDeque<>(queue));
    Vehicle vehicle5 = new Vehicle("vt5", 20, new ArrayDeque<>(queue));
    Vehicle vehicle6 = new Vehicle("vt6", 20, new ArrayDeque<>(queue));
    Vehicle vehicle7 = new Vehicle("vt7", 20, new ArrayDeque<>(queue));

    vehicle1.moveToNextRoad();
    vehicle2.moveToNextRoad();
    vehicle3.moveToNextRoad();
    vehicle4.moveToNextRoad();
    vehicle5.moveToNextRoad();
    vehicle6.moveToNextRoad();
    vehicle7.moveToNextRoad();

    road.advance();

    Map<String, String> correct = new LinkedHashMap<>();
    correct.put("", "road_report");
    correct.put("id", "rt1");
    correct.put("time", "1");
    correct.put("type", "lanes");
    correct.put("state", "(vt1,18),(vt2,18),(vt3,18),(vt4,18),(vt5,18),(vt6,18),(vt7,18)");

    Map<String, String> result = road.generateReport(1);

    assertEquals(correct, result);

    vehicle1.setFaulty(10);
    vehicle2.setFaulty(10);
    road.advance();

    correct.put("state", "(vt3,36),(vt4,36),(vt5,36),(vt6,36),(vt7,36),(vt1,18),(vt2,18)");

    result = road.generateReport(1);

    assertEquals(correct, result);

    vehicle3.setFaulty(10);
    vehicle4.setFaulty(10);
    vehicle5.setFaulty(10);
    road.advance();

    correct.put("state", "(vt6,45),(vt7,45),(vt3,36),(vt4,36),(vt5,36),(vt1,18),(vt2,18)");

    result = road.generateReport(1);

    assertEquals(correct, result);
  }

}
