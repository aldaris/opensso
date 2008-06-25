/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007 Sun Microsystems Inc. All Rights Reserved
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
 *
 */

#include <windows.h>
#include <httpext.h>
#include <string.h>
#include <stdio.h>
#include <stdarg.h>
#include "am_web.h"
#include <nspr.h>
#include "IisAgent6.h"

const char REDIRECT_TEMPLATE[] = {
    "Location: %s\r\n"
    "Content-Length: 0\r\n"
    "\r\n"
};

const char REDIRECT_COOKIE_TEMPLATE[] = {
    "Location: %s\r\n"
    "%s\r\n"
    "Content-Length: 0\r\n"
    "\r\n"
};


/* Comment for both FORBIDDEN_MSG and INTERNAL_SERVER_ERROR_MSG:
 * Both these have string messages only because Netscape browsers
 * can be happy.  Otherwise, they throw up a dialog box saying
 * document contains no data.  Netscape 7 does not say anything
 * at all.
 */
const char FORBIDDEN_MSG[] = {
    "HTTP/1.1 403 Forbidden\r\n"
    "Content-Length: 13\r\n"
    "Content-Type: text/plain\r\n"
    "\r\n"
    "403 Forbidden"
};

const char INTERNAL_SERVER_ERROR_MSG[] = {
    "HTTP/1.1 500 Internal Server Error\r\n"
    "Content-Length: 25\r\n"
    "Content-Type: text/html\r\n"
    "\r\n"
    "500 Internal Server Error"
};

#define AGENT_DESCRIPTION	"Sun Java(tm) System Access Manager Policy Agent 2.2 for Microsoft IIS 6.0"
const CHAR agentDescription[]	= { AGENT_DESCRIPTION };

// actually const. But API prototypes don't alow.
CHAR httpOk[]                   = "200 OK";
CHAR httpRedirect[]             = "302 Found";
CHAR httpBadRequest[]           = "400 Bad Request";
CHAR httpForbidden[]            = "403 Forbidden";
CHAR httpServerError[]          = "500 Internal Server Error";

const CHAR httpProtocol[]		= "http";
const CHAR httpVersion1_1[]		= "HTTP/1.1";
const CHAR httpsProtocol[]		= "https";
const CHAR httpProtocolDelimiter[]	= "://";
const CHAR pszLocalHost[]		= "localhost";
// Do not change. Used to see if port number needed to reconstructing URL.
const CHAR httpPortDefault[]		= "80";
const CHAR httpsPortDefault[]		= "443";
const CHAR httpPortDelimiter[]		= ":";
const CHAR pszLocation[]		= "Location: ";
const CHAR pszContentLengthNoBody[]	= "Content-length: 0\r\n";
const CHAR pszCrlf[]			= "\r\n";
const CHAR postMethod[]			= "POST";
const CHAR getMethod[]			= "GET";
const CHAR pszEntityDelimiter[]		= "\r\n\r\n";
// Response to cache invalidation notification request.
//   I.e. UpdateAgentCacheServlet
const CHAR httpResponseOk[]		= {
    "HTTP/1.1 200 OK\r\n"
    "Content-length: 2\r\n"
    "Content-type: text/plain\r\n\r\n"
    "OK" // case etc. CRITICAL, Exact match in Access Manager.
};
const CHAR contentLength[]		= "Content-Length:";
const DWORD cbCrlfLen			= 2; // strlen("\r\n")
      CHAR  pszHttpAuthHeaderName[]	= "Authorization:"; // Really const.
const CHAR requestMethodType[]		= "sunwMethod";
const CHAR refererServlet[]		= "refererservlet";

// Responses the agent uses to requests.
typedef enum {aaDeny, aaAllow, aaLogin} tAgentAction;

tAgentConfig agentConfig;

BOOL readAgentConfigFile = FALSE;
CRITICAL_SECTION initLock;

typedef struct OphResources {
    CHAR* cookies;		// cookies in the request
    DWORD cbCookies;
    CHAR *url;			// Requested URL
    DWORD cbUrl;
    am_policy_result_t result;
} tOphResources;

#define RESOURCE_INITIALIZER \
    { NULL, 0, NULL, 0, AM_POLICY_RESULT_INITIALIZER }


BOOL WINAPI GetExtensionVersion(HSE_VERSION_INFO * pVer)
{
   HMODULE      nsprHandle = NULL;

   // Initialize NSPR library
   PR_Init(PR_SYSTEM_THREAD, PR_PRIORITY_NORMAL, 0);
   nsprHandle = LoadLibrary("libnspr4.dll");


   pVer->dwExtensionVersion = MAKELONG(0, 1);   // Version 1.0

   // A brief one line description of the ISAPI extension
   strncpy(pVer->lpszExtensionDesc, agentDescription, HSE_MAX_EXT_DLL_NAME_LEN);

   InitializeCriticalSection(&initLock);

   return TRUE;
}

BOOL loadAgentPropertyFile(EXTENSION_CONTROL_BLOCK *pECB)
{
    BOOL  gotInstanceId = FALSE;
    CHAR  *instanceId =  NULL;
    DWORD instanceIdSize = 0;
    CHAR* propertiesFileFullPath  = NULL;
    am_status_t status = AM_SUCCESS;
    am_status_t polsPolicyStatus = AM_SUCCESS;
    BOOL         statusContinue      = FALSE;
    CHAR         debugMsg[2048]   = "";


    // Init to NULL values until we read properties file.
    agentConfig.bAgentInitSuccess = FALSE; // assume Failure until success

    if ( pECB->GetServerVariable(pECB->ConnID, "INSTANCE_ID", NULL,
                                 &instanceIdSize) == FALSE ) {
       instanceId = malloc(instanceIdSize);
       if (instanceId != NULL) {
           gotInstanceId = pECB->GetServerVariable(pECB->ConnID,
    					           "INSTANCE_ID",
					           instanceId,
					           &instanceIdSize);
           if ((gotInstanceId == FALSE) || (instanceIdSize <= 0)) {
               sprintf(debugMsg,
                       "%d: Invalid Instance Id received",
                       instanceIdSize);
	       status = AM_FAILURE;
           }
       } else {
            sprintf(debugMsg,
                    "%d: Invalid Instance Id received",
                    instanceIdSize);
            status = AM_NO_MEMORY;
       }
    }

    if (status == AM_SUCCESS) {
       // propertiesFileFullPath is malloc'd
       // in this iisaPropertiesFilePathGet().
       if (iisaPropertiesFilePathGet(&propertiesFileFullPath, instanceId)
                                     == FALSE) {
           sprintf(debugMsg,
                   "%s: iisaPropertiesFilePathGet() returned failure",
                   agentDescription);
           logPrimitive(debugMsg);
           free(propertiesFileFullPath);
           propertiesFileFullPath = NULL;
           SetLastError(IISA_ERROR_PROPERTIES_FILE_PATH_GET);
           return FALSE;
       }

       // Initialize the Access Manager Policy API
       polsPolicyStatus = am_web_init(propertiesFileFullPath);
       free(propertiesFileFullPath);
       propertiesFileFullPath = NULL;

       if (AM_SUCCESS != polsPolicyStatus) {
    	 // Use logPrimitive() AND am_web_log_error() here since a policy_init()
    	 //   failure could mean am_web_log_error() isn't initialized.
         sprintf(debugMsg,
                 "%s: Initialization of the agent failed: status = %s (%d)",
                 agentDescription, am_status_to_string(polsPolicyStatus),
		 polsPolicyStatus);
         logPrimitive(debugMsg);
         SetLastError(IISA_ERROR_INIT_POLICY);
         return FALSE;
       }
    }

    if (instanceId != NULL) {
       free(instanceId);
    }

    // Record success initializing agent.
    agentConfig.bAgentInitSuccess = TRUE;
    return TRUE;
}

