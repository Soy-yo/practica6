package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class EventTest {

  private Event.Builder eb = new Implementation();

  @Test
  public void validIdTest1() {
    assertTrue(eb.isValid("v1"));
  }

  @Test
  public void validIdTest2() {
    assertTrue(eb.isValid("_v1"));
  }

  @Test
  public void validIdTest3() {
    // sin espacios
    assertFalse(eb.isValid("vehicle 1"));
  }

  @Test
  public void validIdTest4() {
    assertTrue(eb.isValid("vehicle_1"));
  }

  @Test
  public void validIdTest5() {
    // alfabeto inglés
    assertFalse(eb.isValid("vehicle_ñ"));
  }

  @Test
  public void validIdTest6() {
    // sin guiones
    assertFalse(eb.isValid("v-1"));
  }

  @Test
  public void validIdTest7() {
    assertTrue(eb.isValid("1234"));
  }

  @Test
  public void validIdTest8() {
    assertTrue(eb.isValid("vehicle"));
  }

  /**
   * Prueba para describe. No se hace en el respectivo DescribableTest por la constructora
   * privada que tienen los eventos, con lo cual es más sencillo hacerlo desde el mismo paquete.
   */
  @Test
  public void describeTest() {

    Map<String, String> expected = new HashMap<>();

    expected.put(Event.INFO[0], "");
    expected.put(Event.INFO[1], "5");
    expected.put(Event.INFO[2], "Break Vehicles [v1,v2]");
    assertEquals("MakeVehicleFaulty has properly been described", expected,
        new MakeVehicleFaultyEvent(5, "", new String[]{"v1", "v2"}, 15).describe());

    expected.put(Event.INFO[1], "8");
    expected.put(Event.INFO[2], "New Junction j1");
    assertEquals("NewJunctionEvent has properly been described", expected,
        new NewJunctionEvent(8, "j1").describe());

    expected.put(Event.INFO[1], "0");
    expected.put(Event.INFO[2], "New Road r1");
    assertEquals("NewRoadEvent has properly been described", expected,
        new NewRoadEvent(0, "r1", "", "", 0, 0).describe());

    expected.put(Event.INFO[1], "0");
    expected.put(Event.INFO[2], "New Bike v1");
    assertEquals("Also works with advanced objects", expected,
        new NewBicycleEvent(0, "v1", 10, new String[0]).describe());

  }

  private class Implementation implements Event.Builder {

    @Override
    public Event parse(IniSection section) {
      throw new UnsupportedOperationException("Nothing to do");
    }

    @Override
    public String getEventName() {
      throw new UnsupportedOperationException("Nothing to do");
    }

    @Override
    public String getEventFileTemplate() {
      throw new UnsupportedOperationException("Nothing to do");
    }

  }

}