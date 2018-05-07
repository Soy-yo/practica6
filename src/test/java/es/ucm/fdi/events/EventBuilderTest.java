package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import static org.junit.Assert.*;

public class EventBuilderTest {

  @Test
  public void simpleVehicleEvent() {
    TestSimulator simulator = new TestSimulator();
    Junction j1 = new Junction("jt1");
    Junction j2 = new Junction("jt2");
    Road r = new Road("rt1", 10, 10, "jt1", "jt2");
    simulator.addSimulatedObject(j1);
    simulator.addSimulatedObject(j2);
    simulator.addSimulatedObject(r);
    List<Junction> junctions = Arrays.asList(j1, j2);

    IniSection section = new IniSection("new_vehicle");
    section.setValue("time", 0);
    section.setValue("id", "vt1");
    section.setValue("max_speed", 10);
    section.setValue("itinerary", "jt1,jt2");

    Event event = EventBuilder.parse(section);

    assertNotNull(event);
    event.execute(simulator);

    // No se puede usar assertEquals en las colecciones porque no sobreescriben equals()
    int i = 0;
    for (Junction j : simulator.getJunctions()) {
      assertEquals(junctions.get(i), j);
      i++;
    }
    assertEquals("vt1", simulator.getVehicles().iterator().next().getId());
  }

  @Test
  public void unknownEvent() {
    IniSection section = new IniSection("unknown_tag");
    section.setValue("id", "some_id");
    assertNull(EventBuilder.parse(section));
  }

  @Test
  public void wrongIds() {
    IniSection section = new IniSection("new_vehicle");
    section.setValue("id", "");
    try {
      EventBuilder.parse(section);
      fail();
    } catch (IllegalStateException ignored) {
    }
    section.setValue("id", "hello world");
    try {
      EventBuilder.parse(section);
      fail();
    } catch (IllegalStateException ignored) {
    }
    section.setValue("id", "hello-world");
    try {
      EventBuilder.parse(section);
      fail();
    } catch (IllegalStateException ignored) {
    }
  }

  @Test
  public void wrongType() {
    IniSection section = new IniSection("new_vehicle");
    section.setValue("time", "0");
    section.setValue("id", "vehicle");
    section.setValue("itinerary", "j1,j2");
    section.setValue("max_speed", "20000");
    section.setValue("type", "spaceship");
    assertNull(EventBuilder.parse(section));
  }

  @Test
  public void wrongTime() {
    IniSection section = new IniSection("new_junction");
    section.setValue("time", "-1");
    section.setValue("id", "j");
    try {
      EventBuilder.parse(section);
      fail();
    } catch (IllegalStateException ignored) {
    }
    section.setValue("time", "hello_world");
    try {
      EventBuilder.parse(section);
      fail();
    } catch (IllegalStateException ignored) {
    }
  }

  @Test
  public void wrongItinerary() {
    IniSection section = new IniSection("new_vehicle");
    section.setValue("time", "0");
    section.setValue("id", "vehicle");
    section.setValue("itinerary", "");
    section.setValue("max_speed", "20");
    try {
      EventBuilder.parse(section);
      fail();
    } catch (IllegalStateException ignored) {
    }
    section.setValue("itinerary", "");
    try {
      EventBuilder.parse(section);
      fail();
    } catch (IllegalStateException ignored) {
    }
  }

  private class TestSimulator extends TrafficSimulator {

    RoadMap roadMapTest = new RoadMap();

    @Override
    public void addSimulatedObject(SimulatedObject o) {
      roadMapTest.addSimulatedObject(o);
    }

    @Override
    public Queue<Junction> getPath(String[] path) {
      return roadMapTest.getPath(path);
    }

    @Override
    public Collection<Vehicle> getVehicles() {
      return roadMapTest.getVehicles();
    }

  }

}