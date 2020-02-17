package common.servos.configs.interfaces;
import common.pid.configs.PIDConfig;

public interface IRevSRSConfig extends IPWMConfig {
    PIDConfig getPIDConfig();
}