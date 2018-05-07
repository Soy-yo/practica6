package es.ucm.fdi.control.layout.graphlayout;

import java.util.ArrayList;
import java.util.List;

public class Edge {

  private String id;
  private Node source;
  private Node target;
  private int length;
  private List<Dot> dots;
  private boolean enabled;

  public Edge(String id, Node source, Node target, int length, boolean enabled) {
    this.source = source;
    this.target = target;
    this.id = id;
    this.length = length;
    dots = new ArrayList<>();
    this.enabled = enabled;
  }

  public void addDot(Dot e) {
    dots.add(e);
  }

  public String getId() {
    return id;
  }

  public Node getSource() {
    return source;
  }

  public Node getTarget() {
    return target;
  }

  public int getLength() {
    return length;
  }

  public List<Dot> getDots() {
    return dots;
  }

  public boolean isEnabled() {
    return enabled;
  }

}
