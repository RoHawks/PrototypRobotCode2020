package common.cameras;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class LimeLight {

    NetworkTable networkTable;

    public LimeLight(NetworkTable mNetworkTable) {
        this.networkTable = mNetworkTable;
    }

    public double get(String key) {
        return networkTable.getEntry(key).getDouble(0);
    }
}
