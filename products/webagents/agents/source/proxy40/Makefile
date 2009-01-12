#
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
#
# Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
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
# $Id: Makefile,v 1.1 2009-01-12 09:44:44 ranajitgh Exp $
#
#

USERX_ROOT = ../../..

include $(USERX_ROOT)/arch/components.mk

LIB_NAME := libampxy4
ifeq ($(OS_ARCH), WINNT)
LIB_NAME := ampxy4
endif

EXPORTED_LIBS := $(LIB_NAME)$(SO_EXT)
LIB_MAPFILE := $(LIB_NAME)$(MAPFILE_EXT)

OBJS := web_agent.o
DEPENDS := $(OBJS:.o=.d)

ifeq ($(OS_ARCH), SunOS)

CFLAGS += -D__svr4__ -DNET_SSL -DSERVER_BUILD -DDEBUG -DUSE_NSPR_MT \
	-DMCC_HTTPD -DXP_UNIX
INCLUDE_FLAGS := -I$(DEST_INC_DIR)  -I$(PROXY40_INC_DIR)
LDFLAGS += $(LD_COMMON_ORIGIN_FLAG) $(LD_ORIGIN_FLAG) -L$(DEST_LIB_DIR)
LDLIBS += -Bstatic -lamsdk
LDLIBS += $(LD_STATIC_FLAG) $(CXX_STD_LIBS) $(LD_SHARED_FLAG) -lc -lm
LDLIBS += -Bdynamic -L$(NSPR_LIB_DIR) -L$(LIBXML_LIB_DIR) -L$(NSS_LIB_DIR) -lc -lnspr4 -lplds4 -lplc4 -lnss3 -lssl3 -lxml2

PKG_DIR := $(DEST_PACKAGE_SCRATCH_DIR)/SUNWampxy4
PKG_TIMESTAMP += $(PKG_DIR)/timestamp
PACKAGE_REVISION := ,REV=$(shell date +%Y.%m.%d.%H.%M)
EXTRA_PACKAGE_FILES := config ../common/copyright \
	../common/depend.agent pkginfo ../common/prototype.agent \
	prototype unconfig 

all: depends export_libs

$(EXPORTED_LIBS): $(OBJS) $(LIB_MAPFILE)
	$(MAKE_SHARED_LIB)

endif

clean: clean_objs clean_libs
	$(RM) $(EXPORTED_LIBS)
ifeq ($(OS_ARCH),WINNT)
	$(RM) $(EXPORTED_LIBS:.dll=.exp) $(EXPORTED_LIBS:.dll=.lib)
	$(RM) *.ilk
endif

include $(USERX_ROOT)/arch/rules.mk
