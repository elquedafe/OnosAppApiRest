package tests;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import architecture.Environment;
import architecture.Flow;
import architecture.FlowCriteria;
import architecture.FlowInstruction;
import architecture.Host;
import architecture.Meter;
import architecture.Switch;
import architecture.Vpls;
import rest.database.objects.FlowDBResponse;
import rest.gsonobjects.onosside.FlowOnosRequest;
import rest.gsonobjects.onosside.OnosResponse;
import rest.gsonobjects.userside.AuthorizationClientRequest;
import rest.gsonobjects.userside.FlowClientRequest;
import rest.gsonobjects.userside.FlowSocketClientRequest;
import rest.gsonobjects.userside.MeterClientRequest;
import rest.gsonobjects.userside.MeterClientRequestPort;
import rest.gsonobjects.userside.VplsClientRequest;
import tools.DatabaseTools;
import tools.EntornoTools;
import tools.HttpTools;
import tools.JsonManager;
import tools.LogTools;

public class Testmain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EntornoTools.onosHost = "localhost";
		EntornoTools.user = "onos";
		EntornoTools.password = "rocks";
		EntornoTools.endpoint = "http://" + EntornoTools.onosHost + ":8181/onos/v1";
		EntornoTools.endpointNetConf = EntornoTools.endpoint+"/network/configuration/";
		//		try {
		//			EntornoTools.getEnvironment();
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		Gson gson = new Gson();

		/********POST FLOW (new json) user admin***********/
		Response resRest;
		String messageToClient = "";
		String authString = "Basic YWRtaW46YWRtaW4=";
		String jsonIn = "{\n" + 
				"	\"ipVersion\": 4,\n" + 
				"	\"srcHost\": \"10.0.0.2\",\n" + 
				"	\"srcPort\": \"35666\",\n" + 
				"	\"dstHost\": \"10.0.0.3\",\n" + 
				"	\"dstPort\": \"80\"\n" + 
				"}";
		if(DatabaseTools.isAuthenticated(authString)) {
			FlowSocketClientRequest flowReq = gson.fromJson(jsonIn, FlowSocketClientRequest.class);
			FlowOnosRequest flowOnos = new FlowOnosRequest();
			LinkedList<LinkedHashMap<String,Object>> auxList = new LinkedList<LinkedHashMap<String,Object>>();
			LinkedHashMap<String, Object> auxMap = new LinkedHashMap<String,Object>();
			Map<String, LinkedList<LinkedHashMap<String, Object>>> treatement = new LinkedHashMap<String, LinkedList<LinkedHashMap<String,Object>>>();
			Map<String, LinkedList<LinkedHashMap<String,Object>>> selector = new LinkedHashMap<String, LinkedList<LinkedHashMap<String,Object>>>();

			//Get ingress switch + output ports
			List<Switch> ingress = EntornoTools.getIngressSwitchesByHost(flowReq.getSrcHost());
			List<String> ports = EntornoTools.getOutputPorts(ingress.get(0).getId());

			flowOnos.setDeviceId(ingress.get(0).getId());

			//TREATMENT
			auxMap.put("type", "OUTPUT");
			auxMap.put("port", ports.get(0));
			auxList.add(auxMap);
			treatement.put("instructions", auxList);
			flowOnos.setTreatment(treatement);

			//SELECTOR
			//criteria 1
			auxMap = new LinkedHashMap<String, Object>();
			auxList = new LinkedList<LinkedHashMap<String,Object>>();
			if(flowReq.getIpVersion() == 4) {
				auxMap.put("type", "ETH_TYPE");
				auxMap.put("ethType", "0x800");
				auxList.add(auxMap);
				
				auxMap = new LinkedHashMap<String, Object>();
				auxMap.put("type", "IPV4_SRC");
				auxMap.put("ip", (flowReq.getSrcHost()+"/32"));
				auxList.add(auxMap);
				
				auxMap = new LinkedHashMap<String, Object>();
				auxMap.put("type", "IPV4_DST");
				auxMap.put("ip", (flowReq.getDstHost()+"/32"));
				auxList.add(auxMap);
			}
			else if (flowReq.getIpVersion() == 6) {
				auxMap.put("type", "ETH_TYPE");
				auxMap.put("ethType", "0x86DD");
				auxList.add(auxMap);
				
				auxMap = new LinkedHashMap<String, Object>();
				auxMap.put("type", "IPV6_SRC");
				auxMap.put("ip", (flowReq.getSrcHost()+"/32"));
				auxList.add(auxMap);
				
				auxMap = new LinkedHashMap<String, Object>();
				auxMap.put("type", "IPV6_DST");
				auxMap.put("ip", (flowReq.getDstHost()+"/32"));
				auxList.add(auxMap);
			}
			//criteria 2
			auxMap = new LinkedHashMap<String, Object>();
			auxMap.put("type", "IP_PROTO");
			auxMap.put("protocol", "6");
			auxList.add(auxMap);
			//criteria 3
			auxMap = new LinkedHashMap<String, Object>();
			auxMap.put("type", "TCP_SRC");
			auxMap.put("tcpPort", flowReq.getSrcPort());
			auxList.add(auxMap);
			//criteria 4
			auxMap = new LinkedHashMap<String, Object>();
			auxMap.put("type", "TCP_DST");
			auxMap.put("tcpPort", flowReq.getDstPort());
			auxList.add(auxMap);

			selector.put("criteria", auxList);
			flowOnos.setSelector(selector);

			//Generate JSON to ONOS
			String jsonOut = gson.toJson(flowOnos);

			String url = EntornoTools.endpoint+"/flows/"+ingress.get(0).getId();
			try {
				EntornoTools.getEnvironment();

				Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
				for(Map.Entry<String, Flow> flow: EntornoTools.entorno.getMapSwitches().get(ingress.get(0).getId()).getFlows().entrySet()){
					oldFlowsState.put(flow.getKey(), flow.getValue());
				}

				HttpTools.doJSONPost(new URL(url), jsonOut);

				EntornoTools.getEnvironment();

				Map<String, Flow>  newFlowsState = new HashMap<String, Flow>();
				for(Map.Entry<String, Flow> flow: EntornoTools.entorno.getMapSwitches().get(ingress.get(0).getId()).getFlows().entrySet()){
					newFlowsState.put(flow.getKey(), flow.getValue());
				}
				List<Flow> flowsNews;
				System.out.println(".");
				flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);
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
				//return resRest;
			} catch (IOException e) {
				//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
				resRest = Response.ok("IO: "+e.getMessage()+"\n"+jsonOut+"\n", MediaType.TEXT_PLAIN).build();
				resRest = Response.serverError().build();
				//return resRest;
			}
			resRest = Response.ok("{\"response\":\"succesful\"}", MediaType.APPLICATION_JSON_TYPE).build();
			//return resRest;
		}

		/*********POST FLOW User admin**************/
		//		Response resRest;
		//		String messageToClient = "";
		//		String authString = "Basic YWRtaW46YWRtaW4=";
		//		String jsonIn = "{\n" + 
		//				"	\"switchId\": \"of:0000000000000002\",\n" + 
		//				"	\"priority\": 1000,\n" + 
		//				"	\"timeout\": 0, \n" + 
		//				"	\"isPermanent\": true,\n" + 
		//				"	\"srcPort\": \"3\",\n" + 
		//				"	\"dstPort\": \"2\",\n" + 
		//				"	\"srcHost\": \"00:00:00:00:00:02\",\n" + 
		//				"	\"dstHost\": \"00:00:00:00:00:03\"\n" + 
		//				"}";
		//		
		//		if(DatabaseTools.isAuthenticated(authString)) {
		//			FlowClientRequest flowReq = gson.fromJson(jsonIn, FlowClientRequest.class);
		//			FlowOnosRequest flowOnos = new FlowOnosRequest(flowReq.getPriority(), 
		//					flowReq.getTimeout(), flowReq.isPermanent(), flowReq.getSwitchId());
		//			LinkedList<LinkedHashMap<String,String>> auxList = new LinkedList<LinkedHashMap<String,String>>();
		//			LinkedHashMap<String, String> auxMap = new LinkedHashMap<String,String>();
		//			Map<String, LinkedList<LinkedHashMap<String, String>>> treatement = new LinkedHashMap<String, LinkedList<LinkedHashMap<String,String>>>();
		//			Map<String, LinkedList<LinkedHashMap<String,String>>> selector = new LinkedHashMap<String, LinkedList<LinkedHashMap<String,String>>>();
		//
		//			//TREATMENT
		//			auxMap.put("type", "OUTPUT");
		//			auxMap.put("port", flowReq.getDstPort());
		//			auxList.add(auxMap);
		//			treatement.put("instructions", auxList);
		//			flowOnos.setTreatment(treatement);
		//
		//			//SELECTOR
		//			//criteria 1
		//			auxMap = new LinkedHashMap<String, String>();
		//			auxList = new LinkedList<LinkedHashMap<String,String>>();
		//			auxMap.put("type", "IN_PORT");
		//			auxMap.put("port", flowReq.getSrcPort());
		//			auxList.add(auxMap);
		//			//criteria 2
		//			auxMap = new LinkedHashMap<String, String>();
		//			auxMap.put("type", "ETH_DST");
		//			auxMap.put("mac", flowReq.getDstHost());
		//			auxList.add(auxMap);
		//			//criteria 3
		//			auxMap = new LinkedHashMap<String, String>();
		//			auxMap.put("type", "ETH_SRC");
		//			auxMap.put("mac", flowReq.getSrcHost());
		//			auxList.add(auxMap);
		//
		//			selector.put("criteria", auxList);
		//			flowOnos.setSelector(selector);
		//
		//			//Generate JSON to ONOS
		//			String jsonOut = gson.toJson(flowOnos);
		//			//System.out.println("JSON TO ONOS: \n"+jsonOut);
		//			/*String jsonOut = "{" +
		//		        "\"priority\": "+ flowReq.getPriority() +"," +
		//		        "\"timeout\": " + flowReq.getTimeout() + "," +
		//		        "\"isPermanent\": "+ flowReq.isPermanent() + "," +
		//		        "\"deviceId\": \""+ switchId +"\"," +
		//		        "\"tableId\": 0," +
		//		        "\"groupId\": 0," +
		//		        "\"appId\": \"org.onosproject.fwd\"," +
		//		        "\"treatment\": {" +
		//		        "\"instructions\": [" +
		//		        "{" +
		//		        "\"type\": \"OUTPUT\"," +
		//		        "\"port\": \""+ flowReq.getDstPort() +"\"" +
		//		        "}" +
		//		        "]" +
		//		        "}," +
		//		        "\"selector\": {" +
		//		        "\"criteria\": [" +
		//		        "{" +
		//		        "\"type\": \"IN_PORT\"," +
		//		        "\"port\": \""+ flowReq.getSrcPort() +"\"" +
		//		        "}," +
		//		        "{" +
		//		        "\"type\": \"ETH_DST\"," +
		//		        "\"mac\": \""+ flowReq.getDstHost() +"\"" +
		//		        "}," +
		//		        "{" +
		//		        "\"type\": \"ETH_SRC\"," +
		//		        "\"mac\": \""+ flowReq.getSrcHost() +"\"" +
		//		        "}" +
		//		        "]" +
		//		        "}" +
		//		        "}";*/
		//			String url = EntornoTools.endpoint+"/flows/"+flowReq.getSwitchId();
		//			try {
		//				EntornoTools.getEnvironment();
		//				
		//				Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
		//				for(Map.Entry<String, Flow> flow: EntornoTools.entorno.getMapSwitches().get(flowReq.getSwitchId()).getFlows().entrySet()){
		//					oldFlowsState.put(flow.getKey(), flow.getValue());
		//				}
		//				
		//				HttpTools.doJSONPost(new URL(url), jsonOut);
		//				
		//				EntornoTools.getEnvironment();
		//
		//				Map<String, Flow>  newFlowsState = new HashMap<String, Flow>();
		//				for(Map.Entry<String, Flow> flow: EntornoTools.entorno.getMapSwitches().get(flowReq.getSwitchId()).getFlows().entrySet()){
		//					newFlowsState.put(flow.getKey(), flow.getValue());
		//				}
		//				List<Flow> flowsNews;
		//				System.out.println(".");
		//				flowsNews = EntornoTools.compareFlows(flowReq.getSwitchId(), oldFlowsState, newFlowsState);
		//				if(flowsNews.size()>0) {
		//					for(Flow flow : flowsNews) {
		//						try {
		//							DatabaseTools.addFlowByUserId(flow, authString);
		//						} catch (ClassNotFoundException | SQLException e) {
		//							e.printStackTrace();
		//							//TODO: Delete flow from onos and send error to client
		//							
		//						}
		//					}
		//				}
		//			} catch (MalformedURLException e) {
		//				resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+jsonOut+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//				//return resRest;
		//			} catch (IOException e) {
		//				//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//				resRest = Response.ok("IO: "+e.getMessage()+"\n"+jsonOut+"\n", MediaType.TEXT_PLAIN).build();
		//				resRest = Response.serverError().build();
		//				//return resRest;
		//			}
		//			resRest = Response.ok("{\"response\":\"succesful\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//			//return resRest;
		//		}

		/******GET FLOWS FOR USER alvaro********/
		//		//String authString = "Basic YWx2YXJvOmFsdmFybw==";
		//		LogTools.rest("GET", "getFlows");
		//		//Response resRest;
		//		authString = "Basic YWx2YXJvOmFsdmFybw==";
		//		//Check if user is authorized
		//		if(DatabaseTools.isAuthenticated(authString)) {
		//			try {
		//				LogTools.info("getFlows", "Discovering environment");
		//				EntornoTools.getEnvironment();
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//			}
		//
		//			//GET FLOWS STORED IN DB to know user flows ID.
		//			Map<String, FlowDBResponse> flowsDB = DatabaseTools.getFlowsByUser(authString);
		//
		//			Map<String,List<Flow>> map = new HashMap<String,List<Flow>>();
		//			for(Switch s : EntornoTools.entorno.getMapSwitches().values()) {
		//				List<Flow> listFlows = new ArrayList<Flow>();
		//				for(Flow flow : s.getMapFlows().values()) {
		//					// If flow is associated to the user adds to the list
		//					if(flowsDB.containsKey((flow.getId()))) {
		//						listFlows.add(flow);
		//						//System.out.println(flow.getId());
		//					}
		//				}
		//				map.put(s.getId(), listFlows);
		//			}
		//			String json = gson.toJson(map);
		//			LogTools.info("getFlows", "response to client: " + json);
		//			resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
		//			//return resRest;
		//		}

		/******Check Database*******/
		//		boolean auth = DatabaseTools.isAuthenticated("Basic YWRtaW46YWRtaW4="); //alvaro:alvaro=Basic YWx2YXJvOmFsdmFybw==   admin:admin

		/*******DELETE METER SOCKET*********/
		//		Response resRest = null;
		//		OnosResponse onosResponse = null;
		//		boolean matchType = false;
		//		boolean matchPort = false;
		//		boolean matchIp = false;
		//		String hostIp = "10.0.0.1";
		//		String hostPort = "54244";
		//		String meterId = "";
		//		try {
		//			EntornoTools.getEnvironment();
		//			List<Switch> ingress = EntornoTools.getIngressSwitchesByHost(hostIp);
		//			for(Switch s : ingress) {
		//				for(Flow f : s.getMapFlows().values()) {
		//					matchType = false;
		//					matchPort = false;
		//					matchIp = false;
		//					for(FlowCriteria criteria : f.getFlowSelector().getListFlowCriteria()) {
		//						if(criteria.getType().equals("IPV4_SRC") && criteria.getCriteria().getValue().equals(hostIp+"/32"))
		//							matchIp = true;
		//						if(criteria.getType().equals("TCP_SRC") || criteria.getType().equals("UDP_SRC"))
		//							matchType = true;
		//						if(criteria.getCriteria().getValue().equals(String.valueOf(Double.parseDouble(hostPort))) )
		//							matchPort = true;
		//
		//					}
		//					if(matchIp && matchType && matchPort) {
		//						onosResponse = HttpTools.doDelete(new URL(EntornoTools.endpoint+"/flows/"+s.getId()+"/"+f.getId()));
		//						for(FlowInstruction instruction : f.getFlowTreatment().getListInstructions()) {
		//							if(instruction.getInstructions().containsKey("meterId")) {
		//								meterId = instruction.getInstructions().get("meterId");
		//								HttpTools.doDelete(new URL(EntornoTools.endpoint+"/meters/"+s.getId()+"/"+meterId));
		//							}
		//						}
		//					}
		//				}
		//			}
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//
		//		}
		//
		//		resRest = Response.ok(gson.toJson(onosResponse), MediaType.APPLICATION_JSON_TYPE).build();



		/*********TEST SOCKET POST METER**************/
		//		String hostIp = "10.0.0.1";
		//		String body = "{\n" + 
		//				"	\"host\": \"10.0.0.1\",\n" + 
		//				"	\"port\": \"5001\",\n" + 
		//				"	\"portType\": \"tcp\",\n" + 
		//				"	\"rate\": 100000,\n" + 
		//				"	\"burst\": 100000\n" + 
		//				"}";
		//		
		//		LogTools.rest("POST", "setMeter", "Body:\n" + body);
		//		Response resRest = null;
		//		OnosResponse response = null;
		//		String url = "";
		//		String portAux = "";
		//		int meterIdAux = -1;
		//		String hostMeterAux = "";
		//
		//		//DESCUBRIR ENTORNO
		//		MeterClientRequestPort meterReq = gson.fromJson(body, MeterClientRequestPort.class);
		//		//System.out.println("HOST: "+meterReq.getHost());
		//
		//		if(hostIp.equals(meterReq.getHost())) {
		//			//GET HOST
		//			Host h = EntornoTools.getHostByIp(meterReq.getHost());
		//
		//			//System.out.println("HOST: "+meterReq.getHost());
		//			//System.out.println("GET HOST: "+h.getId()+" "+h.getIpList().get(0).toString());
		//
		//
		//			//GET switches connected to host
		//			List<Switch> ingressSwitches = EntornoTools.getIngressSwitchesByHost(meterReq.getHost());
		//
		//			//ADD METERS TO SWITCHES
		//			if(h != null){
		//				for(Switch ingress : ingressSwitches) {
		//					try {
		//						//System.out.println("Ingress sw "+ingress.getId()+" para "+ meterReq.getHost());
		//						response = EntornoTools.addMeter(ingress.getId(), meterReq.getRate(), meterReq.getBurst());
		//						//System.out.println("Meter añadido? respuesta de onos: "+onosResponse);
		//
		//						// GET METER ID ALREADY INSTALLED
		//						List<Meter> meter = EntornoTools.getMeters(ingress.getId());
		//						int meterId = meter.size();
		//						//System.out.println("Meter ID: "+ meterId);
		//
		//						//GET EGRESS PORTS FROM SWITCH
		//						List<String> outputSwitchPorts = EntornoTools.getOutputPorts(ingress.getId());
		//
		//						//Install flows
		//						for(String port : outputSwitchPorts) {
		//							portAux = port;
		//							meterIdAux = meterId;
		//							hostMeterAux = meterReq.getHost();
		//
		//							EntornoTools.addQosFlowWithPort(ingress.getId(), port, meterId, meterReq.getHost(), meterReq.getPort(), meterReq.getPortType());
		//
		//						}
		//
		//					} catch (MalformedURLException e) {
		//						resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+response.getMessage()+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//						
		//					} catch (IOException e) {
		//						resRest = Response.ok("IO: "+e.getMessage()+"\n"+response.getMessage()+
		//								"\n"+"\nHOST:"+h.getId()+
		//								"\ningress: "+ingress.getId()+
		//								"\nport: "+portAux+
		//								"\nmeter id: "+meterIdAux+
		//								"\nmeter Host from request: "+meterReq.getHost(), MediaType.TEXT_PLAIN).build();
		//						//resRest = Response.serverError().build();
		//						
		//					}
		//					
		//
		//				}
		//			}
		//			else {
		//				
		//			}
		//		}

		/*******AUTH TEST*******/
		//		Gson gson = new Gson();
		//		String jsonIn = "{\n" + 
		//				"	\"user\":\"onos\",\n" + 
		//				"	\"password\":\"rocks\",\n" + 
		//				"	\"onosHost\": \"localhost\"\n" + 
		//				"}";
		//		
		//		LogTools.rest("POST", "setAuth", jsonIn);
		//		String messageToClient = "";
		//
		//		Response resRest;
		//		AuthorizationClientRequest authReq = gson.fromJson(jsonIn, AuthorizationClientRequest.class);
		//
		//		EntornoTools.onosHost = authReq.getOnosHost();
		//		EntornoTools.user = authReq.getUser();
		//		EntornoTools.password = authReq.getPassword();
		//		EntornoTools.endpoint = "http://" + EntornoTools.onosHost + ":8181/onos/v1";
		//		EntornoTools.endpointNetConf = EntornoTools.endpoint+"/network/configuration/";
		//
		//		// Check ONOS connectivity
		//		try {
		//			LogTools.info("setAuth", "Cheking connectivity to ONOS");
		//			if(ping(EntornoTools.onosHost)){
		//				LogTools.info("setAuth", "ONOS connectivity");
		//				LogTools.info("setAuth", "Discovering environment");
		//				
		//				//Discover environment
		//				EntornoTools.getEnvironment();
		//				messageToClient= "Success ONOS connectivity";
		//			}
		//			else{
		//				LogTools.error("setAuth", "No ONOS conectivity");
		//				messageToClient= "No ONOS connectivity";
		//			}
		//
		//		} catch (IOException e1) {
		//			messageToClient= "No ONOS connectivity\n" + e1.getMessage();
		//			LogTools.error("setAuth", "No ONOS conectivity");
		//			resRest = Response.ok("{\"response\":\""+messageToClient+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//		}

		/******GET VPLS*********/
		//		String g = EntornoTools.getVpls();



		/*******ADD 2 VPLS*********/
		/*String url = EntornoTools.endpointNetConf;
		List<String> interfaces = new ArrayList<String>();
		interfaces.add("10.0.0.2");
		interfaces.add("10.0.0.4");
		VplsClientRequest vplsReq = new VplsClientRequest("VPLS1", interfaces);


		String jsonOut = EntornoTools.addVplsJson(vplsReq.getVplsName(), vplsReq.getListHosts());
		try {
			//HttpTools.doDelete(new URL(url));
			//Thread.sleep(200);
			HttpTools.doJSONPost(new URL(url), jsonOut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		interfaces = new ArrayList<String>();
		interfaces.add("10.0.0.1");
		interfaces.add("10.0.0.3");
		vplsReq = new VplsClientRequest("VPLS2", interfaces);
		jsonOut = EntornoTools.addVplsJson(vplsReq.getVplsName(), vplsReq.getListHosts());

		try {
			//HttpTools.doDelete(new URL(url));
			//Thread.sleep(200);
			HttpTools.doJSONPost(new URL(url), jsonOut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		 */

		/*******DELETE 1 VPLS (new)************/
		//		try {
		//			EntornoTools.deleteVpls("VPLS1");
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		/*********DELETE 1 VPLS************/
		/*String json = "";
		String vplsToDelete = "VPLS1";


		try {
			json = HttpTools.doJSONGet(new URL(EntornoTools.endpointNetConf));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//DELETE ALL VPLS
		try {
			HttpTools.doDelete(new URL(EntornoTools.endpointNetConf));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//ADD ONLY NOT DELETED
		List<Vpls> vplss = JsonManager.parseoVpls(json);
		for(int i = 0; i < vplss.size(); i++) {
			if(!vplss.get(i).getName().equals(vplsToDelete)) {
				json = EntornoTools.addVplsJson(vplss.get(i).getName(), vplss.get(i).getInterfaces());
				try {
					HttpTools.doJSONPost(new URL(EntornoTools.endpointNetConf), json);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}*/

		/********GET VPLS************/
		/*String jsonToClient = EntornoTools.getVpls();
		System.out.println(jsonToClient);*/

		/*******ADD METER******/
		/*try {
			EntornoTools.getAllMeters();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		/******ADD 1 or 2 VPLS*******/
		/*List<String> interfaces = new ArrayList<String>();
		interfaces.add("10.0.0.2");
		interfaces.add("10.0.0.3");
		VplsClientRequest vplsReq = new VplsClientRequest("VPLS1", interfaces);

		String json = EntornoTools.addVplsJson(vplsReq.getVplsName(), vplsReq.getListHosts());
		try {
			HttpTools.doJSONPost(new URL(EntornoTools.endpointNetConf), json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		interfaces = new ArrayList<String>();
		interfaces.add("10.0.0.1");
		interfaces.add("10.0.0.4");
		vplsReq = new VplsClientRequest("VPLS2", interfaces);

		json = EntornoTools.addVplsJson(vplsReq.getVplsName(), vplsReq.getListHosts());
		try {
			HttpTools.doJSONPost(new URL(EntornoTools.endpointNetConf), json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		/****ACTUAL VPLS STATE IN JSON POST FORMAT****/
		/*String json = "{\"ports\":{";
		//PORTS def
		json += EntornoTools.getNetConfPorts();

		json += "},";

		//VPLS def
		json += "\"apps\" : {\n" + 
				"    \"org.onosproject.vpls\" : {\n" + 
				"      \"vpls\" : {\n" + 
				"        \"vplsList\" : [\n"; 
		//VPLS LIST
		json += EntornoTools.getVplsInstalled();
		json += "        ]\n" + 
				"      }\n" + 
				"    }\n" + 
				"  }\n" +
				"}";


		System.out.println("\n\nJSON VPLS DEFINITIVO:"+json);*/
		/*********** DELETE VPLS **********/
		/*try {
			String resp = HttpTools.doDelete(new URL(EntornoTools.endpointNetConf));
			System.out.println("CODIGO RESPUESTA: "+resp);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		/***** QOS*******/
		/*String url ="http://localhost:8181/onos/v1/links?device=of:0000000000000002&direction=EGRESS";
		try {
			String json = HttpTools.doJSONGet(new URL(url));

			System.out.println(json);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/


		/*Response resRest;
		String onosResponse = "";
		String url = "";

		MeterClientRequest meterReq = new MeterClientRequest( "10.0.0.4", 100000, 100000);
		//MeterClientRequest meterReq = gson.fromJson(jsonIn, MeterClientRequest.class);


		//GET HOST
		Host h = EntornoTools.getHostByIp(meterReq.getHost());
		System.out.println("HOST: "+meterReq.getHost());
		System.out.println("GET HOST: "+h.getId()+" "+h.getIpList().get(0).toString());

		//GET switches connected to host
		List<Switch> ingressSwitches = EntornoTools.getIngressSwitchesByHost(meterReq.getHost());

		//ADD METERS TO SWITCHES
		if(h != null){
			for(Switch ingress : ingressSwitches) {
				try {
					System.out.println("Ingress sw "+ingress.getId()+" para "+ meterReq.getHost());
					onosResponse = EntornoTools.addMeter(ingress.getId(), meterReq.getRate(), meterReq.getBurst());

					// GET METER ID ALREADY INSTALLED
					List<Meter> meter = EntornoTools.getMeters(ingress.getId());
					int meterId = meter.size();
					System.out.println("Meter ID: "+ meterId);

					//GET EGRESS PORTS FROM SWITCH
					List<String> outputSwitchPorts = EntornoTools.getOutputPorts(ingress.getId());

					//Install flows
					for(String port : outputSwitchPorts) {

						EntornoTools.addQosFlow(ingress.getId(), port, meterId, meterReq.getHost());


					}

				} catch (MalformedURLException e) {
					resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+onosResponse+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();

				} catch (IOException e) {
					resRest = Response.ok("IO: "+e.getMessage()+"\n"+onosResponse+"\n"+"\nHOST:"+h.getId(), MediaType.TEXT_PLAIN).build();
					//resRest = Response.serverError().build();

				}
				resRest = Response.ok("{\"response\":\"succesful"+ onosResponse +"\"}", MediaType.APPLICATION_JSON_TYPE).build();

			}
		}
		else {
			resRest = Response.ok("{\"response\":\"host not found"+ onosResponse +"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		}*/


	}


	/**
	 * Checks weather or not a host is available
	 * @param ip ip address or hostname of the destination
	 * @return true if is reachable or false if not
	 * @throws IOException
	 */
	private static boolean ping(String ip) throws IOException{
		try {
			boolean ret = false;
			Socket t = new Socket();
			t.connect(new InetSocketAddress(ip, 8181), 2000);
			DataInputStream dis = new DataInputStream(t.getInputStream());
			PrintStream ps = new PrintStream(t.getOutputStream());
			ps.println("Hello");
			String str = dis.readLine();
			if (str.equals("Hello")){
				LogTools.info("ping", "Alive connection checked");
			}
			else{
				LogTools.info("ping", "Not dead connection checked");
			}
			ret = true;
			t.close();
			return ret;
		} catch (IOException ex) {
			throw new IOException("Socket error");
		}

	}



}
