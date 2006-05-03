# $Id: components.mk,v 1.1 2006-05-03 22:43:33 madan_ranganath Exp $
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
# This makefile defines the location of the all of the common components
# used to build the Agent Pack.
#
# Make sure USERX_ROOT is defined before including this file.


ifndef	COMPONENTS_INCLUDED
COMPONENTS_INCLUDED := true

#
# The following is here to insure that 'all' is always the default target.
#
all:

include $(USERX_ROOT)/arch/defines.mk

##########################################################
# Assemble all the bits into the standard set of symbols
##########################################################

##########################################
# Apache 2.x defines
##########################################
APACHE_DIR = $(EXTERNAL_DIR)/apache
APACHE_INC_DIR = $(APACHE_DIR)/include
APACHE_LIB_DIR = $(APACHE_DIR)/lib


##########################################
# LIBXML defines
##########################################
LIBXML_DIR := $(EXTERNAL_DIR)/libxml2
LIBXML_INC_DIR := $(LIBXML_DIR)/include/libxml2
LIBXML_LIB_DIR := $(LIBXML_DIR)/lib
ifndef LIBXML_LIBS
ifeq ($(OS_ARCH), WINNT)
LIBXML_LIBS := -llibxml2
else
LIBXML_LIBS := -lxml2
endif
endif


##########################################
# NSPR defines
##########################################

NSPR_DIR := $(EXTERNAL_DIR)/nspr
NSPR_INC_DIR := $(NSPR_DIR)/include
NSPR_LIB_DIR := $(NSPR_DIR)/lib

ifndef	NSPR_LIBS
ifeq ($(OS_ARCH), WINNT)
NSPR_LIBS := -llibplc4 -llibplds4 -llibnspr4
else
NSPR_LIBS := -lplc4 -lplds4 -lnspr4
endif
endif

##########################################
# NSS defines
##########################################

NSS_DIR := $(EXTERNAL_DIR)/nss
NSS_BIN_DIR := $(NSS_DIR)/bin
NSS_INC_DIR := $(NSS_DIR)/include
NSS_LIB_DIR := $(NSS_DIR)/lib
NSS_DYNAMIC_LIBS := -lssl3 -lnss3 
NSS_LIBS := $(NSS_DYNAMIC_LIBS)

endif

