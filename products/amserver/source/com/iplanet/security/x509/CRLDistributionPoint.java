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
 * $Id: CRLDistributionPoint.java,v 1.1 2006-01-28 09:28:35 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.security.x509;

import java.io.IOException;
import java.io.OutputStream;

import sun.security.util.BitArray;
import sun.security.x509.GeneralNames;
import com.iplanet.security.x509.RDN;
import com.iplanet.security.x509.GeneralNamesException;

/**
 * <pre>
 * DistributionPoint ::= SEQUENCE {
 *      distributionPoint       [0]     DistributionPointName OPTIONAL,
 *      reasons                 [1]     ReasonFlags OPTIONAL,
 *      cRLIssuer               [2]     GeneralNames OPTIONAL }
 *
 * DistributionPointName ::= CHOICE {
 *      fullName                [0]     GeneralNames,
 *      nameRelativeToCRLIssuer [1]     RelativeDistinguishedName }
 *
 * ReasonFlags ::= BIT STRING {
 *      unused                  (0),
 *      keyCompromise           (1),
 *      cACompromise            (2),
 *      affiliationChanged      (3),
 *      superseded              (4),
 *      cessationOfOperation    (5),
 *      certificateHold         (6) }
 * </pre>
 */
public interface CRLDistributionPoint {
    /**
     * Returns the <code>fullName</code> of the
     * <code>DistributionPointName</code>, which may be <code>null</code>.
     * @return the <code>fullName</code> of the
     * <code>DistributionPointName</code>, which may be <code>null</code>.
     */
    public GeneralNames getFullName();

    /**
     * Returns the <code>relativeName</code> of the
     * <code>DistributionPointName</code>, which may be <code>null</code>.
     * @return the <code>relativeName</code> of the
     * <code>DistributionPointName</code>, which may be <code>null</code>.
     */
    public RDN getRelativeName();

    /**
     * Returns the reason flags for this distribution point.  May be
     * <code>null</code>.
     */
    public BitArray getReasons();
    /**
     * Sets the reason flags for this distribution point.  May be set to
     * <code>null</code>.
     */
    public void setReasons(BitArray reasons);

    /**
     * Returns the CRLIssuer for the CRL at this distribution point.
     * May be <code>null</code>.
     */
    public GeneralNames getCRLIssuer();
}

