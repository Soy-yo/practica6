package es.ucm.fdi.model;

import java.util.Map;

/**
 * Permite a los objetos que implementen esta interfaz dar información sobre sí mismos en un mapa
 */
public interface Describable {

  Map<String, String> describe();

}
