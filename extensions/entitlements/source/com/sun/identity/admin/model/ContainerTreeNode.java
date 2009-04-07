package com.sun.identity.admin.model;

import java.util.List;

public interface ContainerTreeNode extends TreeNode {
    public List<? extends TreeNode> getTreeNodes();
}
