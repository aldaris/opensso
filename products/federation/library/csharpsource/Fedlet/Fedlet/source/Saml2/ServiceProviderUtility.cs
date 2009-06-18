/*
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
 * $Id: ServiceProviderUtility.cs,v 1.6 2009-06-18 22:20:15 ggennaro Exp $
 */

using System;
using System.Collections;
using System.Collections.Specialized;
using System.Globalization;
using System.IO;
using System.Net;
using System.Security.Cryptography;
using System.Security.Cryptography.Xml;
using System.Text;
using System.Text.RegularExpressions;
using System.Web;
using System.Xml;
using Sun.Identity.Common;
using Sun.Identity.Properties;
using Sun.Identity.Saml2.Exceptions;

namespace Sun.Identity.Saml2
{
    /// <summary>
    /// Utility class to encapsulate configuration and metadata management
    /// along with convenience methods for retrieveing SAML2 objects.
    /// </summary>
    public class ServiceProviderUtility
    {
        #region Members

        /// <summary>
        /// Home folder containing configuration and metadata.
        /// </summary>
        private string homeFolder;

        #endregion

        #region Constructors

        /// <summary>
        /// Initializes a new instance of the ServiceProviderUtility class
        /// using the App_Data folder for the application as the default home
        /// folder for configuration and metadata.
        /// </summary>
        /// <param name="context">HttpContext used for reading application data.</param>
        public ServiceProviderUtility(HttpContext context)
        {
            this.Initialize(context.Server.MapPath(@"App_Data"));
        }

        /// <summary>
        /// Initializes a new instance of the ServiceProviderUtility class
        /// using the given home folder for configuration and metadata.
        /// </summary>
        /// <param name="homeFolder">Home folder containing configuration and metadata.</param>
        public ServiceProviderUtility(string homeFolder)
        {
            this.Initialize(homeFolder);
        }
        
        #endregion

        #region Properties
        /// <summary>
        /// Gets the service provider configured for the hosted application.
        /// </summary>
        public ServiceProvider ServiceProvider { get; private set; }

        /// <summary>
        /// Gets the collection of identity providers configured for the
        /// hosted application where the key is the identity provider's
        /// entity ID.
        /// </summary>
        public Hashtable IdentityProviders { get; private set; }

        /// <summary>
        /// Gets the collection of circle-of-trusts configured for the
        /// hosted application where the key is the circle-of-trust's
        /// "cot-name".
        /// </summary>
        public Hashtable CircleOfTrusts { get; private set; }
        #endregion

        #region Methods

