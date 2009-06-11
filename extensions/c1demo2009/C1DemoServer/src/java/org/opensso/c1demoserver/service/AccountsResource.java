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
import org.opensso.c1demoserver.model.Account;
import org.opensso.c1demoserver.model.Phone;
import org.opensso.c1demoserver.converter.AccountsConverter;
import org.opensso.c1demoserver.converter.AccountConverter;

/**
 *
 * @author pat
 */

@Path("/accounts/")
public class AccountsResource {
    @Context
    protected UriInfo uriInfo;
    @Context
    protected ResourceContext resourceContext;

    /** Creates a new instance of AccountsResource */
    public AccountsResource() {
    }

    /**
     * Get method for retrieving a collection of Account instance in XML format.
     *
     * @return an instance of AccountsConverter
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public AccountsConverter get(@QueryParam("start")
    @DefaultValue("0")
    int start, @QueryParam("max")
    @DefaultValue("10")
    int max, @QueryParam("expandLevel")
    @DefaultValue("1")
    int expandLevel, @QueryParam("query")
    @DefaultValue("SELECT e FROM Account e")
    String query) {
        PersistenceService persistenceSvc = PersistenceService.getInstance();
        try {
            persistenceSvc.beginTx();
            return new AccountsConverter(getEntities(start, max, query), uriInfo.getAbsolutePath(), expandLevel);
        } finally {
            persistenceSvc.commitTx();
            persistenceSvc.close();
        }
    }

    /**
     * Post method for creating an instance of Account using XML as the input format.
     *
     * @param data an AccountConverter entity that is deserialized from an XML stream
     * @return an instance of AccountConverter
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    public Response post(AccountConverter data) {
        PersistenceService persistenceSvc = PersistenceService.getInstance();
        try {
            persistenceSvc.beginTx();
            EntityManager em = persistenceSvc.getEntityManager();
            Account entity = data.resolveEntity(em);
            createEntity(data.resolveEntity(em));
            persistenceSvc.commitTx();
            return Response.created(uriInfo.getAbsolutePath().resolve(entity.getAccountNumber() + "/")).build();
        } finally {
            persistenceSvc.close();
        }
    }

    /**
     * Returns a dynamic instance of AccountResource used for entity navigation.
     *
     * @return an instance of AccountResource
     */
    @Path("{accountNumber}/")
    public AccountResource getAccountResource(@PathParam("accountNumber")
    String id) {
        AccountResource resource = resourceContext.getResource(AccountResource.class);
        resource.setId(id);
        return resource;
    }

    /**
     * Returns all the entities associated with this resource.
     *
     * @return a collection of Account instances
     */
    protected Collection<Account> getEntities(int start, int max, String query) {
        EntityManager em = PersistenceService.getInstance().getEntityManager();
        return em.createQuery(query).setFirstResult(start).setMaxResults(max).getResultList();
    }

    /**
     * Persist the given entity.
     *
     * @param entity the entity to persist
     */
    protected void createEntity(Account entity) {
        EntityManager em = PersistenceService.getInstance().getEntityManager();
        em.persist(entity);

        for (Phone value : entity.getPhoneCollection()) {
            Account oldEntity = value.getAccountNumber();
            value.setAccountNumber(entity);
            if (oldEntity != null) {
                oldEntity.getPhoneCollection().remove(entity);
            }
        }
    }

}
