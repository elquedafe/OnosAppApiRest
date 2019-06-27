package rest.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import com.google.gson.Gson;

import rest.gsonobjects.onosside.OnosResponse;
import rest.gsonobjects.userside.UserCredentials;
import tools.LogTools;

@Path("/rest/register")
public class RegisterWebResources {
private Gson gson;
	
	public RegisterWebResources() {
		LogTools.info("RegisterWebResources", "***ONOS SIMPLE REST API (OSRA) Service***");
		gson = new Gson();
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
	public Response register(String jsonIn) {
		UserCredentials credentials = gson.fromJson(jsonIn, UserCredentials.class);
//		boolean registered = false;
//		String db = "users.cnf";
//		try {
//
//            File f = new File(db);
//
//            BufferedReader b = new BufferedReader(new FileReader(f));
//
//            String readLine = "";
//
//            System.out.println("Reading file using Buffered Reader");
//            String[] user;
//            while ((readLine = b.readLine()) != null) {
//            	user = readLine.split(":");
//            	if(user[0].equals(credentials.getUser()) && user[1].equals(credentials.getPassword())) {
//            		registered = true;
//            		break;
//            	}
//                System.out.println(readLine);
//            }
//            b.close();
//            if(!registered) {
//            	try {
//            	    Files.write(Paths.get("users.cnf"), (credentials.getUser()+":"+credentials.getPassword()+System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
//            	}catch (IOException e) {
//            	    //exception handling left as an exercise for the reader
//            	}
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
		
		String propertiesFilename = "users.properties";
	
		try {
			InputStream input = new FileInputStream(propertiesFilename);
			
            Properties prop = new Properties();
            prop.load(input);
            
            System.out.println("PROP " +prop);
            
            String user = prop.getProperty(credentials.getUser());
            LogTools.info("register", "user retrieved "+user);
            input.close();
            
            //If user already exists
            if(user != null && !user.isEmpty()) {       
            	return Response.ok(new OnosResponse("User already registered",400)).build();
            }
            //If new user then store
            else {
            	prop.setProperty(credentials.getUser(), credentials.getPassword());
            	//CREATE NEW USER
            }
            
			OutputStream output = new FileOutputStream(propertiesFilename);
            prop.store(output, null);
            output.close();
            
            System.out.println(prop);

        } catch (IOException io) {
            io.printStackTrace();
            return Response.ok(new OnosResponse("Error registering",400)).build();
        }
		return Response.ok(new OnosResponse("User registered successfuly",200)).build();
	}

}
