package rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import architecture.Environment;
import architecture.Flow;
import architecture.Switch;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import tools.EntornoTools;
import tools.JsonManager;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
	Gson gson = new Gson();
    Environment entorno = new Environment();
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
        
        try {
        	EntornoTools.onosHost = "localhost";
        	EntornoTools.user = "onos";
        	EntornoTools.password = "rocks";
        	EntornoTools.endpoint = "http://" + EntornoTools.onosHost + ":8181/onos/v1";
			EntornoTools.getEnvironment();
			entorno = EntornoTools.entorno;
			String json = gson.toJson(entorno);
			//System.out.println(json);
			pruebaFlows();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private void pruebaFlows() {
    	Map<String,List<Flow>> map = new HashMap<String,List<Flow>>();
		
		for(Switch s : entorno.getMapSwitches().values()) {
			System.out.println(s.getId());
			List<Flow> listFlows = new ArrayList<Flow>();
			for(Flow flow : s.getMapFlows().values()) {
				listFlows.add(flow);
				System.out.println(flow.getId());
			}
			map.put(s.getId(), listFlows);
		}

		String json = gson.toJson(map);
		System.out.println("JSON: "+json);
    	
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
