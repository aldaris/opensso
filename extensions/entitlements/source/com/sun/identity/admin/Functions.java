package com.sun.identity.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Functions {

    public static String indexOf(List l, Object o) {
        return Integer.toString(l.indexOf(o));
    }

    public static String truncate(String s, int length) {
        if (length >= s.length()) {
            return s;
        }
        return s.substring(0, length - 1) + "...";
    }

    public static int size(Collection c) {
        if (c == null) {
            return 0;
        }
        return c.size();
    }

    public static int length(Object[] oa) {
        return oa.length;
    }

    public static int length(String s) {
        return s.length();
    }

    public static String scrape(String url) {
        try {
            URL u = new URL(url);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    u.openStream()));

            String inputLine;
            StringBuffer b = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                b.append(inputLine);
            }

            in.close();

            String base = getBase(url);
            String result;

            if (base != null) {
                result = setBase(b.toString(), base);
            } else {
                result = b.toString();
            }

            return result;
        } catch (IOException ioe) {
            return AdminResourceBundle.getString("scrapeError", url, ioe);
        }
    }

    private static String getBase(String url) {
        try {
            URL u = new URL(url);
            StringBuffer b = new StringBuffer();
            b.append(u.getProtocol());
            b.append("://");
            b.append(u.getHost());
            if (u.getPort() != -1) {
                b.append(":");
                b.append(u.getPort());
            }

            return b.toString();
        } catch (MalformedURLException mfue) {
            return null;
        }
    }

    private static String setBase(String content, String base) {
        // remove base tag if it exists
        Pattern basePattern = Pattern.compile("<base.*?>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher baseMatcher = basePattern.matcher(content);
        if (baseMatcher.find()) {
            content = baseMatcher.replaceAll("");
        }

        // add new base tag
        Pattern headPattern = Pattern.compile("<head>(.*?)</head>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher headMatcher = headPattern.matcher(content);

        if (headMatcher.find()) {
            StringBuffer newHead = new StringBuffer();
            newHead.append("<head>\n");
            newHead.append("<base href=\"");
            newHead.append(base);
            newHead.append("\" target=\"_blank\"/>\n");
            newHead.append(headMatcher.group(1));
            newHead.append("\n");
            newHead.append("</head>\n");

            content = headMatcher.replaceFirst(newHead.toString());
        }

        return content;
    }
}
