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
 * $Id: nameMapping.php,v 1.1 2007-03-09 02:00:25 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

function nameIdToLocalId($idp, $sp, $nameID)
{
$user="dbuser";
$password="dbpassword";
$database="lightbulb";

mysql_connect("localhost",$user,$password) or die("Not connected : " . mysql_error() . "\n");

mysql_select_db($database) or die( "Unable to select database : " . mysql_error() . "\n");

$query="SELECT localid FROM nameidmapping WHERE (idp='$idp' AND sp='$sp' AND nameid='$nameID')";
$result=mysql_query($query);
if (!$result) {
   echo 'MySQL Error: ' . mysql_error() . "\n";
   exit;
}

$num=mysql_numrows($result);

mysql_close();

$i=0;
while ($i < $num) {
    return mysql_result($result,$i,"localid");;
}
}

function mapNameIdToLocalId($idp, $sp, $nameID, $localId)
{
$user="dbuser";
$password="dbpassword";
$database="lightbulb";

mysql_connect("localhost",$user,$password) or die("Not connected : " . mysql_error() . "\n");

mysql_select_db($database) or die( "Unable to select database : " . mysql_error() . "\n");

$query="INSERT INTO nameidmapping (idp, sp, nameid, localid) VALUES ('$idp', '$sp', '$nameID', '$localId')";
$result=mysql_query($query);
if (!$result) {
   echo 'MySQL Error: ' . mysql_error() . "\n";
   exit;
}

mysql_close();
}
?>