// Set attributes as HTTP headers
static am_status_t set_header(const char *key, const char *values, void **args)
{
     am_status_t status = AM_SUCCESS;
	 CHAR** ptr = NULL;
	 CHAR* set_headers_list = NULL;

     if (key != NULL && args != NULL ) {
        EXTENSION_CONTROL_BLOCK *pECB = (EXTENSION_CONTROL_BLOCK *) args[0];
        int cookie_length = 0;
        char* httpHeaderName = NULL;
        char* tmpHeader = NULL;
        int header_length = 0;

		ptr = (CHAR **) args[1];
		set_headers_list = *ptr;

        if (pECB != NULL) {
          header_length = strlen(key) + strlen("\r\n") + 1;
          if (values != NULL) {
             header_length += strlen(values);
          }
          httpHeaderName = (char *) malloc(header_length + 1);
        } else {
          am_web_log_error("set_header(): Invalid EXTENSION_CONTROL_BLOCK");
          status = AM_INVALID_ARGUMENT;
        }

       if (status == AM_SUCCESS) {
          if (httpHeaderName != NULL) {
             memset(httpHeaderName, 0, sizeof(char) * (header_length + 1));
             strcpy(httpHeaderName, key);
             strcat(httpHeaderName, ":");
             if (values != NULL) {
                strcat(httpHeaderName, values);
             }
             strcat(httpHeaderName, "\r\n");

             if (set_headers_list == NULL) {
                set_headers_list = (char *) malloc(header_length + 1);
                if (set_headers_list != NULL) {
                    memset(set_headers_list, 0, sizeof(char) *
                                                header_length + 1);
                    strcpy(set_headers_list, httpHeaderName);
                } else {
                    am_web_log_error("set_header():Not enough memory 0x%x "
		                     "bytes.",header_length + 1);
                    status = AM_NO_MEMORY;
                }
             } else {
                 tmpHeader = set_headers_list;
                 set_headers_list = (char *) malloc(strlen(tmpHeader) +
                                                    header_length + 1);
                 if (set_headers_list == NULL) {
                    am_web_log_error("set_header():Not enough memory 0x%x "
		                     "bytes.",header_length + 1);
                    status = AM_NO_MEMORY;
                 } else {
                    memset(set_headers_list, 0, sizeof(set_headers_list));
                    strcpy(set_headers_list, tmpHeader);
                    strcat(set_headers_list, httpHeaderName);
                 }
              }
              free(httpHeaderName);
			  if (tmpHeader) {
				free(tmpHeader);
				tmpHeader = NULL;
			  }
            } else {
               am_web_log_error("set_header():Not enough memory 0x%x bytes.",
                                 cookie_length + 1);
               status = AM_NO_MEMORY;
            }
         }
       } else {
          am_web_log_error("set_header(): Invalid arguments obtained");
          status = AM_INVALID_ARGUMENT;
     }

	 if (set_headers_list && set_headers_list[0] != '\0') {
		am_web_log_info("set_header():set_headers_list = %s", set_headers_list);
		*ptr = set_headers_list;
	 }

	 return status;
}

// Function invoked in CDSSO mode to set the cookie in the
// foreign domain
static am_status_t set_cookie(const char *header, void **args)
{
     am_status_t status = AM_SUCCESS;
	 CHAR** ptr = NULL;
	 CHAR* set_cookies_list = NULL;

     if (header != NULL && args != NULL ) {
        EXTENSION_CONTROL_BLOCK *pECB = (EXTENSION_CONTROL_BLOCK *) args[0];
        int cookie_length = 0;
        char* cdssoCookie = NULL;
        char* tmpStr = NULL;

		ptr = (CHAR **) args[2];
		set_cookies_list = *ptr;

        if (pECB != NULL) {
          cookie_length = strlen("Set-Cookie:") + strlen(header)
	                                        + strlen("\r\n");
          cdssoCookie = (char *) malloc(cookie_length + 1);
        } else {
          am_web_log_error("set_cookie(): Invalid EXTENSION_CONTROL_BLOCK");
          status = AM_INVALID_ARGUMENT;
        }

       if (status == AM_SUCCESS) {
          if (cdssoCookie != NULL) {
             sprintf(cdssoCookie, "Set-Cookie:%s\r\n", header);

             if (set_cookies_list == NULL) {
                set_cookies_list = (char *) malloc(cookie_length + 1);
                if (set_cookies_list != NULL) {
                    memset(set_cookies_list, 0, sizeof(char) *
		                                cookie_length + 1);
                    strcpy(set_cookies_list, cdssoCookie);
                } else {
                    am_web_log_error("set_cookie():Not enough memory 0x%x "
		                     "bytes.",cookie_length + 1);
                    status = AM_NO_MEMORY;
		        }
             } else {
                  tmpStr = set_cookies_list;
                  set_cookies_list = (char *) malloc(strlen(tmpStr) +
                                                     cookie_length + 1);
                  if (set_cookies_list == NULL) {
                    am_web_log_error("set_cookie():Not enough memory 0x%x "
		                     "bytes.",cookie_length + 1);
                    status = AM_NO_MEMORY;
                  } else {
                     memset(set_cookies_list,0,sizeof(set_cookies_list));
                     strcpy(set_cookies_list,tmpStr);
                     strcat(set_cookies_list,cdssoCookie);
                  }
            }
            free(cdssoCookie);

			if (tmpStr) {
				free(tmpStr);
				tmpStr = NULL;
			}
          } else {
             am_web_log_error("set_cookie():Not enough memory 0x%x bytes.",
                               cookie_length + 1);
             status = AM_NO_MEMORY;
          }
       }
     } else {
          am_web_log_error("set_cookie(): Invalid arguments obtained");
          status = AM_INVALID_ARGUMENT;
     }

	if (set_cookies_list && set_cookies_list[0] != '\0') {
		am_web_log_info("set_cookie():set_cookies_list = %s", set_cookies_list);
		*ptr = set_cookies_list;
	}

     return status;
}

static am_status_t set_header_attr_as_cookie(const char *header, void **args)
{
  return AM_SUCCESS;
}

static am_status_t get_cookie_sync(const char *cookieName,
                                   char** dpro_cookie,
                                   void **args)
{
   am_status_t status = AM_SUCCESS;
   return status;
}

// Set attributes as cookies
static am_status_t set_cookie_in_response(const char *header, void **args)
{
    am_status_t status = AM_SUCCESS;

    if (header != NULL && args != NULL ) {
        EXTENSION_CONTROL_BLOCK *pECB = (EXTENSION_CONTROL_BLOCK *) args[0];
        int header_length = 0;

        CHAR* httpHeader = NULL;
        CHAR* new_cookie_str = NULL;
        CHAR* tmpHeader = NULL;
		CHAR** ptr = NULL;
		CHAR* set_cookies_list = NULL;

		ptr = (CHAR **) args[2];
		set_cookies_list = *ptr;

        if (pECB == NULL) {
            am_web_log_error("set_cookie_in_response(): Invalid "
	                     "EXTENSION_CONTROL_BLOCK object is null");
            status = AM_INVALID_ARGUMENT;
        } else {
            header_length = strlen("Set-Cookie:") + strlen("\r\n")
		                    + strlen(header) + 1;
            httpHeader = (char *)malloc(header_length);
            if (httpHeader != NULL) {
                sprintf(httpHeader, "Set-Cookie:%s\r\n", header);
	            if (set_cookies_list == NULL) {
                       set_cookies_list = (char *) malloc(header_length + 1);
	               if (set_cookies_list != NULL) {
	                  memset(set_cookies_list, 0, sizeof(char) *
		                                      header_length + 1);
	                  strcpy(set_cookies_list, httpHeader);
                       } else {
                         am_web_log_error("set_cookie_in_response(): Not "
                                          "enough memory 0x%x bytes.",
                                          header_length + 1);
                         status = AM_NO_MEMORY;
                       }
                } else {
                    tmpHeader = set_cookies_list;
	                set_cookies_list = (char *)malloc(strlen(tmpHeader) +
                                       header_length + 1);
                    if (set_cookies_list == NULL) {
                        am_web_log_error("set_cookie_in_response():Not "
			                             "enough memory 0x%x bytes.",
                                         header_length + 1);
                        status = AM_NO_MEMORY;
	                } else {
                      memset(set_cookies_list,0,sizeof(set_cookies_list));
                      strcpy(set_cookies_list,tmpHeader);
	                  strcat(set_cookies_list, httpHeader);
                    }
                }
                if (new_cookie_str) {
                   am_web_free_memory(new_cookie_str);
                }
            } else {
                    am_web_log_error("set_cookie_in_response(): Not enough "
                                     "memory 0x%x bytes.", header_length + 1);
            }
            free(httpHeader);

			if (tmpHeader != NULL) {
				free(tmpHeader);
			}

			if (status != AM_NO_MEMORY) {
				*ptr = set_cookies_list;
			}

        }
     } else {
       am_web_log_error("set_cookie_in_response():Invalid arguments obtained");
       status = AM_INVALID_ARGUMENT;
     }
     return status;
}

