/* The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: QCharset.java,v 1.1 2005-11-01 00:29:40 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.util;

/**
 * This class represent charset to be used. The sorting of this object is based
 * on the Q factor associated with it. The charaset object with more Q factor
 * will be greater.This object allows to represent charaset values received from
 * HTTP header. Example Accept-Charset: ISO-8859-1;Q=0.9 UTF-8 imples UTF-8
 * takes more precedence over ISO-8859-1. Possible Q values are any floating
 * points between 0 and 1. if Q factor is missing it is assumed to be 1.
 */
public class QCharset implements Comparable {

    private String name;

    private float qFactor;

    /**
     * @param name -
     *            Name of the charset
     * @param float
     *            q - Q factor to express preference. 0.0 < q < 1.0 Constructs
     *            new QCharset set object with charset name and q value
     */
    public QCharset(String name, float q) {
        if (name == null) {
            throw new IllegalArgumentException(
                    "QCharset::charset name can't be" + "NULL");
        }
        this.name = name;
        qFactor = q;
    }

    /**
     * @param name -
     *            Name of the charset Constructs new QCharset set object with
     *            charset name and q =1.0
     */
    public QCharset(String name) {
        if (name == null) {
            throw new IllegalArgumentException(
                    "QCharset::charset name can't be" + "NULL");
        }
        this.name = name;
        qFactor = (float) 1.0;
    }

    public String getName() {
        return name;
    }

    public float getQFactor() {
        return qFactor;
    }

    /**
     * @param object
     *            o - QCharset type object
     * @return -1,0,1 based on q value returns -1, 0, 1. If q value is same uses
     *         string.compareTo method
     */
    public int compareTo(Object o1) {
        QCharset q1 = (QCharset) o1;
        if (qFactor < q1.qFactor) {
            return 1;
        }
        if (qFactor > q1.qFactor) {
            return -1;
        }
        /*
         * Do not use collator as it is not necessary codeset names are ASCII
         * only
         */
        return name.compareTo(q1.name);
    }

    public boolean equals(Object o1) {
        QCharset q1 = (QCharset) o1;
        return (qFactor == q1.qFactor && name.equals(q1.name));
    }

    public String toString() {
        return name + ";q=" + qFactor;
    }

}
