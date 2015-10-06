package com.capgemini.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.capgemini.mqtt.AgentEventCallback;
import com.capgemini.mqtt.AgentListener;
import com.capgemini.mqtt.Ambulance;
import com.capgemini.mqtt.RouteCalculator;
import com.google.gson.JsonObject;
import com.ibm.iotf.client.AbstractClient;
import com.ibm.json.java.JSONObject;

//Specify the path to the REST-service
@Path("/")
public class AgentRestService {

	private static final String CLASS_NAME = AbstractClient.class.getName();
	private static final Logger LOG = Logger.getLogger(CLASS_NAME);
	private JSONObject emergencyLocation;
	// Latitude of emergency location
	private Double emergencyLatitude;
	// Longitude of emergency location
	private Double emergencyLongitude;
	// Vehicle identification number (vin) of the closest ambulance to the
	// emergency location
	private String closestAmbulance;

	/*
	 * This method will be called each time an emergency happens
	 * 
	 * @param msg message which is sent to the method by clicking on the map
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void getEmergencyLocation(String msg) {
		// Get the location of emergency from msg
		try {
			emergencyLocation = JSONObject.parse(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Get the latitude of emergency location
		emergencyLatitude = (Double) emergencyLocation.get("latitude");
		// Get the longitude of the emergency location
		emergencyLongitude = (Double) emergencyLocation.get("longitude");

		findTheClosestAmbulance(emergencyLatitude, emergencyLongitude);

	}

	/*
	 * This method finds which ambulance is the closest ambulance to the
	 * emergency location
	 * 
	 * @param emergencyLatitude the latitude of the emergency location
	 * 
	 * @param emergencyLongitude the longitude of the emergency location
	 */

	public void findTheClosestAmbulance(Double emergencyLatitude, Double emergencyLongitude) {

		List<Double> distancesBetweenAmbulanceAndEmergency = new ArrayList<Double>();
		// This hashmap contains the distances from each ambulance to the
		// emergency location
		Map<String, Double> distances = new HashMap<String, Double>();
		// This hashmap contains ambulances
		Map<String, Ambulance> ambulances = AgentEventCallback.ambulances;

		// Find the distance between the current location of each ambulance and
		// the emergency location
		for (Entry<String, Ambulance> ambulance : ambulances.entrySet()) {
			if (ambulance.getValue().getIsFree()) {
				String vin = ambulance.getKey();
				Double currentLatitude = ambulance.getValue().getCurrentLocation().getCurrentLatitude();
				Double currentLongitude = ambulance.getValue().getCurrentLocation().getCurrentLongitude();

				RouteCalculator routeCalculator = new RouteCalculator();
				routeCalculator.calcuateRoute(currentLatitude, currentLongitude, emergencyLatitude, emergencyLongitude);
				Double distance = routeCalculator.getDistance();
				distances.put(vin, distance);
			}

		}

		// Add the distances between each ambulance and the emergency location
		// to the list
		for (Entry<String, Double> entry : distances.entrySet()) {
			distancesBetweenAmbulanceAndEmergency.add(entry.getValue());
		}

		// Find the closest ambulance to the emergency location
		Object minDistance = Collections.min(distancesBetweenAmbulanceAndEmergency);

		// Get the vin (vehicle identification number) of the closest ambulance
		for (Entry<String, Double> entry : distances.entrySet()) {
			if (entry.getValue() == minDistance) {
				closestAmbulance = entry.getKey();
			}
		}

		sendAmbulanceToEmergency();

	}

	/*
	 * This method publishes a command to the device with ID "aca21322819c" and
	 * Type "Raspberry". The published command contains the vin of the closest
	 * ambulance, the latitude and the longitude of the emergency location
	 */
	public void sendAmbulanceToEmergency() {

		String emergencyCommand = "emergencyCommand";

		JsonObject data = new JsonObject();

		data.addProperty("vin", closestAmbulance);
		data.addProperty("emergencyLatitude", Double.toString(emergencyLatitude));
		data.addProperty("emergencyLongitude", Double.toString(emergencyLongitude));

		// Publish a command to the device with the id aca21322819c containing
		// the vin of the ambulance which must be
		// drive to the emergency location and the location of the emergency
		AgentListener.getAgent().getClient().publishCommand("Raspberry", "aca21322819c", emergencyCommand, data, 0);
		
	}

}
