/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensso.c1demoserver.filter;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author pat
 */
public class DemoRequestWrapper extends HttpServletRequestWrapper {
    private Principal userPrincipal;

    public DemoRequestWrapper(HttpServletRequest request){
        super(request);

        userPrincipal = new DemoPrincipal("id=1112223333,ou=user,dc=opensso,dc=java,dc=net");
    }

    @Override
    public Principal getUserPrincipal() {
        return userPrincipal;
    }

    @Override
    public String getRemoteUser() {
        return userPrincipal.getName();
    }
}
