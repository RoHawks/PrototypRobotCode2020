package common.cameras;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight {

    NetworkTable networkTable;

    public Limelight(NetworkTable mNetworkTable) {
        this.networkTable = mNetworkTable;
    }

    public double get(String key) {
        return networkTable.getEntry(key).getDouble(0);
    }
}
