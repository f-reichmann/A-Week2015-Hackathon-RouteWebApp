package com.capgemini.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

//Specify the application path to the REST service
@ApplicationPath("/postEmergencyPosition/*")
public class AgentRestApi extends Application {

}
