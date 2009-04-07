/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: IPCondition.java,v 1.4 2009-04-07 10:25:08 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.shared.debug.Debug;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.security.auth.Subject;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * EntitlementCondition to represent IP, DNS name based  constraint
  */
public class IPCondition implements EntitlementCondition {
    private static final long serialVersionUID = -403250971215465050L;

    /** Key that is used in an <code>IPCondition</code> to define the  DNS
     * name values for which a policy applies. The value corresponding to the
     * key has to be a <code>Set</code> where each element is a <code>String
     * </code> that conforms to the patterns described here.
     *
     * The patterns is :
     * <pre>
     * ccc.ccc.ccc.ccc
     * *.ccc.ccc.ccc</pre>
     * where c is any valid character for DNS domain/host name.
     * There could be any number of <code>.ccc</code> components.
     * Some sample values are:
     * <pre>
     * www.sun.com
     * finace.yahoo.com
     * *.yahoo.com
     * </pre>
     *
     * @see #setProperties(Map)
     */
    public static final String DNS_NAME = "DnsName";


    /** Key that is used to define request IP address that is passed in
     * the <code>env</code> parameter while invoking
     * <code>getConditionDecision</code> method of an <code>IPCondition</code>.
     * Value for the key should be a <code>String</code> that is a string
     * representation of IP of the client, in the form n.n.n.n where n is a
     * value between 0 and 255 inclusive.
     *
     * @see #getConditionDecision(SSOToken, Map)
     * @see #REQUEST_DNS_NAME
     */
    public static final String REQUEST_IP = "requestIp";

    /** Key that is used to define request DNS name that is passed in
     * the <code>env</code> parameter while invoking
     * <code>getConditionDecision</code> method of an <code>IPCondition</code>.
     * Value for the key should be a set of strings representing the
     * DNS names of the client, in the form <code>ccc.ccc.ccc</code>.
     * If the <code>env</code> parameter is null or does not
     * define value for <code>REQUEST_DNS_NAME</code>,  the
     * value for <code>REQUEST_DNS_NAME</code> is obtained
     * from the single sign on token of the user
     *
     * @see #getConditionDecision(SSOToken, Map)
     */
    public static final String REQUEST_DNS_NAME = "requestDnsName";

    private String startIp;
    private String endIp;
    private String domainNameMask;
    private String pConditionName;

    /**
     * Constructs an IPCondition
     */
    public IPCondition() {
    }

    /**
     * Constructs IPCondition object:w
     * @param domainNameMask domain name mask, for example *.example.com,
     * only wild card allowed is *
     */
    public IPCondition(String domainNameMask) {
        this.domainNameMask = domainNameMask;
    }

    /**
     * Constructs IPCondition object:w
     * @param startIp starting ip of a range for example 121.122.123.124
     * @param endIp ending ip of a range, for example 221.222.223.224
     */
    public IPCondition(String startIp, String endIp) {
        this.startIp = startIp;
        this.endIp = endIp;
    }

    /**
     * Constructs IPCondition object:w
     * @param domainNameMask domain name mask, for example *.example.com,
     * only wild card allowed is *
     * @param startIp starting ip of a range for example 121.122.123.124
     * @param endIp ending ip of a range, for example 221.222.223.224
     */
    public IPCondition(String domainNameMask, String startIp, String endIp) {
        this.domainNameMask = domainNameMask;
        this.startIp = startIp;
        this.endIp = endIp;
    }

    /**
     * Returns state of the object
     * @return state of the object encoded as string
     */
    public String getState() {
        return toString();
    }

    /**
     * Sets state of the object
     * @param state State of the object encoded as string
     */
    public void setState(String state) {
        try {
            JSONObject jo = new JSONObject(state);
            domainNameMask = jo.optString("domainNameMask");
            startIp = jo.optString("startIp");
            endIp = jo.optString("endIp");
            pConditionName = jo.optString("pConditionName");
        } catch (JSONException joe) {
        }
    }

    /**
     * Returns <code>ConditionDecision</code> of
     * <code>EntitlementCondition</code> evaluation
     * @param subject EntitlementCondition who is under evaluation.
     * @param resourceName Resource name.
     * @param environment Environment parameters.
     * @return <code>ConditionDecision</code> of
     * <code>EntitlementCondition</code> evaluation
     * @throws com.sun.identity.entitlement,  EntitlementException in case
     * of any error
     */
    public ConditionDecision evaluate(
        Subject subject,
        String resourceName,
        Map<String, Set<String>> environment
    ) throws EntitlementException {
        boolean allowed = true;
        Set<String> setIP = environment.get(REQUEST_IP);
        String ip = ((setIP != null) && !setIP.isEmpty()) ?
            setIP.iterator().next() : null;
        Set reqDnsNames = (Set)environment.get(REQUEST_DNS_NAME);

        if ((ip != null) && !isAllowedByIp(ip)) {
            allowed = false;
        } else if ((reqDnsNames != null) && !reqDnsNames.isEmpty()) {
            for (Iterator names = reqDnsNames.iterator();
                names.hasNext() && allowed; ) {
                allowed = isAllowedByDns((String)names.next());
            }
        }
        return new ConditionDecision(allowed, Collections.EMPTY_MAP);
    }

