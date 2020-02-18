package config;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import common.encoders.configs.BaseEncoderConfig;
import common.motors.configs.SparkMaxConfig;
import common.motors.configs.SparkMaxWithEncoderConfig;
import common.motors.configs.TalonSRXConfig;
import common.motors.configs.interfaces.ITalonSRXConfig;
import common.pid.configs.PIDConfig;
import drivetrain.swerve.wheels.configs.WheelConfig;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Port;
import common.servos.configs.RevSRSConfig;

public class Config {
    public RunConstants runConstants;
    public DriveConstants driveConstants;
    public Ports ports;
    public SwerveSpeeds swerveSpeeds;
    public WheelConfig[] wheelConfigs;
    public IntakeConstants INTAKE; 
    public LiftConstants LIFTER; 
    public ShooterConstants SHOOTER;

    public Config() {
        runConstants = new RunConstants();
        driveConstants = new DriveConstants();
        ports = new Ports();
        swerveSpeeds = new SwerveSpeeds();
        wheelConfigs = new WheelConfig[4];
        INTAKE = new IntakeConstants(); 
        LIFTER = new LiftConstants(); 
        SHOOTER = new ShooterConstants();
    }

    //Constants for the intake test mechanism
    public class IntakeConstants {    
        public int 
            INTAKE_PORT = 5,
            SPEED_UP_BUTTON = 4, //Y button
            SPEED_DOWN_BUTTON = 1; //A button
        public boolean INTAKE_INVERTED = true;
        public double 
            INTAKE_POWER_OUTPUT = 0,
            SPEED_INCREMENT = .1;
    }

    public class LiftConstants {    
        public int 
            LIFT_PORT = 11,
            SPEED_UP_BUTTON = 4, //Y button
            SPEED_DOWN_BUTTON = 1, //A button
            DRIVE_BUTTON = 6, //Right shoulder button
            REVERSE_BUTTON = 5; //Left shoulder button
        public boolean LIFT_INVERTED = true;
        public boolean HAS_TOP_LIMIT_SWITCH = true;
        public boolean HAS_BOTTOM_LIMIT_SWITCH = true;
        public double 
            LIFT_POWER_OUTPUT = 0,
            SPEED_INCREMENT = .05;
        public ITalonSRXConfig MOTOR_CONFIG = new TalonSRXConfig(LIFT_PORT, LIFT_INVERTED);
    }

    public class ShooterConstants {
        public Flywheel FLYWHEEL = new Flywheel();
        public Hood HOOD = new Hood();
        public Pan PAN = new Pan();
        public Belt BELT = new Belt();

        public class Flywheel {
            public int 
                SHOOTER_PORT = 54,
                SPEED_UP_BUTTON = 4, //Y button
                SPEED_DOWN_BUTTON = 1; //A button

            public boolean SHOOTER_INVERTED = true;

            public double 
                RPM_INCREMENT = 100, 
                P = 1, 
                I = .001, 
                D = 0, 
                iZone = 500;

            public SparkMaxWithEncoderConfig MOTOR_CONFIG = new SparkMaxWithEncoderConfig(
                new SparkMaxConfig(SHOOTER_PORT, SHOOTER_INVERTED),
                new BaseEncoderConfig(0, false),
                new PIDConfig(P, I, D, iZone)
            );
        }

        public class Hood {
            public int
                CHANNEL = 9,
                CLOCKWISE_BUTTON = 6, //Right shoulder button
                COUNTERCLOCKWISE_BUTTON = 5; //Left shoulder button
            
            private double
                SPEED_CAP = 1,
                P = 1,
                I = 2.5,
                D = 0,
                IZONE = 0;

            public RevSRSConfig RevSRSConfig = new RevSRSConfig(
                CHANNEL,
                SPEED_CAP,
                new PIDConfig(P, I, D, IZONE));
        }

        public class Pan {
            public int
                CHANNEL,
                CLOCKWISE_BUTTON,
                COUNTERCLOCKWISE_BUTTON;

            private double
                SPEED_CAP = 1,
                P = 1,
                I = 2.5,
                D = 0,
                IZONE = 0;

            public RevSRSConfig RevSRSConfig = new RevSRSConfig(
                CHANNEL,
                SPEED_CAP,
                new PIDConfig(P, I, D, IZONE)
            );
        }

        public class Belt {
            
        }
    }


    // Constatnts from RunConstants
    public class RunConstants {  
        public boolean RUNNING_DRIVE,
            RUNNING_PNEUMATICS,
            RUNNING_CAMERA,
            SECONDARY_JOYSTICK,
            RUNNING_INTAKE, 
            RUNNING_GYRO,
            RUNNING_LIFT,
            RUNNING_SHOOTER,
            RUNNING_SERVO;
    }

    // Constants from DriveConstants
    public class DriveConstants {
        //speed mins, when lower than these don't do anything
        public double 
            MIN_LINEAR_VELOCITY = 0.02, 
            MIN_DIRECTION_MAG = 0.25, // refers to joystick magnitudes
            MAX_INDIVIDUAL_VELOCITY = 1.0;

        public double 
            EMERGENCY_VOLTAGE = 10000, 
            MAX_EMERGENCY_VOLTAGE = 0.5;

        public double 
            MAX_ANGULAR_VELOCITY = 1.0, 
            MAX_LINEAR_VELOCITY = 0.5;

        public int
            PID_INDEX = 0, 
            ROTATIONAL_TOLERANCE = 5,
            ROTATION_IZONE = 500,
            SENSOR_POSITION = 0;

        public double 
            GYRO_P = 0.004, 
            GYRO_I = 0.00002, 
            GYRO_D = 0, 
            GYRO_TOLERANCE = 5,
            GYRO_MAX_SPEED = 1,

            DRIFT_COMP_P = 0.03, 
            DRIFT_COMP_I = 0, 
            DRIFT_COMP_D = 0, 
            DRIFT_COMP_MAX = 0.3;

        public FeedbackDevice SENSOR_TYPE = FeedbackDevice.CTRE_MagEncoder_Absolute;
    }

    public class Ports {
        public SerialPort.Port NAVX = Port.kMXP;
        public int
            XBOX = 0,
            JOYSTICK = 1,
            COMPRESSOR = 0;
    }

    public class SwerveSpeeds {
		public double 
			SPEED_MULT = 1.0,
			ANGULAR_SPEED_MULT = 1.0,
			NUDGE_MOVE_SPEED = 0.2,
			NUDGE_TURN_SPEED = 0.2;
    }
}