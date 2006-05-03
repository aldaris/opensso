# $Id: defines_Linux.mk,v 1.1 2006-05-03 22:43:33 madan_ranganath Exp $
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

ECHO := echo -e
NM := nm
RMDIR := rmdir -p --ignore-fail-on-non-empty

#
# C/C++ Compiler related symbols
#
COMPILERS_DIR :=
GCC_WARNING_FLAGS := -Wall -Wshadow 
CC := gcc
CXX := g++
GCC_33 := $(shell $(CC) -v 2>&1 | grep version | /bin/awk '{print $$3}')

CFLAGS += -DLINUX -pthread $(GCC_WARNING_FLAGS) -fexceptions
CXXFLAGS += -DLINUX -pthread $(GCC_WARNING_FLAGS) -Woverloaded-virtual -fexceptions
CXX_STD_LIBS := -lstdc++
LDFLAGS += -pthread
LD_ORIGIN_FLAG := -Xlinker '-R$$ORIGIN'
LD_COMMON_ORIGIN_FLAG := -Xlinker '-R$$ORIGIN/../../lib'
# NOTE: '-z defs' should probably be added to the following definition.
LD_FILTER_SYMS_FLAG = -Xlinker --version-script -Xlinker $(filter %.mapfile, $^)
LD_MAKE_SHARED_LIB_FLAG := -fPIC -shared -rdynamic
LD_SHARED_FLAG := -Wl,-Bdynamic
LD_STATIC_FLAG := -Wl,-Bstatic
LD_VERSION_LIB_FLAG = -Xlinker -h$@
PIC_FLAG := -fPIC

INSTALL_DIR := opt/agents
RPM_DIR :=  $(DEST_PACKAGE_SCRATCH_DIR)/RPMS/$(MC_ARCH)
BUILDROOT := /tmp/$(USER)/agent-buildroot
BUILDROOT_LIB_DIR := $(BUILDROOT)/$(INSTALL_DIR)/lib
BUILDROOT_CONF_DIR := $(BUILDROOT)/$(INSTALL_DIR)/config
BUILDROOT_BIN_DIR:= $(BUILDROOT)/$(INSTALL_DIR)/bin
BUILDROOT_INC_DIR:= $(BUILDROOT)/$(INSTALL_DIR)/include
BUILDROOT_SAMPLES_DIR:= $(BUILDROOT)/$(INSTALL_DIR)/samples
BUILDROOT_RPM_DIR:= $(BUILDROOT)/RPMS/$(MC_ARCH)

#
# Give DEBUG_FLAGS a default setting based on the build type
#
ifeq ($(BUILD_DEBUG), full)
  DEBUG_FLAGS := -g3 -DDEBUG
endif
ifeq ($(BUILD_DEBUG), optimize)
  DEBUG_FLAGS := -O2 -DNDEBUG
endif
ifndef DEBUG_FLAGS
  DEBUG_FLAGS := -g -O1 -DDEBUG
endif

SHELL_EXEC_EXTENSION :=

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

#
# the following is the name of the tar ball for dsame drop,.
#
DSAME_DROP_FILE_NAME := common_2_2_$(OS_ARCH)_$(MC_ARCH)

MAKE_STATIC_LIB = $(AR) $(ARFLAGS) $@ $(filter %.o, $^)
