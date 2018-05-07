package es.ucm.fdi.control.layout.graphlayout;

import java.util.ArrayList;
import java.util.List;

public class Graph {

  private List<Edge> edges;
  private List<Node> nodes;

  public Graph() {
    edges = new ArrayList<>();
    nodes = new ArrayList<>();
  }

  public void addEdge(Edge e) {
    edges.add(e);
  }

  public void addNode(Node n) {
    nodes.add(n);
  }

  public List<Edge> getEdges() {
    return edges;
  }

  public List<Node> getNodes() {
    return nodes;
  }

  public Node getNode(String id) {
    Node result = null;
    for (Node n : nodes) {
      if (n.getId().equals(id)) {
        result = n;
      }
    }
    return result;
  }

  public Edge getEdge(String id) {
    Edge result = null;
    for (Edge e : edges) {
      if (e.getId().equals(id)) {
        result = e;
      }
    }
    return result;
  }

}
