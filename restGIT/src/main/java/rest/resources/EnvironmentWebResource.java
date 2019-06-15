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

@Path("/rest/environment")
public class EnvironmentWebResource {
	private Gson gson;
	
	public EnvironmentWebResource() {
		LogTools.info("EnvironmentWebResource", "***ONOS SIMPLE REST API (OSRA) Service***");
		gson = new Gson();
	}
	
	/**
	 * Get the environment state from the controller
	 * @return Environment
	 */
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
	public Response getEnvironment() {
		LogTools.rest("GET", "getEnvironment");
		Response resRest;
		try {
			EntornoTools.getEnvironment();
			//this.entorno = EntornoTools.entorno;
		} catch (IOException e) {
			e.printStackTrace();
		}
		String json = gson.toJson(EntornoTools.entorno);
		
		if(json.length() < 900) LogTools.info("getEnvironment", "response to client: " + json);
		else LogTools.info("getEnvironment", "response to client: " +  json.substring(0,  900) + "...");
		
		resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
		return resRest;
	}

}
