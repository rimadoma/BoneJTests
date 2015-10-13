package drawFlinnPeakPlott;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class DrawFlinnPeakPlot {		
	private static final int WIDTH = 10;
	private static final int HEIGHT = 10;
	private static final int SCREEN_SIZE = WIDTH * HEIGHT;
	private static int DEPTH = 10;

	public static MockEllipsoid[] generateMockEllipsoids(int nEllipsoids) {
		MockEllipsoid[] mockEllipsoids = new MockEllipsoid[nEllipsoids];

		for(int i = 0; i < nEllipsoids; i++) {
			mockEllipsoids[i]= new MockEllipsoid();
		}

		return mockEllipsoids;
	}

	private static int[][] generateRandomIDs() {
		int ids [][] = new int [DEPTH][SCREEN_SIZE];
		Random random = new Random(System.currentTimeMillis());

		for (int i = 0; i < DEPTH; i++ ) {
			for (int j = 0; j < SCREEN_SIZE; j++) {
				ids[i][j] = random.nextInt(5) - 2;
			}
		}

		return ids;
	}

	private static float[] drawFlinnPeakPlot(int[][] maxIDs, MockEllipsoid[] ellipsoids) {
		final float[][] ab = new float[DEPTH][];
		final float[][] bc = new float[DEPTH][];

		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = MultiThreader.newThreads();
		for (int thread = 0; thread < threads.length; thread++) {
			threads[thread] = new Thread(new Runnable() {
				public void run() {

					for (int z = ai.getAndIncrement(); z < DEPTH; z = ai
							.getAndIncrement()) {
						synchronized (threads) {						
							int[] idSlice = maxIDs[z];
							int l = 0;
							for (int y = 0; y < HEIGHT; y++) {
								final int offset = y * WIDTH;
								for (int x = 0; x < WIDTH; x++)
									if (idSlice[offset + x] >= 0)
										l++;
							}
							float[] abl = new float[l];
							float[] bcl = new float[l];
							int j = 0;
							for (int y = 0; y < HEIGHT; y++) {
								final int offset = y * WIDTH;
								for (int x = 0; x < WIDTH; x++) {
									final int i = offset + x;
									final int id = idSlice[i];
									if (id >= 0) {
										double radii[] = ellipsoids[id].getRadii();
										abl[j] = (float) (radii[0] / radii[1]);
										bcl[j] = (float) (radii[1] / radii[2]);
										j++;
									}
								}
							}
							ab[z] = abl;
							bc[z] = bcl;
						}
					}
				}
			});
		}
		MultiThreader.startAndJoin(threads);

		float checkRow[] = ab[0];
		return checkRow;
	}

	private static float[] drawFlinnPeakPlotSequential(int[][] maxIDs, MockEllipsoid[] ellipsoids) {
		final float[][] ab = new float[DEPTH][];
		final float[][] bc = new float[DEPTH][];

		for (int z = 0; z < DEPTH; z++) {
			int[] idSlice = maxIDs[z];
			int l = 0;
			for (int y = 0; y < HEIGHT; y++) {
				final int offset = y * WIDTH;
				for (int x = 0; x < WIDTH; x++)
					if (idSlice[offset + x] >= 0)
						l++;
			}
			float[] abl = new float[l];
			float[] bcl = new float[l];
			int j = 0;
			for (int y = 0; y < HEIGHT; y++) {
				final int offset = y * WIDTH;
				for (int x = 0; x < WIDTH; x++) {
					final int i = offset + x;
					final int id = idSlice[i];
					if (id >= 0) {
						double radii[] = ellipsoids[id].getRadii();
						abl[j] = (float) (radii[0] / radii[1]);
						bcl[j] = (float) (radii[1] / radii[2]);
						j++;
					}
				}
			}
			ab[z] = abl;
			bc[z] = bcl;
		}

		float checkRow[] = ab[0];
		return checkRow;		
	}
	
	//It's assumed that the arrays have the same length
	private static boolean rowsMatch(float rowA[], float rowB[]) {
		for (int i = 0; i < rowA.length; i++) {
			if (Double.compare(rowA[i], rowB[i] ) != 0) {
				return false;
			}
		}

		return true;
	}

	public static void main(String[] args) {
		MockEllipsoid[] mockEllipsoids = generateMockEllipsoids(100);		
		int maxIDs[][] = generateRandomIDs();
		Instant start, stop;

		start = Instant.now();
		System.out.println("Starting drawFlinnPeakPlot()  at " + start);
		float controlRow[] = drawFlinnPeakPlot(maxIDs, mockEllipsoids);
		stop = Instant.now();
		System.out.println("Finished drawFlinnPeakPlot() at " + stop);
		System.out.println("Time elapsed: " + (stop.toEpochMilli() - start.toEpochMilli() + " ms\n"));


		start = Instant.now();
		System.out.println("Starting drawFlinnPeakPlotSequential()  at " + start);
		float sequentialControlRow[] = drawFlinnPeakPlotSequential(maxIDs, mockEllipsoids);
		stop = Instant.now();
		System.out.println("Finished drawFlinnPeakPlotSequential() at " + stop);
		System.out.println("Time elapsed: " + (stop.toEpochMilli() - start.toEpochMilli() + " ms\n"));

		if (rowsMatch(controlRow, sequentialControlRow)) {
			System.out.println("Old style parallel and sequential methods match");
		} else {
			System.out.println("Error in testing: old style parallel and sequential methods differ!");
		}

	}

}
