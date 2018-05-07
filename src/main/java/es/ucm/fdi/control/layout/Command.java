package es.ucm.fdi.control.layout;

import java.awt.event.KeyEvent;

/**
 * Enumerado con los tipos de comandos que habrá en la barra de herramientas
 */
public enum Command {

  LOAD_EVENTS("Load events", "open.png", "Load events file", KeyEvent.VK_L, "control L"),
  SAVE_EVENTS("Save events", "save.png", "Save events", KeyEvent.VK_S, "control S"),
  CLEAR_EVENTS("Clear", "clear.png", "Clear events editor", KeyEvent.VK_C, "control D"),
  MOVE_EVENTS("Set events", "events.png", "Move events to events queue", null, "control enter"),
  RUN("Run", "play.png", "Run simulation", KeyEvent.VK_R, "control P"),
  STOP("Stop", "stop.png", "Stop simulation", KeyEvent.VK_T, "control P"),
  RESET("Reset", "reset.png", "Reset simulation", null, "control shift P"),
  GENERATE_REPORT("Generate report", "report.png", "Generate new report", null, null),
  DELETE_REPORT("Delete report", "delete_report.png", "Delete current report", null, null),
  SAVE_REPORT("Save report", "save_report.png", "Save current report", null, "control shift S"),
  EXIT("Exit", "exit.png", "Exit application", KeyEvent.VK_E, "control W");

  final String name;
  final String icon;
  final String tooltip;
  final Integer keyEvent;
  final String accelerator;

  Command(String name, String icon, String tooltip, Integer keyEvent, String accelerator) {
    this.name = name;
    this.icon = icon;
    this.tooltip = tooltip;
    this.keyEvent = keyEvent;
    this.accelerator = accelerator;
  }

}