        /// <summary>
        /// Retrieve the AuthnResponse object found within the HttpRequest
        /// in the context of the HttpContext, performing validation of
        /// the AuthnResponse prior to returning to the user.
        /// </summary>
        /// <param name="context">
        /// HttpContext containing session, request, and response objects.
        /// </param>
        /// <returns>AuthnResponse object</returns>
        public AuthnResponse GetAuthnResponse(HttpContext context)
        {
            ArtifactResponse artifactResponse = null;
            AuthnResponse authnResponse = null;
            ICollection authnRequests = AuthnRequestCache.GetSentAuthnRequests(context);
            HttpRequest request = context.Request;

            // Check if a saml response was received...
            if (string.IsNullOrEmpty(request[Saml2Constants.ResponseParameter])
                && string.IsNullOrEmpty(request[Saml2Constants.ArtifactParameter]))
            {
                throw new ServiceProviderUtilityException(Resources.ServiceProviderUtilityNoSamlResponseReceived);
            }

            // Obtain AuthnResponse object from either HTTP-POST or HTTP-Artifact
            if (request[Saml2Constants.ResponseParameter] != null)
            {
                string samlResponse = Saml2Utils.ConvertFromBase64(request[Saml2Constants.ResponseParameter]);
                authnResponse = new AuthnResponse(samlResponse);

                XmlDocument xmlDoc = (XmlDocument)authnResponse.XmlDom;
                StringBuilder logMessage = new StringBuilder();
                logMessage.Append("AuthnResponse:\r\n").Append(xmlDoc.OuterXml);
                FedletLogger.Info(logMessage.ToString());
            }
            else if (request[Saml2Constants.ArtifactParameter] != null)
            {
                Artifact artifact = new Artifact(request[Saml2Constants.ArtifactParameter]);
                artifactResponse = this.GetArtifactResponse(artifact);
                authnResponse = artifactResponse.AuthnResponse;

                XmlDocument xmlDoc = (XmlDocument)artifactResponse.XmlDom;
                StringBuilder logMessage = new StringBuilder();
                logMessage.Append("ArtifactResponse:\r\n").Append(xmlDoc.OuterXml);
                FedletLogger.Info(logMessage.ToString());
            }

            string prevAuthnRequestId = authnResponse.InResponseTo;
            try
            {
                if (artifactResponse != null)
                {
                    this.ValidateForArtifactProfile(artifactResponse, authnRequests);
                }
                else
                {
                    this.ValidateForPostProfile(authnResponse, authnRequests);
                }
            }
            catch (Saml2Exception se)
            {
                // log and throw again...
                XmlDocument authnResponseXml = (XmlDocument)authnResponse.XmlDom;
                StringBuilder logMessage = new StringBuilder();
                logMessage.Append(se.Message).Append("\r\n").Append(authnResponseXml.InnerXml);
                FedletLogger.Warning(logMessage.ToString());
                throw;
            }
            finally
            {
                AuthnRequestCache.RemoveSentAuthnRequest(context, prevAuthnRequestId);
            }

            return authnResponse;
        }

        /// <summary>
        /// Retrieve the ArtifactResponse object with the given SAMLv2 
        /// artifact.
        /// </summary>
        /// <param name="artifact">SAMLv2 artifact</param>
        /// <returns>ArtifactResponse object</returns>
        public ArtifactResponse GetArtifactResponse(Artifact artifact)
        {
            ArtifactResolve artifactResolve = new ArtifactResolve(this.ServiceProvider, artifact);
            ArtifactResponse artifactResponse = null;

            IdentityProvider idp = this.GetIdpFromArtifact(artifact);
            if (idp == null)
            {
                throw new ServiceProviderUtilityException(Resources.ServiceProviderUtilityIdpNotDeterminedFromArtifact);
            }

            string artifactResolutionSvcLoc = idp.GetArtifactResolutionServiceLocation(Saml2Constants.HttpSoapProtocolBinding);
            if (artifactResolutionSvcLoc == null)
            {
                throw new ServiceProviderUtilityException(Resources.ServiceProviderUtilityIdpArtifactResSvcLocNotDefined);
            }

            HttpWebRequest request = null;
            HttpWebResponse response = null;
            try
            {
                Uri artifactResolutionSvcUri = new Uri(artifactResolutionSvcLoc);
                request = (HttpWebRequest)WebRequest.Create(artifactResolutionSvcUri);
                XmlDocument artifactResolveXml = (XmlDocument)artifactResolve.XmlDom;
                string soapMessage = Saml2Utils.CreateSoapMessage(artifactResolveXml.InnerXml);

                byte[] byteArray = Encoding.UTF8.GetBytes(soapMessage);
                request.ContentType = "text/xml";
                request.ContentLength = byteArray.Length;
                request.AllowAutoRedirect = false;
                request.Method = "POST";

                Stream requestStream = request.GetRequestStream();
                requestStream.Write(byteArray, 0, byteArray.Length);
                requestStream.Close();

                XmlDocument xmlDoc = (XmlDocument)artifactResolve.XmlDom;
                StringBuilder logMessage = new StringBuilder();
                logMessage.Append("ArtifactResolve:\r\n").Append(xmlDoc.OuterXml);
                FedletLogger.Info(logMessage.ToString());

                response = (HttpWebResponse)request.GetResponse();
                StreamReader streamReader = new StreamReader(response.GetResponseStream());
                string responseContent = streamReader.ReadToEnd();
                streamReader.Close();

                XmlDocument soapResponse = new XmlDocument();
                soapResponse.PreserveWhitespace = true;
                soapResponse.LoadXml(responseContent);

                XmlNamespaceManager soapNsMgr = new XmlNamespaceManager(soapResponse.NameTable);
                soapNsMgr.AddNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");
                soapNsMgr.AddNamespace("samlp", "urn:oasis:names:tc:SAML:2.0:protocol");
                soapNsMgr.AddNamespace("saml", "urn:oasis:names:tc:SAML:2.0:assertion");
                soapNsMgr.AddNamespace("ds", "http://www.w3.org/2000/09/xmldsig#");

                XmlElement root = soapResponse.DocumentElement;
                XmlNode responseXml = root.SelectSingleNode("/soap:Envelope/soap:Body/samlp:ArtifactResponse", soapNsMgr);
                string artifactResponseXml = responseXml.OuterXml;

                artifactResponse = new ArtifactResponse(artifactResponseXml);

                if (artifactResolve.Id != artifactResponse.InResponseTo)
                {
                    throw new Saml2Exception(Resources.ArtifactResolutionInvalidInResponseTo);
                }
            }
            catch (WebException we)
            {
                throw new ServiceProviderUtilityException(Resources.ArtifactResolutionWebException, we);
            }
            finally
            {
                if (response != null)
                {
                    response.Close();
                }
            }

            return artifactResponse;
        }

