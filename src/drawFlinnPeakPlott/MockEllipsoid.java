package drawFlinnPeakPlott;

import java.util.Arrays;
import java.util.Random;

public final class MockEllipsoid {
	private double radii[] = new double [3];
	private final double MAX_RADIUS = 10.0;
	private static final Random random = new Random(System.currentTimeMillis()); 
	
	public MockEllipsoid() {
		generateRadii();
	}
	
	/** Sorts the radii */
	public void generateRadii() {
		// A quick fix to avoid division by zero
		radii[0] = random.nextDouble() * MAX_RADIUS + 0.1;
		radii[1] = random.nextDouble() * MAX_RADIUS + 0.1;
		radii[2] = random.nextDouble() * MAX_RADIUS + 0.1;
		Arrays.sort(radii);
	}
	
	public double[] getRadii() {
		return radii;
	}
}
