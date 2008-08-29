/**
 * $Id: debug_utils.c,v 1.1 2008-08-29 22:06:41 kevinserwin Exp $
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


#include <stdio.h>
#include <sys/time.h>
#include <time.h>

#if defined(LINUX) || defined(HPUX)
#define DEBUG_FILE "/var/opt/sun/identity/debug/unix_client.debug"
#else
#define DEBUG_FILE "/var/opt/SUNWam/debug/unix_client.debug"
#endif

#if defined(LINUX ) || defined(HPUX)
#include <sys/types.h>
#endif

char
get_ascii (char c)
{
	char cc = '*';

	if ((c >= ' ') && (c <= '~'))
		cc = c;
	return (cc);
}

void
hexdump (FILE *fp, u_char *buf, int len)
{
	int i, j = 0, k;
	u_char uc;
	char interp[16];

	if (fp) {
		fprintf (fp, "    ");
	} else {
		printf ("    ");
	}
	for (i = 0; i < len; i++) {
		uc = *buf;
		buf++;
		if (fp) {
			fprintf (fp, "%02x ", uc);
		} else {
			printf ("%02x ", uc);
		}
		interp[j] = (char)uc;
		if (++j >= 16) {
			if (fp) {
				fprintf (fp, "\t");
			} else {
				printf ("\t");
			}
			for (j = 0; j < 16; j++) {
				if (fp) {
					fprintf (fp, "%c", get_ascii(interp[j]));
				} else {
					printf ("%c", get_ascii(interp[j]));
				}
			}
			if (fp) {
				fprintf (fp, "\n    ");
			} else {
				printf ("\n    ");
			}
			j = 0;
		}
	}
	if (j) {
		i = 16-j;
		for (k = 0; k < i; k++) {
			if (fp) {
				fprintf (fp, "   ");
			} else {
				printf ("   ");
			}
		}
		if (fp) {
			fprintf (fp, "\t");
		} else {
			printf ("\t");
		}
		for (k = 0; k < j; k++) {
			if (fp) {
				fprintf (fp, "%c", get_ascii(interp[k]));
			} else {
				printf ("%c", get_ascii(interp[k]));
			}
		}
	}
	if (fp) {
		fprintf (fp, "\n");
	} else {
		printf ("\n");
	}
}


void
get_time_str (char *cp, int maxsz)
{
	struct tm *tm;
	struct timeval time;

	#ifdef SOLARIS
	char *nullp = NULL;

	if (gettimeofday (&time, nullp))
		printf ("gettimeofday error%c", 10);
        #endif
	#if defined(LINUX) || defined(HPUX)
	struct timezone *nullp = NULL;

	if (gettimeofday (&time, nullp))
		printf ("gettimeofday error%c", 10);
        #endif
	tm = localtime (&time.tv_sec);
	strftime (cp, maxsz, "%x %X", tm);
}

/*
 *  if DEBUG_FILE exists, then add trace stuff to it,
 *  otherwise, just return.
 */

void
debug_trace(char *stuff)
{
	FILE *foo;
	char datetime[18];

	foo = fopen(DEBUG_FILE, "r");
	if (!foo) return;
	fclose(foo);

	get_time_str (datetime, sizeof(datetime));
	foo = fopen(DEBUG_FILE, "a");
	fprintf(foo, "%s: %s", datetime, stuff);
	fflush(foo);
	fclose(foo);
	return;
}

void
debug_trace_dump(char *stuff, char *bufp, int buflen)
{
	FILE *foo;
	char datetime[18];

	foo = fopen(DEBUG_FILE, "r");
	if (!foo) return;
	fclose(foo);

	get_time_str (datetime, sizeof(datetime));
	foo = fopen(DEBUG_FILE, "a");
	fprintf(foo, "%s: %s", datetime, stuff);
	fprintf(foo, "\ndata:\n");
	hexdump (foo, (u_char *)bufp, buflen);
	fflush(foo);
	fclose(foo);
	return;
}

