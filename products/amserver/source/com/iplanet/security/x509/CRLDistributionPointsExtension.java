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
 * $Id: CRLDistributionPointsExtension.java,v 1.1 2006-01-28 09:28:35 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.security.x509;

import java.io.IOException;

import sun.security.util.DerOutputStream;
import com.iplanet.security.x509.RDN;
import com.iplanet.security.x509.GeneralNamesException;

/**
 * An extension that tells applications where to find the CRL for
 * this certificate.
 *
 * <pre>
 * cRLDistributionPoints ::= SEQUENCE SIZE (1..MAX) OF DistributionPoint
 *
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
public interface CRLDistributionPointsExtension
{
    /**
     * The Object Identifier for this extension.
     */
    public static final String OID = "2.5.29.31";
    public static final String NAME = "CRLDistributionPoints";

    /**
     * Returns the number of distribution points in the sequence.
     */
    public int getNumPoints();
    
    /**
     * Returns the DistributionPoint at the given index in the sequence.
     */
    public CRLDistributionPoint getPointAt(int index);

    /**
     * Encodes this extension to the given DerOutputStream.
     * This method re-encodes each time it is called, so it is not very
     * efficient.
     */
    public void encode(DerOutputStream out) throws IOException;

    /**
     * Returns a printable representation of 
     * the CRLDistributionPointsExtension
     */
    public String toString();
}