        /// <summary>
        /// Gets the HTML for use of submitting the AuthnRequest with POST.
        /// </summary>
        /// <param name="authnRequest">
        /// AuthnRequest to packaged for a POST.
        /// </param>
        /// <param name="idpEntityId">Entity ID of the IDP.</param>
        /// <returns>
        /// HTML with auto-form submission with POST of the AuthnRequest
        /// </returns>
        public string GetAuthnRequestPostHtml(AuthnRequest authnRequest, string idpEntityId)
        {
            if (authnRequest == null)
            {
                throw new ServiceProviderUtilityException(Resources.AuthnRequestIsNull);
            }

            IdentityProvider idp = (IdentityProvider)this.IdentityProviders[idpEntityId];
            if (idp == null)
            {
                throw new ServiceProviderUtilityException(Resources.ServiceProviderUtilityIdentityProviderNotFound);
            }
            
            string ssoPostLocation = idp.GetSingleSignOnServiceLocation(Saml2Constants.HttpPostProtocolBinding);
            if (ssoPostLocation == null)
            {
                throw new ServiceProviderUtilityException(Resources.ServiceProviderUtilityIdpSingleSignOnSvcLocNotDefined);
            }

            string packagedAuthnRequest = Saml2Utils.ConvertToBase64(((XmlDocument)authnRequest.XmlDom).InnerXml);

            StringBuilder html = new StringBuilder();
            html.Append("<html><head><title>OpenSSO - SP initiated SSO</title></head>");
            html.Append("<body onload=\"document.forms[0].submit();\">");
            html.Append("<form method=\"post\" action=\"");
            html.Append(ssoPostLocation);
            html.Append("\">");
            html.Append("<input type=\"hidden\" name=\"");
            html.Append(Saml2Constants.RequestParameter);
            html.Append("\" value=\"");
            html.Append(packagedAuthnRequest);
            html.Append("\" />");
            html.Append("</form>");
            html.Append("</body>");
            html.Append("</html>");

            return html.ToString();
        }

