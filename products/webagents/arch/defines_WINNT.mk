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
# $Id: defines_WINNT.mk,v 1.2 2006-10-06 18:27:28 subbae Exp $
# 
# Copyright 2006 Sun Microsystems Inc. All Rights Reserved
#

#
# This makefile defines a number of standard OS-dependent symbols
# used in by the makefiles that build the Agent Pack.
#

PATH_SEP := ;

RELTOOLS_SUFFIX_ARG := -suf .zip

CCG := $(USERX_ROOT)/arch/compiler.ccg
export CCG
COMPILERS_DIR :=
CC := cc
CXX := cc
EXE_EXT := .exe
MAPFILE_EXT := .def
NM := nm
RC := rc
ifdef	OS_IS_CYGWIN
    RMDIR := rmdir -p --ignore-fail-on-non-empty
else
    RMDIR := rmdir -p
endif
SO_EXT := .dll
LIB_EXT := .lib

CFLAGS += -DWINNT -DWIN32
CXXFLAGS += -DWINNT -DWIN32
LD_FILTER_SYMS_FLAG = -def:$(filter %$(MAPFILE_EXT),$^)
LD_MAKE_SHARED_LIB_FLAG := -dll
# XXX - The following needs be set to the appropriate value.
LD_VERSION_LIB_FLAG :=
PLATFORM_SHARED_OBJS=$(filter %.res, $^)

#
# Give DEBUG_FLAGS a default setting based on the build type
#
ifeq ($(BUILD_DEBUG), full)
    DEBUG_FLAGS := -g -DDEBUG
endif
ifeq ($(BUILD_DEBUG), optimize)
    DEBUG_FLAGS := -O -DNDEBUG
endif
ifndef DEBUG_FLAGS
    DEBUG_FLAGS := -g -DDEBUG
endif

SHELL_EXEC_EXTENSION := .bat

#
# Give MSDEV_BUILD_TYPE a default setting based on the build type
#
ifeq ($(BUILD_DEBUG), full)
    MSDEV_BUILD_TYPE := Debug
endif
ifeq ($(BUILD_DEBUG), optimize)
    MSDEV_BUILD_TYPE := Release
endif
ifndef MSDEV_BUILD_TYPE
    MSDEV_BUILD_TYPE := Debug
endif

MSDEV_BUILD_CONFIG := "All - $(MSDEV_BUILD_TYPE)"

MAKE_STATIC_LIB = LIB -nodefaultlib -nologo $(filter %.o, $^) -OUT:$@
