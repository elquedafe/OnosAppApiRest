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
import architecture.Port;
import rest.database.objects.FlowDBResponse;
import rest.database.objects.MeterDBResponse;
import rest.database.objects.QueueDBResponse;
import rest.database.objects.VplsDBResponse;
import sun.misc.BASE64Decoder;

public class DatabaseTools {
	private static final String IP_MARIADB = "10.0.1.3";
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

//	public static void addFlowByUserId(Flow flow, String authString) throws ClassNotFoundException, SQLException {
//		String[] decoded = getUserPassFromCoded(authString);
//		String user = decoded[0];
//		String sql = "INSERT INTO Flow "
//				+ "(IdFlow, IdSwitch, IdUser) "
//				+ "VALUES ('"+flow.getId()+"', '"+flow.getDeviceId()+"', (SELECT IdUser FROM User WHERE UserName='"+user+"'))";
//		executeStatement(sql);
//	}

	public static void deleteFlow(String idFlow, String authString) throws ClassNotFoundException, SQLException {
		String[] decoded = getUserPassFromCoded(authString);
		String user = decoded[0];
		String sql = "DELETE "
				+ "FROM Flow "
				+ "WHERE IdFlow='"+idFlow+"'";
		executeStatement(sql);
	}

	private static ResultSet executeStatement(String query) throws SQLException, ClassNotFoundException {

		ResultSet rs = null;
		Connection connection = null;
		Statement statement = null;

		try {
			Class.forName("org.mariadb.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mariadb://"+IP_MARIADB+":"+PORT+"/"+DATABASE+"?user="+USER+"&password="+PASS);

			statement = connection.createStatement();
			rs = statement.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		finally {
			if(statement != null)
				statement.close();
			if(connection != null)
				connection.close();
		}



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

//	public static void addMeterByUser(Meter meter, String authString) throws ClassNotFoundException, SQLException {
//		String[] decoded = getUserPassFromCoded(authString);
//		String user = decoded[0];
//		String sql = "INSERT INTO Meter "
//				+ "(IdMeter, IdSwitch, IdUser) "
//				+ "VALUES ('"+meter.getId()+"', '"+meter.getDeviceId()+"', (SELECT IdUser FROM User WHERE UserName='"+user+"'))";
//		executeStatement(sql);
//
//	}

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

	public static Map<String, FlowDBResponse> getFlowsByMeterId(String switchId, String meterId, String authString) {
		String idFlow = "";
		String idSwitch = "";
		String idUser = "";
		String[] decoded = getUserPassFromCoded(authString);
		String user = decoded[0];
		String sql = "SELECT * FROM Flow " + 
				"WHERE IdMeter = '"+ meterId +"' " + 
				"AND IdSwitch = '"+ switchId +"' " + 
				"AND IdUser=(SELECT IdUser FROM User WHERE UserName='"+user+"')";
		Map<String, FlowDBResponse> flows = new HashMap<String, FlowDBResponse>();

		ResultSet rs;
		try {
			rs = executeStatement(sql);
			while (rs.next()){
				idFlow = rs.getString("IdFlow");
				idSwitch = rs.getString("IdSwitch");
				idUser = rs.getString("IdUser");

				flows.put(idFlow, new FlowDBResponse(idFlow, idSwitch, idUser));
				// print the results
				System.out.format("%s, %s, %s\n", idFlow, idSwitch, idUser);
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return flows;
		}

		return flows;
	}

	public static List<MeterDBResponse> getMetersByVpls(String vplsName, String authString) {
		List<MeterDBResponse> meters = new ArrayList<MeterDBResponse>();
		String idMeter = "";
		String idSwitch = "";
		String idUser = "";
		String[] decoded = getUserPassFromCoded(authString);
		String user = decoded[0];
		String sql= "Select * FROM Meter "
				+ "WHERE IdVpls=(SELECT IdVpls FROM Vpls WHERE VplsName='" + vplsName + "') "
				+ "AND IdUser=(SELECT IdUser FROM User WHERE UserName='" + user + "')";
		ResultSet rs;
		try {
			rs = executeStatement(sql);
			while (rs.next()){
				idMeter = rs.getString("IdMeter");
				idSwitch = rs.getString("IdSwitch");
				idUser = rs.getString("IdUser");

				meters.add(new MeterDBResponse(idMeter, idSwitch, idUser));
				// print the results
				System.out.format("%s, %s, %s\n", idMeter, idSwitch, idUser);
			}
		}
		catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return meters;
		}
		return meters;
	}
	
	public static void addFlow(Flow flow, String authString, String idMeter, String vplsName, String idQueue) throws ClassNotFoundException, SQLException {
		String[] decoded = getUserPassFromCoded(authString);
		String user = decoded[0];
		String sql = "INSERT INTO Flow "
				+ "(IdFlow, IdSwitch, IdUser";
		if(idMeter != null && !idMeter.isEmpty()) 
			sql += ", IdMeter";
		
		if(vplsName != null && !vplsName.isEmpty())
			sql += ", IdVpls";
		
		if(idQueue != null && !idQueue.isEmpty())
			sql += ", IdQueue";
		
		sql += ") "
			+ "VALUES ('"+flow.getId()+"', '"+flow.getDeviceId()+"', (SELECT IdUser FROM User WHERE UserName='"+user+"')";
		
		if(idMeter != null && !idMeter.isEmpty()) 
			sql += ", '" + idMeter + "'";
		
		if(vplsName != null && !vplsName.isEmpty())
			sql += ", (SELECT IdVpls FROM Vpls WHERE VplsName='"+vplsName+"')";
		
		if(idQueue != null && !idQueue.isEmpty())
			sql += ", '" + idQueue + "'";
		
		sql += ")";
		executeStatement(sql);
	}

//	public static void addFlowByUserIdQoS(String meterId, Flow flow, String authString)  throws ClassNotFoundException, SQLException {
//		String[] decoded = getUserPassFromCoded(authString);
//		String user = decoded[0];
//		String sql = "INSERT INTO Flow "
//				+ "(IdFlow, IdSwitch, IdUser, IdMeter, IdVpls) "
//				+ "VALUES ('"+flow.getId()+"', '"+flow.getDeviceId()+"', (SELECT IdUser FROM User WHERE UserName='"+user+"'), '"+meterId+"', (SELECT IdVpls FROM Meter WHERE IdMeter='"+meterId+"' AND IdSwitch='"+flow.getDeviceId()+"'))";
//		executeStatement(sql);
//		
//	}

	public static void addMeter(Meter meter, String authString, String vplsName) throws ClassNotFoundException, SQLException {
		String[] decoded = getUserPassFromCoded(authString);
		String user = decoded[0];
		String sql = "INSERT INTO Meter "
				+ "(IdMeter, IdSwitch, IdUser";
		if(vplsName != null && !vplsName.isEmpty())
			sql += ", IdVpls";
		sql += ") VALUES ('"+meter.getId()+"', '"+meter.getDeviceId()+"', (SELECT IdUser FROM User WHERE UserName='"+user+"')";
		
		if(vplsName != null && !vplsName.isEmpty())
			sql += ", (SELECT IdVpls FROM Vpls WHERE VplsName='"+vplsName+"')";
		
		sql += ")";
		executeStatement(sql);
		
	}

	public static Map<String, FlowDBResponse> getFlowsByVplsNoMeter(String vplsName, String authString) {
		Map<String, FlowDBResponse> flows = new HashMap<String, FlowDBResponse>();
		String idFlow = "";
		String idSwitch = "";
		String idUser = "";
		String sql = "";
		String user = getUserPassFromCoded(authString)[0];
		try {
			sql = "SELECT * FROM Flow WHERE IdUser=(SELECT IdUser FROM User WHERE UserName='"+user+"') "
					+ "AND IdVpls=(SELECT IdVpls FROM Vpls WHERE VplsName='"+vplsName+"') "
					+ "AND IdMeter IS NULL";
			ResultSet rs = executeStatement(sql);
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

	public static List<QueueDBResponse> getQueues(String authString) {
		List<QueueDBResponse> queues = new ArrayList<QueueDBResponse>();
		String idQueue = "";
		String idSwitch = "";
		String idQos = "";
		String portName = "";
		String portNumber = "";
		String idUser = "";
		String minRate = "";
		String maxRate = "";
		String burst = "";
		String vplsName = "";
		
		String user = getUserPassFromCoded(authString)[0];
		String sql = "SELECT q.*, v.VplsName FROM Queue AS q LEFT JOIN Vpls AS v ON q.IdVpls=v.IdVpls";
		
		if(!DatabaseTools.isAdministrator(authString)) {
			 sql += " WHERE q.IdUser=(SELECT u.IdUser FROM User AS u WHERE u.UserName='"+user+"')";
		}
		
		try {
			ResultSet rs = executeStatement(sql);
			while (rs.next()){
				idQueue = rs.getString("IdQueue");
				idSwitch = rs.getString("IdSwitch");
				idQos = rs.getString("IdQos");
				portName = rs.getString("PortName");
				portNumber = rs.getString("PortNumber");
				idUser = rs.getString("IdUser");
				minRate = rs.getString("MinRate");
				maxRate = rs.getString("MaxRate");
				burst = rs.getString("Burst");
				vplsName = rs.getString("VplsName");

				queues.add(new QueueDBResponse(idQueue, idSwitch, idQos, portName, portNumber, idUser, minRate, maxRate, burst, vplsName));
				// print the results
				System.out.format("%s, %s, %s\n", idQueue, idSwitch, idUser);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return queues;
		} catch (SQLException e) {
			e.printStackTrace();
			return queues;
		}
		
		return queues;
	}

	public static List<Integer> getAllQueuesIds() {
		List<Integer> queueIds = new ArrayList<Integer>();
		String idQueue = "";

		String sql = "SELECT * FROM Queue";
		
		try {
			ResultSet rs = executeStatement(sql);
			while (rs.next()){
				idQueue = rs.getString("IdQueue");
				queueIds.add(Integer.parseInt(idQueue));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return queueIds;
		} catch (SQLException e) {
			e.printStackTrace();
			return queueIds;
		}
		
		return queueIds;
	}

	public static int getQosIdBySwitchPort(String switchId, String port) {
		int qosId = -1;
		String strIdQos = "";

		String sql = "SELECT IdQos FROM Queue"
				+ " WHERE IdSwitch='"+switchId+"'"
						+ " AND PortNumber='"+port+"'";
		
		try {
			ResultSet rs = executeStatement(sql);
			while (rs.next()){
				strIdQos = rs.getString("IdQos");
				qosId = Integer.parseInt(strIdQos);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return qosId;
		} catch (SQLException e) {
			e.printStackTrace();
			return qosId;
		}
		
		return qosId;
	}

	public static List<Integer> getAllQosIds() {
		List<Integer> qosIds = new ArrayList<Integer>();
		String idQos = "";

		String sql = "SELECT DISTINCT IdQos FROM Queue";
		
		try {
			ResultSet rs = executeStatement(sql);
			while (rs.next()){
				idQos = rs.getString("IdQos");
				qosIds.add(Integer.parseInt(idQos));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return qosIds;
		} catch (SQLException e) {
			e.printStackTrace();
			return qosIds;
		}
		
		return qosIds;
	}

	public static QueueDBResponse getQueue(String authString, String queueId) {
		QueueDBResponse queue = null;
		String idQueue = "";
		String idSwitch = "";
		String idQos = "";
		String portName = "";
		String portNumber = "";
		String idUser = "";
		String minRate = "";
		String maxRate = "";
		String burst = "";
		String vplsName = "";
		String user = getUserPassFromCoded(authString)[0];
		String sql = "SELECT q.*, v.VplsName FROM Queue AS q LEFT JOIN Vpls AS v ON q.IdVpls=v.IdVpls";
		
		if(!DatabaseTools.isAdministrator(authString)) {
			 sql += " WHERE q.IdUser=(SELECT IdUser FROM User WHERE UserName='"+user+"')";
		}
		
		try {
			ResultSet rs = executeStatement(sql);
			while (rs.next()){
				idQueue = rs.getString("IdQueue");
				idSwitch = rs.getString("IdSwitch");
				idQos = rs.getString("IdQos");
				portName = rs.getString("PortName");
				portNumber = rs.getString("PortNumber");
				idUser = rs.getString("IdUser");
				minRate = rs.getString("MinRate");
				maxRate = rs.getString("MaxRate");
				burst = rs.getString("Burst");
				vplsName = rs.getString("VplsName");
				

				queue = new QueueDBResponse(idQueue, idSwitch, idQos, portName, portNumber, idUser, minRate, maxRate, burst, vplsName);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return queue;
		} catch (SQLException e) {
			e.printStackTrace();
			return queue;
		}
		
		return queue;
	}

	public static List<QueueDBResponse> getQueuesBySwitchPort(String authString, String switchId, String port) {
		List<QueueDBResponse> queues = new ArrayList<QueueDBResponse>();
		String idQueue = "";
		String idSwitch = "";
		String idQos = "";
		String portName = "";
		String portNumber = "";
		String idUser = "";
		String minRate = "";
		String maxRate = "";
		String burst = "";
		String vplsName = "";

		String user = getUserPassFromCoded(authString)[0];
		String sql = "SELECT q.*, v.VplsName FROM Queue AS q LEFT JOIN Vpls AS v ON q.IdVpls=v.IdVpls WHERE q.IdSwitch='"+ switchId +"' AND q.PortNumber='"+port+"'";
		
		try {
			ResultSet rs = executeStatement(sql);
			while (rs.next()){
				idQueue = rs.getString("IdQueue");
				idSwitch = rs.getString("IdSwitch");
				idQos = rs.getString("IdQos");
				portName = rs.getString("PortName");
				portNumber = rs.getString("PortNumber");
				idUser = rs.getString("IdUser");
				minRate = rs.getString("MinRate");
				maxRate = rs.getString("MaxRate");
				burst = rs.getString("Burst");
				vplsName = rs.getString("VplsName");

				queues.add(new QueueDBResponse(idQueue, idSwitch, idQos, portName, portNumber, idUser, minRate, maxRate, burst, vplsName));
				// print the results
				System.out.format("%s, %s, %s\n", idQueue, idSwitch, idUser);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return queues;
		} catch (SQLException e) {
			e.printStackTrace();
			return queues;
		}
		
		return queues;
	}

	public static void addQueue(String authString, String queueId, String switchId, String qosId, String portName, String portNumber, String minRate, String maxRate, String burst, String vplsName, String connectionId) throws ClassNotFoundException, SQLException {
		String[] decoded = getUserPassFromCoded(authString);
		String user = decoded[0];
		String sql = "INSERT INTO Queue "
				+ "(IdQueue, IdSwitch, IdQos, PortName, PortNumber, IdUser, MinRate, MaxRate, Burst";
		if(vplsName != null && !vplsName.isEmpty())
			sql += ", IdVpls";
		if(connectionId != null && !connectionId.isEmpty())
			sql += ", IdConnection";
		sql += ") VALUES ('"+queueId+"', '"+switchId+"', '"+qosId+"', '"+portName+"', '"+portNumber+"', (SELECT IdUser FROM User WHERE UserName='"+user+"'), '"+minRate+"', '"+maxRate+"', '"+burst+"'";
		
		if(vplsName != null && !vplsName.isEmpty())
			sql += ", (SELECT IdVpls FROM Vpls WHERE VplsName='"+vplsName+"')";
		if(connectionId != null && !connectionId.isEmpty())
			sql += ", '"+connectionId+"'";
		
		sql += ")";
		executeStatement(sql);
		
	}

	public static int getQosNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void deleteQueue(String authString, String idQueue) throws ClassNotFoundException, SQLException {
		String sql = "DELETE "
				+ "FROM Queue "
				+ "WHERE IdQueue='"+idQueue+"'";
		executeStatement(sql);
		
	}

	public static List<Integer> getAllConnectionIds() {
		List<Integer> connectionIds = new ArrayList<Integer>();
		String connectionId = "";

		String sql = "SELECT DISTINCT IdConnection FROM Queue";
		
		try {
			ResultSet rs = executeStatement(sql);
			while (rs.next()){
				connectionId = rs.getString("IdConnection");
				if(connectionId != null && !connectionId.isEmpty())
					connectionIds.add(Integer.parseInt(connectionId));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return connectionIds;
		} catch (SQLException e) {
			e.printStackTrace();
			return connectionIds;
		}
		
		return connectionIds;
	}

	public static int getDefaultQueueIdBySwitchPort(String switchId, String portNumber) {
		int queueId = -1;
		String strQueueId = null;

		String sql = "SELECT IdQueue FROM Queue WHERE MinRate='0' AND IdSwitch='"+switchId+"' AND PortNumber='"+portNumber+"'";
		
		try {
			ResultSet rs = executeStatement(sql);
			while (rs.next()){
				strQueueId = rs.getString("IdQueue");
				if(strQueueId != null && !strQueueId.isEmpty())
					queueId = Integer.parseInt(strQueueId);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return queueId;
		} catch (SQLException e) {
			e.printStackTrace();
			return queueId;
		}
		
		return queueId;
	}

	public static boolean isMeterInstalled(String vplsName, String idSwitch) {
		boolean is = false;
		String meterId = "";
		String sql = "SELECT IdMeter FROM Meter WHERE IdVpls=(SELECT IdVpls FROM Vpls WHERE VplsName='"+vplsName+"') AND IdSwitch='"+idSwitch+"'";
		
		try {
			ResultSet rs = executeStatement(sql);
			while (rs.next()){
				meterId = rs.getString("IdMeter");
				if(meterId == null || meterId.isEmpty())
					return false;
				else
					return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public static int getVplsSize() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		String sql = "SELECT count(*) AS VplsNumber FROM Vpls";
		ResultSet rs = executeStatement(sql);
		while (rs.next()) {
			int i = rs.getInt("VplsNumber");
			return i;
		}
		return 0;
	}

	public static void updateFlowQueueId(String idFlow, int queueId) throws ClassNotFoundException, SQLException {
		String sql = "UPDATE Flow SET IdQueue='"+queueId+"' WHERE IdFlow='"+idFlow+"'";
		executeStatement(sql);
		
	}
	

//	public static void addFlowByUserIdVpls(String vplsName, Flow flow, String authString) throws ClassNotFoundException, SQLException {
//		String[] decoded = getUserPassFromCoded(authString);
//		String user = decoded[0];
//		String sql = "INSERT INTO Flow "
//				+ "(IdFlow, IdSwitch, IdUser, IdVpls) "
//				+ "VALUES ('"+flow.getId()+"', '"+flow.getDeviceId()+"', (SELECT IdUser FROM User WHERE UserName='"+user+"'), (SELECT IdVpls FROM Vpls WHERE VplsName='"+vplsName+"'))";
//		executeStatement(sql);
//		
//	}
}
