package com.capgemini.rest.geo;

import java.util.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.BasicAuthSecurityHandler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/")
public class GeospatialService {

	private static final String CLASS_NAME = GeospatialService.class.getName();
	private static final Logger LOG = Logger.getLogger(CLASS_NAME);

	/**
	 * Resets all previously generated regions making sure that the service
	 * starts up correctly
	 */
	@Path("initialize")
	@PUT
	public void initializeGeo() {
		System.out.println("Starting to initialize");

		String status = getStatus();
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(status);

		int statusCode = jsonObject.get("status_code").getAsInt();
		System.out.println(statusCode);

		// never started
		if (statusCode == 0) {
			System.out.println("Geospatial Service has never been running...");
			startService();
		}
		// running or stopped or failure
		else {
			switch (statusCode) {
			case 1:
				System.out.println("Geospatial Service is starting");
				break;
			case 2:
				System.out.println("Geospatial Service is running");
				break;
			case 3:
				System.out.println("Geospatial Service is stopped");
				break;
			case 10:
				System.out.println("Geospatial Service failed at runtime");
				break;
			case 11:
				System.out.println("Geospatial Service failed at code generation");
				break;
			default:
				System.out.println("Geospatial Service is in an undefined status");
			}

			// if it is running, stop it
			if (statusCode == 2) {
//				stopService();
			}

			JsonArray customRegions = jsonObject.get("custom_regions").getAsJsonArray();
			JsonArray regularRegions = jsonObject.get("regular_regions").getAsJsonArray();
			// no regions defined yet
			if (customRegions.size() == 0 && regularRegions.size() == 0) {
				System.out.println("No regions defined yet");
			} else {
				System.out.println("There are regions defined. Remove all...");
				for (int i = 0; i < customRegions.size(); i++) {
					String name = customRegions.get(i).getAsJsonObject().get("id").getAsString();
					String regionType = "custom";
					removeRegion(name, regionType);
				}
				for (int i = 0; i < regularRegions.size(); i++) {
					String name = regularRegions.get(i).getAsJsonObject().get("id").getAsString();
					String regionType = "regular";
					removeRegion(name, regionType);
				}

			}
			// then finally start the service
			startService();
		}
	}

