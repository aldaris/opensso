package com.sun.identity.admin.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tree {
    private TreeNode rootNode;

    public Tree(TreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public boolean isEmpty() {
        return rootNode == null;
    }

    public TreeNode remove(TreeNode tn) {
        if (remover(rootNode, tn)) {
            rootNode = null;
        }

        return rootNode;
    }

    private boolean remover(TreeNode currentTn, TreeNode removeTn) {
        if (currentTn == removeTn) {
            return true;
        }
        if (currentTn instanceof ContainerTreeNode) {
            ContainerTreeNode ctn = (ContainerTreeNode)currentTn;
            for (TreeNode childTn: ctn.getTreeNodes()) {
                if (remover(childTn, removeTn)) {
                    ctn.getTreeNodes().remove(childTn);
                    return false;
                }
            }
        }
        return false;
    }

    public int size() {
        int size = sizer(rootNode);
        return size;
    }

    private int sizer(TreeNode currentTn) {
        if (currentTn == null) {
            return 0;
        } else if (currentTn instanceof ContainerTreeNode) {
            int mySize = 1;
            ContainerTreeNode ctn = (ContainerTreeNode)currentTn;
            for (TreeNode childTn: ctn.getTreeNodes()) {
                mySize += sizer(childTn);
            }
            return mySize;
        } else {
            return 1;
        }
    }

    public List<TreeNode> asList() {
        return asListr(rootNode);
    }

    private List<TreeNode> asListr(TreeNode currentTn) {
        if (currentTn == null) {
            return null;
        } else if (currentTn instanceof ContainerTreeNode) {
            ContainerTreeNode ctn = (ContainerTreeNode)currentTn;
            List<TreeNode> nodes = new ArrayList<TreeNode>();
            nodes.add(currentTn);
            for (TreeNode childTn: ctn.getTreeNodes()) {
                nodes.addAll(asListr(childTn));
            }
            return nodes;
        } else {
            return Collections.singletonList(currentTn);
        }
    }

    public List<TreeNode> getAsList() {
        return asList();
    }
}
