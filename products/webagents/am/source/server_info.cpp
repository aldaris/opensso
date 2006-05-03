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
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 *
 */ 

#include <stdio.h>
#if     defined(WINNT)
#define snprintf        _snprintf
#endif

#include <climits>
#include <cstring>
#include <stdexcept>

#include "server_info.h"
#include "url.h"

USING_PRIVATE_NAMESPACE

namespace {
    const std::string protocolSeparators("://");
    const std::string portSeparator(":");

    const std::size_t MAX_PORT_LENGTH = 5;
    const unsigned short HTTP_PORT = 80;
    const unsigned short HTTPS_PORT = 443;

    /* Throws std::invalid_argument with a message containing the invalid url */
    inline void invalidURL(const char *message, const char *url,
			   std::size_t len)
    {
	std::string msg("ServerInfo::parseURL() ");

	msg += message;
	msg += ": ";
	msg.append(url, len);

	throw std::invalid_argument(msg);
    }
}

const std::string ServerInfo::http("http");
const std::string ServerInfo::https("https");

/* Throws std::invalid_argument if url is invalid */
ServerInfo::ServerInfo(const char *url, std::size_t len)
    : host(), port(0), use_ssl(false), uri()
{
    if (NULL == url) {
	throw std::invalid_argument("ServerInfo() url is NULL");
    }

    if (0 == len) {
	len = std::strlen(url);
    }

    parseURL(url, len);
}

ServerInfo::ServerInfo(const std::string& url)
    : host(), port(0), use_ssl(false), uri()
{
    parseURL(url.c_str(), url.size());
}

/* Throws std::invalid_argument if url is invalid */
void ServerInfo::setFromString(const char *url, std::size_t len)
{
    if (NULL == url) {
	throw std::invalid_argument("ServerInfo.setFromString() url is NULL");
    }

    if (0 == len) {
	len = std::strlen(url);
    }

    parseURL(url, len);
}

void ServerInfo::setFromString(const std::string& url)
{
    parseURL(url.c_str(), url.size());
}

void ServerInfo::parseURL(const char *url, std::size_t len)
{
    std::size_t offset = 0;
    bool urlUsesSSL;

    // Check that the length of the infoString is at least as long as the
    // shortest permissible URL.  This check allows us to safely execute
    // all of the checks in the following if statement without worrying
    // about running off the end of the buffer.  The comparison string
    // is only significant for being a minimally valid URL.
    if (len - offset < MIN_URL_LEN) {
	invalidURL("URL too short", url, len);
    }

    if ((url[offset] == 'h' || url[offset] == 'H') &&
	(url[++offset] == 't' || url[offset] == 'T') &&
	(url[++offset] == 't' || url[offset] == 'T') &&
	(url[++offset] == 'p' || url[offset] == 'P')) {
	if (url[offset + 1] == 's' || url[offset + 1] == 'S') {
	    urlUsesSSL = true;
	    offset += 2;
	} else {
	    urlUsesSSL = false;
	    offset += 1;
	}

	if (url[offset] == ':' && url[offset + 1] == '/' &&
	    url[offset + 2] == '/') {
	    offset += 3;

	    std::size_t startOfHost = offset;
	    while (offset < len && ':' != url[offset] && '/' != url[offset]) {
		offset += 1;
	    }

	    std::size_t hostLen = offset - startOfHost;
	    if (hostLen > 0) {
		std::string newHost(&url[startOfHost], hostLen);
		unsigned short newPort = urlUsesSSL ? HTTPS_PORT : HTTP_PORT;
		std::string newURI("/");

		if (offset < len) {
		    if (':' == url[offset]) {
			offset += 1;

			std::size_t startOfPort = offset;
			while (offset < len && '0' <= url[offset] &&
			    '9' >= url[offset]) {
			    offset += 1;
			}

			// NOTE: RFC NNNN allows a URL to have the following
			// form: http://host:/, i.e. port separator present
			// but no port number specified.
			if (offset - startOfPort > 0) {
			    unsigned int value = 0;

			    for (std::size_t i = startOfPort; i < offset; ++i){
				value = (value * 10) + (url[i] - '0');
			    }
			    if (value <= USHRT_MAX) {
				newPort = static_cast<unsigned short>(value);
			    } else {
				invalidURL("invalid port number", url, len);
			    }
			}
		    }

		    if (offset < len) {
			if ('/' == url[offset++]) {
			    newURI.append(&url[offset], len - offset);
			} else {
			    invalidURL("invalid character in port number",
				       url, len);
			}
		    }
		}
		// We have successfully parsed the URL, so now we can
		// assign the bits and pieces to the member fields, without
		// worrying about corrupting the object, since none of
		// these operations will generate an exception.
		host.swap(newHost);
		port = newPort;
		use_ssl = urlUsesSSL;
		uri.swap(newURI);
	    } else {
		invalidURL("missing host name", url, len);
	    }
	} else {
	    invalidURL("unable to parse protocol terminator", url, len);
	}
    } else {
	invalidURL("unable to parse protocol", url, len);
    }
}

void ServerInfo::setURI(const std::string& newURI)
{
    if (newURI.size() == 0) {
	uri = "/";
    } else {
	uri = newURI;
    }
}

std::string ServerInfo::toString() const
{
    char portBuf[MAX_PORT_LENGTH + 1];
    std::string result;

    result.reserve(getProtocol().size() + protocolSeparators.size() +
		   host.size() + portSeparator.size() + MAX_PORT_LENGTH +
		   uri.size());

    result.append(getProtocol());
    result.append(protocolSeparators);
    result.append(host);
    result.append(portSeparator);
    snprintf(portBuf, sizeof(portBuf), "%u", port);
    result.append(portBuf);
    result.append(uri);

    return result;
}
