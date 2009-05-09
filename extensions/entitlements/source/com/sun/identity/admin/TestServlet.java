package com.sun.identity.admin;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.PrivilegeManager;
import com.sun.identity.entitlement.opensso.OpenSSOPrivilege;
import com.sun.identity.entitlement.opensso.SubjectUtils;
import java.io.IOException;
import java.io.PrintWriter;
import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String action = request.getParameter("action");

            if (action == null) {
                throw new ServletException("no action specified");
            }
            if (action.equals("privilege.create")) {
                int n = 1;
                if (request.getParameter("n") != null) {
                    n = Integer.parseInt(request.getParameter("n"));
                }
                String template = request.getParameter("template");
                if (template == null) {
                    throw new ServletException("no privilege template specified");
                }
                PrivilegeManager pm = getPrivilegeManager(request);
                Privilege p = pm.getPrivilege(template);
                if (p == null) {
                    throw new ServletException("template privilege did not exist");
                }
                for (int i = 0; i < n; i++) {
                    String name = "policy" + System.currentTimeMillis();
                    OpenSSOPrivilege op = new OpenSSOPrivilege(name, p.getEntitlement(), p.getSubject(), p.getCondition(), p.getResourceAttributes());
                    op.setDescription("created by test servlet");
                    out.print("creating privilege: " + name + " ... ");
                    pm.addPrivilege(op);
                    out.println("done");
                }
            } else {
                throw new ServletException("unknown action:" + action);
            }
        } catch (EntitlementException ee) {
            throw new ServletException(ee);
        } finally {
            out.close();
        }
    }

    private PrivilegeManager getPrivilegeManager(HttpServletRequest request) throws ServletException {
        try {
            SSOToken t = SSOTokenManager.getInstance().createSSOToken(request);
            Subject s = SubjectUtils.createSubject(t);
            PrivilegeManager pm = PrivilegeManager.getInstance("/", s);

            return pm;
        } catch (SSOException ssoe) {
            throw new ServletException(ssoe);
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
