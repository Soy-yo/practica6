package es.ucm.fdi.launcher;

import es.ucm.fdi.control.Controller;
import es.ucm.fdi.control.layout.SimulatorWindow;
import es.ucm.fdi.excepcions.SimulatorError;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.model.TrafficSimulator;
import org.apache.commons.cli.*;

import java.io.*;

public class Main {

  private final static Integer TIME_LIMIT_DEFAULT_VALUE = 10;

  private static Integer timeLimit = null;
  private static String infile = null;
  private static String outfile = null;
  private static Boolean guiMode = null;

  private static void parseArgs(String[] args) {

    // define the valid command line options
    //
    Options cmdLineOptions = buildOptions();

    // parse the command line as provided in args
    //
    CommandLineParser parser = new DefaultParser();
    try {
      CommandLine line = parser.parse(cmdLineOptions, args);
      parseHelpOption(line, cmdLineOptions);
      parseMode(line);
      parseInFileOption(line);
      parseOutFileOption(line);
      parseStepsOption(line);

      // if there are some remaining arguments, then something wrong is
      // provided in the command line!
      //
      String[] remaining = line.getArgs();
      if (remaining.length > 0) {
        String error = "Illegal arguments:";
        for (String o : remaining) {
          error += (" " + o);
        }
        throw new ParseException(error);
      }

    } catch (ParseException e) {
      // new Piece(...) might throw GameError exception
      System.err.println(e.getLocalizedMessage());
      System.exit(1);
    }

  }

  /**
   * Construye las opciones del comando help
   */
  private static Options buildOptions() {
    Options cmdLineOptions = new Options();

    cmdLineOptions.addOption(Option.builder("h").longOpt("help")
        .desc("Print this message").build());
    cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg()
        .desc("Events input file").build());
    cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg()
        .desc("’batch’ for batch mode and ’gui’ for GUI mode\n(default value is ’batch’)").build());
    cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg()
        .desc("Output file, where reports are written.").build());
    cmdLineOptions
        .addOption(Option
            .builder("t")
            .longOpt("ticks")
            .hasArg()
            .desc("Ticks to execute the simulator's main loop (default value is "
                + TIME_LIMIT_DEFAULT_VALUE + ").").build());

    return cmdLineOptions;
  }

  private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
    if (line.hasOption("h")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
      System.exit(0);
    }
  }

  private static void parseMode(CommandLine line) {
    guiMode = "gui".equals(line.getOptionValue("m"));
  }

  private static void parseInFileOption(CommandLine line) throws ParseException {
    infile = line.getOptionValue("i");
    if (!guiMode && infile == null) {
      throw new ParseException("An events file is missing");
    }
  }

  private static void parseOutFileOption(CommandLine line) {
    if (!guiMode) {
      outfile = line.getOptionValue("o");
    }
  }

  private static void parseStepsOption(CommandLine line) throws ParseException {
    String t = line.getOptionValue("t", TIME_LIMIT_DEFAULT_VALUE.toString());
    try {
      timeLimit = Integer.parseInt(t);
      assert (timeLimit < 0);
    } catch (Exception e) {
      throw new ParseException("Invalid value for time limit: " + t);
    }
  }

  /**
   * This method run the simulator on all files that ends with .ini if the
   * given path, and compares that output to the expected output. It assumes
   * that for example "example.ini" the expected output is stored in
   * "example.ini.eout". The simulator's output will be stored in
   * "example.ini.out"
   *
   * @throws IOException
   */
  static void test(String path) throws IOException {

    File dir = new File(path);

    if (!dir.exists()) {
      throw new FileNotFoundException(path);
    }

    File[] files = dir.listFiles((d, name) -> name.endsWith(".ini"));

    boolean everythingOk = true;

    for (File file : files) {
      try {
        everythingOk = test(file.getAbsolutePath(), file.getAbsolutePath() + ".out",
            file.getAbsolutePath() + ".eout", 10) && everythingOk;
      } catch (IOException e) {
        System.out.println("Couldn't find .eout or .out file for " + file.getAbsolutePath());
      }
    }

    if (!everythingOk) {
      throw new SimulatorError("Some tests failed");
    }

  }

  /**
   * Probar un test y ver si coincide con la salida esperada
   */
  private static boolean test(String inFile, String outFile,
                              String expectedOutFile, int timeLimit) throws IOException {
    outfile = outFile;
    infile = inFile;
    Main.timeLimit = timeLimit;
    startBatchMode();
    boolean equalOutput = (new Ini(outfile)).equals(new Ini(
        expectedOutFile));
    System.out.println("Result for: '"
        + infile
        + "' : "
        + (equalOutput ? "OK!" : ("not equal to expected output +'"
        + expectedOutFile + "'")));
    return equalOutput;
  }

  /**
   * Ejecutar el simulador en modo gui
   */
  private static void startGuiMode() {
    File initialFile = infile == null ? null : new File(infile);
    new SimulatorWindow("Traffic Simulator", initialFile, timeLimit, SimulatorWindow.WINDOW_SIZE);
  }

  /**
   * Ejecutar el simulador en modo batch
   */
  private static void startBatchMode() {

    TrafficSimulator simulator = new TrafficSimulator();
    simulator.addListener(new TrafficSimulator.Listener() {
      @Override
      public void registered(TrafficSimulator.UpdateEvent ue) {
      }

      @Override
      public void reset(TrafficSimulator.UpdateEvent ue) {
      }

      @Override
      public void newEvent(TrafficSimulator.UpdateEvent ue) {
      }

      @Override
      public void advanced(TrafficSimulator.UpdateEvent ue) {
      }

      @Override
      public void error(TrafficSimulator.UpdateEvent ue, String msg) {
        System.err.println(msg);
      }
    });

    Controller controller = new Controller(simulator);
    try {
      controller.loadEvents(new FileInputStream(infile));

      try {
        controller.setOutputStream(outfile == null ? System.out : new FileOutputStream(outfile));
      } catch (IOException e) {
        throw new SimulatorError("Something went wrong with output file (" + outfile + ")", e);
      }
      controller.run(timeLimit);

    } catch (IllegalStateException e) {
      throw new SimulatorError("Load failed", e);
    } catch (IOException e) {
      throw new SimulatorError("Something went wrong with input file (" + infile + ")", e);
    }
  }

  private static void start(String[] args) {
    parseArgs(args);
    if (guiMode) {
      startGuiMode();
    } else {
      startBatchMode();
    }
  }

  public static void main(String[] args) {
    start(args);
  }

}
