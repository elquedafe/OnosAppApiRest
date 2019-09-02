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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import architecture.Queue;
import architecture.Switch;
import architecture.Vpls;
import rest.database.objects.FlowDBResponse;
import rest.database.objects.MeterDBResponse;
import rest.database.objects.QueueDBResponse;
import rest.database.objects.VplsDBResponse;
import rest.gsonobjects.onosside.FlowOnosRequest;
import rest.gsonobjects.onosside.IntentOnosRequest;
import rest.gsonobjects.onosside.OnosResponse;
import rest.gsonobjects.onosside.Point;
import rest.gsonobjects.onosside.QueueOnosRequest;
import rest.gsonobjects.userside.AuthorizationClientRequest;
import rest.gsonobjects.userside.FlowClientRequest;
import rest.gsonobjects.userside.FlowSocketClientRequest;
import rest.gsonobjects.userside.FlowSocketWithSwitchClientRequest;
import rest.gsonobjects.userside.MeterClientRequest;
import rest.gsonobjects.userside.MeterClientRequestPort;
import rest.gsonobjects.userside.QueueClientRequest;
import rest.gsonobjects.userside.VplsClientRequest;
import tools.DatabaseTools;
import tools.EntornoTools;
import tools.HttpTools;
import tools.JsonManager;
import tools.LogTools;

