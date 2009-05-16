package com.sun.identity.admin.model;

import com.sun.identity.admin.Resources;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class FeedBean implements Serializable {
    private SyndFeed feed;
    private String urlKey;
    private Exception exception;

    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    public SyndFeed getFeed() {
        if (feed == null) {
            loadFeed();
        }
        return feed;
    }

    private void loadFeed() {
        Resources r = new Resources();
        String u = r.getString(urlKey);

        try {
            URL url = new URL(u);

            SyndFeedInput input = new SyndFeedInput();
            feed = input.build(new XmlReader(url));
            exception = null;
        } catch (MalformedURLException mfue) {
            this.exception = mfue;
        } catch (IOException ioe) {
            this.exception = ioe;
        } catch (FeedException fe) {
            this.exception = fe;
        }
    }

    public Exception getException() {
        return exception;
    }
}
