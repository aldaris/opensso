<?php
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
 * $Id: sp.php,v 1.2 2006-11-01 21:16:16 superpat7 Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

    require('localUserManagement.php');
    require('nameMapping.php');
    require('saml-lib.php');

    if (empty($_POST['SAMLResponse'])) {
?>
        <p>Unable to process the submission.<br />
        No SAMLResponse in posted data</p>
<?php 
    } else {
        error_log("Entering sp.php");

        $rawResponse = $_POST["SAMLResponse"];

        error_log("Raw Response: " . $rawResponse );

        // $rawResponse is ready URL decoded...
        $samlResponse = base64_decode( $rawResponse );

        error_log("Authn response = " . $samlResponse );

        $RelayStateURL = $_POST["RelayState"];
        error_log("RelayState = " . $RelayStateURL);
        if ($token = processResponse($samlResponse)) {
            $nameId = getNameID($token);

            if ( isset( $nameId ) )
            {
                error_log("NameQualifier = " . $nameId["NameQualifier"]);
                error_log("SPNameQualifier = " . $nameId["SPNameQualifier"]);
                error_log("NameID = " . $nameId["NameID"]);

                $localID = nameIdToLocalId($nameId["NameQualifier"], $nameId["SPNameQualifier"], $nameId["NameID"] );
                if ( isset( $localID ) && $localID != "" )
                {
                    // Log user in locally
                    setUserId($localID);

                    header("Location: " . $RelayStateURL);

                    exit;
                }
                else
                {
                    // Authenticate user and set mapping
                    $target = urlencode("mapName.php?idp=" 
                    . $nameId["NameQualifier"] . "&sp=" 
                    . $nameId["SPNameQualifier"] . "&nameId=" 
                    . urlencode( $nameId["NameID"] ) 
                    . "&goto=" . $RelayStateURL);
                    header("Location: prompt.php?showIDPLogin=false&goto=" . $target);

                    exit;
                }
            }
        }
    }
?>
