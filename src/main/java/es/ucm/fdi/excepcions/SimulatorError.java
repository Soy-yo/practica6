package es.ucm.fdi.excepcions;

/**
 * Para los errores que puedan surgir durante el transcurso de la simulación
 */
public class SimulatorError extends RuntimeException {

  public SimulatorError() {
  }

  public SimulatorError(String msg) {
    super(msg);
  }

  public SimulatorError(String msg, Throwable cause) {
    super(msg, cause);
  }

}
