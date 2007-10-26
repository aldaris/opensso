package com.sun.identity.config.util;

import net.sf.click.control.Field;
import net.sf.click.control.Form;

import java.util.Iterator;

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

        Iterator it = getFieldList().iterator();
        while (it.hasNext()) {
            if (!((Field) it.next()).onProcess()) {
                return false;
            }
        }

        return doProcess();
    }

}
