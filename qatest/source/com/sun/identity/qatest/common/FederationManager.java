package com.sun.identity.qatest.common;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.WebClient;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.StringBuffer;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class FederationManager {
    private String amadmUrl;
    private String amUrl;

    public FederationManager(String url) {
        amUrl = url;
        amadmUrl = url + "/fmadm.jsp?cmd=";
    }


    /**
     * Create a new service in server.
     *
     * @param webClient HTML Unit Web Client object.
     * @xmlfile XML file(s) that contains schema.
     */
    public HtmlPage createService(
        WebClient webClient,
        String xmlfile
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "create-service");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (xmlfile != null) {
            HtmlTextArea taxmlfile = (HtmlTextArea)form.getTextAreasByName("xmlfile").get(0);
            taxmlfile.setText(xmlfile);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * Delete service from the server.
     *
     * @param webClient HTML Unit Web Client object.
     * @servicename Service Name(s).
     * @deletepolicyrule Delete policy rule.
     */
    public HtmlPage deleteService(
        WebClient webClient,
        List servicename,
        boolean deletepolicyrule
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "delete-service");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (servicename != null) {
            HtmlSelect slservicename= (HtmlSelect)form.getSelectByName("servicename");
            String[] fakeOptions = new String[servicename.size()];
            int cnt = 0;
            for (Iterator i = servicename.iterator(); i.hasNext(); ) {
                fakeOptions[cnt++] = (String)i.next();
            }
            slservicename.fakeSelectedAttribute(fakeOptions);
        }

        HtmlCheckBoxInput cbdeletepolicyrule = (HtmlCheckBoxInput)form.getInputByName("deletepolicyrule");
        cbdeletepolicyrule.setChecked(deletepolicyrule);


        return (HtmlPage)form.submit();
    }

    /**
     * Remove default attribute values in schema.
     *
     * @param webClient HTML Unit Web Client object.
     * @servicename Name of service.
     * @schematype Type of schema.
     * @attributenames Attribute name(s).
     * @subschemaname Name of sub schema.
     */
    public HtmlPage removeAttributeDefaults(
        WebClient webClient,
        String servicename,
        String schematype,
        List attributenames,
        String subschemaname
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "remove-attribute-defaults");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (servicename != null) {
            HtmlTextInput txtservicename = (HtmlTextInput)form.getInputByName("servicename");
            txtservicename.setValueAttribute(servicename);
        }

        if (schematype != null) {
            HtmlTextInput txtschematype = (HtmlTextInput)form.getInputByName("schematype");
            txtschematype.setValueAttribute(schematype);
        }

        if (attributenames != null) {
            HtmlSelect slattributenames= (HtmlSelect)form.getSelectByName("attributenames");
            String[] fakeOptions = new String[attributenames.size()];
            int cnt = 0;
            for (Iterator i = attributenames.iterator(); i.hasNext(); ) {
                fakeOptions[cnt++] = (String)i.next();
            }
            slattributenames.fakeSelectedAttribute(fakeOptions);
        }

        if (subschemaname != null) {
            HtmlTextInput txtsubschemaname = (HtmlTextInput)form.getInputByName("subschemaname");
            txtsubschemaname.setValueAttribute(subschemaname);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * Add default attribute values in schema.
     *
     * @param webClient HTML Unit Web Client object.
     * @servicename Name of service.
     * @schematype Type of schema.
     * @attributevalues Attribute values e.g. homeaddress=here.
     * @subschemaname Name of sub schema.
     */
    public HtmlPage addAttributeDefaults(
        WebClient webClient,
        String servicename,
        String schematype,
        List attributevalues,
        String subschemaname
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "add-attribute-defaults");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (servicename != null) {
            HtmlTextInput txtservicename = (HtmlTextInput)form.getInputByName("servicename");
            txtservicename.setValueAttribute(servicename);
        }

        if (schematype != null) {
            HtmlTextInput txtschematype = (HtmlTextInput)form.getInputByName("schematype");
            txtschematype.setValueAttribute(schematype);
        }

        if (attributevalues != null) {
            HtmlSelect slattributevalues= (HtmlSelect)form.getSelectByName("attributevalues");
            String[] fakeOptions = new String[attributevalues.size()];
            int cnt = 0;
            for (Iterator i = attributevalues.iterator(); i.hasNext(); ) {
                fakeOptions[cnt++] = (String)i.next();
            }
            slattributevalues.fakeSelectedAttribute(fakeOptions);
        }

        if (subschemaname != null) {
            HtmlTextInput txtsubschemaname = (HtmlTextInput)form.getInputByName("subschemaname");
            txtsubschemaname.setValueAttribute(subschemaname);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * Show default attribute values in schema.
     *
     * @param webClient HTML Unit Web Client object.
     * @servicename Name of service.
     * @schematype Type of schema.
     * @subschemaname Name of sub schema.
     * @attributenames Attribute name(s).
     */
    public HtmlPage showAttributeDefaults(
        WebClient webClient,
        String servicename,
        String schematype,
        String subschemaname,
        List attributenames
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "show-attribute-defaults");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (servicename != null) {
            HtmlTextInput txtservicename = (HtmlTextInput)form.getInputByName("servicename");
            txtservicename.setValueAttribute(servicename);
        }

        if (schematype != null) {
            HtmlTextInput txtschematype = (HtmlTextInput)form.getInputByName("schematype");
            txtschematype.setValueAttribute(schematype);
        }

        if (subschemaname != null) {
            HtmlTextInput txtsubschemaname = (HtmlTextInput)form.getInputByName("subschemaname");
            txtsubschemaname.setValueAttribute(subschemaname);
        }

        if (attributenames != null) {
            HtmlSelect slattributenames= (HtmlSelect)form.getSelectByName("attributenames");
            String[] fakeOptions = new String[attributenames.size()];
            int cnt = 0;
            for (Iterator i = attributenames.iterator(); i.hasNext(); ) {
                fakeOptions[cnt++] = (String)i.next();
            }
            slattributenames.fakeSelectedAttribute(fakeOptions);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * Set default attribute values in schema.
     *
     * @param webClient HTML Unit Web Client object.
     * @servicename Name of service.
     * @schematype Type of schema.
     * @subschemaname Name of sub schema.
     * @attributevalues Attribute values e.g. homeaddress=here.
     */
    public HtmlPage setAttributeDefaults(
        WebClient webClient,
        String servicename,
        String schematype,
        String subschemaname,
        List attributevalues
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "set-attribute-defaults");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (servicename != null) {
            HtmlTextInput txtservicename = (HtmlTextInput)form.getInputByName("servicename");
            txtservicename.setValueAttribute(servicename);
        }

        if (schematype != null) {
            HtmlTextInput txtschematype = (HtmlTextInput)form.getInputByName("schematype");
            txtschematype.setValueAttribute(schematype);
        }

        if (subschemaname != null) {
            HtmlTextInput txtsubschemaname = (HtmlTextInput)form.getInputByName("subschemaname");
            txtsubschemaname.setValueAttribute(subschemaname);
        }

        if (attributevalues != null) {
            HtmlSelect slattributevalues= (HtmlSelect)form.getSelectByName("attributevalues");
            String[] fakeOptions = new String[attributevalues.size()];
            int cnt = 0;
            for (Iterator i = attributevalues.iterator(); i.hasNext(); ) {
                fakeOptions[cnt++] = (String)i.next();
            }
            slattributevalues.fakeSelectedAttribute(fakeOptions);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * Set choice values of attribute schema.
     *
     * @param webClient HTML Unit Web Client object.
     * @servicename Name of service.
     * @schematype Type of schema.
     * @attributename Name of attribute.
     * @add Set this flag to append the choice values to existing ones.
     * @subschemaname Name of sub schema.
     * @choicevalues Choice value e.g. o102=Inactive.
     */
    public HtmlPage setAttributeChoiceValues(
        WebClient webClient,
        String servicename,
        String schematype,
        String attributename,
        boolean add,
        String subschemaname,
        List choicevalues
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "set-attribute-choice-values");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (servicename != null) {
            HtmlTextInput txtservicename = (HtmlTextInput)form.getInputByName("servicename");
            txtservicename.setValueAttribute(servicename);
        }

        if (schematype != null) {
            HtmlTextInput txtschematype = (HtmlTextInput)form.getInputByName("schematype");
            txtschematype.setValueAttribute(schematype);
        }

        if (attributename != null) {
            HtmlTextInput txtattributename = (HtmlTextInput)form.getInputByName("attributename");
            txtattributename.setValueAttribute(attributename);
        }

        HtmlCheckBoxInput cbadd = (HtmlCheckBoxInput)form.getInputByName("add");
        cbadd.setChecked(add);

        if (subschemaname != null) {
            HtmlTextInput txtsubschemaname = (HtmlTextInput)form.getInputByName("subschemaname");
            txtsubschemaname.setValueAttribute(subschemaname);
        }

        if (choicevalues != null) {
            HtmlSelect slchoicevalues= (HtmlSelect)form.getSelectByName("choicevalues");
            String[] fakeOptions = new String[choicevalues.size()];
            int cnt = 0;
            for (Iterator i = choicevalues.iterator(); i.hasNext(); ) {
                fakeOptions[cnt++] = (String)i.next();
            }
            slchoicevalues.fakeSelectedAttribute(fakeOptions);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * Remove choice values from attribute schema.
     *
     * @param webClient HTML Unit Web Client object.
     * @servicename Name of service.
     * @schematype Type of schema.
     * @attributename Name of attribute.
     * @choicevalues Choice values e.g. Inactive
     * @subschemaname Name of sub schema.
     */
    public HtmlPage removeAttributeChoiceValues(
        WebClient webClient,
        String servicename,
        String schematype,
        String attributename,
        List choicevalues,
        String subschemaname
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "remove-attribute-choice-values");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (servicename != null) {
            HtmlTextInput txtservicename = (HtmlTextInput)form.getInputByName("servicename");
            txtservicename.setValueAttribute(servicename);
        }

        if (schematype != null) {
            HtmlTextInput txtschematype = (HtmlTextInput)form.getInputByName("schematype");
            txtschematype.setValueAttribute(schematype);
        }

        if (attributename != null) {
            HtmlTextInput txtattributename = (HtmlTextInput)form.getInputByName("attributename");
            txtattributename.setValueAttribute(attributename);
        }

        if (choicevalues != null) {
            HtmlSelect slchoicevalues= (HtmlSelect)form.getSelectByName("choicevalues");
            String[] fakeOptions = new String[choicevalues.size()];
            int cnt = 0;
            for (Iterator i = choicevalues.iterator(); i.hasNext(); ) {
                fakeOptions[cnt++] = (String)i.next();
            }
            slchoicevalues.fakeSelectedAttribute(fakeOptions);
        }

        if (subschemaname != null) {
            HtmlTextInput txtsubschemaname = (HtmlTextInput)form.getInputByName("subschemaname");
            txtsubschemaname.setValueAttribute(subschemaname);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * Set boolean values of attribute schema.
     *
     * @param webClient HTML Unit Web Client object.
     * @servicename Name of service.
     * @schematype Type of schema.
     * @attributename Name of attribute.
     * @truevalue Value for true.
     * @truei18nkey Internationalization key for true value.
     * @falsevalue Value for false.
     * @falsei18nkey Internationalization key for false value.
     * @subschemaname Name of sub schema.
     */
    public HtmlPage setAttributeBooleanValues(
        WebClient webClient,
        String servicename,
        String schematype,
        String attributename,
        String truevalue,
        String truei18nkey,
        String falsevalue,
        String falsei18nkey,
        String subschemaname
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "set-attribute-boolean-values");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (servicename != null) {
            HtmlTextInput txtservicename = (HtmlTextInput)form.getInputByName("servicename");
            txtservicename.setValueAttribute(servicename);
        }

        if (schematype != null) {
            HtmlTextInput txtschematype = (HtmlTextInput)form.getInputByName("schematype");
            txtschematype.setValueAttribute(schematype);
        }

        if (attributename != null) {
            HtmlTextInput txtattributename = (HtmlTextInput)form.getInputByName("attributename");
            txtattributename.setValueAttribute(attributename);
        }

        if (truevalue != null) {
            HtmlTextInput txttruevalue = (HtmlTextInput)form.getInputByName("truevalue");
            txttruevalue.setValueAttribute(truevalue);
        }

        if (truei18nkey != null) {
            HtmlTextInput txttruei18nkey = (HtmlTextInput)form.getInputByName("truei18nkey");
            txttruei18nkey.setValueAttribute(truei18nkey);
        }

        if (falsevalue != null) {
            HtmlTextInput txtfalsevalue = (HtmlTextInput)form.getInputByName("falsevalue");
            txtfalsevalue.setValueAttribute(falsevalue);
        }

        if (falsei18nkey != null) {
            HtmlTextInput txtfalsei18nkey = (HtmlTextInput)form.getInputByName("falsei18nkey");
            txtfalsei18nkey.setValueAttribute(falsei18nkey);
        }

        if (subschemaname != null) {
            HtmlTextInput txtsubschemaname = (HtmlTextInput)form.getInputByName("subschemaname");
            txtsubschemaname.setValueAttribute(subschemaname);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * Create new metadata template.
     *
     * @param webClient HTML Unit Web Client object.
     * @entityid Entity ID
     * @metadata Specify file name for the standard metadata to be created.
     * @extended Specify file name for the standard metadata to be created.
     * @serviceprovider Specify metaAlias for hosted service provider to be created. The format must be <realm>/<unique string without /> for non-root realm or /<unique string without /> for root realm.
     * @identityprovider Specify metaAlias for hosted identity provider to be created. The format must be <realm>/<unique string without /> for non-root realm or /<unique string without /> for root realm.
     * @spscertalias Service provider signing certificate alias
     * @idpscertalias Identity provider signing certificate alias
     * @specertalias Service provider encryption certificate alias
     * @idpecertalias Identity provider encryption certificate alias.
     * @spec Specify metadata specification, either idff or saml2, defaults to saml2
     */
    public HtmlPage createMetadataTemplate(
        WebClient webClient,
        String entityid,
        boolean metadata,
        boolean extended,
        String serviceprovider,
        String identityprovider,
        String spscertalias,
        String idpscertalias,
        String specertalias,
        String idpecertalias,
        String spec
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "create-metadata-template");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (entityid != null) {
            HtmlTextInput txtentityid = (HtmlTextInput)form.getInputByName("entityid");
            txtentityid.setValueAttribute(entityid);
        }

        HtmlCheckBoxInput cbmetadata = (HtmlCheckBoxInput)form.getInputByName("metadata");
        cbmetadata.setChecked(metadata);

        HtmlCheckBoxInput cbextended = (HtmlCheckBoxInput)form.getInputByName("extended");
        cbextended.setChecked(extended);

        if (serviceprovider != null) {
            HtmlTextInput txtserviceprovider = (HtmlTextInput)form.getInputByName("serviceprovider");
            txtserviceprovider.setValueAttribute(serviceprovider);
        }

        if (identityprovider != null) {
            HtmlTextInput txtidentityprovider = (HtmlTextInput)form.getInputByName("identityprovider");
            txtidentityprovider.setValueAttribute(identityprovider);
        }

        if (spscertalias != null) {
            HtmlTextInput txtspscertalias = (HtmlTextInput)form.getInputByName("spscertalias");
            txtspscertalias.setValueAttribute(spscertalias);
        }

        if (idpscertalias != null) {
            HtmlTextInput txtidpscertalias = (HtmlTextInput)form.getInputByName("idpscertalias");
            txtidpscertalias.setValueAttribute(idpscertalias);
        }

        if (specertalias != null) {
            HtmlTextInput txtspecertalias = (HtmlTextInput)form.getInputByName("specertalias");
            txtspecertalias.setValueAttribute(specertalias);
        }

        if (idpecertalias != null) {
            HtmlTextInput txtidpecertalias = (HtmlTextInput)form.getInputByName("idpecertalias");
            txtidpecertalias.setValueAttribute(idpecertalias);
        }

        if (spec != null) {
            HtmlTextInput txtspec = (HtmlTextInput)form.getInputByName("spec");
            txtspec.setValueAttribute(spec);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * Import entity.
     *
     * @param webClient HTML Unit Web Client object.
     * @metadata Specify file name for the standard metadata to be imported.
     * @extended Specify file name for the extended entity configuration to be imported.
     * @cot Specify name of the Circle of Trust this entity belongs.
     * @spec Specify metadata specification, either idff or saml2, defaults to saml2
     */
    public HtmlPage importEntity(
        WebClient webClient,
        String metadata,
        String extended,
        String cot,
        String spec
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "import-entity");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (metadata != null) {
            HtmlTextArea tametadata = (HtmlTextArea)form.getTextAreasByName("metadata").get(0);
            tametadata.setText(metadata);
        }

        if (extended != null) {
            HtmlTextArea taextended = (HtmlTextArea)form.getTextAreasByName("extended").get(0);
            taextended.setText(extended);
        }

        if (cot != null) {
            HtmlTextInput txtcot = (HtmlTextInput)form.getInputByName("cot");
            txtcot.setValueAttribute(cot);
        }

        if (spec != null) {
            HtmlTextInput txtspec = (HtmlTextInput)form.getInputByName("spec");
            txtspec.setValueAttribute(spec);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * Export entity.
     *
     * @param webClient HTML Unit Web Client object.
     * @entityid Entity ID
     * @realm Realm where data resides
     * @sign Set this flag to sign the metadata
     * @metadata Metadata
     * @extended Extended data
     * @spec Specify metadata specification, either idff or saml2, defaults to saml2
     */
    public HtmlPage exportEntity(
        WebClient webClient,
        String entityid,
        String realm,
        boolean sign,
        boolean metadata,
        boolean extended,
        String spec
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "export-entity");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (entityid != null) {
            HtmlTextInput txtentityid = (HtmlTextInput)form.getInputByName("entityid");
            txtentityid.setValueAttribute(entityid);
        }

        if (realm != null) {
            HtmlTextInput txtrealm = (HtmlTextInput)form.getInputByName("realm");
            txtrealm.setValueAttribute(realm);
        }

        HtmlCheckBoxInput cbsign = (HtmlCheckBoxInput)form.getInputByName("sign");
        cbsign.setChecked(sign);

        HtmlCheckBoxInput cbmetadata = (HtmlCheckBoxInput)form.getInputByName("metadata");
        cbmetadata.setChecked(metadata);

        HtmlCheckBoxInput cbextended = (HtmlCheckBoxInput)form.getInputByName("extended");
        cbextended.setChecked(extended);

        if (spec != null) {
            HtmlTextInput txtspec = (HtmlTextInput)form.getInputByName("spec");
            txtspec.setValueAttribute(spec);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * Delete entity.
     *
     * @param webClient HTML Unit Web Client object.
     * @entityid Entity ID
     * @realm Realm where data resides
     * @extendedonly Set to flag to delete only extended data.
     * @spec Specify metadata specification, either idff or saml2, defaults to saml2
     */
    public HtmlPage deleteEntity(
        WebClient webClient,
        String entityid,
        String realm,
        boolean extendedonly,
        String spec
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "delete-entity");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (entityid != null) {
            HtmlTextInput txtentityid = (HtmlTextInput)form.getInputByName("entityid");
            txtentityid.setValueAttribute(entityid);
        }

        if (realm != null) {
            HtmlTextInput txtrealm = (HtmlTextInput)form.getInputByName("realm");
            txtrealm.setValueAttribute(realm);
        }

        HtmlCheckBoxInput cbextendedonly = (HtmlCheckBoxInput)form.getInputByName("extendedonly");
        cbextendedonly.setChecked(extendedonly);

        if (spec != null) {
            HtmlTextInput txtspec = (HtmlTextInput)form.getInputByName("spec");
            txtspec.setValueAttribute(spec);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * List entities under a realm.
     *
     * @param webClient HTML Unit Web Client object.
     * @realm Realm where entities reside.
     * @spec Specify metadata specification, either idff or saml2, defaults to saml2
     */
    public HtmlPage listEntities(
        WebClient webClient,
        String realm,
        String spec
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "list-entities");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (realm != null) {
            HtmlTextInput txtrealm = (HtmlTextInput)form.getInputByName("realm");
            txtrealm.setValueAttribute(realm);
        }

        if (spec != null) {
            HtmlTextInput txtspec = (HtmlTextInput)form.getInputByName("spec");
            txtspec.setValueAttribute(spec);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * Create circle of trust.
     *
     * @param webClient HTML Unit Web Client object.
     * @cot Circle of Trust
     * @realm Realm where circle of trust resides
     * @trustedproviders Trusted Providers
     * @prefix Prefix URL for idp discovery reader and writer URL.
     * @spec Specify metadata specification, either idff or saml2, defaults to saml2
     */
    public HtmlPage createCircleOfTrust(
        WebClient webClient,
        String cot,
        String realm,
        List trustedproviders,
        String prefix,
        String spec
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "create-circle-of-trust");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (cot != null) {
            HtmlTextInput txtcot = (HtmlTextInput)form.getInputByName("cot");
            txtcot.setValueAttribute(cot);
        }

        if (realm != null) {
            HtmlTextInput txtrealm = (HtmlTextInput)form.getInputByName("realm");
            txtrealm.setValueAttribute(realm);
        }

        if (trustedproviders != null) {
            HtmlSelect sltrustedproviders= (HtmlSelect)form.getSelectByName("trustedproviders");
            String[] fakeOptions = new String[trustedproviders.size()];
            int cnt = 0;
            for (Iterator i = trustedproviders.iterator(); i.hasNext(); ) {
                fakeOptions[cnt++] = (String)i.next();
            }
            sltrustedproviders.fakeSelectedAttribute(fakeOptions);
        }

        if (prefix != null) {
            HtmlTextInput txtprefix = (HtmlTextInput)form.getInputByName("prefix");
            txtprefix.setValueAttribute(prefix);
        }

        if (spec != null) {
            HtmlTextInput txtspec = (HtmlTextInput)form.getInputByName("spec");
            txtspec.setValueAttribute(spec);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * Delete circle of trust.
     *
     * @param webClient HTML Unit Web Client object.
     * @cot Circle of Trust
     * @realm Realm where circle of trust resides
     * @spec Specify metadata specification, either idff or saml2, defaults to saml2
     */
    public HtmlPage deleteCircleOfTrust(
        WebClient webClient,
        String cot,
        String realm,
        String spec
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "delete-circle-of-trust");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (cot != null) {
            HtmlTextInput txtcot = (HtmlTextInput)form.getInputByName("cot");
            txtcot.setValueAttribute(cot);
        }

        if (realm != null) {
            HtmlTextInput txtrealm = (HtmlTextInput)form.getInputByName("realm");
            txtrealm.setValueAttribute(realm);
        }

        if (spec != null) {
            HtmlTextInput txtspec = (HtmlTextInput)form.getInputByName("spec");
            txtspec.setValueAttribute(spec);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * List circle of trusts.
     *
     * @param webClient HTML Unit Web Client object.
     * @realm Realm where circle of trusts reside
     * @spec Specify metadata specification, either idff or saml2, defaults to saml2
     */
    public HtmlPage listCircleOfTrusts(
        WebClient webClient,
        String realm,
        String spec
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "list-circle-of-trusts");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (realm != null) {
            HtmlTextInput txtrealm = (HtmlTextInput)form.getInputByName("realm");
            txtrealm.setValueAttribute(realm);
        }

        if (spec != null) {
            HtmlTextInput txtspec = (HtmlTextInput)form.getInputByName("spec");
            txtspec.setValueAttribute(spec);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * List the members in a circle of trust.
     *
     * @param webClient HTML Unit Web Client object.
     * @cot Circle of Trust
     * @realm Realm where circle of trust resides
     * @spec Specify metadata specification, either idff or saml2, defaults to saml2
     */
    public HtmlPage listCircleOfTrustMembers(
        WebClient webClient,
        String cot,
        String realm,
        String spec
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "list-circle-of-trust-members");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (cot != null) {
            HtmlTextInput txtcot = (HtmlTextInput)form.getInputByName("cot");
            txtcot.setValueAttribute(cot);
        }

        if (realm != null) {
            HtmlTextInput txtrealm = (HtmlTextInput)form.getInputByName("realm");
            txtrealm.setValueAttribute(realm);
        }

        if (spec != null) {
            HtmlTextInput txtspec = (HtmlTextInput)form.getInputByName("spec");
            txtspec.setValueAttribute(spec);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * Remove a member from a circle of trust.
     *
     * @param webClient HTML Unit Web Client object.
     * @cot Circle of Trust
     * @entityid Entity ID
     * @realm Realm where circle of trust resides
     * @spec Specify metadata specification, either idff or saml2, defaults to saml2
     */
    public HtmlPage removeCircleOfTrustMember(
        WebClient webClient,
        String cot,
        String entityid,
        String realm,
        String spec
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "remove-circle-of-trust-member");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (cot != null) {
            HtmlTextInput txtcot = (HtmlTextInput)form.getInputByName("cot");
            txtcot.setValueAttribute(cot);
        }

        if (entityid != null) {
            HtmlTextInput txtentityid = (HtmlTextInput)form.getInputByName("entityid");
            txtentityid.setValueAttribute(entityid);
        }

        if (realm != null) {
            HtmlTextInput txtrealm = (HtmlTextInput)form.getInputByName("realm");
            txtrealm.setValueAttribute(realm);
        }

        if (spec != null) {
            HtmlTextInput txtspec = (HtmlTextInput)form.getInputByName("spec");
            txtspec.setValueAttribute(spec);
        }


        return (HtmlPage)form.submit();
    }

    /**
     * Add a member to a circle of trust.
     *
     * @param webClient HTML Unit Web Client object.
     * @cot Circle of Trust
     * @entityid Entity ID
     * @realm Realm where circle of trust resides
     * @spec Specify metadata specification, either idff or saml2, defaults to saml2
     */
    public HtmlPage addCircleOfTrustMember(
        WebClient webClient,
        String cot,
        String entityid,
        String realm,
        String spec
    ) throws Exception {
        URL cmdUrl = new URL(amadmUrl + "add-circle-of-trust-member");
        HtmlPage page = (HtmlPage)webClient.getPage(cmdUrl);
        HtmlForm form = (HtmlForm)page.getForms().get(0);

        if (cot != null) {
            HtmlTextInput txtcot = (HtmlTextInput)form.getInputByName("cot");
            txtcot.setValueAttribute(cot);
        }

        if (entityid != null) {
            HtmlTextInput txtentityid = (HtmlTextInput)form.getInputByName("entityid");
            txtentityid.setValueAttribute(entityid);
        }

        if (realm != null) {
            HtmlTextInput txtrealm = (HtmlTextInput)form.getInputByName("realm");
            txtrealm.setValueAttribute(realm);
        }

        if (spec != null) {
            HtmlTextInput txtspec = (HtmlTextInput)form.getInputByName("spec");
            txtspec.setValueAttribute(spec);
        }


        return (HtmlPage)form.submit();
    }
}

