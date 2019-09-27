package rest.resources.administration;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
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
import architecture.FlowCriteria;
import architecture.FlowInstruction;
import architecture.Host;
import architecture.Meter;
import architecture.Switch;
import rest.gsonobjects.onosside.OnosResponse;
import rest.gsonobjects.userside.MeterClientRequest;
import rest.gsonobjects.userside.MeterClientRequestPort;
import rest.gsonobjects.userside.MeterClientRequestPortWithSwitch;
import tools.DatabaseTools;
import tools.EntornoTools;
import tools.HttpTools;
import tools.LogTools;

/**
 * Meter web resource.
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
@Path("/administration/meters")
public class MetersAdministratorWebResource {
	private Gson gson;

	public MetersAdministratorWebResource() {
		LogTools.info("MetersAdministratorWebResource", "***ONOS SIMPLE REST API (OSRA) Service***");
		gson = new Gson();
	}

	/**
	 * Get all meters
	 * @param authString auth string
	 * @return
	 */
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
	public Response getAllMeters(@HeaderParam("authorization") String authString) {
		LogTools.rest("GET", "getAllMeters");
		Response resRest;
		List<Meter> meters = null;
		String json = "";
		String onosResponse = "";
		if(DatabaseTools.isAdministrator(authString)) {
			try {
				meters = EntornoTools.getAllMeters();
				json = gson.toJson(meters);
			} catch (IOException e) {
				LogTools.error("getAllMeters", "Error while getting all meters");
				e.printStackTrace();
				resRest = Response.ok("No meters recieved from ONOS", MediaType.TEXT_PLAIN).build();
				return resRest;
			}

			resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;
		}
		else
			return Response.status(401).build();
	}

	/**
	 * Delete meter
	 * @param switchId switch id
	 * @param meterId meter id
	 * @param authString authorization http string
	 * @return
	 */
	@Path("{switchId}/{meterId}")
	@DELETE
	@Produces (MediaType.APPLICATION_JSON)	
	public Response deleteMeter(@PathParam("switchId") String switchId, @PathParam("meterId") String meterId, @HeaderParam("authorization") String authString) {
		LogTools.rest("DELETE", "deleteMeter", "Switch Name: " + switchId + " - MeterID: " + meterId);
		Response resRest;
		if(DatabaseTools.isAdministrator(authString)) {

			resRest = EntornoTools.deleteMeterWithFlows(switchId, meterId, authString);
			return resRest;
		}
		else
			return Response.status(401).build();
	}

	/**
	 * Create a meter for the given switch ID
	 * @param switchId
	 * @param jsonIn
	 * @param authString authorization http string
	 * @return
	 */
	@Path("{switchId}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)	
	public Response setMeter(@PathParam("switchId") String switchId, String jsonIn, @HeaderParam("authorization") String authString) {
		LogTools.rest("POST", "setMeterSw", "Body:\n" + jsonIn);
		Response resRest = null;
		OnosResponse response = null;
		String url = "";
		String portAux = "";

		if(DatabaseTools.isAuthenticated(authString)) {
			//DESCUBRIR ENTORNO
			try {
				EntornoTools.getEnvironment();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			System.out.println("JSON REQUEST FROM CLIENT: "+jsonIn);
			
			//Get user meter request
			MeterClientRequestPortWithSwitch meterReq = gson.fromJson(jsonIn, MeterClientRequestPortWithSwitch.class);

			System.out.println("METER REQUEST FROM CLIENT: "+meterReq.toString());
						
			Switch ingress = EntornoTools.entorno.getMapSwitches().get(switchId);

			//ADD METERS TO SWITCH
			try {
				// Get meters before
				List<Meter> oldMetersState = EntornoTools.getMeters(ingress.getId());

				//Add meter to onos
				response = EntornoTools.addMeter(ingress.getId(), meterReq.getRate(), meterReq.getBurst());

				// Get meter after
				List<Meter> newMetersState = EntornoTools.getMeters(ingress.getId());

				//Compare old and new
				List<Meter> metersToAdd = EntornoTools.compareMeters(oldMetersState, newMetersState);

				System.out.println("meters to add to ddbb = "+metersToAdd.size());
				//Add meter to DDBB
				for(Meter meter : metersToAdd) {
					try {
						DatabaseTools.addMeter(meter, authString, null);
					} catch (ClassNotFoundException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("Fin peticiones a BBDD para meters");
				//GET EGRESS PORTS FROM SWITCH
				String outputSwitchPort = null;
				try {
					outputSwitchPort = EntornoTools.getOutputPortFromSwitches(meterReq.getIngress(), meterReq.getEgress());
				}catch(Exception e ) {
					System.out.println("puerto muerto");
					e.printStackTrace();
				}
				System.out.println("Puerto de salida del switch del egress: "+ outputSwitchPort);

				//Install flows for each new meter
				if(outputSwitchPort != null && !outputSwitchPort.isEmpty()) {
					LogTools.info("setMeterSw", ("No vacio ni null outputSwitchPort y con valor: "+outputSwitchPort));
					System.out.println("num meters to add (otra vez) = "+metersToAdd.size());
					for(Meter meter : metersToAdd) {
						if(ingress.getId().equals(meter.getDeviceId())){
							System.out.println("Ingress = deviceId --> voy a crear flujo");
							System.out.println("Obteniendo entorno");
							//GET OLD STATE
							EntornoTools.getEnvironment();
							Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
							for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet()){
								for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet())
									if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
										oldFlowsState.put(flow.getKey(), flow.getValue());
							}
							System.out.println("Entorno obtenido");
							System.out.format("Voy a meter un flujos qos segun socket: %s %s %s %s %s %s %s %s %s", meterReq.getIpVersion(), ingress.getId(), outputSwitchPort, meter.getId(), meterReq.getSrcHost(), meterReq.getSrcPort(), meterReq.getDstHost(), meterReq.getDstPort(), meterReq.getPortType());
							// CREATE FLOW
							EntornoTools.addQosFlowWithPort(meterReq.getIpVersion(), ingress.getId(), outputSwitchPort, meter.getId(), meterReq.getSrcHost(), meterReq.getSrcPort(), meterReq.getDstHost(), meterReq.getDstPort(), meterReq.getPortType());
							//				}
							System.out.println("Fin instalacion flujo");

							//GET NEW STATE
							System.out.println("Obtengo entorno");
							EntornoTools.getEnvironment();
							Map<String, Flow> newFlowsState = new HashMap<String, Flow>();
							for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet())
								for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet()) 
									if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
										newFlowsState.put(flow.getKey(), flow.getValue());


							System.out.println(".");

							// GET FLOWS CHANGED
							List<Flow> flowsNews;
							flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);
							System.out.println("nº flujos para la ddbb = "+ flowsNews.size());
							// ADD flows to DDBB
							if(flowsNews.size()>0) {
								for(Flow flow : flowsNews) {
									try {
										DatabaseTools.addFlow(flow, authString, null, null, null);
									} catch (ClassNotFoundException | SQLException e) {
										e.printStackTrace();
										//TODO: Delete flow from onos and send error to client

									}
								}
							}
							System.out.println("Fin metodo set meter");

						}
					}
				}
				else {
					System.out.println("out port en main es null");
				}

			} catch (MalformedURLException e) {
				System.out.println("roto url main");
				e.printStackTrace();
				resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+response.getMessage()+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
				return resRest;
			} catch (IOException e) {
				System.out.println("io excepton main");
				e.printStackTrace();
				System.out.println("roto url");
				resRest = Response.ok("IO: "+e.getMessage()+"\n"+response.getMessage()+
						"\n"+"\nHOST:"+meterReq.getSrcHost()+
						"\ningress: "+ingress.getId()+
						"\nport: "+portAux+
						"\nmeter Host from request: "+meterReq.getSrcHost(), MediaType.TEXT_PLAIN).build();
				//resRest = Response.serverError().build();
				return resRest;
			}
			return resRest = Response.ok("{\"response\":\"succesful"+ response.getMessage() +"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		}
		else
			return Response.status(401).build();
	}
	
	/**
	 * Create a meter in ingress switch of srcHost
	 * @param srcHost source ip
	 * @param dstHost destination ip
	 * @param jsonIn json request
	 * @param authString authorization http string
	 * @return Response
	 */
	@Path("{srcHost}/{dstHost}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)	
	public Response setMeter(@PathParam("srcHost") String srcHost, @PathParam("dstHost") String dstHost,  String jsonIn, @HeaderParam("authorization") String authString) {
		LogTools.rest("POST", "setMeter", "Body:\n" + jsonIn);
		Response resRest = null;

		if(DatabaseTools.isAdministrator(authString)) {
			//DESCUBRIR ENTORNO
			try {
				EntornoTools.getEnvironment();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			//Get user meter request
			MeterClientRequestPort meterReq = gson.fromJson(jsonIn, MeterClientRequestPort.class);
			System.out.println("MeterClientRequestPort object: "+ meterReq.toString());

			resRest = EntornoTools.addMeterAndFlow(srcHost, dstHost, authString, meterReq);



			return resRest;
		}
		else
			return Response.status(401).build();
	}



	/**
	 * Delete meter socket
	 * @param hostIp host ip
	 * @param hostPort host port
	 * @param authString authorization http string
	 * @param jsonIn json request
	 * @return Response
	 */
	@Path("{hostIp}/port/{hostPort}")
	@DELETE
	@Produces (MediaType.APPLICATION_JSON)	
	public Response deleteMeterBySocket(@PathParam("hostIp") String hostIp, @PathParam("hostPort") String hostPort, @HeaderParam("authorization") String authString) {
		Response resRest = null;
		OnosResponse onosResponse = null;
		boolean matchType = false;
		boolean matchPort = false;
		boolean matchIp = false;
		String meterId = "";
		if(DatabaseTools.isAdministrator(authString)) {
			try {
				EntornoTools.getEnvironment();
				List<Switch> ingress = EntornoTools.getIngressSwitchesByHost(hostIp);
				for(Switch s : ingress) {
					for(Flow f : s.getMapFlows().values()) {
						matchType = false;
						matchPort = false;
						matchIp = false;
						meterId = "";
						for(FlowCriteria criteria : f.getFlowSelector().getListFlowCriteria()) {
							if(criteria.getType().equals("IPV4_SRC") && criteria.getCriteria().getValue().equals(hostIp+"/32"))
								matchIp = true;
							if(criteria.getType().equals("TCP_SRC") || criteria.getType().equals("UDP_SRC"))
								matchType = true;
							if(criteria.getCriteria().getValue().equals(String.valueOf(Double.parseDouble(hostPort))) )
								matchPort = true;

						}
						if(matchIp && matchType && matchPort) {
							onosResponse = HttpTools.doDelete(new URL(EntornoTools.endpoint+"/flows/"+s.getId()+"/"+f.getId()));
							for(FlowInstruction instruction : f.getFlowTreatment().getListInstructions()) {
								if(instruction.getInstructions().containsKey("meterId")) {
									meterId = (String)instruction.getInstructions().get("meterId");
									HttpTools.doDelete(new URL(EntornoTools.endpoint+"/meters/"+s.getId()+"/"+meterId));
								}
							}
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.ok(new OnosResponse("Not deleted", 400), MediaType.APPLICATION_JSON_TYPE).build();
			}

			resRest = Response.ok(gson.toJson(onosResponse), MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;
		}
		else
			return Response.status(401).build();
	}
}

