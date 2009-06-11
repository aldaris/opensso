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
import org.opensso.c1demoserver.model.Phone;

/**
 *
 * @author pat
 */

@XmlRootElement(name = "phones")
public class PhonesConverter {
    private Collection<Phone> entities;
    private Collection<PhoneConverter> items;
    private URI uri;
    private int expandLevel;
  
    /** Creates a new instance of PhonesConverter */
    public PhonesConverter() {
    }

    /**
     * Creates a new instance of PhonesConverter.
     *
     * @param entities associated entities
     * @param uri associated uri
     * @param expandLevel indicates the number of levels the entity graph should be expanded
     */
    public PhonesConverter(Collection<Phone> entities, URI uri, int expandLevel) {
        this.entities = entities;
        this.uri = uri;
        this.expandLevel = expandLevel;
        getPhone();
    }

    /**
     * Returns a collection of PhoneConverter.
     *
     * @return a collection of PhoneConverter
     */
    @XmlElement
    public Collection<PhoneConverter> getPhone() {
        if (items == null) {
            items = new ArrayList<PhoneConverter>();
        }
        if (entities != null) {
            items.clear();
            for (Phone entity : entities) {
                items.add(new PhoneConverter(entity, uri, expandLevel, true));
            }
        }
        return items;
    }

    /**
     * Sets a collection of PhoneConverter.
     *
     * @param a collection of PhoneConverter to set
     */
    public void setPhone(Collection<PhoneConverter> items) {
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
     * Returns a collection Phone entities.
     *
     * @return a collection of Phone entities
     */
    @XmlTransient
    public Collection<Phone> getEntities() {
        entities = new ArrayList<Phone>();
        if (items != null) {
            for (PhoneConverter item : items) {
                entities.add(item.getEntity());
            }
        }
        return entities;
    }
}
