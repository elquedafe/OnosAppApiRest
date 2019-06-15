package rest.resources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import architecture.Host;
import architecture.Meter;
import architecture.Switch;
import rest.gsonobjects.clientside.MeterClientRequest;
import rest.gsonobjects.onosside.OnosResponse;
import tools.EntornoTools;
import tools.HttpTools;
import tools.LogTools;

@Path("/rest/meters")
public class MeterWebResource {
	private Gson gson;
	
	public MeterWebResource() {
		LogTools.info("MeterWebResource", "***ONOS SIMPLE REST API (OSRA) Service***");
		gson = new Gson();
	}
	
	/**
	 * 
	 * @param jsonIn
	 * @return
	 */
	@Path("{switchId}/{meterId}")
	@DELETE
	@Produces (MediaType.APPLICATION_JSON)	
	public Response deleteMeter(@PathParam("switchId") String switchId, @PathParam("meterId") String meterId) {
		LogTools.rest("DELETE", "deleteMeter", "Switch Name: " + switchId + " - MeterID: " + meterId);

		Response resRest;
		OnosResponse response;
		String url = "";
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
	
	/**
	 * Create a meter for the given host
	 * @param jsonIn JSON retrieved from client
	 * @return
	 */
	@Path("{hostIp}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)	
	public Response setMeter(@PathParam("hostIp") String hostIp, String jsonIn) {
		LogTools.rest("POST", "setMeter", "Body:\n" + jsonIn);
		Response resRest = null;
		OnosResponse response = null;
		String url = "";
		String portAux = "";
		int meterIdAux = -1;
		String hostMeterAux = "";

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
	
	/**
	 * Get switches in the SDN network
	 * @return Switches placed in SDN network
	 */
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
	public Response getAllMeters() {
		LogTools.rest("GET", "getAllMeters");
		Response resRest;
		List<Meter> meters = null;
		String json = "";
		String onosResponse = "";
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
	
}
