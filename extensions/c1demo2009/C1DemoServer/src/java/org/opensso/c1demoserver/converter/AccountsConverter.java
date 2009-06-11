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
import org.opensso.c1demoserver.model.Account;

/**
 *
 * @author pat
 */

@XmlRootElement(name = "accounts")
public class AccountsConverter {
    private Collection<Account> entities;
    private Collection<AccountConverter> items;
    private URI uri;
    private int expandLevel;
  
    /** Creates a new instance of AccountsConverter */
    public AccountsConverter() {
    }

    /**
     * Creates a new instance of AccountsConverter.
     *
     * @param entities associated entities
     * @param uri associated uri
     * @param expandLevel indicates the number of levels the entity graph should be expanded
     */
    public AccountsConverter(Collection<Account> entities, URI uri, int expandLevel) {
        this.entities = entities;
        this.uri = uri;
        this.expandLevel = expandLevel;
        getAccount();
    }

    /**
     * Returns a collection of AccountConverter.
     *
     * @return a collection of AccountConverter
     */
    @XmlElement
    public Collection<AccountConverter> getAccount() {
        if (items == null) {
            items = new ArrayList<AccountConverter>();
        }
        if (entities != null) {
            items.clear();
            for (Account entity : entities) {
                items.add(new AccountConverter(entity, uri, expandLevel, true));
            }
        }
        return items;
    }

    /**
     * Sets a collection of AccountConverter.
     *
     * @param a collection of AccountConverter to set
     */
    public void setAccount(Collection<AccountConverter> items) {
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
     * Returns a collection Account entities.
     *
     * @return a collection of Account entities
     */
    @XmlTransient
    public Collection<Account> getEntities() {
        entities = new ArrayList<Account>();
        if (items != null) {
            for (AccountConverter item : items) {
                entities.add(item.getEntity());
            }
        }
        return entities;
    }
}
