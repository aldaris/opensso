/* -*- Mode: C -*- */
/*
 * $Id: am_utils.h,v 1.1 2008-06-23 20:32:40 mallas Exp $
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
 * NOTE: THIS IS AN UNSUPPORTED PRILIMINARY DEVELOPER EARLY ACCESS VERSION.
 * ALL FUNCTIONS AND DATA STRUCTURES MAY CHANGE WITHOUT
 * BACKWARD COMPATIBILITY.
 */
#ifndef __AM_UTILS_H__
#define __AM_UTILS_H__

#include <am.h>

AM_BEGIN_EXTERN_C

/*
 * URL encodes a HTTP cookie.
 *
 * Parameters:
 *   cookie
 *	the cookie to be URL encoded.
 *   buf
 *      the buffer to put the encoded cookie
 *   len
 *      the size of the buffer 
 *
 * Returns:
 *   AM_SUCCESS
 *      if the cookie was successfully encoded and copied into buf.
 *
 *   AM_INVALID_ARGUMENT
 *      if the cookie or buffer was NULL.
 *
 *   AM_BUFFER_TOO_SMALL
 *      if len was smaller than the size of the encoded value.
 *
 *   AM_FAILURE
 *      other error ocurred while encoding cookie.
 */
AM_EXPORT am_status_t
am_http_cookie_encode(const char *cookie, char *buf, int len);

/*
 * URL decodes a HTTP cookie.
 *
 * Parameters:
 *   cookie
 *	the cookie to be URL decoded.
 *   buf
 *      the buffer to put the decoded cookie
 *   len
 *      the size of the buffer 
 *
 * Returns:
 *   AM_SUCCESS
 *      if the cookie was successfully decoded and copied into buf.
 *
 *   AM_INVALID_ARGUMENT
 *      if the cookie or buffer was NULL.
 *
 *   AM_BUFFER_TOO_SMALL
 *      if len was smaller than the size of the decoded value.
 *
 *   AM_FAILURE
 *      other error ocurred while decoding cookie.
 */
AM_EXPORT am_status_t
am_http_cookie_decode(const char *cookie, char *buf, int len);

AM_END_EXTERN_C

#endif /*__AM_UTILS_H__*/
