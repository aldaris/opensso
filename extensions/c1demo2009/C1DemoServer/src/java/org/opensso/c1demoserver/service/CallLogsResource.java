/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensso.c1demoserver.service;

import java.util.Collection;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import com.sun.jersey.api.core.ResourceContext;
import javax.persistence.EntityManager;
import org.opensso.c1demoserver.model.CallLog;
import org.opensso.c1demoserver.model.Phone;
import org.opensso.c1demoserver.converter.CallLogsConverter;
import org.opensso.c1demoserver.converter.CallLogConverter;

/**
 *
 * @author pat
 */

@Path("/callLogs/")
public class CallLogsResource {
    @Context
    protected UriInfo uriInfo;
    @Context
    protected ResourceContext resourceContext;
  
    /** Creates a new instance of CallLogsResource */
    public CallLogsResource() {
    }

    /**
     * Get method for retrieving a collection of CallLog instance in XML format.
     *
     * @return an instance of CallLogsConverter
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public CallLogsConverter get(@QueryParam("start")
    @DefaultValue("0")
    int start, @QueryParam("max")
    @DefaultValue("10")
    int max, @QueryParam("expandLevel")
    @DefaultValue("1")
    int expandLevel, @QueryParam("query")
    @DefaultValue("SELECT e FROM CallLog e")
    String query) {
        PersistenceService persistenceSvc = PersistenceService.getInstance();
        try {
            persistenceSvc.beginTx();
            return new CallLogsConverter(getEntities(start, max, query), uriInfo.getAbsolutePath(), expandLevel);
        } finally {
            persistenceSvc.commitTx();
            persistenceSvc.close();
        }
    }

    /**
     * Post method for creating an instance of CallLog using XML as the input format.
     *
     * @param data an CallLogConverter entity that is deserialized from an XML stream
     * @return an instance of CallLogConverter
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    public Response post(CallLogConverter data) {
        PersistenceService persistenceSvc = PersistenceService.getInstance();
        try {
            persistenceSvc.beginTx();
            EntityManager em = persistenceSvc.getEntityManager();
            CallLog entity = data.resolveEntity(em);
            createEntity(data.resolveEntity(em));
            persistenceSvc.commitTx();
            return Response.created(uriInfo.getAbsolutePath().resolve(entity.getCallId() + "/")).build();
        } finally {
            persistenceSvc.close();
        }
    }

    /**
     * Returns a dynamic instance of CallLogResource used for entity navigation.
     *
     * @return an instance of CallLogResource
     */
    @Path("{callId}/")
    public CallLogResource getCallLogResource(@PathParam("callId")
    Integer id) {
        CallLogResource resource = resourceContext.getResource(CallLogResource.class);
        resource.setId(id);
        return resource;
    }

    /**
     * Returns all the entities associated with this resource.
     *
     * @return a collection of CallLog instances
     */
    protected Collection<CallLog> getEntities(int start, int max, String query) {
        EntityManager em = PersistenceService.getInstance().getEntityManager();
        return em.createQuery(query).setFirstResult(start).setMaxResults(max).getResultList();
    }

    /**
     * Persist the given entity.
     *
     * @param entity the entity to persist
     */
    protected void createEntity(CallLog entity) {
        entity.setCallId(null);
        EntityManager em = PersistenceService.getInstance().getEntityManager();
        em.persist(entity);
        Phone phoneNumberFrom = entity.getPhoneNumberFrom();
        if (phoneNumberFrom != null) {
            phoneNumberFrom.getCallLogCollection().add(entity);
        }
    }
}
