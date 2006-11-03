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
 * $Id: samlIdpMetadata.php,v 1.2 2006-11-03 00:49:40 superpat7 Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

    $idpMetadata = array( "http://amfmdemo.example.com" => 
      array( "SingleSignOnUrl"=>"http://amfmdemo.example.com:80/amserver/SSORedirect/metaAlias/idp",
             "SingleLogOutUrl"=>"http://amfmdemo.example.com:80/amserver/IDPSloRedirect/metaAlias/idp",
             "certFingerprint"=>"B0:F9:F2:BF:A0:4C:43:B0:E7:00:9A:7A:10:4E:E0:59:72:B0:BE:20" ) );
?>
