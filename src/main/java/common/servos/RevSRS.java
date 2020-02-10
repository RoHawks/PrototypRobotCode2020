package common.servos;

import common.servos.configs.interfaces.IPWMConfig;
import common.servos.interfaces.IAngularServo;
import common.servos.interfaces.IContinuousServo;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.controller.PIDController;

/**
 * REV Robotics Smart Robot Servo.
 */

public class RevSRS extends Servo {
    public enum Mode {
        Continuous,
        Angular
    }

    private Mode mode;

    private final double maxSpeed = 1d;
    private final double minSpeed = -0.998d;

    private double currentSpeed;

    public RevSRS(int port) {
        super(port);
        mode = Mode.Continuous; //If we need a angular mode, then we add another argument to constructor
        setBounds(2.5, 1.5, 1.5, 1.5, 0.5);
    }

    @Override
    public void setSpeed(double output) {
        if(mode != Mode.Continuous) return;
        if(output < minSpeed) output = minSpeed;
        else if(output > maxSpeed) output = maxSpeed;
        currentSpeed = output;
        setSpeed(output);
    }

    @Override
    public double getSpeed() {
        if(mode != Mode.Continuous) return 0;
        return currentSpeed;
    }
}