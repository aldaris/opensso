/* The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: URLFailoverHelper.java,v 1.1 2006-09-28 23:26:09 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.common;



import java.net.HttpURLConnection;
import java.net.URL;

import com.sun.identity.agents.arch.AgentException;
import com.sun.identity.agents.arch.Module;
import com.sun.identity.agents.arch.SurrogateBase;


/**
 * The class manages URL failover
 */
public class URLFailoverHelper extends SurrogateBase 
        implements IURLFailoverHelper 
{
    
    public URLFailoverHelper(Module module) {
        super(module);
    }

    public void initiailze(boolean isPrioritized, String[] urlList)
            throws AgentException {
        if(urlList.length == 1) {
            if(isLogWarningEnabled()) {
                logWarning("URLFailoverHelper: Only one URL is specified, "
                           + "failover will be disabled");
            }

            markDisabled();
        }
        setPrioritized(isPrioritized);
        setURLList(urlList);
    }

    public String getAvailableURL() throws AgentException {
        String result = null;
        if(isEnabled()) {
            int index = getCurrentIndex();
            String url = getURL(index);
            if(isAvailable(url)) {
                result = url;
            } else {
                if(isLogWarningEnabled()) {
                    logWarning("URLFailoverHelper: Detected the failure of "
                               + url + ", initiating failover sequence");
                }
                int currentIndex = index;
                int newIndex = currentIndex;
                boolean done = false;
                while( !done) {
                    String newURL = getURL(newIndex);
                    if(isAvailable(newURL)) {
                        if(isLogMessageEnabled()) {
                            logMessage("URLFailoverHelper: url " + newURL
                                       + " is available");
                        }
                        if(newIndex != currentIndex) {
                            updateIndex(newIndex, currentIndex);

                            result = newURL;
                        }

                        done = true;
                    } else {
                        newIndex = (newIndex + 1) % (getMaxIndex() + 1);

                        if(newIndex == currentIndex) {
                            logError(
                               "URLFailoverHelper: No URL is available at" 
                                    + " this time");

                            throw new AgentException(
                                "No URL is available at this time");
                        }
                    }
                }
            }
        } else {
            result = getCurrentURL();

            if( !this.isAvailable(result)) {
                logError(
                    "URLFailoverHelper: No URL is available at this time");

                throw new AgentException("No URL is available at this time");
            }
        }

        if(isLogMessageEnabled()) {
            logMessage("URLFailoverHelper: getAvailableURL() => " + result);
        }

        return result;
    }

    /**
     * Method updateIndex
     *
     *
     * @param newIndex
     * @param oldIndex
     *
     */
    private void updateIndex(int newIndex, int oldIndex) {

        boolean indexUpdated = false;
        if (!isPrioritized()) {
            if((newIndex != oldIndex) 
                    && (newIndex >= 0) && (newIndex <= getMaxIndex())) 
            {
                    synchronized(this) {
                        if(oldIndex == getCurrentIndex()) {
                            setCurrentIndex(newIndex);

                            indexUpdated = true;
                        }
                    }
                }
            if(isLogWarningEnabled()) {
                String oldURL = getURL(oldIndex);
                String newURL = getURL(newIndex);

                if(indexUpdated) {
                    logWarning("URLFailoverHelper: URL updated from " + oldURL
                               + " to " + newURL);
                }
            }
        }
    }

    private boolean isAvailable(String url) {

        boolean result = false;
        HttpURLConnection conn = null;
        try {
            if(isLogMessageEnabled()) {
                logMessage("URLFailoverHelper: Checking if " + url
                           + " is available");
            }

            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.connect();

            result = true;

            if(isLogMessageEnabled()) {
                logMessage("URLFailoverHelper: URL " + url + " is available");
            }
        } catch(Exception ex) {
            if(isLogWarningEnabled()) {
                logWarning("URLFailoverHelper: the url " + url
                           + " is not available", ex);
            }
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                    if (isLogMessageEnabled()) {
                        logMessage("URLFailoverHelper: disconnected the "
                                + "connection for availability check");
                    }
                } catch (Exception ex2) {
                    if (isLogWarningEnabled()) {
                        logWarning("URLFailoverHelper: failed to disconnect "
                                + "connection for availability check", ex2);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Method setCurrentIndex
     *
     *
     * @param index
     *
     */
    private void setCurrentIndex(int index) {
        _index = index;
    }

    /**
     * Method getURL
     *
     *
     * @param index
     *
     * @return
     *
     */
    private String getURL(int index) {
        return _urlList[index];
    }

    /**
     * Method getCurrentURL
     *
     *
     * @return
     *
     */
    private String getCurrentURL() {
        return _urlList[getCurrentIndex()];
    }

    /**
     * Method getCurrentIndex
     *
     *
     * @return
     *
     */
    private int getCurrentIndex() {
        return _index;
    }

    /**
     * Method setURLList
     *
     *
     * @param urlList
     *
     */
    private void setURLList(String[] urlList) {
        _urlList = urlList;
    }

    /**
     * Method getMaxIndex
     *
     *
     * @return
     *
     */
    private int getMaxIndex() {
        return _urlList.length - 1;
    }
    
    private void setPrioritized(boolean isPrioritized) {
        _isPrioritized = isPrioritized;
    }
    
    private boolean isPrioritized() {
        return _isPrioritized;
    }

    /**
     * Method isEnabled
     *
     *
     * @return
     *
     */
    private boolean isEnabled() {
        return !_disabled;
    }

    /**
     * Method markDisabled
     *
     *
     */
    private void markDisabled() {
        _disabled = true;
    }

    private String[] _urlList;
    private int      _index    = 0;
    private boolean  _disabled = false;
    private boolean _isPrioritized = false;
}
