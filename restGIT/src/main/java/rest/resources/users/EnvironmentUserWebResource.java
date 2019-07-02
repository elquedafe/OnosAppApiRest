package rest.resources.users;


import java.io.IOException;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import sun.misc.BASE64Decoder;

import com.google.gson.Gson;

import tools.DatabaseTools;
import tools.EntornoTools;
import tools.LogTools;

@Path("/users/environment")
public class EnvironmentUserWebResource {
	private Gson gson;

	public EnvironmentUserWebResource() {
		LogTools.info("EnvironmentUserWebResource", "***ONOS SIMPLE REST API (OSRA) Service***");
		gson = new Gson();
	}

	/**
	 * Get the environment state from the controller
	 * @return Environment
	 */
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
	public Response getEnvironment(@HeaderParam("authorization") String authString) {
		LogTools.rest("GET", "getEnvironment");
		Response resRest = null;
		if(DatabaseTools.isAuthenticated(authString)) {
			try {
				EntornoTools.getEnvironmentByUser(authString);
				//this.entorno = EntornoTools.entorno;
			} catch (IOException e) {
				e.printStackTrace();
			}
			String json = gson.toJson(EntornoTools.entorno);

			if(json.length() < 900) LogTools.info("getEnvironment", "response to client: " + json);
			else LogTools.info("getEnvironment", "response to client: " +  json.substring(0,  900) + "...");

			resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
		}
		else
			return Response.status(401).build();
		return resRest;
	}
}
