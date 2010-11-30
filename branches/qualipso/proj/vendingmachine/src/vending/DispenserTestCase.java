package vending;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DispenserTestCase extends TestCase {
	protected Dispenser d;
	
	protected void setUp() throws Exception {
		d = new Dispenser();
	}
 
    public void testDispenserException() {
    	int val;

    	d.setValidSelection( null );
    	val = d.dispense( 50, 10 );
    	assertEquals( 0, val );
    }

    public static Test suite() {
    	return new TestSuite(DispenserTestCase.class);
    }

    public static void main(String[] args) {
    	junit.textui.TestRunner.run(suite());
    }

}
