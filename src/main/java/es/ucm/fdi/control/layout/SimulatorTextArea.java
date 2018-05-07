package es.ucm.fdi.control.layout;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Implementación de las áreas de texto de la interfaz
 */
public class SimulatorTextArea extends JTextArea {

  SimulatorTextArea(boolean editable) {
    super();
    setEnabled(editable);
  }

  /**
   * Rellena el cuadro de texto con el contenido de un archivo Ini
   */
  public void writeFromFile(File file) throws IOException {
    setText(new String(Files.readAllBytes(file.toPath()), "UTF-8"));
  }

  /**
   * Guarda el contenido del área de texto en un archivo Ini
   */
  public void saveToFile(File file) throws IOException {
    Files.write(file.toPath(), getText().getBytes("UTF-8"));
  }

  public void clear() {
    setText("");
  }

}
