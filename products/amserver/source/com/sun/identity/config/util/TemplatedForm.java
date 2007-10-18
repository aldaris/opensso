package com.sun.identity.config.util;

import net.sf.click.control.Field;
import net.sf.click.control.Form;

import java.util.Iterator;
import java.util.List;

/**
 * @author Jeffrey Bermudez
 */
public class TemplatedForm extends Form {

    public TemplatedForm(String name) {
        super(name);
    }

    public TemplatedForm() {
        super();
    }

    public boolean doProcess() {
        // We can overwrite this method to put some specific login here
        return true;
    }

    public final boolean onProcess() {
        super.onProcess();

        List list = getFieldList();
        if ( list != null && !list.isEmpty() ) {
            Iterator i = list.iterator();
            while( i.hasNext() ) {
                Field field = (Field)i.next();
                if ( !field.onProcess() ) {
                    return false;
                }
            }
        }

        return doProcess();
    }
}
