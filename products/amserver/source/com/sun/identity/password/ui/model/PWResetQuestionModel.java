


package com.sun.identity.password.ui.model;

import java.util.Set;
import java.util.Map;

/**
 * <code>PWResetQuestionModelImpl</code> defines a set of methods that
 * are required by password reset question viewbean.
 */
public interface PWResetQuestionModel extends PWResetModel
{   
    /**
     * Resets the user password.
     *
     * @param userDN user DN
     * @param orgDN organization DN
     * @param map  map of user question and answer
     * @throws PWResetException if unable to reset the password
     */
    public void resetPassword(
        String userDN,
        String orgDN,
        Map map)
        throws PWResetException;


    /**
     * Gets map of secret questions
     *
     * @param userDN DN of the user to get the question
     * @param orgDN DN of the organization 
     * @return map of secret question
     */
    public Map getSecretQuestions(String userDN, String orgDN);

    /**
     * Gets password reset question title
     *
     * @param attrValue user attribute value  
     * @return password reset question title
     */
    public String getPWQuestionTitleString(String attrValue);

    /**
     * Gets ok button label
     *
     * @return ok button label
     */
    public String getOKBtnLabel();

    /**
     * Gets previous button label
     *
     * @return previous button label
     */
    public String getPreviousBtnLabel();

    /**
     * Sets no questions configured message
     */
    public void setNoQuestionsInfoMsg();


    /**
     * Gets missing answer message
     *
     * @return missing answer message
     */
    public String getMissingAnswerMessage();

    /**
     * Returns true if the secret questions are available for a user
     *
     * @param userDN user DN
     * @param orgDN organization DN
     * @return true if the questions are available, false otherwise
     */
    public boolean isQuestionAvailable(String userDN, String orgDN);

    /**
     * Gets the localized string for the question
     *
     * @param question i8n key for the question
     * @return localized string for the question
     */
    public String getLocalizedStrForQuestion(String question);
}
