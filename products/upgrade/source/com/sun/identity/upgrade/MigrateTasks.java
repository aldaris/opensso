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
 * $Id: MigrateTasks.java,v 1.1 2008-01-18 08:03:12 bina Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.upgrade;
/**
 * Service that requires migration should implement this interface
 * 
 */
public interface MigrateTasks {
    /**
     * Migrates the service.
     * 
     * @return true if successful else false.
     */
    public boolean migrateService();
    /**
     * All the postmigration tasks are implemented in this function.
     *
     * @return true if successful else false.
     */
    public boolean postMigrateTask();
    /**
     * All the premigration tasks are implemented in this function.
     *
     * @return true if successful else false.
     */
    public boolean preMigrateTask();
}
