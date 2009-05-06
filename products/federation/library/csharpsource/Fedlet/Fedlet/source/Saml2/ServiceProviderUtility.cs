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
 * $Id: ServiceProviderUtility.cs,v 1.2 2009-05-06 22:16:59 ggennaro Exp $
 */

using System;
using System.Collections;
using System.Globalization;
using System.IO;
using System.Security.Cryptography.X509Certificates;
using System.Security.Cryptography.Xml;
using System.Text;
using System.Web;
using System.Xml;
using Sun.Identity.Properties;
using Sun.Identity.Saml2.Exceptions;
using System.Text.RegularExpressions;

namespace Sun.Identity.Saml2
{
    /// <summary>
    /// Utility class to encapsulate configuration and metadata management
    /// along with convenience methods for retrieveing SAML2 objects.
    /// </summary>
    public class ServiceProviderUtility
    {
        #region Members
        private string homeFolder;
        private Hashtable circleOfTrusts;
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
        #endregion

        #region Constructors

        /// <summary>
        /// Constructor using the App_Data folder for the application as the 
        /// default home folder for configuration and metadata.
        /// </summary>
        /// <param name="context"></param>
        public ServiceProviderUtility(HttpContext context)
        {
            this.Initialize(context.Server.MapPath(@"App_Data"));
        }

        /// <summary>
        /// Constructor using the given home folder for configuration and
        /// metadata.
        /// </summary>
        /// <param name="homeFolder"></param>
        public ServiceProviderUtility(string homeFolder)
        {
            this.Initialize(homeFolder);
        }
        
        #endregion

        #region Methods
        /// <summary>
        /// Internal method to load configuration information and metadata
        /// for the hosted service provider and associated identity providers.
        /// </summary>
        /// <param name="homeFolder"></param>
        private void Initialize(string homeFolder) {

            DirectoryInfo dirInfo = new DirectoryInfo(homeFolder);
            if (!dirInfo.Exists)
                throw new ServiceProviderUtilityException(Resources.ServiceProviderUtilityHomeFolderNotFound);

            this.homeFolder = homeFolder;

            // Load the metadata for this service provider.
            this.ServiceProvider = new ServiceProvider(this.homeFolder);

            // Load the configuration for one or more circle of trusts.
            this.circleOfTrusts = new Hashtable();
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
                this.circleOfTrusts.Add(key, cot);
            }

            if (this.circleOfTrusts.Count <= 0)
                throw new ServiceProviderUtilityException(Resources.ServiceProviderUtiltyCircleOfTrustsNotFound);
        }

        /// <summary>
        /// Internal method to load all configuration information for all
        /// identity providers' metadata founds in the home folder.
        /// </summary>
        private void InitializeIdentityProviders()
        {
            int index = 0;
            bool done = false;
            string metadataFilePattern = "idp{0}.xml";
            string extendedFilePattern = "idp{0}-extended.xml";

            while (!done)
            {
                string metadataFileName;
                string extendedFileName;

                if (index == 0)
                {
                    metadataFileName = string.Format(CultureInfo.InvariantCulture, metadataFilePattern, string.Empty);
                    extendedFileName = string.Format(CultureInfo.InvariantCulture, extendedFilePattern, string.Empty);
                }
                else
                {
                    metadataFileName = string.Format(CultureInfo.InvariantCulture, metadataFilePattern, index);
                    extendedFileName = string.Format(CultureInfo.InvariantCulture, extendedFilePattern, index);
                }

                FileInfo metadataFile = new FileInfo(this.homeFolder + @"/" + metadataFileName);
                FileInfo extendedFile = new FileInfo(this.homeFolder + @"/" + extendedFileName);

                if (metadataFile.Exists && extendedFile.Exists)
                {
                    IdentityProvider identityProvider = new IdentityProvider(metadataFile.FullName, extendedFile.FullName);
                    this.IdentityProviders.Add(identityProvider.EntityId, identityProvider);
                    index++;
                }
                else
                {
                    done = true;
                }
            }

            if (this.IdentityProviders.Count <= 0)
                throw new ServiceProviderUtilityException(Resources.ServiceProviderUtilityIdentityProvidersNotFound);
        }

