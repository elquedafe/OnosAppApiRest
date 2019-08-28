package rest.resources.users;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import tools.DatabaseTools;
import tools.LogTools;

@Path("/users/queues")
public class QueuesUserWebResource {
	private Gson gson;

	public QueuesUserWebResource() {
		LogTools.info("QueuesUserWebResource", "***ONOS SIMPLE REST API (OSRA) Service***");
		gson = new Gson();
	}
	
	/**
	 * Get switches in the SDN network
	 * @return Switches placed in SDN network
	 */
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
	public Response getQueues(@HeaderParam("authorization") String authString) {
		if(DatabaseTools.isAuthenticated(authString)) {
			
		}
		else 
			return Response.status(401).build();
		return null;
	}
	
	/**
	 * Get switches in the SDN network
	 * @return Switches placed in SDN network
	 */
	@DELETE
	@Consumes (MediaType.APPLICATION_JSON)	
	public Response deleteQueue(@HeaderParam("authorization") String authString) {
		if(DatabaseTools.isAuthenticated(authString)) {
			
		}
		else 
			return Response.status(401).build();
		return null;
	}

}
