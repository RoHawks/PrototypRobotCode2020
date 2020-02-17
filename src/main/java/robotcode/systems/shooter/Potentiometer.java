package robotcode.systems.shooter;

import edu.wpi.first.wpilibj.AnalogInput;

public class Potentiometer {

    private AnalogInput potentiometer;

    public Potentiometer(int port) {
        potentiometer = new AnalogInput(port); //initialize potentiometer with a port for analog input
    }

    public double getReading() {
        return potentiometer.getAverageVoltage(); //return average voltage
    }


    //finish potentiometer class
    //create class that combines servo, PID constants and potentiometer
    //use that for shooter

}