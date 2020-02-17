package common.servos.configs;

import common.pid.configs.PIDConfig;
import common.servos.configs.interfaces.IPWMConfig;
import common.servos.configs.interfaces.IRevSRSConfig;

public class RevSRSConfig extends BasePWMConfig implements IRevSRSConfig {

    protected int port;
    protected PIDConfig pidConfig;

    public RevSRSConfig(int port, PIDConfig pidConfig) {
        super(port);
        this.pidConfig = pidConfig;
    }

    @Override
    public PIDConfig getPIDConfig() {
        return pidConfig;
    }

}