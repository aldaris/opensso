/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensso.c1demoserver.model;

import java.io.Serializable;

/**
 *
 * @author pat
 */
public class Question implements Serializable {
    private static final long serialVersionUID = 1L;
    private String questionText;

    public Question() {
    }

    public Question(String messageText) {
        this.questionText = messageText;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String messageText) {
        this.questionText = messageText;
    }
}
