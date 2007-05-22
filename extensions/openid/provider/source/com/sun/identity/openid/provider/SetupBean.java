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
 * $Id: SetupBean.java,v 1.2 2007-05-22 22:45:02 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.provider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.event.PhaseEvent;

/**
 * TODO: Description.
 *
 * @author pbryan
 */
public class SetupBean extends CheckidBean
{
    /** TODO: Description. */
    private static final String QUERY = SetupQuery.class.getName();

    /** Attribute name to store types map in view. */
    private static final String TYPES = SetupBean.class.getName() + ".types";

    /** Indicates that a field contains a date value. */
    private static final String TYPE_DATE = "date";

    /** Indicates that a field contains a select value. */
    private static final String TYPE_SELECT = "select";

    /** Indicates that a field contains a text input value. */
    private static final String TYPE_TEXT = "text";

    /** Map of required field names. Lazily initialized. */
    private HashMap<String,Boolean> required = null;

    /** Map of optional field names. Lazily initialized. */
    private HashMap<String,Boolean> optional = null;

    /** Map of requested fields (required + optional). Lazily initialized. */
    private HashMap<String,Boolean> requested = null;

    /** Map of selected fields. Lazily initialized. */
    private HashMap<String,Boolean> selected = null;

    /** Map of field names to data types. */
    private HashMap<String,String> types = new HashMap<String,String>();

    /** Map of field names to field values. */
    private HashMap<String,Object> values = new HashMap<String,Object>();

    /** TODO: Description. */
    private SetupResult result = new SetupResult();

    /** TODO: Description. Initialized by constructor. */
    private SetupQuery query;

    /** Error to display if query population failed. */
    private String error = null;

    /**
     * TODO: Description.
     */
    @SuppressWarnings("unchecked")
    public SetupBean()
    {
        super();

        // try to find valid query in the request
        query = fromRequest();

        // valid query not in request; try to find one in view
        if (query == null) {
            query = (SetupQuery)attributes.get(QUERY);
        }

        // empty query request to provide empty values
        if (query == null) {
            query = new SetupQuery();
        }

        // store query in view so it will persist between requests
        attributes.put(QUERY, query);

        // suppress simple registration extension if disabled in configuration
        if (!Config.getBoolean(Config.SIMPLE_REGISTRATION)) {
           disableRegistration(query);
        }

        // rebuild types map (if any) to initialize values map with empty values
        define((HashMap<String,String>)attributes.get(TYPES));

        // types goes into view to have it available at next view creation
        attributes.put(TYPES, types);
    }

    /**
     * TODO: Description.
     *
     * @param list TODO.
     * @return TODO.
     */
    private static HashMap<String,Boolean> listMap(List<String> list)
    {
        HashMap<String,Boolean> map = new HashMap<String,Boolean>();

        // null list yields empty (non-null) map
        if (list == null) {
            return map;
        }

        for (String item : list) {
            map.put(item, true);
        }

        return map;
    }

    /**
     *  Returns a value, padded with leading zeroes to satisfy the requested
     *  length.
     *
     *  @param value the value to pad with leading zeroes.
     *  @param length length value should be with leading zeroes.
     *  @return the value, padded with leading zeroes, to satisfy length.
     */
    private static String leadingZeroes(String value, int length)
    {
        // absence of a value indicates not selected (all zeroes)
        if (value == null) {
            value = "";
        }

        StringBuffer buf = new StringBuffer(length);

        for (int n = value.length(); n < length; n++) {
            buf.append('0');
        }

        buf.append(value);

        return buf.toString();
    }

    /**
     * Returns a date field in the format required by the OpenID Simple
     * Registration Extension specification (YYYY-MM-DD), with zeroes
     * representing any component of the date the user has not specified.
     *
     * @return the date field in the Simple Registration Extension format.
     */
    private static String srxDate(HashMap<String,String> field)
    {
        if (field == null) {
            return null;
        }

        return leadingZeroes(field.get("year"), 4) + '-' +
         leadingZeroes(field.get("month"), 2) + '-' +
         leadingZeroes(field.get("day"), 2);
    }

    /**
     * TODO: Description.
     *
     * @param request TODO.
     * @return TODO.
     */
    private SetupQuery fromRequest()
    {
        SetupQuery query = new SetupQuery();

        // try to find OpenID checkid_setup query in the request
        try {
            query.populate(request);
        }

        catch (BadRequestException bre) {
            error = bre.getMessage();
            return null;
        }

        return query;
    }

    /**
     * Sets the field type definitions from an another field types map.
     *
     * This method is used to initialize the types map from one found in the
     * view, in order to initialize associated values in the values map.
     *
     * @param map a types map found in the view to use as a source.
     */
    private void define(HashMap<String,String> map)
    {
        if (map == null || map.size() == 0) {
            return;
        }

        for (String name : map.keySet()) {
            define(name, map.get(name));
        }
    }

    /**
     * Reponds with a redirect to the relying party with a positive assertion
     * that the user authenticated successfully.
     */
    private void grant() {
        super.grant(query, result);
    }

    /**
     * Responds with a redirect to the relying party with a negative assertion
     * that the user did not successfully authenticate or trust the relying
     * party with identity and/or registration information.
     */
    private void cancel() {
        result.setMode(Mode.CANCEL);
        sendRedirect(Maps.toQueryString(query.getReturnTo(), result.encode()));
    }

