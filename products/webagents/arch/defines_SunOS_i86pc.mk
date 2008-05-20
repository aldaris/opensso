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
# $Id: defines_SunOS_i86pc.mk,v 1.3 2008-05-20 00:30:11 madan_ranganath Exp $
#
# Copyright 2006 Sun Microsystems Inc. All Rights Reserved
#

# 
#
# This makefile is used to grab all the components needed to build.
# overrides what's in defines.mk and defines_SunOS.mk

OSMC_ARCH := $(OS_ARCH)_$(MC_ARCH)

DEST_LIB_DIR := $(DEST_DIR)/$(OSMC_ARCH)/lib
DEST_PACKAGE_DIR := $(DEST_DIR)/$(OSMC_ARCH)/packages
DEST_PACKAGE_SCRATCH_DIR := $(DEST_DIR)/$(OSMC_ARCH)/packages.scratch

DSAME_DROP_FILE := common_3_0_$(OSMC_ARCH)
