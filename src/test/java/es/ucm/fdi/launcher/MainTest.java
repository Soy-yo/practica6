package es.ucm.fdi.launcher;

import es.ucm.fdi.excepcions.SimulatorError;
import org.junit.Test;

public class MainTest {

  private static final String RES = "src/test/resources/";

  @Test
  public void basicTest() throws Exception {
    Main.test(RES + "examples/basic");
  }

  @Test
  public void advancedTest() throws Exception {
    Main.test(RES + "examples/advanced");
  }

  @Test(expected = SimulatorError.class)
  public void errTest() throws Exception {
    Main.test(RES + "examples/err");
  }

}