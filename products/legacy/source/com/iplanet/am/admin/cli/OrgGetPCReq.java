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
 * $Id: OrgGetPCReq.java,v 1.1 2007-06-06 05:55:59 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.admin.cli;

import com.iplanet.am.sdk.AMConstants;
import com.iplanet.am.sdk.AMException;
import com.iplanet.am.sdk.AMOrganization;
import com.iplanet.am.sdk.AMPeopleContainer;
import com.iplanet.am.sdk.AMSearchControl;
import com.iplanet.am.sdk.AMSearchResults;
import com.iplanet.am.sdk.AMStoreConnection;
import com.iplanet.am.util.PrintUtils;
import com.iplanet.sso.SSOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.io.StringWriter;
import java.io.PrintWriter;

class OrgGetPCReq extends SearchReq {
    private Set PCDNs = new HashSet();
    private boolean DNsOnly = true;
 
    /**
     * Constructs a new OrgGetPCReq.
     *
     * @param targetDN  the Organization DN.
     */
    public OrgGetPCReq(String targetDN) {
        super(targetDN);
    }

    /**
     * sets the value for DNsOnly which tells the process() method to get only
     * the DNs or all the information.
     *
     * @param DNsOnly  if true only DN's , if false all the information.
     */
    void setDNsOnly(boolean DNsOnly) {
        this.DNsOnly = DNsOnly;
    }

    /**
     * gets the value of DNsOnly which tells the process() method to get only
     * the DNs or all the information.
     *
     * @return       DNsOnly if true get only DN's, if false get all the
     *      information.
     */
    boolean isDNsOnly() {
        return DNsOnly;
    }

    /**
     * adds the PCDN dn to Set PCDNs which holds all the PeopleContainer dn's.
     *
     * @param PCDN  the DN of a PeopleContainer
     */
    void addPCDNs(String PCDN) {
        PCDNs.add(PCDN);
    }

    /**
     * gets the PCDNs set which contains all the PeopleContainer DN's whose
     * information should be retrieved.
     *
     * @return       PCDNs which contains all the DN's of PeopleContainer.
     */
    Set getPCDNs() {
        return PCDNs;
    }

    /**
     * converts this object into a string.
     *
     * @return       String. the values of the dnset in print format.
     */
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter prnWriter = new PrintWriter(stringWriter);
        PrintUtils prnUtl = new PrintUtils(prnWriter);
        prnWriter.println(AdminReq.bundle.getString("requestdescription38") +
            " " + targetDN);
        prnWriter.println("   DNsOnly = " + DNsOnly);
        prnWriter.println("   filter = " + filter);
        prnWriter.println("   sizeLimit = " + sizeLimit);
        prnWriter.println("   timeLimit = " + timeLimit);
        
        if (PCDNs.isEmpty()) {
            prnWriter.println("  DN set is empty");
        } else {
            prnUtl.printSet(PCDNs, 2);
        }
        
        prnWriter.flush();
        return stringWriter.toString();
    }

    /**
     * Prints all the PeopleContainer information in an Organization
     * based on the values if the PeopleContainer DNs set is empty than it
     * prints all the PeopleContainer Information. If DNsOnly is true than it
     * prints only the DNs of the PeopleContainers else it prints all the
     * information of the all the PeopleContainers.
     *
     * @param dpConnection  AMStoreConnection.
     * @exception AdminException
     */
    void process(AMStoreConnection dpConnection)
        throws AdminException
    {
        PrintUtils prnUtl = new PrintUtils(AdminReq.writer);
        writer.println(bundle.getString("organization") + " " + targetDN +
            "\n" + bundle.getString("getpeoplecontainers"));

        try {
            AMOrganization org = dpConnection.getOrganization(targetDN);
            boolean needContainerCheck = false;

            if (PCDNs.isEmpty()) {
                AMSearchControl searchCtrl = createSearchControl(
                    AMConstants.SCOPE_SUB);
                AMSearchResults searchResults = org.searchPeopleContainers(
                    filter, searchCtrl);
                PCDNs = searchResults.getSearchResults();
                errorCode = searchResults.getErrorCode();
            } else {
                needContainerCheck = true;
            }
            
            for (Iterator iter = PCDNs.iterator(); iter.hasNext(); ) {
                String dn = (String)iter.next();
                AMPeopleContainer pc = dpConnection.getPeopleContainer(dn);

                if (!needContainerCheck ||
                    (pc.isExists() && AdminUtils.isDescendantOf(pc, targetDN)))
                {
                    AdminReq.writer.println("  " + dn);
                    
                    if (!DNsOnly) {
                        prnUtl.printAVPairs(pc.getAttributes(), 2);
                    }
                }
            }

            printSearchLimitError();
        } catch (AMException dpe) {
            throw new AdminException(dpe.toString());
        } catch (SSOException ssoe) {
            throw new AdminException(ssoe.toString());
        }
    }
}
