package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.TimedRobot;

public class Robot2 extends TimedRobot {
    private WPI_TalonSRX backLeft;

    @Override
    public void robotInit() {
        backLeft = new WPI_TalonSRX(2);
    }
}