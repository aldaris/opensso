/* -*- Mode: C -*- */
/*
 * $Id: am_string_set.h,v 1.1 2008-06-23 20:32:40 mallas Exp $
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

#ifndef AM_STRING_SET_H
#define AM_STRING_SET_H

#include <am_types.h>

typedef struct {
    int size;
    char **strings;
} am_string_set_t;

AM_BEGIN_EXTERN_C

/*
 * Allocate space for a am_string_set_t and space for size strings. 
 * also initializes size to the given size.
 *
 * Parameters:
 *     size
 *         size of set to allocate.
 *
 * Returns: 
 *     a pointer to allocated am_string_set_t, or NULL if size is < 0 (invalid).
 * 
 */
AM_EXPORT am_string_set_t * 
am_string_set_allocate(int size);

/*
 * Frees memory held by the parameter, by freeing each
 * string in the set of strings, followed by the strings pointer,
 * followed by the struct itself.
 *
 * Parameters: 
 *     string_set 
 *         the am_string_set_t pointer to be freed.
 *
 * Returns: 
 *     None
 *
 */
AM_EXPORT void 
am_string_set_destroy(am_string_set_t *string_set);

AM_END_EXTERN_C

#endif	/* not AM_STRING_SET_H */
