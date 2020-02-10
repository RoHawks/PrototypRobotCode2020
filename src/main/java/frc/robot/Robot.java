package frc.robot;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import autonomous.AutonomousRoutineType;
import autonomous.commands.AutonomousCommand;
import autonomous.routines.DefaultRoutine;
import autonomous.routines.DoNothingRoutine;
import common.cameras.Limelight;
import common.motors.SparkMax;
import common.motors.TalonSRX;
import common.motors.configs.TalonSRXConfig;
import common.motors.interfaces.IMotor;
import common.motors.interfaces.IMotorWithEncoder;
import config.Config;
import config.LiftTestConfig;
import config.Robot2017Config;
import config.Robot2018Config;
import config.Robot2019Config;
import config.ShooterTestConfig;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robotcode.driving.DriveTrain;
import robotcode.driving.Wheel;
import robotcode.systems.CompressorWrapper;
import sensors.RobotAngle;
import sensors.TalonAbsoluteEncoder;
import edu.wpi.first.wpilibj.controller.PIDController;

public class Robot extends TimedRobot {

	// *************//
	// VARIABLES //
	// *************//

	//config
	private Config mConfig;

	// controllers
	private XboxController mController;
	private XboxController mJoystick;

	// drive train
	private DriveTrain mDriveTrain;
	private Wheel[] mWheel = new Wheel[4];
	private TalonAbsoluteEncoder[] mEncoder = new TalonAbsoluteEncoder[4];

	// gyro
	private AHRS mNavX;
	private RobotAngle mRobotAngle;

	// PDP and compressor
	private PowerDistributionPanel mPDP;
	private Compressor mCompressor;

	// autonomous setup
	private AutonomousRoutineType mAutonomousRoutine = AutonomousRoutineType.DEFAULT;

	// game setup
	private boolean mInGame = false;
	private long mGameStartMillis;

	//a test intake
	private com.ctre.phoenix.motorcontrol.can.TalonSRX intakeMotor;

	//a test lift
	private IMotor liftMotor;
	private double liftOutput;

	//a test shooter
	private CANSparkMax leftShooterMotor;
	private CANSparkMax rightShooterMotor;
	private com.ctre.phoenix.motorcontrol.can.TalonSRX beltMotor;
	private double shooterRPM; // TODO: abstract into motor
	private Servo panServo;
	private Servo hoodServo;
	private PIDController panServoPID; // TODO: abstract PIDController into Servo wrapper
	private PIDController hoodServoPID;
	private com.ctre.phoenix.motorcontrol.can.TalonSRX sideRoller;
	private NetworkTableInstance networkTable = NetworkTableInstance.getDefault();
	private Limelight limelight;

	// ****************//
	// GENERAL CODE //
	// ****************//
	public Robot() {
	}

	@Override
	public void testInit() {
		SmartDashboard.putBoolean("BackLeft inverted", mWheel[1].Turn.getInverted());
		SmartDashboard.putBoolean("BackLeft encoder reversed", ((Robot2018Config) mConfig).ENCODER_REVERSED[1]);
	}

	@Override
	public void testPeriodic() {
	}

