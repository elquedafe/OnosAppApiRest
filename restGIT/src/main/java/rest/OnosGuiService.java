package rest;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import architecture.Entorno;
import architecture.Switch;

import architecture.Flow;
import tools.EntornoTools;
import tools.JsonManager;


/**
 * Implementacion del servicio web REST de la calculadora. El acceso al servicio se realiza a trav�s de la URI /operacion
 * @author alvaro.luis.martinez
 *
 */
@Path("/rest")
public class OnosGuiService {
	private Entorno entorno;
	private Gson gson;
	private JsonManager parser;
	
	public OnosGuiService() {
		System.out.println("Instanciado el servicio REST OnosGUI");
		entorno = new Entorno();
		gson = new Gson();
		parser = new JsonManager(entorno);
	}
	
	/**
	 * 
	 * @return
	 */
	@Path("/getEntorno")
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
		public Response getEntorno() {
		Response resRest;
		//String op1="8"; String op2="8";
		//System.out.println("invocado getFlow");
		/*String json = "{\n" + 
				"	\"flujoId\" : \"of:0000003\"\n" + 
				"}";*/
		try {
			EntornoTools.descubrirEntorno(entorno, "onos", "rocks", "192.168.56.102", parser);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String json = gson.toJson(entorno);
		
		resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
		return resRest;
	}
	
	@Path("/getSwitches")
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
		public Response getSwitches() {
		Response resRest;
		//String op1="8"; String op2="8";
		//System.out.println("invocado getFlow");
		/*String json = "{\n" + 
				"	\"flujoId\" : \"of:0000003\"\n" + 
				"}";*/
		try {
			EntornoTools.descubrirEntorno(entorno, "onos", "rocks", "192.168.56.102", parser);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String json = gson.toJson(entorno.getMapSwitches().values());
		
		resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
		return resRest;
	}
	
	@Path("/getFlows")
	@GET
	@Produces (MediaType.APPLICATION_JSON)	
		public Response getFlows() {
		Response resRest;
		//String op1="8"; String op2="8";
		//System.out.println("invocado getFlow");
		/*String json = "{\n" + 
				"	\"flujoId\" : \"of:0000003\"\n" + 
				"}";*/
		try {
			EntornoTools.descubrirEntorno(entorno, "onos", "rocks", "192.168.56.102", parser);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String,List<Flow>> map = new HashMap<String,List<Flow>>();
		for(Switch s : entorno.getMapSwitches().values()) {
			List<Flow> listFlows = new ArrayList<Flow>();
			for(Flow flow : s.getMapFlows().values()) {
				listFlows.add(flow);
				System.out.println(flow.getId());
			}
			map.put(s.getId(), listFlows);
		}
		String json = gson.toJson(map);
		resRest = Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
		return resRest;
	}
	
}