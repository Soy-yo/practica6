package es.ucm.fdi.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que guarda las constantes de la aplicación. Permite múltiples lenguajes.
 */
public class SimulatorConstants {

  private static final Map<String, String> CONSTANTS = new HashMap<>();

  private static final String DEFAULT_LANGUAGE = "en";
  private static final String CONST_PATH = "src/main/resources/constants/";
  private static final String CONST_FILENAME = "constants.xml";

  private static String previousLang;

  static {
    setLanguage("default");
  }

  // No se puede instanciar
  private SimulatorConstants() {
  }

  /**
   * Cambia el lenguaje en que muestra la aplicación.
   */
  public static void setLanguage(String language) {
    if (language == null || language.equals("default") || language.trim().isEmpty()) {
      language = DEFAULT_LANGUAGE;
    }
    // No actualiza si coincide con el lenguaje anterior
    if (!language.equals(previousLang)) {
      try {
        readFile(language.toLowerCase());
        previousLang = language;
      } catch (IOException | SAXException | ParserConfigurationException e) {
        System.err.println(e.getMessage());
      }
    }
  }

  private static void readFile(String language)
      throws ParserConfigurationException, IOException, SAXException {
    File file = new File(CONST_PATH + CONST_FILENAME);
    DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document doc = dBuilder.parse(file);
    doc.getDocumentElement().normalize();

    // All tags
    Element main = doc.getDocumentElement();
    if (!"constants".equals(main.getNodeName())) {
      throw new SAXException(
          "Invalid constants file: its main tag is not \"<constants>\": " + main.getNodeName());
    }
    NodeList nodes = main.getChildNodes();

    for (int i = 0; i < nodes.getLength(); i++) {
      // Cada nodo hijo representa el id de una constante
      Node node = nodes.item(i);

      // No es una constante, se ignora
      if (!"constant".equals(node.getNodeName())) {
        continue;
      }

      String id = node.getAttributes().getNamedItem("id").getNodeValue();
      String defaultValue = id;

      NodeList children = node.getChildNodes();
      boolean found = false;

      for (int j = 0; j < children.getLength() && !found; j++) {
        // Cada subnodo se supone que es un lenguaje
        Node child = children.item(j);

        if (!"lang".equals(child.getNodeName())) {
          continue;
        }

        Node lang = child.getAttributes().getNamedItem("value");

        if (lang == null || DEFAULT_LANGUAGE.equals(lang.getNodeValue().toLowerCase())) {
          defaultValue = child.getTextContent();
        }
        if (lang != null && language.equals(lang.getNodeValue().toLowerCase())) {
          found = true;
          CONSTANTS.put(id, child.getTextContent());
        }
      }
      // Si no ha encontrado el lenguaje pone el valor de la etiqueta
      if (!found) {
        CONSTANTS.put(id, defaultValue);
      }
    }
  }

  /**
   * Devuelve la ocnstante con el id proporcionado.
   */
  public static String c(String id) {
    String result = CONSTANTS.get(id);
    if (result == null) {
      throw new IllegalArgumentException("Constant with id " + id + " does not exist");
    }
    return result;
  }

}
