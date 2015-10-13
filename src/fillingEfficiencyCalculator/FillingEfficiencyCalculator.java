package fillingEfficiencyCalculator;

import java.util.concurrent.atomic.AtomicInteger;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;

public class FillingEfficiencyCalculator {

	public static double calculateFillingEfficiency(int[][] maxIDs) {
		final int l = maxIDs.length;
		final long[] foregroundCount = new long[l];
		final long[] filledCount = new long[l];

		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = MultiThreader.newThreads();
		for (int thread = 0; thread < threads.length; thread++) {
			threads[thread] = new Thread(new Runnable() {
				public void run() {

					for (int i = ai.getAndIncrement(); i < l; i = ai
							.getAndIncrement()) {
						//System.out.println("Calculating filling effiency...");
						int[] idSlice = maxIDs[i];
						final int len = idSlice.length;
						for (int j = 0; j < len; j++) {
							final int val = idSlice[j];
							if (val >= -1)
								foregroundCount[i]++;
							if (val >= 0)
								filledCount[i]++;
						}
					}
				}
			});
		}
		MultiThreader.startAndJoin(threads);

		long sumForegroundCount = 0;
		long sumFilledCount = 0;

		for (int i = 0; i < l; i++) {
			sumForegroundCount += foregroundCount[i];
			sumFilledCount += filledCount[i];
		}

		long unfilled = sumForegroundCount - sumFilledCount;
		System.out.println(unfilled + " pixels unfilled with ellipsoids out of "
				+ sumForegroundCount + " total foreground pixels");

		return (double) sumFilledCount / (double) sumForegroundCount;
	}

	public static double calculateFillingEfficiencySequential(int[][] maxIDs) {
		final int l = maxIDs.length;
		final long[] foregroundCount = new long[l];
		final long[] filledCount = new long[l];

		for (int i = 0; i < l; i++) {
			//System.out.println("Calculating filling effiency...");
			int[] idSlice = maxIDs[i];
			final int len = idSlice.length;
			for (int j = 0; j < len; j++) {
				final int val = idSlice[j];
				if (val >= -1) {
					foregroundCount[i]++;
					if (val >= 0) {
						filledCount[i]++;
					}
				}
			}
		}

		long sumForegroundCount = 0;
		long sumFilledCount = 0;

		for (int i = 0; i < l; i++) {
			sumForegroundCount += foregroundCount[i];
			sumFilledCount += filledCount[i];
		}

		long unfilled = sumForegroundCount - sumFilledCount;
		System.out.println(unfilled + " pixels unfilled with ellipsoids out of "
				+ sumForegroundCount + " total foreground pixels");

		return (double) sumFilledCount / (double) sumForegroundCount;
	}

	// totalForegroundPixels == 0 -> 0.0 || 1.0?
	public static double calculateFillingEfficiencyJava8Style(int maxIDs[][]) 
	{
		long totalForegroundPixels = 0;
		long filledPixels = 0;
		
		for (int i = 0; i < maxIDs.length; i++) {
			int tFPRowSum = (int) Arrays.stream(maxIDs[i]).parallel().filter(id -> id >= -1).count();
			totalForegroundPixels += tFPRowSum;
			int fPRowSum = (int) Arrays.stream(maxIDs[i]).parallel().filter(id -> id >= 0).count();
			filledPixels += fPRowSum;
		}
		
		long unfilled = totalForegroundPixels - filledPixels;
		System.out.println(unfilled + " pixels unfilled with ellipsoids out of "
				+ totalForegroundPixels + " total foreground pixels");

		return (double) filledPixels / (double) totalForegroundPixels;
	}

	private static int[][] generateRandomIDs(int size) {		
		int ids [][] = new int [size][size];
		Random random = new Random(System.currentTimeMillis());

		for (int i = 0; i < size; i++ ) {
			for (int j = 0; j < size; j++) {
				ids[i][j] = random.nextInt(5) - 2;
			}
		}

		return ids;
	}

	public static void main(String[] args) {
		int [][] maxIDs = generateRandomIDs(10_000);
		Instant start, stop;

		start = Instant.now();
		System.out.println("Starting filling efficiency at " + start);		
		FillingEfficiencyCalculator.calculateFillingEfficiency(maxIDs);
		stop = Instant.now();
		System.out.println("Finished filling efficiency at " + stop);
		System.out.println("Time elapsed: " + (stop.toEpochMilli() - start.toEpochMilli() + " ms\n"));

		start = Instant.now();
		System.out.println("Starting sequential filling efficiency at " + start);		
		FillingEfficiencyCalculator.calculateFillingEfficiencySequential(maxIDs);
		stop = Instant.now();
		System.out.println("Starting sequential  filling efficiency at " + stop);
		System.out.println("Time elapsed: " + (stop.toEpochMilli() - start.toEpochMilli() + " ms\n"));
		
		start = Instant.now();
		System.out.println("Starting lambda & stream filling efficiency at " + start);		
		FillingEfficiencyCalculator.calculateFillingEfficiencyJava8Style(maxIDs);
		stop = Instant.now();
		System.out.println("Starting lambda & stream filling efficiency at " + stop);
		System.out.println("Time elapsed: " + (stop.toEpochMilli() - start.toEpochMilli() + " ms\n"));
	}
}
