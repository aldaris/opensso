/*
 * The contents of this file are subject to the terms
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
 * $Id: FAMHttpServletResponse.java,v 1.1 2007-07-06 19:17:05 huacui Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wssagents.common;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.ServletOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * A helper class used to manage the servlet response content. 
 */
public class FAMHttpServletResponse extends HttpServletResponseWrapper
{
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    public FAMHttpServletResponse(HttpServletResponse response) {
        super(response);
    }

    public ServletOutputStream getOutputStream() {
        return new FAMServletOutputStream(bos);
    }

    /**
     * Returns the contents of the response
     */
    public String getContents() {
        return bos.toString();
    }
}
