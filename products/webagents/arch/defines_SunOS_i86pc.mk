# $Id: defines_SunOS_i86pc.mk,v 1.1 2006-05-03 22:43:33 madan_ranganath Exp $
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
# This makefile is used to grab all the components needed to build.
# overrides what's in defines.mk and defines_SunOS.mk

OSMC_ARCH := $(OS_ARCH)_$(MC_ARCH)

DEST_LIB_DIR := $(DEST_DIR)/$(OSMC_ARCH)/lib
DEST_PACKAGE_DIR := $(DEST_DIR)/$(OSMC_ARCH)/packages
DEST_PACKAGE_SCRATCH_DIR := $(DEST_DIR)/$(OSMC_ARCH)/packages.scratch

DSAME_DROP_FILE := common_2_2_$(OSMC_ARCH)

