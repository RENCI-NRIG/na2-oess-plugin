package org.renci.nodeagent2.oess;

import org.renci.nodeagent2.oess.Main.OESSEndpoint;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for OESS
 */
public class OESSTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public OESSTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( OESSTest.class );
    }


    public void testUrnParse()
    {
    	String urn = "urn:ogf:network:domain=al2s.net.internet2.edu:node=sdn-sw.houh.net.internet2.edu:port=et-0/3/0.0";
		OESSEndpoint ep = Main.parseUrn(urn);
		assert("et-0/3/0.0".equals(ep.getIntface()) && "sdn-sw.houh.net.internet2.edu".equals(ep.getNode()));
    }
}
