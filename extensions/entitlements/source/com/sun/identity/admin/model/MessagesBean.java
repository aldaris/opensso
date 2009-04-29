package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public class MessagesBean implements Serializable {
    private String clientId;

    public static String getClientId(String componentId) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot root = context.getViewRoot();

        UIComponent c = findComponent(root, componentId);
        return c.getClientId(context);
    }

    private static UIComponent findComponent(UIComponent c, String id) {
        if (id.equals(c.getId())) {
            return c;
        }
        Iterator<UIComponent> kids = c.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent found = findComponent(kids.next(), id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public void setComponentId(String componentId) {
        if (componentId == null) {
            return;
        }

        if (componentId.equals("_global")) {
            this.clientId = componentId;
        } else {
            this.clientId = getClientId(componentId);
        }
    }

    public List<MessageBean> getMessageBeans() {
        FacesContext context = FacesContext.getCurrentInstance();
        Iterator<FacesMessage> i;
        if (clientId == null) {
            i = context.getMessages();
        } else if (clientId.equals("_global")) {
            i = context.getMessages(null);
        } else {
            i = context.getMessages(clientId);
        }

        List<MessageBean> msgs = new ArrayList<MessageBean>();
        while (i.hasNext()) {
            msgs.add(new MessageBean(i.next()));
        }

        return msgs;
    }

    public boolean isExists() {
        FacesContext fc = FacesContext.getCurrentInstance();
        boolean exists = false;
        if (clientId == null) {
            exists = fc.getMessages().hasNext();
        } else if (clientId.equals("_global")) {
            exists = fc.getMessages(null).hasNext();
        } else {
            exists = fc.getMessages(clientId).hasNext();
        }

        return exists;
    }

    public boolean isExistsError() {
        for (MessageBean mb: getMessageBeans()) {
            if (mb.isError()) {
                return true;
            }
        }
        return false;
    }

    public boolean isExistsInfo() {
        for (MessageBean mb: getMessageBeans()) {
            if (mb.isInfo()) {
                return true;
            }
        }
        return false;
    }

    public void addMessageBean(MessageBean mb) {
        FacesMessage fm = mb.toFacesMessage();
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(null, fm);
    }
}
