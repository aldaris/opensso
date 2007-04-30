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
 * $Id: ProviderServlet.java,v 1.1 2007-04-30 01:28:31 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO: Description.
 *
 * @author pbryan
 */
public class ProviderServlet extends HttpServlet
{
    /**
     *  TODO: Description.
     *
     *  @param request TODO.
     *  @param response TODO.
     *  @throws IOException TODO.
     *  @throws ServletException TODO.
     */
    protected void doGet(HttpServletRequest request,
    HttpServletResponse response) throws IOException, ServletException {
        doPost(request, response);
    }

    /**
     *  TODO: Description.
     *
     *  @param request TODO.
     *  @param response TODO.
     *  @throws IOException TODO.
     *  @throws ServletException TODO.
     */
    protected void doPost(HttpServletRequest request,
    HttpServletResponse response) throws IOException, ServletException
    {
        String mode = request.getParameter("openid.mode");

        if (mode == null) {
            dispatchFacelet(request, response, "unknown.jsf");
        }

        else if (mode.equals("associate")) {
            dispatchAction(request, response, new AssociateAction(request, response));
        }

        else if (mode.equals("checkid_immediate")) {
            dispatchFacelet(request, response, "immediate.jsf");
        }

        else if (mode.equals("checkid_setup")) {
            dispatchFacelet(request, response, "setup.jsf");
        }

        else if (mode.equals("check_authentication")) {
            dispatchAction(request, response, new AuthenticationAction(request, response));
        }

        else {
            dispatchFacelet(request, response, "unknown.jsf");
        }
    }

    /**
     * TODO: Description.
     *
     * @param request object representing request made from client.
     * @param response object representing response to be sent to client.
     * @param action TODO.
     * @throws IOException if the target facelet throws this exception.
     */
    private static void dispatchAction(HttpServletRequest request,
    HttpServletResponse response, Action action) throws IOException
    {
        try {
            action.perform();
        }

        catch (BadRequestException bre) {
            response.sendError(response.SC_BAD_REQUEST, bre.getMessage());
        }
    }

    /**
     * TODO: Description.
     *
     * @param request object representing request made from client.
     * @param response object representing response to be sent to client.
     * @param path pathname to facelet to dispatch to.
     * @throws IOException if the target facelet throws this exception.
     * @throws ServletException if the facelet throws exception, or could not dispatch.
     */
    private static void dispatchFacelet(HttpServletRequest request,
    HttpServletResponse response, String path) throws IOException, ServletException
    {
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);

        if (dispatcher == null) {
            throw new ServletException("no dispatcher found for facelet");
        }

        // forward request to facelet to allow it to manage multiple user interactions
        dispatcher.forward(request, response);
    }
}
