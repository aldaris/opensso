/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.identity.oauth.service.resources;

import com.sun.identity.oauth.service.persistence.Consumer;
import com.sun.identity.oauth.service.util.UniqueRandomString;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * Endpoint for Service Consumer Registration
 *
 * @author Hubert A. Le Van Gong <hubert.levangong at Sun.COM>
 */
@Path(PathDefs.ConsumerRegistrationPath)
public class ConsumerRequest {
    @Context
    private UriInfo context;

    /** Creates a new instance of ConsumersRegistration */
    public ConsumerRequest() {
    }



    /**
     * POST method for registering a Service Consumer
     * and obtaining corresponding consumer key & secret.
     *
     * @param content {@link String} containing the service consumer's description.
     * This description takes the form of name=value pairs separated by &.
     * The following parameters are supported:
     * <OL>
     * <LI>name - the service consumer's name.</LI>
     * <LI>icon - the service consumer's URI for its icon (MUST be unique).</LI>
     * <LI>service - the service consumer's URI for its service</LI>
     * <LI>rsapublickey - (optional) the RSA public key of the Service Consumer.</LI>
     * </OL>
     * <p>
     *
     * Example of string:
     * <pre>
     *  name=Service XYZ&icon=http://www.example.com/icon.jpg&service=http://www.example.com
     * </pre>
     *
     *
     * @return an HTTP response with content of the created resource.
     * The location URI is set to the newly created OAuth consumer key.
     * The body of the response is of the form:
     * <pre>
     * consumer_key=http://serviceprovider/0123456762121
     * consumer_secret=12345633
     * </pre>
     * Both values are URL encoded.
     */
    @POST
    @Consumes("application/x-www-form-urlencoded")
    public Response postConsumerRegistrations(String content) {
        PersistenceService service = PersistenceService.getInstance();
        try {
            service.beginTx();

            // We might want to check whether this Consumer already exists,
            // but for now we let consumers register ad nauseum...
            Consumer cons = new Consumer();

            StringTokenizer tokenizer = new StringTokenizer(content, "&");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.contains("=")) {
                    String kv[] = token.split("=");
                    if (kv.length > 2) {
                        throw new WebApplicationException(new Throwable("Authorization header element has more than one value."), 400);
                    }
                    String pname = kv[0];
                    String pval = "";
                    if (kv.length == 2)
                        pval = kv[1];
                    if (pname.equalsIgnoreCase("name"))
                            cons.setConsName(URLDecoder.decode(pval));
                    else
                        if (pname.equalsIgnoreCase("icon"))
                            cons.setConsIconUri(URLDecoder.decode(pval));
                        else
                            if (pname.equalsIgnoreCase("service")) {
                                Consumer tmpc = getConsumerByUri(Consumer.class, URLDecoder.decode(pval));
                                if (tmpc != null)
                                    throw new WebApplicationException(new Throwable("Service already registered."));
                                cons.setConsSvcUri(URLDecoder.decode(pval));
                            } else
                                if (pname.equalsIgnoreCase("rsapublickey"))
                                    cons.setConsRsakey(URLDecoder.decode(pval));
                }
            }

            //URI consKeyURI = URI.create(endOfConsKey.toString());
            String baseUri = context.getBaseUri().toString();
            if (baseUri.endsWith("/"))
                    baseUri = baseUri.substring(0, baseUri.length() - 1);
            URI loc = URI.create(baseUri + PathDefs.ConsumersPath + "/" + new UniqueRandomString().getString());
            String consKey =  loc.toString();
            cons.setConsKey(consKey);

            cons.setConsSecret(new UniqueRandomString().getString());

            service.persistEntity(cons);
            service.commitTx();

            String resp = "consumer_key=" + URLEncoder.encode(cons.getConsKey()) + "&consumer_secret=" + URLEncoder.encode(cons.getConsSecret());
            return Response.created(loc).entity(resp).type(MediaType.APPLICATION_FORM_URLENCODED).build();
        } finally {
            service.close();
        }
    }

    protected <T> T getConsumerByUri(Class<T> type, String uri) {
        try {
            return (T) PersistenceService.getInstance().createQuery("SELECT e FROM " + type.getSimpleName()+" e where e.consSvcUri = :uri").setParameter("uri", uri).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

}