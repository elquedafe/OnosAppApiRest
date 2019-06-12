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

public class HttpTools {

	public static String doJSONPost(URL url, String body) throws IOException{
		String encoding;
		String line;
		String response="";
		HttpURLConnection connection = null;
		OutputStreamWriter osw = null;
		BufferedReader in = null;
		BufferedReader inError = null;

		LogTools.post("", url.toString(), body);

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

			/*InputStream content = (InputStream)connection.getInputStream();
            in = new BufferedReader (new InputStreamReader (content));
            while ((line = in.readLine()) != null) {
                response += line+"\n";
            }*/

			connection.getInputStream();
			response = String.valueOf(connection.getResponseCode());
			/*InputStream contentError = (InputStream)connection.getErrorStream();
            inError = new BufferedReader (new InputStreamReader (content));
            while ((line = inError.readLine()) != null) {
                response += line+"\n";
            }*/
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

	public static String doDelete(URL url) throws IOException{
		String encoding;
		String line;
		String response="";
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
			connection.getInputStream();
			response = String.valueOf(connection.getResponseCode());


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

	public static String doJSONGet(URL url) throws IOException{
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
			BufferedReader in   = 
					new BufferedReader (new InputStreamReader (content));
			while ((line = in.readLine()) != null) {
				//System.out.println(line);
				json += line+"\n";
			}
		} catch (IOException e) {
			LogTools.error("doJSONGet", e.getMessage());
			throw new IOException(e);
		}
		finally{
			if(connection != null)
				connection.disconnect();
		}

		if(json.length() < 900)
			LogTools.info("doJSONGet", "ONOS response:\n" + json);
		else
			LogTools.info("doJSONGet", "ONOS response:\n");
		return json;
	}

}
