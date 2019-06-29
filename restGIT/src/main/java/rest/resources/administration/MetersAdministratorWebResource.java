package rest.resources.administration;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

import architecture.Flow;
import architecture.FlowCriteria;
import architecture.FlowInstruction;
import architecture.Host;
import architecture.Meter;
import architecture.Switch;
import rest.gsonobjects.onosside.OnosResponse;
import rest.gsonobjects.userside.MeterClientRequest;
import rest.gsonobjects.userside.MeterClientRequestPort;
import tools.DatabaseTools;
import tools.EntornoTools;
import tools.HttpTools;
import tools.LogTools;

@Path("/administration/meters")
public class MetersAdministratorWebResource {
	private Gson gson;

	public MetersAdministratorWebResource() {
		LogTools.info("MetersAdministratorWebResource", "***ONOS SIMPLE REST API (OSRA) Service***");
		gson = new Gson();
	}

	/**
	 * Get switches in the SDN network
	 * @return Switches placed in SDN network
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
				LogTools.info("getAllMeters", "Geting meters");
				meters = EntornoTools.getAllMeters();
				LogTools.info("getAllMeters", meters.toArray().toString());
				json = gson.toJson(meters);
				LogTools.info("getAllMeters", "response to client: " + json);
			} catch (IOException e) {
				LogTools.error("getAllMeters", "Error while getting all meters");
				e.printStackTrace();
				resRest = Response.ok("{\"response\":\"No meters recieved from ONOS\"}", MediaType.APPLICATION_JSON_TYPE).build();
				return resRest;
			}

			resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;
		}
		else
			return Response.status(401).build();
	}

	/**
	 * 
	 * @param jsonIn
	 * @return
	 */
	@Path("{switchId}/{meterId}")
	@DELETE
	@Produces (MediaType.APPLICATION_JSON)	
	public Response deleteMeter(@PathParam("switchId") String switchId, @PathParam("meterId") String meterId, @HeaderParam("authorization") String authString) {
		LogTools.rest("DELETE", "deleteMeter", "Switch Name: " + switchId + " - MeterID: " + meterId);

		Response resRest;
		OnosResponse response;
		String url = "";
		if(DatabaseTools.isAdministrator(authString)) {
			url = EntornoTools.endpoint + "/meters/"+switchId+"/"+meterId;

			try {
				response = HttpTools.doDelete(new URL(url));
			} catch (MalformedURLException e) {
				resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
				return resRest;
			} catch (IOException e) {
				//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
				resRest = Response.ok("IO: "+e.getMessage(), MediaType.TEXT_PLAIN).build();
				//resRest = Response.serverError().build();
				return resRest;
			}


			resRest = Response.ok("{\"response\":\"succesful_"+ response.getMessage() +"\"}", MediaType.APPLICATION_JSON_TYPE).build();

			return resRest;
		}
		else
			return Response.status(401).build();
	}

