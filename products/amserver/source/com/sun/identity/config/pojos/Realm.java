package com.sun.identity.config.pojos;

/**
 * This class represents a realm stored in the back-end side.
 * TODO this class should be moved to the correct package once the Configurator interface is implemented appropiately.
 *
 * @author Jeffrey Bermudez
 */
public class Realm {

    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
