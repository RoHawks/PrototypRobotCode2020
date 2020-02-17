package common.servos;

import common.pid.configs.PIDConfig;
import common.servos.configs.RevSRSConfig;
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
    private PIDController pid;
    private double speedCap;

    public RevSRS(RevSRSConfig config) {
        super(config.getPort());

        setBounds(2.5, 1.5, 1.5, 1.5, 0.5);

        mode = Mode.Continuous; //If we need a angular mode, then we add another argument to constructor
        speedCap = config.getSpeedCap();
        PIDConfig pidConfig = config.getPIDConfig();

        pid = new PIDController(
            pidConfig.getP(),
            pidConfig.getI(),
            pidConfig.getD(), 
            pidConfig.getIZone()
        );
    }

    public void setPIDSpeed(double target) {
        currentSpeed = pid.calculate(target);
        setSpeed(currentSpeed);
    }

    @Override
    public void setSpeed(double output) {
        if (mode != Mode.Continuous) {
            return;
        }
        if (output < minSpeed) {
            output = minSpeed;
        } else if (output > maxSpeed) {
            output = maxSpeed;
        }

        currentSpeed = output * speedCap;
        super.setSpeed(currentSpeed);
    }

    @Override
    public double getSpeed() {
        if (mode != Mode.Continuous) {
            return 0;
        }
        return currentSpeed;
    }
}
