/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.identity.oauth.service.resources;

import com.sun.identity.oauth.service.persistence.RequestToken;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Hubert A. Le Van Gong <hubert.levangong at Sun.COM>
 */
@Path(PathDefs.NoBrowserAuthorizationPath)
public class NoBrowserAuthorization {
    @Context
    private UriInfo context;


    /** Creates a new instance of AuthorizationFactory */
    public NoBrowserAuthorization() {
    }

    /**
     * GET method to authenticate & obtain user's consent.
     * This endpoint does not use callback and does not rely on
     * browser-based authorization but rather submits the credentials
     * to a predefined OpenSSO endpoint.
     *
     * @param username (@String) is the user name to authenticate at the OpenSSO
     * instance
     * @param password (@String) is the user's password
     * @param reqtoken (@String) is the request token to authorize
     * @return 200 in case of success, 400 otherwise.
     */
    @GET
    @Consumes("application/x-www-form-urlencoded")
    public Response NoBrowserAuthorization(
            @QueryParam("username") String name,
            @QueryParam("password") String pwd,
            @QueryParam("request_token") String reqtoken) {
        try {
            if ((name == null) || (pwd == null) || (reqtoken == null)) {
                throw new WebApplicationException(new Throwable("Request invalid."));
            }
            String userid = null;
            //URL url = new URL(PathDefs.OpenSSOAuthenticationEndpoint);
            URL url = new URL("http://www.cnn.com");
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            String msg = "username=" + URLEncoder.encode (name) +
                    "&password=" + URLEncoder.encode (pwd);
            dos.writeBytes(msg);
            dos.flush();
            dos.close();
            BufferedReader bufr = new BufferedReader(new InputStreamReader((InputStream) conn.getContent()));
            String line;
            while ((line = bufr.readLine()) != null) {
                int index = line.indexOf("token");
                if (index != -1)
                    userid = line.substring(9);
            }
            if (userid != null) {
                PersistenceService service = PersistenceService.getInstance();
                try {
                    service.beginTx();
                    RequestToken rt = getReqtokenByURI(RequestToken.class, reqtoken);
                    if (rt == null)
                        throw new WebApplicationException(new Throwable("Request token invalid."));
                    rt.setReqtPpalid(userid);
                    service.persistEntity(rt);
                    service.commitTx();

                    return Response.ok().build();
                } finally {
                    service.close();
                }
            } else {
                return Response.status(400).build();
            }
        } catch (IOException ex) {
            Logger.getLogger(NoBrowserAuthorization.class.getName()).log(Level.SEVERE, null, ex);
            return Response.serverError().build();
        }
    }


    protected <T> T getReqtokenByURI(Class<T> type, String uri) {
        try {
            return (T) PersistenceService.getInstance().createQuery("SELECT e FROM " + type.getSimpleName() + " e where e.reqtUri = :uri").setParameter("uri", uri).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

}