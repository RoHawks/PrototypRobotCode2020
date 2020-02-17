package common.servos.configs;

import common.servos.configs.interfaces.IPWMConfig;

public class BasePWMConfig implements IPWMConfig {
    protected int port;

    public BasePWMConfig(int port) {
        this.port = port;
    }

    @Override
    public int getPort() {
        return port;
    }

}