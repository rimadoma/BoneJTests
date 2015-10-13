package fillingEfficiencyCalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FillingEfficiencyCalculatorTest {

	@Test
	public void testCalculateFillingEfficiencyJava8Style() {
		final long FILLED_PIXELS = 9;
		final long FOREGROUND_PIXELS = 12;
		final double EXPECTED_EFFICIENCY = (double) FILLED_PIXELS / (double) FOREGROUND_PIXELS;  
		final int [][] MAX_IDS = {{-2, -2, -2}, {-1, -1, -1}, {0, 0, 0}, {1, 1, 1}, {2, 2, 2}};
		final double EPSILON = 0.00000001;
		
		double result = FillingEfficiencyCalculator.calculateFillingEfficiencyJava8Style(MAX_IDS);
		double oldStyleResult = FillingEfficiencyCalculator.calculateFillingEfficiency(MAX_IDS);
		double sequentialResult = FillingEfficiencyCalculator.calculateFillingEfficiencySequential(MAX_IDS);
		
		assertEquals(EXPECTED_EFFICIENCY, result, EPSILON);
		assertEquals(oldStyleResult, result, EPSILON);
		assertEquals(result, sequentialResult, EPSILON);
	}

}