public class Testmain {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		EntornoTools.onosHost = "10.0.2.1";
		EntornoTools.user = "onos";
		EntornoTools.password = "rocks";
		EntornoTools.endpoint = "http://" + EntornoTools.onosHost + ":8181/onos/v1";
		EntornoTools.endpointNetConf = EntornoTools.endpoint+"/network/configuration/";
		EntornoTools.endpointQueues = "http://" + EntornoTools.onosHost + ":8181/onos/upm/queues/ovsdb:10.0.2.2";
		try {
			EntornoTools.getEnvironment();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Gson gson = new Gson();

		Environment entorno = EntornoTools.entorno;
		String endpoint = EntornoTools.endpoint;
		
		String authString = "Basic YWRtaW46YWRtaW4="; //admin:admin
		authString = "Basic YWx2YXJvOmE="; //alvaro:a

		/****GET QUEUES***/
		List<Queue> queues = new ArrayList<Queue>();
		String jsonOut = "";
		if(DatabaseTools.isAuthenticated(authString)) {
			try {
				EntornoTools.getEnvironment();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			List<QueueDBResponse> queuesDb = DatabaseTools.getQueues(authString);
			queues = EntornoTools.getQueues(queuesDb);
			jsonOut = gson.toJson(queues);
		}
		
		/***set auth**/
		String jsonIn = "{\n" + 
				"	\"userOnos\":\"onos\",\n" + 
				"	\"passwordOnos\":\"rocks\",\n" + 
				"	\"onosHost\": \"10.0.2.1\",\n" + 
				"	\"ovsdbDevice\": \"ovsdb:10.0.2.2\"\n" + 
				"}";
		LogTools.rest("POST", "setAuth", jsonIn);
		String messageToClient = "";

		Response resRest=null;
		AuthorizationClientRequest authReq = gson.fromJson(jsonIn, AuthorizationClientRequest.class);

		String ovsdbDevice = authReq.getOvsdbDevice();
		EntornoTools.onosHost = authReq.getOnosHost();
		EntornoTools.user = authReq.getUserOnos();
		EntornoTools.password = authReq.getPasswordOnos();
		EntornoTools.endpoint = "http://" + EntornoTools.onosHost + ":8181/onos/v1";
		EntornoTools.endpointNetConf = EntornoTools.endpoint+"/network/configuration/";
		EntornoTools.endpointQueues = "http://" + EntornoTools.onosHost + ":8181/onos/upm/queues/ovsdb:10.0.2.2";

		LogTools.rest("POST", "setAuth", "usuarioOnos "+EntornoTools.user+" passOnos "+EntornoTools.password);
		
		// Check ONOS connectivity
		try {
			LogTools.info("setAuth", "Cheking connectivity to ONOS");
			if(ping(EntornoTools.onosHost)){
				LogTools.info("setAuth", "ONOS connectivity");
				LogTools.info("setAuth", "Discovering environment");

				//Discover environment
				EntornoTools.getEnvironment();
				messageToClient= "Success ONOS connectivity";
			}
			else{
				LogTools.error("setAuth", "No ONOS conectivity");
				messageToClient= "No ONOS connectivity";
			}

		} catch (IOException e1) {
			messageToClient= "No ONOS connectivity\n" + e1.getMessage();
			LogTools.error("setAuth", "No ONOS conectivity");
			resRest = Response.ok("{\"response\":\""+messageToClient+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			
		}
		resRest = Response.ok("[{\"response\":\""+messageToClient+"\"},"+"{\"onosCode\":"+String.valueOf(200)+"}]", MediaType.APPLICATION_JSON_TYPE).build();
		
		
		/**********ADD QUEUE ********/
//		String jsonOut = "";
//		QueueOnosRequest queueOnosRequest = null;
//		String jsonIn = "{\n" + 
//				"	\"ipVersion\":\"4\",\n" + 
//				"	\"srcHost\":\"10.0.3.2\",\n" + 
//				"	\"srcPort\":\"80\",\n" + 
//				"	\"dstHost\":\"10.0.3.5\",\n" + 
//				"	\"dstPort\":\"5000\",\n" + 
//				"	\"portType\":\"tcp\",\n" + 
//				"	\"minRate\":10000,\n" + 
//				"	\"maxRate\": 10000,\n" + 
//				"	\"burst\":10000\n" + 
//				"}";
//		OnosResponse onosResponse = new OnosResponse();
//		if(DatabaseTools.isAuthenticated(authString)) {
//			try {
//				EntornoTools.getEnvironment();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//			QueueClientRequest queueReq = gson.fromJson(jsonIn, QueueClientRequest.class);
//			
//			
//			
//			
//			/// QUEUE ADD
//			try {
//				onosResponse = EntornoTools.addQueue(authString, queueReq);
//			} catch (IOException | ClassNotFoundException | SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//		}
		
		/*****ADD VPLS******/
//		String vplsName = "vpls1";
//		String jsonIn = "{\n" + 
//				"	\"vplsName\":\""+vplsName+"\",\n" + 
//				"	\"hosts\" : [\"10.0.3.5\",\"10.0.3.2\",\"10.0.3.4\"]\n" + 
//				",\n" + 
//				"\"rate\":100,\n" + 
//				"\"burst\":100}";
//		Response resRest;
//		String jsonOut = "";
//		String url = "";
//		if(DatabaseTools.isAuthenticated(authString)) {
//			url = EntornoTools.endpointNetConf;
//			try {
//				LogTools.info("setVpls", "Discovering environment");
//				EntornoTools.getEnvironment();
//
//				VplsClientRequest vplsReq = gson.fromJson(jsonIn, VplsClientRequest.class);
//
//				List<Vpls> vplsBefore = EntornoTools.getVplsState();
//
//				if(vplsReq.getVplsName().equals(vplsName))
//					jsonOut = EntornoTools.addVplsJson(vplsReq.getVplsName(), vplsReq.getHosts());
//
//				//GET OLD FLOW STATE
//				Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
//				for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet()){
//					for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet())
//						if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
//							oldFlowsState.put(flow.getKey(), flow.getValue());
//				}
//				
//				//HttpTools.doDelete(new URL(url));
//				HttpTools.doJSONPost(new URL(url), jsonOut);
//
//				List<Vpls> vplsAfter = EntornoTools.getVplsState();
//
//				List<Vpls> vplsNews = EntornoTools.compareVpls(vplsBefore, vplsAfter);
//
//				//ADD new vpls to DDBB
//				for(Vpls v : vplsNews) {
//					try {
//						DatabaseTools.addVplsByUser(v.getName(), authString);
//					} catch (ClassNotFoundException | SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//
//				//GET NEW STATE
//				EntornoTools.getEnvironment();
//				Map<String, Flow> newFlowsState = new HashMap<String, Flow>();
//				for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet())
//					for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet()) 
//						if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
//							newFlowsState.put(flow.getKey(), flow.getValue());
//				
//				List<Flow> flowsNews;
//				flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);
//				
//				// ADD flows to DDBB
//				if(flowsNews.size()>0) {
//					for(Flow flow : flowsNews) {
//						try {
//							//												System.out.format("Añadiend flujo a la bbdd: %s %s %s", flow.getId(), flow.getDeviceId(), flow.getFlowSelector().getListFlowCriteria().get(3));
//							System.out.format("Añadiendo flujo a la bbdd: %s %s", flow.getId(), flow.getDeviceId());
//							DatabaseTools.addFlow(flow, authString, null, null, null);
//						} catch (ClassNotFoundException | SQLException e) {
//							e.printStackTrace();
//							//TODO: Delete flow from onos and send error to client
//
//						}
//					}
//				}
//				
//				if((vplsReq.getRate() != -1) && (vplsReq.getBurst() != -1)) {
//					MeterClientRequestPort meterReq;
//					for(String srcHost : vplsReq.getHosts()) {
//						for(String dstHost : vplsReq.getHosts()) {
//							if(!srcHost.equals(dstHost)) {
//								meterReq = new MeterClientRequestPort();
//								meterReq.setSrcHost(srcHost);
//								meterReq.setDstHost(dstHost);
//								meterReq.setRate(vplsReq.getRate());
//								meterReq.setBurst(vplsReq.getBurst());
//								EntornoTools.addMeterAndFlowWithVpls(vplsName, srcHost, dstHost, authString, meterReq);
//							}
//						}
//					}
//				}
//
//
//			} catch (MalformedURLException e) {
//				resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+jsonOut+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
////				return resRest;
//			} catch (IOException e) {
//				//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
//				resRest = Response.ok("IO: "+e.getMessage()+"\n"+jsonOut+"\n", MediaType.TEXT_PLAIN).build();
//				resRest = Response.serverError().build();
////				return resRest;
//			}
//			resRest = Response.ok("{\"response\":\"succesful\"}", MediaType.APPLICATION_JSON_TYPE).build();
////			return resRest;
//		}
		
		/****GET ENVIRONMENT*****/
//		OnosResponse response = new OnosResponse();
//		URL urlClusters = new URL(endpoint + "/cluster");
//		URL urlDevices = new URL(endpoint + "/devices");
//		URL urlLinks = new URL(endpoint + "/links");
//		String urlFlows = endpoint + "/flows";
//		URL urlHosts = new URL(endpoint + "/hosts");
//
//		// CLUSTERS
//		response = HttpTools.doJSONGet(urlClusters);
//		if(response.getCode()/100 == 2)
//			JsonManager.parseJsonClustersGson(response.getMessage());
//
//		// SWITCHES
//		response = HttpTools.doJSONGet(urlDevices);
//		if(response.getCode()/100 == 2)
//			JsonManager.parseJsonDevicesGson(response.getMessage());
//
//		//PORTS
//		if(entorno.getMapSwitches() != null) {
//			for(Switch s : entorno.getMapSwitches().values()){
//				response = HttpTools.doJSONGet(new URL(endpoint+"/devices/"+s.getId()+"/ports"));
//				if(response.getCode()/100 == 2)
//					JsonManager.parseJsonPortsGson(response.getMessage());
//			}
//		}
//
//		//LINKS
//		response = HttpTools.doJSONGet(urlLinks);
//		if(response.getCode()/100 == 2)
//			JsonManager.parseJsonLinksGson(response.getMessage());
//
//		//FLOWS
//		Map<String, FlowDBResponse> userFlows = DatabaseTools.getFlowsByUser(authString);
//		for(FlowDBResponse userFlow : userFlows.values()) {
//			response = HttpTools.doJSONGet(new URL(urlFlows+"/"+userFlow.getIdSwitch()+"/"+userFlow.getIdFlow()));
//			JsonManager.parseJsonFlowGson(response.getMessage());
//		}
//
//
//		//HOSTS
//		response = HttpTools.doJSONGet(urlHosts);
//		if(response.getCode()/100 == 2)
//			JsonManager.parseJsonHostsGson(response.getMessage()); 
//
//		Environment entornos = EntornoTools.entorno;
//		System.out.println("");
//		String json = gson.toJson(EntornoTools.entorno);
		
		/*******DELETE METER********/
//		String switchId = "of:0000000000000005";
//		String meterId = "1";	
//		LogTools.rest("DELETE", "deleteMeter", "Switch Name: " + switchId + " - MeterID: " + meterId);
//
//		Response resRest;
//		if(DatabaseTools.isAuthenticated(authString))
//			resRest = EntornoTools.deleteMeterWithFlows(switchId, meterId, authString);
		
		
		/**********DELETE VPLS******/
//		String vplsName = "vpls";
//		LogTools.rest("DELETE", "deteleVpls", "VPLS Name: " + vplsName);
//
//		Response resRest;
//		OnosResponse response;
//		String url = "";
//		if(DatabaseTools.isAuthenticated(authString)) {
//
//			try {
//				List<MeterDBResponse> dbMeters = DatabaseTools.getMetersByVpls(vplsName, authString);
//				response = EntornoTools.deleteVpls(vplsName, authString);
//				//onosResponse = HttpTools.doDelete(new URL(url));
//				for(MeterDBResponse dbMeter : dbMeters) {
//					EntornoTools.deleteMeterWithFlows(dbMeter.getIdSwitch(), dbMeter.getIdMeter(), authString);
//				}
//
//				//DELETE FROM DDBB
//				DatabaseTools.deleteVpls(vplsName, authString);
//			} catch (MalformedURLException e) {
//				resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
////				return resRest;
//			} catch (IOException e) {
//				//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
//				resRest = Response.ok("IO: "+e.getMessage(), MediaType.TEXT_PLAIN).build();
//				//resRest = Response.serverError().build();
////				return resRest;
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//
////			resRest = Response.ok("{\"response\":\"succesful"+ response.getMessage() +"\"}", MediaType.APPLICATION_JSON_TYPE).build();
//		}


		/***SET METER****/
		//		String srcHost = "10.0.3.5";
		//		String dstHost = "10.0.3.3";
		//		String jsonIn = "{\"ipVersion\": \"4\",\"srcHost\": \"10.0.3.5\",\"srcPort\": \"\",\"dstHost\": \"10.0.3.3\",\"dstPort\": \"\",\"portType\": \"\",\"rate\": 1000,\"burst\": 1000}\n" + 
		//				"";
		//		LogTools.rest("POST", "setMeter", "Body:\n" + jsonIn);
		//		Response resRest = null;
		////		OnosResponse response = null;
		////		String url = "";
		////		String portAux = "";
		//
		//		if(DatabaseTools.isAuthenticated(authString)) {
		//			//DESCUBRIR ENTORNO
		//			try {
		//				EntornoTools.getEnvironment();
		//			} catch (IOException e1) {
		//				e1.printStackTrace();
		//			}
		//
		//			//Get user meter request
		//			MeterClientRequestPort meterReq = gson.fromJson(jsonIn, MeterClientRequestPort.class);
		//			System.out.println("MeterClientRequestPort object: "+ meterReq.toString());
		//			
		//			resRest = EntornoTools.addMeterAndFlow(srcHost, dstHost, authString, meterReq);
		//		}


		/**SET QoS VPLS***/
		//		String vplsName = "VplsB";
		//		String jsonIn = "{\n" + 
		//				"			\"vplsName\":\"VplsB\",\n" + 
		//				"			\"hosts\" : [\"10.0.3.2\",\"10.0.3.5\",\"10.0.3.3\"]\n" + 
		//				"		,\n" + 
		//				"		\"rate\":1000,\n" + 
		//				"		\"burst\":1000}";
		//		LogTools.rest("POST", "setVpls", "VPLS Name: " + vplsName + "Body:\n" + jsonIn);
		//		Response resRest;
		//		String jsonOut = "";
		//		String url = "";
		//		if(DatabaseTools.isAuthenticated(authString)) {
		//			url = EntornoTools.endpointNetConf;
		//			try {
		//				LogTools.info("setVpls", "Discovering environment");
		//				EntornoTools.getEnvironment();
		//
		//				VplsClientRequest vplsReq = gson.fromJson(jsonIn, VplsClientRequest.class);
		//
		//				List<Vpls> vplsBefore = EntornoTools.getVplsState();
		//
		//				if(vplsReq.getVplsName().equals(vplsName))
		//					jsonOut = EntornoTools.addVplsJson(vplsReq.getVplsName(), vplsReq.getHosts());
		//
		//				//HttpTools.doDelete(new URL(url));
		//				HttpTools.doJSONPost(new URL(url), jsonOut);
		//
		//				List<Vpls> vplsAfter = EntornoTools.getVplsState();
		//
		//				List<Vpls> vplsNews = EntornoTools.compareVpls(vplsBefore, vplsAfter);
		//
		//				//ADD new vpls to DDBB
		//				for(Vpls v : vplsNews) {
		//					try {
		//						DatabaseTools.addVplsByUser(v.getName(), authString);
		//					} catch (ClassNotFoundException | SQLException e) {
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
		//				}
		//
		//				if((vplsReq.getRate() != -1) && (vplsReq.getBurst() != -1)) {
		//					MeterClientRequestPort meterReq;
		//					for(String srcHost : vplsReq.getHosts()) {
		//						for(String dstHost : vplsReq.getHosts()) {
		//							if(!srcHost.equals(dstHost)) {
		//								meterReq = new MeterClientRequestPort();
		//								meterReq.setSrcHost(srcHost);
		//								meterReq.setDstHost(dstHost);
		//								meterReq.setRate(vplsReq.getRate());
		//								meterReq.setBurst(vplsReq.getBurst());
		//								EntornoTools.addMeterAndFlow(srcHost, dstHost, authString, meterReq);
		//							}
		//						}
		//					}
		//				}
		//
		//
		//			} catch (MalformedURLException e) {
		//				resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+jsonOut+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//				//				return resRest;
		//
		//			} catch (IOException e) {
		//				//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//				resRest = Response.ok("IO: "+e.getMessage()+"\n"+jsonOut+"\n", MediaType.TEXT_PLAIN).build();
		//				resRest = Response.serverError().build();
		//				
		//			}
		//		}
		//		resRest = Response.ok("{\"response\":\"succesful\"}", MediaType.APPLICATION_JSON_TYPE).build();




		/****GET VPLS****/
		//		LogTools.rest("GET", "getVpls");
		//		Response resRest;
		//		List<Vpls> vplss = null;
		//		List<Vpls> userVplss = new ArrayList<Vpls>();
		//		List<VplsDBResponse> vplssDB = null;
		//		if(DatabaseTools.isAdministrator(authString)) {
		//			try {
		//				LogTools.info("getVpls", "Discovering environment");
		//				EntornoTools.getEnvironment();
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//			}
		//
		//			//vplssDB = DatabaseTools.getVplsByUser(authString);
		//			vplss = EntornoTools.getVplsList();
		//
		//			//				for(Vpls vpls : vplss) {
		//			//					for(VplsDBResponse vplsDB : vplssDB) {
		//			//						if(vpls.getName().equals(vplsDB.getVplsName())) {
		//			//							userVplss.add(vpls);
		//			//						}
		//			//					}
		//			//				}
		//
		//			String json = gson.toJson(vplss);
		//			//				String json = EntornoTools.getVpls();
		//
		//			//String json = gson.toJson(map);
		//			LogTools.info("getVpls", "response to client: " + json);
		//			resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
		////			return resRest;
		//
		//		}

		/****SET VPLS*****/
		//		String vplsName = "VPLS1";
		//		String jsonIn = "{\n" + 
		//				"	\"vplsName\":\"VPLS1\",\n" + 
		//				"	\"hosts\" : [\"10.0.3.3\",\"10.0.3.1\"]\n" + 
		//				",\n" + 
		//				"\"rate\":1000,\n" + 
		//				"\"burst\":1000}";
		//		LogTools.rest("POST", "setVpls", "VPLS Name: " + vplsName + "Body:\n" + jsonIn);
		//		Response resRest;
		//		String jsonOut = "";
		//		String url = "";
		//		if(DatabaseTools.isAdministrator(authString)) {
		//			url = EntornoTools.endpointNetConf;
		//			try {
		//				LogTools.info("setVpls", "Discovering environment");
		//				EntornoTools.getEnvironment();
		//
		//
		//
		//				VplsClientRequest vplsReq = gson.fromJson(jsonIn, VplsClientRequest.class);
		//
		//				List<Vpls> vplsBefore = EntornoTools.getVplsState();
		//
		//				if(vplsReq.getVplsName().equals(vplsName))
		//					jsonOut = EntornoTools.addVplsJson(vplsReq.getVplsName(), vplsReq.getHosts());
		//
		//				//HttpTools.doDelete(new URL(url));
		//				HttpTools.doJSONPost(new URL(url), jsonOut);
		//
		//				List<Vpls> vplsAfter = EntornoTools.getVplsState();
		//
		//				List<Vpls> vplsNews = EntornoTools.compareVpls(vplsBefore, vplsAfter);
		//
		//				//ADD new vpls to DDBB
		//				for(Vpls v : vplsNews)
		//					try {
		//						DatabaseTools.addVplsByUser(v.getName(), authString);
		//					} catch (ClassNotFoundException | SQLException e) {
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
		//
		//				if(vplsReq.getRate() != -1 && vplsReq.getBurst() != -1) {
		//
		//				}
		//				//ADD METER IF
		//			} catch (MalformedURLException e) {
		//				resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+jsonOut+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//				//					return resRest;
		//			} catch (IOException e) {
		//				//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//				resRest = Response.ok("IO: "+e.getMessage()+"\n"+jsonOut+"\n", MediaType.TEXT_PLAIN).build();
		//				resRest = Response.serverError().build();
		//				//					return resRest;
		//			}
		//			resRest = Response.ok("{\"response\":\"succesful\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//			//				return resRest;
		//		}
		//		//			else
		//		//				return Response.status(401).build();
		//	}


		/*******POST FLOW*******/
		//		String srcElement = "10.0.3.5";
		//		String dstElement = "10.0.3.2";
		//		String element = "host";
		//		String jsonIn ="{\"ipVersion\": 4,\n" + 
		//				"	\"srcHost\": \"10.0.3.5\",\n" + 
		//				"	\"srcPort\": \"1000\",\n" + 
		//				"	\"dstHost\": \"10.0.3.2\",\n" + 
		//				"	\"dstPort\": \"90\",\n" + 
		//				"	\"portType\": \"TCP\"\n" + 
		//				"}";
		//		LogTools.rest("POST", "setFlow", "From host " + srcElement + " to host "+dstElement+". JSON:\n" + jsonIn);
		//		Response resRest;
		//		String messageToClient = "";
		//
		//
		//		IntentOnosRequest intentOnos = new IntentOnosRequest();
		//		IntentOnosRequest intentOnosInversed = new IntentOnosRequest();
		//		if(DatabaseTools.isAuthenticated(authString)) {
		//			System.out.println("***ELEMENT: "+element);
		//			switch(element) {
		//			case "host":
		//				FlowSocketClientRequest flowReq = gson.fromJson(jsonIn, FlowSocketClientRequest.class);
		//				LogTools.info("POST FLOW", "*INTENT*" + flowReq.toString());
		//				//CREATE INTENT SELECTOR
		//				Map<String, LinkedList<LinkedHashMap<String,Object>>> selector = EntornoTools.createSelector(flowReq);
		//				//INGRESS POINT
		//				Point ingressPoint = EntornoTools.getIngressPoint(flowReq.getSrcHost());
		//				//EGRESS POINT
		//				Point egressPoint = EntornoTools.getIngressPoint(flowReq.getDstHost());
		//				//COMPLETE INTENT
		//				intentOnos.setIngressPoint(ingressPoint);
		//				intentOnos.setEgressPoint(egressPoint);
		//				intentOnos.setSelector(selector);
		//
		//				//CREATE INTENT SELECTOR INVERSE
		//				String auxSrcHost = flowReq.getSrcHost();
		//				String auxSrcPort = flowReq.getSrcPort();
		//				String auxDstHost = flowReq.getDstHost();
		//				String auxDstPort = flowReq.getDstPort();
		//				flowReq.setDstHost(auxSrcHost);
		//				flowReq.setSrcHost(auxDstHost);
		//				flowReq.setSrcPort(auxDstPort);
		//				flowReq.setDstPort(auxSrcPort);
		//				Map<String, LinkedList<LinkedHashMap<String,Object>>> selectorInversed = EntornoTools.createSelector(flowReq);
		//				//COMPLETE INTENT
		//				intentOnosInversed.setIngressPoint(egressPoint);
		//				intentOnosInversed.setEgressPoint(ingressPoint);
		//				intentOnosInversed.setSelector(selectorInversed);
		//
		//				//Generate JSON to ONOS
		//				String jsonOut = gson.toJson(intentOnos);
		//				String jsonOutInversed = gson.toJson(intentOnosInversed);
		//				LogTools.info("setFlowSocket", "json to create intent: "+jsonOut);
		//				LogTools.info("setFlowSocket", "json to create intent INVERSED: "+jsonOutInversed);
		//				String url = EntornoTools.endpoint+"/intents";
		//				try {
		//
		//					//GET OLD STATE
		//					EntornoTools.getEnvironment();
		//					Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
		//					for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet()){
		//						for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet())
		//							if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
		//								oldFlowsState.put(flow.getKey(), flow.getValue());
		//					}
		//
		//					// CREATE FLOWS
		//					HttpTools.doJSONPost(new URL(url), jsonOut);
		//					HttpTools.doJSONPost(new URL(url), jsonOutInversed);
		//
		//					//Wait for new state
		//					//				try {
		//					//					Thread.sleep(200);
		//					//				} catch (InterruptedException e1) {
		//					//					// TODO Auto-generated catch block
		//					//					e1.printStackTrace();
		//					//				}
		//
		//					//GET NEW STATE
		//					EntornoTools.getEnvironment();
		//					Map<String, Flow> newFlowsState = new HashMap<String, Flow>();
		//					for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet())
		//						for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet()) 
		//							if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
		//								newFlowsState.put(flow.getKey(), flow.getValue());
		//
		//
		//					System.out.println(".");
		//
		//					// GET FLOWS CHANGED
		//					List<Flow> flowsNews;
		//					flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);
		//					if(flowsNews.size()>0) {
		//						for(Flow flow : flowsNews) {
		//							try {
		//								DatabaseTools.addFlowByUserId(flow, authString);
		//							} catch (ClassNotFoundException | SQLException e) {
		//								e.printStackTrace();
		//								//TODO: Delete flow from onos and send error to client
		//
		//							}
		//						}
		//					}
		//				} catch (MalformedURLException e) {
		//					resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+jsonOut+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		////					return resRest;
		//				} catch (IOException e) {
		//					//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//					resRest = Response.ok("IO: "+e.getMessage()+"\n"+jsonOut+"\n", MediaType.TEXT_PLAIN).build();
		//					//					resRest = Response.serverError().build();
		////					return resRest;
		//				}
		//				String jsonToClient = "{\"ingress\":\""+ingressPoint.getDevice()+"\",\"ingressPort\":\""+ingressPoint.getPort()+"\",\"egress\":\""+egressPoint.getDevice()+"\",\"egressPort\":\""+egressPoint.getPort()+"\"}";
		//				System.out.println("*****INGRESS/EGRESS: "+jsonToClient);
		//				resRest = Response.ok(jsonToClient, MediaType.APPLICATION_JSON_TYPE).build();
		////				return resRest;
		//			case "switch":
		//				FlowSocketWithSwitchClientRequest flowReqSw = gson.fromJson(jsonIn, FlowSocketWithSwitchClientRequest.class);
		//
		//				LogTools.info("POST FLOW", "*INTENT*" + flowReqSw.toString());
		//
		//				//CREATE INTENT SELECTOR
		//				Map<String, LinkedList<LinkedHashMap<String,Object>>> selectorSw = EntornoTools.createSelector(flowReqSw);
		//				//INGRESS POINT
		//				Point ingressPointSw = new Point(flowReqSw.getIngressPort(), flowReqSw.getIngress());
		//				//EGRESS POINT
		//				Point egressPointSw = new Point(flowReqSw.getEgressPort(), flowReqSw.getEgress());
		//				//COMPLETE INTENT
		//				intentOnos.setIngressPoint(ingressPointSw);
		//				intentOnos.setEgressPoint(egressPointSw);
		//				intentOnos.setSelector(selectorSw);
		//
		//				//CREATE INTENT SELECTOR INVERSE
		//				String auxSrcHostSw = flowReqSw.getSrcHost();
		//				String auxSrcPortSw = flowReqSw.getSrcPort();
		//				String auxDstHostSw = flowReqSw.getDstHost();
		//				String auxDstPortSw = flowReqSw.getDstPort();
		//				flowReqSw.setDstHost(auxSrcHostSw);
		//				flowReqSw.setSrcHost(auxDstHostSw);
		//				flowReqSw.setSrcPort(auxDstPortSw);
		//				flowReqSw.setDstPort(auxSrcPortSw);
		//				Map<String, LinkedList<LinkedHashMap<String,Object>>> selectorInversedSw = EntornoTools.createSelector(flowReqSw);
		//				//COMPLETE INTENT
		//				intentOnosInversed.setIngressPoint(egressPointSw);
		//				intentOnosInversed.setEgressPoint(ingressPointSw);
		//				intentOnosInversed.setSelector(selectorInversedSw);
		//
		//				//Generate JSON to ONOS
		//				String jsonOutSw = gson.toJson(intentOnos);
		//				String jsonOutInversedSw = gson.toJson(intentOnosInversed);
		//				LogTools.info("setFlowSocket", "json to create intent: "+jsonOutSw);
		//				LogTools.info("setFlowSocket", "json to create intent INVERSED: "+jsonOutInversedSw);
		//				String urlSw = EntornoTools.endpoint+"/intents";
		//				try {
		//
		//					//GET OLD STATE
		//					EntornoTools.getEnvironment();
		//					Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
		//					for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet()){
		//						for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet())
		//							if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
		//								oldFlowsState.put(flow.getKey(), flow.getValue());
		//					}
		//
		//					// CREATE FLOWS
		//					HttpTools.doJSONPost(new URL(urlSw), jsonOutSw);
		//					HttpTools.doJSONPost(new URL(urlSw), jsonOutInversedSw);
		//
		//					//Wait for new state
		//					//				try {
		//					//					Thread.sleep(200);
		//					//				} catch (InterruptedException e1) {
		//					//					// TODO Auto-generated catch block
		//					//					e1.printStackTrace();
		//					//				}
		//
		//					//GET NEW STATE
		//					EntornoTools.getEnvironment();
		//					Map<String, Flow> newFlowsState = new HashMap<String, Flow>();
		//					for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet())
		//						for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet()) 
		//							if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
		//								newFlowsState.put(flow.getKey(), flow.getValue());
		//
		//
		//					System.out.println(".");
		//
		//					// GET FLOWS CHANGED
		//					List<Flow> flowsNews;
		//					flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);
		//					if(flowsNews.size()>0) {
		//						for(Flow flow : flowsNews) {
		//							try {
		//								DatabaseTools.addFlowByUserId(flow, authString);
		//							} catch (ClassNotFoundException | SQLException e) {
		//								e.printStackTrace();
		//								//TODO: Delete flow from onos and send error to client
		//
		//							}
		//						}
		//					}
		//				} catch (MalformedURLException e) {
		//					resRest = Response.ok("URLERROR:{\"response\":\"URL error\", \"trace\":\""+jsonOutSw+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		////					return resRest;
		//				} catch (IOException e) {
		//					//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//					resRest = Response.ok("IO: "+e.getMessage()+"\n"+jsonOutSw+"\n", MediaType.TEXT_PLAIN).build();
		//					resRest = Response.serverError().build();
		////					return resRest;
		//				}
		//				resRest = Response.ok("{\"response\":\"succesful\"}", MediaType.APPLICATION_JSON_TYPE).build();
		////				return resRest;
		//
		//			default:
		////				return Response.status(400).build();
		//			}
		//
		//		}

		/**GET ENVIRONM***/
		//		try {
		//			EntornoTools.getEnvironment();
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		/*****POST METER PARA LISTOS********/
		//		String switchId = "of:0000000000000001";
		//		String jsonIn = "{\n" + 
		//				"	\"ipVersion\":\"4\",\n" + 
		//				"	\"srcHost\":\"10.0.0.1\",\n" + 
		//				"	\"srcPort\":\"80\",\n" + 
		//				"	\"dstHost\":\"10.0.0.4\",\n" + 
		//				"	\"dstPort\":\"5000\",\n" + 
		//				"	\"portType\":\"tcp\",\n" + 
		//				"	\"rate\":10000,\n" + 
		//				"	\"burst\":10000\n" + 
		//				"}";
		//		String authString = "Basic YWx2YXJvOmFsdmFybw==";
		//		
		//		LogTools.rest("POST", "setMeterSw", "Body:\n" + jsonIn);
		//		Response resRest = null;
		//		OnosResponse response = null;
		//		String url = "";
		//		String portAux = "";
		//
		//		if(DatabaseTools.isAuthenticated(authString)) {
		//			//DESCUBRIR ENTORNO
		//			try {
		//				EntornoTools.getEnvironment();
		//			} catch (IOException e1) {
		//				e1.printStackTrace();
		//			}
		//
		//			System.out.println("JSON REQUEST FROM CLIENT: "+jsonIn);
		//			//Get user meter request
		//			MeterClientRequestPort meterReq = gson.fromJson(jsonIn, MeterClientRequestPort.class);
		//			
		//			System.out.println("METER REQUEST FROM CLIENT: "+meterReq.toString());
		//			//GET HOST
		//			//			Host h = EntornoTools.getHostByIp(meterReq.getSrcHost());
		//
		//			//System.out.println("HOST: "+meterReq.getHost());
		//			//System.out.println("GET HOST: "+h.getId()+" "+h.getIpList().get(0).toString());
		//
		//
		//			//GET switches connected to host
		//			//			List<Switch> ingressSwitches = EntornoTools.getIngressSwitchesByHost(meterReq.getSrcHost());
		//			Switch ingress = EntornoTools.entorno.getMapSwitches().get(switchId);
		//
		//			//ADD METERS TO SWITCH
		//			try {
		//				// Get meters before
		//				List<Meter> oldMetersState = EntornoTools.getMeters(ingress.getId());
		//
		//				//Add meter to onos
		//				response = EntornoTools.addMeter(ingress.getId(), meterReq.getRate(), meterReq.getBurst());
		//
		//				// Get meter after
		//				List<Meter> newMetersState = EntornoTools.getMeters(ingress.getId());
		//
		//				//Compare old and new
		//				List<Meter> metersToAdd = EntornoTools.compareMeters(oldMetersState, newMetersState);
		//
		//				//Add meter to DDBB
		//				for(Meter meter : metersToAdd) {
		//					try {
		//						DatabaseTools.addMeterByUser(meter, authString);
		//					} catch (ClassNotFoundException | SQLException e) {
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
		//				}
		//
		//				//GET EGRESS PORTS FROM SWITCH
		//				String outputSwitchPort = EntornoTools.getOutputPort(meterReq.getSrcHost(), meterReq.getDstHost());
		//
		//				//Install flows for each new meter
		//				if(outputSwitchPort != null && !outputSwitchPort.isEmpty()) {
		//					for(Meter meter : metersToAdd) {
		//						if(ingress.getId().equals(meter.getDeviceId())){
		//							//GET OLD STATE
		//							EntornoTools.getEnvironment();
		//							Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
		//							for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet()){
		//								for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet())
		//									if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
		//										oldFlowsState.put(flow.getKey(), flow.getValue());
		//							}
		//
		//							// CREATE FLOW
		//							EntornoTools.addQosFlowWithPort(meterReq.getIpVersion(), ingress.getId(), outputSwitchPort, meter.getId(), meterReq.getSrcHost(), meterReq.getSrcPort(), meterReq.getDstHost(), meterReq.getDstPort(), meterReq.getPortType());
		//							//				}
		//
		//							//GET NEW STATE
		//							EntornoTools.getEnvironment();
		//							Map<String, Flow> newFlowsState = new HashMap<String, Flow>();
		//							for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet())
		//								for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet()) 
		//									if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
		//										newFlowsState.put(flow.getKey(), flow.getValue());
		//
		//
		//							System.out.println(".");
		//
		//							// GET FLOWS CHANGED
		//							List<Flow> flowsNews;
		//							flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);
		//
		//							// ADD flows to DDBB
		//							if(flowsNews.size()>0) {
		//								for(Flow flow : flowsNews) {
		//									try {
		//										DatabaseTools.addFlowByUserId(flow, authString);
		//									} catch (ClassNotFoundException | SQLException e) {
		//										e.printStackTrace();
		//										//TODO: Delete flow from onos and send error to client
		//
		//									}
		//								}
		//							}
		//
		//						}
		//					}
		//				}
		//
		//			} catch (MalformedURLException e) {
		//				resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+response.getMessage()+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		////				return resRest;
		//			} catch (IOException e) {
		//				resRest = Response.ok("IO: "+e.getMessage()+"\n"+response.getMessage()+
		//						"\n"+"\nHOST:"+meterReq.getSrcHost()+
		//						"\ningress: "+ingress.getId()+
		//						"\nport: "+portAux+
		//						"\nmeter Host from request: "+meterReq.getSrcHost(), MediaType.TEXT_PLAIN).build();
		//				//resRest = Response.serverError().build();
		////				return resRest;
		//			}
		////			return resRest = Response.ok("{\"response\":\"succesful"+ response.getMessage() +"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//		}


		/******ADD INTENT***********/
		////		LogTools.rest("POST", "setFlow", "From host " + srcElement + " to host "+dstElement+". JSON:\n" + jsonIn);
		//		Response resRest;
		//		String messageToClient = "";
		//		
		//		String authString = "Basic YWx2YXJvOmFsdmFybw==";
		//		String element = "switch";
		//		String jsonIn = "{\n" + 
		//				"	\"ipVersion\": 4,\n" + 
		//				"	\"srcHost\": \"10.0.0.2\",\n" + 
		//				"	\"srcPort\": \"35666\",\n" + 
		//				"	\"dstHost\": \"10.0.0.3\",\n" + 
		//				"	\"dstPort\": \"80\",\n" + 
		//				"	\"portType\": \"tcp\",\n" + 
		//				"	\"ingress\": \"of:0000000000000001\",\n" + 
		//				"	\"ingressPort\": \"2\",\n" + 
		//				"	\"egress\": \"of:0000000000000002\",\n" + 
		//				"	\"egressPort\": \"3\"\n" + 
		//				"}";
		//		IntentOnosRequest intentOnos = new IntentOnosRequest();
		//		IntentOnosRequest intentOnosInversed = new IntentOnosRequest();
		//		if(DatabaseTools.isAuthenticated(authString)) {
		//			
		//			switch(element) {
		//			case "host":
		//				FlowSocketClientRequest flowReq = gson.fromJson(jsonIn, FlowSocketClientRequest.class);
		//				LogTools.info("POST FLOW", "*INTENT*" + flowReq.toString());
		//				//CREATE INTENT SELECTOR
		//				Map<String, LinkedList<LinkedHashMap<String,Object>>> selector = EntornoTools.createSelector(flowReq);
		//				//INGRESS POINT
		//				Point ingressPoint = EntornoTools.getIngressPoint(flowReq.getSrcHost());
		//				//EGRESS POINT
		//				Point egressPoint = EntornoTools.getIngressPoint(flowReq.getDstHost());
		//				//COMPLETE INTENT
		//				intentOnos.setIngressPoint(ingressPoint);
		//				intentOnos.setEgressPoint(egressPoint);
		//				intentOnos.setSelector(selector);
		//
		//				//CREATE INTENT SELECTOR INVERSE
		//				String auxSrcHost = flowReq.getSrcHost();
		//				String auxSrcPort = flowReq.getSrcPort();
		//				String auxDstHost = flowReq.getDstHost();
		//				String auxDstPort = flowReq.getDstPort();
		//				flowReq.setDstHost(auxSrcHost);
		//				flowReq.setSrcHost(auxDstHost);
		//				flowReq.setSrcPort(auxDstPort);
		//				flowReq.setDstPort(auxSrcPort);
		//				Map<String, LinkedList<LinkedHashMap<String,Object>>> selectorInversed = EntornoTools.createSelector(flowReq);
		//				//COMPLETE INTENT
		//				intentOnosInversed.setIngressPoint(egressPoint);
		//				intentOnosInversed.setEgressPoint(ingressPoint);
		//				intentOnosInversed.setSelector(selectorInversed);
		//
		//				//Generate JSON to ONOS
		//				String jsonOut = gson.toJson(intentOnos);
		//				String jsonOutInversed = gson.toJson(intentOnosInversed);
		//				LogTools.info("setFlowSocket", "json to create intent: "+jsonOut);
		//				LogTools.info("setFlowSocket", "json to create intent INVERSED: "+jsonOutInversed);
		//				String url = EntornoTools.endpoint+"/intents";
		//				try {
		//
		//					//GET OLD STATE
		//					EntornoTools.getEnvironment();
		//					Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
		//					for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet()){
		//						for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet())
		//							if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
		//								oldFlowsState.put(flow.getKey(), flow.getValue());
		//					}
		//
		//					// CREATE FLOWS
		//					HttpTools.doJSONPost(new URL(url), jsonOut);
		//					HttpTools.doJSONPost(new URL(url), jsonOutInversed);
		//
		//					//Wait for new state
		//					//				try {
		//					//					Thread.sleep(200);
		//					//				} catch (InterruptedException e1) {
		//					//					// TODO Auto-generated catch block
		//					//					e1.printStackTrace();
		//					//				}
		//
		//					//GET NEW STATE
		//					EntornoTools.getEnvironment();
		//					Map<String, Flow> newFlowsState = new HashMap<String, Flow>();
		//					for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet())
		//						for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet()) 
		//							if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
		//								newFlowsState.put(flow.getKey(), flow.getValue());
		//
		//
		//					System.out.println(".");
		//
		//					// GET FLOWS CHANGED
		//					List<Flow> flowsNews;
		//					flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);
		//					if(flowsNews.size()>0) {
		//						for(Flow flow : flowsNews) {
		//							try {
		//								DatabaseTools.addFlowByUserId(flow, authString);
		//							} catch (ClassNotFoundException | SQLException e) {
		//								e.printStackTrace();
		//								//TODO: Delete flow from onos and send error to client
		//
		//							}
		//						}
		//					}
		//				} catch (MalformedURLException e) {
		//					resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+jsonOut+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		////					return resRest;
		//				} catch (IOException e) {
		//					//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//					resRest = Response.ok("IO: "+e.getMessage()+"\n"+jsonOut+"\n", MediaType.TEXT_PLAIN).build();
		//					resRest = Response.serverError().build();
		////					return resRest;
		//				}
		//				resRest = Response.ok("{\"response\":\"succesful\"}", MediaType.APPLICATION_JSON_TYPE).build();
		////				return resRest;
		//			case "switch":
		//				FlowSocketWithSwitchClientRequest flowReqSw = gson.fromJson(jsonIn, FlowSocketWithSwitchClientRequest.class);
		//
		//				LogTools.info("POST FLOW", "*INTENT*" + flowReqSw.toString());
		//
		//				//CREATE INTENT SELECTOR
		//				Map<String, LinkedList<LinkedHashMap<String,Object>>> selectorSw = EntornoTools.createSelector(flowReqSw);
		//				//INGRESS POINT
		//				Point ingressPointSw = new Point(flowReqSw.getIngressPort(), flowReqSw.getIngress());
		//				//EGRESS POINT
		//				Point egressPointSw = new Point(flowReqSw.getEgressPort(), flowReqSw.getEgress());
		//				//COMPLETE INTENT
		//				intentOnos.setIngressPoint(ingressPointSw);
		//				intentOnos.setEgressPoint(egressPointSw);
		//				intentOnos.setSelector(selectorSw);
		//
		//				//CREATE INTENT SELECTOR INVERSE
		//				String auxSrcHostSw = flowReqSw.getSrcHost();
		//				String auxSrcPortSw = flowReqSw.getSrcPort();
		//				String auxDstHostSw = flowReqSw.getDstHost();
		//				String auxDstPortSw = flowReqSw.getDstPort();
		//				flowReqSw.setDstHost(auxSrcHostSw);
		//				flowReqSw.setSrcHost(auxDstHostSw);
		//				flowReqSw.setSrcPort(auxDstPortSw);
		//				flowReqSw.setDstPort(auxSrcPortSw);
		//				Map<String, LinkedList<LinkedHashMap<String,Object>>> selectorInversedSw = EntornoTools.createSelector(flowReqSw);
		//				//COMPLETE INTENT
		//				intentOnosInversed.setIngressPoint(egressPointSw);
		//				intentOnosInversed.setEgressPoint(ingressPointSw);
		//				intentOnosInversed.setSelector(selectorInversedSw);
		//
		//				//Generate JSON to ONOS
		//				String jsonOutSw = gson.toJson(intentOnos);
		//				String jsonOutInversedSw = gson.toJson(intentOnosInversed);
		//				LogTools.info("setFlowSocket", "json to create intent: "+jsonOutSw);
		//				LogTools.info("setFlowSocket", "json to create intent INVERSED: "+jsonOutInversedSw);
		//				String urlSw = EntornoTools.endpoint+"/intents";
		//				try {
		//
		//					//GET OLD STATE
		//					EntornoTools.getEnvironment();
		//					Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
		//					for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet()){
		//						for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet())
		//							if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
		//								oldFlowsState.put(flow.getKey(), flow.getValue());
		//					}
		//
		//					// CREATE FLOWS
		//					HttpTools.doJSONPost(new URL(urlSw), jsonOutSw);
		//					HttpTools.doJSONPost(new URL(urlSw), jsonOutInversedSw);
		//
		//					//Wait for new state
		//					//				try {
		//					//					Thread.sleep(200);
		//					//				} catch (InterruptedException e1) {
		//					//					// TODO Auto-generated catch block
		//					//					e1.printStackTrace();
		//					//				}
		//
		//					//GET NEW STATE
		//					EntornoTools.getEnvironment();
		//					Map<String, Flow> newFlowsState = new HashMap<String, Flow>();
		//					for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet())
		//						for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet()) 
		//							if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
		//								newFlowsState.put(flow.getKey(), flow.getValue());
		//
		//
		//					System.out.println(".");
		//
		//					// GET FLOWS CHANGED
		//					List<Flow> flowsNews;
		//					flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);
		//					if(flowsNews.size()>0) {
		//						for(Flow flow : flowsNews) {
		//							try {
		//								DatabaseTools.addFlowByUserId(flow, authString);
		//							} catch (ClassNotFoundException | SQLException e) {
		//								e.printStackTrace();
		//								//TODO: Delete flow from onos and send error to client
		//
		//							}
		//						}
		//					}
		//				} catch (MalformedURLException e) {
		//					resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+jsonOutSw+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		////					return resRest;
		//				} catch (IOException e) {
		//					//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//					resRest = Response.ok("IO: "+e.getMessage()+"\n"+jsonOutSw+"\n", MediaType.TEXT_PLAIN).build();
		//					resRest = Response.serverError().build();
		////					return resRest;
		//				}
		//				resRest = Response.ok("{\"response\":\"succesful\"}", MediaType.APPLICATION_JSON_TYPE).build();
		////				return resRest;
		//
		//			default:
		////				return Response.status(400).build();
		//			}
		//
		//		}

		/*********ADD IPV6 socket***************/
		//		String authString = "Basic YWx2YXJvOmFsdmFybw==";
		////		LogTools.rest("POST", "setFlow", "From host " + srcHost + " to host "+dstHost+". JSON:\n" + jsonIn);
		//		String jsonIn = "{\n" + 
		//				"	\"ipVersion\":6,\n" + 
		//				"	\"srcHost\":\"fc00::3\",\n" + 
		//				"	\"srcPort\":\"\",\n" + 
		//				"	\"dstHost\":\"fc00::100\",\n" + 
		//				"	\"dstPort\":\"5005\",\n" + 
		//				"	\"portType\":\"tcp\"\n" + 
		//				"}";
		//		Response resRest;
		//		String messageToClient = "";
		//		if(DatabaseTools.isAuthenticated(authString)) {
		//			FlowSocketClientRequest flowReq = gson.fromJson(jsonIn, FlowSocketClientRequest.class);
		//			IntentOnosRequest intentOnos = new IntentOnosRequest();
		//			IntentOnosRequest intentOnosInversed = new IntentOnosRequest();
		//			
		//			LogTools.info("POST FLOW", "*INTENT*" + flowReq.toString());
		//			
		//			//CREATE INTENT SELECTOR
		//			Map<String, LinkedList<LinkedHashMap<String,Object>>> selector = EntornoTools.createSelector(flowReq);
		//			//INGRESS POINT
		//			Point ingressPoint = EntornoTools.getIngressPoint(flowReq.getSrcHost());
		//			//EGRESS POINT
		//			Point egressPoint = EntornoTools.getIngressPoint(flowReq.getDstHost());
		//			//COMPLETE INTENT
		//			intentOnos.setIngressPoint(ingressPoint);
		//			intentOnos.setEgressPoint(egressPoint);
		//			intentOnos.setSelector(selector);
		//
		//			//CREATE INTENT SELECTOR INVERSE
		//			String auxSrcHost = flowReq.getSrcHost();
		//			String auxSrcPort = flowReq.getSrcPort();
		//			String auxDstHost = flowReq.getDstHost();
		//			String auxDstPort = flowReq.getDstPort();
		//			flowReq.setDstHost(auxSrcHost);
		//			flowReq.setSrcHost(auxDstHost);
		//			flowReq.setSrcPort(auxDstPort);
		//			flowReq.setDstPort(auxSrcPort);
		//			Map<String, LinkedList<LinkedHashMap<String,Object>>> selectorInversed = EntornoTools.createSelector(flowReq);
		//			//COMPLETE INTENT
		//			intentOnosInversed.setIngressPoint(egressPoint);
		//			intentOnosInversed.setEgressPoint(ingressPoint);
		//			intentOnosInversed.setSelector(selectorInversed);
		//
		//			//Generate JSON to ONOS
		//			String jsonOut = gson.toJson(intentOnos);
		//			String jsonOutInversed = gson.toJson(intentOnosInversed);
		//			LogTools.info("setFlowSocket", "json to create intent: "+jsonOut);
		//			LogTools.info("setFlowSocket", "json to create intent INVERSED: "+jsonOutInversed);
		//			String url = EntornoTools.endpoint+"/intents";
		//			try {
		//
		//				//GET OLD STATE
		//				EntornoTools.getEnvironment();
		//				Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
		//				for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet()){
		//					for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet())
		//						if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
		//							oldFlowsState.put(flow.getKey(), flow.getValue());
		//				}
		//
		//				// CREATE FLOWS
		//				HttpTools.doJSONPost(new URL(url), jsonOut);
		//				HttpTools.doJSONPost(new URL(url), jsonOutInversed);
		//
		//				//Wait for new state
		//				//				try {
		//				//					Thread.sleep(200);
		//				//				} catch (InterruptedException e1) {
		//				//					// TODO Auto-generated catch block
		//				//					e1.printStackTrace();
		//				//				}
		//
		//				//GET NEW STATE
		//				EntornoTools.getEnvironment();
		//				Map<String, Flow> newFlowsState = new HashMap<String, Flow>();
		//				for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet())
		//					for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet()) 
		//						if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
		//							newFlowsState.put(flow.getKey(), flow.getValue());
		//
		//
		//				System.out.println(".");
		//
		//				// GET FLOWS CHANGED
		//				List<Flow> flowsNews;
		//				flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);
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
		////				return resRest;
		//			} catch (IOException e) {
		//				//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//				resRest = Response.ok("IO: "+e.getMessage()+"\n"+jsonOut+"\n", MediaType.TEXT_PLAIN).build();
		//				resRest = Response.serverError().build();
		////				return resRest;
		//			}
		//			resRest = Response.ok("{\"response\":\"succesful\"}", MediaType.APPLICATION_JSON_TYPE).build();
		////			return resRest;
		//		}

		/********ADD VPLS user alvaro*************/
		//		String authString = "Basic YWx2YXJvOmFsdmFybw==";
		//		String vplsName = "VPLS3";
		//		String jsonIn = "{\n" + 
		//				"   \"vplsName\":\"VPLS3\",\n" + 
		//				"   \"hosts\":[\n" + 
		//				"      \"10.0.0.1\",\n" + 
		//				"      \"10.0.0.3\"\n" + 
		//				"   ]\n" + 
		//				"}";
		//		LogTools.rest("POST", "setVpls", "VPLS Name: " + vplsName + "Body:\n" + jsonIn);
		//		Response resRest;
		//		String jsonOut = "";
		//		String url = "";
		//		if(DatabaseTools.isAuthenticated(authString)) {
		//			url = EntornoTools.endpointNetConf;
		//			try {
		//				LogTools.info("setVpls", "Discovering environment");
		//				EntornoTools.getEnvironment();
		//
		//				VplsClientRequest vplsReq = gson.fromJson(jsonIn, VplsClientRequest.class);
		//
		//				List<Vpls> vplsBefore = EntornoTools.getVplsState();
		//
		//				if(vplsReq.getVplsName().equals(vplsName))
		//					jsonOut = EntornoTools.addVplsJson(vplsReq.getVplsName(), vplsReq.getHosts());
		//
		//				//HttpTools.doDelete(new URL(url));
		//				HttpTools.doJSONPost(new URL(url), jsonOut);
		//
		//				List<Vpls> vplsAfter = EntornoTools.getVplsState();
		//
		//				List<Vpls> vplsNews = EntornoTools.compareVpls(vplsBefore, vplsAfter);
		//
		//				//ADD new vpls to DDBB
		//				for(Vpls v : vplsNews)
		//					try {
		//						DatabaseTools.addVplsByUser(v.getName(), authString);
		//					} catch (ClassNotFoundException | SQLException e) {
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
		//
		//			} catch (MalformedURLException e) {
		//				resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+jsonOut+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		////				return resRest;
		//			} catch (IOException e) {
		//				//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//				resRest = Response.ok("IO: "+e.getMessage()+"\n"+jsonOut+"\n", MediaType.TEXT_PLAIN).build();
		//				resRest = Response.serverError().build();
		////				return resRest;
		//			}
		////			resRest = Response.ok("{\"response\":\"succesful\"}", MediaType.APPLICATION_JSON_TYPE).build();
		////			return resRest;
		//		}
		/***** GET METERS user alvaro**********/
		//		String authString = "Basic YWx2YXJvOmFsdmFybw==";
		//		LogTools.rest("GET", "getAllMeters");
		//		Response resRest;
		//		List<Meter> meters = null;
		//		List<Meter> userMeters = new ArrayList<Meter>();
		//		List<MeterDBResponse> metersDB = null;
		//		String json = "";
		//		String onosResponse = "";
		//		if(DatabaseTools.isAuthenticated(authString)) {
		//			try {
		//				LogTools.info("getAllMeters", "Getting meters");
		//				meters = EntornoTools.getAllMeters();
		//				metersDB = DatabaseTools.getMetersByUser(authString);
		//
		//				for(Meter meter : meters) {
		//					for(MeterDBResponse meterDB : metersDB) {
		//						if(meter.getId().equals(meterDB.getIdMeter()) && meter.getDeviceId().equals(meterDB.getIdSwitch())) {
		//							userMeters.add(meter);
		//						}
		//					}
		//				}
		//
		//				LogTools.info("getAllMeters", userMeters.toArray().toString());
		//				json = gson.toJson(userMeters);
		//				LogTools.info("getAllMeters", "response to client: " + json);
		//			} catch (IOException e) {
		//				LogTools.error("getAllMeters", "Error while getting all meters");
		//				e.printStackTrace();
		//				resRest = Response.ok("{\"response\":\"No meters recieved from ONOS\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//				
		//			}
		//
		//			resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
		//			
		//		}

		/*******POST METER user alvaro**********/
		//		String jsonIn = "{\n" + 
		//				"   \"ipVersion\":\"4\",\n" + 
		//				"   \"srcHost\":\"10.0.0.1\",\n" + 
		//				"   \"srcPort\":\"80\",\n" + 
		//				"   \"dstHost\":\"10.0.0.4\",\n" + 
		//				"   \"dstPort\":\"5000\",\n" + 
		//				"   \"portType\":\"tcp\",\n" + 
		//				"   \"rate\":10000,\n" + 
		//				"   \"burst\":10000\n" + 
		//				"}";
		//		String srcHost = "10.0.0.1";
		//		String dstHost = "10.0.0.4";
		//		String authString = "Basic YWx2YXJvOmFsdmFybw==";
		//		LogTools.rest("POST", "setMeter", "Body:\n" + jsonIn);
		//		Response resRest = null;
		//		OnosResponse response = null;
		//		String url = "";
		//		String portAux = "";
		//		int meterIdAux = -1;
		//		//String hostMeterAux = "";
		//
		//		if(DatabaseTools.isAuthenticated(authString)) {
		//			//DESCUBRIR ENTORNO
		//			try {
		//				EntornoTools.getEnvironment();
		//			} catch (IOException e1) {
		//				e1.printStackTrace();
		//			}
		//
		//			//Get user meter request
		//			MeterClientRequestPort meterReq = gson.fromJson(jsonIn, MeterClientRequestPort.class);
		//
		//			if(srcHost.equals(meterReq.getSrcHost()) && dstHost.equals(meterReq.getDstHost())) {
		//				//GET HOST
		//				Host h = EntornoTools.getHostByIp(meterReq.getSrcHost());
		//
		//				//System.out.println("HOST: "+meterReq.getHost());
		//				//System.out.println("GET HOST: "+h.getId()+" "+h.getIpList().get(0).toString());
		//
		//
		//				//GET switches connected to host
		//				List<Switch> ingressSwitches = EntornoTools.getIngressSwitchesByHost(meterReq.getSrcHost());
		//
		//
		//				//ADD METERS TO SWITCHES
		//				if(h != null && (ingressSwitches.size() > 0)){
		//					for(Switch ingress : ingressSwitches) {
		//						try {
		//							// Get meters before
		//							List<Meter> oldMetersState = EntornoTools.getMeters(ingress.getId());
		//
		//							//Add meter to onos
		//							response = EntornoTools.addMeter(ingress.getId(), meterReq.getRate(), meterReq.getBurst());
		//
		//							// Get meter after
		//							List<Meter> newMetersState = EntornoTools.getMeters(ingress.getId());
		//
		//							//Compare old and new
		//							List<Meter> metersToAdd = EntornoTools.compareMeters(oldMetersState, newMetersState);
		//
		//							//Add meter to DDBB
		//							for(Meter meter : metersToAdd) {
		//								try {
		//									DatabaseTools.addMeterByUser(meter, authString);
		//								} catch (ClassNotFoundException | SQLException e) {
		//									// TODO Auto-generated catch block
		//									e.printStackTrace();
		//								}
		//							}
		//							// GET METER ID ALREADY INSTALLED
		//
		//							//							List<Meter> meter = EntornoTools.getMeters(ingress.getId());
		//							//							int meterId = meter.size();
		//							//							//System.out.println("Meter ID: "+ meterId);
		//
		//							//TODO do it with consulting /path/srcHost/dstHost
		//							//GET EGRESS PORTS FROM SWITCH
		//							String outputSwitchPort = EntornoTools.getOutputPort(meterReq.getSrcHost(), meterReq.getDstHost());
		//							//							List<String> outputSwitchPorts = EntornoTools.getOutputPort(meterReq.getSrcHost(), meterReq.getDstHost());
		//
		//							//Install flows for each new meter
		//							if(outputSwitchPort != null && !outputSwitchPort.isEmpty()) {
		//								for(Meter meter : metersToAdd) {
		//									if(ingress.getId().equals(meter.getDeviceId())){
		//										//										portAux = port;
		//										meterIdAux = Integer.parseInt(meter.getId());
		//										//										hostMeterAux = meterReq.getSrcHost();
		//
		//										EntornoTools.addQosFlowWithPort(meterReq.getIpVersion(), ingress.getId(), outputSwitchPort, meter.getId(), meterReq.getSrcHost(), meterReq.getSrcPort(), meterReq.getDstHost(), meterReq.getDstPort(), meterReq.getPortType());
		//									}
		//								}
		//							}
		//
		//						} catch (MalformedURLException e) {
		//							resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+response.getMessage()+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
		//							
		//						} catch (IOException e) {
		//							resRest = Response.ok("IO: "+e.getMessage()+"\n"+response.getMessage()+
		//									"\n"+"\nHOST:"+h.getId()+
		//									"\ningress: "+ingress.getId()+
		//									"\nport: "+portAux+
		//									"\nmeter id: "+meterIdAux+
		//									"\nmeter Host from request: "+meterReq.getSrcHost(), MediaType.TEXT_PLAIN).build();
		//							//resRest = Response.serverError().build();
		//						}
		//						String p = "{\"response\":\"succesful\"";
		//
		//					}
		//				}
		//				else {
		//					String s = "{\"response\":\"error host or switches = 0\"}";
		//				}
		//			}
		//		}



		/********POST FLOW (new json) user admin***********/
		//		Response resRest;
		//		String messageToClient = "";
		//		String authString = "Basic YWRtaW46YWRtaW4=";
		//		String jsonIn = "{\n" + 
		//				"	\"ipVersion\": 4,\n" + 
		//				"	\"srcHost\": \"10.0.0.2\",\n" + 
		//				"	\"srcPort\": \"35666\",\n" + 
		//				"	\"dstHost\": \"10.0.0.3\",\n" + 
		//				"	\"dstPort\": \"80\"\n" + 
		//				"}";
		//		if(DatabaseTools.isAuthenticated(authString)) {
		//			FlowSocketClientRequest flowReq = gson.fromJson(jsonIn, FlowSocketClientRequest.class);
		//			FlowOnosRequest flowOnos = new FlowOnosRequest();
		//			LinkedList<LinkedHashMap<String,Object>> auxList = new LinkedList<LinkedHashMap<String,Object>>();
		//			LinkedHashMap<String, Object> auxMap = new LinkedHashMap<String,Object>();
		//			Map<String, LinkedList<LinkedHashMap<String, Object>>> treatement = new LinkedHashMap<String, LinkedList<LinkedHashMap<String,Object>>>();
		//			Map<String, LinkedList<LinkedHashMap<String,Object>>> selector = new LinkedHashMap<String, LinkedList<LinkedHashMap<String,Object>>>();
		//
		//			//Get ingress switch + output ports
		//			List<Switch> ingress = EntornoTools.getIngressSwitchesByHost(flowReq.getSrcHost());
		//			List<String> ports = EntornoTools.getOutputPorts(ingress.get(0).getId());
		//
		//			flowOnos.setDeviceId(ingress.get(0).getId());
		//
		//			//TREATMENT
		//			auxMap.put("type", "OUTPUT");
		//			auxMap.put("port", ports.get(0));
		//			auxList.add(auxMap);
		//			treatement.put("instructions", auxList);
		//			flowOnos.setTreatment(treatement);
		//
		//			//SELECTOR
		//			//criteria 1
		//			auxMap = new LinkedHashMap<String, Object>();
		//			auxList = new LinkedList<LinkedHashMap<String,Object>>();
		//			if(flowReq.getIpVersion() == 4) {
		//				auxMap.put("type", "ETH_TYPE");
		//				auxMap.put("ethType", "0x800");
		//				auxList.add(auxMap);
		//				
		//				auxMap = new LinkedHashMap<String, Object>();
		//				auxMap.put("type", "IPV4_SRC");
		//				auxMap.put("ip", (flowReq.getSrcHost()+"/32"));
		//				auxList.add(auxMap);
		//				
		//				auxMap = new LinkedHashMap<String, Object>();
		//				auxMap.put("type", "IPV4_DST");
		//				auxMap.put("ip", (flowReq.getDstHost()+"/32"));
		//				auxList.add(auxMap);
		//			}
		//			else if (flowReq.getIpVersion() == 6) {
		//				auxMap.put("type", "ETH_TYPE");
		//				auxMap.put("ethType", "0x86DD");
		//				auxList.add(auxMap);
		//				
		//				auxMap = new LinkedHashMap<String, Object>();
		//				auxMap.put("type", "IPV6_SRC");
		//				auxMap.put("ip", (flowReq.getSrcHost()+"/32"));
		//				auxList.add(auxMap);
		//				
		//				auxMap = new LinkedHashMap<String, Object>();
		//				auxMap.put("type", "IPV6_DST");
		//				auxMap.put("ip", (flowReq.getDstHost()+"/32"));
		//				auxList.add(auxMap);
		//			}
		//			//criteria 2
		//			auxMap = new LinkedHashMap<String, Object>();
		//			auxMap.put("type", "IP_PROTO");
		//			auxMap.put("protocol", "6");
		//			auxList.add(auxMap);
		//			//criteria 3
		//			auxMap = new LinkedHashMap<String, Object>();
		//			auxMap.put("type", "TCP_SRC");
		//			auxMap.put("tcpPort", flowReq.getSrcPort());
		//			auxList.add(auxMap);
		//			//criteria 4
		//			auxMap = new LinkedHashMap<String, Object>();
		//			auxMap.put("type", "TCP_DST");
		//			auxMap.put("tcpPort", flowReq.getDstPort());
		//			auxList.add(auxMap);
		//
		//			selector.put("criteria", auxList);
		//			flowOnos.setSelector(selector);
		//
		//			//Generate JSON to ONOS
		//			String jsonOut = gson.toJson(flowOnos);
		//
		//			String url = EntornoTools.endpoint+"/flows/"+ingress.get(0).getId();
		//			try {
		//				EntornoTools.getEnvironment();
		//
		//				Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
		//				for(Map.Entry<String, Flow> flow: EntornoTools.entorno.getMapSwitches().get(ingress.get(0).getId()).getFlows().entrySet()){
		//					oldFlowsState.put(flow.getKey(), flow.getValue());
		//				}
		//
		//				HttpTools.doJSONPost(new URL(url), jsonOut);
		//
		//				EntornoTools.getEnvironment();
		//
		//				Map<String, Flow>  newFlowsState = new HashMap<String, Flow>();
		//				for(Map.Entry<String, Flow> flow: EntornoTools.entorno.getMapSwitches().get(ingress.get(0).getId()).getFlows().entrySet()){
		//					newFlowsState.put(flow.getKey(), flow.getValue());
		//				}
		//				List<Flow> flowsNews;
		//				System.out.println(".");
		//				flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);
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
