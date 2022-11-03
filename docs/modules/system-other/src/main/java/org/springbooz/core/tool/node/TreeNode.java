//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springbooz.core.tool.node;

/**
 * ss
 */
public class TreeNode extends BaseNode {
    private static final long serialVersionUID = 1L;
    private String title;
    private Long key;
    private Long value;

    /**
     *
     */
    public TreeNode() {
    }

    public String getTitle() {
        return this.title;
    }

    public Long getKey() {
        return this.key;
    }

    public Long getValue() {
        return this.value;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setKey(final Long key) {
        this.key = key;
    }

    public void setValue(final Long value) {
        this.value = value;
    }

}
