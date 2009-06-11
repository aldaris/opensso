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
@XmlRootElement(name = "setPassword")
public class SetPassword {
    private OTP otp;
    private Password password;

    public SetPassword() {
    }

    /**
     * Returns a collection of QuestionConverter.
     *
     * @return a collection of QuestionConverter
     */
    @XmlElement
    public OTP getOtp() {
        return otp;
    }

    public void setOtp(OTP otp) {
        this.otp = otp;
    }

        /**
     * Returns a collection of QuestionConverter.
     *
     * @return a collection of QuestionConverter
     */
    @XmlElement
    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }
}
