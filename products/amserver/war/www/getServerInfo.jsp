<%@ page
import="com.sun.identity.authentication.AuthContext,
        com.iplanet.am.util.SystemProperties,
        com.sun.identity.setup.AMSetupServlet,
        com.sun.identity.setup.BootstrapData,
        com.sun.identity.setup.EmbeddedOpenDS,
        java.io.File,
        java.net.URLDecoder,
        java.net.URLEncoder,
        java.util.ArrayList,
        java.util.Map,
        javax.security.auth.callback.Callback,
        javax.security.auth.callback.NameCallback,
        javax.security.auth.callback.PasswordCallback"
%>
<%
   String method = request.getMethod();
   if ("POST".equals(request.getMethod()) != true) {
       response.sendError(405);
       return;
   }
   String username = request.getParameter("IDToken1");
   String password = request.getParameter("IDToken2");

   if ( username == null && password == null) {
       response.sendError(400);
       return;
   }

   username = URLDecoder.decode(username, "UTF-8");
   password = URLDecoder.decode(password, "UTF-8");

   if ("amadmin".equals(username) == false) {
       response.sendError(401);
       return;
   }
   
   AuthContext lc = new AuthContext("/");
   lc.login();
    while (lc.hasMoreRequirements()) {
        Callback[] callbacks = lc.getRequirements();
        ArrayList missing = new ArrayList();
        // loop through the requires setting the needs..
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                NameCallback nc = (NameCallback) callbacks[i];
                nc.setName(username);
            } else if (callbacks[i] instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback) callbacks[i];
                pc.setPassword(password.toCharArray());
            } else {
                missing.add(callbacks[i]);
            }
        }
        // there's missing requirements not filled by this
        if (missing.size() > 0) {
            // need add the missing later..
            response.sendError(401);
            return;
        }
        lc.submitRequirements(callbacks);
    }
    // validate the password..
    if (lc.getStatus() != AuthContext.Status.SUCCESS) {
        response.sendError(401);
        return;
    }

    // Read the local bootstrap file.
    //ldap://localhost:52389/http%3A%2F%2Fwww.idp.com%3A8080%2Fopensso?pwd=AQIC5wM2LY4Sfcy%2BAQBQxghVwhBE92i78cqf&embeddedds=%2FUsers%2Frajeevangal%2FTopenssoGF%2Fopends&dsbasedn=dc%3Dopensso%2Cdc%3Djava%2Cdc%3Dnet&dsmgr=cn%3DDirectory+Manager&dspwd=AQIC5wM2LY4Sfcy%2BAQBQxghVwhBE92i78cqf

    String baseDir = SystemProperties.get(SystemProperties.CONFIG_PATH);
    String encKey = SystemProperties.get("am.encryption.pwd");
    BootstrapData bootstrapData = new BootstrapData(baseDir);
    //String dsbasedn = bootstrapData.getBaseDN();
    boolean isEmbeddedDS = (new File(baseDir + "/opends")).exists();
    // Assumption : opends entry is the 1st
    Map bMap = bootstrapData.getDataAsMap(0);
    String dsbasedn = (String) bMap.get(BootstrapData.DS_BASE_DN);
    String dsport = (String) bMap.get(BootstrapData.DS_PORT);
    String dsprotocol = (String) bMap.get(BootstrapData.DS_PROTOCOL);
    String dsrelport = (String) bMap.get(BootstrapData.DS_REPLICATIONPORT);
    String dshost = (String) bMap.get(BootstrapData.DS_HOST);
    String dsmgr = (String) bMap.get(BootstrapData.DS_MGR);
    String dspwd = (String) bMap.get(BootstrapData.DS_PWD);

    // if embedded get replication port status. Two cases :
    //   i) No replication port -> generate a new one
    //   ii) replication port available -> retrieve it
    String replPort = null;
    String replPortAvailable = null;
    if (isEmbeddedDS) {
       replPort = EmbeddedOpenDS.getReplicationPort(username, password, 
                      "localhost", dsport);
       replPortAvailable = "true";
       if (replPort == null) {
           replPortAvailable = "false";
           replPort = ""+ AMSetupServlet.getUnusedPort("localhost", 50889, 1000);
        }
    }
    // We have collected all the data - return a response
    assert username != null && password != null;
    StringBuffer buf = new StringBuffer();
    
    buf.append(BootstrapData.DS_ISEMBEDDED).append("=").append(isEmbeddedDS).
                                        append("&");
    if (dsprotocol != null) {
        buf.append(BootstrapData.DS_PROTOCOL).append("=").
            append(URLEncoder.encode(dsprotocol, "UTF-8")).append("&");
    }
    if (dshost != null) {
        buf.append(BootstrapData.DS_HOST).append("=").
            append(URLEncoder.encode(dshost, "UTF-8")).append("&");
    }
    if (dsport != null) {
        buf.append(BootstrapData.DS_PORT).append("=").append(dsport).
            append("&");
    }
    if (dsbasedn != null) {
        buf.append(BootstrapData.DS_BASE_DN).append("=").
            append(URLEncoder.encode(dsbasedn, "UTF-8")).append("&");
    }
    if (replPort != null) {
        buf.append(BootstrapData.DS_REPLICATIONPORT).append("=").
            append(replPort).append("&");
    }
    if (replPortAvailable != null) {
        buf.append(BootstrapData.DS_REPLICATIONPORT_AVAILABLE).append("=").
            append(replPortAvailable).append("&");
    }
    if (dsmgr != null) {
        buf.append(BootstrapData.DS_MGR).append("=").
            append(URLEncoder.encode(dsmgr, "UTF-8")).append("&");
    }
    if (dspwd != null) {
        buf.append(BootstrapData.DS_PWD).append("=").
            append(URLEncoder.encode(dspwd, "UTF-8")).append("&");
    }
    if (encKey != null) {
        buf.append(BootstrapData.ENCKEY).append("=").
            append(URLEncoder.encode(encKey, "UTF-8"));
    }
    out.println(buf.toString());
%>