    /**
     * Redirects to the OpenSSO login page.
     */
    private void redirectToLogin()
    {
        String loginURL = Config.getString(Config.LOGIN_URL);
        String gotoURL = Maps.toQueryString(getServiceURL(), query.encode());

        StringBuffer buf = new StringBuffer(loginURL);

        try {
            buf.append(URLEncoder.encode(gotoURL, "UTF-8"));
        }

        // a java virtual machine without UTF-8 encoding shouldn't occur
        catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException(uee);
        }

        sendRedirect(buf.toString());
    }

    /**
     * Exposes the bean property "required", which is a map based on the
     * "trust.required" parameter (comma-delimited list). Each entry in the
     * map represents a field in the comma-delimited list, and has a boolean
     * value of true.
     *
     * @return linked hash map of entries in the trust.required parameter.
     */
    public HashMap<String,Boolean> getRequired()
    {
        // lazy initialization (synchronization not required)
        if (required == null) {
            required = listMap(query.getRequired());
        }
 
        return required;
    }

    /**
     * Exposes the bean property "optional", which is a map based on the
     * "trust.optional" parameter (comma-delimited list). Each entry in the
     * map represents a field in the comma-delimited list, and has a boolean
     * value of true.
     *
     * @return linked hash map of entries in the trust.optional parameter.
     */
    public HashMap<String,Boolean> getOptional()
    {
        // lazy initialization (synchronization not required)
        if (optional == null) {
            optional = listMap(query.getOptional());
        }

        return optional;
    }

    /**
     * Exposes the read-only bean property "requested", which is an
     * intersection of the entries of the "required" and "optional" properties.
     *
     * @return TODO.
     */
    public HashMap<String,Boolean> getRequested()
    {
        // lazy initialization (synchronization not required)
        if (requested == null) {
            HashMap<String,Boolean> map = new HashMap<String,Boolean>();
            map.putAll(getRequired());
            map.putAll(getOptional());
            requested = map;
        }

        return requested;
    }

    /**
     * Exposes the bean property "selected", which is a map containing the
     * selected checkboxes in the form.
     *
     * @return TODO.
     */
    public HashMap<String,Boolean> getSelected()
    {
        // lazy initialization (synchronization not required)
        if (selected == null) {
            HashMap<String,Boolean> map = new HashMap<String,Boolean>();
            map.putAll(getRequested());
            selected = map;
        }

        return selected;
    }

    /**
     * Returns the page field values, each having a name and value.
     *
     * @return a map containing the field values.
     */
    public HashMap<String,Object> getValues() {
        return values;
    }

    /**
     * TODO: Description.
     *
     * @param event TODO.
     * @throws BadRequestException TODO.
     */
    public void beforeRenderResponse(PhaseEvent event)
    throws BadRequestException
    {
        // if error encountered in constructor, display it now in error page
        if (query.getMode() == null) {
            throw new BadRequestException(error);
        }

        // get user principal to ensure that user is logged in
        Principal principal = getPrincipal();

        // user must log into OpenSSO before we can proceed
        if (principal == null) {
            redirectToLogin();
            return;
        }

        // cancel request if this not this user's OpenID identifier
        if (!identityMatches(principal, query.getIdentity())) {
            cancel();
            return;
        }

// TODO: persistent trust: call grant/deny method here accordingly

        /* If the user is required to interact with the user interface, this
         * method completes normally (here), causing the trust management form
         * to be rendered.
         */
    }

    /**
     * Defines a form field and its associated data type.
     *
     * This is called by the page in the form of my:define tags, which are used
     * during view creation to establish what fields are being handled by this
     * backing bean, and what data types each field has.
     *
     * This method allows the backing bean to handle forms abstractly without
     * needing to know the actual fields or data types being presented in the
     * page.
     *
     * @param name name of field in form. 
     * @param type data type of field (either: text, select or date).
     */
    public void define(String name, String type)
    {
        // store the type in the type map for future reference
        types.put(name, type);

        // don't create placeholder in values if value already present
        if (values.get(name) != null) {
            return;
        }

        // add string type to value map for text or select type fields
        if (type.equals(TYPE_TEXT) || type.equals(TYPE_SELECT)) {
            values.put(name, "");
        }

        // add map type to value map for date type fields
        else if (type.equals(TYPE_DATE)) {
            values.put(name, new HashMap<String,String>());
        }
    }

    /**
     * TODO: Description.
     *
     * @return the string constant "grant".
     */
    @SuppressWarnings("unchecked")
    public String grantOnce()
    {
        HashMap<String,Boolean> selected = getSelected();

        HashMap<String,String> sreg = result.getSreg();

        for (String name : getRequested().keySet())
        {
            String type = types.get(name);

            // ignore fields that have not been defined by page
            if (type == null) {
                continue;
            }

            // ignore fields that have not been selected by user
            if (!selected.get(name)) {
                continue;
            }

            String value = null;

            if (type.equals(TYPE_TEXT) || type.equals(TYPE_SELECT)) {
                value = (String)values.get(name);
            }

            else if (type.equals(TYPE_DATE)) {
                value = srxDate((HashMap<String,String>)values.get(name));
            }

            sreg.put(name, value);
        }

        // grant authentication
        grant();

        // navigation here should be irrelevant given redirect to provider
        return "grantOnce";
    }

    /**
     * TODO: Description.
     *
     * @return the string constant "denyOnce".
     */
    public String denyOnce()
    {
        // cancel authentication
        cancel();

        // navigation here is irrelevant; redirects to relying party
        return "denyOnce";
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public String getIdentity() {
        return Codec.encodeURL(query.getIdentity());
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public String getPolicyURL() {
        return Codec.encodeURL(query.getPolicyURL());
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public String getTrustRoot() {
        return Codec.encodeURL(query.getTrustRoot());
    }
}
