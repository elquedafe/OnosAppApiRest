package architecture;

import java.util.List;

/**
 * Represents a network VPLS
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class Vpls {
	private String name;
	private List<String> interfaces;
	private String encapsulation;

	/**
	 * VPLS constructor
	 * @param name VPLS name
	 * @param interfaces hosts list
	 */
	public Vpls(String name, List<String> interfaces) {
		super();
		this.name = name;
		this.interfaces = interfaces;
	}
	/**
	 * Get VPLS name
	 * @return VPLS name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Set VPLS name
	 * @param name VPLS name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Get hosts list
	 * @return hosts list
	 */
	public List<String> getInterfaces() {
		return interfaces;
	}
	/**
	 * Set hosts list
	 * @param interfaces hosts list
	 */
	public void setInterfaces(List<String> interfaces) {
		this.interfaces = interfaces;
	}
	/**
	 * Get encapsulation
	 * @return encapsulation
	 */
	public String getEncapsulation() {
		return encapsulation;
	}
	/**
	 * Set encapsulation
	 * @param encapsulation encapsulation
	 */
	public void setEncapsulation(String encapsulation) {
		this.encapsulation = encapsulation;
	}


}
