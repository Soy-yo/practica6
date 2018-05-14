package es.ucm.fdi.control.layout;

import es.ucm.fdi.control.Controller;
import es.ucm.fdi.control.Stepper;
import es.ucm.fdi.control.layout.graphlayout.*;
import es.ucm.fdi.events.Event;
import es.ucm.fdi.events.EventBuilder;
import es.ucm.fdi.model.Junction;
import es.ucm.fdi.model.Road;
import es.ucm.fdi.model.TrafficSimulator;
import es.ucm.fdi.model.Vehicle;
import es.ucm.fdi.util.TextAreaOutputStream;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

/**
 * Ventana principal para el modo GUI
 */
public class SimulatorWindow extends JFrame {

  public static Dimension WINDOW_SIZE = new Dimension(1000, 1000);
  private static final Dimension HORIZONTAL_THIRD = new Dimension(
      WINDOW_SIZE.width / 6, WINDOW_SIZE.height / 8);
  private static final Dimension VERTICAL_THIRD = new Dimension(
      WINDOW_SIZE.width / 3, WINDOW_SIZE.height / 8);

  private static final String READ_FILE_ERROR = "Error reading file";
  private static final String WRITE_FILE_ERROR = "Error writing file";

  private Controller controller;

  // Elementos importantes de la interfaz
  private SimulatorTextArea eventsEditor;
  private JScrollPane eventsEditorScroll;
  private SimulatorTable<Event> eventsQueue;
  private SimulatorTextArea reportsArea;
  private SimulatorTable<Vehicle> vehiclesTable;
  private SimulatorTable<Road> roadsTable;
  private SimulatorTable<Junction> junctionsTable;
  private GraphComponent roadMap;
  private JLabel statusBarText;
  private JSpinner stepDelay;
  private JSpinner stepCounter;
  private JTextField time;

  private Map<Command, SimulatorAction> actionMap;

  private Stepper stepper;

  // Dirección desde donde se abrirá el siguiente JFileChooser
  private String previousPath;

  public SimulatorWindow(String title, File initialFile, int steps, Dimension dimension) {
    super(title);
    controller = new Controller(new TrafficSimulator());
    initialize(dimension, initialFile, steps);
    stepper = new Stepper(
        () -> SwingUtilities.invokeLater(() -> {
          enableActions(false, Command.LOAD_EVENTS, Command.SAVE_EVENTS, Command.CLEAR_EVENTS,
              Command.MOVE_EVENTS, Command.RUN, Command.RESET, Command.GENERATE_REPORT);
          enableActions(true, Command.STOP);
          stepCounter.setEnabled(false);
          stepDelay.setEnabled(false);
        }),
        () -> controller.run(1),
        () -> SwingUtilities.invokeLater(() -> {
          enableActions(true, Command.LOAD_EVENTS, Command.SAVE_EVENTS, Command.CLEAR_EVENTS,
              Command.MOVE_EVENTS, Command.RUN, Command.RESET, Command.GENERATE_REPORT);
          enableActions(false, Command.STOP);
          stepCounter.setEnabled(true);
          stepDelay.setEnabled(true);
        }));
    if (initialFile != null) {
      previousPath = initialFile.getPath();
    }
  }

  private void initialize(Dimension dimension, File initialFile, int steps) {
    setSize(dimension);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    generateActionMap();
    addSections(initialFile);
    addToolBar(steps);
    addStatusBar();
    addListeners();
    setVisible(true);
  }

  /**
   * Genera el mapa que une cada comando con la acción que ejecuta
   */
  private void generateActionMap() {
    actionMap = new HashMap<>();
    actionMap.put(Command.LOAD_EVENTS,
        new SimulatorAction(Command.LOAD_EVENTS, this::loadEvents));
    actionMap.put(Command.SAVE_EVENTS,
        new SimulatorAction(Command.SAVE_EVENTS, this::saveEvents));
    actionMap.put(Command.CLEAR_EVENTS,
        new SimulatorAction(Command.CLEAR_EVENTS, this::clearEvents));
    actionMap.put(Command.MOVE_EVENTS,
        new SimulatorAction(Command.MOVE_EVENTS, this::readEvents));
    actionMap.put(Command.RUN,
        new SimulatorAction(Command.RUN, this::run));
    actionMap.put(Command.STOP,
        new SimulatorAction(Command.STOP, this::stop));
    actionMap.put(Command.RESET,
        new SimulatorAction(Command.RESET, this::reset));
    actionMap.put(Command.GENERATE_REPORT,
        new SimulatorAction(Command.GENERATE_REPORT, this::generateReports));
    actionMap.put(Command.DELETE_REPORT,
        new SimulatorAction(Command.DELETE_REPORT, this::deleteReports));
    actionMap.put(Command.SAVE_REPORT,
        new SimulatorAction(Command.SAVE_REPORT, this::saveReport));
    actionMap.put(Command.EXIT,
        new SimulatorAction(Command.EXIT, () -> System.exit(0)));
  }

