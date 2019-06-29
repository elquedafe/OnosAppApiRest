package rest.resources.users;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
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

import architecture.Environment;
import architecture.Flow;
import architecture.Switch;
import rest.database.objects.FlowDBResponse;
import rest.gsonobjects.onosside.FlowOnosRequest;
import rest.gsonobjects.onosside.OnosResponse;
import rest.gsonobjects.userside.FlowClientRequest;
import tools.DatabaseTools;
import tools.EntornoTools;
import tools.HttpTools;
import tools.LogTools;

@Path("/users/flows")
public class FlowsUserWebResource {
	private Gson gson;

	public FlowsUserWebResource() {
		LogTools.info("FlowsUserWebResource", "***ONOS SIMPLE REST API (OSRA) Service***");
		gson = new Gson();
	}

	/**
	 * Get all flows from all the switches of the network
	 * @return All flows in the network
	 */
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
	public Response getFlows(@HeaderParam("authorization") String authString) {
		LogTools.rest("GET", "getFlows");
		Response resRest;

		//Check if user is authorized
		if(DatabaseTools.isAuthenticated(authString)) {
			try {
				LogTools.info("getFlows", "Discovering environment");
				EntornoTools.getEnvironment();
			} catch (IOException e) {
				e.printStackTrace();
			}

			//GET FLOWS STORED IN DB to know user flows ID.
			Map<String, FlowDBResponse> flowsDB = DatabaseTools.getFlowsByUser(authString);

			Map<String,List<Flow>> map = new HashMap<String,List<Flow>>();
			for(Switch s : EntornoTools.entorno.getMapSwitches().values()) {
				List<Flow> listFlows = new ArrayList<Flow>();
				for(Flow flow : s.getMapFlows().values()) {
					// If flow is associated to the user adds to the list
					if(flowsDB.containsKey((flow.getId()))) {
						listFlows.add(flow);
						//System.out.println(flow.getId());
					}
				}
				map.put(s.getId(), listFlows);
			}
			String json = gson.toJson(map);
			LogTools.info("getFlows", "response to client: " + json);
			resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;
		}
		else
			return Response.status(401).build();
	}

