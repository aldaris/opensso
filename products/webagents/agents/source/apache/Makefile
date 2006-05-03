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
# Copyright 2006 Sun Microsystems Inc. All Rights Reserved
#
# Makefile for building the Apache Policy Agent.
#

USERX_ROOT = ../../..

include $(USERX_ROOT)/arch/components.mk

ifeq ($(OS_ARCH), WINNT)
APACHE25_LIB_NAME := libamapc2
APACHE25_LIB := $(APACHE25_LIB_NAME)$(SO_EXT)
APACHE25_INCLUDE_FLAGS := -I$(DEST_INC_DIR) -I../common
APACHE25_CFLAGS += -DAPACHE2 -DEAPI -DUSE_EXPAT -DXP_WIN32 -DAM_STATIC_LIB $(PIC_FLAG) -DCORE_PRIVATE $(APACHE25_INCLUDE_FLAGS) -I$(APACHE_INC_DIR)
LDFLAGS := ../../../am/source/am_web.res -L$(APACHE_LIB_DIR) -L$(DEST_LIB_DIR) -L$(NSS_LIB_DIR) -L$(NSPR_LIB_DIR) -L$(LIBXML_LIB_DIR) -L../../built/WINNT/lib
LDLIBS := -llibapr -llibapriconv -llibhttpd -lamsdk_static -llibnspr4 -lnss3 -lssl3 -llibplc4 -llibxml2 -luser32

SRCS := apache_agent.c
$(APACHE25_LIB_NAME).o: apache_agent.c
	$(COMPILE.c) $(APACHE25_CFLAGS) $< $(OUTPUT_OPTION)
$(APACHE25_LIB): APACHE_VERSION:=$(APACHE_25_VERSION)
APACHE25_OBJS := $(patsubst %.c, $(APACHE25_LIB_NAME).o, $(SRCS))
EXPORTED_LIBS := $(APACHE25_LIB)
OBJS := $(APACHE25_OBJS)
$(APACHE25_LIB): $(APACHE25_OBJS) libamapc.def
	$(MAKE_SHARED_LIB)

PKG_DIR := $(DEST_PACKAGE_SCRATCH_DIR)/SUNWamapache
APACHE25_ZIP_FILE := SUNWamapache.zip
ZIP_DIRS := bin config 

$(APACHE25_ZIP_FILE): $(DEST_PACKAGE_SCRATCH_DIR) $(APACHE25_LIB)
	$(RM) -r $(PKG_DIR)
	$(MKDIR) $(patsubst %, $(PKG_DIR)/apache/%, $(ZIP_DIRS))
	$(CP) $(filter %$(SO_EXT), $^) $(PKG_DIR)/apache/bin
	$(CP) dsame.conf $(PKG_DIR)/apache/config/dsame.conf.orig
	cd $(PKG_DIR) ; zip -r ../$(notdir $@) apache

all: $(APACHE25_ZIP_FILE)

clean: clean_libs clean_objs
	$(RM) $(APACHE25_LIB_NAME).o $(APACHE25_LIB_NAME).dll $(APACHE25_LIB_NAME).lib $(APACHE25_LIB_NAME).exp

else
APACHE2_LIB_NAME := libamapc2

APACHE2_LIB := $(APACHE2_LIB_NAME)$(SO_EXT)
	
APACHE2_INCLUDE_FLAGS := -I$(DEST_INC_DIR) -I../common

ifeq ($(OS_ARCH), Linux)
   ifeq ($(OS_ARCH_VER), 2.2.20-compact)
     APACHE2_CFLAGS += -D_FILE_OFFSET_BITS=64
   endif
endif


ifeq ($(OS_ARCH), Linux)
     ifeq ($(MC_ARCH), x86_64)
          APACHE2_CFLAGS += -DX86_64
      endif
endif

APACHE2_CFLAGS += -DAPACHE2 -DEAPI -DMOD_PERL -DUSE_EXPAT -DXP_UNIX $(PIC_FLAG) -DCORE_PRIVATE $(APACHE2_INCLUDE_FLAGS) -I$(APACHE_DIR)/include

LDFLAGS += $(LD_COMMON_ORIGIN_FLAG) $(LD_ORIGIN_FLAG)

SYSTEM_LIBS := -lpthread -ldl -lc -lm
LDFLAGS += $(LD_COMMON_ORIGIN_FLAG) $(LD_ORIGIN_FLAG)
LDFLAGS += -L$(NSS_LIB_DIR) -L$(NSPR_LIB_DIR) -L$(LIBXML_LIB_DIR)
$(STD_LIB)_LDLIBS := $(NSS_LIBS) $(NSPR_LIBS)
LDLIBS += -L ../../../am/source
LDLIBS += $(LDFLAGS) $(LIBXML_LIBS) $($(STD_LIB)_LDLIBS) $(SYSTEM_LIBS)

