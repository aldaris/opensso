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
  
   $Id: DeletePolicy.jsp,v 1.1 2006-04-26 18:41:59 bhavnab Exp $
  
   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
-->

<html>
    <head>
        <title>OpenSSO Demo | Delete Policy Utility</title>
    </head>
    <body bgcolor="#FFFFFF" text="#000000">
        <table width="600">
        <tr>
        <td>
        <h3>Delete Policy</h3>
        <p>
        Please enter  the policy name(s) to be deleted, 
	"," separated if multiple.
        </p>        
        <form action="<%=request.getContextPath()%>/deletePolicy" method="POST">
            <table bgcolor="000000" cellpadding="1" cellspacing="0">
            <tr><td>
            <table bgcolor="F0F0F0" border="0" cellpadding="5" cellspacing="0">
                <tr>
                    <td>Policy Name(s) "," separated if multiple</td>
                    <td><input type="text" name="policyNames" size="20"></td>
                </tr>
                <tr>
                    <td colspan="2" align="center">
                        <input type="submit" name="submit" value="Delete">
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