        /// <summary>
        /// Gets the AuthnRequest location along with querystring parameters 
        /// to be used for actual browser requests.
        /// </summary>
        /// <param name="authnRequest">
        /// AuthnRequest to packaged for a POST.
        /// </param>
        /// <param name="idpEntityId">Entity ID of the IDP.</param>
        /// <returns>
        /// URL with query string parameter for the specified IDP.
        /// </returns>
        public string GetAuthnRequestRedirectLocation(AuthnRequest authnRequest, string idpEntityId)
        {
            if (authnRequest == null)
            {
                throw new ServiceProviderUtilityException(Resources.AuthnRequestIsNull);
            }

            IdentityProvider idp = (IdentityProvider)this.IdentityProviders[idpEntityId];
            if (idp == null)
            {
                throw new ServiceProviderUtilityException(Resources.ServiceProviderUtilityIdentityProviderNotFound);
            }

            string ssoRedirectLocation = idp.GetSingleSignOnServiceLocation(Saml2Constants.HttpRedirectProtocolBinding);
            if (ssoRedirectLocation == null)
            {
                throw new ServiceProviderUtilityException(Resources.ServiceProviderUtilityIdpSingleSignOnSvcLocNotDefined);
            }

            string packagedAuthnRequest = Saml2Utils.CompressConvertToBase64UrlEncode(authnRequest.XmlDom);

            StringBuilder redirectUrl = new StringBuilder();
            redirectUrl.Append(ssoRedirectLocation);
            redirectUrl.Append("?");
            redirectUrl.Append(Saml2Constants.RequestParameter);
            redirectUrl.Append("=");
            redirectUrl.Append(packagedAuthnRequest);

            return redirectUrl.ToString();
        }

        /// <summary>
        /// Sends an AuthnRequest to the specified IDP with the given 
        /// parameters.
        /// </summary>
        /// <param name="context">
        /// HttpContext containing session, request, and response objects.
        /// </param>
        /// <param name="idpEntityId">Entity ID of the IDP.</param>
        /// <param name="parameters">
        /// NameValueCollection of varying parameters for use in the 
        /// construction of the AuthnRequest.
        /// </param>
        public void SendAuthnRequest(HttpContext context, string idpEntityId, NameValueCollection parameters)
        {
            if (parameters == null)
            {
                parameters = new NameValueCollection();
            }

            AuthnRequest authnRequest = new AuthnRequest(this.ServiceProvider, parameters);
            XmlDocument xmlDoc = (XmlDocument)authnRequest.XmlDom;
            StringBuilder logMessage = new StringBuilder();
            logMessage.Append("AuthnRequest:\r\n").Append(xmlDoc.OuterXml);
            FedletLogger.Info(logMessage.ToString());

            // Add this AuthnRequest for this user for validation on AuthnResponse
            AuthnRequestCache.AddSentAuthnRequest(context, authnRequest);

            // Send with Redirect or Post based on the 'reqBinding' parameter.
            if (parameters[Saml2Constants.RequestBinding] == Saml2Constants.HttpPostProtocolBinding)
            {
                string postHtml = this.GetAuthnRequestPostHtml(authnRequest, idpEntityId);
                context.Response.Write(postHtml);
                context.Response.End();
            }
            else
            {
                string redirectUrl = this.GetAuthnRequestRedirectLocation(authnRequest, idpEntityId);
                context.Response.Redirect(redirectUrl.ToString(), true);
            }
        }

        /// <summary>
        /// Validates the given ArtifactResponse object.
        /// </summary>
        /// <param name="artifactResponse">ArtifactResponse object.</param>
        /// <param name="authnRequests">
        /// Collection of previously sent authnRequests used to compare with
        /// the InResponseTo attribute (if present) of the embedded 
        /// AuthnResponse within the ArtifactResponse.
        /// </param>
        /// <see cref="ServiceProviderUtility.Validate"/>
        public void ValidateForArtifactProfile(ArtifactResponse artifactResponse, ICollection authnRequests)
        {
            this.CheckSignature(artifactResponse);
            this.Validate(artifactResponse.AuthnResponse, authnRequests);
        }

