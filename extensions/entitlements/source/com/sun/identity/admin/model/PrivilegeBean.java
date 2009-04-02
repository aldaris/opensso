package com.sun.identity.admin.model;

import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.OrSubject;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.ResourceAttributes;
import com.sun.identity.entitlement.opensso.OpenSSOPrivilege;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class PrivilegeBean implements Serializable {
    private String name;
    private String description;
    private List<Resource> resources = new ArrayList<Resource>();
    private List<ViewCondition> viewConditions = new ArrayList<ViewCondition>();
    private List<SubjectContainer> subjectContainers = new ArrayList<SubjectContainer>();
    private List<Action> actions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public List<ViewCondition> getViewConditions() {
        return viewConditions;
    }

    public List<SubjectContainer> getSubjectContainers() {
        return subjectContainers;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Privilege toPrivilige() {
        // subjects
        Set<EntitlementSubject> eSubjects = new HashSet<EntitlementSubject>();
        for (SubjectContainer sc : subjectContainers) {
            List<ViewSubject> viewSubjects = sc.getViewSubjects();
            for (ViewSubject vs : viewSubjects) {
                eSubjects.add(vs.getSubject());
            }
        }
        EntitlementSubject orSubject = new OrSubject(eSubjects);


        // resources / actions
        Set<Entitlement> entitlements = new HashSet<Entitlement>();
        for (Resource r : resources) {
            Entitlement e = r.getEntitlement(actions);
            entitlements.add(e);
        }

        // conditions
        cleanConditions();
        ViewCondition conditionTree = buildConditionExpression(viewConditions);
        EntitlementCondition eCondition = null;
        if (conditionTree != null) {
            eCondition = conditionTree.getEntitlementCondition();
        }

        // resource attrs
        // TODO
        Set<ResourceAttributes> attrs = null;


        Privilege privilege = new OpenSSOPrivilege(
                name,
                entitlements.iterator().next(), //TODO: use scalar entitlement
                orSubject,
                eCondition,
                attrs);

        return privilege;
    }

    public ViewCondition getLastViewCondition() {
        if (viewConditions.size() == 0) {
            return null;
        }
        return viewConditions.get(viewConditions.size()-1);
    }

    private void cleanConditions() {
        ViewCondition lastVc = getLastViewCondition();
        if (lastVc == null) {
            // no conditions
            return;
        }
        while ((lastVc = getLastViewCondition()).getConditionType().isExpression()) {
            viewConditions.remove(lastVc);
        }
    }

    private ViewCondition buildConditionExpression(List<ViewCondition> vcs) {
        Stack<ViewCondition> output = new Stack<ViewCondition>();
        Stack<ViewCondition> operators = new Stack<ViewCondition>();

        for (ViewCondition vc : vcs) {
            if (vc.getConditionType().isExpression()) {
                if (operators.size() > 0 &&
                        (vc instanceof OrViewCondition ||
                        vc instanceof AndViewCondition)) {
                    output.push(operators.pop());
                }
                operators.push(vc);
            } else {
                output.push(vc);
            }
        }
        while (operators.size() > 0) {
            output.push(operators.pop());
        }

        ViewCondition tree = buildConditionTree(output);
        return tree;
    }

    private ViewCondition buildConditionTree(Stack<ViewCondition> output) {
        if (output.size() == 0) {
            return null;
        }

        ViewCondition head = output.pop();
        if (head instanceof AndViewCondition) {
            AndViewCondition andHead = (AndViewCondition) head;
            ViewCondition left = buildConditionTree(output);
            ViewCondition right = buildConditionTree(output);
            andHead.getAndViewConditions().add(left);
            andHead.getAndViewConditions().add(right);

            return andHead;
        } else if (head instanceof OrViewCondition) {
            OrViewCondition orHead = (OrViewCondition) head;
            ViewCondition left = buildConditionTree(output);
            ViewCondition right = buildConditionTree(output);
            orHead.getOrViewConditions().add(left);
            orHead.getOrViewConditions().add(right);

            return orHead;
        } else if (head instanceof NotViewCondition) {
            NotViewCondition notHead = (NotViewCondition) head;
            ViewCondition child = buildConditionTree(output);
            notHead.setNotCondition(child);

            return notHead;
        }

        return head;
    }

}
