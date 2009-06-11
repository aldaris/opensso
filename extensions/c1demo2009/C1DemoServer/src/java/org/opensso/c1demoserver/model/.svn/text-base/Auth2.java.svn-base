/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensso.c1demoserver.model;

import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author pat
 */
@XmlRootElement(name = "auth2")
public class Auth2 {
    private Collection<String> answers;

    public Auth2() {
    }

    /**
     * Returns a collection of Answers.
     *
     * @return a collection of Answers
     */
    @XmlElement(name="answer")
    public Collection<String> getAnswers() {
        return answers;
    }

    public void setAnswers(Collection<String> answers) {
        this.answers = answers;
    }
}
