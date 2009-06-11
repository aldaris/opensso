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
import org.opensso.c1demoserver.model.Notification;
import org.opensso.c1demoserver.model.Phone;
import org.opensso.c1demoserver.converter.NotificationsConverter;
import org.opensso.c1demoserver.converter.NotificationConverter;

/**
 *
 * @author pat
 */

@Path("/notifications/")
public class NotificationsResource {
    @Context
    protected UriInfo uriInfo;
    @Context
    protected ResourceContext resourceContext;
  
    /** Creates a new instance of NotificationsResource */
    public NotificationsResource() {
    }

    /**
     * Get method for retrieving a collection of Notification instance in XML format.
     *
     * @return an instance of NotificationsConverter
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public NotificationsConverter get(@QueryParam("start")
    @DefaultValue("0")
    int start, @QueryParam("max")
    @DefaultValue("10")
    int max, @QueryParam("expandLevel")
    @DefaultValue("1")
    int expandLevel, @QueryParam("query")
    @DefaultValue("SELECT e FROM Notification e")
    String query) {
        PersistenceService persistenceSvc = PersistenceService.getInstance();
        try {
            persistenceSvc.beginTx();
            return new NotificationsConverter(getEntities(start, max, query), uriInfo.getAbsolutePath(), expandLevel);
        } finally {
            persistenceSvc.commitTx();
            persistenceSvc.close();
        }
    }

    /**
     * Post method for creating an instance of Notification using XML as the input format.
     *
     * @param data an NotificationConverter entity that is deserialized from an XML stream
     * @return an instance of NotificationConverter
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    public Response post(NotificationConverter data) {
        PersistenceService persistenceSvc = PersistenceService.getInstance();
        try {
            persistenceSvc.beginTx();
            EntityManager em = persistenceSvc.getEntityManager();
            Notification entity = data.resolveEntity(em);
            createEntity(data.resolveEntity(em));
            persistenceSvc.commitTx();
            return Response.created(uriInfo.getAbsolutePath().resolve(entity.getNotificationId() + "/")).build();
        } finally {
            persistenceSvc.close();
        }
    }

    /**
     * Returns a dynamic instance of NotificationResource used for entity navigation.
     *
     * @return an instance of NotificationResource
     */
    @Path("{notificationId}/")
    public NotificationResource getNotificationResource(@PathParam("notificationId")
    Integer id) {
        NotificationResource resource = resourceContext.getResource(NotificationResource.class);
        resource.setId(id);
        return resource;
    }

    /**
     * Returns all the entities associated with this resource.
     *
     * @return a collection of Notification instances
     */
    protected Collection<Notification> getEntities(int start, int max, String query) {
        EntityManager em = PersistenceService.getInstance().getEntityManager();
        return em.createQuery(query).setFirstResult(start).setMaxResults(max).getResultList();
    }

    /**
     * Persist the given entity.
     *
     * @param entity the entity to persist
     */
    protected void createEntity(Notification entity) {
        entity.setNotificationId(null);
        EntityManager em = PersistenceService.getInstance().getEntityManager();
        em.persist(entity);
        Phone phoneNumber = entity.getPhoneNumber();
        if (phoneNumber != null) {
            phoneNumber.getNotificationCollection().add(entity);
        }
    }
}
