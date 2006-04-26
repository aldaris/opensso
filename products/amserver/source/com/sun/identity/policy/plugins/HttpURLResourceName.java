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
 * $Id: HttpURLResourceName.java,v 1.1 2006-04-26 05:14:47 dillidorai Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */



package com.sun.identity.policy.plugins;

import com.sun.identity.policy.ResourceMatch;

/** 
  * This implementation of <code>ResourceName</code> extends <code>
  * URLResourceName</code> to add special handling of resources which end 
  * in delimiter followed by a multi level wildcard. Wildcard card at the end
  * matches across levels.
  * so <code>http://abc.com/b/c/d/*</code> matches <code>
  * http://abc.com/b/c/d/e/f/g.html</code>
  * while <code>http://abc.com/"*"/d</code> matches only string of type
  * <code>http://abc.com/a/d</code>. So embedded wildcard matches only at that
  * level where specified, while wildcard at the end, matches
  * across levels.
  * Behaviour of existing URLResourceName was not changed to
  * maintain backward compatibility.
  */

public class HttpURLResourceName extends URLResourceName {

    /**
     * Compares two resources.
     *
     * Description: The targetResource may contain wildcard '*' which
     *     matches zero or more characters. The wildcard character 
     *     can show up anywhere within the string. The targetResource
     *     can contain any number of wildcard characters.
     *     One of the five possible match types is returned based 
     *     on how the two resource strings are related. The comparison
     *     starts from the beginning of the reource strings.
     *  
     *     ResourceMatch.NO_MATCH means two resources don't match.
     *     ResourceMatch.EXACT_MATCH means two resources match.
     *     ResourceMatch.SUB_RESOURCE_MATCH means targetResource is 
     *         the sub resource of the requestResource.
     *     ResourceMatch.SUPER_RESOURCE_MATCH means targetResource 
     *         is the super resource of the requestResource.
     *     ResourceMatch.WILDCARD_MATCH means two resources match 
     *         with respect to the wildcard.
     *
     * @param requestResource name of the resource which will be compared
     * @param targetResource name of the resource which will be compared with
     * @param wildcardCompare flag for wildcard comparison
     *        
     * @return returns <code>ResourceMatch</code> that
     * specifies if the resources are exact match, or
     * otherwise.
     */
    public ResourceMatch compare(String requestResource, 
                                 String targetResource, 
                                 boolean wildcardCompare) 
    {
        /* if it is a non-wildcard comparison or the target
           resource doesn't end with delimiter plus wildcard
           then use the result from PrefixResourceName.compare()
        */
        if ((!wildcardCompare) || 
            (!targetResource.endsWith(delimiter+wildcard))) {
            return (super.compare(requestResource, targetResource, 
                                  wildcardCompare));
        }

        int strlen = targetResource.length();
        String substr = targetResource.substring(0, strlen - 2);
        ResourceMatch res = super.compare(requestResource, substr,
                                          wildcardCompare);
        if ((res == ResourceMatch.EXACT_MATCH) 
             || (res == ResourceMatch.WILDCARD_MATCH) 
             || (res == ResourceMatch.SUPER_RESOURCE_MATCH)) {
            res = ResourceMatch.WILDCARD_MATCH;
        } else if (res == ResourceMatch.SUB_RESOURCE_MATCH) {
            res = ResourceMatch.SUB_RESOURCE_MATCH;
        } else {
            res = ResourceMatch.NO_MATCH;
        }
        return res;
    }    
}