        /// <summary>
        /// Validates the given AuthnResponse object.
        /// </summary>
        /// <param name="authnResponse">AuthnResponse object.</param>
        /// <param name="authnRequests">
        /// Collection of previously sent authnRequests used to compare with
        /// the InResponseTo attribute (if present) of the AuthnResponse.
        /// </param>
        /// <see cref="ServiceProviderUtility.Validate"/>
        public void ValidateForPostProfile(AuthnResponse authnResponse, ICollection authnRequests)
        {
            this.CheckSignature(authnResponse);
            this.Validate(authnResponse, authnRequests);
        }

        /// <summary>
        /// Validates the given AuthnResponse object except for xml signature.
        /// XML signature checking is expected to be done prior to calling
        /// this method based on the appropriate profile.
        /// </summary>
        /// <param name="authnResponse">AuthnResponse object.</param>
        /// <param name="authnRequests">
        /// Collection of previously sent authnRequests used to compare with
        /// the InResponseTo attribute of the AuthnResponse (if present).
        /// </param>
        /// <see cref="ServiceProviderUtility.ValidateForArtifactProfile"/>
        /// <see cref="ServiceProviderUtility.ValidateForPostProfile"/>
        public void Validate(AuthnResponse authnResponse, ICollection authnRequests)
        {
            if (authnResponse.InResponseTo != null)
            {
                ServiceProviderUtility.CheckInResponseTo(authnResponse, authnRequests);
            }

            this.CheckIssuer(authnResponse);
            ServiceProviderUtility.CheckStatusCode(authnResponse);
            ServiceProviderUtility.CheckConditionWithTime(authnResponse);
            this.CheckConditionWithAudience(authnResponse);
            this.CheckCircleOfTrust(authnResponse);
        }

        /// <summary>
        /// Checks the time condition of the given AuthnResponse.
        /// </summary>
        /// <param name="authnResponse">SAMLv2 AuthnResponse.</param>
        private static void CheckConditionWithTime(AuthnResponse authnResponse)
        {
            DateTime utcNow = DateTime.UtcNow;
            DateTime utcBefore = TimeZoneInfo.ConvertTimeToUtc(authnResponse.ConditionNotBefore);
            DateTime utcOnOrAfter = TimeZoneInfo.ConvertTimeToUtc(authnResponse.ConditionNotOnOrAfter);

            if (utcNow < utcBefore || utcNow >= utcOnOrAfter)
            {
                throw new Saml2Exception(Resources.AuthnResponseInvalidConditionTime);
            }
        }

        /// <summary>
        /// Checks the InResponseTo field of the given AuthnResponse to
        /// see if it is one of the managed authn requests.
        /// </summary>
        /// <param name="authnResponse">SAMLv2 AuthnResponse.</param>
        /// <param name="authnRequests">
        /// Collection of previously sent AuthnRequests.
        /// </param>
        private static void CheckInResponseTo(AuthnResponse authnResponse, ICollection authnRequests)
        {
            if (authnRequests != null && authnResponse.InResponseTo != null)
            {
                IEnumerator i = authnRequests.GetEnumerator();
                while (i.MoveNext())
                {
                    AuthnRequest authnRequest = (AuthnRequest)i.Current;
                    if (authnRequest.Id == authnResponse.InResponseTo)
                    {
                        // Found one, return quietly.
                        return;
                    }
                }
            }

            // Didn't find one, complain loudly.
            throw new Saml2Exception(Resources.AuthnResponseInvalidInResponseTo);
        }

        /// <summary>
        /// Checks the status code of the given AuthnResponse.
        /// </summary>
        /// <param name="authnResponse">SAMLv2 AuthnResponse.</param>
        private static void CheckStatusCode(AuthnResponse authnResponse)
        {
            if (authnResponse.StatusCode != Saml2Constants.Success)
            {
                throw new Saml2Exception(Resources.AuthnResponseInvalidStatusCode);
            }
        }

