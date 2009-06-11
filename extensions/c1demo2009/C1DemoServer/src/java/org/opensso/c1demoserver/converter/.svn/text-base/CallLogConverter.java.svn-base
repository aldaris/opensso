/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensso.c1demoserver.converter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opensso.c1demoserver.model.CallLog;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.ws.rs.core.UriBuilder;
import javax.persistence.EntityManager;
import org.opensso.c1demoserver.model.Phone;
import org.opensso.c1demoserver.service.AccountsResource;

/**
 *
 * @author pat
 */

@XmlRootElement(name = "callLog")
public class CallLogConverter {
    private CallLog entity;
    private URI uri;
    private int expandLevel;
  
    /** Creates a new instance of CallLogConverter */
    public CallLogConverter() {
        entity = new CallLog();
    }

    /**
     * Creates a new instance of CallLogConverter.
     *
     * @param entity associated entity
     * @param uri associated uri
     * @param expandLevel indicates the number of levels the entity graph should be expanded@param isUriExtendable indicates whether the uri can be extended
     */
    public CallLogConverter(CallLog entity, URI uri, int expandLevel, boolean isUriExtendable) {
        this.entity = entity;
        this.uri = (isUriExtendable) ? UriBuilder.fromUri(uri).path(entity.getCallId() + "/").build() : uri;
        this.expandLevel = expandLevel;
        getPhoneNumberFrom();
    }

    /**
     * Creates a new instance of CallLogConverter.
     *
     * @param entity associated entity
     * @param uri associated uri
     * @param expandLevel indicates the number of levels the entity graph should be expanded
     */
    public CallLogConverter(CallLog entity, URI uri, int expandLevel) {
        this(entity, uri, expandLevel, false);
    }

    /**
     * Getter for callTime.
     *
     * @return value for callTime
     */
    @XmlElement
    public Date getCallTime() {
        return (expandLevel > 0) ? entity.getCallTime() : null;
    }

    /**
     * Setter for callTime.
     *
     * @param value the value to set
     */
    public void setCallTime(Date value) {
        entity.setCallTime(value);
    }

    /**
     * Getter for callDurationSecs.
     *
     * @return value for callDurationSecs
     */
    @XmlElement
    public Integer getCallDurationSecs() {
        return (expandLevel > 0) ? entity.getCallDurationSecs() : null;
    }

    /**
     * Setter for callDurationSecs.
     *
     * @param value the value to set
     */
    public void setCallDurationSecs(Integer value) {
        entity.setCallDurationSecs(value);
    }

    /**
     * Getter for callId.
     *
     * @return value for callId
     */
    @XmlElement
    public Integer getCallId() {
        return (expandLevel > 0) ? entity.getCallId() : null;
    }

    /**
     * Setter for callId.
     *
     * @param value the value to set
     */
    public void setCallId(Integer value) {
        entity.setCallId(value);
    }

    /**
     * Getter for phoneNumberFrom.
     *
     * @return value for phoneNumberFrom
     */
    @XmlElement
    public PhoneConverter getPhoneNumberFrom() {
        if (expandLevel > 0) {
            if (entity.getPhoneNumberFrom() != null) {
                return new PhoneConverter(entity.getPhoneNumberFrom(), uri.resolve("phoneNumberFrom/"), expandLevel - 1, false);
            }
        }
        return null;
    }

    /**
     * Setter for phoneNumberFrom.
     *
     * @param value the value to set
     */
    public void setPhoneNumberFrom(PhoneConverter value) {
        entity.setPhoneNumberFrom((value != null) ? value.getEntity() : null);
    }

    /**
     * Getter for phoneNumberTo.
     *
     * @return value for phoneNumberTo
     */
    @XmlElement
    public String getPhoneNumberTo() {
        return (expandLevel > 0) ? entity.getPhoneNumberTo() : null;
    }

    /**
     * Setter for phoneNumberTo.
     *
     * @param value the value to set
     */
    public void setPhoneNumberTo(String value) {
        entity.setPhoneNumberTo(value);
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
     * Returns the CallLog entity.
     *
     * @return an entity
     */
    @XmlTransient
    public CallLog getEntity() {
        if (entity.getCallId() == null) {
            CallLogConverter converter = UriResolver.getInstance().resolve(CallLogConverter.class, uri);
            if (converter != null) {
                entity = converter.getEntity();
            }
        }
        return entity;
    }

    /**
     * Returns the resolved CallLog entity.
     *
     * @return an resolved entity
     */
    public CallLog resolveEntity(EntityManager em) {
        Phone phoneNumberFrom = entity.getPhoneNumberFrom();
        if (phoneNumberFrom != null) {
            entity.setPhoneNumberFrom(em.getReference(Phone.class, phoneNumberFrom.getPhoneNumber()));
        }
        return entity;
    }
}