ifeq ($(OS_ARCH), SunOS)
LDFLAGS += -L$(DEST_LIB_DIR)
LDLIBS += $(LD_SHARED_FLAG) $(CXX_STD_LIBS) -lc -lm
endif

LDLIBS += -Bstatic -lamsdk

$(APACHE2_LIB_NAME).o: apache_agent.c
	$(COMPILE.c) $(APACHE2_CFLAGS) $< $(OUTPUT_OPTION)

ifeq ($(OS_ARCH), Linux)
$(APACHE2_LIB): APACHE_VERSION:=$(APACHE_252_VERSION)
else
$(APACHE2_LIB): APACHE_VERSION:=$(APACHE_2_VERSION)
endif

SRCS := apache_agent.c
APACHE2_OBJS := $(patsubst %.c, $(APACHE2_LIB_NAME).o, $(SRCS))
OBJS := $(APACHE2_OBJS)

EXPORTED_LIBS := $(APACHE2_LIB)

$(APACHE2_LIB): $(APACHE2_OBJS) libamapc.mapfile
	$(MAKE_SHARED_LIB)
 
all: export_libs

EXTRA_RPM_FILES := dsame.conf apcagent-2.2-0.spec
RPM := $(RPM_DIR)/apcagent-2.2-0.$(MC_ARCH).rpm
BUILDROOT_APACHE := $(BUILDROOT)/$(INSTALL_DIR)/apache
BUILDROOT_APACHE_LIB_DIR := $(BUILDROOT_APACHE)/lib
BUILDROOT_APACHE_CONF_DIR := $(BUILDROOT_APACHE)/config
BUILDROOT_APACHE_BIN_DIR := $(BUILDROOT_APACHE)/bin
BUILDROOT_APACHE_CERT_DIR := $(BUILDROOT_APACHE)/cert
ifeq ($(OS_ARCH), Linux)
all: $(RPM)
endif

$(RPM): $(EXPORTED_LIBS) $(EXTRA_RPM_FILES)
	$(MKDIR) $(BUILDROOT_CONF_DIR)
	$(MKDIR) $(BUILDROOT_BIN_DIR)
	$(MKDIR) $(RPM_DIR)/apache
	$(RM) -r $(BUILDROOT_APACHE)
	$(MKDIR) $(BUILDROOT_APACHE_LIB_DIR)
	$(MKDIR) $(BUILDROOT_APACHE_CONF_DIR)
	$(MKDIR) $(BUILDROOT_APACHE_BIN_DIR)
	$(MKDIR) $(BUILDROOT_APACHE_CERT_DIR)
	$(MKDIR) $(BUILDROOT_RPM_DIR)
	$(CP) $(USERX_ROOT)/conf/AMAgent.properties $(BUILDROOT_CONF_DIR)/AMAgent.properties.orig
	$(CP) $(USERX_ROOT)/am/source/crypt_util $(BUILDROOT_BIN_DIR)
	$(CP) config_linux $(BUILDROOT_APACHE_BIN_DIR)
	$(CP) unconfig_linux $(BUILDROOT_APACHE_BIN_DIR)
	$(CP) $(NSS_BIN_DIR)/certutil $(BUILDROOT_APACHE_CERT_DIR)
	$(CP) $(DEST_LIB_DIR)/libamapc2.so $(BUILDROOT_APACHE_LIB_DIR)
	$(CP) dsame.conf $(BUILDROOT_APACHE_CONF_DIR)/dsame.conf.orig
	$(CP) $(LIBXML_LIB_DIR)/libxml2.so.2 $(BUILDROOT_APACHE_LIB_DIR)
	$(CP) ../common/comlib_linux $(BUILDROOT_APACHE_BIN_DIR)
	if [ -f $(HOME)/.rpmmacros ] ; then mv $(HOME)/.rpmmacros $(HOME)/.rpmmacros.agent; fi
	echo "%_topdir $(BUILDROOT)" > $(HOME)/.rpmmacros
	rpmbuild -bb --buildroot $(BUILDROOT) --target $(MC_ARCH) apcagent-2.2-0.spec
	$(RM) $(HOME)/.rpmmacros
	if [ -f $(HOME)/.rpmmacros.agent ] ; then mv $(HOME)/.rpmmacros.agent $(HOME)/.rpmmacros ; fi

	$(MV) $(BUILDROOT_RPM_DIR)/apcagent-2.2-0.$(MC_ARCH).rpm $(RPM_DIR)/apache
	$(RM) -r $(BUILDROOT)

clean: clean_libs clean_objs
	$(RM) $(EXPORTED_LIBS) pkginfo.apache
	$(RM) -r $(DEST_PACKAGE_SCRATCH_DIR)/RPMS
	$(RM) $(APACHE2_LIB_NAME).o
endif

include $(USERX_ROOT)/arch/rules.mk
