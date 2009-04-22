package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import java.util.List;
import javax.faces.model.SelectItem;

public interface PolicyNameBean {
    public PrivilegeBean getPrivilegeBean();
    public Effect getPolicyNameInputEffect();
    public void setPolicyNameInputEffect(Effect e);
    public Effect getPolicyNameMessageEffect();
    public void setPolicyNameMessageEffect(Effect e);
    public List<SelectItem> getViewApplicationItems();
}
