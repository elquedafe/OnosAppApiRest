package rest.gsonobjects.onosside;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents a intent request to ONOS
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class IntentOnosRequest {
	private String type;
	private String appId;
	private Map<String, LinkedList<LinkedHashMap<String,Object>>> selector;
	private int priority;
	private List<Constraint> constraints;
	private Point ingressPoint;
	private Point egressPoint;
	
	public IntentOnosRequest() {
		this.type = "PointToPointIntent";
		this.appId = "org.onosproject.fwd";
		this.priority = 1000;
		this.constraints = new ArrayList<Constraint>();
		List<String> types = new ArrayList<String>(Arrays.asList("OPTICAL"));
		constraints.add(new Constraint(false, types, "LinkTypeConstraint"));
	}

	/**
	 * @return the selector
	 */
	public Map<String, LinkedList<LinkedHashMap<String, Object>>> getSelector() {
		return selector;
	}

	/**
	 * @param selector the selector to set
	 */
	public void setSelector(Map<String, LinkedList<LinkedHashMap<String, Object>>> selector) {
		this.selector = selector;
	}

	/**
	 * @return the ingressPoint
	 */
	public Point getIngressPoint() {
		return ingressPoint;
	}

	/**
	 * @param ingressPoint the ingressPoint to set
	 */
	public void setIngressPoint(Point ingressPoint) {
		this.ingressPoint = ingressPoint;
	}

	/**
	 * @return the egressPoint
	 */
	public Point getEgressPoint() {
		return egressPoint;
	}

	/**
	 * @param egressPoint the egressPoint to set
	 */
	public void setEgressPoint(Point egressPoint) {
		this.egressPoint = egressPoint;
	}
	
	
	

}
