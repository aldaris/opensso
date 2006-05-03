# $Id: rules_WINNT.mk,v 1.1 2006-05-03 22:43:34 madan_ranganath Exp $
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
# This makefile defines the Windows NT/2000 specific rules needed to build
# the Agent Pack.
#

%.cpp %.d:

%.o: %.cpp
	$(COMPILE.cc) $< $(OUTPUT_OPTION)

%.res: %.rc
	$(RC) -fo$@ $<

#
# We currently do not have any means of generating dependency information
# on Windows so make sure this variable has an empty value.
#
DEPENDS :=

#
# Build/clean up OS/compiler specific junk. 
#
.PHONY: build_objs build_workspace clean_objs clean_workspace

ifdef WORKSPACE_NAME
clean_workspace:
	msdev $(WORKSPACE_NAME) /MAKE $(MSDEV_BUILD_CONFIG) /CLEAN
	if [ -d $(MSDEV_BUILD_TYPE) ] ; then \
	    $(RMDIR) $(MSDEV_BUILD_TYPE) || exit 0 ; \
	fi
	$(RM) $(WORKSPACE_NAME:.dsw=.ncb $(WORKSPACE_NAME:.dsw=.plg)) $(WORKSPACE_NAME:.dsw=.opt)

clean_objs: clean_workspace

build_workspace:
	msdev $(WORKSPACE_NAME) /MAKE $(MSDEV_BUILD_CONFIG)

build_objs: build_workspace
endif

clean_objs:
ifneq ($(strip $(OBJS) $(DEPENDS)),)
	$(RM) $(OBJS) $(DEPENDS)
endif
