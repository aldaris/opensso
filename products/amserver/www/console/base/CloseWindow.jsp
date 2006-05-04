<%--
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

   $Id: CloseWindow.jsp,v 1.1 2006-05-04 06:55:51 veiming Exp $

   Copyright 2006 Sun Microsystems Inc. All Rights Reserved
--%>




<%@page info="CloseWindow" language="java"%>
<%@taglib uri="/WEB-INF/jato.tld" prefix="jato" %>
<%@taglib uri="/WEB-INF/com_sun_web_ui/cc.tld" prefix="cc"%>
<jato:useViewBean
    className="com.sun.identity.console.base.CloseWindowViewBean"
    fireChildDisplayEvents="true">

<body onload="this.window.close()">
</body>
</jato:useViewBean>
