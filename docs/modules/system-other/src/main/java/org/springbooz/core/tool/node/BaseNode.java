//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springbooz.core.tool.node;

import java.util.ArrayList;
import java.util.List;

/**
 * ss
 */
public class BaseNode implements INode {
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    protected String id;
    /**
     *
     */
    protected String parentId;
    /**
     *
     */
    protected List<INode> children = new ArrayList();
    /**
     *
     */
    private Boolean hasChildren;

    @Override
    public Boolean getHasChildren() {
        return this.children.size() > 0 ? true : this.hasChildren;
    }
    /**
     *
     */
    public BaseNode() {
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public List<INode> getChildren() {
        return this.children;
    }

    /**
     *
     * @param id sdsa
     */
    public void setId(final String id) {
        this.id = id;
    }
    /**
     *
     * @param parentId sdsa
     */
    public void setParentId(final String parentId) {
        this.parentId = parentId;
    }

    /**
     *
     * @param children s
     */
    public void setChildren(final List<INode> children) {
        this.children = children;
    }

    /**
     *
     * @param hasChildren sadsa
     */
    public void setHasChildren(final Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }


}
