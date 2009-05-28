package com.sun.identity.admin;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.PrivilegeManager;
import com.sun.identity.entitlement.opensso.SubjectUtils;
import java.io.IOException;
import java.io.PrintWriter;
import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PrivilegeXmlServlet extends HttpServlet {

    private SSOToken getSSOToken(HttpServletRequest httpRequest) throws ServletException {
        try {
            SSOTokenManager manager = SSOTokenManager.getInstance();
            SSOToken ssoToken = manager.createSSOToken(httpRequest);

            return ssoToken;
        } catch (SSOException ssoe) {
            throw new ServletException(ssoe);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String[] names = request.getParameterValues("name");
        if (names == null || names.length == 0) {
            throw new ServletException("no names specified");
        }

        SSOToken t = getSSOToken(request);
        Subject s = SubjectUtils.createSubject(t);

        try {
            //TODO: realm
            PrivilegeManager pm = PrivilegeManager.getInstance("/", s);
            StringBuffer xml = new StringBuffer();

            // TODO: fetch single policy set
            for (String name: names) {
                xml.append(pm.getPrivilegeXML(name));
            }
            
            out.print(xml);
        } catch (EntitlementException ee) {
            throw new ServletException(ee);
        } finally { 
            out.close();
        }
    } 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
}
