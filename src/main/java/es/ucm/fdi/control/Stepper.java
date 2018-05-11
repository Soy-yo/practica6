package es.ucm.fdi.control;

public class Stepper {

	private Runnable before;
	private Runnable during;
	private Runnable after;
  private Thread thread;

	private boolean stopRequested = false;

	public Stepper(Runnable before, Runnable during, Runnable after) {
		this.before = before;
		this.during = during;
		this.after = after;
	}

  public Thread start(int steps, int delay) {
    stopRequested = false;
    thread = new Thread(() -> {
      before.run();
      for (int i = 0; i < steps && !stopRequested; i++) {
        during.run();
        try {
          Thread.sleep(delay);
        } catch (InterruptedException ignored) {
          stopRequested = true;
        }
      }
      after.run();
    });
    thread.start();
    return thread;
  }

	public void stop() {
    thread.interrupt();
  }

}

