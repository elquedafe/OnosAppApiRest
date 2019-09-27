package tools;

/**
 * Logger with format
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class LogTools {
	/**
	 * Error output
	 * @param method method where output is
	 * @param message info message
	 */
	public static void error(String method, String message) {
		System.err.println("[ERROR] " + method + ": " + message);
	}
	/**
	 * Info output
	 * @param method method where output is
	 * @param message info message
	 */
	public static void info(String method, String message) {
		System.out.println("[INFO] " + method + ": " + message);
	}
	
	/**
	 * Rest output log
	 * @param restMethod rest method
	 * @param javaMethod java method
	 * @param message info
	 */
	public static void rest(String restMethod, String javaMethod, String message) {
		System.out.println("[" + restMethod + "] " + javaMethod + ": " + message);
	}
	
	/**
	 * Rest output log
	 * @param restMethod rest method
	 * @param javaMethod java method
	 */
	public static void rest(String restMethod, String javaMethod) {
		System.out.println("[" + restMethod + "] " + javaMethod);
	}
	
	/**
	 * Rest POST log
	 * @param method method name
	 * @param url url to post
	 * @param body json
	 */
	public static void post(String method, String url, String body) {
		System.out.println("[doPOST] " + method + ": " + url + " Body:\n" + body);
	}
	
	/**
	 * Rest GET log
	 * @param method method name
	 * @param url url to get
	 */
	public static void get(String method, String url) {
		System.out.println("[doGET] " + method + ": " + url);
	}
	
	/**
	 * Rest DELETE log
	 * @param method method name
	 * @param url url to delete
	 */
	public static void delete(String method, String url) {
		System.out.println("[doDELETE] " + method + ": " + url);
	}
	
}
