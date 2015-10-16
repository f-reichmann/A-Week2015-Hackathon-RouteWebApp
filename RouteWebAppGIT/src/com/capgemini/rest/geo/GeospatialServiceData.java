package com.capgemini.rest.geo;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class GeospatialServiceData {

	private static GeospatialServiceData instance;
	
	// TODO exchange all values with your own Geospatial Service information
	private final static String GEO_HOST = "streams-broker.ng.bluemix.net";
	private final static int GEO_PORT = 443;

	private final static String USER_ID = "";
	private final static String PASSWORD = "";

	private final static String START_PATH = "/jax-rs/geo/start/service_instances/370d472a-5443-43f0-8d7d-a0bc80abdb0c/service_bindings/3c69cfef-9ada-4897-8ffa-79d94ff331eb";
	private final static String STATUS_PATH = "/jax-rs/geo/status/service_instances/370d472a-5443-43f0-8d7d-a0bc80abdb0c/service_bindings/3c69cfef-9ada-4897-8ffa-79d94ff331eb";
	private final static String ADD_REGION_PATH = "/jax-rs/geo/addRegion/service_instances/370d472a-5443-43f0-8d7d-a0bc80abdb0c/service_bindings/3c69cfef-9ada-4897-8ffa-79d94ff331eb";
	private final static String REMOVE_REGION_PATH = "/jax-rs/geo/removeRegion/service_instances/370d472a-5443-43f0-8d7d-a0bc80abdb0c/service_bindings/3c69cfef-9ada-4897-8ffa-79d94ff331eb";
	private final static String STOP_PATH = "/jax-rs/geo/stop/service_instances/370d472a-5443-43f0-8d7d-a0bc80abdb0c/service_bindings/3c69cfef-9ada-4897-8ffa-79d94ff331eb";
	private final static String DASHBOARD_PATH = "/jax-rs/dashboard/370d472a-5443-43f0-8d7d-a0bc80abdb0c";
	private final static String RESTART_PATH = "/jax-rs/geo/restart/service_instances/370d472a-5443-43f0-8d7d-a0bc80abdb0c/service_bindings/3c69cfef-9ada-4897-8ffa-79d94ff331eb";

	private String geoHost;
	private int geoPort;

	private String userId;
	private String password;

	private String startPath;
	private String statusPath;
	private String addRegionPath;
	private String removeRegionPath;
	private String stopPath;
	private String dashboardPath;
	private String restartPath;

	public static GeospatialServiceData getInstance() {
		if (instance == null) {
			instance = new GeospatialServiceData();
		}
		return instance;
	}
	
	private GeospatialServiceData() {
		System.out.println("Preparing the  to initialize");
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");

		try {
			// For Bluemix applications
			if (VCAP_SERVICES != null) {
				JSONObject vcap = (JSONObject) JSONObject.parse(VCAP_SERVICES);
				JSONArray geoService = (JSONArray) vcap.get("Geospatial Analytics");
				JSONObject geoServiceInstance = (JSONObject) geoService.get(0);
				JSONObject geoServiceCredentials = (JSONObject) geoServiceInstance.get("credentials");

				setGeoHost((String) geoServiceCredentials.get("geo_host"));
				setGeoPort((int) geoServiceCredentials.get("geo_port"));

				setUserId((String) geoServiceCredentials.get("userid"));
				setPassword((String) geoServiceCredentials.get("password"));

				setStartPath((String) geoServiceCredentials.get("start_path"));
				setStatusPath((String) geoServiceCredentials.get("status_path"));
				setAddRegionPath((String) geoServiceCredentials.get("add_region_path"));
				setRemoveRegionPath((String) geoServiceCredentials.get("remove_region_path"));
				setStopPath((String) geoServiceCredentials.get("stop_path"));
				setDashboardPath((String) geoServiceCredentials.get("dashboard_path"));
				setRestartPath((String) geoServiceCredentials.get("restart_path"));

			}
			// no application on Bluemix
			else {
				setGeoHost(GEO_HOST);
				setGeoPort(GEO_PORT);

				setUserId(USER_ID);
				setPassword(PASSWORD);

				setStartPath(START_PATH);
				setStatusPath(STATUS_PATH);
				setAddRegionPath(ADD_REGION_PATH);
				setRemoveRegionPath(REMOVE_REGION_PATH);
				setStopPath(STOP_PATH);
				setDashboardPath(DASHBOARD_PATH);
				setRestartPath(RESTART_PATH);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the geoHost
	 */
	public String getGeoHost() {
		return geoHost;
	}

	/**
	 * @param geoHost
	 *            the geoHost to set
	 */
	public void setGeoHost(String geoHost) {
		this.geoHost = geoHost;
	}

	/**
	 * @return the geoPort
	 */
	public int getGeoPort() {
		return geoPort;
	}

	/**
	 * @param geoPort
	 *            the geoPort to set
	 */
	public void setGeoPort(int geoPort) {
		this.geoPort = geoPort;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the startPath
	 */
	public String getStartPath() {
		return startPath;
	}

	/**
	 * @param startPath
	 *            the startPath to set
	 */
	public void setStartPath(String startPath) {
		this.startPath = startPath;
	}

	/**
	 * @return the statusPath
	 */
	public String getStatusPath() {
		return statusPath;
	}

	/**
	 * @param statusPath
	 *            the statusPath to set
	 */
	public void setStatusPath(String statusPath) {
		this.statusPath = statusPath;
	}

	/**
	 * @return the addRegionPath
	 */
	public String getAddRegionPath() {
		return addRegionPath;
	}

	/**
	 * @param addRegionPath
	 *            the addRegionPath to set
	 */
	public void setAddRegionPath(String addRegionPath) {
		this.addRegionPath = addRegionPath;
	}

	/**
	 * @return the removeRegionPath
	 */
	public String getRemoveRegionPath() {
		return removeRegionPath;
	}

	/**
	 * @param removeRegionPath
	 *            the removeRegionPath to set
	 */
	public void setRemoveRegionPath(String removeRegionPath) {
		this.removeRegionPath = removeRegionPath;
	}

	/**
	 * @return the stopPath
	 */
	public String getStopPath() {
		return stopPath;
	}

	/**
	 * @param stopPath
	 *            the stopPath to set
	 */
	public void setStopPath(String stopPath) {
		this.stopPath = stopPath;
	}

	/**
	 * @return the dashboardPath
	 */
	public String getDashboardPath() {
		return dashboardPath;
	}

	/**
	 * @param dashboardPath
	 *            the dashboardPath to set
	 */
	public void setDashboardPath(String dashboardPath) {
		this.dashboardPath = dashboardPath;
	}

	/**
	 * @return the restartPath
	 */
	public String getRestartPath() {
		return restartPath;
	}

	/**
	 * @param restartPath
	 *            the restartPath to set
	 */
	public void setRestartPath(String restartPath) {
		this.restartPath = restartPath;
	}

}
