/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensso.c1demoserver.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author pat
 */
public class DemoFilter implements Filter {
    private FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest req, ServletResponse res,
        FilterChain chain) throws IOException, ServletException {
        if (filterConfig == null)
            return;

        // make sure we are dealing with HTTP
        if (req instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) req;

            DemoRequestWrapper wrapper = new DemoRequestWrapper(request);

            chain.doFilter(wrapper, res);
        } else {
            chain.doFilter(req, res);
        }
    }

    public void destroy() {
        this.filterConfig = null;
    }
}
