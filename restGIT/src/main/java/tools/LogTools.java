package tools;

public class LogTools {

	public static void error(String method, String message) {
		System.err.println("[ERROR] " + method + ": " + message);
	}
	
	public static void info(String method, String message) {
		System.out.println("[INFO] " + method + ": " + message);
	}
	
	public static void rest(String restMethod, String javaMethod, String message) {
		System.out.println("[" + restMethod + "] " + javaMethod + ": " + message);
	}
	
	public static void rest(String restMethod, String javaMethod) {
		System.out.println("[" + restMethod + "] " + javaMethod);
	}
	
	public static void post(String method, String url, String body) {
		System.out.println("[doPOST] " + method + ": " + url + " Body:\n" + body);
	}
	
	public static void get(String method, String url) {
		System.out.println("[doGET] " + method + ": " + url);
	}
	
	public static void delete(String method, String url) {
		System.out.println("[doDELETE] " + method + ": " + url);
	}
	
}