	/**
	 * Create a flow for the given switch
	 * @param switchId Switch ID where flow is created
	 * @param jsonIn JSON retrieved from client
	 * @return
	 */
	@Path("{switchId}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)	
	public Response setFlow(@PathParam("switchId") String switchId, String jsonIn, @HeaderParam("authorization") String authString) {
		LogTools.rest("POST", "setFlow", "To " + switchId + " switch. JSON:\n" + jsonIn);
		Response resRest;
		String messageToClient = "";
		if(DatabaseTools.isAuthenticated(authString)) {
			FlowClientRequest flowReq = gson.fromJson(jsonIn, FlowClientRequest.class);
			FlowOnosRequest flowOnos = new FlowOnosRequest(flowReq.getPriority(), 
					flowReq.getTimeout(), flowReq.isPermanent(), flowReq.getSwitchId());
			LinkedList<LinkedHashMap<String,String>> auxList = new LinkedList<LinkedHashMap<String,String>>();
			LinkedHashMap<String, String> auxMap = new LinkedHashMap<String,String>();
			Map<String, LinkedList<LinkedHashMap<String, String>>> treatement = new LinkedHashMap<String, LinkedList<LinkedHashMap<String,String>>>();
			Map<String, LinkedList<LinkedHashMap<String,String>>> selector = new LinkedHashMap<String, LinkedList<LinkedHashMap<String,String>>>();

			//TREATMENT
			auxMap.put("type", "OUTPUT");
			auxMap.put("port", flowReq.getDstPort());
			auxList.add(auxMap);
			treatement.put("instructions", auxList);
			flowOnos.setTreatment(treatement);

			//SELECTOR
			//criteria 1
			auxMap = new LinkedHashMap<String, String>();
			auxList = new LinkedList<LinkedHashMap<String,String>>();
			auxMap.put("type", "IN_PORT");
			auxMap.put("port", flowReq.getSrcPort());
			auxList.add(auxMap);
			//criteria 2
			auxMap = new LinkedHashMap<String, String>();
			auxMap.put("type", "ETH_DST");
			auxMap.put("mac", flowReq.getDstHost());
			auxList.add(auxMap);
			//criteria 3
			auxMap = new LinkedHashMap<String, String>();
			auxMap.put("type", "ETH_SRC");
			auxMap.put("mac", flowReq.getSrcHost());
			auxList.add(auxMap);

			selector.put("criteria", auxList);
			flowOnos.setSelector(selector);

			//Generate JSON to ONOS
			String jsonOut = gson.toJson(flowOnos);
			//System.out.println("JSON TO ONOS: \n"+jsonOut);
			/*String jsonOut = "{" +
		        "\"priority\": "+ flowReq.getPriority() +"," +
		        "\"timeout\": " + flowReq.getTimeout() + "," +
		        "\"isPermanent\": "+ flowReq.isPermanent() + "," +
		        "\"deviceId\": \""+ switchId +"\"," +
		        "\"tableId\": 0," +
		        "\"groupId\": 0," +
		        "\"appId\": \"org.onosproject.fwd\"," +
		        "\"treatment\": {" +
		        "\"instructions\": [" +
		        "{" +
		        "\"type\": \"OUTPUT\"," +
		        "\"port\": \""+ flowReq.getDstPort() +"\"" +
		        "}" +
		        "]" +
		        "}," +
		        "\"selector\": {" +
		        "\"criteria\": [" +
		        "{" +
		        "\"type\": \"IN_PORT\"," +
		        "\"port\": \""+ flowReq.getSrcPort() +"\"" +
		        "}," +
		        "{" +
		        "\"type\": \"ETH_DST\"," +
		        "\"mac\": \""+ flowReq.getDstHost() +"\"" +
		        "}," +
		        "{" +
		        "\"type\": \"ETH_SRC\"," +
		        "\"mac\": \""+ flowReq.getSrcHost() +"\"" +
		        "}" +
		        "]" +
		        "}" +
		        "}";*/
			String url = EntornoTools.endpoint+"/flows/"+flowReq.getSwitchId();
			try {
				EntornoTools.getEnvironment();
				//				Environment oldEnv = EntornoTools.entorno;
				Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
				for(Map.Entry<String, Flow> flow: EntornoTools.entorno.getMapSwitches().get(flowReq.getSwitchId()).getFlows().entrySet()){
					oldFlowsState.put(flow.getKey(), flow.getValue());
				}
				HttpTools.doJSONPost(new URL(url), jsonOut);

				//Wait for new state
//				try {
//					Thread.sleep(200);
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}

				EntornoTools.getEnvironment();
				Map<String, Flow>  newFlowsState = new HashMap<String, Flow>();
				for(Map.Entry<String, Flow> flow: EntornoTools.entorno.getMapSwitches().get(flowReq.getSwitchId()).getFlows().entrySet()){
					newFlowsState.put(flow.getKey(), flow.getValue());
				}
				List<Flow> flowsNews;
				System.out.println(".");
				flowsNews = EntornoTools.compareFlows(flowReq.getSwitchId(), oldFlowsState, newFlowsState);
				if(flowsNews.size()>0) {
					for(Flow flow : flowsNews) {
						try {
							DatabaseTools.addFlowByUserId(flow, authString);
						} catch (ClassNotFoundException | SQLException e) {
							e.printStackTrace();
							//TODO: Delete flow from onos and send error to client

						}
					}
				}
			} catch (MalformedURLException e) {
				resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+jsonOut+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
				return resRest;
			} catch (IOException e) {
				//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
				resRest = Response.ok("IO: "+e.getMessage()+"\n"+jsonOut+"\n", MediaType.TEXT_PLAIN).build();
				resRest = Response.serverError().build();
				return resRest;
			}
			resRest = Response.ok("{\"response\":\"succesful\"}", MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;
		}
		else
			return Response.status(401).build();
	}


	/**
	 * Delete flow from switches of the network
	 * @return All flows in the network
	 */
	@Path("{switchId}/{flowId}")
	@DELETE
	@Produces (MediaType.APPLICATION_JSON)	
	public Response deleteFlow(@PathParam("switchId") String switchId, @PathParam("flowId") String flowId, @HeaderParam("authorization") String authString) {
		LogTools.rest("DELETE", "deleteFlow");
		Response resRest = null;
		String json = "";
		OnosResponse response;
		if(DatabaseTools.isAuthenticated(authString)) {
			try {
				response = HttpTools.doDelete(new URL(EntornoTools.endpoint+"/flows/"+switchId+"/"+flowId));
			} 
			catch (IOException e) {
				e.printStackTrace();
				return Response.ok(gson.toJson(new OnosResponse(e.getMessage(),404)), MediaType.APPLICATION_JSON_TYPE).build();
			}
			resRest = Response.ok(gson.toJson(response), MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;
		}
		else
			return Response.status(401).build();
	}

}
