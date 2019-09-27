package rest.gsonobjects.onosside;

import java.util.List;

/**
 * Represent ONOS constraint
 * @author alvaroluismartinez
 *
 */
public class Constraint {

	private boolean inclusive;
	private List<String> types;
	private String type;
	/**
	 * @param inclusive
	 * @param types
	 * @param type
	 */
	public Constraint(boolean inclusive, List<String> types, String type) {
		super();
		this.inclusive = inclusive;
		this.types = types;
		this.type = type;
	}
	
	
}
