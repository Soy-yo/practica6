package es.ucm.fdi.util;

import org.junit.Test;

import static es.ucm.fdi.util.SimulatorConstants.c;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SimulatorConstantsTest {

  @Test
  public void testConstants() {
    String test = c("TEST");
    assertEquals("English by default", test, "Constants are fun");
    SimulatorConstants.setLanguage("es");
    test = c("TEST");
    assertEquals("Properly switched to Spanish", "Las constantes son divertidas", test);
    SimulatorConstants.setLanguage("it");
    test = c("TEST");
    assertEquals("Italian not allowed; it gets default value: en", "Constants are fun", test);
    test = c("EMPTY");
    assertEquals("Constant with no lang tag returns the constant ID", "EMPTY", test);
    try {
      c("UNKNOWN");
      fail("Unknown tag throws an exception");
    } catch (IllegalArgumentException ignored) {
    }
  }

}