/**
 * $Id: unixyp_passwd.c,v 1.1 2008-08-29 22:06:41 kevinserwin Exp $
 * Copyright © 2005 Sun Microsystems, Inc.  All rights reserved.
 *
 * Sun Microsystems, Inc. has intellectual property rights relating to
 * technology embodied in the product that is described in this document.
 * In particular, and without limitation, these intellectual property rights
 * may include one or more of the U.S. patents listed at
 * http://www.sun.com/patents and one or more additional patents or pending
 * patent applications in the U.S. and in other countries.
 *
 * U.S. Government Rights - Commercial software.  Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and applicable
 * provisions of the FAR and its supplements.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.Sun,
 * Sun Microsystems and  the Sun logo are trademarks or registered trademarks
 * of Sun Microsystems, Inc. in the U.S. and other countries.  
 *
 * Copyright © 2005 Sun Microsystems, Inc. Tous droits réservés.
 * Sun Microsystems, Inc. détient les droits de propriété intellectuels relatifs
 * à la technologie incorporée dans le produit qui est décrit dans ce document.
 * En particulier, et ce sans limitation, ces droits de propriété
 * intellectuelle peuvent inclure un ou plus des brevets américains listés
 * à l'adresse http://www.sun.com/patents et un ou les brevets supplémentaires
 * ou les applications de brevet en attente aux Etats - Unis et dans les
 * autres pays.
 *
 * L'utilisation est soumise aux termes du contrat de licence.
 *
 * Cette distribution peut comprendre des composants développés par des
 * tierces parties.
 *
 * Sun,  Sun Microsystems et  le logo Sun sont des marques de fabrique ou des
 * marques déposées de Sun Microsystems, Inc. aux Etats-Unis et dans
 * d'autres pays.
 */


/*
 * unixyp_passwd.c: simple program for hashing a clear text password
 * 	and comparing it to its "known" hashed value
 *
 * Usage:   unixyp_passwd (userid, clear_passwd)
 * Returns: 0  if good password
 *          !0 if password mismatch or can't find username
 */


#include <sys/types.h>
#include <stdio.h>
#include <string.h>
#include <sys/signal.h>
#include <stdlib.h>
#include <shadow.h>
#include <errno.h>

#ifdef LINUX
#include <pthread.h>
#define _XOPEN_SOURCE 
#include <unistd.h>
#else
#ifdef HPUX		/* hpux-dev */
#include <pthread.h>
#include <unistd.h>
#else
#include <thread.h>
#include <synch.h>
#endif
#endif


#define MAX_STRING_LEN 256
//hpux-dev
#if defined(LINUX) || defined(HPUX)
    static pthread_mutex_t lock =PTHREAD_MUTEX_INITIALIZER;
#else
    static mutex_t lock = DEFAULTMUTEX;
#endif

#ifdef HPUX		/* hpux-dev */
    static pthread_mutex_t lock_spnam = PTHREAD_MUTEX_INITIALIZER;
#endif

#ifndef HPUX		/* hpux-dev */
char *crypt(char *pw,char *salt);
#endif

int check_password(char *pw,char *e_pw) {

	char *cpw;
	int result = -1;
	#if defined(LINUX) || defined(HPUX)		/* hpux-dev */
	    pthread_mutex_lock(&lock);
	#else
	    mutex_lock(&lock);
	#endif
	cpw = crypt(pw,e_pw);
	result = strncmp(cpw,e_pw,MAX_STRING_LEN);
	#if defined(LINUX) || defined(HPUX)		/* hpux-dev */
	    pthread_mutex_unlock(&lock);
	#else
	    mutex_unlock (&lock);
	#endif
	return result;
}

get_enc_passwd (char *usernamep, char *encpwdp)
{
	struct spwd spwd, *spwdp;
	struct spwd tmpbuf;
	int buflen;
	extern int errno;

	#ifdef LINUX
	    getspnam_r(usernamep, &spwd, (char *)&tmpbuf, sizeof(struct spwd),&spwdp);
	#else 
	#ifdef HPUX		/* hpux-dev */
	   pthread_mutex_lock(&lock_spnam);
	   spwdp = getspnam(usernamep);
	   pthread_mutex_unlock(&lock_spnam);
	#else 
	    spwdp = getspnam_r(usernamep, &spwd, (char *)&tmpbuf, sizeof(struct spwd));
	#endif
	#endif

	if (spwdp != NULL) {
		strcpy (encpwdp, spwd.sp_pwdp);
		return (0);
	} else {
		*encpwdp = 0x00;
		return (-1);
	}
}

int
check_unix_passwd (char *usernamep, char *clear_passwordp)
{
	char username[MAX_STRING_LEN];
	char enc_passwd[MAX_STRING_LEN];
	int i;

	if (usernamep == NULL || clear_passwordp == NULL || *clear_passwordp == 0x00) {
		return(-1);
	}

	i = get_enc_passwd (usernamep, enc_passwd);
	if (i) {	/* can't find user in database */
		return (-2);
	}

	i = check_password (clear_passwordp, enc_passwd);
	return (i);
}