// Function to reset all the cookies before redirecting to AM
static am_status_t reset_cookie(const char *header, void **args)
{
   am_status_t status = AM_SUCCESS;

   if (header != NULL && args != NULL) {

        EXTENSION_CONTROL_BLOCK *pECB = (EXTENSION_CONTROL_BLOCK *) args[0];
        int reset_cookie_length = 0;
        char *resetCookie = NULL;
        char *tmpStr = NULL;
		CHAR* set_cookies_list = NULL;
		CHAR** ptr = NULL;

		ptr = (CHAR **) args[2];
		set_cookies_list = *ptr;

        if (pECB != NULL) {
          reset_cookie_length = strlen("Set-Cookie:") + strlen(header)
	                                              + strlen("\r\n");
          resetCookie = (char *) malloc(reset_cookie_length + 1);
        } else {
          am_web_log_error("reset_cookie(): Invalid EXTENSION_CONTROL_BLOCK");
          status = AM_INVALID_ARGUMENT;
        }

        if (status == AM_SUCCESS) {
          if (resetCookie != NULL) {
             memset(resetCookie, 0, sizeof(char) * reset_cookie_length + 1);
             sprintf(resetCookie, "Set-Cookie:%s\r\n", header);

             if (set_cookies_list == NULL) {
               set_cookies_list = (char *) malloc(reset_cookie_length + 1);
               if (set_cookies_list != NULL) {
                   memset(set_cookies_list, 0, sizeof(char) *
		                                reset_cookie_length + 1);
                   strcpy(set_cookies_list, resetCookie);
               } else {
               am_web_log_error("reset_cookie():Not enough memory 0x%x bytes.",
                                reset_cookie_length + 1);
                status = AM_NO_MEMORY;
	           }
	         } else {
                 tmpStr = set_cookies_list;
	             set_cookies_list = (char *) malloc(strlen(tmpStr) +
	       				             reset_cookie_length + 1);
                 if (set_cookies_list == NULL) {
                   am_web_log_error("reset_cookie():Not enough memory 0x%x "
		                    "bytes.", reset_cookie_length + 1);
                   status = AM_NO_MEMORY;
	             } else {
	                 memset(set_cookies_list, 0, sizeof(set_cookies_list));
	                 strcpy(set_cookies_list, tmpStr);
	                 strcat(set_cookies_list, resetCookie);
                 }
             }
   	     am_web_log_debug("reset_cookie(): set_cookies_list ==> %s",
                     	       set_cookies_list);
	     free(resetCookie);

		 if (tmpStr != NULL) {
			free(tmpStr);
		 }
		 if (status != AM_NO_MEMORY) {
			*ptr = set_cookies_list;
		 }

          } else {
             am_web_log_error("reset_cookie():Not enough memory 0x%x bytes.",
                               reset_cookie_length + 1);
             status = AM_NO_MEMORY;
          }
       }
   } else {
          am_web_log_error("reset_cookie(): Invalid arguments obtained");
          status = AM_INVALID_ARGUMENT;
   }
   return status;
}

static void set_method(void ** args, char * orig_req)
{
}

VOID WINAPI execute_orig_request(EXTENSION_CONTROL_BLOCK *pECB,
    				  PVOID pContext,
    				  DWORD cbIO,
    				  DWORD dwError)
{
    HSE_EXEC_URL_STATUS execUrlStatus;
    CHAR		szStatus[32] = "";
    CHAR		szWin32Error[32] = "";
    BOOL                result;
    HSE_EXEC_URL_INFO   *pExecUrlInfo;

    pExecUrlInfo = (HSE_EXEC_URL_INFO *)pContext;

    //
    // Get the results of the child request and report it.
    //
    result = pECB->ServerSupportFunction(pECB->ConnID,
                                HSE_REQ_GET_EXEC_URL_STATUS,
                                &execUrlStatus,
                                NULL,
                                NULL );
    if ( result )
    {
        if ( execUrlStatus.uHttpSubStatus != 0 )
        {
            _snprintf( szStatus,
	    	       32,
                       "Child Status=%d.%d",
                       execUrlStatus.uHttpStatusCode,
                       execUrlStatus.uHttpSubStatus );
        }
        else
        {
            _snprintf( szStatus,
	    	       32,
                       "%d",
                       execUrlStatus.uHttpStatusCode );
        }

        szStatus[31] = '\0';

        if ( execUrlStatus.dwWin32Error != ERROR_SUCCESS )
        {
            am_web_log_error( szWin32Error,
                       16,
                       "ErrorCode=%d, ",
                       execUrlStatus.dwWin32Error );

            szWin32Error[31] = '\0';
        }
    }

    //
    // Clean up the context pointer
    //

    if ( pExecUrlInfo != NULL )
    {
        free(pExecUrlInfo);
        pExecUrlInfo = NULL;
    }


    //
    // Notify IIS that we are done with this request
    //

    pECB->ServerSupportFunction(
        pECB->ConnID,
        HSE_REQ_DONE_WITH_SESSION,
        NULL,
        NULL,
        NULL
        );
}

DWORD process_original_url(EXTENSION_CONTROL_BLOCK *pECB,
			   CHAR* requestURL,
			   CHAR* orig_req_method,
			   CHAR* request_hdrs,
			   tOphResources* pOphResources)
{
    CHAR* authtype = NULL;

    HSE_EXEC_URL_INFO  *execUrlInfo  =
                       (HSE_EXEC_URL_INFO *) malloc(sizeof(HSE_EXEC_URL_INFO));

    if (execUrlInfo == NULL) {
        am_web_log_error("process_original_url(): Error %d occurred during "
                         "creating HSE_EXEC_URL_INFO context. \r\n",
                         GetLastError());
        return HSE_STATUS_ERROR;
    } else {
    	execUrlInfo->pszUrl = NULL;          // Use original request URL
    	if (orig_req_method != NULL) {
			//CDSSO mode(restore orig method)
      	    execUrlInfo->pszMethod = orig_req_method;
      	    //Remove the entity-body sent by the CDC servlet
      	    execUrlInfo->pEntity = "\0";
      	    am_web_log_debug("process_original_url(): CDSSO Mode - "
      	                     "method set back to original method (%s), "
      	                     "CDC servlet content deleted",orig_req_method);
    	} else {
      	    execUrlInfo->pszMethod = NULL;       // Use original request method
      	    execUrlInfo->pEntity = NULL;         // Use original request entity
        }
        if (request_hdrs != NULL) {
           am_web_log_debug("process_original_url(): request_hdrs = %s",
                                                     request_hdrs);
           execUrlInfo->pszChildHeaders = request_hdrs; // Original and custom
	                                               // headers
        } else {
            execUrlInfo->pszChildHeaders = NULL;// Use original request headers
        }
        execUrlInfo->pUserInfo = NULL;       // Use original request user info
        if (pOphResources->result.remote_user != NULL) {
           // Set the remote user
           execUrlInfo->pUserInfo = malloc(sizeof(HSE_EXEC_URL_USER_INFO));
           if (execUrlInfo->pUserInfo != NULL) {
               memset(execUrlInfo->pUserInfo,0,sizeof(execUrlInfo->pUserInfo));
               execUrlInfo->pUserInfo->hImpersonationToken = NULL;
               execUrlInfo->pUserInfo->pszCustomUserName =
	                             (LPSTR)pOphResources->result.remote_user;
               authtype = am_web_get_authType();
               if (authtype != NULL)
                   execUrlInfo->pUserInfo->pszCustomAuthType = authtype;
               else
                   execUrlInfo->pUserInfo->pszCustomAuthType = "dsame";
               am_web_log_debug("process_original_url(): Auth-Type set to %s",
                        execUrlInfo->pUserInfo->pszCustomAuthType);
           }
        }
    }

    //
    // Need to set the below flag to avoid recursion
    //

    execUrlInfo->dwExecUrlFlags = HSE_EXEC_URL_IGNORE_CURRENT_INTERCEPTOR;

    //
    // Associate the completion routine and the current URL with
    // this request.
    //

    if ( pECB->ServerSupportFunction( pECB->ConnID,
                                      HSE_REQ_IO_COMPLETION,
                                      execute_orig_request,
                                      0,
                                      (LPDWORD)execUrlInfo) == FALSE )
    {
        am_web_log_error("process_original_url(): Error %d occurred setting "
			 "I/O completion.\r\n", GetLastError());

        if (execUrlInfo->pUserInfo != NULL) {
            free(execUrlInfo->pUserInfo);
            execUrlInfo->pUserInfo = NULL;
        }
        return HSE_STATUS_ERROR;
    }

    //
    // Execute child request
    //

    if ( pECB->ServerSupportFunction( pECB->ConnID,
                                      HSE_REQ_EXEC_URL,
                                      execUrlInfo,
                                      NULL,
                                      NULL ) == FALSE )
    {
        am_web_log_error("process_original_url(): Error %d occurred calling "
	                 "HSE_REQ_EXEC_URL.\r\n", GetLastError() );

        if (execUrlInfo->pUserInfo != NULL) {
            free(execUrlInfo->pUserInfo);
            execUrlInfo->pUserInfo = NULL;
        }
        return HSE_STATUS_ERROR;
    }

    //
    // Return pending and let the completion clean up.
    //

    if (execUrlInfo->pUserInfo != NULL) {
        free(execUrlInfo->pUserInfo);
        execUrlInfo->pUserInfo = NULL;
    }
    return HSE_STATUS_PENDING;
}

