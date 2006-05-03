# $Id: defines_WINNT.mk,v 1.1 2006-05-03 22:43:33 madan_ranganath Exp $
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

JAVA    := java
JAVAC   := javac 
JAVAH   := javah 
JAVADOC := javadoc
JAR     := jar

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
