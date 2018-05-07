package es.ucm.fdi.model;

import java.util.Map;
import java.util.Queue;
import java.util.Random;

/**
 * Vehículo con cierta probabilidad de estropearse
 */
public class Car extends Vehicle {

  public static final String TYPE = "car";

  private int resistance;
  private int lastTimeFaulty;
  private double faultyProbability;
  private int maxFaultDuration;
  private Random randomNumber;

  public Car(String id, int maxSpeed, Queue<Junction> itinerary, int resistance,
             double faultProbability, int maxFaultDuration, long seed) {
    super(id, maxSpeed, itinerary);
    this.resistance = resistance;
    this.faultyProbability = faultProbability;
    this.maxFaultDuration = maxFaultDuration;
    randomNumber = new Random(seed);
    lastTimeFaulty = 0;
  }

  @Override
  public void setFaulty(int faulty) {
    super.setFaulty(faulty);
    lastTimeFaulty = kilometrage;
  }

  @Override
  public void advance() {
    if (faulty == 0 && kilometrage - lastTimeFaulty > resistance &&
        randomNumber.nextDouble() < faultyProbability) {
      setFaulty(randomNumber.nextInt(maxFaultDuration) + 1);
    }
    super.advance();
  }

  @Override
  public void fillReportDetails(Map<String, String> kvps) {
    kvps.put("type", TYPE);
    super.fillReportDetails(kvps);
  }

}
