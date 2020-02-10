package robotcode.systems;

import common.cameras.Limelight;
import common.servos.RevSRS;
import edu.wpi.first.wpilibj.controller.PIDController;

public class Shooter {

    public enum State {
        INTAKING, SHOOTING, HOLDING, IDLE;
    }

    private RevSRS panServo;
    private RevSRS hoodServo;
    private Limelight limelight;
    private PIDController panServoPID;
    private PIDController hoodServoPID;
    //Potentiometer goes here

    public Shooter(RevSRS panServo, RevSRS hoodServo, Limelight limelight, double kP, double kI, double kD) {
        this.panServo = panServo;
        this.hoodServo = hoodServo;
        this.limelight = limelight;
        panServoPID = new PIDController(kP, kI, kD);
		hoodServoPID = new PIDController(kP, kI, kD);
    }

    public void aimAuto() {
        if (limelight.get("tv") == 1) {
            double xTarget = limelight.get("tx");
            double yTarget = limelight.get("ty");
            double panServoSpeed = panServoPID.calculate(xTarget);
            double hoodServoSpeed = hoodServoPID.calculate(yTarget);
            panServo.setSpeed(panServoSpeed);
            hoodServo.setSpeed(hoodServoSpeed);
            //If potentiometer reports angle greater than the hood or pan rail, set speed to 0
        }
    }

    public void aimManual(double panSpeed, double hoodSpeed) { //make sure to smooth joystick input (< 0.2 means 0 speed) in robot.java
        panServo.setSpeed(panSpeed);
        hoodServo.setSpeed(hoodSpeed);
    }

    // private void runShooter(State state) {
	// 	switch(state) {
	// 		case INTAKING:
	// 			intakeMotor.set(ControlMode.PercentOutput, .8);
	// 			beltMotor.set(ControlMode.PercentOutput, 0);
	// 			sideRoller.set(ControlMode.PercentOutput, 0);
	// 			leftShooterMotor.set(0);
	// 			break;
	// 		case SHOOTING:
	// 			intakeMotor.set(ControlMode.PercentOutput, 0);
	// 			beltMotor.set(ControlMode.PercentOutput, -.5);
	// 			sideRoller.set(ControlMode.PercentOutput, .2);
	// 			leftShooterMotor.set(1);
	// 			break;
	// 		case HOLDING:
	// 			intakeMotor.set(ControlMode.PercentOutput, 0);
	// 			beltMotor.set(ControlMode.PercentOutput, 0);
	// 			sideRoller.set(ControlMode.PercentOutput, 0);
	// 			leftShooterMotor.set(1);
	// 			break;
	// 		default:
	// 			intakeMotor.set(ControlMode.PercentOutput, 0);
	// 			beltMotor.set(ControlMode.PercentOutput, 0);
	// 			sideRoller.set(ControlMode.PercentOutput, 0);
	// 			leftShooterMotor.set(0);
	// 			break;
	// 	}

	// 	// SmartDashboard.putNumber("Shooter RPM", leftShooterMotor.get());
	// 	// SmartDashboard.putNumber("Shooter RPM Target", shooterRPM);
	// 	// SmartDashboard.putNumber("Shooter motor current draw", leftShooterMotor.g());
	// 	SmartDashboard.putNumber("Shooter motor percent output", leftShooterMotor.get());
	// }


}