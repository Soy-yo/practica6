package es.ucm.fdi.control.layout.graphlayout;

public class Dot {

  private String id;
  private int location;

  public Dot(String id, int location) {
    this.id = id;
    this.location = location;
  }

  public String getId() {
    return id;
  }

  public int getLocation() {
    return location;
  }

}
