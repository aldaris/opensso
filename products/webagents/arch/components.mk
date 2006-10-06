#
# The contents of this file are subject to the terms
# of the Common Development and Distribution License
# (the License). You may not use this file except in
#
# The contents of this file are subject to the terms
# of the Common Development and Distribution License
# (the License). You may not use this file except in
# compliance with the License.
#
# You can obtain a copy of the License at
# https://opensso.dev.java.net/public/CDDLv1.0.html or
# opensso/legal/CDDLv1.0.txt
# See the License for the specific language governing
# permission and limitations under the License.
#
# When distributing Covered Code, include this CDDL
# Header Notice in each file and include the License file
# at opensso/legal/CDDLv1.0.txt.
# If applicable, add the following below the CDDL Header,
# with the fields enclosed by brackets [] replaced by
# your own identifying information:
# "Portions Copyrighted [year] [name of copyright owner]"
#
# $Id: components.mk,v 1.2 2006-10-06 18:27:27 subbae Exp $
# 
# Copyright 2006 Sun Microsystems Inc. All Rights Reserved
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

