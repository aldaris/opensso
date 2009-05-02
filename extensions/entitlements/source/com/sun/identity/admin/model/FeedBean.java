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

    public void setUrlKey(String urlKey) {
        Resources r = new Resources();
        String u = r.getString(urlKey);

        try {
            URL url = new URL(u);

            SyndFeedInput input = new SyndFeedInput();
            feed = input.build(new XmlReader(url));
        } catch (MalformedURLException mfue) {
            // TODO
        } catch (IOException ioe) {
            // TODO
        } catch (FeedException fe) {
            // TODO
        }
    }

    public SyndFeed getFeed() {
        return feed;
    }
}