    private boolean isAllowedByIp(String ip)
        throws EntitlementException {
        long requestIp = stringToIp(ip);
        long startIpNum = stringToIp(startIp);
        long endIpNum = stringToIp(endIp);
        return ((requestIp >= startIpNum) && (requestIp <= endIpNum));
    }

    private long stringToIp(String ip) throws EntitlementException {
        StringTokenizer st = new StringTokenizer(ip, ".");
        int tokenCount = st.countTokens();
        if ( tokenCount != 4 ) {
            String args[] = { "ip", ip };
            throw new EntitlementException(400, args);
        }
        long ipValue = 0L;
        while ( st.hasMoreElements()) {
            String s = st.nextToken();
            short ipElement = 0;
            try {
                ipElement = Short.parseShort(s);
            } catch(Exception e) {
                String args[] = { "ip", ip };
                throw new EntitlementException(400, args);
            }
            if ( ipElement < 0 || ipElement > 255 ) {
                String args[] = { "ipElement", s };
                throw new EntitlementException(400, args);
            }
            ipValue = ipValue * 256L + ipElement;
        }
        return ipValue;
    }

    private boolean isAllowedByDns(String dnsName)
        throws EntitlementException {
        return true;
//        boolean allowed = false;
//        dnsName = dnsName.toLowerCase();
//        Iterator dnsNames = dnsList.iterator();
//        while ( dnsNames.hasNext() ) {
//            String dnsPattern = (String)dnsNames.next();
//            if (dnsPattern.equals("*")) {
//                // single '*' matches everything
//                allowed = true;
//                break;
//            }
//            int starIndex = dnsPattern.indexOf("*");
//            if (starIndex != -1 ) {
//                // the dnsPattern is a string like *.ccc.ccc
//                String dnsWildSuffix = dnsPattern.substring(1);
//                if (dnsName.endsWith(dnsWildSuffix)) {
//                    allowed = true;
//                    break;
//                }
//            }
//            else if (dnsPattern.equalsIgnoreCase(dnsName)) {
//                    allowed = true;
//                    break;
//            }
//        }
//        return allowed;
    }

    /**
     * @return the domainNameMask
     */
    public String getDomainNameMask() {
        return domainNameMask;
    }

    /**
     * @param domainNameMask the domainNameMask to set
     */
    public void setDomainNameMask(String domainNameMask) {
        this.domainNameMask = domainNameMask;
    }

    /**
     * @return the startIp
     */
    public String getStartIp() {
        return startIp;
    }

    /**
     * @param startIp the startIp to set
     */
    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    /**
     * @return the endIp
     */
    public String getEndIp() {
        return endIp;
    }

    /**
     * @param endIp the endIp to set
     */
    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    /**
     * Returns OpenSSO policy subject name of the object
     * @return subject name as used in OpenSSO policy,
     * this is releavant only when UserECondition was created from
     * OpenSSO policy Condition
     */
    public String getPConditionName() {
        return pConditionName;
    }

    /**
     * Sets OpenSSO policy subject name of the object
     * @param pConditionName subject name as used in OpenSSO policy,
     * this is releavant only when UserECondition was created from
     * OpenSSO policy Condition
     */
    public void setPConditionName(String pConditionName) {
        this.pConditionName = pConditionName;
    }

    /**
     * Returns JSONObject mapping of the object
     * @return JSONObject mapping  of the object
     */
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("domainNameMask", domainNameMask);
        jo.put("startIp", startIp);
        jo.put("endIp", endIp);
        jo.put("pConditionName", pConditionName);
        return jo;
    }

    /**
     * Returns <code>true</code> if the passed in object is equal to this object
     * @param obj object to check for equality
     * @return  <code>true</code> if the passed in object is equal to this object
     */
    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || !getClass().equals(obj.getClass())) {
            return false;
        }
        IPCondition object = (IPCondition) obj;
        if (getDomainNameMask() == null) {
            if (object.getDomainNameMask() != null) {
                return false;
            }
        } else {
            if (!domainNameMask.equals(object.getDomainNameMask())) {
                return false;
            }
        }
        if (getStartIp() == null) {
            if (object.getStartIp() != null) {
                return false;
            }
        } else {
            if (!startIp.equals(object.getStartIp())) {
                return false;
            }
        }
        if (getEndIp() == null) {
            if (object.getEndIp() != null) {
                return false;
            }
        } else {
            if (!endIp.equals(object.getEndIp())) {
                return false;
            }
        }
        if (getPConditionName() == null) {
            if (object.getPConditionName() != null) {
                return false;
            }
        } else {
            if (!pConditionName.equals(object.getPConditionName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns hash code of the object
     * @return hash code of the object
     */
    @Override
    public int hashCode() {
        int code = 0;
        if (domainNameMask != null) {
            code += domainNameMask.hashCode();
        }
        if (startIp != null) {
            code += startIp.hashCode();
        }
        if (endIp != null) {
            code += endIp.hashCode();
        }
        if (pConditionName != null) {
            code += pConditionName.hashCode();
        }
        return code;
    }

    /**
     * Returns string representation of the object
     * @return string representation of the object
     */
    @Override
    public String toString() {
        String s = null;
        try {
            s = toJSONObject().toString(2);
        } catch (JSONException joe) {
            Debug debug = Debug.getInstance("Entitlement");
            debug.error("IPCondiiton.toString(), JSONException:" +
                    joe.getMessage());
        }
        return s;
    }
}
