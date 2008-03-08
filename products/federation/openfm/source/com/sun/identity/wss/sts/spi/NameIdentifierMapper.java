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
 * $Id: NameIdentifierMapper.java,v 1.1 2008-03-08 03:03:19 mallas Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.wss.sts.spi;

/**
 * The interface <code>NameIdentifierMapper</code> is used to map the
 * real user identity to the psuedo name and vice versa.
 */
public interface NameIdentifierMapper {

     /**
      * Returns the user psuedo name for a given userid.
      * @param userid user id or name for which psuedo name to be retrieved.
      * @return the user psuedo name for a given userid. 
      */
     public String getUserPsuedoName(String userid);

     /**
      * Returns the userid for a given psuedo name.
      * @param the user psuedo name. 
      * @return the userid for a given psuedo name.
      */
     public String getUserID(String psuedoName);
}
