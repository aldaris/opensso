# $Id: rules.mk,v 1.1 2006-05-03 22:43:34 madan_ranganath Exp $
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
# This makefile defines the standard rules used to build the Agent Pack.
#
# Make sure USERX_ROOT is defined before including this file.

ifndef	RULES_INCLUDED
RULES_INCLUDED := true

include $(USERX_ROOT)/arch/defines.mk

.PHONY: all clean depends

internal_DEST_DIRS := \
	$(DEST_BIN_DIR) \
	$(DEST_CLASS_DIR) \
	$(DEST_CONFIG_DIR) \
	$(DEST_DOC_DIR) \
	$(DEST_DROP_DIR) \
	$(DEST_EXAMPLES_DIR) \
	$(DEST_INC_DIR) \
	$(DEST_LIB_DIR) \
	$(DEST_SAMPLES_DIR) \
	$(DEST_PACKAGE_DIR) \
	$(DEST_PACKAGE_SCRATCH_DIR) \
	$(DEST_TEST_DIR)

include $(USERX_ROOT)/arch/rules_$(OS_ARCH).mk

$(internal_DEST_DIRS):
	$(MKDIR) $@

bytecode: $(DEST_CLASS_DIR)
	$(JAVAC) $(JAVAC_FLAGS) -classpath "$(CLASSPATH)" -d $(DEST_CLASS_DIR) $(filter %.java, $(SRCS))

#
# Provide this for backward compatibility.
#
subdirs: all_subdirs
cleansubdirs: clean_subdirs

%_subdirs:
ifeq	($(strip $(SUBDIRS)),)
	@echo "There are no SUBDIRS to process."
else
	@set -e; for i in $(SUBDIRS); do \
		$(MAKE) -C $$i $*; \
	done
endif

$(DEST_LIB_DIR)/% $(DEST_INC_DIR)/%: %
	$(CP) $< $@

ifdef	PACKAGE_PREFIX
DEST_PACKAGE_CLASS_DIR := $(DEST_CLASS_DIR)/$(PACKAGE_PREFIX)
JAVA_OBJS += $(patsubst %.java, %.class, $(filter %.java, $(SRCS)))

ifeq	($(strip $(JAVA_OBJS)),)
clean_bytecode:
	@echo "No bytecode to clean up."
else
clean_bytecode:
	if [ -d $(DEST_PACKAGE_CLASS_DIR) ]; then \
	    (cd $(DEST_PACKAGE_CLASS_DIR) ; $(RM) $(JAVA_OBJS)) ; \
	    $(RMDIR) $(DEST_PACKAGE_CLASS_DIR) || exit 0; \
	fi
endif
else
clean_bytecode:
	@echo "Must define PACKAGE_PREFIX to use the clean_bytecode target."
endif

clean_headers:
	if [ -d $(DEST_INC_DIR) ]; then \
	    (cd $(DEST_INC_DIR) ; $(RM) $(EXPORTED_HDRS)) ; \
	    $(RMDIR) $(DEST_INC_DIR) || exit 0 ; \
	fi

clean_libs:
	if [ -d $(DEST_LIB_DIR) ]; then \
	    (cd $(DEST_LIB_DIR) ; $(RM) $(EXPORTED_LIBS)) ; \
	    $(RMDIR) $(DEST_LIB_DIR) || exit 0 ; \
	fi

clean_samples:
	if [ -d $(DEST_SAMPLES_DIR) ]; then \
	    (cd $(DEST_SAMPLES_DIR) ; $(RM) $(EXPORTED_SAMPLES)) ; \
	    $(RMDIR) $(DEST_SAMPLES_DIR) || exit 0 ; \
	fi

export_headers: $(DEST_INC_DIR) \
		$(patsubst %, $(DEST_INC_DIR)/%, $(EXPORTED_HDRS))

export_libs: $(DEST_LIB_DIR) $(patsubst %, $(DEST_LIB_DIR)/%, $(EXPORTED_LIBS))
export_static_libs: $(DEST_LIB_DIR) $(patsubst %, $(DEST_LIB_DIR)/%, $(EXPORTED_STATIC_LIBS))

export_samples: $(DEST_SAMPLES_DIR) 
		$(CP) $(EXPORTED_SAMPLES) $(DEST_SAMPLES_DIR)

copyProperties: $(DEST_TEST_DIR)
	$(CP) *.properties $(DEST_TEST_DIR)

copyLocaleFiles: $(DEST_CONFIG_DIR)/locale $(DEST_CLASS_DIR) $(LOCALE_FILES)
	$(CP) $(LOCALE_FILES) $(DEST_CONFIG_DIR)/locale
	$(CP) $(LOCALE_FILES) $(DEST_CLASS_DIR)

depends: $(DEPENDS)

endif
