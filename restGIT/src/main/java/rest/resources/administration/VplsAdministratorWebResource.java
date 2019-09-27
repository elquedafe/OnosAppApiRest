package rest.resources.administration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import architecture.Flow;
import architecture.Host;
import architecture.Switch;
import architecture.Vpls;
import rest.database.objects.FlowDBResponse;
import rest.database.objects.MeterDBResponse;
import rest.database.objects.VplsDBResponse;
import rest.gsonobjects.onosside.OnosResponse;
import rest.gsonobjects.userside.MeterClientRequestPort;
import rest.gsonobjects.userside.QueueClientRequest;
import rest.gsonobjects.userside.VplsClientRequest;
import tools.DatabaseTools;
import tools.EntornoTools;
import tools.HttpTools;
import tools.LogTools;

/**
 * Vpls web resource.
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
@Path("/administration/vpls")
public class VplsAdministratorWebResource {
	private Gson gson;

	public VplsAdministratorWebResource() {
		LogTools.info("VplsAdministratorWebResource", "***ONOS SIMPLE REST API (OSRA) Service***");
		gson = new Gson();
	}

	/**
	 * Get vpls
	 * @param authString authorization http user
	 * @return Response
	 */
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
	public Response getVpls(@HeaderParam("authorization") String authString) {
		LogTools.rest("GET", "getVpls");
		Response resRest;
		List<Vpls> vplss = null;
		if(DatabaseTools.isAdministrator(authString)) {
			try {
				LogTools.info("getVpls", "Discovering environment");
				EntornoTools.getEnvironment();
			} catch (IOException e) {
				e.printStackTrace();
			}
			vplss = EntornoTools.getVplsList();
			String json = gson.toJson(vplss);
			LogTools.info("getVpls", "response to client: " + json);
			resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;

		}
		else
			return Response.status(401).build();
	}

	

	/**
	 * Delete vpls
	 * @param vplsName vpls name
	 * @param authString authorization http user
	 * @return
	 */
	@Path("{vplsName}")
	@DELETE
	@Produces (MediaType.APPLICATION_JSON)	
	public Response deleteVpls(@PathParam("vplsName") String vplsName, @HeaderParam("authorization") String authString) {
		LogTools.rest("DELETE", "deteleVpls", "VPLS Name: " + vplsName);

		Response resRest;
		OnosResponse response = null;
		String url = "";
		if(DatabaseTools.isAdministrator(authString)) {

			try {
				List<MeterDBResponse> dbMeters = DatabaseTools.getMetersByVpls(vplsName, authString);
				Map<String, FlowDBResponse> dbFlowsIntent = DatabaseTools.getFlowsByVplsNoMeter(vplsName, authString);
				//ONOS DELETE VPLS
				int vplsSize = DatabaseTools.getVplsSize();
				if(vplsSize == 1) {
					HttpTools.doDelete(new URL(EntornoTools.endpointNetConf));
					response = new OnosResponse("Deleted VPLS from ONOS", 204);
				}
				else
					response = EntornoTools.deleteVpls(vplsName, authString);


				//DELETE INTENT FLOWS
				for(FlowDBResponse dbFlowIntent : dbFlowsIntent.values()) {
					DatabaseTools.deleteFlow(dbFlowIntent.getIdFlow(), authString);
					try {
						HttpTools.doDelete(new URL(EntornoTools.endpoint+"/flows/"+dbFlowIntent.getIdSwitch()+"/"+dbFlowIntent.getIdFlow()));
					}catch(IOException e) {
						e.printStackTrace();
					}
				}

				//DELETE METERS AND FLOWS ASSOCIATED TO METER
				for(MeterDBResponse dbMeter : dbMeters) {
					EntornoTools.deleteMeterWithFlows(dbMeter.getIdSwitch(), dbMeter.getIdMeter(), authString);
				}

				//DELETE FROM DDBB
				DatabaseTools.deleteVpls(vplsName, authString);
			} catch (MalformedURLException e) {
				resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
				return resRest;
			} catch (IOException e) {
				return Response.status(400).entity("IO: "+e.getMessage()).build();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return Response.status(400).entity("ClassNotFoundException ERROR").build();
			} catch (SQLException e) {
				e.printStackTrace();
				return Response.status(400).entity("SQL ERROR").build();
			}


			resRest = Response.ok("{\"response\":\"succesful"+ response.getMessage() +"\"}", MediaType.APPLICATION_JSON_TYPE).build();

			return resRest;
		}
		else
			return Response.status(401).build();
	}

	/**
	 * Create VPLS
	 * @param vplsName vpls name
	 * @param jsonIn json vpls request
	 * @param authString authorization http user
	 * @return Response
	 */
	@Path("{vplsName}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)	
	public Response setVpls(@PathParam("vplsName") String vplsName, String jsonIn, @HeaderParam("authorization") String authString) {
		LogTools.rest("POST", "setVpls", "VPLS Name: " + vplsName + "Body:\n" + jsonIn);
		Response resRest;
		String jsonOut = "";
		String url = "";
		if(DatabaseTools.isAdministrator(authString)) {
			url = EntornoTools.endpointNetConf;
			try {
				LogTools.info("setVpls", "Discovering environment");
				EntornoTools.getEnvironment();

				//GET OLD FLOW STATE
				Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
				for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet()){
					for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet())
						if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
							oldFlowsState.put(flow.getKey(), flow.getValue());
				}

				VplsClientRequest vplsReq = gson.fromJson(jsonIn, VplsClientRequest.class);

				List<Vpls> vplsBefore = EntornoTools.getVplsState();

				if(vplsReq.getVplsName().equals(vplsName))
					jsonOut = EntornoTools.addVplsJson(vplsReq.getVplsName(), vplsReq.getHosts());

				HttpTools.doJSONPost(new URL(url), jsonOut);

				List<Vpls> vplsAfter = EntornoTools.getVplsState();

				List<Vpls> vplsNews = EntornoTools.compareVpls(vplsBefore, vplsAfter);

				//ADD new vpls to DDBB
				for(Vpls v : vplsNews) {
					try {
						DatabaseTools.addVplsByUser(v.getName(), authString);
					} catch (ClassNotFoundException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				//GET NEW FLOWS STATE
				EntornoTools.getEnvironment();
				Map<String, Flow> newFlowsState = new HashMap<String, Flow>();
				for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet())
					for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet()) 
						if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
							newFlowsState.put(flow.getKey(), flow.getValue());

				List<Flow> flowsNews;
				flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);

				// ADD flows of intents to DDBB
				if(flowsNews.size()>0) {
					for(Flow flow : flowsNews) {
						try {
							//							System.out.format("Añadiend flujo a la bbdd: %s %s %s", flow.getId(), flow.getDeviceId(), flow.getFlowSelector().getListFlowCriteria().get(3));
							System.out.format("Añadiendo flujo a la bbdd: %s %s", flow.getId(), flow.getDeviceId());
							DatabaseTools.addFlow(flow, authString, null, vplsName, null);
						} catch (ClassNotFoundException | SQLException e) {
							e.printStackTrace();
							//TODO: Delete flow from onos and send error to client

						}
					}
				}


				// ADD METERS OR QUEUES FOR VPLS AND ITS FLOWS
				if((vplsReq.getMaxRate() != -1) && (vplsReq.getBurst() != -1) && (vplsReq.getMinRate()==-1)) {
					MeterClientRequestPort meterReq;
					for(String srcHost : vplsReq.getHosts()) {
						for(String dstHost : vplsReq.getHosts()) {
							if(!srcHost.equals(dstHost)) {
								meterReq = new MeterClientRequestPort();
								meterReq.setSrcHost(srcHost);
								meterReq.setDstHost(dstHost);
								meterReq.setRate(vplsReq.getMaxRate());
								meterReq.setBurst(vplsReq.getBurst());
								EntornoTools.addMeterAndFlowWithVpls(vplsName, srcHost, dstHost, authString, meterReq);
							}
						}
					}
				}
				else if((vplsReq.getMaxRate() != -1) && (vplsReq.getBurst() != -1) && (vplsReq.getMinRate()!=-1)){
					QueueClientRequest queueReq;
					for(String srcHost : vplsReq.getHosts()) {
						for(String dstHost : vplsReq.getHosts()) {
							if(!srcHost.equals(dstHost)) {
								queueReq = new QueueClientRequest();
								queueReq.setSrcHost(srcHost);
								queueReq.setDstHost(dstHost);
								queueReq.setMinRate(vplsReq.getMinRate());
								queueReq.setMaxRate(vplsReq.getMaxRate());
								queueReq.setBurst(vplsReq.getBurst());
								
								EntornoTools.addQueueConnection(authString, queueReq, flowsNews);
							}
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
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.status(400).entity("Queue flow adding fail").build();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resRest = Response.ok("{\"response\":\"succesful\"}", MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;
		}
		else
			return Response.status(401).build();
	}
}
