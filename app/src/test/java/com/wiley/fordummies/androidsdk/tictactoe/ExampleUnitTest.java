package com.wiley.fordummies.androidsdk.tictactoe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
	public void isFloatingPointNum() throws Exception {
    	double myDouble = 10.0;
    	assertTrue(Double.isFinite(myDouble) && !Double.isNaN(myDouble));
	}

	@Test
	public void isZipCodeValid() throws Exception {
    	String zip = "43210";
    	assert((zip.length() == 5 || zip.length() == 9) && zip.matches("[0-9]+"));
	}


	@Test
	public void isMalformedZipCodeInvalid() throws Exception {
		String zip = "XA2 CB3";
		assert((zip.length() != 5 && zip.length() != 9) && !zip.matches("[0-9]+"));
	}
}
