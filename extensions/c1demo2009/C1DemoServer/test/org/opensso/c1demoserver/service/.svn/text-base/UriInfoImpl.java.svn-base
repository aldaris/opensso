/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensso.c1demoserver.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author azacharski
 */
public class UriInfoImpl implements UriInfo {

    String absolutePath;

    public UriInfoImpl(String absolutePath)
    {
        this.absolutePath = absolutePath;
    }

    public String getPath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getPath(boolean arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<PathSegment> getPathSegments() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<PathSegment> getPathSegments(boolean arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public URI getRequestUri() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public UriBuilder getRequestUriBuilder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public URI getAbsolutePath() {
        try {
            return new URI(absolutePath);
        } catch (URISyntaxException ex) {
            
        }
        return null;
    }

    public UriBuilder getAbsolutePathBuilder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public URI getBaseUri() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public UriBuilder getBaseUriBuilder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public MultivaluedMap<String, String> getPathParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public MultivaluedMap<String, String> getPathParameters(boolean arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public MultivaluedMap<String, String> getQueryParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public MultivaluedMap<String, String> getQueryParameters(boolean arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<String> getMatchedURIs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<String> getMatchedURIs(boolean arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Object> getMatchedResources() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
