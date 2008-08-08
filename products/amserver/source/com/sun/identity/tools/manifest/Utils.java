/**
 * $Id: Utils.java,v 1.1 2008-08-08 22:36:23 kevinserwin Exp $
 * Copyright © 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Sun Microsystems, Inc. has intellectual property rights relating to
 * technology embodied in the product that is described in this document.
 * In particular, and without limitation, these intellectual property rights
 * may include one or more of the U.S. patents listed at
 * http://www.sun.com/patents and one or more additional patents or pending
 * patent applications in the U.S. and in other countries.
 *
 * U.S. Government Rights - Commercial software.  Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and applicable
 * provisions of the FAR and its supplements.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.Sun,
 * Sun Microsystems and  the Sun logo are trademarks or registered trademarks
 * of Sun Microsystems, Inc. in the U.S. and other countries.  
 *
 * Copyright © 2008 Sun Microsystems, Inc. Tous droits réservés.
 * Sun Microsystems, Inc. détient les droits de propriété intellectuels relatifs
 * à la technologie incorporée dans le produit qui est décrit dans ce document.
 * En particulier, et ce sans limitation, ces droits de propriété
 * intellectuelle peuvent inclure un ou plus des brevets américains listés
 * à l'adresse http://www.sun.com/patents et un ou les brevets supplémentaires
 * ou les applications de brevet en attente aux Etats - Unis et dans les
 * autres pays.
 *
 * L'utilisation est soumise aux termes du contrat de licence.
 *
 * Cette distribution peut comprendre des composants développés par des
 * tierces parties.
 *
 * Sun,  Sun Microsystems et  le logo Sun sont des marques de fabrique ou des
 * marques déposées de Sun Microsystems, Inc. aux Etats-Unis et dans
 * d'autres pays.
 */

package com.sun.identity.tools.manifest;

import java.io.InputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.ListIterator;

public class Utils implements ManifestConstants {
    
    static byte[] buf = new byte[BUFFER_SIZE];
    
    /**
     * Run the hash with the pass in MessageDigest and InputStream
     *
     * @param md The MessageDigest to be used.
     * @param in The InputStream of the data to be hashed.
     * @return The MessageDigest object after doing the hashing.
     */
    public static MessageDigest hashing(MessageDigest md, InputStream in){
        try {
            DigestInputStream din = new DigestInputStream(in, md);
            synchronized(buf){
                while (din.read(buf) != -1);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return md;
    }
    
    /**
     * Calculate and return the hash value with byte array.
     *
     * @param algorithm The string to indicate the hashing algorithm to be used.
     * @param in The InputStream of the data to be hashed.
     * @return The hash value in byte array.
     */
    
    public static byte[] getHash(String algorithm, InputStream in){
        try {
            MessageDigest md=MessageDigest.getInstance(algorithm);
            return hashing(md, in).digest();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Translate the byte array into Hex String.
     *
     * @param hash The byte array of hash value.
     * @return The string of the hash value in Hex.
     */
    
    public static String translateHashToString(byte[] hash){
        StringBuffer hashBuffer = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            hashBuffer.append(Character.forDigit((hash[i] >> 4) & 0x0F, 16));
            hashBuffer.append(Character.forDigit(hash[i] & 0x0F, 16));
        }
        return hashBuffer.toString();
    }
    
    /**
     * Check whether the string matches the pattern.
     *
     * @param actualString The string to be checked.
     * @param patterns A list of patterns to check for.
     * @param wildChar A character which is used as wild card in the pattern.
     * @return Whether the string matches one of the patterns in the list.
     */
    
    public static boolean isMatch(String actualString, LinkedList patterns,
        char wildCard){
        boolean matched = false;
        for (ListIterator iter = patterns.listIterator(0); iter.hasNext(); ) {
            if(isMatch(actualString, (String) iter.next(), wildCard)){
                matched = true;
                break;
            }
        }
        return matched;
    }
    
    /**
     * Check whether the string matches the pattern.
     *
     * @param actualString The string to be checked.
     * @param pattern A pattern to check for.
     * @param wildChar A character which is used as wild card in the pattern.
     * @return Whether the string matches one of the patterns in the list.
     */
    
    public static boolean isMatch(String actualString, String pattern,
        char wildCard){
        String tempPattern=pattern.trim();
        int matchOffset = 0;
        boolean matched = true;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < tempPattern.length(); i++) {
            if (tempPattern.charAt(i) != wildCard) {
                buffer.append(tempPattern.charAt(i));
            }
            if ((i == (tempPattern.length() - 1)) ||
                (tempPattern.charAt(i) == wildCard)) {
                if (buffer.length() > 0) {
                    int matchedIndex = actualString.indexOf(buffer.toString(),
                            matchOffset);
                    if (matchedIndex >= matchOffset) {
                        if (i != (tempPattern.length() - 1)) {
                            matchOffset = matchedIndex +
                                    buffer.length();
                        } else {
                            if (tempPattern.charAt(i) != wildCard) {
                                if (actualString.substring(matchedIndex).
                                    length() !=
                                    buffer.length()) {
                                    matched = false;
                                    break;
                                }
                            }
                        }
                    } else {
                        matched = false;
                        break;
                    }
                    buffer = new StringBuffer();
                }
            }
        }
        return matched;
    }
}
