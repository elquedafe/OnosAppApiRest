package tests;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import architecture.Host;
import architecture.Meter;
import architecture.Switch;
import rest.MeterClientRequest;
import rest.VplsClientRequest;
import tools.EntornoTools;
import tools.HttpTools;

public class Testmain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EntornoTools.onosHost = "localhost";
		EntornoTools.user = "onos";
		EntornoTools.password = "rocks";
		EntornoTools.endpoint = "http://" + EntornoTools.onosHost + ":8181/onos/v1";
		EntornoTools.endpointNetConf = EntornoTools.endpoint+"/network/configuration/";
		try {
			EntornoTools.descubrirEntorno();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*******ADD METER******/
		try {
			EntornoTools.getAllMeters();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/******ADD 1 VPLS*******/
		/*List<String> interfaces = new ArrayList<String>();
		interfaces.add("10.0.0.1");
		interfaces.add("10.0.0.2");
		VplsClientRequest vplsReq = new VplsClientRequest("VPLS1", interfaces);
		
		EntornoTools.addVplsJson(vplsReq.getVplsName(), vplsReq.getListHosts());*/
		
		
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

}
