package es.ucm.fdi.control.layout;

import es.ucm.fdi.model.Junction;
import es.ucm.fdi.model.Road;
import es.ucm.fdi.model.SimulatedObject;
import es.ucm.fdi.model.Vehicle;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Clase que permite abrir un diálogo para elegir los objetos de los que queremos informes
 */
public class SimulatedObjectDialog extends JDialog {

  public static final int CANCELED = 0;
  public static final int ACCEPTED = 1;

  private SimulatedObjectListModel<Vehicle> vehicles;
  private SimulatedObjectListModel<Road> roads;
  private SimulatedObjectListModel<Junction> junctions;

  private JList<Vehicle> vehicleList;
  private JList<Road> roadList;
  private JList<Junction> junctionList;

  private int status;

  private Border defaultBorder = BorderFactory.createLineBorder(Color.BLACK, 2);

  public SimulatedObjectDialog(JFrame parent, String title) {
    super(parent);
    initialize(title);
  }

  private void initialize(String title) {

    status = CANCELED;

    // Es necesario cerrarlo para que continúe la ejecución
    setModal(true);
    setResizable(false);
    setTitle(title);
    JPanel mainPanel = new JPanel(new BorderLayout());

    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
    mainPanel.add(contentPanel, BorderLayout.CENTER);

    // Paneles para mostrar las listas
    JPanel vehiclesPanel = new JPanel(new BorderLayout());
    JPanel roadsPanel = new JPanel(new BorderLayout());
    JPanel junctionsPanel = new JPanel(new BorderLayout());

    contentPanel.add(vehiclesPanel);
    contentPanel.add(roadsPanel);
    contentPanel.add(junctionsPanel);

    vehiclesPanel.setBorder(BorderFactory.createTitledBorder(defaultBorder, "Vehicles",
        TitledBorder.LEFT, TitledBorder.TOP));
    roadsPanel.setBorder(BorderFactory.createTitledBorder(defaultBorder, "Roads",
        TitledBorder.LEFT, TitledBorder.TOP));
    junctionsPanel.setBorder(BorderFactory.createTitledBorder(defaultBorder, "Junctions",
        TitledBorder.LEFT, TitledBorder.TOP));

    vehiclesPanel.setMinimumSize(new Dimension(100, 1000));
    roadsPanel.setMinimumSize(new Dimension(100, 1000));
    junctionsPanel.setMinimumSize(new Dimension(100, 1000));

    // Listas de objetos
    vehicleList = new JList<>();
    roadList = new JList<>();
    junctionList = new JList<>();

    vehiclesPanel.add(new JScrollPane(vehicleList,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
        BorderLayout.CENTER);

    roadsPanel.add(new JScrollPane(roadList,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
        BorderLayout.CENTER);

    junctionsPanel.add(new JScrollPane(junctionList,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
        BorderLayout.CENTER);

    // Checkboxes para seleccionar/deseleccionar todos los objetos de un tipo
    JCheckBox allVehicles = new JCheckBox("Select all");
    allVehicles.addActionListener(e -> {
      if (allVehicles.isSelected()) {
        vehicleList.setSelectionInterval(0, vehicles.getSize());
        // Sin esta línea da problemas (por alguna razón el otro listener lo deselecciona)
        allVehicles.setSelected(true);
      } else {
        vehicleList.clearSelection();
      }
    });

    JCheckBox allRoads = new JCheckBox("Select all");
    allRoads.addActionListener(e -> {
      if (allRoads.isSelected()) {
        roadList.setSelectionInterval(0, roads.getSize());
        allRoads.setSelected(true);
      } else {
        roadList.clearSelection();
      }
    });

    JCheckBox allJunctions = new JCheckBox("Select all");
    allJunctions.addActionListener(e -> {
      if (allJunctions.isSelected()) {
        junctionList.setSelectionInterval(0, junctions.getSize());
        allJunctions.setSelected(true);
      } else {
        junctionList.clearSelection();
      }
    });

    // Si al seleccionar algo en una lista están todos seleccionados marca el checkbox,
    // si no, lo desmarca
    vehicleList.addListSelectionListener(e ->
        allVehicles.setSelected(vehicleList.getSelectedIndices().length == vehicles.getSize()));
    roadList.addListSelectionListener(e ->
        allRoads.setSelected(roadList.getSelectedIndices().length == roads.getSize()));
    junctionList.addListSelectionListener(e ->
        allJunctions.setSelected(junctionList.getSelectedIndices().length == junctions.getSize()));

    vehiclesPanel.add(allVehicles, BorderLayout.SOUTH);
    roadsPanel.add(allRoads, BorderLayout.SOUTH);
    junctionsPanel.add(allJunctions, BorderLayout.SOUTH);

    // Botones para cerrar el cuadro
    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    mainPanel.add(buttonsPanel, BorderLayout.PAGE_END);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> {
      status = CANCELED;
      setVisible(false);
    });
    buttonsPanel.add(cancelButton);

    JButton allObjectsButton = new JButton("All objects");
    allObjectsButton.addActionListener(e -> {
      vehicleList.setSelectionInterval(0, vehicles.getSize());
      roadList.setSelectionInterval(0, roads.getSize());
      junctionList.setSelectionInterval(0, junctions.getSize());
      status = ACCEPTED;
      setVisible(false);
    });
    buttonsPanel.add(allObjectsButton);

    JButton okButton = new JButton("OK");
    okButton.addActionListener(e -> {
      status = ACCEPTED;
      setVisible(false);
    });
    buttonsPanel.add(okButton);

    // Texto superior
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    mainPanel.add(infoPanel, BorderLayout.PAGE_START);

    infoPanel.add(new JLabel("Select items for which you want to generate reports."));

    setContentPane(mainPanel);
    setMinimumSize(new Dimension(100, 100));
    setVisible(false);
  }

  public void setVehicles(Collection<Vehicle> newVehicles) {
    vehicles = new SimulatedObjectListModel<>(newVehicles);
    vehicleList.setModel(vehicles);
  }

  public void setRoads(Collection<Road> newRoads) {
    roads = new SimulatedObjectListModel<>(newRoads);
    roadList.setModel(roads);
  }

  public void setJunctions(Collection<Junction> newJunctions) {
    junctions = new SimulatedObjectListModel<>(newJunctions);
    junctionList.setModel(junctions);
  }

  public List<Vehicle> getSelectedVehicles() {
    return getSelectedData(vehicleList);
  }

  public List<Road> getSelectedRoads() {
    return getSelectedData(roadList);
  }

  public List<Junction> getSelectedJunctions() {
    return getSelectedData(junctionList);
  }

  private <T> List<T> getSelectedData(JList<T> list) {
    int[] indices = list.getSelectedIndices();
    ListModel<T> model = list.getModel();
    List<T> result = new ArrayList<>();
    // Por alguna razón si el checkbox seleccionado se lo traga como otro elemento más
    for (int i = 0; i < indices.length && indices[i] < model.getSize(); i++) {
      result.add(model.getElementAt(indices[i]));
    }
    return result;
  }

  public int open() {
    setLocation(getParent().getLocation().x + 50, getParent().getLocation().y + 50);
    pack();
    setVisible(true);
    return status;
  }

  /**
   * Clase interna que representa el modelo de la lista donde mostrar los objetos
   */
  private class SimulatedObjectListModel<T extends SimulatedObject> extends DefaultListModel<T> {

    List<T> list;

    SimulatedObjectListModel(Collection<T> c) {
      if (c == null) {
        throw new IllegalArgumentException("List model cannot be null");
      }
      // Es necesaria la copia porque los objetos llegan como Collection
      // Al fin y al cabo la creación de este objeto no se va a dar muy a menudo
      list = new ArrayList<>(c);
      refresh();
    }

    @Override
    public T get(int index) {
      return list.get(index);
    }

    @Override
    public T getElementAt(int index) {
      return get(index);
    }

    @Override
    public int getSize() {
      return list.size();
    }

    void refresh() {
      fireContentsChanged(this, 0, getSize());
    }

  }

}
