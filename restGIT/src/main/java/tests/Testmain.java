package tests;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import architecture.Flow;
import architecture.FlowCriteria;
import architecture.FlowInstruction;
import architecture.Host;
import architecture.Meter;
import architecture.Switch;
import architecture.Vpls;
import rest.gsonobjects.clientside.AuthorizationClientRequest;
import rest.gsonobjects.clientside.MeterClientRequest;
import rest.gsonobjects.clientside.MeterClientRequestPort;
import rest.gsonobjects.clientside.VplsClientRequest;
import rest.gsonobjects.onosside.OnosResponse;
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
		try {
			EntornoTools.getEnvironment();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Gson gson = new Gson();



		/*******DELETE METER SOCKET*********/
		Response resRest = null;
		OnosResponse onosResponse = null;
		boolean matchType = false;
		boolean matchPort = false;
		boolean matchIp = false;
		String hostIp = "10.0.0.1";
		String hostPort = "54244";
		String meterId = "";
		try {
			EntornoTools.getEnvironment();
			List<Switch> ingress = EntornoTools.getIngressSwitchesByHost(hostIp);
			for(Switch s : ingress) {
				for(Flow f : s.getMapFlows().values()) {
					matchType = false;
					matchPort = false;
					matchIp = false;
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

		}

		resRest = Response.ok(gson.toJson(onosResponse), MediaType.APPLICATION_JSON_TYPE).build();



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
