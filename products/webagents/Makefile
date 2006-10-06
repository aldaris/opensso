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
# Top level makefile for the Agent Pack.
#
#

override BUILD_MODULE := USERX_ROOT
USERX_ROOT := .
include $(USERX_ROOT)/arch/components.mk

#
# Make sure that the local copies of the components are populated before
# doing the main part of the build.
#

SUBDIRS := am/source agents/source

ifeq ($(OS_ARCH), WINNT)
   AM_LIB_PREFIX :=
   NSS_LIB_PREFIX :=
   NSS_EXE_SUFFIX := .exe
   LIB_XML_SUFFIX :=
else
   AM_LIB_PREFIX := lib
   NSS_LIB_PREFIX := lib
   NSS_EXE_SUFFIX :=
   LIB_XML_SUFFIX := .2
endif

all: all_subdirs $(DEST_DROP_DIR)/$(DSAME_DROP_FILE) agent_install

clean: clean_subdirs
	$(RM) -r $(DEST_DIR)
	$(RM) -r $(DIST_DIR)

drop: $(DEST_DROP_DIR)/$(DSAME_DROP_FILE)

$(DEST_DROP_DIR)/$(DSAME_DROP_FILE): $(DEST_PACKAGE_DIR) 
	$(ECHO) Making Target $(DSAME_DROP_FILE)
	$(MKDIR) $(DEST_DROP_DIR)
ifeq ($(OS_ARCH), WINNT)
	cd $(DEST_PACKAGE_SCRATCH_DIR)/SUNWamcom; \
	zip -r ../../../../$@.zip *
else
ifeq ($(OS_ARCH), Linux)
	# DSAME_DROP_FILE on linux takes the form of rpms which makes 
	# the name inconsistent with other platforms. 
	# release wants consistent name for all platforms, so use 
	# DSAME_DROP_FILE_NAME instead of just DSAME_DROP_FILE for Linux. 
	cd $(DEST_PACKAGE_SCRATCH_DIR)/common; \
	$(TAR) cvf ../../../../$(DEST_DROP_DIR)/$(DSAME_DROP_FILE_NAME).tar *; \
	gzip -f ../../../../$(DEST_DROP_DIR)/$(DSAME_DROP_FILE_NAME).tar
else
ifeq ($(OS_ARCH), HP-UX)
	cd $(DEST_PACKAGE_SCRATCH_DIR)/SUNWamcom; \
	$(TAR) cvf ../../../../$@.tar *; \
	gzip -f ../../../../$@.tar
else
	cd $(DEST_PACKAGE_SCRATCH_DIR); \
	$(TAR) cvf ../../../$@.tar is_csdk; \
	gzip -f ../../../$@.tar
endif
endif
endif

agent_install:
	ant -buildfile $(USERX_ROOT)/install/build.xml -Dagent_name=$(BUILD_AGENT)

	$(MKDIR) $(INSTALL_BIN)
	$(MKDIR) $(INSTALL_ETC)
	$(MKDIR) $(INSTALL_CONF)
	$(MKDIR) $(INSTALL_LIB)
	$(MKDIR) $(INSTALL_JCE)
	$(MKDIR) $(INSTALL_JSSE)
	$(MKDIR) $(INSTALL_LOCALE)

ifeq ($(OS_ARCH), WINNT)
	$(CP) $(USERX_ROOT)/am/source/cryptit.exe $(INSTALL_BIN)
else
	$(CP) $(USERX_ROOT)/am/source/crypt_util $(INSTALL_BIN)
endif
ifeq ($(OS_ARCH), WINNT)
	$(CP) $(LIBXML_LIB_DIR)/libxml2.dll $(INSTALL_LIB)
else
	$(CP) $(LIBXML_LIB_DIR)/libxml2.so $(INSTALL_LIB)
