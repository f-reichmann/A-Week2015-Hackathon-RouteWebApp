package com.capgemini.mqtt;

import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ibm.iotf.client.AbstractClient;

public class AgentListener implements ServletContextListener {

	private static Agent agent;
	public static String routeFolderPath;

	private static final String CLASS_NAME = AbstractClient.class.getName();
	private static final Logger LOG = Logger.getLogger(CLASS_NAME);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		LOG.info("Destroyed");
		getAgent().disconnect();

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		LOG.info("Started");
		agent = new Agent();
		ServletContext context = servletContextEvent.getServletContext();
		// Get the path of the folder containing graphhopper files
		routeFolderPath = context.getRealPath("routeFolder");
	

	}

	public static Agent getAgent() {
		return agent;
	}

}
