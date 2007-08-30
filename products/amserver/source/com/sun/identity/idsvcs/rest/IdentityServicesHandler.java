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
 * $Id: IdentityServicesHandler.java,v 1.1 2007-08-30 00:26:05 arviranga Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.idsvcs.rest;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.sun.identity.idsvcs.GeneralFailure;
import com.sun.identity.idsvcs.IdentityServicesImpl;
import com.sun.identity.idsvcs.IdentityServicesFactory;
import com.sun.identity.idsvcs.Token;
import com.sun.identity.idsvcs.UserDetails;

/**
 * Provides a marshall/unmarshall layer to the Security interface.
 */
public class IdentityServicesHandler extends HttpServlet {

    private static final long serialVersionUID = 2774677132209419157L;

    // =======================================================================
    // Constants
    // =======================================================================
    private static final String PARAM_PROVIDER = "provider";
    private static final Class PROVIDER_DEFAULT = IdentityServicesImpl.class;

    // =======================================================================
    // Fields
    // =======================================================================
    private IdentityServicesFactory factory;

    // =======================================================================
    // Initialize/Destroy
    // =======================================================================
    /**
     * Loads the init parameters for use in the HTTP methods.
     *
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException {
        super.init();
        // determine if the provider is correct..
        try {
            // get the security provider from the params..
            String def = PROVIDER_DEFAULT.toString();
            String provider = getInitParameter(PARAM_PROVIDER, def);
            this.factory = IdentityServicesFactory.getInstance(provider);
        } catch (Exception e) {
            // wrap in a servlet exception as to not scare the natives..
            throw new ServletException(e);
        }
    }

    // =======================================================================
    // HTTP Methods
    // =======================================================================
    /**
     * Determines unmarshalls the request and executes the proper method based
     * on the request parameters.
     *
     * @see javax.servlet.http.HttpServlet#service(HttpServletRequest request,
     *      HttpServletResponse response)
     */
    protected void service(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        IdentityServicesImpl security = this.factory.newInstance();
        SecurityMethod.execute(security, request, response);
    }

    // =======================================================================
    // Helper Methods
    // =======================================================================
    /**
     * Get a consistent behaviour between application servers..
     */
    String getInitParameter(String param, String def) {
        String ret = getInitParameter(param);
        if (isBlank(param)) {
            ret = def;
        }
        return ret;
    }

    private static boolean isBlank(String val)
    {
        return (val == null) ? true :
            ((val.trim().length() == 0) ? true : false);
    }
    
    /**
     * Enum to get the request parameters and test w/ the SecurityMethods.
     */
    public static class SecurityParameter {

        public static final SecurityParameter URI =
            new SecurityParameter("URI");
        public static final SecurityParameter ACTION =
            new SecurityParameter("ACTION");
        public static final SecurityParameter USERNAME =
            new SecurityParameter("USERNAME");
        public static final SecurityParameter PASSWORD =
            new SecurityParameter("PASSWORD");
        public static final SecurityParameter TOKENID =
            new SecurityParameter("TOKENID", Token.class);
        public static final SecurityParameter SUBJECTID =
            new SecurityParameter("SUBJECTID", Token.class);
        public static final SecurityParameter ATTRIBUTENAMES =
            new SecurityParameter("ATTRIBUTENAMES",
            (new String[1]).getClass());
        public static final SecurityParameter LOGNAME =
            new SecurityParameter("LOGNAME");
        public static final SecurityParameter MESSAGE =
            new SecurityParameter("MESSAGE");
        public static final SecurityParameter MESSAGECODE =
            new SecurityParameter("MESSAGECODE");
        public static final SecurityParameter APPID =
            new SecurityParameter("APPID", Token.class);
        // ===================================================================
        // Fields
        // ===================================================================
        final Class type;
        final String name;

        SecurityParameter(String name) {
            this.name = name;
            this.type = String.class;
        }

        SecurityParameter(String name, Class type) {
            this.name = name;
            this.type = type;
        }

        String name() {
            return name;
        }

        Object getValue(ServletRequest request) {
            Object ret = null;
            if (this.type == Token.class) {
                ret = getToken(request);
            } else if (this.type == List.class) {
                ret = getList(request);
            } else {
                ret = getString(request);
            }
            return ret;
        }

        public String getString(ServletRequest request) {
            String name = name().toLowerCase();
            String ret = request.getParameter(name);
            if (isBlank(ret)) {
                ret = null;
            }
            return ret;
        }

        public Token getToken(ServletRequest request) {
            Token ret = null;
            String n = name().toLowerCase();
            String id = request.getParameter(n);
            if (!isBlank(id)) {
                ret = new Token();
                ret.setId(id);
            }
            return ret;
        }

        public List getList(ServletRequest request) {
            List ret = null;
            String n = name().toLowerCase();
            String[] values = request.getParameterValues(n);
            if (values != null) {
                ret = new ArrayList();
                for (int i = 0; i < values.length; i++) {
                    String value = values[i];
                    if (!isBlank(value)) {
                        ret.add(value);
                    }
                }
            }
            return ret;
        }
    }

   /**
     * Defined by the interface 'com.sun.identity.idsvcs.Security'.
     */
    public static class SecurityMethod {

