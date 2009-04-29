package com.sun.identity.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: cache
public class Scraper {

    private static Map<URL, String> cache = new ExpiringHashMap<URL, String>(1000 * 60 * 15);
    private URL url;

    public Scraper(String u) throws MalformedURLException {
        this.url = new URL(u);
    }

    public String scrape() throws IOException {
        String content = cache.get(url);
        if (content == null) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    url.openStream()));

            String inputLine;
            StringBuffer b = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                b.append(inputLine);
            }

            in.close();

            String base = getBase();
            String result;

            if (base != null) {
                content = setBase(b.toString(), base);
            } else {
                content = b.toString();
            }
            cache.put(url, content);
        }
        return content;
    }

    private String getBase() {
        StringBuffer b = new StringBuffer();
        b.append(url.getProtocol());
        b.append("://");
        b.append(url.getHost());
        if (url.getPort() != -1) {
            b.append(":");
            b.append(url.getPort());
        }

        return b.toString();
    }

    private static String setBase(String content, String base) {
        // remove base tag if it exists
        Pattern basePattern = Pattern.compile("<base.*?>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher baseMatcher = basePattern.matcher(content);
        if (baseMatcher.find()) {
            // base is already set
            return content;
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
