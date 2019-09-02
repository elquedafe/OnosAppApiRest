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
import java.sql.SQLException;
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
import tools.DatabaseTools;
import tools.LogTools;

@Path("/rest/register")
public class RegisterWebResources {
	private Gson gson;

	public RegisterWebResources() {
		LogTools.info("RegisterWebResources", "***ONOS SIMPLE REST API (OSRA) Service***");
		gson = new Gson();
	}

	/**
	 * Register user in the OSRA system
	 * @param jsonIn
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)	
	public Response register(String jsonIn) {
		UserCredentials credentials = gson.fromJson(jsonIn, UserCredentials.class);

		try {
			DatabaseTools.register(credentials.getUser(), credentials.getPassword(), false);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return Response.status(400).entity(new OnosResponse("Error registering",400)).build();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(400).entity(new OnosResponse("Error registering",400)).build();
		}
		return Response.ok(new OnosResponse("User registered successfuly",200)).build();
	}

}
