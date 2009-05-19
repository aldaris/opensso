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
 * $Id: Saml2Utils.cs,v 1.1 2009-05-19 16:01:03 ggennaro Exp $
 */

using System;
using System.Globalization;
using System.IO;
using System.IO.Compression;
using System.Text;
using System.Web;
using System.Xml;
using System.Xml.XPath;

namespace Sun.Identity.Saml2
{
    /// <summary>
    /// Utility class for performing SAMLv2 operations.
    /// </summary>
    public static class Saml2Utils
    {
        #region Methods

        /// <summary>
        /// Converts the string from the base64 encoded input.
        /// </summary>
        /// <param name="value">Base64 encoded string.</param>
        /// <returns>String contained within the base64 encoded string.</returns>
        public static string ConvertFromBase64(string value)
        {
            byte[] byteArray = Convert.FromBase64String(value);
            return Encoding.UTF8.GetString(byteArray);
        }

        /// <summary>
        /// Converts the base64 encoded string of the given input string.
        /// </summary>
        /// <param name="value">String to be encoded.</param>
        /// <returns>Base64 encoded output of the specified string.</returns>
        public static string ConvertToBase64(string value)
        {
            return Convert.ToBase64String(Encoding.UTF8.GetBytes(value));
        }

        /// <summary>
        /// Creates a SOAP message, with no header, to encompass the specified
        /// xml payload in its body.
        /// </summary>
        /// <param name="xmlPayload">XML to be placed within the body of this message.</param>
        /// <returns>String representation of the SOAP message.</returns>
        public static string CreateSoapMessage(string xmlPayload)
        {
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.Append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.Append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" >");
            soapMessage.Append("  <soap:Body>");
            soapMessage.Append(xmlPayload);
            soapMessage.Append("  </soap:Body>");
            soapMessage.Append("</soap:Envelope>");

            return soapMessage.ToString();
        }

        /// <summary>
        /// Generates a random ID for use in SAMLv2 assertions, requests, and
        /// responses.
        /// </summary>
        /// <returns>String representing a random ID with length specified by Saml2Constants.IdLength</returns>
        public static string GenerateId()
        {
            Random random = new Random();
            byte[] byteArray = new byte[Saml2Constants.IdLength];
            random.NextBytes(byteArray);
            string id = BitConverter.ToString(byteArray).Replace("-", string.Empty);

            return id;
        }

        /// <summary>
        /// Generates the current time, in UTC, formatted in the invariant
        /// culture format for use in SAMLv2 assertions, requests, and
        /// responses.
        /// </summary>
        /// <returns>Current time in UTC, invariant culture format.</returns>
        public static string GenerateIssueInstant()
        {
            string issueInstant = DateTime.UtcNow.ToString("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'", DateTimeFormatInfo.InvariantInfo);

            return issueInstant;
        }

        /// <summary>
        /// Gets the preferred identity provider entity id based on the value
        /// found in the query string found in the given HttpRequest.
        /// </summary>
        /// <param name="request">HttpRequest containing Common Domain Cookie results.</param>
        /// <returns>Preferred IDP Entity ID, null if not available.</returns>
        public static string GetPreferredIdentityProvider(HttpRequest request)
        {
            string commonDomainCookieValue = request.QueryString[Saml2Constants.CommonDomainCookieName];
            return Saml2Utils.GetPreferredIdentityProvider(commonDomainCookieValue);
        }

        /// <summary>
        /// Gets the preferred identity provider entity id based on the value
        /// found in the specified string.
        /// </summary>
        /// <param name="commonDomainCookieValue">Common Domain Cookie value.</param>
        /// <returns>Preferred IDP Entity ID, null if not available.</returns>
        public static string GetPreferredIdentityProvider(string commonDomainCookieValue)
        {
            string idpEntityId = null;

            if (commonDomainCookieValue != null)
            {
                char[] separator = { ' ' };
                string[] listOfIdpEntityIds = commonDomainCookieValue.Split(separator);

                if (listOfIdpEntityIds.Length > 0)
                {
                    idpEntityId = Saml2Utils.ConvertFromBase64(listOfIdpEntityIds[listOfIdpEntityIds.Length - 1]);
                }
            }

            return idpEntityId;
        }

        /// <summary>
        /// Compresses, converts to Base64, then URL encodes the given 
        /// parameter and returns the ensuing string.
        /// </summary>
        /// <param name="xml">XML to undergo the process</param>
        /// <returns>String output from the process.</returns>
        public static string CompressConvertToBase64UrlEncode(IXPathNavigable xml)
        {
            XmlDocument xmlDoc = (XmlDocument)xml;

            byte[] buffer = Encoding.UTF8.GetBytes(xmlDoc.OuterXml);
            MemoryStream memoryStream = new MemoryStream();
            DeflateStream compressedStream = new DeflateStream(memoryStream, CompressionMode.Compress, true);
            compressedStream.Write(buffer, 0, buffer.Length);
            compressedStream.Close();

            memoryStream.Position = 0;
            byte[] compressedBuffer = new byte[memoryStream.Length];
            memoryStream.Read(compressedBuffer, 0, compressedBuffer.Length);
            memoryStream.Close();

            string compressedBase64String = Convert.ToBase64String(compressedBuffer);
            string compressedBase64UrlEncodedString = HttpUtility.UrlEncode(compressedBase64String);

            return compressedBase64UrlEncodedString;
        }

        /// <summary>
        /// URL decodes, converts from Base64, then decompresses the given
        /// parameter and returns the ensuing string.
        /// </summary>
        /// <param name="message">message to undergo the process</param>
        /// <returns>String output from the process.</returns>
        public static string UrlDecodeConvertFromBase64Decompress(string message)
        {
            // url decode it
            string decodedMessage = HttpUtility.UrlDecode(message);

            // convert from base 64
            byte[] byteArray = Convert.FromBase64String(decodedMessage);

            // inflate the gzip deflated message
            StreamReader streamReader = new StreamReader(new DeflateStream(new MemoryStream(byteArray), CompressionMode.Decompress));

            // put in a string
            string decompressedMessage = streamReader.ReadToEnd();
            streamReader.Close();

            return decompressedMessage;
        }

        #endregion
    }
}
