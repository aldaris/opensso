/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensso.c1demoserver.service;

import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import com.sun.jersey.api.core.ResourceContext;
import java.util.ArrayList;
import org.opensso.c1demoserver.model.Question;
import org.opensso.c1demoserver.converter.QuestionsConverter;

/**
 *
 * @author pat
 */

public class QuestionsResource {
    @Context
    protected UriInfo uriInfo;
    @Context
    protected ResourceContext resourceContext;
  
    /** Creates a new instance of QuestionsResource */
    public QuestionsResource() {
    }

    /**
     * Get method for retrieving a collection of Question instance in XML format.
     *
     * @return an instance of QuestionsConverter
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public QuestionsConverter get(@QueryParam("start")
    @DefaultValue("0")
    int start, @QueryParam("max")
    @DefaultValue("10")
    int max, @QueryParam("expandLevel")
    @DefaultValue("1")
    int expandLevel, @QueryParam("query")
//  @DefaultValue("SELECT e FROM Question e")
    String query) {
        PersistenceService persistenceSvc = PersistenceService.getInstance();
        try {
            persistenceSvc.beginTx();
            return new QuestionsConverter(getEntities(start, max, query), uriInfo.getAbsolutePath(), expandLevel);
        } finally {
            persistenceSvc.commitTx();
            persistenceSvc.close();
        }
    }

    /**
     * Returns all the entities associated with this resource.
     *
     * @return a collection of Question instances
     */
    protected Collection<Question> getEntities(int start, int max, String query) {
        /*
        EntityManager em = PersistenceService.getInstance().getEntityManager();
        return em.createQuery(query).setFirstResult(start).setMaxResults(max).getResultList();
         */
        return new ArrayList<Question>();
    }
}
