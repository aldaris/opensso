
package com.sun.enterprise.registration;

import com.sun.enterprise.util.RegistrationUtil;
import com.sun.identity.setup.SetupConstants;
import com.sun.identity.shared.debug.Debug;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;

/**
 * 
 */
public class StartRegister {

    public StartRegister() {
    }

    public static void servicetagTransfer() {
        File registrationHome = RegistrationUtil.getRegistrationHome();
        if (registrationHome == null) {
            Debug.getInstance(SetupConstants.DEBUG_NAME).error(
                "AMSetupServlet.servicetagTransfer: " +
                "Can't find registration home.");
        }

        /*
         *  Put all the jars in this directory into the classpath
         *  for the registrationclassloader
         */
        final File [] files = registrationHome.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".jar") || name.endsWith(".zip"));
            }
        });

        /*
         *  start registration daemon only if the directory exists and
         *  it contains some jars
         */
        if (files != null && files.length != 0 ) {
            try {
                URL[] registrationJars = new URL[files.length];
                for (int i = 0; i < files.length; i++) {
                    registrationJars[i] = files[i].toURI().toURL();
                }
                URLClassLoader classLoaderRegistration =
                    new URLClassLoader(registrationJars);
                Class registrationDaemonClass = null;
                registrationDaemonClass =
                    Class.forName(
                        "com.sun.enterprise.registration.RegisterFAMDaemon",
                        true, classLoaderRegistration);
                Class [] fclass = {File.class};
                Method m =
                    registrationDaemonClass.getMethod("start", fclass);
                final File stfile = RegistrationUtil.getServiceTagRegistry();
                Date startDate, stopDate;

                /*
                 * if the registration daemon doesn't complete the
                 * servicetag transfer to the local repository within
                 * 5 seconds, there was some problem.  don't hold up
                 * completion of configuration process because of it.
                 */
                synchronized (stfile) {
                    Object [] params = {stfile};
                    m.invoke((Object)null, params);
                    startDate = new Date();
                    stfile.wait(5000);
                    stopDate = new Date();
                }
                long startMS = startDate.getTime();
                long stopMS = stopDate.getTime();
                long diffMS = stopMS - startMS;
                if (Debug.getInstance(
                    SetupConstants.DEBUG_NAME).messageEnabled())
                {
                    Debug.getInstance(SetupConstants.DEBUG_NAME).message(
                        "AMSetupServlet.servicetagTransfer: " +
                        "registration daemon finished in " + diffMS + " ms.");
                }
            } catch (Exception e) {
                Debug.getInstance(SetupConstants.DEBUG_NAME).error(
                    "AMSetupServlet.servicetagTransfer: " +
                    "Exception starting registration daemon", e);
            }
        }
    }
}

