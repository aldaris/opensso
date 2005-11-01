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
  
   $Id: login.jsp,v 1.1 2005-11-01 00:28:37 arvindp Exp $
  
   Copyright 2005 Sun Microsystems Inc. All Rights Reserved
-->

<html>
    <head>
        <title>OpenSSO Demo | Login</title>
    </head>
    <body bgcolor="#FFFFFF" text="#000000">
	<table width="600">
	<tr>
	<td>
        <h3>Authenticate</h3>
        <p>
        Please enter your user name and password:
	</p>
        <p>(Hint: you can login as andy/andy; bob/bob; or as chris/chris)</p>
        <form action="<%=request.getContextPath()%>/login" method="POST">
	    <table bgcolor="000000" cellpadding="1" cellspacing="0">
	    <tr><td>
            <table bgcolor="F0F0F0" border="0" cellpadding="5" cellspacing="0">
                <tr>
                    <td>Username</td>
                    <td><input type="text" name="username" size="20"></td>
                </tr>
                <tr>
                    <td>Password</td>
                    <td><input type="password" name="password" size="20"></td>
                </tr>
                <tr>
                    <td colspan="2" align="center">
			<input type="reset">
                        <input type="submit" name="submit" value="submit">
                    </td>
                </tr>
            </table>
	    </td></tr>
            </table>
	    <p>
	    You can use the following fields to set optional properties in your
	    session. These properties are identified as p1, p2 and p3.
	    </p>
	    <p>
	    <table bgcolor="000000" cellpadding="1" cellspacing="0">
	    <tr><td>
            <table bgcolor="F0F0F0" border="0" cellpadding="5" cellspacing="0">
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
                <tr>
                    <td>Property p1 =</td>
                    <td><input type="text" name="p1" value="" size="20"></td>
                </tr>
                <tr>
                    <td>Property p2 =</td>
                    <td><input type="text" name="p2"
			value="<%=System.currentTimeMillis()%>" size="20"></td>
                </tr>
                <tr>
                    <td>Property p3 =</td>
                    <td><input type="text" name="p3" value="123" size="20"></td>
		</tr>
            </table>
	    </td></tr>
            </table>
            </p>
        </form>
	</td>
	</tr>
	</table>
    </body>
</html>
