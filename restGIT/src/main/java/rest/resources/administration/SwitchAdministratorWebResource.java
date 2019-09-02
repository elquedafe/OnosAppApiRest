package rest.resources.administration;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import tools.DatabaseTools;
import tools.EntornoTools;
import tools.LogTools;

@Path("/administration/switches")
public class SwitchAdministratorWebResource {
	private Gson gson;

	public SwitchAdministratorWebResource() {
		LogTools.info("SwitchAdministratorWebResource", "***ONOS SIMPLE REST API (OSRA) Service***");
		gson = new Gson();
	}

	/**
	 * Get switches in the SDN network
	 * @return Switches placed in SDN network
	 */
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
	public Response getSwitches(@HeaderParam("authorization") String authString) {
		LogTools.rest("GET", "getSwitches");
		Response resRest;
		if(DatabaseTools.isAdministrator(authString)) {
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
	else
		return Response.status(401).build();
	}
}