static DWORD do_redirect(EXTENSION_CONTROL_BLOCK *pECB,
			 am_status_t status,
			 am_policy_result_t *policy_result,
			 const char *original_url,
			 const char *method,
			 void** args)
{
    char *redirect_header = NULL;
    size_t redirect_hdr_len = 0;
    char *redirect_status = httpServerError;
    char *redirect_url = NULL;
    DWORD redirect_url_len = 0;
    HSE_SEND_HEADER_EX_INFO sendHdr;
    DWORD advice_headers_len = 0;
    char *advice_headers = NULL;
    const char advice_headers_template[] = {
             "Content-Length: %d\r\n"
             "Content-Type: text/html\r\n"
             "\r\n"
    };


    am_status_t ret = AM_SUCCESS;
    const am_map_t advice_map = policy_result->advice_map;
    char *am_rev_number = am_web_get_am_revision_number();

    ret = am_web_get_url_to_redirect(status, advice_map, original_url,
				      method, AM_RESERVED,&redirect_url);

    // Compute the length of the redirect response.  Using the size of
    // the format string overallocates by a couple of bytes, but that is
    // not a significant issue given the short life span of the allocation.
    switch(status) {
    case AM_ACCESS_DENIED:
    case AM_INVALID_SESSION:
    case AM_INVALID_FQDN_ACCESS:

        //Check whether policy advices exist. If exists send
        //the advice back to client
        if ((ret == AM_SUCCESS) && (am_rev_number != NULL) &&
			(!strcmp(am_rev_number,"7.0")) && (redirect_url != NULL) &&
			(policy_result->advice_string != NULL)) {

            char *advice_txt = NULL;
            ret = am_web_build_advice_response(policy_result, redirect_url,
                         &advice_txt);
            am_web_log_debug("do_redirect(): policy status=%s, "
               "response[%s]", am_status_to_string(status), advice_txt);

            if(ret == AM_SUCCESS) {
                size_t data_length = (advice_txt != NULL)?strlen(advice_txt):0;

                if(data_length > 0) { //send the advice to client
                    //Send the headers
                    advice_headers_len = strlen(advice_headers_template) + 3;
                    advice_headers = (char *) malloc(advice_headers_len);
                    if (advice_headers != NULL) {
                        memset(advice_headers, 0, advice_headers_len);
                        sprintf(advice_headers, advice_headers_template, data_length);
                        sendHdr.pszStatus = httpOk;
                        sendHdr.pszHeader = advice_headers;
                        sendHdr.cchStatus = strlen(httpOk);
                        sendHdr.cchHeader = strlen(advice_headers);
                        sendHdr.fKeepConn = FALSE;
                        pECB->ServerSupportFunction(pECB->ConnID,
                                HSE_REQ_SEND_RESPONSE_HEADER_EX,
                                &sendHdr,
                                NULL,
                                NULL);
                        //Send the advice
                        if ((pECB->WriteClient( pECB->ConnID, (LPVOID)advice_txt,
                            (LPDWORD)&data_length, (DWORD)0))==FALSE) {
                            am_web_log_error("do_redirect(): WriteClient did not "
                                  "succeed sending policy advice: "
                                  "Attempted message = %s ", advice_txt);
                        }
                    } else {
                         am_web_log_error("do_redirect(): Not enough memory 0x%x "
                                   "bytes.",advice_headers_len);
                    }
                }

            } else {
              am_web_log_error("do_redirect(): Error while building "
               "advice response body:%s", am_status_to_string(ret));
            }
        //no policy advices exist. proceed normally.
        } else {
            if (ret == AM_SUCCESS && redirect_url != NULL) {

	        CHAR* set_cookies_list = *((CHAR**) args[2]);

	        am_web_log_debug("do_redirect() policy status = %s"
	    		     "redirection URL is %s",
			     am_status_to_string(status),
			     redirect_url);

                if (set_cookies_list == NULL) {
	            redirect_hdr_len = sizeof(REDIRECT_TEMPLATE) +
	    		           strlen(redirect_url);
                } else {
	            redirect_hdr_len = sizeof(REDIRECT_COOKIE_TEMPLATE) +
	    		           strlen(redirect_url) +
	    		           strlen(set_cookies_list);
	        }

	        redirect_header = malloc(redirect_hdr_len + 1);
	        if (redirect_header != NULL) {
		    redirect_status = httpRedirect;
                    if (set_cookies_list == NULL) {
		      _snprintf(redirect_header, redirect_hdr_len,
			    REDIRECT_TEMPLATE, redirect_url);
                    } else {
		      _snprintf(redirect_header, redirect_hdr_len,
			    REDIRECT_COOKIE_TEMPLATE, redirect_url,
			    set_cookies_list);
                       free(set_cookies_list);
                       set_cookies_list = NULL;
		    }
	            am_web_log_info("do_redirect(): redirect_header = %s",
				 redirect_header);

	        } else {
		    am_web_log_error("do_redirect() unable to allocate "
				 "%u bytes",redirect_hdr_len);
	        }
	    } else {
                if(status == AM_ACCESS_DENIED) {
		    // Only reason why we should be sending 403 forbidden.
		    // All other cases are non-deterministic.
			redirect_status = httpForbidden;
		}
	        am_web_log_error("do_redirect(): Error while calling "
			     "am_web_get_redirect_url(): status = %s",
			     am_status_to_string(ret));
	    }

            if (redirect_status == httpRedirect) {

               sendHdr.pszStatus = httpRedirect;
               sendHdr.pszHeader = redirect_header;
               sendHdr.cchStatus = strlen(httpRedirect);
               sendHdr.cchHeader = strlen(redirect_header);
               sendHdr.fKeepConn = FALSE;

               pECB->ServerSupportFunction(pECB->ConnID,
				   HSE_REQ_SEND_RESPONSE_HEADER_EX,
				   &sendHdr,
				   NULL,
				   NULL);
            } else {
               size_t data_len = sizeof(FORBIDDEN_MSG) - 1;
               const char *data = FORBIDDEN_MSG;
               if (redirect_status == httpServerError) {
                  data = INTERNAL_SERVER_ERROR_MSG;
                  data_len = sizeof(INTERNAL_SERVER_ERROR_MSG) - 1;
               }
               if (pECB->WriteClient(pECB->ConnID, (LPVOID)data,
                            (LPDWORD)&data_len, (DWORD) 0)) {
                  am_web_log_error("do_redirect() WriteClient did not "
                           "succeed: Attempted message = %s ", data);
               }
            }

            free(redirect_header);

        }
        if (redirect_url) {
            am_web_free_memory(redirect_url);
        }
        if (advice_headers) {
            free(advice_headers);
        }

	break;
    default:
	// All the default values are set to send 500 code.
	break;
    }
    return HSE_STATUS_SUCCESS;
}


