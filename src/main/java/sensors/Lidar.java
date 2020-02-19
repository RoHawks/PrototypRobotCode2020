package sensors;

import java.util.LinkedList;
import java.util.Queue;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;

public class Lidar {

    private static final int CALIBRATION_OFFSET = -1;
    private static final double CM_TO_IN = 0.394;
    private Counter counter;
    private int printedWarningCount = 5;
    private DigitalInput source;
    
    private Queue<Double> historicalMeasurements = new LinkedList<Double>();
    private double total = 0;

    public Lidar(int port) {
        source = new DigitalInput(port);
        counter = new Counter(source);
        counter.setMaxPeriod(1.0);
        counter.setSemiPeriodMode(true);
        counter.reset();

        for (int i=0; i<20; i++) {
            historicalMeasurements.add(0.0); 
        }
    }

    public double getDistance() {
        double cm;
        if ( counter.get() < 1) {
            if (printedWarningCount-- > 0) {
                System.out.println("LidarLite waiting for distance measurement");
            }
            return 0;
        } 
        cm = (counter.getPeriod() * 1000000.0/10.0) + CALIBRATION_OFFSET;
        return cm * CM_TO_IN;
    }

    public double getAverageDistance() {
        double cm;
        if ( counter.get() < 1) {
            if (printedWarningCount-- > 0) {
                System.out.println("LidarLite waiting for distance measurement");
            }
            return 0;
        } 
        cm = (counter.getPeriod() * 1000000.0/10.0) + CALIBRATION_OFFSET;
        total += cm;
        historicalMeasurements.add(cm);
        total -= historicalMeasurements.remove();
        
        return (total / 20.0) * CM_TO_IN;
    }
}
