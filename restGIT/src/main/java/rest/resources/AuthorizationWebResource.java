package rest.resources;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import rest.gsonobjects.onosside.OnosResponse;
import rest.gsonobjects.userside.AuthorizationClientRequest;
import tools.DatabaseTools;
import tools.EntornoTools;
import tools.LogTools;

/**
 * Authorization web resource.
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
@Path("/rest/authorization")
public class AuthorizationWebResource {
	private Gson gson;

	public AuthorizationWebResource() {
		LogTools.info("AuthorizationWebResource", "***ONOS SIMPLE REST API (OSRA) Service***");
		gson = new Gson();
	}

	/**
	 * Create authorization access
	 * @param jsonIn json request
	 * @return Response
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)	
	public Response setAuth(String jsonIn) {
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
			return resRest;
		}
		resRest = Response.ok("[{\"response\":\""+messageToClient+"\"},"+"{\"onosCode\":"+String.valueOf(200)+"}]", MediaType.APPLICATION_JSON_TYPE).build();
		
		return resRest;
	}

	@GET
	@Produces (MediaType.APPLICATION_JSON)	
	public Response isAdmin(@HeaderParam("authorization") String authString) {
		LogTools.rest("GET", "isAdmin", authString);

		boolean isAdmin = DatabaseTools.isAdministrator(authString);
		
		return Response.ok("{\"isAdmin\":"+isAdmin+"}", MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	/**
	 * Checks weather or not a host is available
	 * @param ip ip address or hostname of the destination
	 * @return true if is reachable or false if not
	 * @throws IOException
	 */
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
				LogTools.info("ping", "Alive connection checked");
			}
			else{
				LogTools.info("ping", "Not dead connection checked");
			}
			ret = true;
			t.close();
			return ret;
		} catch (IOException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
			throw new IOException("Socket error");
		}

	}

//	private boolean authRestUser(String userRest, String passwordRest) {
//		String propertiesFilename = "users.properties";
//		String passwd = null;
//		boolean authenticated = false;
//		try {
//			InputStream input = new FileInputStream(propertiesFilename);
//
//			Properties prop = new Properties();
//			prop.load(input);
//
//			passwd = prop.getProperty(userRest);
//			if(passwd.equals(passwordRest)) {
//				return true;
//			}
//
//		} catch (IOException io) {
//			io.printStackTrace();
//			return false;
//		}
//		return authenticated;
//	}
}
