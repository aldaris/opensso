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
 * $Id: BadRequestException.java,v 1.1 2007-03-10 23:00:09 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan.
 */

package com.sun.identity.openid;

/**
 *  TODO: Description.
 *
 *  @author pbryan
 */
public class BadRequestException extends Exception
{
  /**
   *  Constructs a new exception with the specified detail message.
   *
   *  @param message the detail message. 
   */
  public BadRequestException(String message)
  {
    super(message);
  }
}
