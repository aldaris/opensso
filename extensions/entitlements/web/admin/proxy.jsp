<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.sun.identity.admin.Scraper" %>
<%@page import="com.sun.identity.admin.Resources" %>
<%@page import="com.sun.identity.admin.Functions" %>
<%@page import="java.io.IOException" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%
        String url;
        Scraper s;
        Exception ex = null;
        String result = null;

        url = request.getParameter("url");
        s = new Scraper(url);
        try {
            result = s.scrape();
        } catch (IOException ioe) {
            ex = ioe;
        }

        if (result == null || ex != null) {
            String localUri = request.getParameter("localUri");

            if (localUri != null) {
                StringBuffer b = new StringBuffer();

                String scheme = request.getScheme();
                String server = request.getServerName();
                int port = request.getServerPort();
                String path = request.getContextPath();

                b.append(scheme);
                b.append("://");
                b.append(server);
                b.append(":");
                b.append(port);
                b.append(path);
                b.append(localUri);

                url = b.toString();
                
                s = new Scraper(url);
                try {
                    result = s.scrape();
                } catch (IOException ioe) {
                    ex = ioe;
                }
            }
        }

        if (result == null) {
            Resources r = new Resources(request);
            result = r.getString(Functions.class, "scrapeError", ex);
        }
%>
<%= result != null ? result : ""%>