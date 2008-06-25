/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2005 Sun Microsystems Inc. All Rights Reserved
 *
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
 * $Id: AMSendMail.java,v 1.3 2008-06-25 05:41:27 qcheng Exp $
 *
 */

package com.iplanet.am.util;

import com.sun.identity.shared.Constants;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/*
 * This is a send mail utility class which can be used to send notifications to
 * the users if some event occurs.
 */
public class AMSendMail {
    private static String mailServerHost = SystemProperties.get(
            Constants.AM_SMTP_HOST, "localhost");

    private static String mailServerPort = SystemProperties.get(
            Constants.SM_SMTP_PORT, "25");

    private static Properties props = new Properties();

    static {
        // Set the host smtp address
        props.put("mail.smtp.host", mailServerHost);
        props.put("mail.smtp.port", mailServerPort);
    }

    /**
     * Posts e-mail messages to users This method will wait on for the timeouts
     * when the specified host is down. Use this method in a separate thread so
     * that it will not hang when the mail server is down.
     */
    public void postMail(String recipients[], String subject, String message,
            String from) throws MessagingException {
        postMail(recipients, subject, message, from, null);
    }

    public void postMail(String recipients[], String subject, String message,
            String from, String charset) throws MessagingException {
        boolean debug = false;

        // create some properties and get the default mail Session
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(debug);

        // create a message object
        MimeMessage msg = new MimeMessage(session);

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];

        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }

        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Setting the Subject and Content Type
        if (charset == null) {
            msg.setSubject(subject);
            msg.setContent(message, "text/plain");
        } else {
            charset = BrowserEncoding.mapHttp2JavaCharset(charset);
            msg.setSubject(subject, charset);
            msg.setContent(message, "text/plain; charset=" + charset);
        }

        // Transport the message now
        Transport.send(msg);
    }

    public static void main(String[] args) {

        String from = "<" + "ganesh@iplanet.com" + ">";
        String[] to = { "malla@sun.com", "ganesh@iplanet.com" };
        String sub = "Hello Bond";
        String msg = "Have fun dude";

        try {
            AMSendMail sm = new AMSendMail();
            sm.postMail(to, sub, msg, from);
        } catch (MessagingException ex) {
            System.out.println("Message Exception occured");
            ex.printStackTrace();
        }
    }

}
