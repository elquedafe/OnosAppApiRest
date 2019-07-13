package tools;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import architecture.Flow;
import architecture.Meter;
import rest.database.objects.FlowDBResponse;
import rest.database.objects.MeterDBResponse;
import rest.database.objects.VplsDBResponse;
import sun.misc.BASE64Decoder;

public class DatabaseTools {
	private static final String IP_MARIADB = "localhost";
	private static final String PORT = "3306";
	private static final String DATABASE = "osra";
	private static final String USER = "alvaro";
	private static final String PASS = "a";


	public static boolean isAuthenticated(String authString) {
		String usernameDB = "";
		String passwordDB = "";
		boolean isAdmin = false;

		String[] decoded = getUserPassFromCoded(authString);

		if(decoded != null) {
			String user = decoded[0];
			String password = decoded[1];


			try {
				ResultSet rs = executeStatement("SELECT UserName,Password,IsAdmin FROM User WHERE UserName='"+user+"' AND Password='"+password+"'");

				while (rs.next()){
					usernameDB = rs.getString("UserName");
					passwordDB = rs.getString("Password");
					isAdmin = rs.getBoolean("IsAdmin");

					// print the results
					System.out.format("%s, %s, %b\n", usernameDB, passwordDB, isAdmin);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}



			if(user.equals(usernameDB) && password.equals(passwordDB)) {
				LogTools.info("isAuthenticated", "AUTENTICADO: "+user);
				return true;
			}
			else {
				LogTools.info("isAuthenticated", "NOT AUTENTICADO: ");
				return false;
			}
		}
		else return false;
	}

	public static boolean isAdministrator(String authString) {
		String usernameDB = "";
		String passwordDB = "";
		boolean isAdmin = false;

		String[] decoded = getUserPassFromCoded(authString);

		if(decoded != null) {
			String user = decoded[0];
			String password = decoded[1];

			try {
				ResultSet rs = executeStatement("SELECT UserName,Password,IsAdmin FROM User WHERE UserName='"+user+"' AND Password='"+password+"'");

				while (rs.next()){
					//				usernameDB = rs.getString("UserName");
					//				passwordDB = rs.getString("Password");
					isAdmin = rs.getBoolean("IsAdmin");

					// print the results
					System.out.format("%s, %s, %b\n", usernameDB, passwordDB, isAdmin);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}

			if(isAdmin) {
				LogTools.info("isAdministrator", "ADMIN AUTENTICADO "+user);
				return true;
			}
			else {
				LogTools.info("isAdministrator", "ADMIN NO AUTENTICADO: ");
				return false;
			}
		}
		else return false;
	}

	public static Map<String, FlowDBResponse> getFlowsByUser(String authString) {
		Map<String, FlowDBResponse> flows = new HashMap<String, FlowDBResponse>();
		String idFlow = "";
		String idSwitch = "";
		String idUser = "";

		String user = getUserPassFromCoded(authString)[0];
		try {
			ResultSet rs = executeStatement("SELECT * FROM Flow WHERE IdUser=(SELECT IdUser FROM User WHERE UserName='"+user+"')");
			while (rs.next()){
				idFlow = rs.getString("IdFlow");
				idSwitch = rs.getString("IdSwitch");
				idUser = rs.getString("IdUser");

				flows.put(idFlow, new FlowDBResponse(idFlow, idSwitch, idUser));
				// print the results
				System.out.format("%s, %s, %s\n", idFlow, idSwitch, idUser);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return flows;
		} catch (SQLException e) {
			e.printStackTrace();
			return flows;
		}

		return flows;

	}
	
	
	public static List<MeterDBResponse> getMetersByUser(String authString) {
		List<MeterDBResponse> meters = new ArrayList<MeterDBResponse>();
		String idMeter = "";
		String idSwitch = "";
		String idUser = "";

		String user = getUserPassFromCoded(authString)[0];
		try {
			ResultSet rs = executeStatement("SELECT * FROM Meter WHERE IdUser=(SELECT IdUser FROM User WHERE UserName='"+user+"')");
			while (rs.next()){
				idMeter = rs.getString("IdMeter");
				idSwitch = rs.getString("IdSwitch");
				idUser = rs.getString("IdUser");

				meters.add(new MeterDBResponse(idMeter, idSwitch, idUser));
				// print the results
				System.out.format("%s, %s, %s\n", idMeter, idSwitch, idUser);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return meters;
		} catch (SQLException e) {
			e.printStackTrace();
			return meters;
		}

		return meters;

	}

	private static String[] getUserPassFromCoded(String authString) {
		String[] decoded = null;
		LogTools.info("isAdministrator", "Lets authenticate coded string: "+authString);
		String decodedAuth = "";
		// Header is in the format "Basic 5tyc0uiDat4"
		// We need to extract data before decoding it back to original string
		String[] authParts = authString.split("\\s+");
		LogTools.info("isAdministrator", "authParts[0]: "+authParts[0]+" authParts[1]: "+authParts[1]);
		if(authParts.length > 0) {
			String authInfo = authParts[1];
			LogTools.info("isAdministrator", "authInfo: "+authInfo);
			// Decode the data back to original string
			byte[] bytes;
			try {
				bytes = new BASE64Decoder().decodeBuffer(authInfo);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			LogTools.info("isAdministrator", "bytes: "+bytes);
			decodedAuth = new String(bytes);
			decoded = decodedAuth.split(":");
		}
		return decoded;
	}
	
	public static List<VplsDBResponse> getVplsByUser(String authString) {
		List<VplsDBResponse> vpls = new ArrayList<VplsDBResponse>();
		String idVpls = "";
		String vplsName = "";
		String idUser = "";

		String user = getUserPassFromCoded(authString)[0];
		try {
			ResultSet rs = executeStatement("SELECT * FROM Vpls WHERE IdUser=(SELECT IdUser FROM User WHERE UserName='"+user+"')");
			while (rs.next()){
				idVpls = rs.getString("IdVpls");
				vplsName = rs.getString("VplsName");
				idUser = rs.getString("IdUser");

				vpls.add(new VplsDBResponse(idVpls, vplsName, idUser));
				// print the results
				System.out.format("%s, %s, %s\n", idVpls, vplsName, idUser);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return vpls;
		} catch (SQLException e) {
			e.printStackTrace();
			return vpls;
		}

		return vpls;
		
	}

	public static void addFlowByUserId(Flow flow, String authString) throws ClassNotFoundException, SQLException {
		String[] decoded = getUserPassFromCoded(authString);
		String user = decoded[0];
		String sql = "INSERT INTO Flow "
				+ "(IdFlow, IdSwitch, IdUser) "
				+ "VALUES ('"+flow.getId()+"', '"+flow.getDeviceId()+"', (SELECT IdUser FROM User WHERE UserName='"+user+"'))";
		executeStatement(sql);
	}
	
	public static void deleteFlow(String idFlow, String authString) throws ClassNotFoundException, SQLException {
		String[] decoded = getUserPassFromCoded(authString);
		String user = decoded[0];
		String sql = "DELETE "
				+ "FROM Flow "
				+ "WHERE IdFlow='"+idFlow+"'";
		executeStatement(sql);
	}
	
	private static ResultSet executeStatement(String query) throws ClassNotFoundException, SQLException {
		Class.forName("org.mariadb.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mariadb://"+IP_MARIADB+":"+PORT+"/"+DATABASE+"?user="+USER+"&password="+PASS);
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(query);

		statement.close();
		connection.close();

		return rs;
	}
	
	public static void register(String user, String password, boolean isAdmin) throws ClassNotFoundException, SQLException {
		String sql = "INSERT INTO User "
				+ "(UserName, Password, IsAdmin) "
				+ "VALUES ('"+user+"', '"+password+"', "+(isAdmin ? 1 : 0)+")";
		executeStatement(sql);
	}

	public static void deleteUser(String authString) throws ClassNotFoundException, SQLException {
		String[] decoded = getUserPassFromCoded(authString);
		String user = decoded[0];
		String sql = "DELETE "
				+ "FROM User "
				+ "WHERE UserName='"+user+"'";
		executeStatement(sql);
	}
	
	public static void addMeterByUser(Meter meter, String authString) throws ClassNotFoundException, SQLException {
		String[] decoded = getUserPassFromCoded(authString);
		String user = decoded[0];
		String sql = "INSERT INTO Meter "
				+ "(IdMeter, IdSwitch, IdUser) "
				+ "VALUES ('"+meter.getId()+"', '"+meter.getDeviceId()+"', (SELECT IdUser FROM User WHERE UserName='"+user+"'))";
		executeStatement(sql);
		
	}
	
	public static void deleteMeter(String meterId, String switchId) throws ClassNotFoundException, SQLException {
		String sql = "DELETE "
				+ "FROM Meter "
				+ "WHERE IdMeter='"+meterId+"' AND IdSwitch='"+switchId+"'";
		executeStatement(sql);
	}
	
	public static void addVplsByUser(String vplsName, String authString) throws ClassNotFoundException, SQLException {
		String[] decoded = getUserPassFromCoded(authString);
		String user = decoded[0];
		String sql = "INSERT INTO Vpls "
				+ "(VplsName, IdUser) "
				+ "VALUES ('"+vplsName+"', (SELECT IdUser FROM User WHERE UserName='"+user+"'))";
		executeStatement(sql);
		
	}
	
	public static void deleteVpls(String vplsName, String authString) throws ClassNotFoundException, SQLException {
		String[] decoded = getUserPassFromCoded(authString);
		String user = decoded[0];
		String sql = "DELETE "
				+ "FROM Vpls "
				+ "WHERE VplsName='"+vplsName+"' AND IdUser=(SELECT IdUser FROM User WHERE UserName='"+user+"')";
		executeStatement(sql);
	}
}
