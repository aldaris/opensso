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
 * $Id: IssuingDistributionPointExtension.java,v 1.1 2006-01-28 09:28:35 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.security.x509;

import java.io.IOException;

import sun.security.util.DerOutputStream;

/**
 * A critical CRL extension that identifies the CRL distribution point
 * for a particular CRL
 *
 * <pre>
 * issuingDistributionPoint ::= SEQUENCE {
 *         distributionPoint       [0] DistributionPointName OPTIONAL,
 *         onlyContainsUserCerts   [1] BOOLEAN DEFAULT FALSE,
 *         onlyContainsCACerts     [2] BOOLEAN DEFAULT FALSE,
 *         onlySomeReasons         [3] ReasonFlags OPTIONAL,
 *         indirectCRL             [4] BOOLEAN DEFAULT FALSE }
 *
 * DistributionPointName ::= CHOICE {
 *         fullName                [0]     GeneralNames,
 *         nameRelativeToCRLIssuer [1]     RelativeDistinguishedName }
 *
 * ReasonFlags ::= BIT STRING {
 *         unused                  (0),
 *         keyCompromise           (1),
 *         cACompromise            (2),
 *         affiliationChanged      (3),
 *         superseded              (4),
 *         cessationOfOperation    (5),
 *         certificateHold         (6) }
 *
 * GeneralNames ::= SEQUENCE SIZE (1..MAX) OF GeneralName
 *
 * GeneralName ::= CHOICE {
 *         otherName                       [0]     OtherName,
 *         rfc822Name                      [1]     IA5String,
 *         dNSName                         [2]     IA5String,
 *         x400Address                     [3]     ORAddress,
 *         directoryName                   [4]     Name,
 *         ediPartyName                    [5]     EDIPartyName,
 *         uniformResourceIdentifier       [6]     IA5String,
 *         iPAddress                       [7]     OCTET STRING,
 *         registeredID                    [8]     OBJECT IDENTIFIER}
 *
 * OtherName ::= SEQUENCE {
 *         type-id    OBJECT IDENTIFIER,
 *         value      [0] EXPLICIT ANY DEFINED BY type-id }
 *
 * EDIPartyName ::= SEQUENCE {
 *         nameAssigner            [0]     DirectoryString OPTIONAL,
 *         partyName               [1]     DirectoryString }
 *
 * RelativeDistinguishedName ::=
 *         SET OF AttributeTypeAndValue
 *
 * AttributeTypeAndValue ::= SEQUENCE {
 *         type     AttributeType,
 *         value    AttributeValue }
 *
 * AttributeType ::= OBJECT IDENTIFIER
 *
 * AttributeValue ::= ANY DEFINED BY AttributeType
 * </pre>
 */
public interface IssuingDistributionPointExtension
{
    /**
     * The Object Identifier for this extension.
     */
    public static final String OID = "2.5.29.28";

    /**
     * Attribute names.
     */
    public static final String NAME = "IssuingDistributionPoint";

    /**
     * Returns the issuing distribution point.
     */
    public IssuingDistributionPoint getIssuingDistributionPoint();

    /** 
     * Gets the criticality of this extension.  PKIX dictates that this
     * extension SHOULD be critical, so by default, the extension is critical.
     */
    public boolean getCritical(boolean critical);

    /**
     * Encodes this extension to the given DerOutputStream.
     * This method re-encodes each time it is called, so it is not very
     * efficient.
     */
    public void encode(DerOutputStream out) throws IOException;

    /**
     * Returns a printable representation of 
     * the IssuingDistributionPointExtension
     */

    public String toString();
}
