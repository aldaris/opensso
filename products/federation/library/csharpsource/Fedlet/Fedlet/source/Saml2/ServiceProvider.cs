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
 * $Id: ServiceProvider.cs,v 1.1 2009-05-01 15:19:55 ggennaro Exp $
 */

using System.IO;
using System.Xml;
using Sun.Identity.Saml2.Exceptions;
using Sun.Identity.Properties;

namespace Sun.Identity.Saml2
{
    /// <summary>
    /// Service Provider (SP) for the Fedlet in the ASP.NET environment. 
    /// </summary>
    public class ServiceProvider
    {
        #region Members
        private static string metadataFilename = "sp.xml";
        private static string extendedMetadataFilename = "sp-extended.xml";
        private XmlDocument metadata;
        private XmlNamespaceManager metadataNsMgr;
        private XmlDocument extendedMetadata;
        private XmlNamespaceManager extendedMetadataNsMgr;
        #endregion

        #region Properties
        /// <summary>
        /// Gets the entity ID for this service provider.
        /// </summary>
        public string EntityId
        {
            get 
            {
                string xpath = "/md:EntityDescriptor";
                XmlNode root = this.metadata.DocumentElement;
                XmlNode node = root.SelectSingleNode(xpath, this.metadataNsMgr);
                return node.Attributes["entityID"].Value.Trim();
            }
        }

        /// <summary>
        /// Gets the meta alias for this service provider.
        /// </summary>
        public string MetaAlias
        {
            get
            {
                string xpath = "/mdx:EntityConfig/mdx:SPSSOConfig";
                XmlNode root = this.extendedMetadata.DocumentElement;
                XmlNode node = root.SelectSingleNode(xpath, this.extendedMetadataNsMgr);
                return node.Attributes["metaAlias"].Value.Trim();
            }
        }
        #endregion

        #region Methods

        /// <summary>
        /// Constructor to load metadata files found in the specified home folder. 
        /// </summary>
        /// <param name="homeFolder"></param>
        public ServiceProvider(string homeFolder)
        {
            try
            {
                this.metadata = new XmlDocument();
                this.metadata.Load(homeFolder + "\\" + ServiceProvider.metadataFilename);
                this.metadataNsMgr = new XmlNamespaceManager(this.metadata.NameTable);
                this.metadataNsMgr.AddNamespace("md", "urn:oasis:names:tc:SAML:2.0:metadata");

                this.extendedMetadata = new XmlDocument();
                this.extendedMetadata.Load(homeFolder + "\\" + ServiceProvider.extendedMetadataFilename);
                this.extendedMetadataNsMgr = new XmlNamespaceManager(this.extendedMetadata.NameTable);
                this.extendedMetadataNsMgr.AddNamespace("mdx", "urn:sun:fm:SAML:2.0:entityconfig");
            }
            catch (DirectoryNotFoundException dnfe)
            {
                throw new ServiceProviderException(Resources.ServiceProviderDirNotFound, dnfe);
            }
            catch (FileNotFoundException fnfe)
            {
                throw new ServiceProviderException(Resources.ServiceProviderFileNotFound, fnfe);
            }
            catch (XmlException xe)
            {
                throw new ServiceProviderException(Resources.ServiceProviderXmlException, xe);
            }
        }


        #endregion
    }
}
