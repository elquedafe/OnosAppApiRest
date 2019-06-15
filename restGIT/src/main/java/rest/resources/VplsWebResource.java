package rest.resources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

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
import rest.gsonobjects.clientside.VplsClientRequest;
import rest.gsonobjects.onosside.OnosResponse;
import tools.EntornoTools;
import tools.HttpTools;
import tools.LogTools;

@Path("/rest/vpls")
public class VplsWebResource {
	private Gson gson;
	
	public VplsWebResource() {
		LogTools.info("VplsWebResource", "***ONOS SIMPLE REST API (OSRA) Service***");
		gson = new Gson();
	}
	
	/**
	 * Get all flows from all the switches of the network
	 * @return All flows in the network
	 */
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
	public Response getVpls() {
		LogTools.rest("GET", "getVpls");
		Response resRest;
		try {
			LogTools.info("getVpls", "Discovering environment");
			EntornoTools.getEnvironment();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String json = EntornoTools.getVpls();

		//String json = gson.toJson(map);
		LogTools.info("getVpls", "response to client: " + json);
		resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
		return resRest;
	}
	
	/**
	 * Create a meter for the given switch
	 * @param switchId Switch ID where meter is installed
	 * @param jsonIn JSON retrieved from client
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)	
	public Response setAllVpls(String jsonIn) {
		LogTools.rest("POST", "setAllVpls", "Body:\n" + jsonIn);

		Response resRest;
		String jsonOut = "";
		String url = "";

		VplsClientRequest vplsReq = gson.fromJson(jsonIn, VplsClientRequest.class);

		try {
			//GET CURRENT TOPOLOGY STATE
			LogTools.info("setAllVpls", "Discovering environment");
			EntornoTools.getEnvironment();

			//GET JSON FOR ONOS
			jsonOut = jsonVplsGeneration(vplsReq);

			LogTools.info("setAllVpls", "JSON to ONOS for VPLS"+jsonOut);

			url = EntornoTools.endpointNetConf;
			HttpTools.doJSONPost(new URL(url), jsonOut);
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
	
	/**
	 * 
	 * @return
	 */
	@DELETE
	@Produces (MediaType.APPLICATION_JSON)	
	public Response deleteAllVpls() {
		LogTools.rest("DELETE", "deleteAllVpls");
		Response resRest;
		OnosResponse response;
		String url = "";

		//1. DELETE QoS flows
		//get hosts in vpls to delete

		//get switches connected to host

		//delete flows with instruction meter:

		//2. Delete all vpls

		url = EntornoTools.endpointNetConf;
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


		resRest = Response.ok("{\"response\":\"succesful"+ response.getMessage() +"\"}", MediaType.APPLICATION_JSON_TYPE).build();

		return resRest;
	}
	
	/**
	 * 
	 * @param jsonIn
	 * @return
	 */
	@Path("{vplsName}")
	@DELETE
	@Produces (MediaType.APPLICATION_JSON)	
	public Response deleteVpls(@PathParam("vplsName") String vplsName) {
		LogTools.rest("DELETE", "deteleVpls", "VPLS Name: " + vplsName);

		Response resRest;
		OnosResponse response;
		String url = "";

		//1. DELETE QoS flows in ingress Switches of the vplsName hosts
		//get hosts in vpls to delete

		//get switches connected to host

		//delete flows with instruction meter:


		//2. Copy 'ports' list from GET netconf

		//3. Set vpls want to maintain


		try {
			response = EntornoTools.deleteVpls(vplsName);
			//onosResponse = HttpTools.doDelete(new URL(url));
		} catch (MalformedURLException e) {
			resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;
		} catch (IOException e) {
			//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			resRest = Response.ok("IO: "+e.getMessage(), MediaType.TEXT_PLAIN).build();
			//resRest = Response.serverError().build();
			return resRest;
		}


		resRest = Response.ok("{\"response\":\"succesful"+ response.getMessage() +"\"}", MediaType.APPLICATION_JSON_TYPE).build();

		return resRest;
	}

	/**
	 * Create a meter for the given switch
	 * @param switchId Switch ID where meter is installed
	 * @param jsonIn JSON retrieved from client
	 * @return
	 */
	@Path("{vplsName}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)	
	public Response setVpls(@PathParam("vplsName") String vplsName, String jsonIn) {
		LogTools.rest("POST", "setVpls", "VPLS Name: " + vplsName + "Body:\n" + jsonIn);
		Response resRest;
		String jsonOut = "";
		String url = "";
		url = EntornoTools.endpointNetConf;
		try {
			LogTools.info("setVpls", "Discovering environment");
			EntornoTools.getEnvironment();


			VplsClientRequest vplsReq = gson.fromJson(jsonIn, VplsClientRequest.class);

			if(vplsReq.getVplsName().equals(vplsName))
				jsonOut = EntornoTools.addVplsJson(vplsReq.getVplsName(), vplsReq.getListHosts());

			//HttpTools.doDelete(new URL(url));
			HttpTools.doJSONPost(new URL(url), jsonOut);
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
	
	
	/**
	 * Generate Json document for ONOS to include VPLS
	 * @param vplsReq vpls client object
	 * @return
	 */
	private String jsonVplsGeneration(VplsClientRequest vplsReq) {
		String jsonOut = "";
		//FOR EACH HOST REQUESTED IN VPLS GENERATE JSON STRING SEGMENTS
		jsonOut = "{\r\n\"" +
				"ports\": {\r\n";
		for(int i = 0; i < vplsReq.getListHosts().size(); i++) {
			String hostVplsId = vplsReq.getListHosts().get(i);
			Host h = EntornoTools.entorno.getMapHosts().get(hostVplsId);
			vplsReq.getListHosts().set(i, "\""+hostVplsId+"\"");

			//GENERATE JSON FOR SWITCH CONNECTED TO HOST
			for(Map.Entry<String, String> entry: h.getMapLocations().entrySet()) {
				jsonOut += "\""+entry.getKey()+"/"+entry.getValue()+"\": {\r\n" + 
						"      \"interfaces\": [\r\n" + 
						"        {\r\n" + 
						"          \"name\": \""+hostVplsId+"\"\r\n" + 
						"        }\r\n" + 
						"      ]\r\n" + 
						"    },";
			}
		}

		//DELETE LAST COMMA
		if(jsonOut.endsWith(",")) {
			jsonOut = jsonOut.substring(0, jsonOut.length()-1);
		}


		jsonOut += "    }," +
				"  \"apps\" : {\r\n" + 
				"    \"org.onosproject.vpls\" : {\r\n" + 
				"      \"vpls\" : {\r\n" + 
				"        \"vplsList\" : [\r\n" + 
				"          {\r\n" + 
				"            \"name\" : \""+vplsReq.getVplsName()+"\",\r\n" + 
				"            \"interfaces\" : "+vplsReq.getListHosts().toString()+"\r\n" + 
				"        ]\r\n" + 
				"      }\r\n" + 
				"    }\r\n" + 
				"  }\r\n" + 
				"}";
		return jsonOut;
	}
}
