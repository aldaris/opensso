<!--
   The contents of this file are subject to the terms
   of the Common Development and Distribution License
   (the License). You may not use this file except in
   compliance with the License.
  
   You can obtain a copy of the License at
   https://opensso.dev.java.net/public/CDDLv1.0.html or
   opensso/legal/CDDLv1.0.txt
   See the License for the specific language governing
   permission and limitations under the License.
  
   When distributing Covered Code, include this CDDL
   Header Notice in each file and include the License file
   at opensso/legal/CDDLv1.0.txt.
   If applicable, add the following below the CDDL Header,
   with the fields enclosed by brackets [] replaced by
   your own identifying information:
   "Portions Copyrighted [year] [name of copyright owner]"
  
   $Id: CreatePolicy.jsp,v 1.1 2006-04-26 18:41:59 bhavnab Exp $
  
   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
-->

<html>
    <head>
        <title>OpenSSO Demo | Create Policy Utility</title>
    </head>
    <body bgcolor="#FFFFFF" text="#000000">
        <table width="600">
        <tr>
        <td>
        <h3>Create Policy</h3>
        <p>
        Please enter  the policy name, resource and user, to who the policy 
	is applicable :
        </p>        
        <form action="<%=request.getContextPath()%>/createPolicy" method="POST">
            <table bgcolor="000000" cellpadding="1" cellspacing="0">
            <tr><td>
            <table bgcolor="F0F0F0" border="0" cellpadding="5" cellspacing="0">
                <tr>
                    <td>Policy Name</td>
                    <td><input type="text" name="policyName" size="20"></td>
                </tr>
                <tr>
                    <td>Resource name</td>
                    <td><input type="text" name="resource" size="80"></td>
                </tr>
                <tr>
                    <td>User name </td>
                    <td><input type="text" name="user" size="20"></td>
                </tr>
                <tr>
                    <td colspan="2" align="center">
                        <input type="submit" name="submit" value="Create">
                        <input type="reset">
                    </td>
                </tr>
            </table>
            </td></tr>
            </table>
        </form>
        </td>
        </tr>
        </table>
    </body>
</html>
