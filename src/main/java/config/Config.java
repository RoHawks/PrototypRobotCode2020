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

public class Config {
    public RunConstants runConstants;
    public DriveConstants driveConstants;
    public Ports ports;
    public SwerveSpeeds swerveSpeeds;
    public WheelConfig[] wheelConfigs;
    public IntakeConstants intakeConstants; 
    public LiftConstants liftConstants; 
    public ShooterConstants shooterConstants;
    public LimeLightConstants limeLightConstants;

    public Config() {
        runConstants = new RunConstants();
        driveConstants = new DriveConstants();
        ports = new Ports();
        swerveSpeeds = new SwerveSpeeds();
        wheelConfigs = new WheelConfig[4];
        intakeConstants = new IntakeConstants(); 
        liftConstants = new LiftConstants(); 
        shooterConstants = new ShooterConstants();
        limeLightConstants = new LimeLightConstants();
    }

    //Constants for the intake test mechanism
    public class IntakeConstants {    
        public final int 
            INTAKE_PORT = 5,
            SPEED_UP_BUTTON = 4, //Y button
            SPEED_DOWN_BUTTON = 1; //A button
        public final boolean INTAKE_INVERTED = true;
        public final double 
            INTAKE_POWER_OUTPUT = 1,
            SPEED_INCREMENT = .1;
    }

    public class LiftConstants {    
        public final int 
            LIFT_PORT = 11,
            SPEED_UP_BUTTON = 4, //Y button
            SPEED_DOWN_BUTTON = 1, //A button
            DRIVE_BUTTON = 6, //Right shoulder button
            REVERSE_BUTTON = 5; //Left shoulder button
        public final boolean LIFT_INVERTED = true;
        public final boolean HAS_TOP_LIMIT_SWITCH = true;
        public final boolean HAS_BOTTOM_LIMIT_SWITCH = true;
        public final double 
            LIFT_POWER_OUTPUT = 0,
            SPEED_INCREMENT = .05;
        public ITalonSRXConfig MOTOR_CONFIG = new TalonSRXConfig(LIFT_PORT, LIFT_INVERTED);
    }


    public class ShooterConstants {

        public Flywheel FLYWHEEL = new Flywheel();
        public HoodServo HOOD = new HoodServo();
        public TurretServo TURRET = new TurretServo();
        public Belt BELT = new Belt();

        public class Flywheel {

            public final int 
                LEFT_MOTOR_PORT = 50, //TODO Insert correct port
                RIGHT_MOTOR_PORT = 50, //TODO Insert correct port

                // SPEED_UP_BUTTON = 4, //Y button
                // SPEED_DOWN_BUTTON = 1, //A button
                DRIVE_BUTTON = 6, //Right shoulder button
                REVERSE_BUTTON = 5, //Left shoulder button
                AIM_BUTTON = 4; // Y butto

            public final boolean SHOOTER_INVERTED = true;

            public final double
                RPM_INCREMENT = 100,
                P = 1,
                I = .001,
                D = 0,
                iZone = 500;
                
            public SparkMaxWithEncoderConfig MOTOR_CONFIG = new SparkMaxWithEncoderConfig(
                new SparkMaxConfig(LEFT_MOTOR_PORT, SHOOTER_INVERTED),
                new BaseEncoderConfig(0, false),
                new PIDConfig(P, I, D, iZone)
                );
        }

        public class HoodServo {

            public final int
                PORT = 1, //TODO Insert correct port
                CLOCKWISE_BUTTON = 6, //Right shoulder button
                COUNTERCLOCKWISE_BUTTON = 5; //Left shoulder button

            public final double
                MAX_SPEED = 0.9,
                P = 1,
                I = 2.5,
                D = 0,
                iZone = 500;

            public PIDConfig PID_CONFIG = new PIDConfig(P, I, D, iZone);
        }

        public class TurretServo {

            public final int
                PORT = 0;
                // CLOCKWISE_BUTTON, COUNTERCLOCKWISE_BUTTON;

            public final double
                MAX_SPEED = 0.9,
                P = 1,
                I = 2.5,
                D = 0,
                iZone = 500;

            public PIDConfig PID_CONFIG = new PIDConfig(P, I, D, iZone);
        }

        public class Belt {

            public final int PORT = 55;
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
            RUNNING_SHOOTER;
    }

    // Constants from DriveConstants
    public class DriveConstants {
        //speed mins, when lower than these don't do anything
        public final double 
            MIN_LINEAR_VELOCITY = 0.02, 
            MIN_DIRECTION_MAG = 0.25, // refers to joystick magnitudes
            MAX_INDIVIDUAL_VELOCITY = 1.0;

        public final double 
            EMERGENCY_VOLTAGE = 10000, 
            MAX_EMERGENCY_VOLTAGE = 0.5;

        public final double 
            MAX_ANGULAR_VELOCITY = 1.0, 
            MAX_LINEAR_VELOCITY = 0.5;

        public final int
            PID_INDEX = 0, 
            ROTATIONAL_TOLERANCE = 5,
            ROTATION_IZONE = 500,
            SENSOR_POSITION = 0;

        public final double 
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
        public final int
            XBOX = 0,
            JOYSTICK = 1,
            COMPRESSOR = 0;
    }

    public class SwerveSpeeds {
        public final double 
            SPEED_MULT = 1.0,
            ANGULAR_SPEED_MULT = 1.0,
            NUDGE_MOVE_SPEED = 0.2,
            NUDGE_TURN_SPEED = 0.2;
    }

    public class LimeLightConstants {
        // TBD
    }
}
