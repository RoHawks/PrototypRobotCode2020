package common.servos.configs;

import common.pid.configs.PIDConfig;
import common.servos.configs.interfaces.IPWMConfig;
import common.servos.configs.interfaces.IRevSRSConfig;

public class RevSRSConfig extends BasePWMConfig implements IRevSRSConfig {

    protected int port;
    protected PIDConfig pidConfig;
    protected double speedCap;

    public RevSRSConfig(int port, double speedCap, PIDConfig pidConfig) {
        super(port);
        this.pidConfig = pidConfig;
        this.speedCap = speedCap;
    }

    public RevSRSConfig(int port, double speedCap) {
        super(port);
        this.speedCap = speedCap;
    }

    @Override
    public PIDConfig getPIDConfig() {
        return pidConfig;
    }

    @Override
    public double getSpeedCap() {
        return speedCap;
    }
}