	@Override
	public void robotInit() {
		//mConfig = new Robot2019Config();
		mConfig = new Robot2018Config();
		//mConfig = new Robot2017Config();
		//mConfig = new LiftTestConfig();
		//mConfig = new ShooterTestConfig();
		mController = new XboxController(mConfig.ports.XBOX);
		if (mConfig.runConstants.RUNNING_GYRO) {
			mNavX = new AHRS(mConfig.ports.NAVX);
		}
		mPDP = new PowerDistributionPanel();

		if (mConfig.runConstants.RUNNING_DRIVE && mConfig.runConstants.RUNNING_GYRO) {
			driveInit();
		}

		if (mConfig.runConstants.RUNNING_INTAKE) {
			// intakeMotor = new TalonSRX(new TalonSRXConfig(mConfig.intakeConstants.INTAKE_PORT, mConfig.intakeConstants.INTAKE_INVERTED));
			intakeMotor = new com.ctre.phoenix.motorcontrol.can.TalonSRX(34);
			intakeMotor.setInverted(true);
		}

		if (mConfig.runConstants.SECONDARY_JOYSTICK) {
			mJoystick = new XboxController(mConfig.ports.JOYSTICK);
		}

		if (mConfig.runConstants.RUNNING_LIFT) {
			liftMotor = new TalonSRX(mConfig.liftConstants.MOTOR_CONFIG);
			liftOutput = mConfig.liftConstants.LIFT_POWER_OUTPUT;
		}

		if (mConfig.runConstants.RUNNING_SHOOTER) {
			// leftShooterMotor = new SparkMax(mConfig.shooterConstants.MOTOR_CONFIG);
			leftShooterMotor = new CANSparkMax(54, MotorType.kBrushless);
			rightShooterMotor = new CANSparkMax(53, MotorType.kBrushless);
			leftShooterMotor.restoreFactoryDefaults();
			rightShooterMotor.restoreFactoryDefaults();
			rightShooterMotor.follow(leftShooterMotor, true);

			leftShooterMotor.getPIDController().setFF(0.00020735834);
			leftShooterMotor.getPIDController().setP(.0002);
			leftShooterMotor.getPIDController().setI(0);
			leftShooterMotor.getPIDController().setD(0);

			beltMotor = new com.ctre.phoenix.motorcontrol.can.TalonSRX(31);
			sideRoller = new com.ctre.phoenix.motorcontrol.can.TalonSRX(37);
			shooterRPM = 0;
			panServo = new Servo(mConfig.shooterConstants.TURRET.PORT);
			hoodServo = new Servo(mConfig.shooterConstants.HOOD.PORT);
			panServo.setBounds(2.5, 1.5, 1.5, 1.5, 0.5);
			hoodServo.setBounds(2.5, 1.5, 1.5, 1.5, 0.5);

			panServoPID = new PIDController(mConfig.shooterConstants.TURRET.P, mConfig.shooterConstants.TURRET.I, mConfig.shooterConstants.TURRET.D);
			hoodServoPID = new PIDController(mConfig.shooterConstants.HOOD.P, mConfig.shooterConstants.HOOD.I, mConfig.shooterConstants.HOOD.D);
		}

		if (mConfig.runConstants.RUNNING_CAMERA) {
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setResolution(240, 180);
			camera.setFPS(30);

			limelight = new Limelight(networkTable.getTable("limelight"));
		}

		if (mConfig.runConstants.RUNNING_PNEUMATICS) {
			mCompressor = new Compressor(mConfig.ports.COMPRESSOR);
		}
	}

	ArrayList<AutonomousCommand> autonomousCommands;
	// initialize step variables
	int currentStep = 0;
	int previousStep = -1;

	@Override
	public void autonomousInit() {
		// select auto commands
		if (mAutonomousRoutine == AutonomousRoutineType.DEFAULT) {
			autonomousCommands = (new DefaultRoutine(this)).getAutonomousCommands();
		} else {
			autonomousCommands = (new DoNothingRoutine()).getAutonomousCommands();
		}

		// start game
		startGame();
	}

	@Override
	public void autonomousPeriodic() {
		SmartDashboard.putNumber("Autonomous step", currentStep);

		if (currentStep < autonomousCommands.size()) {
			AutonomousCommand command = autonomousCommands.get(currentStep);

			if (currentStep != previousStep) {
				command.startup();
				previousStep = currentStep;
			}

			boolean moveToNextStep = command.runCommand();
			if (moveToNextStep) {
				currentStep++;
			}
		} // else we're done with auto
	}

	@Override
	public void teleopInit() {
		// start game, again
		startGame();
	}

	@Override
	public void teleopPeriodic() {
		swerveDrive();

		if (mConfig.runConstants.RUNNING_INTAKE && mConfig.runConstants.SECONDARY_JOYSTICK) {
			runIntake();
		}

		if (mConfig.runConstants.RUNNING_LIFT && mConfig.runConstants.SECONDARY_JOYSTICK) {
			runLift();
		}

		if (mConfig.runConstants.RUNNING_SHOOTER && mConfig.runConstants.SECONDARY_JOYSTICK) {
			runShooter();
		}

		if (mConfig.runConstants.RUNNING_CAMERA && mConfig.runConstants.SECONDARY_JOYSTICK) {
			runAim();
		}

		// put info on SmartDashboard
		if (mConfig.runConstants.RUNNING_DRIVE) {
			for (int i = 0; i < 4; i++) {
				SmartDashboard.putNumber("Motor Current " + i, mWheel[i].Drive.getOutput());
				SmartDashboard.putNumber("Current Offset Angle " + i, mWheel[i].Turn.getReversedOffsetAngle());
				SmartDashboard.putBoolean("Wheel Reversed " + i, mWheel[i].Turn.getReversed());
				SmartDashboard.putBoolean("Drive Inverted " + i, mWheel[i].Drive.getInverted());
				SmartDashboard.putNumber("Raw Ticks " + i, mWheel[i].Turn.getRawPosition());
				SmartDashboard.putNumber("Motor Output " + i, mWheel[i].Drive.getOutput());
				SmartDashboard.putNumber("Gyro Raw Angle", mRobotAngle.getRawAngleDegrees());
			}
		}
	}

