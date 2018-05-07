package es.ucm.fdi.control.layout.graphlayout;

import es.ucm.fdi.model.Junction;
import es.ucm.fdi.model.Road;
import es.ucm.fdi.model.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GraphComponent extends JComponent {

  private static final long serialVersionUID = 1L;

  /**
   * The radius of each node
   */
  private static final int NODE_RADIUS = 20;

  /**
   * The radius of each dot
   */
  private static final int DOT_RADIUS = 5;

  /**
   * An inner class that represent a location of a node. Fields cX and cY are
   * the center of the node, and fields tX and tY are the location where the
   * label of the node is drawn. This is calculated for each node in the
   * method {@code calculateNodeCoordinates()}
   */
  private class Point {
    int cX;
    int cY;
    int tX;
    int tY;

    public Point(int cX, int cY, int tX, int tY) {
      this.cX = cX;
      this.cY = cY;
      this.tX = tX;
      this.tY = tY;
    }
  }

  /**
   * The graph to layout
   */
  private Graph graph;

  /**
   * A map to store the location of each node
   */
  private Map<String, Point> nodesPositions;

  /**
   * width and height of the window when it was last resized. When change we
   * recalculate the location of nodes to scale the graph, etc.
   */
  private int lastWidth;
  private int lastHeight;

  public GraphComponent() {
    nodesPositions = new HashMap<>();
    setMinimumSize(new Dimension(500, 500));
    setPreferredSize(new Dimension(500, 500));
    lastWidth = -1;
    lastHeight = -1;
  }

  public void clear() {
    graph = null;
    refresh();
  }

  public void paint(Graphics graphics) {
    Graphics2D g = (Graphics2D) graphics;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    if (graph == null || graph.getNodes().size() == 0) {
      g.setColor(Color.RED);
      g.drawString("No graph yet!", getWidth() / 2 - 50, getHeight() / 2);
    } else {
      drawMap(g);
    }
  }

  private void drawMap(Graphics g) {

    // if the size of the component has changed since the last time we
    // calculated the positions of the nodes, then we recalculate again.
    // This way the map get scaled down/up.
    if (lastHeight != getHeight() || lastWidth != getWidth()) {
      lastHeight = getHeight();
      lastWidth = getWidth();
      calculateNodeCoordinates();
    }

    // draw nodes
    for (Node j : graph.getNodes()) {
      Point p = nodesPositions.get(j.getId());
      g.setColor(Color.BLUE);
      g.fillOval(p.cX - NODE_RADIUS / 2, p.cY - NODE_RADIUS / 2,
          NODE_RADIUS, NODE_RADIUS);
      g.setColor(Color.BLACK);
      g.drawString(j.getId(), p.tX, p.tY);
    }

    // draw edges
    for (Edge e : graph.getEdges()) {
      Point p1 = nodesPositions.get(e.getSource().getId());
      Point p2 = nodesPositions.get(e.getTarget().getId());

      // draw the edge
      //Color arrowColor = Math.random() > 0.5 ? Color.RED : Color.GREEN;
      Color arrowColor = e.isEnabled() ? Color.GREEN : Color.RED;
      drawArrowLine(g, p1.cX, p1.cY, p2.cX, p2.cY, 15, 5, Color.BLACK,
          arrowColor);
      int idx = (p1.cX + p2.cX) / 2;
      int idy = (p1.cY + p2.cY) / 2;
      g.setColor(Color.RED);
      g.drawString(e.getId(), idx, idy);

      // draw dots as circles. Dots at the same location are drawn with
      // circles of
      // different diameter.
      int lastLocation = -1;
      int diam = DOT_RADIUS;
      for (Dot d : e.getDots()) {
        if (lastLocation != d.getLocation()) {
          lastLocation = d.getLocation();
          diam = DOT_RADIUS;
        } else {
          diam += DOT_RADIUS;
        }
        Color dotColor = Math.random() > 0.5 ? Color.MAGENTA
            : Color.ORANGE;
        drawCircleOnALine(g, p1.cX, p1.cY, p2.cX, p2.cY, e.getLength(),
            d.getLocation(), diam, dotColor, d.getId());
      }
    }
  }

  /**
   * put the objects in a circle, for each one store the center coordinate and
   * a coordinate for a corresponding text.
   */
  private void calculateNodeCoordinates() {

    int r = Math.min(lastHeight, lastWidth) / 2 - NODE_RADIUS - 50; // 50
    // for
    // text
    int tr = (r + NODE_RADIUS + 10);

    int xc = lastWidth / 2 - 10;
    int yc = lastHeight / 2 - 10;

    double slice = 2 * Math.PI / graph.getNodes().size();
    int i = 0;
    for (Node n : graph.getNodes()) {

      double angle = slice * i;
      int cX = (int) (xc + r * Math.cos(angle));
      int cY = (int) (yc + r * Math.sin(angle));
      int tX = (int) (xc + tr * Math.cos(angle));
      int tY = (int) (yc + tr * Math.sin(angle));

      nodesPositions.put(n.getId(), new Point(cX, cY, tX, tY));
      i++;
    }

  }

  /**
   * Draws a circle on the line from (x1,y1) to (x2,y2). Assuming the
   * (virtual) length of the line is virtualLength, the circles is drawn at
   * location virtualLocation (0..virtualLength). The diameter is 'diam'
   */
  private void drawCircleOnALine(Graphics g, int x1, int y1, int x2, int y2,
                                 int virtualLength, int virtualLocation, int diam, Color c,
                                 String txt) {

    // The actual length of the line
    double lineActualLength = Math.sqrt(Math.pow(x1 - x2, 2)
        + Math.pow(y1 - y2, 2)) - 45;

    // the angle of the line with the horizontal axis
    double alpha = Math.atan(((double) Math.abs(x1 - x2))
        / ((double) Math.abs(y1 - y2)));

    // the actual location on the line (0..lineActualLength)
    double actualLocation = lineActualLength * ((double) virtualLocation)
        / ((double) virtualLength) + 15;

    // the coordinates of the location
    double x = Math.sin(alpha) * actualLocation;
    double y = Math.cos(alpha) * actualLocation;

    // signs repressing the direction of the line (left/right, up/down)
    int xDir = x1 < x2 ? 1 : -1;
    int yDir = y1 < y2 ? 1 : -1;

    // draw the point
    g.setColor(c);
    g.drawOval(x1 + xDir * ((int) x) - diam / 2, y1 + yDir * ((int) y)
        - diam / 2, diam, diam);

    // draw the text
    g.setColor(Color.DARK_GRAY);
    g.drawString(txt, x1 + xDir * ((int) x) - diam / 2, y1 + yDir
        * ((int) y) - diam / 2);
  }

  /**
   * Draws a line from (x1,y1) to (x2,y2) with an arrow of width d and height
   * h. The color of the line is lineColor and that of the arrow is
   * arrowColor.
   */
  private void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2,
                             int d, int h, Color lineColor, Color arrowColor) {
    int dx = x2 - x1, dy = y2 - y1;
    double D = Math.sqrt(dx * dx + dy * dy);
    double xm = D - d, xn = xm, ym = h, yn = -h, x;
    double sin = dy / D, cos = dx / D;

    x = xm * cos - ym * sin + x1;
    ym = xm * sin + ym * cos + y1;
    xm = x;

    x = xn * cos - yn * sin + x1;
    yn = xn * sin + yn * cos + y1;
    xn = x;

    int[] xpoints = {x2, (int) xm, (int) xn};
    int[] ypoints = {y2, (int) ym, (int) yn};

    g.setColor(lineColor);
    g.drawLine(x1, y1, x2, y2);
    g.setColor(arrowColor);
    g.fillPolygon(xpoints, ypoints, 3);
  }

  public void setGraph(Graph graph) {
    this.graph = graph;
    calculateNodeCoordinates();
    refresh();
  }

  public void refresh() {
    repaint();
  }

  /**
   * Genera un grafo con los objetos del simulador
   */
  public void generateGraph(Collection<Vehicle> vehicles, Collection<Road> roads,
                            Collection<Junction> junctions, Set<Road> greenRoads) {
    graph = new Graph();
    Map<String, Node> js = new HashMap<>();
    for (Junction j : junctions) {
      Node n = new Node(j.getId());
      js.put(j.getId(), n);
      graph.addNode(n);
    }
    Map<String, Edge> rs = new HashMap<>();
    for (Road r : roads) {
      Node source = js.get(r.getSource());
      Node destiny = js.get(r.getDestiny());
      Edge e = new Edge(r.getId(), source, destiny, r.getLength(), greenRoads.contains(r));
      rs.put(r.getId(), e);
      graph.addEdge(e);
    }
    for (Vehicle v : vehicles) {
      if (!v.hasArrived()) {
        Edge e = rs.get(v.getRoad().getId());
        e.addDot(new Dot(v.getId(), v.getLocation()));
      }
    }
    calculateNodeCoordinates();
    refresh();
  }

}
