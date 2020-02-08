package sensors;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import resource.ResourceFunctions;

public abstract class RotationInputter implements PIDSource {

	private double mOffsetDegrees;
	private boolean mAdd180;

	public RotationInputter(double pOffset) {
		mOffsetDegrees = pOffset;
	}

	public void setAdd180(boolean add) {
		mAdd180 = add;
	}

	protected abstract double getRawAngleDegrees();

	public double getAngleDegreesNoAdd180() {
		double angle = getRawAngleDegrees();
		return ResourceFunctions.putAngleInRange(angle);
	}

	public double getAngleDegrees() {
		double angle = getRawAngleDegrees();
		angle -= mOffsetDegrees;
		if (mAdd180) {
			angle += 180; // when we reverse the direction, update the angle
		}
		angle = ResourceFunctions.putAngleInRange(angle);

		return angle;
	}

	public boolean getAdd180() {
		return mAdd180;
	}

	public double pidGet() {
		return getAngleDegrees();
	}

	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {

	}

	@Override
	public PIDSourceType getPIDSourceType() {
		return null;
	}

	// @Override
	public void setOffset(double offset) {
		mOffsetDegrees = offset;
	}

	public double getOffset() {
		return mOffsetDegrees;
	}

}