	/**
	 * Create a meter for the given host
	 * @param jsonIn JSON retrieved from client
	 * @return
	 */
	@Path("{hostIp}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)	
	public Response setMeter(@PathParam("hostIp") String hostIp, String jsonIn, @HeaderParam("authorization") String authString) {
		LogTools.rest("POST", "setMeter", "Body:\n" + jsonIn);
		Response resRest = null;
		OnosResponse response = null;
		String url = "";
		String portAux = "";
		int meterIdAux = -1;
		String hostMeterAux = "";

		if(DatabaseTools.isAdministrator(authString)) {
			//DESCUBRIR ENTORNO
			MeterClientRequest meterReq = gson.fromJson(jsonIn, MeterClientRequest.class);
			//System.out.println("HOST: "+meterReq.getHost());

			if(hostIp.equals(meterReq.getHost())) {
				//GET HOST
				Host h = EntornoTools.getHostByIp(meterReq.getHost());

				//System.out.println("HOST: "+meterReq.getHost());
				//System.out.println("GET HOST: "+h.getId()+" "+h.getIpList().get(0).toString());


				//GET switches connected to host
				List<Switch> ingressSwitches = EntornoTools.getIngressSwitchesByHost(meterReq.getHost());

				//ADD METERS TO SWITCHES
				if(h != null){
					for(Switch ingress : ingressSwitches) {
						try {
							//System.out.println("Ingress sw "+ingress.getId()+" para "+ meterReq.getHost());
							response = EntornoTools.addMeter(ingress.getId(), meterReq.getRate(), meterReq.getBurst());
							//System.out.println("Meter añadido? respuesta de onos: "+onosResponse);

							// GET METER ID ALREADY INSTALLED
							List<Meter> meter = EntornoTools.getMeters(ingress.getId());
							int meterId = meter.size();
							//System.out.println("Meter ID: "+ meterId);

							//GET EGRESS PORTS FROM SWITCH
							List<String> outputSwitchPorts = EntornoTools.getOutputPorts(ingress.getId());

							//Install flows
							for(String port : outputSwitchPorts) {
								portAux = port;
								meterIdAux = meterId;
								hostMeterAux = meterReq.getHost();

								EntornoTools.addQosFlow(ingress.getId(), port, meterId, meterReq.getHost());

							}

						} catch (MalformedURLException e) {
							resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+response.getMessage()+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
							return resRest;
						} catch (IOException e) {
							resRest = Response.ok("IO: "+e.getMessage()+"\n"+response.getMessage()+
									"\n"+"\nHOST:"+h.getId()+
									"\ningress: "+ingress.getId()+
									"\nport: "+portAux+
									"\nmeter id: "+meterIdAux+
									"\nmeter Host from request: "+meterReq.getHost(), MediaType.TEXT_PLAIN).build();
							//resRest = Response.serverError().build();
							return resRest;
						}
						return resRest = Response.ok("{\"response\":\"succesful"+ response.getMessage() +"\"}", MediaType.APPLICATION_JSON_TYPE).build();

					}
				}
				else {
					return resRest = Response.ok("{\"response\":\"host not found"+ response.getMessage() +"\"}", MediaType.APPLICATION_JSON_TYPE).build();
				}
			}

			return resRest;
		}
		else
			return Response.status(401).build();
	}

	/**
	 * Create a meter for the given host
	 * @param jsonIn JSON retrieved from client
	 * @return
	 */
	@Path("{hostIp}/{hostPort}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)	
	public Response setMeter(@PathParam("hostIp") String hostIp, @PathParam("hostPort") String hostPort,  String jsonIn, @HeaderParam("authorization") String authString) {
		LogTools.rest("POST", "setMeter", "Body:\n" + jsonIn);
		Response resRest = null;
		OnosResponse response = null;
		String url = "";
		String portAux = "";
		int meterIdAux = -1;
		String hostMeterAux = "";

		if(DatabaseTools.isAdministrator(authString)) {
			//DESCUBRIR ENTORNO
			MeterClientRequestPort meterReq = gson.fromJson(jsonIn, MeterClientRequestPort.class);
			//System.out.println("HOST: "+meterReq.getHost());

			if(hostIp.equals(meterReq.getHost())) {
				//GET HOST
				Host h = EntornoTools.getHostByIp(meterReq.getHost());

				//System.out.println("HOST: "+meterReq.getHost());
				//System.out.println("GET HOST: "+h.getId()+" "+h.getIpList().get(0).toString());


				//GET switches connected to host
				List<Switch> ingressSwitches = EntornoTools.getIngressSwitchesByHost(meterReq.getHost());

				//ADD METERS TO SWITCHES
				if(h != null){
					for(Switch ingress : ingressSwitches) {
						try {
							//System.out.println("Ingress sw "+ingress.getId()+" para "+ meterReq.getHost());
							response = EntornoTools.addMeter(ingress.getId(), meterReq.getRate(), meterReq.getBurst());
							//System.out.println("Meter añadido? respuesta de onos: "+onosResponse);

							// GET METER ID ALREADY INSTALLED
							List<Meter> meter = EntornoTools.getMeters(ingress.getId());
							int meterId = meter.size();
							//System.out.println("Meter ID: "+ meterId);

							//GET EGRESS PORTS FROM SWITCH
							List<String> outputSwitchPorts = EntornoTools.getOutputPorts(ingress.getId());

							//Install flows
							for(String port : outputSwitchPorts) {
								portAux = port;
								meterIdAux = meterId;
								hostMeterAux = meterReq.getHost();

								EntornoTools.addQosFlowWithPort(ingress.getId(), port, meterId, meterReq.getHost(), meterReq.getPort(), meterReq.getPortType());

							}

						} catch (MalformedURLException e) {
							resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+response.getMessage()+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
							return resRest;
						} catch (IOException e) {
							resRest = Response.ok("IO: "+e.getMessage()+"\n"+response.getMessage()+
									"\n"+"\nHOST:"+h.getId()+
									"\ningress: "+ingress.getId()+
									"\nport: "+portAux+
									"\nmeter id: "+meterIdAux+
									"\nmeter Host from request: "+meterReq.getHost(), MediaType.TEXT_PLAIN).build();
							//resRest = Response.serverError().build();
							return resRest;
						}
						return resRest = Response.ok("{\"response\":\"succesful"+ response.getMessage() +"\"}", MediaType.APPLICATION_JSON_TYPE).build();

					}
				}
				else {
					return resRest = Response.ok("{\"response\":\"host not found"+ response.getMessage() +"\"}", MediaType.APPLICATION_JSON_TYPE).build();
				}
			}

			return resRest;
		}
		else
			return Response.status(401).build();
	}



	/**
	 * 
	 * @param jsonIn
	 * @return
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
									meterId = instruction.getInstructions().get("meterId");
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
