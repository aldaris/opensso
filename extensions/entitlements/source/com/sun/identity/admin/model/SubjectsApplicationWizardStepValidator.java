package com.sun.identity.admin.model;

import com.sun.identity.admin.Resources;
import java.util.List;
import javax.faces.application.FacesMessage;

public class SubjectsApplicationWizardStepValidator extends ApplicationWizardStepValidator {
    public SubjectsApplicationWizardStepValidator(WizardBean wizardBean) {
        super(wizardBean);
    }

    public boolean validate() {
        List<SubjectType> sts =
                getApplicationWizardBean().getViewApplication().getSubjectTypes();

        if (sts == null || sts.size() == 0) {
            MessageBean mb = new MessageBean();
            Resources r = new Resources();
            mb.setSummary(r.getString(this, "noSubjectTypesSummary"));
            mb.setDetail(r.getString(this, "noSubjectTypesDetail"));
            mb.setSeverity(FacesMessage.SEVERITY_ERROR);

            getMessagesBean().addMessageBean(mb);

            return false;
        }

        return true;
    }
}