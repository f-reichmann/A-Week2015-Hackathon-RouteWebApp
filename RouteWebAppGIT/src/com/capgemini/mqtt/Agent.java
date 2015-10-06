package com.capgemini.mqtt;

import java.util.Properties;
import java.util.logging.Logger;
import com.ibm.iotf.client.AbstractClient;
import com.ibm.iotf.client.app.ApplicationClient;

public class Agent {

	// Enter API-Key and API-Token to connect to the Internet of Things (IoT)
	// Foundation
	private static final String LOCAL_API_KEY = "a-ozz0rt-nsphpzxtfy";
	private static final String LOCAL_API_TOKEN = ")i)4zQZB7vDsXDKo_6";

	private String id = "App" + (Math.random() * 10000);
	private ApplicationClient client = null;

	private static final String CLASS_NAME = AbstractClient.class.getName();
	private static final Logger LOG = Logger.getLogger(CLASS_NAME);

	public Agent() {
		LOG.info("Initiate Application");

		// Initiate application client
		Properties options = new Properties();
		ApplicationClient localClient = null;

		String apiKey = LOCAL_API_KEY;
		String apiToken = LOCAL_API_TOKEN;

		options.put("id", id);
		options.put("auth-method", "apikey");
		options.put("auth-key", apiKey);
		options.put("auth-token", apiToken);

		try {

			// Create a new application client
			localClient = new ApplicationClient(options);
			// Connect to IBM Internet of Things Foundation
			localClient.connect();
			localClient.setEventCallback(new AgentEventCallback(this));
			// Subscribe to device events
			localClient.subscribeToDeviceEvents();
			setClient(localClient);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ApplicationClient getClient() {
		return client;
	}

	public void setClient(ApplicationClient client) {
		this.client = client;
	}

	// This method disconnects the application client
	public void disconnect() {
		try {
			if (getClient() != null) {
				getClient().disconnect();
			} else {
				LOG.info("No ApplicationClient available, no disconnect required");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
