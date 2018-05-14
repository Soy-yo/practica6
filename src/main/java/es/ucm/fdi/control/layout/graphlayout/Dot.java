package es.ucm.fdi.control.layout.graphlayout;

public class Dot {

  private String id;
  private int location;
  private int value;

  public Dot(String id, int location, int value) {
    this.id = id;
    this.location = location;
    this.value = value;
  }

  public String getId() {
    return id;
  }

  public int getLocation() {
    return location;
  }

  public int getValue() {
    return value;
  }

}
