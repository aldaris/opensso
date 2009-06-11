/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensso.c1demoserver.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author pat
 */
public class EntitlementShim {
    private static String serviceUrl = "http://localhost:8080/entitlement";
    private static Client client = Client.create();
    private static String realm = "/";

    public static boolean isAllowed(String subject, String action, String resource)
    {
        String url = serviceUrl + "/ws/1/entitlement/decision";

        WebResource webResource = client.resource(url);

        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("realm", realm);
        queryParams.add("subject", subject);
        queryParams.add("action", action);
        queryParams.add("resource", resource);

        String s = webResource.queryParams(queryParams).get(String.class);

        return s.trim().equalsIgnoreCase("allow");
    }
}