am_status_t get_request_url(EXTENSION_CONTROL_BLOCK *pECB,
                            CHAR** requestURL,
			    tOphResources* pOphResources)
{
    CHAR *requestHostHeader = NULL;
    DWORD requestHostHeaderSize	= 0;
    BOOL gotRequestHost = FALSE;

    const CHAR* requestProtocol = NULL;

    CHAR  *requestProtocolType  = NULL;
    DWORD requestProtocolTypeSize = 0;
    BOOL  gotRequestProtocol = FALSE;

    CHAR  defaultPort[TCP_PORT_ASCII_SIZE_MAX + 1] = "";

    CHAR  requestPort[TCP_PORT_ASCII_SIZE_MAX + 1] = "";
    DWORD requestPortSize = sizeof requestPort;
    BOOL  gotRequestPort = FALSE;

    CHAR  *queryString = NULL;
    DWORD queryStringSize = 0;
    BOOL  gotQueryString = FALSE;

    CHAR* baseUrl = NULL;
    CHAR* colon_ptr = NULL;
    DWORD baseUrlLength = 0;
    BOOL  gotUrl = FALSE;
    am_status_t status = AM_SUCCESS;

     // Check whether the request is http or https
     if ( pECB->GetServerVariable( pECB->ConnID, "HTTPS", NULL,
                                      &requestProtocolTypeSize ) == FALSE ) {
        if (requestProtocolTypeSize > 0) {
           requestProtocolType = malloc(requestProtocolTypeSize);

           if (requestProtocolType != NULL) {
              gotRequestProtocol = pECB->GetServerVariable(pECB->ConnID,
                                                     "HTTPS",
                                                     requestProtocolType,
                                                     &requestProtocolTypeSize);
              if ((gotRequestProtocol == FALSE) ||
	                               (requestProtocolTypeSize <= 0)) {
                  am_web_log_error("get_request_url(): Unable to get protocol"
		                " type, gotRequestProtocol = %d, "
                        "requestProtocolType = %s, "
                        "requestProtocolTypeSize = %d",
                        gotRequestProtocol, requestProtocolType,
                        requestProtocolTypeSize);
                  status = AM_FAILURE;
              }
           } else {
             am_web_log_error("get_request_url():Not enough memory 0x%x"
		              "bytes.",requestProtocolTypeSize);
             status = AM_NO_MEMORY;
	      }
        }
    }

    if (status == AM_SUCCESS) {
        am_web_log_debug("get_request_url(): requestProtocolType = %s",
    		         requestProtocolType);

        if(strncmp(requestProtocolType,"on", 2) == 0) {
          requestProtocol = httpsProtocol;
          strcpy(defaultPort, httpsPortDefault);
        } else if(strncmp(requestProtocolType,"off", 3) == 0) {
          requestProtocol = httpProtocol;
          strcpy(defaultPort, httpPortDefault);
        }

        // Get the host name
        if ( pECB->GetServerVariable( pECB->ConnID, "HEADER_Host", NULL,
                                      &requestHostHeaderSize ) == FALSE ) {

          requestHostHeader = malloc(requestHostHeaderSize);
          if (requestHostHeader != NULL) {
             gotRequestHost = pECB->GetServerVariable(pECB->ConnID,
                                                      "HEADER_Host",
                                                      requestHostHeader,
                                                      &requestHostHeaderSize);
             if ((gotRequestHost == FALSE) || (requestHostHeaderSize <= 0)) {
                am_web_log_error("get_request_url(): Unable to get Host name "
                                 "of request. errorHost = %d, "
                                 "RequestHostHeaderSize = %d",
                                 gotRequestHost, requestHostHeaderSize);
	            status = AM_FAILURE;
             }
          }
       } else {
          am_web_log_error("get_request_url():Not enough memory 0x%x"
		           "bytes.",requestHostHeaderSize);
          status = AM_NO_MEMORY;
       }
    }

    if ((status == AM_SUCCESS) && (requestHostHeader != NULL)) {
       am_web_log_debug("get_request_url(): requestHostHeader = %s",
    	      	         requestHostHeader);
       colon_ptr = strchr(requestHostHeader, ':');
       if (colon_ptr != NULL) {
          strncpy(requestPort, colon_ptr + 1, strlen(colon_ptr)-1);
       } else {
           // Get the port number from Server variable
           gotRequestPort = pECB->GetServerVariable(pECB->ConnID,
    	       				            "SERVER_PORT",
                                                    requestPort,
                                                    &requestPortSize);
           if ((gotRequestPort == FALSE) || (requestPortSize <= 0)) {
               am_web_log_error("get_request_url(): Unable to get TCP port "
                                "GetServerVariable(SERVER_PORT) = %d, "
                                "requestPortSize = %d",
                                gotRequestPort, requestPortSize);
               status = AM_FAILURE;
           }
        }
    }

    if (status == AM_SUCCESS) {
        am_web_log_debug("get_request_url(): requestPort = %s", requestPort);

        pOphResources->cbUrl = strlen(requestProtocol)          +
                               strlen(httpProtocolDelimiter)    +
                               strlen(requestHostHeader)        +
                               strlen(httpPortDelimiter)        +
                               strlen(requestPort)              +
                               URL_SIZE_MAX;

        pOphResources->url = malloc(pOphResources->cbUrl);
        if (pOphResources->url == NULL) {
    	    am_web_log_error("get_request_url(): Not enough memory"
	        	     "pOphResources->cbUrl");
            status = AM_NO_MEMORY;
        }
    }

    if (status == AM_SUCCESS) {
        strcpy(pOphResources->url, requestProtocol);
        strcat(pOphResources->url, httpProtocolDelimiter);
        strcat(pOphResources->url, requestHostHeader);

        // Add the port number if it's not the default HTTP(S) port and
        //   there's no port delimiter in the Host: header indicating
        //   that the port is not present in the Host: header.
        if (strstr(requestHostHeader, httpPortDelimiter) == NULL) {
	        if (strcmp(requestPort, defaultPort) != 0) {
	            strcat(pOphResources->url, httpPortDelimiter);
	            strcat(pOphResources->url, requestPort);
	        }
	        // following 2 "else if" were added based on
	        // instruction that port number has to be added for IIS
	        else if (strcmp(requestProtocol, httpProtocol) == 0) {
	             strcat(pOphResources->url, httpPortDelimiter);
	             strcat(pOphResources->url, httpPortDefault);
	        } else if (strcmp(requestProtocol, httpsProtocol) == 0) {
	             strcat(pOphResources->url, httpPortDelimiter);
	             strcat(pOphResources->url, httpsPortDefault);
	        }
        }

        //Get the base url
        if ( pECB->GetServerVariable( pECB->ConnID, "URL", NULL,
                                      &baseUrlLength ) == FALSE ) {
            if (baseUrlLength > 0) {
                baseUrl = malloc(baseUrlLength);

                if ( baseUrl != NULL ) {
                    gotUrl = pECB->GetServerVariable(pECB->ConnID, "URL",
			                    	baseUrl, &baseUrlLength );
        	        if ((gotUrl == FALSE) || (baseUrlLength <= 0)) {
                        am_web_log_error("get_request_url(): Unable to get "
                                         "base URL "
                                         "gotUrl = %d, baseUrlLength = %d",
                                         gotUrl, baseUrlLength);
                        status = AM_FAILURE;
                    }
                } else {
          	    am_web_log_error("get_request_url():Not enough memory 0x%x"
		                     "bytes.", baseUrlLength);
                   status = AM_NO_MEMORY;
                }
            }
        }
    }

    if (status == AM_SUCCESS) {
        am_web_log_debug("get_request_url(): baseUrl = %s", baseUrl);

        strcat(pOphResources->url, baseUrl);

        if ( pECB->GetServerVariable( pECB->ConnID, "QUERY_STRING", NULL,
                                      &queryStringSize ) == FALSE ) {

           queryString = malloc(queryStringSize+1);
           if (queryString != NULL) {
              gotQueryString = pECB->GetServerVariable(pECB->ConnID,
                                                       "QUERY_STRING",
                                                       queryString,
                                                       &queryStringSize);
              if (queryString != NULL && strlen(queryString) > 0) {
                  am_web_log_debug("get_request_url(): queryString = %s",
	                           queryString);
                  queryString[queryStringSize] = '\0';
                  strcat(pOphResources->url, "?");
                  strcat(pOphResources->url, queryString);
              }
           } else {
              am_web_log_error("get_request_url():Not enough memory 0x%x"
		               "bytes.", queryStringSize);
              status = AM_NO_MEMORY;
          }
        }

        *requestURL = pOphResources->url;

        if (requestURL != NULL) {
           am_web_log_debug("get_request_url(): Constructed request url = %s",
                            *requestURL);
        }
    }

    free(requestProtocolType);
    free(requestHostHeader);
    free(baseUrl);
    free(queryString);

    return status;
}