	String state = "IDLE";

	private void runShooter() {
		//check secondary for speed change
		// if(mJoystick.getRawButtonReleased(mConfig.shooterConstants.SPEED_UP_BUTTON)) {
		// 	shooterRPM += mConfig.shooterConstants.RPM_INCREMENT;
		// }
		// else if(mJoystick.getRawButtonReleased(mConfig.shooterConstants.SPEED_DOWN_BUTTON)) {
		// 	shooterRPM -= mConfig.shooterConstants.RPM_INCREMENT;
		// }

		// if (mJoystick.getRawButton(mConfig.shooterConstants.DRIVE_BUTTON)) {
		// 	leftShooterMotor.set(0.2);	
		// }
		// else if (mJoystick.getRawButton(mConfig.shooterConstants.REVERSE_BUTTON)) {
		// 	leftShooterMotor.set(-0.2);

		// } else {
		// 	leftShooterMotor.set(0);
		// }

		if (mJoystick.getXButton()) {
			state = "INTAKING";
		}
		if (mJoystick.getBButton()) {
			state = "SHOOTING";
		}
		if (mJoystick.getAButton()) {
			state = "HOLDING";
		}
		if (mJoystick.getYButton()) {
			state = "IDLE";
		}
		switch (state) {
		case "INTAKING":
			intakeMotor.set(ControlMode.PercentOutput, .8);
			beltMotor.set(ControlMode.PercentOutput, 0);
			sideRoller.set(ControlMode.PercentOutput, 0);
			leftShooterMotor.set(0);
			break;
		case "SHOOTING":
			intakeMotor.set(ControlMode.PercentOutput, 0);
			beltMotor.set(ControlMode.PercentOutput, -.5);
			sideRoller.set(ControlMode.PercentOutput, .2);
			leftShooterMotor.set(1);
			break;
		case "HOLDING":
			intakeMotor.set(ControlMode.PercentOutput, 0);
			beltMotor.set(ControlMode.PercentOutput, 0);
			sideRoller.set(ControlMode.PercentOutput, 0);
			leftShooterMotor.set(1);
			break;
		default:
			intakeMotor.set(ControlMode.PercentOutput, 0);
			beltMotor.set(ControlMode.PercentOutput, 0);
			sideRoller.set(ControlMode.PercentOutput, 0);
			leftShooterMotor.set(0);
			break;
		}

		// SmartDashboard.putNumber("Shooter RPM", leftShooterMotor.get());
		// SmartDashboard.putNumber("Shooter RPM Target", shooterRPM);
		// SmartDashboard.putNumber("Shooter motor current draw", leftShooterMotor.g());
		SmartDashboard.putNumber("Shooter motor percent output", leftShooterMotor.get());
	}

	private void runIntake() {
		//check secondary for speed change
		// if(mJoystick.getRawButtonReleased(mConfig.intakeConstants.SPEED_UP_BUTTON)) {
		// 	intakeOutput += mConfig.intakeConstants.SPEED_INCREMENT;
		// }
		// else if(mJoystick.getRawButtonReleased(mConfig.intakeConstants.SPEED_DOWN_BUTTON)) {
		// 	intakeOutput -= mConfig.intakeConstants.SPEED_INCREMENT;
		// }
		// intakeMotor.set(ControlMode.PercentOutput, intakeOutput);
		// SmartDashboard.putNumber("Intake speed", intakeOutput);
	}

