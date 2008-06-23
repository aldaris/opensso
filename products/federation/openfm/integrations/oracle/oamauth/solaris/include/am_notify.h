/* -*- Mode: C -*-
 * $Id: am_notify.h,v 1.1 2008-06-23 20:32:40 mallas Exp $
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


#ifndef AM_NOTIFY_H
#define AM_NOTIFY_H

#include <am_policy.h>

AM_BEGIN_EXTERN_C

/*
 * This function should be called by the service listening on the 
 * notification URL given in the properties file if notification is enabled.
 *
 * It parses the XML message and calls SSO Token listeners and policy 
 * notification handlers accordingly. 
 *
 * Parameters:
 *   xmlmsg
 *		XML message containing the notification message.
 * 
 *   policy_handle_t
 *              The policy handle created from am_policy_service_init().
 * 
 *              NULL if policy is not initialized or not used.
 *
 * Returns:
 *   AM_SUCCESS 
 *              if XML message was successfully parsed and processed.
 * 
 *   AM_INVALID_ARGUMENT
 *		if any input parameter is invalid.
 * 
 *   AM_ERROR_PARSING_XML
 *              if there was an error parsing the XML message.
 *
 *   AM_ERROR_DISPATCH_LISTENER
 *              if there was an error dispatching the listener(s).
 *
 *   AM_FAILURE
 *		if any other error occurred.
 */
AM_EXPORT am_status_t 
am_notify(const char *xmlmsg, am_policy_t policy_handle);

AM_END_EXTERN_C

#endif
