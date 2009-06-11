/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensso.c1demoserver.converter;

import java.net.URI;
import java.util.Collection;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.ArrayList;
import org.opensso.c1demoserver.model.CallLog;

/**
 *
 * @author pat
 */

@XmlRootElement(name = "callLogs")
public class CallLogsConverter {
    private Collection<CallLog> entities;
    private Collection<CallLogConverter> items;
    private URI uri;
    private int expandLevel;
  
    /** Creates a new instance of CallLogsConverter */
    public CallLogsConverter() {
    }

    /**
     * Creates a new instance of CallLogsConverter.
     *
     * @param entities associated entities
     * @param uri associated uri
     * @param expandLevel indicates the number of levels the entity graph should be expanded
     */
    public CallLogsConverter(Collection<CallLog> entities, URI uri, int expandLevel) {
        this.entities = entities;
        this.uri = uri;
        this.expandLevel = expandLevel;
        getCallLog();
    }

    /**
     * Returns a collection of CallLogConverter.
     *
     * @return a collection of CallLogConverter
     */
    @XmlElement
    public Collection<CallLogConverter> getCallLog() {
        if (items == null) {
            items = new ArrayList<CallLogConverter>();
        }
        if (entities != null) {
            items.clear();
            for (CallLog entity : entities) {
                items.add(new CallLogConverter(entity, uri, expandLevel, true));
            }
        }
        return items;
    }

    /**
     * Sets a collection of CallLogConverter.
     *
     * @param a collection of CallLogConverter to set
     */
    public void setCallLog(Collection<CallLogConverter> items) {
        this.items = items;
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
     * Returns a collection CallLog entities.
     *
     * @return a collection of CallLog entities
     */
    @XmlTransient
    public Collection<CallLog> getEntities() {
        entities = new ArrayList<CallLog>();
        if (items != null) {
            for (CallLogConverter item : items) {
                entities.add(item.getEntity());
            }
        }
        return entities;
    }
}