endif
	$(CP) $(USERX_ROOT)/conf/AMAgent.properties $(INSTALL_CONF)/AMAgent.template
	$(CP) $(USERX_ROOT)/dist/$(BUILD_AGENT)-install.jar $(INSTALL_LIB)/
	$(CP) $(USERX_ROOT)/extlib/opensso-installtools.jar $(INSTALL_LIB)/
	$(CP) $(USERX_ROOT)/extlib/opensso-installtools-launcher.jar $(INSTALL_LIB)/
	$(CP) $(USERX_ROOT)/install/scripts/agentadmin $(INSTALL_BIN)/
	$(CP) $(USERX_ROOT)/install/scripts/agentadmin.bat $(INSTALL_BIN)/
	$(CP) $(USERX_ROOT)/install/resources/amToolsMessages.properties $(INSTALL_LOCALE)/
	$(CP) $(USERX_ROOT)/install/resources/AMToolsConfig.properties $(INSTALL_CONF)/
	$(CP) $(USERX_ROOT)/install/$(BUILD_AGENT)/resources/$(BUILD_AGENT)Tools.properties $(INSTALL_LOCALE)/
	$(CP) $(USERX_ROOT)/install/$(BUILD_AGENT)/xml/configure.xml $(INSTALL_CONF)/
	$(CP) $(USERX_ROOT)/legal/license.txt $(INSTALL_AGENT_DIR)/
	$(CP) $(USERX_ROOT)/README $(INSTALL_AGENT_DIR)/

ifeq ($(BUILD_AGENT), apache)
ifeq ($(OS_ARCH), WINNT)
	$(CP) $(DEST_LIB_DIR)/libamapc2.dll $(INSTALL_LIB)
else
	$(CP) $(DEST_LIB_DIR)/libamapc2.so $(INSTALL_LIB)
endif
	$(CP) $(USERX_ROOT)/agents/source/apache/dsame.conf $(INSTALL_ETC)/dsame.template
endif

ifeq ($(OS_ARCH), SunOS)
	cd $(BUILT_DIR); \
	$(TAR) cvf $(BUILD_AGENT)_SunOS_agent.tar web_agents; \
	gzip -f $(BUILD_AGENT)_SunOS_agent.tar; \
	$(MV) $(BUILD_AGENT)_SunOS_agent.tar.gz ../dist/ 
	$(RM) $(USERX_ROOT)/dist/$(BUILD_AGENT)-install.jar
endif
ifeq ($(OS_ARCH), Linux)
	cd $(BUILT_DIR); \
	$(TAR) cvf $(BUILD_AGENT)_Linux_agent.tar web_agents; \
	gzip -f $(BUILD_AGENT)_Linux_agent.tar; \
	$(MV) $(BUILD_AGENT)_Linux_agent.tar.gz ../dist/ 
	$(RM) $(USERX_ROOT)/dist/$(BUILD_AGENT)-install.jar
endif
ifeq ($(OS_ARCH), WINNT)
	cd $(BUILT_DIR); \
	zip -r $(BUILD_AGENT)_WINNT_agent.zip web_agents; \
	$(MV) $(BUILD_AGENT)_WINNT_agent.zip ../dist/ 
	$(RM) $(USERX_ROOT)/dist/$(BUILD_AGENT)-install.jar
endif

make_zip:
ifdef BUILD_SHIP
ifeq ($(OS_ARCH), WINNT)
	$(CP) $(DEST_DROP_DIR)/$(DSAME_DROP_FILE).zip $(BUILD_SHIP)
else
ifeq ($(OS_ARCH), Linux)
	$(CP) $(DEST_DROP_DIR)/$(DSAME_DROP_FILE_NAME).tar.gz $(BUILD_SHIP)
else
	$(CP) $(DEST_DROP_DIR)/$(DSAME_DROP_FILE).tar.gz $(BUILD_SHIP)
endif
endif
	$(CP) $(DEST_PACKAGE_DIR)/agent_$(OS_ARCH)* $(BUILD_SHIP)
endif

include $(USERX_ROOT)/arch/rules.mk
