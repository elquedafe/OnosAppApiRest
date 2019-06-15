package rest.resources;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import tools.EntornoTools;
import tools.LogTools;

@Path("/rest/switches")
public class SwitchWebResource {
	private Gson gson;
	
	public SwitchWebResource() {
		LogTools.info("SwitchWebResource", "***ONOS SIMPLE REST API (OSRA) Service***");
		gson = new Gson();
	}
	
	/**
	 * Get switches in the SDN network
	 * @return Switches placed in SDN network
	 */
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
	public Response getSwitches() {
		LogTools.rest("GET", "getSwitches");
		Response resRest;
		try {
			LogTools.info("getSwitches", "Discovering environment");
			EntornoTools.getEnvironment();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String json = gson.toJson(EntornoTools.entorno.getMapSwitches().values());
		LogTools.info("getSwitches", "response to client: " + json);
		resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
		return resRest;
	}
	

}
