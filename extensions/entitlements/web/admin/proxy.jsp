<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.sun.identity.admin.Scraper" %>
<%@page import="com.sun.identity.admin.Resources" %>
<%@page import="com.sun.identity.admin.Functions" %>
<%@page import="java.io.IOException" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%
    String url = request.getParameter("url");
    Scraper s = new Scraper(url);

    String result;
    try {
        result = s.scrape();
    } catch (IOException ioe) {
        Resources r = new Resources(request);
        result = r.getString(Functions.class, "scrapeError", url, ioe);
    }
 %>
 <%= result %>