        public static final SecurityMethod AUTHENTICATE = new SecurityMethod(
            "AUTHENTICATE", Token.class, SecurityParameter.USERNAME,
            SecurityParameter.PASSWORD, SecurityParameter.URI);
        public static final SecurityMethod AUTHORIZE = new SecurityMethod(
            "AUTHORIZE", Boolean.class, SecurityParameter.URI,
            SecurityParameter.ACTION, SecurityParameter.SUBJECTID);
        public static final SecurityMethod ATTRIBUTES = new SecurityMethod(
            "ATTRIBUTES", UserDetails.class, SecurityParameter.ATTRIBUTENAMES,
            SecurityParameter.SUBJECTID);
        public static final SecurityMethod LOG = new SecurityMethod(
            "LOG", Void.class, new SecurityParameter[]
            {SecurityParameter.APPID, SecurityParameter.SUBJECTID,
             SecurityParameter.URI, SecurityParameter.LOGNAME,
             SecurityParameter.MESSAGE});

        // ===================================================================
        // Constructors
        // ===================================================================
        private SecurityMethod(String name, Class clazz,
            SecurityParameter[] params) {
            
            final Method[] SECURITY_METHODS =
                IdentityServicesImpl.class.getMethods();
            // find the method
            Method imethod = null;
            String lname = name.toLowerCase();
            for (int i = 0; i < SECURITY_METHODS.length; i++) {
                Method m = SECURITY_METHODS[i];
                // found the method by name..
                String mname = m.getName();
                if (mname.equals(lname)) {
                    // lets check based on parameters..
                    imethod = m;
                    break;
                }
            }
            // need to throw if we can't find it..
            if (imethod == null) {
                throw new IllegalArgumentException();
            }
            // set the internal fields
            this.type = clazz;
            this.method = imethod;
            this.parameters = params;
        }

        private SecurityMethod(String name, Class clazz,
            SecurityParameter param1) {
            this(name, clazz, new SecurityParameter[]{param1});
        }

        private SecurityMethod(String name, Class clazz,
            SecurityParameter param1, SecurityParameter param2) {
            this(name, clazz, new SecurityParameter[]{param1, param2});
        }

        private SecurityMethod(String name, Class clazz,
            SecurityParameter param1, SecurityParameter param2,
            SecurityParameter param3) {
            this(name, clazz, new SecurityParameter[]{param1, param2, param3});
        }

        // ===================================================================
        // Fields
        // ===================================================================
        final Class type;
        final Method method;
        final SecurityParameter[] parameters;

        public static void execute(IdentityServicesImpl security,
            HttpServletRequest request, ServletResponse response)
            throws ServletException, IOException {
            
            // find the security method from the path..
            Writer wrt = response.getWriter();
            String path = request.getPathInfo();
            MarshallerFactory mar = getMarshaller(path);
            path = path.substring(path.lastIndexOf('/') + 1).toUpperCase();
            SecurityMethod method = null;
            if (path.equals("AUTHENTICATE")) {
                method = SecurityMethod.AUTHENTICATE;
            } else if (path.equals("AUTHORIZE")) {
                method = SecurityMethod.AUTHORIZE;
            } else if (path.equals("ATTRIBUTES")) {
                method = SecurityMethod.ATTRIBUTES;
            } else if (path.equals("LOG")) {
                method = SecurityMethod.LOG;
            }

            try {
                // execute the method w/ the parameters..
                Object value = method.invoke(security, request);
                // marshall the response..
                if (method.type != Void.class) {
                    mar.newInstance(method.type).marshall(wrt, value);
                }
            } catch (GeneralFailure ex) {
                // write out the proper security based exception..
                try {
                    mar.newInstance(GeneralFailure.class).marshall(wrt, ex);
                } catch (Exception e) {
                    // something really went wrong so just give up..
                    throw new ServletException(e);
                }
            } catch (Exception e) {
                // something really went wrong so just give up..
                throw new ServletException(e);
            }
        }

        /**
         * If both exist on the path then return JSON, XML, and then Properites
         * in that order.
         */
        private static MarshallerFactory getMarshaller(String path) {
            // default is properties format
            boolean xml = path.indexOf("xml/") != -1;
            boolean json = path.indexOf("json/") != -1;
            return // (json) ? MarshallerFactory.JSON :
                (xml) ? MarshallerFactory.XML : MarshallerFactory.PROPS;
        }

        private Object invoke(IdentityServicesImpl security,
            ServletRequest request) throws GeneralFailure {
            
            // find the value for each parameter..
            Object[] params = new Object[this.parameters.length];
            for (int i = 0; i < this.parameters.length; i++) {
                SecurityParameter param = this.parameters[i];
                params[i] = param.getValue(request);
            }
            
            Object ret = null;
            try {
                // invoke the actual security param..
                ret = method.invoke(security, params);
            } catch (IllegalArgumentException e) {
                throw new GeneralFailure(e.getMessage());
            } catch (IllegalAccessException e) {
                throw new GeneralFailure(e.getMessage());
            } catch (InvocationTargetException e) {
                // make sure to get the actual InvalidPassword etc..
                throw (GeneralFailure) e.getTargetException();
            }
            return ret;
        }
    }
}
