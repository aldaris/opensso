/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensso.c1demoserver.converter;

import java.net.URI;
import org.opensso.c1demoserver.model.Question;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.persistence.EntityManager;

/**
 *
 * @author pat
 */

@XmlRootElement(name = "question")
public class QuestionConverter {
    private Question entity;
    private URI uri;
    private int expandLevel;
  
    /** Creates a new instance of NotificationConverter */
    public QuestionConverter() {
        entity = new Question();
    }

    /**
     * Creates a new instance of NotificationConverter.
     *
     * @param entity associated entity
     * @param uri associated uri
     * @param expandLevel indicates the number of levels the entity graph should be expanded
     * @param isUriExtendable indicates whether the uri can be extended
     */
    public QuestionConverter(Question entity, URI uri, int expandLevel, boolean isUriExtendable) {
        this.entity = entity;
        this.uri = uri;
        this.expandLevel = expandLevel;
    }

    /**
     * Creates a new instance of NotificationConverter.
     *
     * @param entity associated entity
     * @param uri associated uri
     * @param expandLevel indicates the number of levels the entity graph should be expanded
     */
    public QuestionConverter(Question entity, URI uri, int expandLevel) {
        this(entity, uri, expandLevel, false);
    }

    /**
     * Getter for messageText.
     *
     * @return value for messageText
     */
    @XmlElement
    public String getQuestionText() {
        return (expandLevel > 0) ? entity.getQuestionText() : null;
    }

    /**
     * Setter for messageText.
     *
     * @param value the value to set
     */
    public void setQuestionText(String value) {
        entity.setQuestionText(value);
    }

    /**
     * Returns the URI associated with this converter.
     *
     * @return the uri
     */
    @XmlAttribute
    public URI getUri() {
        return uri;
    }

    /**
     * Sets the URI for this reference converter.
     *
     */
    public void setUri(URI uri) {
        this.uri = uri;
    }

    /**
     * Returns the Notification entity.
     *
     * @return an entity
     */
    @XmlTransient
    public Question getEntity() {
        if (entity.getQuestionText() == null) {
            QuestionConverter converter = UriResolver.getInstance().resolve(QuestionConverter.class, uri);
            if (converter != null) {
                entity = converter.getEntity();
            }
        }
        return entity;
    }

    /**
     * Returns the resolved Notification entity.
     *
     * @return an resolved entity
     */
    public Question resolveEntity(EntityManager em) {
        return entity;
    }
}
