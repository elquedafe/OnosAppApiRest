package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import rest.gsonobjects.onosside.OnosResponse;

public class HttpTools {

	public static OnosResponse doJSONPost(URL url, String body) throws IOException{
		String encoding;
		String line;
		OnosResponse response= new OnosResponse();
		HttpURLConnection connection = null;
		OutputStreamWriter osw = null;
		BufferedReader in = null;
		BufferedReader inError = null;

		LogTools.post("POST", url.toString(), body);

		try {
			encoding = Base64.getEncoder().encodeToString((EntornoTools.user + ":"+ EntornoTools.password).getBytes("UTF-8"));
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization", "Basic " + encoding);
			OutputStream os = connection.getOutputStream();
			osw = new OutputStreamWriter(os, "UTF-8");    
			osw.write(body);
			osw.flush();

			//MAYBE COMENTAR
			InputStream content = (InputStream)connection.getInputStream();
            in = new BufferedReader (new InputStreamReader (content));
            String str = "";
            while ((line = in.readLine()) != null) {
            	str += str+"\n";
            }

            response.setMessage(str);
			response.setCode(connection.getResponseCode());
			
		} catch (IOException e) {
			LogTools.error("doJSONPost", e.getMessage());
			throw new IOException(e);
		}
		finally{
			if(osw != null)
				osw.close();
			if(connection != null)
				connection.disconnect();
			if(in != null)
				in.close();
			if(inError != null)
				inError.close();
		}
		LogTools.info("doJSONPost", "ONOS response: " + response);
		return response;
	}

	public static OnosResponse doDelete(URL url) throws IOException{
		String encoding;
		String line;
		OnosResponse response= new OnosResponse();
		HttpURLConnection connection = null;
		OutputStreamWriter osw = null;
		BufferedReader in = null;
		BufferedReader inError = null;
		
		LogTools.delete("", url.toString());
		
		
		try {
			encoding = Base64.getEncoder().encodeToString((EntornoTools.user + ":"+ EntornoTools.password).getBytes("UTF-8"));
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("DELETE");
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization", "Basic " + encoding);
			
			//MAYBE COMENTAR
			InputStream content = (InputStream)connection.getInputStream();
            in = new BufferedReader (new InputStreamReader (content));
            String str = "";
            while ((line = in.readLine()) != null) {
            	str += str+"\n";
            }
			
			response.setMessage(str);
			response.setCode(connection.getResponseCode());


		} catch (IOException e) {
			LogTools.error("doJSONDelete", e.getMessage());
			throw new IOException(e);
		}
		finally{
			if(osw != null)
				osw.close();
			if(connection != null)
				connection.disconnect();
			if(in != null)
				in.close();
			if(inError != null)
				inError.close();
		}
		LogTools.info("doJSONDelete", "ONOS response: " + response);
		return response;
	}

	public static OnosResponse doJSONGet(URL url) throws IOException{
		OnosResponse response = new OnosResponse();
		String encoding;
		String line;
		String json="";
		HttpURLConnection connection = null;
		
		LogTools.get("", url.toString());
		
		try {
			encoding = Base64.getEncoder().encodeToString((EntornoTools.user + ":"+ EntornoTools.password).getBytes("UTF-8"));
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization", "Basic " + encoding);
			InputStream content = (InputStream)connection.getInputStream();
			response.setCode(connection.getResponseCode());
			BufferedReader in   = 
					new BufferedReader (new InputStreamReader (content));
			while ((line = in.readLine()) != null) {
				//System.out.println(line);
				json += line+"\n";
			}
			response.setMessage(json);
		} catch (IOException e) {
			LogTools.error("doJSONGet", e.getMessage());
			throw new IOException(e);
		}
		finally{
			if(connection != null)
				connection.disconnect();
		}

		if(json.length() < 900) LogTools.info("doJSONGet", "ONOS response:\n" + json);
		else LogTools.info("doJSONGet", "ONOS response:\n");
		
		return response;
	}

}
