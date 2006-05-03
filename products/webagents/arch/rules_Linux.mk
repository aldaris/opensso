# $Id: rules_Linux.mk,v 1.1 2006-05-03 22:43:34 madan_ranganath Exp $
#
# Copyright © 2006 Sun Microsystems, Inc. All rights reserved.
# 
# U.S. Government Rights - Commercial software. Government users are
# subject to the Sun Microsystems, Inc. standard license agreement and
# applicable provisions of the FAR and its supplements. Use is subject to
# license terms. Sun, Sun Microsystems, the Sun logo and Sun ONE are
# trademarks or registered trademarks of Sun Microsystems, Inc. in the
# U.S. and other countries.
# 
# Copyright © 2006 Sun Microsystems, Inc. Tous droits réservés.
# 
# Droits du gouvernement américain, utlisateurs gouvernmentaux - logiciel
# commercial. Les utilisateurs gouvernmentaux sont soumis au contrat de
# licence standard de Sun Microsystems, Inc., ainsi qu aux dispositions en
# vigueur de la FAR [ (Federal Acquisition Regulations) et des suppléments
# à celles-ci.
# 
# Distribué par des licences qui en restreignent l'utilisation. Sun, Sun
# Microsystems, le logo Sun et Sun ONE sont des marques de fabrique ou des
# marques déposées de Sun Microsystems, Inc. aux Etats-Unis et dans
# d'autres pays.
# 
#
# This makefile defines the Solaris-specific rules needed to build
# the Agent Pack.
#

%.cpp %.cxx %.d:

%.o: %.cpp
	$(COMPILE.cc) $< $(OUTPUT_OPTION)

%.o: %.cxx
	$(COMPILE.cc) $< $(OUTPUT_OPTION)

%.d: %.c
	set -e; $(filter-out -g3,$(COMPILE.c)) -MM $< \
		| sed 's;\($*\.o\)[ :]*;\1 $@ : ;' > $@; [ -s $@ ] || $(RM) $@

%.d: %.cpp
	set -e; $(filter-out -g3,$(COMPILE.cc)) -MM $< \
		| sed 's;\($*\.o\)[ :]*;\1 $@ : ;' > $@; [ -s $@ ] || $(RM) $@

%.d: %.cxx
	set -e; $(filter-out -g3,$(COMPILE.cc)) -MM $< \
		| sed 's;\($*\.o\)[ :]*;\1 $@ : ;' > $@; [ -s $@ ] || $(RM) $@

#
# Clean up OS/compiler specific junk. 
#
clean_objs:
	$(RM) $(OBJS) $(DEPENDS)