am_status_t set_request_headers(EXTENSION_CONTROL_BLOCK *pECB, void** args)
{
  CHAR* httpHeaders = NULL;
  BOOL gotHttpHeaders = FALSE;
  DWORD httpHeadersSize = 0;
  int http_headers_length = 0;

  am_status_t status = AM_SUCCESS;

  if (pECB != NULL) {

	  CHAR* set_headers_list = *((CHAR**) args[1]);
	  CHAR* set_cookies_list = *((CHAR**) args[2]);
	  CHAR** ptr = (CHAR **) args[3];
	  CHAR* request_hdrs = *ptr;

      if (pECB->GetServerVariable(pECB->ConnID, "ALL_RAW", NULL,
                                  &httpHeadersSize) == FALSE ) {
         httpHeaders = malloc(httpHeadersSize);

         if (httpHeaders != NULL) {
             gotHttpHeaders = pECB->GetServerVariable(pECB->ConnID,
                                                      "ALL_RAW",
                                                      httpHeaders,
                                                      &httpHeadersSize);
             if (httpHeaders == NULL) {
                am_web_log_error("set_request_headers(): Unable to get http "
		                 "headers");
                status = AM_FAILURE;
             }
         } else {
             am_web_log_error("set_request_headers():Not enough memory 0x%x"
		              "bytes.", httpHeadersSize);
             status = AM_NO_MEMORY;
	    }
      } else {
           am_web_log_error("set_request_headers(): Unable to get http "
		            "headers size");
           status = AM_FAILURE;
      }

      if (status == AM_SUCCESS) {
          if (set_headers_list != NULL) {
              http_headers_length = strlen(httpHeaders) +
	                              strlen(set_headers_list);
          } else if (set_cookies_list != NULL) {
              http_headers_length = strlen(httpHeaders) +
	                              strlen(set_cookies_list);
	      }
	      request_hdrs = (char *)malloc(http_headers_length + 1);
	      if (request_hdrs != NULL) {
	         memset(request_hdrs,0, sizeof(char) * http_headers_length);
	         strcpy(request_hdrs,httpHeaders);
	         if (set_headers_list != NULL) {
	            strcat(request_hdrs,set_headers_list);
             } else if (set_cookies_list != NULL) {
	           strcat(request_hdrs,set_cookies_list);
	         }
             am_web_log_debug("set_request_headers():request_hdrs = %s",
	                                              request_hdrs);
             free(httpHeaders);
             free(set_headers_list);
     	     set_headers_list = NULL;
			 *ptr = request_hdrs;
	      } else {
              am_web_log_error("set_request_headers():Not enough memory 0x%x"
                               "bytes.", http_headers_length);
              status = AM_NO_MEMORY;
	      }
       }
  }
  return status;
}

void OphResourcesFree(tOphResources* pOphResources)
{
    if (pOphResources->cookies != NULL) {
        free(pOphResources->cookies);
        pOphResources->cookies   = NULL;
        pOphResources->cbCookies    = 0;
    }

    if (pOphResources->url != NULL) {
        free(pOphResources->url);
        pOphResources->url       = NULL;
        pOphResources->cbUrl        = 0;
    } 

    am_web_clear_attributes_map(&pOphResources->result);
    am_policy_result_destroy(&pOphResources->result);
    return;
}

