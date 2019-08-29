package rest.resources.users;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import architecture.Queue;
import rest.database.objects.QueueDBResponse;
import tools.DatabaseTools;
import tools.EntornoTools;
import tools.LogTools;

@Path("/users/queues")
public class QueuesUserWebResource {
	private Gson gson;

	public QueuesUserWebResource() {
		LogTools.info("QueuesUserWebResource", "***ONOS SIMPLE REST API (OSRA) Service***");
		gson = new Gson();
	}
	
	/**
	 * Get queues in the SDN network
	 * @return Switches placed in SDN network
	 */
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
	public Response getQueues(@HeaderParam("authorization") String authString) {
		List<Queue> queues = new ArrayList<Queue>();
		if(DatabaseTools.isAuthenticated(authString)) {
			List<QueueDBResponse> queuesDb = DatabaseTools.getQueues(authString);
			queues = EntornoTools.getQueues(queuesDb);
			
		}
		else 
			return Response.status(401).build();
		
		return Response.ok(gson.toJson(queues), MediaType.APPLICATION_JSON_TYPE).build();
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
