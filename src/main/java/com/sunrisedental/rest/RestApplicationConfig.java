package com.sunrisedental.rest;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;


@ApplicationPath("/api")
public class RestApplicationConfig extends ResourceConfig {

    public RestApplicationConfig() {
        register(AppointmentResource.class);
        register(BillResource.class);
        register(JacksonFeature.class);
    }
}
