package es.ucm.fdi.control;

public class Stepper {
	private Runnable before;
	private Runnable during;
	private Runnable after;
	
	private boolean paused = false;
	private boolean stopRequested = false;
	private int steps;
	
	public Stepper(Runnable before, Runnable during, Runnable after) {
		this.before = before;
		this.during = during;
		this.after = after;
	}
	
	public Thread start(int steps, int delay) {
		return null;
	}
	
	public void pause() {
		paused = true;
	}
	
	public void stop() {
		stopRequested = true;
	}
	
}