  /**
   * Añade cada una de las secciones de la interfaz
   */
  private void addSections(File initialFile) {

    // Cuadro de edición de eventos
    eventsEditorScroll = new JScrollPane();
    eventsEditorScroll.setMinimumSize(HORIZONTAL_THIRD);
    eventsEditorScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK),
        initialFile == null || !initialFile.exists() ?
            "Events editor" : "Events: " + initialFile.getName()));
    eventsEditor = new SimulatorTextArea(true);
    if (initialFile != null) {
      try {
        eventsEditor.writeFromFile(initialFile);
      } catch (IOException e) {
        showErrorMessage(READ_FILE_ERROR, e.getMessage());
      }
    }
    createTemplatePopupMenu();
    eventsEditorScroll.setViewportView(eventsEditor);

    // Cola de eventos cargados (tabla)
    JScrollPane eventsQueueScroll = new JScrollPane();
    eventsQueueScroll.setMinimumSize(HORIZONTAL_THIRD);
    eventsQueueScroll.setBorder(new TitledBorder(
        new LineBorder(Color.BLACK), "Events Queue"));
    eventsQueue = new SimulatorTable<>(Event.INFO);
    eventsQueueScroll.setViewportView(eventsQueue);

    // Zona de informes generados por el simulador
    JScrollPane reportsAreaScroll = new JScrollPane();
    reportsAreaScroll.setMinimumSize(HORIZONTAL_THIRD);
    reportsAreaScroll.setBorder(new TitledBorder(
        new LineBorder(Color.BLACK), "Reports"));
    reportsArea = new SimulatorTextArea(false);
    reportsArea.setDisabledTextColor(Color.BLACK);
    reportsAreaScroll.setViewportView(reportsArea);

    // Tabla de vehículos simulados
    JScrollPane vehiclesScroll = new JScrollPane();
    vehiclesScroll.setMinimumSize(VERTICAL_THIRD);
    vehiclesScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK),
        "Vehicles"));
    vehiclesTable = new SimulatorTable<>(Vehicle.INFO);
    vehiclesScroll.setViewportView(vehiclesTable);

    // Tabla de carreteras simuladas
    JScrollPane roadsScroll = new JScrollPane();
    roadsScroll.setMinimumSize(VERTICAL_THIRD);
    roadsScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK),
        "Roads"));
    roadsTable = new SimulatorTable<>(Road.INFO);
    roadsScroll.setViewportView(roadsTable);

    // Tabla de cruces simulados
    JScrollPane junctionsScroll = new JScrollPane();
    junctionsScroll.setMinimumSize(VERTICAL_THIRD);
    junctionsScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK),
        "Junctions"));
    junctionsTable = new SimulatorTable<>(Junction.INFO);
    junctionsScroll.setViewportView(junctionsTable);

    // Grafo del mapa de carreteras
    roadMap = new GraphComponent();

    // Colocación de todos los elementos con JSplitPane
    JSplitPane topLeftSplit = createSeparator(JSplitPane.HORIZONTAL_SPLIT,
        eventsEditorScroll, eventsQueueScroll, .5);
    JSplitPane topRightSplit = createSeparator(JSplitPane.HORIZONTAL_SPLIT,
        topLeftSplit, reportsAreaScroll, .66);

    JSplitPane bottomLeftTopSplit = createSeparator(
        JSplitPane.VERTICAL_SPLIT, vehiclesScroll, roadsScroll, .5);
    JSplitPane bottomLeftSplit = createSeparator(JSplitPane.VERTICAL_SPLIT,
        bottomLeftTopSplit, junctionsScroll, .66);

    JSplitPane bottomSplit = createSeparator(JSplitPane.HORIZONTAL_SPLIT,
        bottomLeftSplit, roadMap, .5);

    JSplitPane main = createSeparator(JSplitPane.VERTICAL_SPLIT,
        topRightSplit, bottomSplit, .4);

    add(main);

  }

  /**
   * Popup menu sobre el editor de eventos que permite generar plantillas de texto para
   * simplificar la escritura de eventos, así como otras acciones útiles
   */
  private void createTemplatePopupMenu() {
    JPopupMenu popup = new JPopupMenu();
    JMenu menu = new JMenu("Add template");
    menu.setEnabled(true);
    // Aprovecha los Event.Builder de Controller: les pide el texto que debe generar
    for (Event.Builder builder : EventBuilder.SUPPORTED_EVENTS) {
      JMenuItem item = new JMenuItem(builder.getEventName());
      item.addActionListener(e -> eventsEditor.insert(builder.getEventFileTemplate() + "\n",
          eventsEditor.getCaretPosition()));
      menu.add(item);
    }
    popup.add(menu);
    popup.addSeparator();
    popup.add(actionMap.get(Command.LOAD_EVENTS));
    popup.add(actionMap.get(Command.SAVE_EVENTS));
    popup.add(actionMap.get(Command.CLEAR_EVENTS));
    eventsEditor.setComponentPopupMenu(popup);
  }

  /**
   * Crea un JSplitPane entre los dos componentes indicados y lo devuelve
   */
  private JSplitPane createSeparator(int orientation, Component first, Component second,
                                     double weight) {
    JSplitPane splitPane = new JSplitPane(orientation, first, second);
    splitPane.setDividerSize(5);
    splitPane.setVisible(true);
    splitPane.setResizeWeight(weight);
    splitPane.setContinuousLayout(true);
    return splitPane;
  }

  /**
   * Añade la barra de herramientas y los menús superiores
   */
  private void addToolBar(int initialSteps) {
    // Tool bar
    JToolBar bar = new JToolBar();

    stepDelay = new JSpinner(new SpinnerNumberModel(300, 0, 2000, 100));
    stepDelay.setMaximumSize(new Dimension(75, 30));

    stepCounter = new JSpinner(new SpinnerNumberModel(initialSteps, 0, 100, 1));
    stepCounter.setMaximumSize(new Dimension(75, 30));

    time = new JTextField("0", 4);
    time.setMaximumSize(new Dimension(75, 30));
    time.setHorizontalAlignment(JTextField.RIGHT);
    time.setEnabled(false);
    time.setDisabledTextColor(Color.BLACK);

    // Añade cada componente/acción por orden
    addActionsToToolBar(bar, Command.LOAD_EVENTS, Command.SAVE_EVENTS, Command.CLEAR_EVENTS,
        Command.MOVE_EVENTS, Command.RUN, Command.STOP, Command.RESET);
    addComponentsToToolBar(bar, new JLabel(" Delay: "), stepDelay, new JLabel(" Steps: "),
        stepCounter, new JLabel(" Time: "), time);
    addActionsToToolBar(bar, Command.GENERATE_REPORT, Command.DELETE_REPORT, Command.SAVE_REPORT,
        Command.EXIT);

    add(bar, BorderLayout.NORTH);

    JCheckBoxMenuItem redirectOutput = new JCheckBoxMenuItem("Redirect output");
    redirectOutput.addItemListener(e -> {
      if (redirectOutput.isSelected()) {
        controller.setOutputStream(new TextAreaOutputStream(reportsArea));
      } else {
        controller.setOutputStream(null);
      }
    });

    // Menu bar
    JMenuBar menu = new JMenuBar();

    // Menú File
    JMenu file = new JMenu("File");
    file.add(actionMap.get(Command.LOAD_EVENTS));
    file.add(actionMap.get(Command.SAVE_EVENTS));
    file.addSeparator();
    file.add(actionMap.get(Command.SAVE_REPORT));
    file.addSeparator();
    file.add(actionMap.get(Command.EXIT));

    // Menú Simulator
    JMenu simulator = new JMenu("Simulator");
    simulator.add(actionMap.get(Command.RUN));
    simulator.add(actionMap.get(Command.STOP));
    simulator.add(actionMap.get(Command.RESET));
    simulator.addSeparator();
    simulator.add(actionMap.get(Command.MOVE_EVENTS));
    simulator.add(redirectOutput);

    // Menú Reports
    JMenu reports = new JMenu("Reports");
    reports.add(actionMap.get(Command.GENERATE_REPORT));
    reports.add(actionMap.get(Command.DELETE_REPORT));

    menu.add(file);
    menu.add(simulator);
    menu.add(reports);

    setJMenuBar(menu);
  }

  /**
   * Añade todos los componentes indicados a la barra indicada
   */
  private void addComponentsToToolBar(JToolBar bar, JComponent... elements) {
    for (JComponent c : elements) {
      bar.add(c);
    }
  }

  /**
   * Añade todas las acciones indicadas por los comandos a la barra indicada
   */
  private void addActionsToToolBar(JToolBar bar, Command... commands) {
    for (Command c : commands) {
      bar.add(actionMap.get(c));
    }
  }

  /**
   * Activa o desactiva los comandos indicados
   */
  private void enableActions(boolean enabled, Command... commands) {
    for (Command c : commands) {
      actionMap.get(c).setEnabled(enabled);
    }
  }

  /**
   * Añade la barra de estado inferior
   */
  private void addStatusBar() {
    JPanel statusBar = new JPanel();
    statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
    statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
    statusBarText = new JLabel("Welcome to the simulator!");
    statusBar.add(statusBarText);
    add(statusBar, BorderLayout.SOUTH);
  }

  /**
   * Cambia el texto de la barra de estado
   */
  private void setStatusText(String text) {
    statusBarText.setText(text);
  }

  /**
   * Añade los observadores de eventos necesarios
   */
  private void addListeners() {
    controller.getSimulator().addListener(new TrafficSimulator.Listener() {
      @Override
      public void registered(TrafficSimulator.UpdateEvent ue) {
        enableActions(false, Command.RUN, Command.STOP, Command.RESET, Command.GENERATE_REPORT,
            Command.DELETE_REPORT, Command.SAVE_REPORT);
      }

      @Override
      public void reset(TrafficSimulator.UpdateEvent ue) {
        time.setText("" + 0);
        enableActions(true, Command.MOVE_EVENTS);
        enableActions(false, Command.RUN, Command.RESET, Command.GENERATE_REPORT,
            Command.DELETE_REPORT, Command.SAVE_REPORT);
        refreshTables(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        roadMap.clear();
        setStatusText("Simulator has just been reset!");
      }

      @Override
      public void newEvent(TrafficSimulator.UpdateEvent ue) {
        List<Event> events = ue.getEventQueue();
        if (!events.isEmpty()) {
          eventsQueue.setElements(events);
          enableActions(true, Command.RUN);
        }
        setStatusText("Events have been loaded to the simulator!");
      }

      @Override
      public void advanced(TrafficSimulator.UpdateEvent ue) {
        time.setText("" + ue.getCurrentTime());
        refreshTables(ue.getVehicles(), ue.getRoads(), ue.getJunctions());
        eventsQueue.setElements(ue.getEventQueue());
        generateGraph(ue.getVehicles(), ue.getRoads(), ue.getJunctions(),
            controller.getSimulator().getGreenRoads());
        setStatusText("Simulator advanced " + ue.getCurrentTime() + " steps!");
      }

      @Override
      public void error(TrafficSimulator.UpdateEvent ue, String msg) {
        setStatusText("An error occurred!!");
        stepper.stop();
        showErrorMessage("Simulator error", msg);
        SimulatorWindow.this.reset();
      }
    });
  }

  /**
   * Genera un grafo con los objetos del simulador
   */
  private void generateGraph(Collection<Vehicle> vehicles, Collection<Road> roads,
                             Collection<Junction> junctions, Set<Road> greenRoads) {
    Graph graph = new Graph();
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
        e.addDot(new Dot(v.getId(), v.getLocation(), v.hashCode()));
      }
    }
    roadMap.setGraph(graph);
  }

  /**
   * Carga los eventos del fichero que indique el usuario
   */
  private void loadEvents() {
    JFileChooser chooser = new JFileChooser(previousPath);
    chooser.setFileFilter(new FileNameExtensionFilter("INI files", "ini"));
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      // Actualiza la dirección para el siguiente uso
      previousPath = file.getPath();
      try {
        eventsEditor.writeFromFile(file);
        updateEventsEditorTitle(file.getName());
      } catch (IOException e) {
        showErrorMessage(READ_FILE_ERROR, e.getMessage());
      }
    }
  }

  /**
   * Guarda los eventos en el fichero indicado por el usuario
   */
  private void saveEvents() {
    JFileChooser chooser = new JFileChooser(previousPath);
    chooser.setFileFilter(new FileNameExtensionFilter("INI files", "ini"));
    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      // Actualiza la dirección para el siguiente uso
      previousPath = file.getPath();
      try {
        eventsEditor.saveToFile(file);
        updateEventsEditorTitle(file.getName());
      } catch (IOException e) {
        showErrorMessage(WRITE_FILE_ERROR, e.getMessage());
      }
    }
  }

  /**
   * Actualiza el título del panel del editor de eventos
   */
  private void updateEventsEditorTitle(String title) {
    TitledBorder border = (TitledBorder) eventsEditorScroll.getBorder();
    border.setTitle("Events: " + title);
    eventsEditorScroll.repaint();
  }

  /**
   * Limpia el texto del editor de eventos
   */
  private void clearEvents() {
    eventsEditor.clear();
  }

  /**
   * Carga en el simulador los eventos que haya en el editor de eventos y los escribe en la tabla
   * de la cola de eventos
   */
  private void readEvents() {
    try {
      String text = eventsEditor.getText();
      // Crea un InputStream a partir del texto
      InputStream is = new ByteArrayInputStream(text.getBytes("UTF-8"));
      controller.getSimulator().clearEvents();
      controller.loadEvents(is);
    } catch (IOException | IllegalStateException e) {
      reset();
      showErrorMessage("Error reading events", e.getMessage());
    }
  }

  /**
   * Lanza la ejecución del simulador los ticks indicados
   */
  private void run() {
    controller.getSimulator().reset();
    int delay = (Integer) stepDelay.getValue();
    int ticks = (Integer) stepCounter.getValue();
    stepper.start(ticks, delay);
  }

  /**
   * Para la ejecución del simulador
   */
  private void stop() {
    stepper.stop();
  }

  /**
   * Devuelve al simulador a su estado inicial
   */
  private void reset() {
    eventsQueue.clear();
    reportsArea.clear();
    controller.reset();
    controller.getSimulator().clearEvents();
  }

  /**
   * Genera los informes de los objetos que indique el usuario
   */
  private void generateReports() {
    SimulatedObjectDialog dialog = new SimulatedObjectDialog(this, "Generate reports");
    TrafficSimulator simulator = controller.getSimulator();
    // Objetos a elegir del simulador
    dialog.setVehicles(simulator.getVehicles());
    dialog.setRoads(simulator.getRoads());
    dialog.setJunctions(simulator.getJunctions());
    if (dialog.open() == SimulatedObjectDialog.ACCEPTED) {
      // Objetos seleccionados
      Collection<Vehicle> vehicles = dialog.getSelectedVehicles();
      Collection<Road> roads = dialog.getSelectedRoads();
      Collection<Junction> junctions = dialog.getSelectedJunctions();
      reportsArea.clear();
      simulator.generateReports(new TextAreaOutputStream(reportsArea), junctions, roads, vehicles);
      enableActions(true, Command.DELETE_REPORT, Command.SAVE_REPORT);
    }
  }

  /**
   * Limpia la zona de informes
   */
  private void deleteReports() {
    reportsArea.clear();
    enableActions(false, Command.DELETE_REPORT, Command.SAVE_REPORT);
  }

  /**
   * Guarda el informe que haya sido generado por el simulador
   */
  private void saveReport() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(new FileNameExtensionFilter("INI files", "ini"));
    int value = chooser.showSaveDialog(this);
    if (value == JFileChooser.APPROVE_OPTION) {
      try {
        reportsArea.saveToFile(chooser.getSelectedFile());
      } catch (IOException e) {
        showErrorMessage(WRITE_FILE_ERROR, e.getMessage());
      }
    }
  }

  /**
   * Muestra una ventana con un mensaje de error
   */
  private void showErrorMessage(String title, String msg) {
    JOptionPane.showMessageDialog(SimulatorWindow.this, msg, title,
        JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Reescribe los elementos de las tablas de los objetos simulados
   */
  private void refreshTables(Collection<Vehicle> vehicles, Collection<Road> roads,
                             Collection<Junction> junctions) {
    vehiclesTable.setElements(vehicles);
    roadsTable.setElements(roads);
    junctionsTable.setElements(junctions);
  }

}
