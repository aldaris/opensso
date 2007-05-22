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
 * $Id: AssertionConsumerService.php,v 1.1 2007-05-22 05:38:39 andreas1980 Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */




	require_once('config/config.php');
	require_once('lib/saml-lib.php');
	
    require_once('spi/sessionhandling/' . $LIGHTBULB_CONFIG['spi-sessionhandling'] . '.php');
    require_once('spi/namemapping/' . $LIGHTBULB_CONFIG['spi-namemapping'] . '.php');

    if (empty($_POST['SAMLResponse'])) {
		
		echo '<p>Unable to process the submission.<br />
        No SAMLResponse in posted data</p>';
        
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

                $localID = spi_namemapping_nameIdToLocalId($nameId["NameQualifier"], $nameId["SPNameQualifier"], $nameId["NameID"] );
                
                if ( isset( $localID ) && $localID != "" ) {
                
                
                	error_log("NameID successfull federation. Now set User ID = " . $localID);
                
                    // User is sucessfully mapped to a local ID
                    spi_sessionhandling_setNameID($nameId["NameID"]);
                    spi_sessionhandling_setUserID($localID);
                    spi_sessionhandling_setResponse($token);

                } else {
                
                    // User is not mapped to a local ID. The user is still authenticated, but further steps may be needed
                    // in order to proceed. The service will probably do one of two things, either auto-generate a new account or
                    // require the user to login using a local account and then federate the user.
                    
                    error_log("No Local ID ");
                    
                    spi_sessionhandling_setNameID($nameId["NameID"]);
                    spi_sessionhandling_setResponse($token);
                }
                
                // Either the user is mapped to a local account or not, the user is redirected back.
                header("Location: " . urldecode($RelayStateURL) );
                
            } else {
            	echo '<p>Error extracting the NameID</p>';
            }
            
        } else {
        	echo '<p>Error processing response<p>';
        }
        
    }
?>