        /// <summary>
        /// Internal method to load configuration information and metadata
        /// for the hosted service provider and associated identity providers.
        /// </summary>
        /// <param name="homeFolder">Home folder containing configuration and metadata.</param>
        private void Initialize(string homeFolder)
        {
            DirectoryInfo dirInfo = new DirectoryInfo(homeFolder);
            if (!dirInfo.Exists)
            {
                throw new ServiceProviderUtilityException(Resources.ServiceProviderUtilityHomeFolderNotFound);
            }

            this.homeFolder = homeFolder;

            // Load the metadata for this service provider.
            this.ServiceProvider = new ServiceProvider(this.homeFolder);

            // Load the configuration for one or more circle of trusts.
            this.CircleOfTrusts = new Hashtable();
            this.InitializeCircleOfTrusts();

            // Load metadata for one or more identity providers.
            this.IdentityProviders = new Hashtable();
            this.InitializeIdentityProviders();
        }

        /// <summary>
        /// Internal method to load all configuration information for all
        /// circle of trusts found in the home folder.
        /// </summary>
        private void InitializeCircleOfTrusts()
        {
            DirectoryInfo dirInfo = new DirectoryInfo(this.homeFolder);
            FileInfo[] files = dirInfo.GetFiles("fedlet*.cot");

            foreach (FileInfo file in files)
            {
                CircleOfTrust cot = new CircleOfTrust(file.FullName);
                string key = cot.Attributes["cot-name"];
                this.CircleOfTrusts.Add(key, cot);
            }

            if (this.CircleOfTrusts.Count <= 0)
            {
                throw new ServiceProviderUtilityException(Resources.ServiceProviderUtiltyCircleOfTrustsNotFound);
            }
        }

        /// <summary>
        /// Internal method to load all configuration information for all
        /// identity providers' metadata founds in the home folder.
        /// </summary>
        private void InitializeIdentityProviders()
        {
            DirectoryInfo dirInfo = new DirectoryInfo(this.homeFolder);
            FileInfo[] files = dirInfo.GetFiles("idp*.xml");

            string metadataFilePattern = "idp(.*).xml";         // for regex.match
            string extendedFilePattern = "idp{0}-extended.xml"; // for string.format
            string fileIndex = null;

            foreach (FileInfo metadataFile in files)
            {
                Match m = Regex.Match(metadataFile.Name, metadataFilePattern);
                
                // determine index
                if (m.Success)
                {
                    fileIndex = m.Groups[1].Value;
                }

                string extendedFileName;
                if (fileIndex == null)
                {
                    extendedFileName = string.Format(CultureInfo.InvariantCulture, extendedFilePattern, string.Empty);
                }
                else
                {
                    extendedFileName = string.Format(CultureInfo.InvariantCulture, extendedFilePattern, fileIndex);
                }

                FileInfo extendedFile = new FileInfo(this.homeFolder + @"/" + extendedFileName);
                if (metadataFile.Exists && extendedFile.Exists)
                {
                    IdentityProvider identityProvider = new IdentityProvider(metadataFile.FullName, extendedFile.FullName);
                    this.IdentityProviders.Add(identityProvider.EntityId, identityProvider);
                }
            }

            if (this.IdentityProviders.Count <= 0)
            {
                throw new ServiceProviderUtilityException(Resources.ServiceProviderUtilityIdentityProvidersNotFound);
            }
        }

        /// <summary>
        /// Checks the issuer of the given AuthnResponse.
        /// </summary>
        /// <param name="authnResponse">SAMLv2 AuthnResponse.</param>
        private void CheckIssuer(AuthnResponse authnResponse)
        {
            if (!this.IdentityProviders.ContainsKey(authnResponse.Issuer))
            {
                throw new Saml2Exception(Resources.AuthnResponseInvalidIssuer);
            }
        }

        /// <summary>
        /// Checks the audience condition of the given AuthnResponse.
        /// </summary>
        /// <param name="authnResponse">SAMLv2 AuthnResponse.</param>
        private void CheckConditionWithAudience(AuthnResponse authnResponse)
        {
            if (!authnResponse.ConditionAudiences.Contains(this.ServiceProvider.EntityId))
            {
                throw new Saml2Exception(Resources.AuthnResponseInvalidConditionAudience);
            }
        }