        /// <summary>
        /// Retrieve the AuthnResponse object found encoded within the HttpRequest form.
        /// </summary>
        /// <param name="request">HttpRequest containing the HTTP-POST SAMLResponse parameter.</param>
        /// <returns>AuthnResponse object</returns>
        public AuthnResponse GetAuthnResponse(HttpRequest request)
        {
            AuthnResponse authnResponse = null;

            if (request[Saml2Constants.HttpPostParameter] != null)
            {
                string encodedResponse = request[Saml2Constants.HttpPostParameter];
                byte[] byteArray = Convert.FromBase64String(encodedResponse);
                string samlResponse = Encoding.ASCII.GetString(byteArray);
                authnResponse = this.GetAuthnResponse(samlResponse);
            }
            else
            {
                throw new ServiceProviderUtilityException(Resources.ServiceProviderUtilityNoSamlResponseReceived);
            }

            return authnResponse;
        }

        /// <summary>
        /// Retrieve the AuthnResponse object with the given decoded SAMLv2 response string.
        /// </summary>
        /// <param name="samlResponse">SAMLv2 response.</param>
        /// <returns>AuthnResponse object</returns>
        public AuthnResponse GetAuthnResponse(string samlResponse)
        {
            AuthnResponse authnResponse = new AuthnResponse(samlResponse);

            CheckIssuer(authnResponse);
            CheckStatusCode(authnResponse);
            CheckSignature(authnResponse);
            CheckConditionWithTime(authnResponse);
            CheckConditionWithAudience(authnResponse);
            CheckCircleOfTrust(authnResponse);

            return authnResponse;
        }

        private void CheckIssuer(AuthnResponse authnResponse)
        {
            if (!this.IdentityProviders.ContainsKey(authnResponse.Issuer))
                throw new Saml2Exception(Resources.AuthnResponseInvalidIssuer);
        }

        private static void CheckStatusCode(AuthnResponse authnResponse)
        {
            if (authnResponse.StatusCode != Saml2Constants.Success)
                throw new Saml2Exception(Resources.AuthnResponseInvalidStatusCode);
        }

        private void CheckConditionWithAudience(AuthnResponse authnResponse)
        {
            if (!authnResponse.ConditionAudiences.Contains(this.ServiceProvider.EntityId))
                throw new Saml2Exception(Resources.AuthnResponseInvalidConditionAudience);
        }

        private static void CheckConditionWithTime(AuthnResponse authnResponse)
        {
            DateTime utcNow = DateTime.UtcNow;
            DateTime utcBefore = TimeZoneInfo.ConvertTimeToUtc(authnResponse.ConditionNotBefore);
            DateTime utcOnOrAfter = TimeZoneInfo.ConvertTimeToUtc(authnResponse.ConditionNotOnOrAfter);

            if (utcNow < utcBefore || utcNow >= utcOnOrAfter)
                throw new Saml2Exception(Resources.AuthnResponseInvalidConditionTime);
        }

        private void CheckSignature(AuthnResponse authnResponse)
        {
            IdentityProvider identityProvider = (IdentityProvider)this.IdentityProviders[authnResponse.Issuer];
            string idpCert = Regex.Replace(identityProvider.EncodedSigningCertificate, @"\s", "");
            string authCert = Regex.Replace(authnResponse.SignatureCertificate, @"\s", "");

            if (authCert != null)
            {
                if (authCert != idpCert)
                    throw new Saml2Exception(Resources.AuthnResponseInvalidSignatureCertsDontMatch);
            }

            SignedXml signedXml = new SignedXml((XmlDocument)authnResponse.XmlDom);
            XmlElement authSignatureElement = (XmlElement)authnResponse.XmlSignature;
            signedXml.LoadXml(authSignatureElement);
            bool results = signedXml.CheckSignature(identityProvider.SigningCertificate, true);

            if( results == false )
                throw new Saml2Exception(Resources.AuthnResponseInvalidSignature);
        }

        private void CheckCircleOfTrust(AuthnResponse authnResponse)
        {
            string spEntityId = this.ServiceProvider.EntityId;
            string authIdpEntityId = authnResponse.Issuer;

            foreach (string cotName in this.circleOfTrusts.Keys)
            {
                CircleOfTrust cot = (CircleOfTrust) this.circleOfTrusts[cotName];
                if (cot.AreProvidersTrusted(spEntityId, authIdpEntityId))
                    return;
            }
            throw new Saml2Exception(Resources.AuthnResponseNotInCircleOfTrust);
        }

        #endregion

    }
}
