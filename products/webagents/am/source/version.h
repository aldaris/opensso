/* The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 *
 * Abstract:
 *
 * Version object
 */

#ifndef _VERSION_H_
#define _VERSION_H_
#include "internal_macros.h"

BEGIN_PRIVATE_NAMESPACE
/*
 * Abstract:
 *
 * Version and build information written in the agent log file are
 * defined here. They have to be defined before each build.
 *
 */


/*
 * The major and minor version are fixed in a given branch
 * and should not be modified.
 * Those 4 variables should be updated before each build.
 *
 * - MICRO_VERSION:  third number
 *                   Ex:MICRO_VERSION "1" if version is 2.2.1
 *                      MICRO_VERSION "" if no micro version
 *
 * - PATCH_NUMBER:   number of a regular patch for web agents
 *                   Ex:PATCH_NUMBER "01" if agent is 2.1_01
 *                      PATCH_NUMBER "" if no patch number
 *
 * - ESCALATION_ID:  escalation ids when the build is not a regular patch
 *                   but a hotfix done for one or more escalations.
 *                   Ex: ESCALATION_ID "1-5705455,1-5705458"
 *                       ESCALATION_ID "" if not a hotfix
 *
 * - Mon Jun 19 16:17:10 PDT 2006:     date and time when the build is done in the format
 *                   given in the example
 *                   Ex:Mon Jun 19 16:17:10 PDT 2006 "Fri Jan  7 14:33:30 PST 2005"
 */
#define MICRO_VERSION NULL
#define PATCH_NUMBER NULL
#define ESCALATION_ID NULL

/* Should not be modified */

class Version {
public:
    static const char* getMajorVersion() {return "2";}
    static const char* getMinorVersion() {return "2";}
    static const char* getMicroVersion() {return MICRO_VERSION;}
    static const char* getPatchNumber() {return PATCH_NUMBER;}
    static const char* getEscalationId() {return ESCALATION_ID;}
    static const char* getBuildDate() {return "Mon Jun 19 16:17:10 PDT 2006";}
};

END_PRIVATE_NAMESPACE

#endif