	private void runLift() {
		//check secondary for speed change
		if (mJoystick.getRawButtonReleased(mConfig.liftConstants.SPEED_UP_BUTTON)) {
			liftOutput += mConfig.liftConstants.SPEED_INCREMENT;
		} else if (mJoystick.getRawButtonReleased(mConfig.liftConstants.SPEED_DOWN_BUTTON)) {
			liftOutput -= mConfig.liftConstants.SPEED_INCREMENT;
		}

		if (mJoystick.getRawButton(mConfig.liftConstants.DRIVE_BUTTON)) {
			liftMotor.setOutput(liftOutput);
		} else if (mJoystick.getRawButton(mConfig.liftConstants.REVERSE_BUTTON)) {
			liftMotor.setOutput(-liftOutput);

		} else {
			liftMotor.setOutput(0);
		}
		SmartDashboard.putNumber("Lift speed", liftMotor.getOutput());
		SmartDashboard.putNumber("Lift speed we will set it to", liftOutput);
		SmartDashboard.putNumber("Lift motor current draw", liftMotor.getCurrent());
	}

	private void runAim() {
		if (mJoystick.getBumper(Hand.kRight)) {
			if (limelight.get("tv") == 1) {
				double xTarget = limelight.get("tx");
				double yTarget = limelight.get("ty");
				SmartDashboard.putNumber("limelight x target", xTarget);
				SmartDashboard.putNumber("limelight y target", yTarget);
				double panServoSpeed = panServoPID.calculate(xTarget);
				double hoodServoSpeed = hoodServoPID.calculate(yTarget);
				panServoSpeed = panServoSpeed > 1 ? 1 : panServoSpeed;
				panServoSpeed = panServoSpeed < -1 ? -1 : panServoSpeed;
				hoodServoSpeed = hoodServoSpeed > 1 ? 1 : hoodServoSpeed;
				hoodServoSpeed = hoodServoSpeed < -1 ? -1 : hoodServoSpeed;
				panServoSpeed *= mConfig.shooterConstants.TURRET.MAX_SPEED;
				hoodServoSpeed *= mConfig.shooterConstants.HOOD.MAX_SPEED;
				SmartDashboard.putNumber("pan servo speed", panServoSpeed);
				SmartDashboard.putNumber("hood servo speed", hoodServoSpeed);
				panServo.setSpeed(panServoSpeed);
				hoodServo.setSpeed(hoodServoSpeed);
			}
		} else {
			double panSpeed = -mJoystick.getX(Hand.kRight);
			double hoodSpeed = mJoystick.getY(Hand.kRight);
			SmartDashboard.putNumber("manual pan speed %", panSpeed);
			SmartDashboard.putNumber("manual hood speed %", hoodSpeed);
			panSpeed = Math.abs(panSpeed) < .2 ? 0 : panSpeed;
			hoodSpeed = Math.abs(hoodSpeed) < .2 ? 0 : hoodSpeed;
			panServo.setSpeed(panSpeed * .9);
			hoodServo.setSpeed(hoodSpeed * .9);
		}
	}

	public void startGame() {
		if (!mInGame) {
			mGameStartMillis = System.currentTimeMillis();

			CompressorWrapper.action(mCompressor, mConfig);
			mInGame = true;
		}
	}

	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		// if (mConfig.runConstants.SECONDARY_JOYSTICK && mJoystick.getTriggerPressed()) {
		// 	// rotate autonomous routines to select which one to start with:
		// 	if (mAutonomousRoutine == AutonomousRoutineType.DEFAULT) {
		// 		mAutonomousRoutine = AutonomousRoutineType.DO_NOTHING;
		// 	} else if (mAutonomousRoutine == AutonomousRoutineType.DO_NOTHING) {
		// 		mAutonomousRoutine = AutonomousRoutineType.DEFAULT;
		// 	}
		// }

