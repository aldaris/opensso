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
 * $Id: localUserManagement.php,v 1.1 2006-10-19 22:03:37 superpat7 Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

session_start();

// Brain dead implementation...
function setUserId($userID)
{
    $_SESSION['UserID'] = $userID;
}

function getUserId()
{
    return $_SESSION["UserID"];
}

function clearUserId()
{
    unset($_SESSION["UserID"]);
}

function authenticateLocalUser($userid, $password)
{
    $user="root";
    $mysqlpassword="";
    $database="mysql";

    mysql_connect("localhost",$user,$mysqlpassword) or die("Not connected : " . mysql_error() . "\n");

    mysql_select_db($database) or die( "Unable to select database : " . mysql_error() . "\n");

    $passwordhash = sha1( $password );

    $query="SELECT username FROM users WHERE (userid='$userid' AND passwordhash='$passwordhash')";
    $result=mysql_query($query);
    if (!$result) {
        echo 'MySQL Error: ' . mysql_error() . "\n";
        exit;
    }

    mysql_close();

    $username=NULL;

    $num=mysql_numrows($result);
    if ( $num == 1 )
    {
        $username = mysql_result($result,0,"username");
    }

    return $username;
}

function getUserName($userid)
{
    $user="root";
    $password="";
    $database="mysql";

    mysql_connect("localhost",$user,$password) or die("Not connected : " . mysql_error() . "\n");

    mysql_select_db($database) or die( "Unable to select database : " . mysql_error() . "\n");

    $query="SELECT username FROM users WHERE (userid='$userid')";
    $result=mysql_query($query);
    if (!$result) {
        echo 'MySQL Error: ' . mysql_error() . "\n";
        exit;
    }

    mysql_close();

    $username=NULL;

    $num=mysql_numrows($result);
    if ( $num == 1 )
    {
        $username = mysql_result($result,0,"username");
    }

    return $username;
}
?>
