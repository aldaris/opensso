# $Id: defines_SunOS.mk,v 1.1 2006-05-03 22:43:33 madan_ranganath Exp $
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

PATH_SEP := :

ECHO := echo
NM := nm -p

#
# C/C++ Compiler related symbols
#
CC := cc
CXX := CC

CFLAGS += -DSOLARIS -mt
CXXFLAGS += -DSOLARIS -mt
CXX_STD_LIBS := -lCstd -lCrun
LDFLAGS += -mt -R /usr/lib/mps 
LD_ORIGIN_FLAG := '-R$$ORIGIN'
LD_COMMON_ORIGIN_FLAG := '-R/opt/SUNWam/lib:/usr/lib/mps'
# NOTE: '-z defs' should probably be added to the following definition.
LD_FILTER_SYMS_FLAG = -M$(filter %.mapfile, $^)
LD_MAKE_SHARED_LIB_FLAG := -G -znodelete
LD_SHARED_FLAG := -Bdynamic
LD_STATIC_FLAG := -Bstatic
LD_VERSION_LIB_FLAG = -h$@
PIC_FLAG := -KPIC

#
# Give DEBUG_FLAGS a default setting based on the build type
#
ifeq ($(BUILD_DEBUG), full)
  DEBUG_FLAGS := -g -DDEBUG
endif
ifeq ($(BUILD_DEBUG), optimize)
  DEBUG_FLAGS := -xO3 -DNDEBUG
endif
ifndef DEBUG_FLAGS
  DEBUG_FLAGS := -g -xO1 -DDEBUG
endif

SHELL_EXEC_EXTENSION :=

#
# processor name used for ARCH variable in pkginfo 
#
PKG_ARCH := $(shell uname -p)

#
# NOTE: The JAVA* variables need to be set with '=', rather than ':=',
# because JAVA_HOME variable is an alias for the JDK_DIR variable, which
# is set in components.mk, which includes this file before defining that
# variable.
#
JAVA    = $(JAVA_HOME)/bin/java
JAVAC   = $(JAVA_HOME)/bin/javac 
JAVAH   = $(JAVA_HOME)/bin/javah 
JAVADOC = $(JAVA_HOME)/bin/javadoc
JAR     = $(JAVA_HOME)/bin/jar

LN_s := ln -s
TAR := /tools/ns/bin/tar

ifeq ($(MC_ARCH), i86pc)
include $(USERX_ROOT)/arch/defines_SunOS_$(MC_ARCH).mk
endif

PRODUCT_DIR := SUNWam/agents
#
# How to make a System V package
#
# The following is intentionally defined with '=', instead of ':=',
# so that the target dependent values are used.
#
MAKE_STATIC_LIB = $(CXX) $(LD_STATIC_FLAGS) -o $@ $(filter %.o, $^)
