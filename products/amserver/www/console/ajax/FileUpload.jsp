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

   $Id: FileUpload.jsp,v 1.1 2008-01-17 06:36:22 veiming Exp $

   Copyright 2008 Sun Microsystems Inc. All Rights Reserved
--%>

<%@page import="java.io.*" %>
<%@page import="java.net.*" %>
<%@page import="java.util.*" %>

<%
    InputStream is = null;

    try {
        is = request.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(10000);
        byte[] b = new byte[10000];
        int len = is.read(b, 0, 10000);
        while (len != -1) {
            bos.write(b, 0 ,len);
            len = is.read(b, 0, 10000);
        }
        String data = bos.toString();
        int idx = data.indexOf("filename=\"");
        idx = data.indexOf("\r\n\r\n", idx);
        data = data.substring(idx+2);
        idx = data.lastIndexOf("\r\n-----------------");
        data = data.substring(0, idx);
        out.println("<div id=\"data\">" + data + "</div>");
    } catch (IOException e) {
    } finally {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            //ignore
        }
    }
%>
