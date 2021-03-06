package rest.resources.administration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import architecture.Queue;
import rest.database.objects.QueueDBResponse;
import rest.gsonobjects.onosside.OnosResponse;
import rest.gsonobjects.onosside.QueueOnosRequest;
import rest.gsonobjects.userside.QueueClientRequest;
import tools.DatabaseTools;
import tools.EntornoTools;
import tools.LogTools;

/**
 * Queues web resource
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
@Path("/administration/queues")
public class QueueAdministratorWebResource {
	private Gson gson;

	public QueueAdministratorWebResource() {
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
		String jsonOut = "";
		if(DatabaseTools.isAdministrator(authString)) {
			try {
				EntornoTools.getEnvironment();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			List<QueueDBResponse> queuesDb = DatabaseTools.getQueues(authString);
			queues = EntornoTools.getQueues(queuesDb);
			jsonOut = gson.toJson(queues);
		}
		else 
			return Response.status(401).build();
		
		return Response.ok(jsonOut, MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	/**
	 * Add queue
	 * @param authString authorization http user
	 * @param jsonIn json request
	 * @return Response
	 */
	@POST
	@Consumes (MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)
	public Response addQueue(@HeaderParam("authorization") String authString, String jsonIn) {
		Gson gson = new Gson();
		QueueOnosRequest queueOnosRequest = null;
		String jsonOut = "";
		OnosResponse onosResponse = new OnosResponse();
		if(DatabaseTools.isAdministrator(authString)) {
			try {
				EntornoTools.getEnvironment();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			QueueClientRequest queueReq = gson.fromJson(jsonIn, QueueClientRequest.class);
			
			/// QUEUE ADD
			int nQueues = DatabaseTools.getAllQueuesIds().size();
			try {
				if(nQueues > 0 && (queueReq != null))
					onosResponse = EntornoTools.addQueueConnection(authString, queueReq);
				else {
					EntornoTools.addQueuesDefault();
					if(queueReq != null)
						onosResponse = EntornoTools.addQueueConnection(authString, queueReq);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.status(400).entity("Queue flow adding fail").build();
			}
			
		}
		else 
			return Response.status(401).build();
		return Response.ok(gson.toJson(onosResponse), MediaType.APPLICATION_JSON).build();
	}
	
	/**
	 * Delete queue
	 * @param authString authorization http user
	 * @param queueId queue id
	 * @return Response
	 */
	@Path("{queueId}")
	@DELETE
	public Response deleteQueue(@HeaderParam("authorization") String authString,
			@PathParam("queueId") String queueId) {
		try {
			EntornoTools.getEnvironment();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(DatabaseTools.isAdministrator(authString)) {
			try {
				return EntornoTools.deleteQueue(authString, queueId);
			} catch (IOException | ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				return Response.status(Response.Status.CONFLICT).build();
			}
		}
		else 
			return Response.status(401).build();
	}

}
