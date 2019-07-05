package rest.database.objects;

public class VplsDBResponse {
	private String idVpls;
	private String vplsName;
	private String idUser;
	/**
	 * @param idVpls
	 * @param vplsName
	 * @param idUser
	 */
	public VplsDBResponse(String idVpls, String vplsName, String idUser) {
		super();
		this.idVpls = idVpls;
		this.vplsName = vplsName;
		this.idUser = idUser;
	}
	/**
	 * @return the idVpls
	 */
	public String getIdVpls() {
		return idVpls;
	}
	/**
	 * @param idVpls the idVpls to set
	 */
	public void setIdVpls(String idVpls) {
		this.idVpls = idVpls;
	}
	/**
	 * @return the vplsName
	 */
	public String getVplsName() {
		return vplsName;
	}
	/**
	 * @param vplsName the vplsName to set
	 */
	public void setVplsName(String vplsName) {
		this.vplsName = vplsName;
	}
	/**
	 * @return the idUser
	 */
	public String getIdUser() {
		return idUser;
	}
	/**
	 * @param idUser the idUser to set
	 */
	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}
	

}
