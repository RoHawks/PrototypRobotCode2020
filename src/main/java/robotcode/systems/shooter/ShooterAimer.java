package robotcode.systems.shooter;

import common.cameras.Limelight;
import common.servos.RevSRS;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.controller.PIDController;

public class ShooterAimer {

    private Potentiometer panPotentiometer;
    private Potentiometer hoodPotentiometer;
    private final double MIN_PAN_POSITION = 0;
    private final double MAX_PAN_POSITION = 0.5;
    private final double MIN_HOOD_POSITION = 0;
    private final double MAX_HOOD_POSITION = 0.5;

    
    private RevSRS panServo;
    private RevSRS hoodServo;
    private PIDController panServoPID;
    private PIDController hoodServoPID;

    private Limelight limelight;

    public ShooterAimer(int panPotentiometerPort,
                        int hoodPotentiometerPort,
                        int panServoPort,
                        int hoodServoPort, 
                        Limelight limelight, 
                        double kPPan, 
                        double kIPan, 
                        double kDPan,
                        double kPHood,
                        double kIHood,
                        double kDHood) {
        panPotentiometer = new Potentiometer(panPotentiometerPort);
        hoodPotentiometer = new Potentiometer(hoodPotentiometerPort);
        panServo = new RevSRS(panServoPort);
        hoodServo = new RevSRS(hoodServoPort);
        panServoPID = new PIDController(kPPan, kIPan, kDPan); //Make Config file
        hoodServoPID = new PIDController(kPHood, kIHood, kDHood);
    }

    public void aimAuto() {
        if (limelight.get("tv") == 1) {
            double xTarget = limelight.get("tx");
            double yTarget = limelight.get("ty");
            double panServoSpeed = panServoPID.calculate(xTarget);
            double hoodServoSpeed = hoodServoPID.calculate(yTarget);
            if (inRange(panPotentiometer, MIN_PAN_POSITION, MAX_PAN_POSITION, panServoSpeed) && inRange(hoodPotentiometer, MIN_HOOD_POSITION, MAX_HOOD_POSITION, hoodServoSpeed)) {
                //only move if the speed is pointed in a direction that will not make the servo run off the rails.
                //if the servo is on one end of the rails, then it can only spin in a direction that moves the servo away from that end
                panServo.setSpeed(panServoSpeed); 
                hoodServo.setSpeed(hoodServoSpeed);
            } else {
                panServo.setSpeed(0);
                hoodServo.setSpeed(0);
            }
        }
    }

    public void aimManual(double panSpeed, double hoodSpeed) { //make sure to smooth joystick input (< 0.2 means 0 speed) in robot.java
        if (inRange(panPotentiometer, MIN_PAN_POSITION, MAX_PAN_POSITION, panSpeed) && inRange(hoodPotentiometer, MIN_HOOD_POSITION, MAX_HOOD_POSITION, hoodSpeed)) {
            panServo.setSpeed(panSpeed);
            hoodServo.setSpeed(hoodSpeed);
        } else {
            panServo.setSpeed(0);
            hoodServo.setSpeed(0);
        }
    }

    private boolean inRange(Potentiometer potentiometer, double min, double max, double speed) {
        if (speed > 0) { //turn clockwise
            if (potentiometer.getReading() > min) { //should we use > or >= ?
                return true; 
            } else {
                return false;
            }
        } else {
            if (potentiometer.getReading() < max) { //should we use > or >= ?
                return true;
            } else {
                return false;
            }
        }
    }

    //finish potentiometer class
    //create class that combines servo, PID constants and potentiometer
    //use that for shooter

}