DWORD WINAPI HttpExtensionProc(EXTENSION_CONTROL_BLOCK *pECB)
{
    CHAR* requestURL = NULL;
    CHAR* dpro_cookie = NULL;
    am_status_t status = AM_SUCCESS;
    DWORD returnValue = HSE_STATUS_SUCCESS;
    CHAR *set_cookies_list = NULL;
    CHAR *set_headers_list = NULL;
    CHAR *request_hdrs = NULL;

    void *args[] = {(void *) pECB, (void *) &set_headers_list,
					(void *) &set_cookies_list, (void *) &request_hdrs };

    BOOL gotRequestMethod = FALSE;
    CHAR *requestMethod = NULL;
    DWORD requestMethodSize = 0;

    BOOL gotRequestClientIP = FALSE;
    CHAR *requestClientIP = NULL;
    DWORD requestClientIPSize = 0;

    BOOL gotPathInfo = FALSE;
    CHAR pathInfo[PATH_INFO_SIZE_MAX] = "";
    DWORD pathInfoSize = sizeof pathInfo;

    BOOL gotScriptName = FALSE;
    CHAR *scriptName = NULL;
    DWORD scriptNameSize = 0;

    am_map_t env_parameter_map = NULL;

    tOphResources OphResources = RESOURCE_INITIALIZER;
    tOphResources* pOphResources = &OphResources;

    CHAR* orig_req_method = NULL;
    CHAR* query = NULL;
    CHAR* response = NULL;
    BOOL fCookie = FALSE;
    DWORD cbCookiesLength = 0;
    CHAR* cookieValue = NULL;
    CHAR* newPathInfo = NULL;
    int length = 0;
    int i = 0;

    // Load Agent Propeties file only once
    if (readAgentConfigFile == FALSE) {
		EnterCriticalSection(&initLock);
		if (readAgentConfigFile == FALSE) {
			loadAgentPropertyFile(pECB);
			readAgentConfigFile = TRUE;
		}
		LeaveCriticalSection(&initLock);
    }

    // get the current request url
    status = get_request_url(pECB, &requestURL, pOphResources);


    // Check whether the url is a notification url
    if ((status == AM_SUCCESS) &&
         (B_TRUE == am_web_is_notification(requestURL))) {
          const char* data = NULL;
          if (pECB->cbTotalBytes > 0) {
             data =  pECB->lpbData;
             am_web_handle_notification(data, pECB->cbTotalBytes);
             OphResourcesFree(pOphResources);
             return HSE_STATUS_SUCCESS_AND_KEEP_CONN;
          }
    }

    if (status == AM_SUCCESS) {
        if ( pECB->GetServerVariable( pECB->ConnID, "REQUEST_METHOD", NULL,
                                      &requestMethodSize ) == FALSE ) {

           requestMethod = malloc(requestMethodSize);
	       if (requestMethod != NULL) {
              gotRequestMethod = pECB->GetServerVariable(pECB->ConnID,
                                                         "REQUEST_METHOD",
                                                         requestMethod,
                                                         &requestMethodSize);
              if ((gotRequestMethod == FALSE) || (requestMethodSize <= 0)) {
                 am_web_log_error("HttpExtensionProc(): Unable to get request "
                                   "method. GetHeader(method) = %d, "
                                   "requestMethodSize = %d", gotRequestMethod,
                                   requestMethodSize);
                  status = AM_FAILURE;
              }
           } else {
             am_web_log_error("get_request_url():Not enough memory 0x%x"
		              "bytes.",requestMethodSize);
             status = AM_NO_MEMORY;
           }
        }
    }

    if (status == AM_SUCCESS) {
     am_web_log_debug("HttpExtensionProc(): requestMethod = %s",requestMethod);

     // Get the cookie from header
     pOphResources->cbCookies  = COOKIES_SIZE_MAX + 1;
     pOphResources->cookies = malloc(pOphResources->cbCookies);
     if (pOphResources->cookies != NULL) {

         memset(pOphResources->cookies,0,pOphResources->cbCookies);
         cbCookiesLength = pOphResources->cbCookies;
    	 fCookie = pECB->GetServerVariable(pECB->ConnID,
                                           "HTTP_COOKIE",
                                           pOphResources->cookies,
                                           &cbCookiesLength);

         if (fCookie  &&  cbCookiesLength > 0) {
            const char *cookieName = am_web_get_cookie_name();
            // look for the Access Manager cookie
           if (cookieName != NULL) {
              cookieValue = strstr(pOphResources->cookies, cookieName);
              while (cookieValue) {
                 char *marker = strstr(cookieValue+1, cookieName);
                 if (marker) {
                    cookieValue = marker;
                 } else {
                    break;
                 }
	      }

	      if (cookieValue != NULL) {
	          cookieValue = strchr(cookieValue ,'=');
	          cookieValue = &cookieValue[1]; // 1 vs 0 skips over '='
	          // find the end of the cookie
	          length = 0;
	          for (i=0;(cookieValue[i] != ';') &&
                      (cookieValue[i] != '\0'); i++) {
		           length++;
	          }
	          cookieValue[length]='\0';
	          if (length < URL_SIZE_MAX-1) {
		      am_web_log_info("HttpExtensionProc(): "
                                      "Access Manager Cookie value: %s",
                                      cookieValue);
                      if (length > 0) {
                         dpro_cookie = malloc(length+1);
                         if (dpro_cookie != NULL) {
                           strncpy(dpro_cookie, cookieValue, length);
                           dpro_cookie[length] = '\0';
                       } else {
                           am_web_log_error("HttpExtensionProc(): "
                                            "Unable to allocate "
                                            "memory for cookie, size = %u",
                                            length);
                           status = AM_NO_MEMORY;
                      }
                    }
                  }
                }
             }
          }
      } else {
	     am_web_log_error("HttpExtensionProc(): "
                          "Not enough memory for "
                          "pOphResources->cbCookies");
	     status = AM_NO_MEMORY;
      }
     }

     if (status == AM_SUCCESS) {
    	query = strchr(requestURL, '?');
    	if(query != NULL) {
          query++;
          if (AM_SUCCESS == am_web_get_parameter_value(query,
                                         requestMethodType,
                                         &orig_req_method)) {
              status = am_web_remove_parameter_from_query(requestURL,
                                                        requestMethodType,
                                                        &requestURL);
          }
        }
     }

     if (status == AM_SUCCESS) {
      if (am_web_is_cdsso_enabled() == B_TRUE) {
        if ((strcmp(requestMethod, postMethod) == 0)
	    && (orig_req_method != NULL)
	    && (strlen(orig_req_method) > 0)) {

               int totalBytesRecvd = pECB->cbTotalBytes;

               if (totalBytesRecvd > 0) {
	          response = (char *) malloc(totalBytesRecvd + 1);
	          if (response != NULL) {
                     memset(response,0, sizeof(char) *
		                        totalBytesRecvd + 1);
	             strncpy(response, pECB->lpbData, totalBytesRecvd);
                  } else {
                    am_web_log_error("HttpExtensionProc():Not enough memory "
		                     "0x%x bytes.", pECB->cbTotalBytes);
                    status = AM_NO_MEMORY;
		  }
               }
	       if (status == AM_SUCCESS) {
	           status = am_web_check_cookie_in_post(args, &dpro_cookie,
                                                    &requestURL,
                                                    &orig_req_method,
                                                    requestMethod,response,
                                                    B_FALSE, set_cookie,
                                                    set_method);
               }
        } else if ((strcmp(requestMethod, getMethod) == 0)
	          && (orig_req_method != NULL)
	    	  && (strlen(orig_req_method) > 0)) {

	             status = am_web_check_cookie_in_query(args, &dpro_cookie,
                                              query, &requestURL,
                                              &orig_req_method, requestMethod,
                                              set_cookie, set_method);
             }
         }
     }

     if (status == AM_SUCCESS) {
    	am_web_log_debug("HttpExtensionProc(): "
                         "Value of dpro_cookie = %s,status = %s (%d)",
                         dpro_cookie, am_status_to_string(status), status);

        if (pECB->GetServerVariable(pECB->ConnID, "REMOTE_ADDR", NULL,
                                     &requestClientIPSize ) == FALSE ) {
            requestClientIP = malloc(requestClientIPSize);
            if (requestClientIP != NULL) {
               gotRequestClientIP = pECB->GetServerVariable(pECB->ConnID,
                                                       "REMOTE_ADDR",
                                                       requestClientIP,
                                                       &requestClientIPSize);
            } else {
                am_web_log_error("HttpExtensionProc():Not enough memory 0x%x"
                                 "bytes.",requestClientIPSize);
                status = AM_NO_MEMORY;
            }
         }
     }

     if (status == AM_SUCCESS) {
         am_web_log_debug("HttpExtensionProc(): requestClientIP = %s",
    					      requestClientIP);
         gotPathInfo = pECB->GetServerVariable(pECB->ConnID,
    					       "PATH_INFO",
					       pathInfo,
					       &pathInfoSize);
         if ((gotPathInfo == FALSE) || (pathInfoSize <= 0)) {
             am_web_log_error("HttpExtensionProc(): "
                              "Unable to get Path info"
                              "of request. gotPathInfo= %d, "
                              "pathInfoSize = %d", gotPathInfo, pathInfoSize);
            status = AM_FAILURE;
         }
    }

     if (status == AM_SUCCESS) {
         am_web_log_debug("HttpExtensionProc(): pathInfo = %s", pathInfo);
         if (pECB->GetServerVariable(pECB->ConnID, "SCRIPT_NAME", NULL,
                                     &scriptNameSize) == FALSE ) {
            scriptName = malloc(scriptNameSize);
            if (scriptName != NULL) {
               gotScriptName = pECB->GetServerVariable(pECB->ConnID,
                                                       "SCRIPT_NAME",
                                                       scriptName,
                                                       &scriptNameSize);
               if ((gotScriptName == FALSE) || (scriptNameSize <= 0)) {
                   am_web_log_error("HttpExtensionProc(): "
                                    "Unable to get scriptName"
                                    "gotScriptName= %d, "
                                    "scriptNameSize = %d", gotScriptName,
                                    scriptNameSize);
                   status = AM_FAILURE;
               }
             } else {
                am_web_log_error("HttpExtensionProc():Not enough memory 0x%x"
                                 "bytes.", scriptNameSize);
                status = AM_NO_MEMORY;
             }
          }
     }

     if (status == AM_SUCCESS) {
       am_web_log_debug("HttpExtensionProc(): scriptName = %s", scriptName);
       if (pathInfo != NULL && scriptName != NULL) {
          newPathInfo = &pathInfo[strlen(scriptName)];
       }
     }

     if (status == AM_SUCCESS) {
        am_web_log_debug("HttpExtensionProc(): newPathInfo = %s", newPathInfo);

         status = am_map_create(&env_parameter_map);

         am_web_log_debug("HttpExtensionProc(): status after"
    		          "am_map_create = %s (%d)",
		          am_status_to_string(status),
		          status);
      }
      if ((am_web_is_cdsso_enabled() == B_TRUE) && (orig_req_method != NULL)) {
          strcpy(requestMethod, orig_req_method);
      }

      if (status == AM_SUCCESS) {
	  status = am_web_is_access_allowed(dpro_cookie, requestURL,
                                        newPathInfo, requestMethod,
                                        (char *)requestClientIP,
                                        env_parameter_map,
                                        &OphResources.result);
      }

      free(dpro_cookie);
      am_map_destroy(env_parameter_map);

      am_web_log_debug("HttpExtensionProc(): status after "
                       "am_web_is_access_allowed = %s (%d)",
                       am_status_to_string(status), status);

      switch(status) {
        case AM_SUCCESS:
          if (am_web_is_logout_url(requestURL) == B_TRUE) {
             (void)am_web_logout_cookies_reset(reset_cookie, args);
          }

          // set user attributes to http header/cookies
	      status = am_web_result_attr_map_set(&OphResources.result,
                                        set_header, set_cookie_in_response,
                                        set_header_attr_as_cookie,
                                        get_cookie_sync, args);

          if (status == AM_SUCCESS) {
			 // Set request headers
             if ((set_headers_list != NULL) || (set_cookies_list != NULL)) {
                 status = set_request_headers(pECB, args);
             }
          }

          // If set_cookies_list is not empty, set the cookies in the current
          // response
          if (set_cookies_list != NULL && strlen(set_cookies_list) > 0) {
              HSE_SEND_HEADER_EX_INFO cookieResponseHdr;

              cookieResponseHdr.pszStatus = NULL;
              cookieResponseHdr.pszHeader = set_cookies_list;
              cookieResponseHdr.cchStatus = 0;
              cookieResponseHdr.cchHeader = strlen(set_cookies_list);
              cookieResponseHdr.fKeepConn = TRUE;

              pECB->ServerSupportFunction(pECB->ConnID,
                                          HSE_REQ_SEND_RESPONSE_HEADER_EX,
                                          &cookieResponseHdr,
                                          NULL,
                                          NULL);

              free(set_cookies_list);
              set_cookies_list = NULL;
         }

         returnValue = process_original_url(pECB, requestURL,
                                            orig_req_method,
                                            request_hdrs, pOphResources);
         break;

         case AM_INVALID_SESSION:
    	   am_web_log_info("HttpExtensionProc(): Invalid session.");
           am_web_do_cookies_reset(reset_cookie, args);
    	   returnValue = do_redirect(pECB, status,
                                     &OphResources.result,
                                     requestURL, requestMethod, args);
           break;

           case AM_ACCESS_DENIED:
	     am_web_log_info("HttpExtensionProc(): Access denied to %s",
                            OphResources.result.remote_user ?
                            OphResources.result.remote_user : "unknown user");
             returnValue = do_redirect(pECB, status,
                                       &OphResources.result,
                                       requestURL, requestMethod, args);
           break;

           case AM_INVALID_FQDN_ACCESS:
              am_web_log_info("HttpExtensionProc(): Invalid FQDN access.");
              returnValue = do_redirect(pECB, status,
                                        &OphResources.result,
                                        requestURL, requestMethod, args);
           break;

           case AM_INVALID_ARGUMENT:
           case AM_NO_MEMORY:
           case AM_FAILURE:
           default:
             am_web_log_error("HttpExtensionProc(): status: %s (%d)",
                              am_status_to_string(status), status);
             pECB->ServerSupportFunction(pECB->ConnID,
                                         HSE_REQ_SEND_RESPONSE_HEADER,
                                         httpServerError,
                                        (DWORD)NULL, (DWORD)NULL);
             returnValue = HSE_STATUS_SUCCESS_AND_KEEP_CONN;
           break;
      }

      if (requestMethod != NULL) {
         free(requestMethod);
      }

      if (requestClientIP != NULL) {
         free(requestClientIP);
      }

      if (orig_req_method != NULL) {
         am_web_free_memory(orig_req_method);
      }

      if (response != NULL) {
         free(response);
      }

      if (scriptName != NULL) {
         free(scriptName);
      }

      if (request_hdrs != NULL) {
         free(request_hdrs);
         request_hdrs = NULL;
      }

      OphResourcesFree(pOphResources);

      return returnValue;
}

