/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensso.c1demoserver.service;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import com.sun.jersey.api.core.ResourceContext;
import javax.ws.rs.WebApplicationException;
import javax.persistence.NoResultException;
import javax.persistence.EntityManager;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.opensso.c1demoserver.model.Account;
import org.opensso.c1demoserver.model.Phone;
import org.opensso.c1demoserver.converter.AccountConverter;

/**
 *
 * @author pat
 */

public class AccountResource {
    @Context
    protected UriInfo uriInfo;
    @Context
    protected ResourceContext resourceContext;
    protected String id;

    /** Creates a new instance of AccountResource */
    public AccountResource() {
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get method for retrieving an instance of Account identified by id in XML format.
     *
     * @param id identifier for the entity
     * @return an instance of AccountConverter
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public AccountConverter get(@QueryParam("expandLevel")
    @DefaultValue("1")
    int expandLevel,
    @Context
    HttpServletRequest request) {
        PersistenceService persistenceSvc = PersistenceService.getInstance();
        try {
            persistenceSvc.beginTx();
            
            AccountConverter ac = new AccountConverter(getEntity(), uriInfo.getAbsolutePath(), expandLevel);
            
            // Need to create a normalized account URL, as we might be being accessed as a subresource - i.e.
            // with a URL such as http://localhost:8080/C1DemoServer/resources/phones/1112223333/accountNumber/
            String protocol = request.isSecure() ? "https" : "http";
            String resource = protocol + "://" + request.getServerName();
            int port = request.getServerPort();
            if ( ( protocol.equals("http") && port != 80 ) || 
                ( protocol.equals("https") && port != 443 ) ) {
                resource += ":" + Integer.toString(port);
            }
            resource += request.getContextPath() + request.getServletPath() + "/accounts/" + ac.getAccountNumber() + "/";
            
            // resource should now be in the form http://localhost:8080/C1DemoServer/resources/accounts/123456789012345

            if ( ! EntitlementShim.isAllowed(request.getUserPrincipal().getName(), request.getMethod(),
                resource)) {
                throw new WebApplicationException(Response.Status.FORBIDDEN);
            }

            return ac;
        } finally {
            PersistenceService.getInstance().close();
        }
    }

    /**
     * Put method for updating an instance of Account identified by id using XML as the input format.
     *
     * @param id identifier for the entity
     * @param data an AccountConverter entity that is deserialized from a XML stream
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    public void put(AccountConverter data) {
        PersistenceService persistenceSvc = PersistenceService.getInstance();

        try {
            persistenceSvc.beginTx();
            EntityManager em = persistenceSvc.getEntityManager();
            updateEntity(getEntity(), data.resolveEntity(em));
            persistenceSvc.commitTx();
        } finally {
            persistenceSvc.close();
        }
    }

    /**
     * Delete method for deleting an instance of Account identified by id.
     *
     * @param id identifier for the entity
     */
    @DELETE
    public void delete() {
        PersistenceService persistenceSvc = PersistenceService.getInstance();
        try {
            persistenceSvc.beginTx();
            deleteEntity(getEntity());
            persistenceSvc.commitTx();
        } finally {
            persistenceSvc.close();
        }
    }

    /**
     * Returns an instance of Account identified by id.
     *
     * @param id identifier for the entity
     * @return an instance of Account
     */
    protected Account getEntity() {
        EntityManager em = PersistenceService.getInstance().getEntityManager();
        try {
            return (Account) em.createQuery("SELECT e FROM Account e where e.accountNumber = :accountNumber").setParameter("accountNumber", id).getSingleResult();
        } catch (NoResultException ex) {
            throw new WebApplicationException(new Throwable("Resource for " + uriInfo.getAbsolutePath() + " does not exist."), 404);
        }
    }

    /**
     * Updates entity using data from newEntity.
     *
     * @param entity the entity to update
     * @param newEntity the entity containing the new data
     * @return the updated entity
     */
    protected Account updateEntity(Account entity, Account newEntity) {
        EntityManager em = PersistenceService.getInstance().getEntityManager();
        Collection<Phone> phoneCollection = entity.getPhoneCollection();
        Collection<Phone> phoneCollectionNew = newEntity.getPhoneCollection();
        entity = em.merge(newEntity);
        for (Phone value : phoneCollection) {
            if (!phoneCollectionNew.contains(value)) {
                throw new WebApplicationException(new Throwable("Cannot remove items from phoneCollection"));
            }
        }
        for (Phone value : phoneCollectionNew) {
            if (!phoneCollection.contains(value)) {
                Account oldEntity = value.getAccountNumber();
                value.setAccountNumber(entity);
                if (oldEntity != null && !oldEntity.equals(entity)) {
                    oldEntity.getPhoneCollection().remove(value);
                }
            }
        }
        return entity;
    }

    /**
     * Deletes the entity.
     *
     * @param entity the entity to deletle
     */
    protected void deleteEntity(Account entity) {
        EntityManager em = PersistenceService.getInstance().getEntityManager();
        if (!entity.getPhoneCollection().isEmpty()) {
            throw new WebApplicationException(new Throwable("Cannot delete entity because phoneCollection is not empty."));
        }
        em.remove(entity);
    }

    /**
     * Returns a dynamic instance of PhonesResource used for entity navigation.
     *
     * @param id identifier for the parent entity
     * @return an instance of PhonesResource
     */
    @Path("phoneCollection/")
    public PhonesResource getPhoneCollectionResource() {
        PhoneCollectionResourceSub resource = resourceContext.getResource(PhoneCollectionResourceSub.class);
        resource.setParent(getEntity());
        return resource;
    }

    public static class PhoneCollectionResourceSub extends PhonesResource {

        private Account parent;

        public void setParent(Account parent) {
            this.parent = parent;
        }

        @Override
        protected Collection<Phone> getEntities(int start, int max, String query) {
            Collection<Phone> result = new java.util.ArrayList<Phone>();
            int index = 0;
            for (Phone e : parent.getPhoneCollection()) {
                if (index >= start && (index - start) < max) {
                    result.add(e);
                }
                index++;
            }
            return result;
        }
    }
}