		// SmartDashboard.putString("AUTO ROUTINE:", mAutonomousRoutine.toString());
	}

	private void tankDrive() {
		if (mConfig.runConstants.RUNNING_DRIVE) {
			mDriveTrain.driveTank();
		}
	}

	private void crabDrive() {
		if (mConfig.runConstants.RUNNING_DRIVE) {
			mDriveTrain.driveCrab();
		}
	}

	private void swerveDrive() {
		if (mConfig.runConstants.RUNNING_DRIVE) {
			mDriveTrain.driveSwerve();
		}
	}

	public void driveInit() {
		for (int i = 0; i < 4; i++) {
			mWheel[i] = new Wheel(mConfig.wheelConfigs[i]);
		}
		mRobotAngle = new RobotAngle(mNavX, false, 0);
		mDriveTrain = new DriveTrain(mWheel, mController, mRobotAngle, mConfig);
	}

	public DriveTrain getDriveTrain() {
		return mDriveTrain;
	}

	private void addLogValueDouble(StringBuilder pLogString, double pVal) {
		pLogString.append(pVal);
		pLogString.append(",");
	}

	private void addLogValueInt(StringBuilder pLogString, int pVal) {
		pLogString.append(pVal);
		pLogString.append(",");
	}

	private void addLogValueLong(StringBuilder pLogString, long pVal) {
		pLogString.append(pVal);
		pLogString.append(",");
	}

	private void addLogValueBoolean(StringBuilder pLogString, boolean pVal) {
		pLogString.append(pVal ? "1" : "0");
		pLogString.append(",");
	}

	private void addLogValueString(StringBuilder pLogString, String pVal) {
		pLogString.append(pVal);
		pLogString.append(",");
	}

	private void addLogValueEndDouble(StringBuilder pLogString, double pVal) {
		pLogString.append(pVal);
		pLogString.append("\n");
	}

	private void addLogValueEndInt(StringBuilder pLogString, int pVal) {
		pLogString.append(pVal);
		pLogString.append("\n");
	}

	private void addLogValueEndLong(StringBuilder pLogString, long pVal) {
		pLogString.append(pVal);
		pLogString.append("\n");
	}

	private void addLogValueEndBoolean(StringBuilder pLogString, boolean pVal) {
		pLogString.append(pVal ? "1" : "0");
		pLogString.append("\n");
	}

	private void addLogValueEndString(StringBuilder pLogString, String pVal) {
		pLogString.append(pVal);
		pLogString.append("\n");
	}

	public void log() {
		long time = System.currentTimeMillis();
		long timeElapsed = time - mGameStartMillis;

		SmartDashboard.putBoolean("Game Has Started:", mInGame);
		SmartDashboard.putNumber("Time Game Started:", mGameStartMillis);
		SmartDashboard.putNumber("Time Elapsed:", timeElapsed);

		StringBuilder logString = new StringBuilder();

		// for now it is one frame per line
		addLogValueInt(logString, (int) timeElapsed);

		addLogValueBoolean(logString, mController.getYButton());
		addLogValueBoolean(logString, mController.getBButton());
		addLogValueBoolean(logString, mController.getAButton());
		addLogValueBoolean(logString, mController.getXButton());
		addLogValueBoolean(logString, mController.getBumper(Hand.kLeft));
		addLogValueBoolean(logString, mController.getBumper(Hand.kRight));
		addLogValueDouble(logString, mController.getTriggerAxis(Hand.kLeft));
		addLogValueDouble(logString, mController.getTriggerAxis(Hand.kRight));
		addLogValueInt(logString, mController.getPOV());
		addLogValueBoolean(logString, mController.getStartButton());
		addLogValueBoolean(logString, mController.getBackButton());
		addLogValueDouble(logString, mController.getX(Hand.kLeft));
		addLogValueDouble(logString, mController.getY(Hand.kLeft));
		addLogValueDouble(logString, mController.getX(Hand.kRight));
		addLogValueDouble(logString, mController.getY(Hand.kRight));

		if (mConfig.runConstants.SECONDARY_JOYSTICK) {
			for (int i = 1; i < 12; i++) {
				addLogValueBoolean(logString, mJoystick.getRawButton(i));
			}
		}

		if (mConfig.runConstants.RUNNING_DRIVE) {
			for (int i = 0; i < 4; i++) {
				//TODO: put these back
				// addLogValueDouble(logString, mTurn[i].getOutputCurrent());
				// addLogValueDouble(logString, mDrive[i].getOutputCurrent());

				// addLogValueDouble(logString, mTurn[i].getMotorOutputVoltage());
				// addLogValueDouble(logString, mDrive[i].getMotorOutputVoltage());

				addLogValueDouble(logString, mEncoder[i].getAngleDegrees());
			}

			addLogValueDouble(logString, mDriveTrain.getDesiredRobotVel().getMagnitude());
			addLogValueDouble(logString, mDriveTrain.getDesiredRobotVel().getAngle());
			addLogValueDouble(logString, mDriveTrain.getDesiredAngularVel());
		}

		if (mConfig.runConstants.RUNNING_PNEUMATICS) {
			addLogValueDouble(logString, mCompressor.getCompressorCurrent());
		}
		addLogValueDouble(logString, mPDP.getTotalCurrent());
		addLogValueDouble(logString, mPDP.getVoltage());

		addLogValueEndDouble(logString, mRobotAngle.getAngleDegrees());

		SmartDashboard.putString("LogString", logString.toString());
	}
}
