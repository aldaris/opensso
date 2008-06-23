/* -*- Mode: C -*- */
/*
 * $Id: am_types.h,v 1.1 2008-06-23 20:32:40 mallas Exp $
 * Copyright © 2002 Sun Microsystems, Inc. All rights reserved.
 * 
 * U.S. Government Rights - Commercial software. Government users are
 * subject to the Sun Microsystems, Inc. standard license agreement and
 * applicable provisions of the FAR and its supplements. Use is subject to
 * license terms. Sun, Sun Microsystems, the Sun logo and Sun ONE are
 * trademarks or registered trademarks of Sun Microsystems, Inc. in the
 * U.S. and other countries.
 * 
 * Copyright © 2002 Sun Microsystems, Inc. Tous droits réservés.
 * 
 * Droits du gouvernement américain, utlisateurs gouvernmentaux - logiciel
 * commercial. Les utilisateurs gouvernmentaux sont soumis au contrat de
 * licence standard de Sun Microsystems, Inc., ainsi qu aux dispositions en
 * vigueur de la FAR [ (Federal Acquisition Regulations) et des suppléments
 * à celles-ci.
 * 
 * Distribué par des licences qui en restreignent l'utilisation. Sun, Sun
 * Microsystems, le logo Sun et Sun ONE sont des marques de fabrique ou des
 * marques déposées de Sun Microsystems, Inc. aux Etats-Unis et dans
 * d'autres pays.
 *
 */

/*
 * Abstract:
 *
 * Common types and macros provided by the Sun Java System Access Manager 
 * Access Management SDK.
 *
 */

#ifndef AM_TYPES_H
#define AM_TYPES_H

#if	defined(WINNT)
#if	defined(AM_BUILDING_LIB)
#define	AM_EXPORT	__declspec(dllexport)
#else
#if	!defined(AM_STATIC_LIB)
#define	AM_EXPORT	__declspec(dllimport)
#else
#if	!defined(__cplusplus)
#define	AM_EXPORT	extern
#endif
#endif
#endif
#else
#define	AM_EXPORT
#endif

#if	defined(__cplusplus)
#define	AM_BEGIN_EXTERN_C	extern "C" {
#define	AM_END_EXTERN_C		}
#else
#define	AM_BEGIN_EXTERN_C
#define	AM_END_EXTERN_C
#endif

AM_BEGIN_EXTERN_C

#if defined(WINNT) || defined(LINUX) || defined(HPUX)
#include <sys/stat.h>     /* for time_t */
typedef enum { 
    B_FALSE, 
    B_TRUE 
} boolean_t;
#else 
#include <sys/types.h>   /* for time_t and boolean_t */
#endif /* WINNT */

#if defined(AIX)
typedef enum {
    B_FALSE=0,
    B_TRUE
} booleant;
#endif


typedef enum {
    AM_FALSE = 0,
    AM_TRUE
} am_bool_t;

typedef enum {
    AM_SUCCESS = 0,
    AM_FAILURE,
    AM_INIT_FAILURE,
    AM_AUTH_FAILURE,
    AM_NAMING_FAILURE,
    AM_SESSION_FAILURE,
    AM_POLICY_FAILURE,
    AM_NO_POLICY,
    AM_INVALID_ARGUMENT,
    AM_INVALID_VALUE,
    AM_NOT_FOUND,
    AM_NO_MEMORY,
    AM_NSPR_ERROR,
    AM_END_OF_FILE,
    AM_BUFFER_TOO_SMALL,
    AM_NO_SUCH_SERVICE_TYPE,
    AM_SERVICE_NOT_AVAILABLE,
    AM_ERROR_PARSING_XML,
    AM_INVALID_SESSION,
    AM_INVALID_ACTION_TYPE,
    AM_ACCESS_DENIED,
    AM_HTTP_ERROR,
    AM_INVALID_FQDN_ACCESS,
    AM_FEATURE_UNSUPPORTED,
    AM_AUTH_CTX_INIT_FAILURE,
    AM_SERVICE_NOT_INITIALIZED,
    AM_INVALID_RESOURCE_FORMAT,
    AM_NOTIF_NOT_ENABLED,
    AM_ERROR_DISPATCH_LISTENER,
    AM_REMOTE_LOG_FAILURE,
    AM_LOG_FAILURE,
    AM_REMOTE_LOG_NOT_INITIALIZED,
    AM_NUM_ERROR_CODES	/* This should always be the last. */
} am_status_t;

/*
 * Returns the message for the given status code.
 * For example, the message for AM_SUCCESS is "success".
 *
 * Parameters:
 *   status     the status code.
 *
 * Returns:
 *   Message for the status code as a const char *.
 */
AM_EXPORT const char *am_status_to_string(am_status_t status);

/*
 * Returns the name of the given status code as a string.
 * For example, the name of AM_SUCCESS is "AM_SUCCCESS".
 *
 * Parameters:
 *   status     the status code.
 *
 * Returns:
 *   Name of the status code as a const char *.
 */
AM_EXPORT const char *am_status_to_name(am_status_t status);

AM_END_EXTERN_C

#endif	/* not AM_TYPES_H */