	@Path("addRegion")
	@PUT
	public void addRegion(@FormParam(value = "latitude") String latitude,
			@FormParam(value = "longitude") String longitude) {
		System.out.println("defineRegions");

		// validate that the given parameter are okay
		Double.parseDouble(latitude);
		Double.parseDouble(longitude);
		JsonObject jsonObject = new JsonObject();
		JsonArray regions = new JsonArray();
		JsonObject region = new JsonObject();
		jsonObject.add("regions", regions);
		regions.add(region);

		region.addProperty("name", "LocalRegion");
		region.addProperty("region_type", "regular");
		region.addProperty("center_latitude", latitude);
		region.addProperty("center_longitude", longitude);
		region.addProperty("number_of_sides", "10");
		region.addProperty("distance_to_vertices", "150");
		region.addProperty("notifyOnExit", "true");

		// RegularRegion reg = new RegularRegion();
		// reg.setRegion_type("regular");
		// reg.setName("LocalRegion");
		// reg.setNotifyOnExit("true");
		// reg.setCenter_latitude("48.12489");
		// reg.setCenter_longitude("11.65323");
		// reg.setNumber_of_sides("10");
		// reg.setDistance_to_vertices("150");
		// Regions lregions = new Regions();
		// lregions.getRegions().add(reg);

		String url = buildUrl(getServiceData().getAddRegionPath());
		try {
			Resource resource = getResource(url);

			System.out.println("defineRegions - URL " + url);
			System.out.println("defineRegions - Input: " + jsonObject.toString());
			org.apache.wink.client.ClientResponse resp = resource.put(jsonObject.toString());

			System.out.println("defineRegions - " + resp.getStatusCode() + " " + resp.getEntity(String.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Path("start")
	@PUT
	public void startService() {
		System.out.println("startService");
		String iot_org = "4tnf41";

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("mqtt_client_id_input",
				"a:" + iot_org + ":geoInput" + (int) Math.floor(Math.random() * 1000));
		jsonObject.addProperty("mqtt_client_id_notify",
				"a:" + iot_org + ":geoNotify" + (int) Math.floor(Math.random() * 1000));
		// use the API key
		jsonObject.addProperty("mqtt_uid", "a-4tnf41-8zwazdru2i");
		// use the API Authentication token
		jsonObject.addProperty("mqtt_pw", "9pyulb*6Evdon_lb*0");
		// Org plus default
		jsonObject.addProperty("mqtt_uri", iot_org + ".messaging.internetofthings.ibmcloud.com:1883");
		// make sure to have ALL ids in the selected topic (by using the +)
		jsonObject.addProperty("mqtt_input_topics", "iot-2/type/Raspberry/id/+/evt/location/fmt/json");
		// set to any topic, this has to be reused to receive any events from
		// the GeoService
		jsonObject.addProperty("mqtt_notify_topic", "iot-2/type/api/id/geospatial/cmd/geoAlert/fmt/json");
		jsonObject.addProperty("device_id_attr_name", "vin");
		jsonObject.addProperty("latitude_attr_name", "latitude");
		jsonObject.addProperty("longitude_attr_name", "longitude");

		String url = buildUrl(getServiceData().getStartPath());
		try {
			Resource resource = getResource(url);
			resource.contentType(MediaType.APPLICATION_JSON);

			System.out.println("startService - URL " + url);
			System.out.println("startService - Input: " + jsonObject.toString());
			org.apache.wink.client.ClientResponse resp = resource.put(jsonObject.toString());

			System.out.println("startService - " + resp.getStatusCode() + " " + resp.getEntity(String.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Path("status")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getStatus() {
		System.out.println("getStatus");
		String url = buildUrl(getServiceData().getStatusPath());
		String result = null;
		try {
			Resource resource = getResource(url);

			System.out.println("getStatus - URL " + url);
			System.out.println("getStatus - Input: noInput");
			org.apache.wink.client.ClientResponse resp = resource.get();
			System.out.println("getStatus - " + resp.getStatusCode() + " " + resp.getEntity(String.class));
			if (resp.getStatusCode() == 200) {
				result = resp.getEntity(String.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Path("stop")
	public void stopService() {
		System.out.println("stopService");
		String url = buildUrl(getServiceData().getStopPath());

		try {
			Resource resource = getResource(url);

			System.out.println("stopService - URL " + url);
			System.out.println("stopService - Input: noInput");
			org.apache.wink.client.ClientResponse resp = resource.put(new String());
			System.out.println("stopService - " + resp.getStatusCode() + " " + resp.getEntity(String.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Path("removeRegion")
	public void removeRegion(@FormParam(value = "name") String name, @FormParam(value = "type") String type) {
		System.out.println("removeRegion");
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("region_type", type);
		jsonObject.addProperty("region_name", name);

		String url = buildUrl(getServiceData().getRemoveRegionPath());

		try {
			Resource resource = getResource(url);

			System.out.println("removeRegion - URL " + url);
			System.out.println("removeRegion - Input: " + jsonObject.toString());
			org.apache.wink.client.ClientResponse resp = resource.put(jsonObject.toString());
			System.out.println("removeRegion - " + resp.getStatusCode() + " " + resp.getEntity(String.class));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	private String buildUrl(String path) {
		String url = "https://" + getServiceData().getGeoHost() + ":" + getServiceData().getGeoPort() + path;
		return url;
	}

	private Resource getResource(String url) {
		ClientConfig clientConfig = new ClientConfig();
		BasicAuthSecurityHandler basicAuthHandler = new BasicAuthSecurityHandler();

		basicAuthHandler.setUserName(getServiceData().getUserId());
		basicAuthHandler.setPassword(getServiceData().getPassword());
		clientConfig.handlers(basicAuthHandler);

		RestClient client = new RestClient(clientConfig);
		Resource resource = client.resource(url);
		resource.contentType(MediaType.APPLICATION_JSON);

		return resource;
	}

	private GeospatialServiceData getServiceData() {
		return GeospatialServiceData.getInstance();
	}

}
