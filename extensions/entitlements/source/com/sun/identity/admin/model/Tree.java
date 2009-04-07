package com.sun.identity.admin.model;

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
        int size = sizer(rootNode, 0);
        return size;
    }

    private int sizer(TreeNode currentTn, int size) {
        if (currentTn instanceof ContainerTreeNode) {
            ContainerTreeNode ctn = (ContainerTreeNode)currentTn;
            for (TreeNode childTn: ctn.getTreeNodes()) {
                size += sizer(childTn, size);
            }
        } else {
            size++;
        }

        return size;
    }
}