        /// <summary>
        /// Checks the signature of the given ArtifactResponse and embedded
        /// AuthnResponse used for the Artifact profile.
        /// </summary>
        /// <param name="artifactResponse">ArtifactResponse object.</param>
        /// <seealso cref="ServiceProviderUtility.ValidateForArtifactProfile"/>
        private void CheckSignature(ArtifactResponse artifactResponse)
        {
            AuthnResponse authnResponse = artifactResponse.AuthnResponse;

            IdentityProvider identityProvider = (IdentityProvider)this.IdentityProviders[authnResponse.Issuer];
            if (identityProvider == null)
            {
                throw new Saml2Exception(Resources.AuthnResponseInvalidIssuer);
            }

            XmlElement artifactResponseSignature = (XmlElement)artifactResponse.XmlSignature;
            XmlElement responseSignature = (XmlElement)authnResponse.XmlResponseSignature;
            XmlElement assertionSignature = (XmlElement)authnResponse.XmlAssertionSignature;

            XmlElement validationSignature = null;
            string validationSignatureCert = null;
            string validationReferenceId = null;

            if (this.ServiceProvider.WantArtifactResponseSigned && artifactResponseSignature == null)
            {
                throw new Saml2Exception(Resources.AuthnResponseInvalidSignatureMissingOnArtifactResponse);
            }
            else if (this.ServiceProvider.WantPostResponseSigned && responseSignature == null && artifactResponseSignature == null)
            {
                throw new Saml2Exception(Resources.AuthnResponseInvalidSignatureMissingOnResponse);
            }
            else if (this.ServiceProvider.WantAssertionsSigned && assertionSignature == null && responseSignature == null && artifactResponseSignature == null) 
            {
                throw new Saml2Exception(Resources.AuthnResponseInvalidSignatureMissing);
            }

            // pick the ArtifactResponse, Response or the Assertion for further validation...
            if (artifactResponseSignature != null)
            {
                validationSignature = artifactResponseSignature;
                validationSignatureCert = artifactResponse.SignatureCertificate;
                validationReferenceId = artifactResponse.Id;
            }
            else if (responseSignature != null)
            {
                validationSignature = responseSignature;
                validationSignatureCert = authnResponse.ResponseSignatureCertificate;
                validationReferenceId = authnResponse.Id;
            }
            else
            {
                validationSignature = assertionSignature;
                validationSignatureCert = authnResponse.AssertionSignatureCertificate;
                validationReferenceId = authnResponse.AssertionId;
            }

            if (validationSignatureCert != null)
            {
                string idpCert = Regex.Replace(identityProvider.EncodedSigningCertificate, @"\s", string.Empty);
                validationSignatureCert = Regex.Replace(validationSignatureCert, @"\s", string.Empty);
                if (idpCert != validationSignatureCert)
                {
                    throw new Saml2Exception(Resources.AuthnResponseInvalidSignatureCertsDontMatch);
                }
            }

            // check the signature of the xml document (optional for artifact)
            if (validationSignature != null) 
            {
                Saml2Utils.ValidateSignedXml(
                                             identityProvider.SigningCertificate,
                                             (XmlDocument)artifactResponse.XmlDom,
                                             validationSignature,
                                             validationReferenceId);
            }
        }
        
