//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springbooz.core.tool.node;

import java.io.Serializable;
import java.util.List;

/**
 * ss
 */
public interface INode extends Serializable {
    /**
     *
     * @return ss
     */
    String getId();
    /**
     *
     * @return ss
     */
    String getParentId();
    /**
     *
     * @return ss
     */
    List<INode> getChildren();
    /**
     *
     * @return ss
     */
    default Boolean getHasChildren() {
        return false;
    }
}
