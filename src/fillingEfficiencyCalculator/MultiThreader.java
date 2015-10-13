package fillingEfficiencyCalculator;

/**
 * Multithreader utility class for convenient multithreading of ImageJ plugins
 * 
 * @author Stephan Preibisch
 * @author Michael Doube
 * 
 * @see <p>
 *      <a href="http://repo.or.cz/w/trakem2.git?a=blob;f=mpi/fruitfly/general/MultiThreading.java;hb=HEAD"
 *      >http://repo.or.cz/w/trakem2.git?a=blob;f=mpi/fruitfly/general/
 *      MultiThreading.java;hb=HEAD</a>
 */
public class MultiThreader {
	public static void startTask(Runnable run) {
		Thread[] threads = newThreads();

		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(run);
		startAndJoin(threads);
	}

	public static void startTask(Runnable run, int numThreads) {
		Thread[] threads = newThreads(numThreads);

		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(run);
		startAndJoin(threads);
	}

	public static Thread[] newThreads() {
		int nthread = 4;
		return new Thread[nthread];
	}

	public static Thread[] newThreads(int numThreads) {
		return new Thread[numThreads];
	}

	public static void startAndJoin(Thread[] threads) {
		for (int ithread = 0; ithread < threads.length; ++ithread) {
			threads[ithread].setPriority(Thread.NORM_PRIORITY);
			threads[ithread].start();
		}

		try {
			for (int ithread = 0; ithread < threads.length; ++ithread)
				threads[ithread].join();
		} catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}
	}
}