        /// <summary>
        /// Checks the signature of the given AuthnResponse used for the POST
        /// profile.
        /// </summary>
        /// <param name="authnResponse">AuthnResponse object.</param>
        /// <seealso cref="ServiceProviderUtility.ValidateForPostProfile"/>
        private void CheckSignature(AuthnResponse authnResponse)
        {
            IdentityProvider identityProvider = (IdentityProvider)this.IdentityProviders[authnResponse.Issuer];
            if (identityProvider == null)
            {
                throw new Saml2Exception(Resources.AuthnResponseInvalidIssuer);
            }

            XmlElement responseSignature = (XmlElement)authnResponse.XmlResponseSignature;
            XmlElement assertionSignature = (XmlElement)authnResponse.XmlAssertionSignature;
            XmlElement validationSignature = null;
            string validationSignatureCert = null;
            string validationReferenceId = null;

            if (responseSignature == null && assertionSignature == null)
            {
                throw new Saml2Exception(Resources.AuthnResponseInvalidSignatureMissing);
            }
            else if (this.ServiceProvider.WantPostResponseSigned && responseSignature == null)
            {
                throw new Saml2Exception(Resources.AuthnResponseInvalidSignatureMissingOnResponse);
            }

            // pick the Response or the Assertion for further validation...
            if (responseSignature != null)
            {
                validationSignature = responseSignature;
                validationSignatureCert = authnResponse.ResponseSignatureCertificate;
                validationReferenceId = authnResponse.Id;
            }
            else
            {
                validationSignature = assertionSignature;
                validationSignatureCert = authnResponse.AssertionSignatureCertificate;
                validationReferenceId = authnResponse.AssertionId;
            }

            if (validationSignatureCert != null)
            {
                string idpCert = Regex.Replace(identityProvider.EncodedSigningCertificate, @"\s", string.Empty);
                validationSignatureCert = Regex.Replace(validationSignatureCert, @"\s", string.Empty);
                if (idpCert != validationSignatureCert)
                {
                    throw new Saml2Exception(Resources.AuthnResponseInvalidSignatureCertsDontMatch);
                }
            }

            // check the signature of the xml document (always for post)
            Saml2Utils.ValidateSignedXml(
                                         identityProvider.SigningCertificate,
                                         (XmlDocument)authnResponse.XmlDom,
                                         validationSignature,
                                         validationReferenceId);
        }

        /// <summary>
        /// Checks to confirm the issuer and hosted service provider are in
        /// the same circle of trust for the the given AuthnResponse.
        /// </summary>
        /// <param name="authnResponse">SAMLv2 AuthnResponse.</param>
        private void CheckCircleOfTrust(AuthnResponse authnResponse)
        {
            string spEntityId = this.ServiceProvider.EntityId;
            string authIdpEntityId = authnResponse.Issuer;

            foreach (string cotName in this.CircleOfTrusts.Keys)
            {
                CircleOfTrust cot = (CircleOfTrust) this.CircleOfTrusts[cotName];
                if (cot.AreProvidersTrusted(spEntityId, authIdpEntityId))
                {
                    return;
                }
            }

            throw new Saml2Exception(Resources.AuthnResponseNotInCircleOfTrust);
        }

        /// <summary>
        /// Gets the Identity Provider associated with the specified artifact.
        /// The currently maintained list of IDPs each have their entity ID
        /// hashed and compared with the given artifact's source ID to make
        /// the correct determination.
        /// </summary>
        /// <param name="artifact">SAML artifact.</param>
        /// <returns>
        /// Identity Provider who's entity ID matches the source ID
        /// within the artifact, null if not found.
        /// </returns>
        private IdentityProvider GetIdpFromArtifact(Artifact artifact)
        {
            SHA1 sha1 = new SHA1CryptoServiceProvider();
            IdentityProvider idp = null;
            string idpEntityIdHashed = null;

            foreach (string idpEntityId in this.IdentityProviders.Keys)
            {
                idpEntityIdHashed = BitConverter.ToString(sha1.ComputeHash(Encoding.UTF8.GetBytes(idpEntityId)));
                idpEntityIdHashed = idpEntityIdHashed.Replace("-", string.Empty);

                if (idpEntityIdHashed == artifact.SourceId)
                {
                    idp = (IdentityProvider)this.IdentityProviders[idpEntityId];
                    break;
                }
            }

            return idp;
        }

        #endregion
    }
}
