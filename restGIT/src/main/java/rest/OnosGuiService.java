package rest;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.google.gson.internal.LinkedTreeMap;

import architecture.Environment;
import architecture.Switch;

import architecture.Flow;
import architecture.Host;
import architecture.Link;
import architecture.Meter;
import tools.EntornoTools;
import tools.HttpTools;
import tools.JsonManager;
import tools.LogTools;


/**
 * REST web service implementation for the SDN application. REST service is accessed through /rest URI.
 * @author alvaro.luis.martinez
 *
 */
@Path("/rest")
public class OnosGuiService {
	private Gson gson;

	public OnosGuiService() {
		LogTools.info("OnosGuiService", "Instantiate ONOS GUI API REST Service");
		gson = new Gson();
	}

	/**
	 * Create a meter for the given switch
	 * @param switchId Switch ID where meter is installed
	 * @param jsonIn JSON retrieved from client
	 * @return
	 */
	@Path("authorization")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)	
	public Response setAuth(String jsonIn) {
		LogTools.rest("POST", "setAuth", jsonIn);
		String messageToClient = "";
		
		Response resRest;
		AuthorizationClientRequest authReq = gson.fromJson(jsonIn, AuthorizationClientRequest.class);

		EntornoTools.onosHost = authReq.getOnosHost();
		EntornoTools.user = authReq.getUser();
		EntornoTools.password = authReq.getPassword();
		EntornoTools.endpoint = "http://" + EntornoTools.onosHost + ":8181/onos/v1";
		EntornoTools.endpointNetConf = EntornoTools.endpoint+"/network/configuration/";
		
		try {
            // Check connetivity
            LogTools.info("setAuth", "Cheking connectivity to ONOS");
            if(ping(EntornoTools.onosHost)){
            	LogTools.info("setAuth", "Active ONOS connectivity");
            	LogTools.info("setAuth", "Discovering environment");
            	EntornoTools.descubrirEntorno();
            	messageToClient= "Success ONOS connectivity";
            }
            else{
                LogTools.error("setAtuh", "No ONOS conectivity");
                messageToClient= "No ONOS connectivity";
            }
            
        } catch (IOException e1) {
        	messageToClient= "No ONOS connectivity\n" + e1.getMessage();
        	LogTools.error("setAtuh", "No ONOS conectivity");
            resRest = Response.ok("{\"response\":\""+messageToClient+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
            return resRest;
        }

		/*try {
			EntornoTools.doJSONPost(new URL(url), "onos", "rocks", jsonOut);
		} catch (MalformedURLException e) {
			resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+jsonOut+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;
		} catch (IOException e) {
			//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			resRest = Response.ok("IO: "+e.getMessage()+"\n"+jsonOut+"\n", MediaType.TEXT_PLAIN).build();
			resRest = Response.serverError().build();
			return resRest;
		}*/
		resRest = Response.ok("{\"response\":\""+messageToClient+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		return resRest;
	}

	/**
	 * Get the environment state from the controller
	 * @return Environment
	 */
	@Path("getEntorno")
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
	public Response getEntorno() {
		LogTools.rest("GET", "getEntorno");
		Response resRest;
		try {
			EntornoTools.descubrirEntorno();
			//this.entorno = EntornoTools.entorno;
		} catch (IOException e) {
			e.printStackTrace();
		}
		String json = gson.toJson(EntornoTools.entorno);
		if(json.length() < 900)
			LogTools.info("getEntorno", "response to client: " + json);
		else
			LogTools.info("getEntorno", "response to client: " +  json.substring(0,  900) + "...");
		resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
		return resRest;
	}

	/**
	 * Get switches in the SDN network
	 * @return Switches placed in SDN network
	 */
	@Path("getSwitches")
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
	public Response getSwitches() {
		LogTools.rest("GET", "getSwitches");
		Response resRest;
		try {
			LogTools.info("getSwitches", "Discovering environment");
			EntornoTools.descubrirEntorno();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String json = gson.toJson(EntornoTools.entorno.getMapSwitches().values());
		LogTools.info("getSwitches", "response to client: " + json);
		resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
		return resRest;
	}

	/**
	 * Get all flows from all the switches of the network
	 * @return All flows in the network
	 */
	@Path("getFlows")
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
	public Response getFlows() {
		LogTools.rest("GET", "getFlows");
		Response resRest;
		try {
			LogTools.info("getFlows", "Discovering environment");
			EntornoTools.descubrirEntorno();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String,List<Flow>> map = new HashMap<String,List<Flow>>();
		for(Switch s : EntornoTools.entorno.getMapSwitches().values()) {
			List<Flow> listFlows = new ArrayList<Flow>();
			for(Flow flow : s.getMapFlows().values()) {
				listFlows.add(flow);
				//System.out.println(flow.getId());
			}
			map.put(s.getId(), listFlows);
		}
		String json = gson.toJson(map);
		LogTools.info("getFlows", "response to client: " + json);
		resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
		return resRest;
	}

	/**
	 * Create a flow for the given switch
	 * @param switchId Switch ID where flow is created
	 * @param jsonIn JSON retrieved from client
	 * @return
	 */
	@Path("flow/{switchId}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)	
	public Response setFlow(@PathParam("switchId") String switchId, String jsonIn) {
		LogTools.rest("POST", "setFlow", "To " + switchId + " switch. JSON:\n" + jsonIn);
		Response resRest;
		String messageToClient = "";
		
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
	 * Create a meter for the given switch
	 * @param switchId Switch ID where meter is installed
	 * @param jsonIn JSON retrieved from client
	 * @return
	 */
	@Path("vpls")
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
			LogTools.info("getAllVpls", "Discovering environment");
			EntornoTools.descubrirEntorno();

			//GET JSON FOR ONOS
			jsonOut = jsonVplsGeneration(vplsReq);

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
	 * Create a meter for the given switch
	 * @param switchId Switch ID where meter is installed
	 * @param jsonIn JSON retrieved from client
	 * @return
	 */
	@Path("vpls/{vplsName}")
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
			EntornoTools.descubrirEntorno();
			
			
			VplsClientRequest vplsReq = gson.fromJson(jsonIn, VplsClientRequest.class);

			jsonOut = EntornoTools.addVplsJson(vplsReq.getVplsName(), vplsReq.getListHosts());

			HttpTools.doDelete(new URL(url));
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

	
	/**
	 * 
	 * @return
	 */
	@Path("vpls")
	@DELETE
	@Produces (MediaType.APPLICATION_JSON)	
	public Response deleteAllVpls() {
		LogTools.rest("DELETE", "deleteAllVpls");
		Response resRest;
		String onosResponse = "";
		String url = "";
		
		//1. DELETE QoS flows
			//get hosts in vpls to delete
			
			//get switches connected to host
		
			//delete flows with instruction meter:
		
		//2. Delete all vpls
		
		url = EntornoTools.endpointNetConf;
		try {
			onosResponse = HttpTools.doDelete(new URL(url));
		} catch (MalformedURLException e) {
			resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;
		} catch (IOException e) {
			//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			resRest = Response.ok("IO: "+e.getMessage(), MediaType.TEXT_PLAIN).build();
			//resRest = Response.serverError().build();
			return resRest;
		}


		resRest = Response.ok("{\"response\":\"succesful"+ onosResponse +"\"}", MediaType.APPLICATION_JSON_TYPE).build();

		return resRest;
	}
	
	/**
	 * 
	 * @param jsonIn
	 * @return
	 */
	@Path("vpls/{vplsName}")
	@DELETE
	@Produces (MediaType.APPLICATION_JSON)	
	public Response deleteVpls(@PathParam("vplsName") String vplsName) {
		LogTools.rest("DELETE", "deteleVpls", "VPLS Name: " + vplsName);
		
		Response resRest;
		String onosResponse = "";
		String url = "";
		
		//1. DELETE QoS flows in ingress Switches of the vplsName hosts
			//get hosts in vpls to delete
			
			//get switches connected to host
		
			//delete flows with instruction meter:
		
		
		//2. Copy 'ports' list from GET netconf
		
		//3. Set vpls want to maintain
		
		url = EntornoTools.endpointNetConf;
		try {
			onosResponse = HttpTools.doDelete(new URL(url));
		} catch (MalformedURLException e) {
			resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;
		} catch (IOException e) {
			//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			resRest = Response.ok("IO: "+e.getMessage(), MediaType.TEXT_PLAIN).build();
			//resRest = Response.serverError().build();
			return resRest;
		}


		resRest = Response.ok("{\"response\":\"succesful"+ onosResponse +"\"}", MediaType.APPLICATION_JSON_TYPE).build();

		return resRest;
	}
	
	
	/**
	 * 
	 * @param jsonIn
	 * @return
	 */
	@Path("meter/{switchId}/{meterId}")
	@DELETE
	@Produces (MediaType.APPLICATION_JSON)	
	public Response deleteMeter(@PathParam("switchId") String switchId, @PathParam("meterId") String meterId) {
		LogTools.rest("DELETE", "deleteMeter", "Switch Name: " + switchId + " - MeterID: " + meterId);
		
		Response resRest;
		String onosResponse = "";
		String url = "";
		url = EntornoTools.endpoint + "/meters/"+switchId+"/"+meterId;
		
		try {
			onosResponse = HttpTools.doDelete(new URL(url));
		} catch (MalformedURLException e) {
			resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;
		} catch (IOException e) {
			//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			resRest = Response.ok("IO: "+e.getMessage(), MediaType.TEXT_PLAIN).build();
			//resRest = Response.serverError().build();
			return resRest;
		}


		resRest = Response.ok("{\"response\":\"succesful_"+ onosResponse +"\"}", MediaType.APPLICATION_JSON_TYPE).build();

		return resRest;
	}
	
	/**
	 * Create a meter for the given host
	 * @param jsonIn JSON retrieved from client
	 * @return
	 */
	@Path("meter")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)	
	public Response setMeter(String jsonIn) {
		LogTools.rest("POST", "setMeter", "Body:\n" + jsonIn);
		Response resRest = null;
		String onosResponse = "";
		String url = "";
		String portAux = "";
		int meterIdAux = -1;
		String hostMeterAux = "";
		
		//DESCUBRIR ENTORNO
		
		MeterClientRequest meterReq = gson.fromJson(jsonIn, MeterClientRequest.class);
		//System.out.println("HOST: "+meterReq.getHost());
		
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
					onosResponse = EntornoTools.addMeter(ingress.getId(), meterReq.getRate(), meterReq.getBurst());
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
					resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+onosResponse+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
					return resRest;
				} catch (IOException e) {
					resRest = Response.ok("IO: "+e.getMessage()+"\n"+onosResponse+
							"\n"+"\nHOST:"+h.getId()+
							"\ningress: "+ingress.getId()+
							"\nport: "+portAux+
							"\nmeter id: "+meterIdAux+
							"\nmeter Host from request: "+meterReq.getHost(), MediaType.TEXT_PLAIN).build();
					//resRest = Response.serverError().build();
					return resRest;
				}
				return resRest = Response.ok("{\"response\":\"succesful"+ onosResponse +"\"}", MediaType.APPLICATION_JSON_TYPE).build();
				
			}
		}
		else {
			return resRest = Response.ok("{\"response\":\"host not found"+ onosResponse +"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		}
		
		return resRest;
	}
	
	/**
	 * Get switches in the SDN network
	 * @return Switches placed in SDN network
	 */
	@Path("meters")
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
	
	private boolean ping(String ip) throws IOException{
        try {
            boolean ret = false;
            Socket t = new Socket();
            t.connect(new InetSocketAddress(ip, 8181), 2000);
            DataInputStream dis = new DataInputStream(t.getInputStream());
            PrintStream ps = new PrintStream(t.getOutputStream());
            ps.println("Hello");
            String str = dis.readLine();
            if (str.equals("Hello")){
                LogTools.info("ping", "Alive connection");
            }
            else{
            	LogTools.info("ping", "Not error but not checked connection");
            }
            ret = true;
            t.close();
            return ret;
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            throw new IOException("Socket error");
        }
        
    }
	
	

	

}