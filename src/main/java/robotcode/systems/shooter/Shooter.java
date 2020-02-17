package robotcode.systems.shooter;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import common.cameras.Limelight;
import common.servos.RevSRS;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.AnalogInput;

public class Shooter {

    public enum Mode {
        MANUAL, AUTO;
    }

    private CANSparkMax leftShooterMotor;
    private CANSparkMax rightShooterMotor;
    private com.ctre.phoenix.motorcontrol.can.TalonSRX beltMotor;
    private double shooterRPM = 0;
    private final double BELT_MOTOR_OUTPUT = -0.5;
    private final double EPSILON = 2E-2;

    private ShooterAimer shooterAimer;

    public Shooter(CANSparkMax leftShooterMotor, 
                   CANSparkMax rightShooterMotor, 
                   com.ctre.phoenix.motorcontrol.can.TalonSRX beltMotor, 
                   ShooterAimer shooterAimer) {
        this.leftShooterMotor = leftShooterMotor; //set up PID for shooter motors
        this.rightShooterMotor = rightShooterMotor; //Wrap shooter flywheel into one class
        this.beltMotor = beltMotor;
        this.shooterAimer = shooterAimer;
    }

    public void shooting(double panSpeed, double hoodSpeed) { //goal is to make it one method that sense automatically when to stop shooting
        shooterAimer.aimManual(panSpeed, hoodSpeed);
        leftShooterMotor.set(1); //when we switch to RPM, check to make sure repeatedly setting the setpoint does not mess up PID
        if (Math.abs(1-leftShooterMotor.get()) < EPSILON) {
            beltMotor.set(ControlMode.PercentOutput, BELT_MOTOR_OUTPUT);
        }
    }
    
    public void shooting() { //goal is to make it one method that sense automatically when to stop shooting
        shooterAimer.aimAuto();
        leftShooterMotor.set(1); //when we switch to RPM, check to make sure repeatedly setting the setpoint does not mess up PID
        if (Math.abs(1-leftShooterMotor.get()) < EPSILON) {
            beltMotor.set(ControlMode.PercentOutput, BELT_MOTOR_OUTPUT); 
        }
    }

    public void idle() {
        leftShooterMotor.set(0);
        beltMotor.set(ControlMode.PercentOutput, 0);
        shooterAimer.aimManual(0, 0);
    }

}