BOOL iisaPropertiesFilePathGet(CHAR** propertiesFileFullPath,char *instanceId)
{
    // Max WINAPI path
    const DWORD dwPropertiesFileFullPathSize = MAX_PATH + 1;
    const CHAR  szPropertiesFileName[]       = "FAMAgentBootstrap.properties";
    CHAR agentApplicationSubKey[1000] = "";
    const CHAR agentDirectoryKeyName[]       = "Path";
    DWORD dwPropertiesFileFullPathLen        = dwPropertiesFileFullPathSize;
    HKEY hKey                                = NULL;
    LONG lRet                                = ERROR_SUCCESS;
    CHAR debugMsg[2048]                      = "";

    strcpy(agentApplicationSubKey,
      "Software\\Sun Microsystems\\Access Manager IIS6 Agent\\Identifier_");
    if (instanceId != NULL) {
       strcat(agentApplicationSubKey,instanceId);
    }
    ///////////////////////////////////////////////////////////////////
    //  get the location of the properties file from the registry
    lRet = RegOpenKeyEx(HKEY_LOCAL_MACHINE, agentApplicationSubKey,
                        0, KEY_READ, &hKey);
    if(lRet != ERROR_SUCCESS) {
        sprintf(debugMsg,
                "%s(%d) Opening registry key %s%s failed with error code %d",
                __FILE__, __LINE__, "HKEY_LOCAL_MACHINE\\",
                agentApplicationSubKey, lRet);
        logPrimitive(debugMsg);
        return FALSE;
    }

    // free'd by caller, even when there's an error.
    *propertiesFileFullPath = malloc(dwPropertiesFileFullPathLen);
    if (*propertiesFileFullPath == NULL) {
        sprintf(debugMsg,
              "%s(%d) Insufficient memory for propertiesFileFullPath %d bytes",
             __FILE__, __LINE__, dwPropertiesFileFullPathLen);
        logPrimitive(debugMsg);
        return FALSE;
    }
    lRet = RegQueryValueEx(hKey, agentDirectoryKeyName, NULL, NULL,
                           *propertiesFileFullPath,
                           &dwPropertiesFileFullPathLen);
    if (lRet != ERROR_SUCCESS || *propertiesFileFullPath == NULL ||
        (*propertiesFileFullPath)[0] == '\0') {
        sprintf(debugMsg,
          "%s(%d) Reading registry value %s\\%s\\%s failed with error code %d",
          __FILE__, __LINE__,
          "HKEY_LOCAL_MACHINE\\", agentApplicationSubKey,
          agentDirectoryKeyName, lRet);
        logPrimitive(debugMsg);
        return FALSE;
    }
    if (*propertiesFileFullPath &&
        (**propertiesFileFullPath == '\0')) {
        sprintf(debugMsg,
                "%s(%d) Properties file directory path is NULL.",
                __FILE__, __LINE__);
        logPrimitive(debugMsg);
        return FALSE;
    }
    if (*(*propertiesFileFullPath + dwPropertiesFileFullPathLen - 1) !=
        '\0') {
        sprintf(debugMsg,
             "%s(%d) Properties file directory path missing NULL termination.",
             __FILE__, __LINE__);
        logPrimitive(debugMsg);
        return FALSE;
    }
    // closes system registry
    RegCloseKey(hKey);
    if ((strlen(*propertiesFileFullPath) + 2 /* size of \\ */ +
         strlen(szPropertiesFileName) + 1) > dwPropertiesFileFullPathSize) {
        sprintf(debugMsg,
              "%s(%d) Properties file directory path exceeds Max WINAPI path.",
              __FILE__, __LINE__);
        logPrimitive(debugMsg);
        return FALSE;
    }
    strcat(*propertiesFileFullPath, "\\");
    strcat(*propertiesFileFullPath, szPropertiesFileName);
    return TRUE;
}

// Primitive error logger here that works before before policy_error() is
// initialized.
void logPrimitive(CHAR *message)
{
    HANDLE hes        = NULL;
    const CHAR* rsz[] = {message};

    if (message == NULL) {
	return;
    }

    hes = RegisterEventSource(0, agentDescription);
    if (hes) {
	ReportEvent(hes, EVENTLOG_ERROR_TYPE, 0, 0, 0, 1, 0, rsz, 0);
	DeregisterEventSource(hes);
    }
}

BOOL WINAPI TerminateExtension(DWORD dwFlags)
{
    am_status_t status = am_web_cleanup();
	DeleteCriticalSection(&initLock);
    return TRUE;
}
